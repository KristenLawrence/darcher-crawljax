package com.crawljax.core.plugin;

import com.crawljax.core.CrawlerContext;
import com.crawljax.core.state.StateVertex;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class ElementInteractionPlugin implements OnNewStatePlugin {

	private static final Logger LOG = LoggerFactory.getLogger(ElementInteractionPlugin.class);
	private final ImmutableList<Predicate<Document>> predicates;
	private final By selector;

	protected ElementInteractionPlugin(ImmutableList<Predicate<Document>> predicates, By selector) {
		this.predicates = predicates;
		this.selector = selector;
	}

	@Override
	public void onNewState(CrawlerContext context, StateVertex newState) {
		Document dom = Jsoup.parse(context.getBrowser().getDom());
		for (Predicate<Document> predicate : predicates) {
			if (!predicate.apply(dom)) {
				LOG.debug("Predicate {} was false", predicate);
				return;
			}
			LOG.debug("All predicates succeeded");
		}
		Elements elements = resolve(selector, dom);
		for (WebElement element : context.getBrowser().findElements(selector)) {
			try {
				interactWith(element, dom, newState, context);
			} catch (RuntimeException e) {
				LOG.warn("Interacting with element {} in state {} failed because: {}",
						element, newState.getName(), e.getMessage(), e);
				LOG.info("The stack trace was: {}", e.getMessage(), e);
			}
		}
	}

	private Elements resolve(By selector, Document dom) {

		//TODO return the list as JSOUP
		if (selector instanceof By.ByCssSelector) {

		} else if (selector instanceof By.ByTagName) {

		} else if (selector instanceof By.ByClassName) {

		} else if (selector instanceof By.ByXPath) {

		} else if (selector instanceof By.ById) {

		}
	}

	/**
	 * Use this method to interact with elements.
	 *
	 * <p>
	 * You can for example insert text into elements. Force a particular state on them. Etc.
	 * </p>
	 *
	 * @param element
	 * 		The element you can interact with.
	 * @param dom
	 * 		The current document. This is a mutable copy of the actual dom. The mutated DOM is passed through to every
	 * 		element found in this state and disposed after that. Unless you really know what you're doing,
	 * 		do not modify this dom.
	 * 		element
	 * @param newState
	 * 		the new state this action will be applied to.
	 * @param context
	 * 		the current context.
	 */
	protected abstract void interactWith(WebElement element, Document dom, StateVertex newState,
			CrawlerContext context);
}
