package com.crawljax.core;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.condition.eventablecondition.EventableCondition;
import com.crawljax.core.state.Identification;
import com.crawljax.util.DomUtils;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import org.w3c.dom.Element;

/**
 * Candidate element for crawling.
 */
public class CandidateElement {

	private final Identification identification;

	private final Element element;
	private final String relatedFrame;

	private EventableCondition eventableCondition;

	/**
	 * Constructor for a element a identification and a relatedFrame.
	 *
	 * @param element
	 * 		the element.
	 * @param identification
	 * 		the identification.
	 * @param relatedFrame
	 * 		the frame this element belongs to.
	 */
	public CandidateElement(Element element, Identification identification, String relatedFrame) {
		this.identification = identification;
		this.element = element;
		this.relatedFrame = relatedFrame;
	}

	/**
	 * Constructor for a element a xpath-identification and no relatedFrame.
	 *
	 * @param element
	 * 		the element
	 * @param xpath
	 * 		the xpath expression of the element
	 */
	public CandidateElement(Element element, String xpath) {
		this(element, new Identification(Identification.How.xpath, xpath), "");
	}

	/**
	 * @return unique string without atusa attribute
	 */
	public String getGeneralString() {
		ImmutableSet<String> exclude = ImmutableSet.of("atusa");

		StringBuilder result = new StringBuilder();
		if (element != null) {
			result.append(this.element.getNodeName()).append(": ");

		}
		result.append(DomUtils.getElementAttributes(this.element, exclude)).append(' ')
				.append(this.identification).append(' ').append(relatedFrame);

		return result.toString();
	}

	/**
	 * @return unique string of this candidate element
	 */
	public String getUniqueString() {

		String result = "";

		if (element != null) {
			result +=
					this.element.getNodeName() + ": "
							+ DomUtils.getAllElementAttributes(this.element) + " ";
		}

		result += this.identification + " " + relatedFrame;

		return result;
	}

	/**
	 * @return the element
	 */
	public Element getElement() {
		return element;
	}

	/**
	 * @param eventableCondition
	 * 		the EventableCondition
	 */
	public void setEventableCondition(EventableCondition eventableCondition) {
		this.eventableCondition = eventableCondition;
	}

	/**
	 * @return the identification object.
	 */
	public Identification getIdentification() {
		return identification;
	}

	/**
	 * @return the relatedFrame
	 */
	public String getRelatedFrame() {
		return relatedFrame;
	}

	/**
	 * Check all eventable Condition for correctness.
	 *
	 * @param browser
	 * 		the current browser instance that contains the current dom
	 * @return true if all conditions are satisfied or no conditions are specified
	 * @see #eventableCondition
	 * @see EventableCondition#checkAllConditionsSatisfied(EmbeddedBrowser)
	 */
	public boolean allConditionsSatisfied(EmbeddedBrowser browser) {
		if (eventableCondition != null) {
			return eventableCondition.checkAllConditionsSatisfied(browser);
		}
		// No condition specified so return true....
		return true;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("identification", identification)
				.add("element", element)
				.add("eventableCondition", eventableCondition)
				.add("relatedFrame", relatedFrame)
				.toString();
	}
}
