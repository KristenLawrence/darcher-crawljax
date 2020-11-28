package xyz.troublor.crawljax.experiments;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.core.CrawljaxRunner;
import com.crawljax.core.configuration.*;
import com.crawljax.core.plugin.OnUrlLoadPlugin;
import com.crawljax.core.state.Identification;
import com.crawljax.forms.FormInput;
import org.kristen.crawljax.plugins.grpc.GRPCClientPlugin;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class AugurExperiment extends Experiment {
    private static final long WAIT_TIME_AFTER_EVENT = 500;
    private static final long WAIT_TIME_AFTER_RELOAD = 500;
    private static final String DAPP_URL = "http://localhost:8080/";
    private static final String DAPP_NAME = "Augur";
    private static int instanceId = 1;
    private static final String METAMASK_POPUP_URL = "chrome-extension://jbppcachblnkaogkgacckpgohjbpcekf/home.html";
    private static final String METAMASK_PASSWORD = "12345678";
    private static final String BROWSER_PROFILE_PATH = "/Users/troublor/workspace/darcher_mics/browsers/Chrome/UserData";

    // DApp Gloabal Variables
    private static final String ETHEREUM_ADDRESS = "0x913dA4198E6bE1D5f5E4a40D0667f70C0B5430Eb";
    private static final String OTHER_ADDRESS = "0x9D4C6d4B84cd046381923C9bc136D6ff1FE292D9";


    /**
     * Run this method to start the crawl.
     **/
    public CrawljaxRunner initialize(String chromeDebuggerAddress) {
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

        /* Set timeouts */
        builder.crawlRules().waitAfterReloadUrl(WAIT_TIME_AFTER_RELOAD, TimeUnit.MILLISECONDS);
        builder.crawlRules().waitAfterEvent(WAIT_TIME_AFTER_EVENT, TimeUnit.MILLISECONDS);

        InputSpecification inputSpec = new InputSpecification();

        /* creating markets (create customized market is clicked)*/
        inputSpec.inputField(FormInput.InputType.CUSTOMIZE,
                new Identification(Identification.How.xpath, "//BUTTON[@class='SingleDatePickerInput_calendarIcon SingleDatePickerInput_calendarIcon_1']"))
                .setInputFiller((driver, webElement, nodeElement) -> {
                    Date now = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(now);
                    int today = cal.get(Calendar.DAY_OF_MONTH);
                    int monthMaxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

                    // click date picker
                    webElement.click();
                    WebElement picker = new WebDriverWait(driver, Duration.ofMillis(100))
                            .until(d -> d.findElement(
                                    By.xpath("//DIV[@class='SingleDatePicker_picker SingleDatePicker_picker_1 SingleDatePicker_picker__directionLeft SingleDatePicker_picker__directionLeft_2']")));
                    // find tomorrow button
                    if (today > monthMaxDays - 3) {
                        // next month
                        picker.findElement(By.className("DayPickerNavigation_rightButton__horizontal")).click();
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    cal.add(Calendar.DAY_OF_YEAR, 3);
                    int tomorrow = cal.get(Calendar.DAY_OF_MONTH);
                    String monthStr = new SimpleDateFormat("MMMM").format(cal.getTime());
                    List<WebElement> elements =
                            picker.findElements(By.xpath("//BUTTON[text()='" + tomorrow + "']"));
                    for (WebElement elem :
                            elements) {
                        if (elem.getAttribute("aria-label").contains(monthStr)) {
                            elem.click();
                            break;
                        }
                    }

                    // time picker
                    driver.findElement(By.cssSelector(".form-styles_TimeSelector")).findElement(By.tagName("BUTTON")).click();

                    // Market question
                    List<WebElement> textAreas = driver.findElements(By.tagName("TEXTAREA"));
                    for (WebElement textArea :
                            textAreas) {
                        String placeHolder = textArea.getAttribute("placeholder");
                        if (placeHolder.startsWith("Example")) {
                            textArea.sendKeys("Is DArcher useful to test DApps?");
                            break;
                        }
                    }

                    // Market Category
                    driver.findElement(By.xpath("//UL[@class='form-styles_CategoryMultiSelect']/LI[1]/*/DIV[@role='button']"))
                            .click();
                    WebElement entertainment = new WebDriverWait(driver, Duration.ofMillis(100))
                            .until(d -> d.findElement(By.xpath("//UL[@class='form-styles_CategoryMultiSelect']/LI[1]//BUTTON[@value='Entertainment']")));
                    entertainment.click();
                    WebElement secondaryCategoryBtn = new WebDriverWait(driver, Duration.ofMillis(100))
                            .until(d -> d.findElement(By.xpath("//UL[@class='form-styles_CategoryMultiSelect']/LI[2]/*/DIV[@role='button']")));
                    secondaryCategoryBtn.click();
                    WebElement awards = new WebDriverWait(driver, Duration.ofMillis(100))
                            .until(d -> d.findElement(By.xpath("//UL[@class='form-styles_CategoryMultiSelect']/LI[2]//BUTTON[@value='Awards']")));
                    awards.click();
                    WebElement subCategoryBtn = new WebDriverWait(driver, Duration.ofMillis(100))
                            .until(d -> d.findElement(By.xpath("//UL[@class='form-styles_CategoryMultiSelect']/LI[3]/*/DIV[@role='button']")));
                    subCategoryBtn.click();
                    WebElement academicAwards = new WebDriverWait(driver, Duration.ofMillis(100))
                            .until(d -> d.findElement(By.xpath("//UL[@class='form-styles_CategoryMultiSelect']/LI[3]//BUTTON[@value='Academy Awards']")));
                    academicAwards.click();
                });
        /* Don't click view txs button in account summary */
        builder.crawlRules().dontClick("BUTTON").withAttribute("title", "View Transactions");

        /* Don't click save draft */
        builder.crawlRules().dontClick("BUTTON").withAttribute("title", "Save draft");

        /* Don't click Back */
        builder.crawlRules().dontClick("BUTTON").withAttribute("title", "Back");

        /* Don't preview market */
        builder.crawlRules().dontClick("BUTTON").withAttribute("title", "Preview your market");

        /* Don't click Add Funds */
        builder.crawlRules().dontClick("BUTTON").withAttribute("title", "Deposit");

        /* Don't click Learn More */
        builder.crawlRules().dontClick("BUTTON").withText("Learn more");

        /* Don't click depth chart */
        builder.crawlRules().dontClick("BUTTON").underXPath("//DIV[@class='depth-styles_MarketOutcomeDepth__container']");

        /* Transfer funds form (same with Withdraw form) */
        Form transferForm = new Form();
        transferForm.inputField(FormInput.InputType.TEXT,
                new Identification(Identification.How.xpath, "//INPUT[@placeholder='0x...']"))
                .inputValues(OTHER_ADDRESS); // transfer to Augur1
        transferForm.inputField(FormInput.InputType.TEXT,
                new Identification(Identification.How.xpath, "//INPUT[@placeholder='0.00']"))
                .inputValues("10");
        inputSpec.setValuesInForm(transferForm).beforeClickElement("BUTTON").withAttribute("title", "Send");

        /* don't cancel */
        builder.crawlRules().dontClick("BUTTON").withAttribute("title", "Cancel");
        builder.crawlRules().dontClick("BUTTON").withText("cancel");
        builder.crawlRules().dontClick("BUTTON").withText("Cancel");

        /* Don't click Get REP button, it is not available in dev mode */
        builder.crawlRules().dontClick("BUTTON").withAttribute("title", "Get REP");

        /* Don't click Disputing Guide button */
        builder.crawlRules().dontClick("BUTTON").withAttribute("class", "common-styles_ReportingModalButton");

        /* But Tokens form */
        Form buyTokensForm = new Form();
        buyTokensForm.inputField(FormInput.InputType.TEXT,
                new Identification(Identification.How.xpath, "//INPUT[@placeholder='0.0000']"))
                .inputValues("10");
        inputSpec.setValuesInForm(buyTokensForm).beforeClickElement("BUTTON").withText("buy");

        /* Don't click alert button */
        builder.crawlRules().dontClick("BUTTON").withAttribute("class", "top-bar-styles_alerts");

        /* Don't click category filter button */
        builder.crawlRules().dontClick("BUTTON").withAttribute("data-testid", "Category-0");
        builder.crawlRules().dontClick("BUTTON").withAttribute("data-testid", "Category-1");
        builder.crawlRules().dontClick("BUTTON").withAttribute("data-testid", "Category-2");
        builder.crawlRules().dontClick("BUTTON").withAttribute("data-testid", "label-type-0");
        builder.crawlRules().dontClick("BUTTON").withAttribute("data-testid", "label-type-1");
        builder.crawlRules().dontClick("BUTTON").withAttribute("data-testid", "label-type-2");

        /* Don't click share buttons */
        builder.crawlRules().dontClick("BUTTON").withAttribute("id", "facebookButton");
        builder.crawlRules().dontClick("BUTTON").withAttribute("id", "twitterButton");
        builder.crawlRules().dontClick("BUTTON").withAttribute("title", "Toggle Favorite");

        /*Don't click show details button*/
        builder.crawlRules().dontClick("BUTTON").underXPath("//DIV[@class='market-header-styles_MarketDetails']");

        /* Don't click buttons in open order lists */
        builder.crawlRules().dontClick("BUTTON").underXPath("//DIV[@class='market-view-styles_OrdersPane']");

        /* Click existing orders */
        builder.crawlRules().click("DIV").underXPath("//SECTION[@class='order-book-styles_OrderBook']/DIV/DIV");

        /* Don't click buttons in trading form */
        builder.crawlRules().dontClick("BUTTON")
                .underXPath("//SECTION[@class='trading-form-styles_TradingForm']/SECTION[1]/DIV[1]");

        /* Don't click expand toggle button*/
        builder.crawlRules().dontClick("BUTTON").withAttribute("class", "buttons-styles_ToggleExtendButton");

        /* Don't click stats buttons*/
        builder.crawlRules().dontClick("BUTTON").underXPath("//DIV[@class='stats-styles_Stats']");

        /* place order */
        Form placeBuyOrderForm = new Form();
        placeBuyOrderForm.inputField(FormInput.InputType.CUSTOMIZE,
                new Identification(Identification.How.id, "quantity"))
                .setInputFiller((driver, webElement, nodeElement) -> {
                    if (webElement.getAttribute("value").equals("")) {
                        webElement.sendKeys("100");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        placeBuyOrderForm.inputField(FormInput.InputType.CUSTOMIZE,
                new Identification(Identification.How.id, "limit-price"))
                .setInputFiller((driver, webElement, nodeElement) -> {
                    if (webElement.getAttribute("value").equals("")) {
                        webElement.sendKeys("0.5");
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
        inputSpec.setValuesInForm(placeBuyOrderForm).beforeClickElement("BUTTON").withAttribute("class", "buttons-styles_SellOrderButton");
        inputSpec.setValuesInForm(placeBuyOrderForm).beforeClickElement("BUTTON").withAttribute("class", "buttons-styles_BuyOrderButton");

        builder.crawlRules().setInputSpec(inputSpec);
        builder.setBrowserConfig(
                new BrowserConfiguration(EmbeddedBrowser.BrowserType.CHROME_EXISTING, chromeDebuggerAddress));

        // CrawlOverview
//        builder.addPlugin(new CrawlOverview());
//        builder.addPlugin(new MetaMaskSupportPlugin(METAMASK_POPUP_URL, METAMASK_PASSWORD));
        builder.addPlugin(new GRPCClientPlugin(DAPP_NAME, instanceId, METAMASK_POPUP_URL, DAPP_URL, METAMASK_PASSWORD));

        // some Augur-specific plugins
        builder.addPlugin((OnUrlLoadPlugin) context -> {
            WebDriver driver = context.getBrowser().getWebDriver();
            System.out.print("Wait for Augur DApp reloading...");
            new WebDriverWait(driver, Duration.ofSeconds(10)).until(d -> {
                try {
                    WebElement sample = driver.findElement(By.xpath("//*[@id=\"app\"]/main/div/section/section[3]/aside/ul/div[1]/div/div[1]/div/span[2]"));
                    try {
                        Integer.parseInt(sample.getText());
                    } catch (NumberFormatException ignored) {
                        return false;
                    }
                    return true;
                } catch (NoSuchElementException e) {
                    try {
                        driver.findElement(By.xpath("//DIV[@class='price-history-styles_PriceHistory']/DIV"));
                        return true;
                    } catch (NoSuchElementException ignored) {
                        return false;
                    }
                }
            });

            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            System.out.println("done");
        });

        // test zone
//        builder.crawlRules().click("BUTTON").withText("Create Market");
//        builder.crawlRules().click("BUTTON").withText("Create a custom market");
//        builder.crawlRules().click("BUTTON").withAttribute("title", "Next");
//        builder.crawlRules().click("BUTTON").withAttribute("class", "buttons-styles_BuyOrderButton");
//        builder.crawlRules().click("BUTTON").withAttribute("class", "buttons-styles_SellOrderButton");
//        builder.crawlRules().click("BUTTON").withText("Account Summary");
//        builder.crawlRules().click("BUTTON").underXPath("//DIV[@class='notification-styles_Message']");

        return new CrawljaxRunner(builder.build());
    }

    public static void main(String[] args) throws IOException {
        new AugurExperiment().start("scripts" + File.separator + "status.log", "localhost:9222");
    }
}
