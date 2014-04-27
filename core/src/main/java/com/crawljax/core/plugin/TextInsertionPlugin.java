package com.crawljax.core.plugin;

import com.crawljax.core.CrawlerContext;
import com.crawljax.core.state.StateVertex;
import com.crawljax.util.XPathHelper;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextInsertionPlugin extends ElementInteractionPlugin {

	private static final Logger LOG = LoggerFactory.getLogger(TextInsertionPlugin.class);

	private static final class TextInsertAction implements StateVertex.DomAction {

		private final String text;
		private final String xPath;

		private TextInsertAction(String text, String xPath) {
			this.text = text;
			this.xPath = xPath;
		}

		@Override
		public void perform(WebDriver driver, CrawlerContext context) {
			WebElement found = driver.findElement(By.xpath(xPath));
			if (found == null) {
				LOG.debug("Could not find element with XPath {} to replay in state {}", xPath,
						context.getCurrentState().getName());
			} else {
				found.sendKeys(text);
			}
		}
	}

	private final String text;

	public TextInsertionPlugin(String text,
			ImmutableList<Predicate<Document>> predicates, By selector) {
		super(predicates, selector);
		this.text = text;
	}

	@Override
	protected void interactWith(final WebElement element, Document dom, StateVertex newState, CrawlerContext context) {
		element.sendKeys(text);
		if (element instanceof RemoteWebElement) {
			final String xPath = XPathHelper.pathToClosestId((RemoteWebElement) element);
			newState.addDomAction(new TextInsertAction(text, xPath));
		}
	}
}
