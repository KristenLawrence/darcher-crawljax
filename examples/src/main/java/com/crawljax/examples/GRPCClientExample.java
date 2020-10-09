package com.crawljax.examples;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.CrawlSession;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.*;
import com.crawljax.core.state.Identification;
import com.crawljax.forms.FormInput;
import com.crawljax.plugins.crawloverview.CrawlOverview;
import xyz.troublor.crawljax.plugins.metamask.MetaMaskSupportPlugin;
import org.kristen.crawljax.plugins.grpc.GRPCClientPlugin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GRPCClientExample {
    private static final long WAIT_TIME_AFTER_EVENT = 500;
    private static final long WAIT_TIME_AFTER_RELOAD = 500;
    private static final String DAPP_URL = "chrome-extension://pblaiiacglodkdimplphhfffmpblfgmh/home.html#send";
    private static final String DAPP_NAME = "Augur";
    private static int instanceId = 1;
//    chrome-extension://nkbihfbeogaeaoehlefnkodbefgpgknn/home.html
    private static final String METAMASK_POPUP_URL = "chrome-extension://pblaiiacglodkdimplphhfffmpblfgmh/home.html";
    private static final String METAMASK_PASSWORD = "gRP'b~jz|zz;DA7~[[P9";

    /**
     * Run this method to start the crawl.
     *
     * @throws IOException when the output folder cannot be created or emptied.
     */
    public static void main(String[] args) throws IOException {
        CrawljaxConfiguration.CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor(DAPP_URL);

        builder.crawlRules().setFormFillMode(CrawlRules.FormFillMode.RANDOM);
//        builder.crawlRules().click("div").withAttribute("")
//        builder.crawlRules().setFormFillMode(CrawlRules.FormFillMode.XPATH_TRAINING);
        // click these elements
//        builder.crawlRules().clickDefaultElements();
//        CrawljaxConfiguration crawler = new CrawljaxConfiguration();
//        crawler.click("a");
//        crawler.click("div").withAttribute("class", "clickable");
//        crawler.dontClick("a").withText("id", "logout");
//        crawler.dontClick("a").underXpath("//DIV[@id='header']");
        builder.crawlRules().click("A");
        builder.crawlRules().click("button");
        builder.crawlRules().click("div").underXPath("//*[@onclick]");

        builder.crawlRules().crawlHiddenAnchors(true);
        builder.crawlRules().crawlFrames(false);
        builder.setUnlimitedCrawlDepth();
        builder.setUnlimitedRuntime();
        builder.setUnlimitedStates();

        //builder.setMaximumStates(10);
        //builder.setMaximumDepth(3);
        builder.crawlRules().clickElementsInRandomOrder(false);

        // Set timeouts
        builder.crawlRules().waitAfterReloadUrl(WAIT_TIME_AFTER_RELOAD, TimeUnit.MILLISECONDS);
        builder.crawlRules().waitAfterEvent(WAIT_TIME_AFTER_EVENT, TimeUnit.MILLISECONDS);

        InputSpecification input = new InputSpecification();
        Identification id = new Identification(Identification.How.id, "limit-price");
        input.inputField(FormInput.InputType.TEXT, id).inputValues("121");
        id = new Identification(Identification.How.id, "quantity");
        input.inputField(FormInput.InputType.TEXT, id).inputValues("1");
        id = new Identification(Identification.How.id, "total-order-value");
        input.inputField(FormInput.InputType.TEXT, id).inputValues("1");
        builder.crawlRules().setInputSpec(input);



        builder.setBrowserConfig(
                new BrowserConfiguration(EmbeddedBrowser.BrowserType.CHROME, 1,
                        new BrowserOptions("/Users/shuqing/Documents/application")));

        // CrawlOverview
        builder.addPlugin(new CrawlOverview());
//        builder.addPlugin(new MetaMaskSupportPlugin(METAMASK_POPUP_URL, METAMASK_PASSWORD));
        builder.addPlugin(new GRPCClientPlugin(DAPP_NAME, instanceId, METAMASK_POPUP_URL, DAPP_URL, METAMASK_PASSWORD));

        CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
        CrawlSession session = crawljax.call();
        System.out.println("Crawl Complete: " + crawljax.getReason());

    }
}
