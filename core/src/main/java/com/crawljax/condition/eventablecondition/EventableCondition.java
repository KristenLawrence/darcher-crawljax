package com.crawljax.condition.eventablecondition;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.crawljax.browser.EmbeddedBrowser;
import com.crawljax.condition.Condition;
import com.crawljax.core.state.Eventable;
import com.crawljax.util.XPathHelper;

/**
 * An EventableCondition specifies properties of an {@link Eventable} which should be satisfied in
 * order to crawl the element. The user does not need this class when using the API. Only for use
 * with properties files .
 */
public class EventableCondition {

	private final String id;
	private List<Condition> conditions = new ArrayList<Condition>();
	private String inXPath;

	/**
	 * @param id
	 *            Identifier.
	 */
	public EventableCondition(String id) {
		this.id = id;
	}

	/**
	 * @param id
	 *            Identifier.
	 * @param conditions
	 *            Conditions that should be satisfied.
	 */
	public EventableCondition(String id, Condition... conditions) {
		this.id = id;
		this.conditions.addAll(Arrays.asList(conditions));
	}

	/**
	 * @param browser
	 *            The browser.
	 * @return true iff all the conditions are satisfied.
	 */
	public boolean checkAllConditionsSatisfied(EmbeddedBrowser browser) {
		for (Condition condition : getConditions()) {
			if (!condition.check(browser)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @return id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return The conditions.
	 */
	public List<Condition> getConditions() {
		return conditions;
	}

	/**
	 * @return The inXPath.
	 */
	public String getInXPath() {
		return inXPath;
	}

	/**
	 * @param inXPath
	 *            The inXPath expression.
	 */
	public void setInXPath(String inXPath) {
		this.inXPath = XPathHelper.formatXPath(inXPath);
	}

	/**
	 * @param conditions
	 *            the conditions to set
	 */
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

	/**
	 * Add a new condition to the list of conditions.
	 * 
	 * @param condition
	 *            The condition.
	 */
	public void addCondition(Condition condition) {
		this.conditions.add(condition);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EventableCondition [");
		if (id != null) {
			builder.append("id=");
			builder.append(id);
			builder.append(", ");
		}
		if (conditions != null) {
			builder.append("conditions=");
			builder.append(conditions);
			builder.append(", ");
		}
		if (inXPath != null) {
			builder.append("inXPath=");
			builder.append(inXPath);
			builder.append(", ");
		}
		builder.append("]");
		return builder.toString();
	}

}
