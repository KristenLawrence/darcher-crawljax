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

public class GRPCClientPlugin implements PreCrawlingPlugin, PostCrawlingPlugin, OnBrowserCreatedPlugin, OnUrlFirstLoadPlugin, OnFireEventSucceededPlugin {
    private String METAMASK_PASSWORD = "";
    private String METAMASK_POPUP_URL = "chrome-extension://pblaiiacglodkdimplphhfffmpblfgmh/home.html";
    private String DAPP_URL = "http://localhost:8080";
    private final String INIT_CONTROL_MSG_ID = "0";
    private int WAIT_TIME_FOR_METAMASK_PLUGIN = 1000;
    private static String SERVER_HOST = "localhost";
    private static int SERVER_PORT = 1234;

    public static ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_HOST, SERVER_PORT)
            .usePlaintext()
            .build();
    public final DAppTestDriverServiceGrpc.DAppTestDriverServiceBlockingStub blockingStub;
    public final DAppTestDriverServiceGrpc.DAppTestDriverServiceStub asyncStub;
    public String dappName;
    public int instanceId;
    private String fromAddress;
    private String toAddress;
    private String txHash;
    public ControlMsgHandlerThread controlMsgHandlerThread;
    public MetamaskTxConfirmThread metamaskTxConfirmThread;
    public EmbeddedBrowser dappBrowser;
//    volatile WebDriver driver;

//    public Thread controlThread;

    public GRPCClientPlugin(String dappName, int instanceId, String metamaskUrl, String dappUrl, String metamaskPassword) {
        blockingStub = DAppTestDriverServiceGrpc.newBlockingStub(channel);
        asyncStub = DAppTestDriverServiceGrpc.newStub(channel);

        this.dappName = dappName;
        this.instanceId = instanceId;
        this.METAMASK_POPUP_URL = metamaskUrl;
        this.DAPP_URL = dappUrl;
        this.METAMASK_PASSWORD = metamaskPassword;

//        this.controlMsgHandlerThread = new ControlMsgHandlerThread(SERVER_HOST, SERVER_PORT, channel, blockingStub, asyncStub, dappName, instanceId);
        this.controlMsgHandlerThread = new ControlMsgHandlerThread(this.dappName, this.instanceId);
        Thread controlThread = new Thread(this.controlMsgHandlerThread);
        controlThread.start();

//        this.metamaskTxConfirmThread = new MetamaskTxConfirmThread(METAMASK_POPUP_URL);
//        Thread metamaskThread = new Thread(this.metamaskTxConfirmThread);
//        metamaskThread.start();
    }

//    private StreamObserver<DappTestService.DAppDriverControlMsg> responseObserver =
//            DAppTestDriverServiceGrpc.newStub(channel).dappDriverControl(new StreamObserver<DappTestService.DAppDriverControlMsg>() {
//        @Override
//        public void onNext(DappTestService.DAppDriverControlMsg dAppDriverControlMsg) {
//            System.out.println("Receive from stream: " + dAppDriverControlMsg.getAllFields());
//            handleControlMsg(dAppDriverControlMsg);
//        }
//
//        @Override
//        public void onError(Throwable t) {
//            System.out.println("Error from stream");
//        }
//
//        @Override
//        public void onCompleted() {
//            System.out.println("Stream completed");
//        }
//    });
//
//    private StreamObserver<DappTestService.DAppDriverControlMsg> requestObserver;

    public void handleControlMsg(DappTestService.DAppDriverControlMsg dAppDriverControlMsg) {
        DappTestService.DAppDriverControlType controlType = dAppDriverControlMsg.getControlType();
        switch(controlType) {
            case Refresh:
                WebDriver driver = dappBrowser.getWebDriver();
                driver.navigate().refresh();
                System.out.println("The page is successfully refreshed!!!!!");
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
        Identification activityPaneId = new Identification(Identification.How.xpath,
                "/html/body/div[1]/div/div[4]/div/div/div/div[3]/ul/li[2]");
        Identification txBoxId = new Identification(Identification.How.xpath,
                "/html/body/div[1]/div/div[4]/div/div/div/div[3]/div/div/div/div/div[1]");
//        Identification fromAddressBoxId = new Identification(Identification.How.xpath,
//                "/html/body/div[2]/div/div/section/div/div/div[2]/div[1]/div/div[1]/div/div/div/span");
//        Identification toAddressBoxId = new Identification(Identification.How.xpath,
//                "/html/body/div[2]/div/div/section/div/div/div[2]/div[1]/div/div[1]/div/div/div/span");
        Identification copyFromId = new Identification(Identification.How.xpath,
                "/html/body/div[2]/div/div/section/div/div/div[2]/div[1]/div/div[1]/div/div/div");
        Identification copyToId = new Identification(Identification.How.xpath,
                "/html/body/div[2]/div/div/section/div/div/div[2]/div[1]/div/div[3]/div/div/div");
        Identification copyTxHashId = new Identification(Identification.How.xpath,
                "/html/body/div[2]/div/div/section/div/div/div[1]/div[2]/button[1]");


        if (browser.elementExists(activityPaneId)) {
            System.out.println("*************************************************************************************");
            WebElement activityPane = browser.getWebElement(activityPaneId);
            activityPane.click();
        }
        if (browser.elementExists(txBoxId)) {
            WebElement txBox = browser.getWebElement(txBoxId);
            txBox.click();
        }
//        if (browser.elementExists(fromAddressBoxId)) {
//            WebElement fromAddressBox = browser.getWebElement(fromAddressBoxId);
//            this.fromAddress = fromAddressBox.getAttribute("outerHTML").substring(6, 54);
//        }
//        if (browser.elementExists(toAddressBoxId)) {
//            WebElement toAddressBox = browser.getWebElement(toAddressBoxId);
//            this.toAddress = toAddressBox.getAttribute("outerHTML").substring(6, 54);
//        }
        if (browser.elementExists(copyFromId)) {
            WebElement copyFromElement = browser.getWebElement(copyFromId);
            copyFromElement.click();
        }
        this.fromAddress = getTextFromClipboard();
        if (browser.elementExists(copyToId)) {
            WebElement copyToElement = browser.getWebElement(copyToId);
            copyToElement.click();
        }
        this.toAddress = getTextFromClipboard();
        if (browser.elementExists(copyTxHashId)) {
            WebElement copyTxHashElement = browser.getWebElement(copyTxHashId);
            copyTxHashElement.click();
        }
        this.txHash = getTextFromClipboard();

        System.out.printf("From address: %s, to address: %s, txHash: %s\n", fromAddress, toAddress, txHash);
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

//        DappTestService.DAppDriverControlMsg dAppDriverControlMsg = DappTestService.DAppDriverControlMsg
//                .newBuilder()
//                .setRole(Common.Role.DAPP)
//                .setId(INIT_CONTROL_MSG_ID)
//                .setDappName(this.dappName)
//                .setInstanceId(Integer.toString(this.instanceId))
//                .setControlType(DappTestService.DAppDriverControlType.NilType)
//                .build();
//        this.requestObserver = asyncStub.dappDriverControl(responseObserver);
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
//        if (newBrowser == null) {
//            System.out.println("Before setting browser, browser is null");
//        } else {
//            System.out.println("Before setting browser, browser is not null");
//        }

//        controlMsgHandlerThread.setBrowser(newBrowser);

        this.dappBrowser = newBrowser;

        try {
            newBrowser.goToUrl(new URI(METAMASK_POPUP_URL));
        } catch (URISyntaxException e) {
            System.out.println("ERROR: invalid MetaMask popup url, " + METAMASK_POPUP_URL);
        }
        if (isLogInPage(newBrowser) && !logIn(newBrowser, METAMASK_PASSWORD)) {
            System.out.println("ERROR: MetaMask login failed");
        }

        // TODO: handle other scenarios (specific)
//         Sign up for Augur
        try {
            newBrowser.goToUrl(new URI(DAPP_URL));

            // Sign up for Augur
            WebDriver driver = newBrowser.getWebDriver();
            driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            driver.findElement(By.cssSelector(".buttons-styles_SecondaryButton")).click();
            driver.findElement(By.cssSelector(".buttons-styles_SecondarySignInButton:nth-child(7) > div > div > div:nth-child(1)")).click();
        } catch (URISyntaxException e) {
            System.out.println("ERROR: invalid Augur url, " + METAMASK_POPUP_URL);
        }

//        this.dappBrowser = newBrowser;
//        metamaskTxConfirmThread.setDriver(driver);

//        DappTestService.TestStartMsg testStartMsg = DappTestService.TestStartMsg
//                .newBuilder()
//                .setDappName(this.dappName)
//                .setInstanceId(Integer.toString(this.instanceId))
//                .build();
//        blockingStub.notifyTestStart(testStartMsg);

//        this.controlThread.start();
    }

    /**
     * Execute `ethereum.enable()` to connect dapp with MetaMask
     *
     * @param context the current crawler context.
     */
    @Override
    public void onUrlFirstLoad(CrawlerContext context) {
        EmbeddedBrowser browser = context.getBrowser();
        WebDriver driver = browser.getWebDriver();
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor)driver).executeScript("ethereum.enable()");
        } else {
            throw new IllegalStateException("This driver does not support JavaScript!");
        }
    }

    /**
     * this function will automatically click the primary button in the MetaMask popup window
     *
     * @param browser the browser instance
     */
    private void processMetaMaskPopup(EmbeddedBrowser browser) {

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

        // TODO: Sign
        if (!isMetaMaskMainPage(browser)) {
            Identification primaryBtnId = new Identification(Identification.How.xpath,
                    "//button[contains(@class, 'btn-primary')]");
            Identification secondaryBtnId = new Identification(Identification.How.xpath,
                    "//button[contains(@class, 'btn-secondary')]");
            Identification defaultBtnId = new Identification(Identification.How.xpath,
                    "//button[contains(@class, 'btn-default')]");
            Identification signBtnId = new Identification(Identification.How.xpath, "//button[contains(@class, 'request-signature__footer__sign-button')]");
            if (browser.elementExists(primaryBtnId)) {
                WebElement primaryBtn = browser.getWebElement(primaryBtnId);
                primaryBtn.click();

            } else if (browser.elementExists(secondaryBtnId)) {
                WebElement secondaryBtn = browser.getWebElement(secondaryBtnId);
                secondaryBtn.click();
              } else if (browser.elementExists(defaultBtnId)) {
                WebElement defaultBtn = browser.getWebElement(defaultBtnId);
                defaultBtn.click();
            } else {
                return;
            }
//            if (browser.elementExists(signBtnId)) {
//                WebElement signBtn = browser.getWebElement(signBtnId);
//                signBtn.click();
//            } else {
//                driver.close();
//                driver.switchTo().window(currentHandle);
//                System.out.println("Returned");
//                return;
//            }

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            getTxInfo(browser);
            DappTestService.TxMsg txMsg = DappTestService.TxMsg.newBuilder()
                    .setDappName(this.dappName)
                    .setInstanceId(Integer.toString(this.instanceId))
                    .setHash(this.txHash)
                    .setFrom(this.fromAddress)
                    .setTo(this.toAddress)
                    .build();
            System.out.println("##############################################################");
            blockingStub.waitForTxProcess(txMsg);
        }

        driver.close();
        driver.switchTo().window(currentHandle);
    }

    private boolean isLogInPage(EmbeddedBrowser browser) {
        Identification loginPage = new Identification(Identification.How.xpath, "//div[@class='unlock-page']");
        return browser.elementExists(loginPage);
    }


    /**
     * login MetaMask with password
     *
     * @param password the password of MetaMask
     * @return whether the login is successful or not
     */
    private boolean logIn(EmbeddedBrowser browser, String password) {
        try {
            Identification passwordInput = new Identification(Identification.How.id, "password");
            browser.input(passwordInput, password);
            Identification loginBtnId = new Identification(Identification.How.xpath, "//button[@type='submit']");
            if (!browser.elementExists(loginBtnId)) {
                return false;
            }
            WebElement loginBtn = browser.getWebElement(loginBtnId);
            loginBtn.click();
        } catch (CrawljaxException e) {
            return false;
        }
        return true;
    }

    @Override
    public void onFireEventSucceeded(CrawlerContext context, Eventable eventable, List<Eventable> pathToFailure) {
        System.out.println("succeed");
        EmbeddedBrowser browser = context.getBrowser();
        processMetaMaskPopup(browser);
    }


    public class ControlMsgHandlerThread implements Runnable {
        private String dappName;
        private int instanceId;
        public DappTestService.DAppDriverControlMsg dAppDriverCtlMsg;
//        volatile EmbeddedBrowser browser;

        public ControlMsgHandlerThread (String dappName, int instanceId) {
            this.dappName = dappName;
            this.instanceId = instanceId;
        }

        StreamObserver<DappTestService.DAppDriverControlMsg> requestObserver;

        StreamObserver<DappTestService.DAppDriverControlMsg> responseObserver =
                DAppTestDriverServiceGrpc.newStub(channel).dappDriverControl(new StreamObserver<DappTestService.DAppDriverControlMsg>() {
                    @Override
                    public void onNext(DappTestService.DAppDriverControlMsg dAppDriverControlMsg) {
                        System.out.println("Receive from stream: " + dAppDriverControlMsg.getAllFields());
                        if (dappBrowser == null) {
                            System.out.println("Browser is null !?!");
                            return;
                        }
                        handleControlMsg(dAppDriverControlMsg);
                        dAppDriverCtlMsg = DappTestService.DAppDriverControlMsg
                                .newBuilder()
                                .setRole(Common.Role.DAPP)
                                .setId(dAppDriverControlMsg.getId())
                                .setDappName(dAppDriverControlMsg.getDappName())
                                .setInstanceId(dAppDriverControlMsg.getInstanceId())
                                .setControlType(DappTestService.DAppDriverControlType.NilType)
                                .build();
                        responseObserver.onNext(dAppDriverControlMsg);
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable t) {
                        System.out.printf("Error from stream %s", t.toString());
                        t.printStackTrace();
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Stream completed");
                    }
                });

//        public void setBrowser(EmbeddedBrowser browser) {
//            this.browser = browser;
//
//            if (browser == null) {
//                System.out.println("After setting browser, browser is null");
//            } else {
//                System.out.println("After setting browser, browser is not null");
//            }
//        }


        @Override
        public void run() {
            requestObserver = asyncStub.dappDriverControl(responseObserver);

            DappTestService.DAppDriverControlMsg dAppDriverControlMsg = DappTestService.DAppDriverControlMsg
                    .newBuilder()
                    .setRole(Common.Role.DAPP)
                    .setId(INIT_CONTROL_MSG_ID)
                    .setDappName(this.dappName)
                    .setInstanceId(Integer.toString(this.instanceId))
                    .setControlType(DappTestService.DAppDriverControlType.NilType)
                    .build();
            responseObserver.onNext(dAppDriverControlMsg);
//            requestObserver.onCompleted();


//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        }
    }
}
