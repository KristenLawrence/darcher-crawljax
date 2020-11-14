package com.crawljax.examples;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.CrawlSession;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.*;
import com.crawljax.core.state.Identification;
import com.crawljax.forms.FormInput;
import com.crawljax.forms.InputValue;
import com.crawljax.plugins.crawloverview.CrawlOverview;
import org.kristen.crawljax.plugins.grpc.GRPCClientPlugin;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GivethForeignExperiment {
    private static final long WAIT_TIME_AFTER_EVENT = 500;
    private static final long WAIT_TIME_AFTER_RELOAD = 500;
    private static final String DAPP_URL = "http://localhost:3010/";
    private static final String DAPP_NAME = "Giveth Foreign";
    private static int instanceId = 1;
    private static final String METAMASK_POPUP_URL = "chrome-extension://jbppcachblnkaogkgacckpgohjbpcekf/home.html";
    private static final String METAMASK_PASSWORD = "12345678";
    private static final String BROWSER_PROFILE_PATH = "/Users/troublor/workspace/darcher_mics/browsers/Chrome/UserData";

    // DApp Gloabal Variables
    private static final String ETHEREUM_ADDRESS = "0x90F8bf6A479f320ead074411a4B0e7944Ea8c9C1";
    private static final String OTHER_ADDRESS = "0xFFcf8FDEE72ac11b5c542428B35EEF5769C409f0";


    /**
     * Run this method to start the crawl.
     *
     * @throws IOException when the output folder cannot be created or emptied.
     */
    public static void main(String[] args) throws IOException {
        CrawljaxConfiguration.CrawljaxConfigurationBuilder builder = CrawljaxConfiguration.builderFor(DAPP_URL);

//        builder.crawlRules().setFormFillMode(CrawlRules.FormFillMode.RANDOM);
//        builder.crawlRules().click("div").withAttribute("")
        // we use normal mode to avoid randomly fill forms and only allow predefined form inputs
        builder.crawlRules().setFormFillMode(CrawlRules.FormFillMode.NORMAL);
        builder.crawlRules().clickOnce(true);
        // click these elements
        builder.crawlRules().click("A");
        builder.crawlRules().click("BUTTON");

        builder.crawlRules().crawlHiddenAnchors(true);
        builder.crawlRules().crawlFrames(false);
        builder.setUnlimitedCrawlDepth();
        builder.setUnlimitedRuntime();
        builder.setUnlimitedStates();

        // 1 hour timeout
        builder.setMaximumRunTime(1, TimeUnit.HOURS);

//        builder.setMaximumStates(0); // unlimited
        builder.setMaximumDepth(0); // unlimited
        builder.crawlRules().clickElementsInRandomOrder(true);

        // Set timeouts
        builder.crawlRules().waitAfterReloadUrl(WAIT_TIME_AFTER_RELOAD, TimeUnit.MILLISECONDS);
        builder.crawlRules().waitAfterEvent(WAIT_TIME_AFTER_EVENT, TimeUnit.MILLISECONDS);

        /* don't click unrelated buttons */
        builder.crawlRules().dontClick("A").withText(" Join Giveth");
        builder.crawlRules().dontClick("BUTTON").withText("Draft Saved");
        builder.crawlRules().dontClick("A").withText("Tech Support");
        builder.crawlRules().dontClick("A").withAttribute("id", "navbarDropdownYou");

        // form input specifications
        InputSpecification input = new InputSpecification();

        /* don't click editor tool bar */
        builder.crawlRules().dontClick("BUTTON").underXPath("//*[@id=\"quill-formsy\"]/DIV[2]/DIV[3]");
        builder.crawlRules().dontClick("A").underXPath("//*[@id=\"quill-formsy\"]/DIV[2]/DIV[3]");
        // don't click hidden things
        builder.crawlRules().dontClick("BUTTON").underXPath("//*[@id=\"quill-formsy\"]/DIV[1]");
        builder.crawlRules().dontClick("A").underXPath("//*[@id=\"quill-formsy\"]/DIV[1]");

        /* new create Campaign form */
        Form createCampaignForm = new Form();
        AtomicInteger campaignCount = new AtomicInteger(0);
        // campaign name
        createCampaignForm.inputField(FormInput.InputType.DYNAMIC, new Identification(Identification.How.id, "title-input"))
                .setInputGenerator((webElement, nodeElement) -> new InputValue("Campaign" + campaignCount.getAndIncrement()));
        // campaign description
        createCampaignForm.inputField(FormInput.InputType.CUSTOMIZE, new Identification(Identification.How.xpath, "//*[@id=\"quill-formsy\"]/DIV[2]/DIV[1]"))
                .setInputFiller((webElement, nodeElement) -> webElement.sendKeys("Several descriptions here..."));
        // campaign picture
        createCampaignForm.inputField(FormInput.InputType.TEXT, new Identification(Identification.How.name, "picture"))
                .inputValues("/Users/troublor/workspace/darcher/packages/darcher-examples/giveth/misc/picture.jpg");
        // select reviewer
        createCampaignForm.inputField(FormInput.InputType.SELECT, new Identification(Identification.How.name, "reviewerAddress"))
                .inputValues(ETHEREUM_ADDRESS); // select Giveth0 account as reviewer always
        // attach create campaign form at BUTTON[@text='Create Campaign']
        input.setValuesInForm(createCampaignForm).beforeClickElement("BUTTON").withText("Create Campaign");

        /* Create DAC form */
        Form createDACForm = new Form();
        AtomicInteger dacCount = new AtomicInteger(0);
        // campaign name
        createDACForm.inputField(FormInput.InputType.DYNAMIC, new Identification(Identification.How.id, "title-input"))
                .setInputGenerator((webElement, nodeElement) -> new InputValue("DAC" + dacCount.getAndIncrement()));
        // campaign description
        createDACForm.inputField(FormInput.InputType.CUSTOMIZE, new Identification(Identification.How.xpath, "//*[@id=\"quill-formsy\"]/DIV[2]/DIV[1]"))
                .setInputFiller((webElement, nodeElement) -> webElement.sendKeys("Several descriptions here..."));
        // campaign picture
        createDACForm.inputField(FormInput.InputType.TEXT, new Identification(Identification.How.name, "picture"))
                .inputValues("/Users/troublor/workspace/darcher/packages/darcher-examples/giveth/misc/picture.jpg");
        // attach create campaign form at BUTTON[@text='Create DAC']
        input.setValuesInForm(createCampaignForm).beforeClickElement("BUTTON").withText("Create DAC");

        /* Create Milestone form */
        Form createMileStoneForm = new Form();
        AtomicInteger milestoneCount = new AtomicInteger(0);
        // campaign name
        createMileStoneForm.inputField(FormInput.InputType.DYNAMIC, new Identification(Identification.How.id, "title-input"))
                .setInputGenerator((webElement, nodeElement) -> new InputValue("DAC" + milestoneCount.getAndIncrement()));
        // campaign description
        createMileStoneForm.inputField(FormInput.InputType.CUSTOMIZE, new Identification(Identification.How.xpath, "//*[@id=\"quill-formsy\"]/DIV[2]/DIV[1]"))
                .setInputFiller((webElement, nodeElement) -> {
                    webElement.sendKeys("Several descriptions here...");
                });
        // campaign picture
        createMileStoneForm.inputField(FormInput.InputType.TEXT, new Identification(Identification.How.name, "picture"))
                .inputValues("/Users/troublor/workspace/darcher/packages/darcher-examples/giveth/misc/picture.jpg");
        // select reviewer
        createMileStoneForm.inputField(FormInput.InputType.SELECT, new Identification(Identification.How.name, "reviewerAddress"))
                .inputValues(ETHEREUM_ADDRESS); // select Giveth0 account as reviewer always
        // select money destination after completion
        createCampaignForm.inputField(FormInput.InputType.TEXT, new Identification(Identification.How.name, "recipientAddress"))
                .inputValues(ETHEREUM_ADDRESS);
        // no need to click "Use My Address" anymore
        builder.crawlRules().dontClick("BUTTON").withText("Use My Address");
        // set maximum amount
        createCampaignForm.inputField(FormInput.InputType.NUMBER, new Identification(Identification.How.name, "fiatAmount"))
                .inputValues("1");
        // attach create campaign form at BUTTON[@text='Create Milestone']
        input.setValuesInForm(createCampaignForm).beforeClickElement("BUTTON").withText("Create Milestone");

        /* cancel campaign confirmation input */
        input.inputField(FormInput.InputType.DYNAMIC, new Identification(Identification.How.xpath, "/HTML/BODY/DIV/DIV/DIV[4]/DIV/INPUT"))
                .setInputGenerator((webElement, nodeElement) -> {
                    // fill the confirmation input by copy the answer from previous sibling node
                    String answer = nodeElement.getPreviousSibling().getTextContent();
                    return new InputValue(answer);
                });

        /* Change Milestone Recipient */
        input.inputField(FormInput.InputType.DYNAMIC,
                new Identification(Identification.How.xpath, "//INPUT[@class='swal-content__input']"))
                .setInputGenerator((webElement, nodeElement) -> {
                    if (nodeElement.getAttribute("placeholder").contains("recipient address")) {
                        return new InputValue(OTHER_ADDRESS);
                    } else {
                        return null;
                    }
                });

        /* Delegate Donation Form */
        Form delegateDonationForm = new Form();
        // delegate 1 ETH
        delegateDonationForm.inputField(FormInput.InputType.NUMBER,
                new Identification(Identification.How.name, "amount"))
                .inputValues("1");
        // click source
        builder.crawlRules().click("DIV").withAttribute("class", "ReactTokenInput__option");
        input.setValuesInForm(delegateDonationForm).beforeClickElement("BUTTON").withText("Delegate here");

        /* Cancel Milestone Form */
        Form cancelMilestoneForm = new Form();
        cancelMilestoneForm.inputField(FormInput.InputType.CUSTOMIZE,
                new Identification(Identification.How.xpath, "//*[@id=\"quill-formsy\"]/DIV[2]/DIV[1]"))
                .setInputFiller((webElement, nodeElement) -> webElement.sendKeys("Cancel milestone"));
        input.setValuesInForm(cancelMilestoneForm).beforeClickElement("BUTTON").withText("Cancel Milestone");

        /* Change ownership form */
        Form changeOwnershipForm = new Form();
        changeOwnershipForm.inputField(FormInput.InputType.SELECT,
                new Identification(Identification.How.name, "ownerAddress"))
                .inputValues(OTHER_ADDRESS);

        /* Don't download CSV */
        builder.crawlRules().dontClick("BUTTON").withText("Download CSV");


        builder.crawlRules().setInputSpec(input);
        builder.setBrowserConfig(
                new BrowserConfiguration(EmbeddedBrowser.BrowserType.CHROME, 1,
                        new BrowserOptions(BROWSER_PROFILE_PATH)));

        // CrawlOverview
        builder.addPlugin(new CrawlOverview());
//        builder.addPlugin(new MetaMaskSupportPlugin(METAMASK_POPUP_URL, METAMASK_PASSWORD));
        builder.addPlugin(new GRPCClientPlugin(DAPP_NAME, instanceId, METAMASK_POPUP_URL, DAPP_URL, METAMASK_PASSWORD));

        CrawljaxRunner crawljax = new CrawljaxRunner(builder.build());
        CrawlSession session = crawljax.call();
        System.out.println("Crawl Complete: " + crawljax.getReason());
    }
}
