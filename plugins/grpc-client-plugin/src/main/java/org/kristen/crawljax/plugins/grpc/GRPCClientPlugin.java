package org.kristen.crawljax.plugins.grpc;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.*;
import com.crawljax.core.configuration.CrawljaxConfiguration;
import com.crawljax.core.plugin.*;
import com.crawljax.core.state.Eventable;
import com.crawljax.core.state.Identification;
import com.crawljax.core.state.StateVertex;
import com.google.common.collect.ImmutableList;
import io.grpc.stub.StreamObserver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import com.google.protobuf.InvalidProtocolBufferException;
import com.sun.org.apache.xml.internal.security.utils.Base64;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.kristen.rpc.darcher.*;

public class GRPCClientPlugin implements PreCrawlingPlugin, PostCrawlingPlugin, OnBrowserCreatedPlugin, OnFireEventSucceededPlugin {
    private String METAMASK_POPUP_URL = "chrome-extension://nkbihfbeogaeaoehlefnkodbefgpgknn/popup.html";
    private final String INIT_CONTROL_MSG_ID = "0";
    private int WAIT_TIME_FOR_METAMASK_PLUGIN = 1000;

    private static ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8899)
            .usePlaintext()
            .build();;
    private final DAppTestDriverServiceGrpc.DAppTestDriverServiceBlockingStub blockingStub;
    private final DAppTestDriverServiceGrpc.DAppTestDriverServiceStub asyncStub;
    private String dappName;
    private int instanceId;
    private String fromAddress;
    private String toAddress;
    private String txHash;
    private EmbeddedBrowser dappBrowser;

    public GRPCClientPlugin(String dappName, int instanceId, String url) {
        blockingStub = DAppTestDriverServiceGrpc.newBlockingStub(channel);
        asyncStub = DAppTestDriverServiceGrpc.newStub(channel);

        this.dappName = dappName;
        this.instanceId = instanceId;
        this.METAMASK_POPUP_URL = url;
    }

    private StreamObserver<DappTestService.DAppDriverControlMsg> responseObserver =
            DAppTestDriverServiceGrpc.newStub(channel).dappDriverControl(new StreamObserver<DappTestService.DAppDriverControlMsg>() {
        @Override
        public void onNext(DappTestService.DAppDriverControlMsg dAppDriverControlMsg) {
            System.out.println("Receive from stream: " + dAppDriverControlMsg.getAllFields());
            handleControlMsg(dAppDriverControlMsg);
        }

        @Override
        public void onError(Throwable t) {
            System.out.println("Error from stream");
        }

        @Override
        public void onCompleted() {
            System.out.println("Stream completed");
        }
    });

    private StreamObserver<DappTestService.DAppDriverControlMsg> requestObserver;

    private void handleControlMsg(DappTestService.DAppDriverControlMsg dAppDriverControlMsg) {
        DappTestService.DAppDriverControlType controlType = dAppDriverControlMsg.getControlType();
        switch(controlType) {
            case Refresh:
                WebDriver driver = this.dappBrowser.getWebDriver();
                driver.navigate().refresh();
                break;
            case NilType:
            case UNRECOGNIZED:
                System.out.println("Unrecognized control type.");
                break;
            default:
                break;
        }
    }

    /**
     * Get the information of the latest transaction.
     *
     * @param browser the browser instance
     */
    private void getTxInfo(EmbeddedBrowser browser) {
        WebDriver driver = browser.getWebDriver();
        String currentHandle = driver.getWindowHandle();
        ((JavascriptExecutor) driver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.indexOf(currentHandle) + 1));
        driver.get(METAMASK_POPUP_URL);

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!isMetaMaskMainPage(browser)) {
            Identification activityPaneId = new Identification(Identification.How.xpath,
                    "/html/body/div[1]/div/div[4]/div/div/div/div[3]/ul/li[2]");
            Identification txBoxId = new Identification(Identification.How.xpath,
                    "/html/body/div[1]/div/div[4]/div/div/div/div[3]/div/div/div/div/div[1]");
            Identification fromAddressBoxId = new Identification(Identification.How.xpath,
                    "/html/body/div[2]/div/div/section/div/div/div[2]/div[1]/div/div[1]/div/div/div/span");
            Identification toAddressBoxId = new Identification(Identification.How.xpath,
                    "/html/body/div[2]/div/div/section/div/div/div[2]/div[1]/div/div[1]/div/div/div/span");
            Identification copyTxHashId = new Identification(Identification.How.xpath,
                    "/html/body/div[2]/div/div/section/div/div/div[1]/div[2]/button[1]");

            if (browser.elementExists(activityPaneId)) {
                WebElement activityPane = browser.getWebElement(activityPaneId);
                activityPane.click();
            }
            if (browser.elementExists(txBoxId)) {
                WebElement txBox = browser.getWebElement(txBoxId);
                txBox.click();
            }
            if (browser.elementExists(fromAddressBoxId)) {
                WebElement fromAddressBox = browser.getWebElement(fromAddressBoxId);
                this.fromAddress = fromAddressBox.getAttribute("outerHTML").substring(6, 54);
            }
            if (browser.elementExists(toAddressBoxId)) {
                WebElement toAddressBox = browser.getWebElement(toAddressBoxId);
                this.toAddress = toAddressBox.getAttribute("outerHTML").substring(6, 54);
            }
            if (browser.elementExists(copyTxHashId)) {
                WebElement copyTxHashElement = browser.getWebElement(copyTxHashId);
                copyTxHashElement.click();
            }
            this.txHash = getTextFromClipboard();
        }
        driver.close();
        driver.switchTo().window(currentHandle);
    }

    /**
     * Get the text from system clipboard.
     * @return the text in clipboard
     */
    private String getTextFromClipboard() {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable trans = clipboard.getContents(null);
        if (trans != null) {
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
                    return text;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private boolean isMetaMaskMainPage(EmbeddedBrowser browser) {
        Identification mainId = new Identification(Identification.How.xpath, "//div[@class='main-container']");
        return browser.elementExists(mainId);
    }

    @Override
    public void preCrawling(CrawljaxConfiguration config) throws RuntimeException {
        System.out.println("Start crawling...");

        DappTestService.TestStartMsg testStartMsg = DappTestService.TestStartMsg
                .newBuilder()
                .setDappName(this.dappName)
                .setInstanceId(Integer.toString(this.instanceId))
                .build();
        blockingStub.notifyTestStart(testStartMsg);

        DappTestService.DAppDriverControlMsg dAppDriverControlMsg = DappTestService.DAppDriverControlMsg
                .newBuilder()
                .setRole(Common.Role.DAPP)
                .setId(INIT_CONTROL_MSG_ID)
                .setDappName(this.dappName)
                .setInstanceId(Integer.toString(this.instanceId))
                .setControlType(DappTestService.DAppDriverControlType.NilType)
                .build();
        this.requestObserver = asyncStub.dappDriverControl(responseObserver);
    }

    @Override
    public void postCrawling(CrawlSession session, ExitNotifier.ExitStatus exitReason) {
        System.out.println("Crawling ended.");
        DappTestService.TestEndMsg testEndMsg = DappTestService.TestEndMsg
                .newBuilder()
                .setDappName(this.dappName)
                .setInstanceId(Integer.toString(this.instanceId))
                .build();
        blockingStub.notifyTestEnd(testEndMsg);
    }

    @Override
    public void onBrowserCreated(EmbeddedBrowser newBrowser) {
        this.dappBrowser = newBrowser;
    }

    @Override
    public void onFireEventSucceeded(CrawlerContext context, Eventable eventable, List<Eventable> pathToFailure) {
        try {
            Thread.sleep(WAIT_TIME_FOR_METAMASK_PLUGIN);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        WebDriver driver = context.getBrowser().getWebDriver();
        String currentHandle = driver.getWindowHandle();
        ((JavascriptExecutor) driver).executeScript("window.open()");
        ArrayList<String> tabs = new ArrayList<>(driver.getWindowHandles());
        driver.switchTo().window(tabs.get(tabs.indexOf(currentHandle) + 1));
        driver.get(METAMASK_POPUP_URL);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (!isMetaMaskMainPage(context.getBrowser())) {
            System.out.println("One transaction has been sent out.");
            EmbeddedBrowser browser = context.getBrowser();
            getTxInfo(browser);
            DappTestService.TxMsg txMsg = DappTestService.TxMsg.newBuilder()
                    .setDappName(this.dappName)
                    .setInstanceId(Integer.toString(this.instanceId))
                    .setHash(this.txHash)
                    .setFrom(this.fromAddress)
                    .setTo(this.toAddress)
                    .build();
            blockingStub.waitForTxProcess(txMsg);
        }
    }

}
