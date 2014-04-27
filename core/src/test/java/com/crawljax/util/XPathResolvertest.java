package com.crawljax.util;

import com.crawljax.browser.BrowserProvider;
import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.test.BrowserTest;
import com.crawljax.test.RunWithWebServer;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;

@Category(BrowserTest.class)
public class XPathResolverTest {

	@ClassRule
	public static final RunWithWebServer SERVER = new RunWithWebServer("site");

	@Rule
	public final BrowserProvider provider = new BrowserProvider();

	private EmbeddedBrowser browser;

	@Test
	public void testAllXpathsAreResolvedCorrectly() {
		RemoteWebDriver driver = provider.newBrowser();
		driver.navigate().to(SERVER.getSiteUrl().resolve("xpathResolveTestFile.html").toString());
		for (WebElement element : driver.findElements(By.cssSelector("*"))) {
			System.out.println(XPathHelper.pathToClosestId(element));
		}
	}


}
