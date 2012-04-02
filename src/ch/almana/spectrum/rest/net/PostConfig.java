package ch.almana.spectrum.rest.net;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import ch.almana.spectrum.rest.access.BaseModelAccess;
import ch.almana.spectrum.rest.model.SpectrumAttibute;

public class PostConfig {

	/*
	 *         <xs:element name="greater-than" type="comparison-oper-type" />
        <xs:element name="greater-than-or-equals" type="comparison-oper-type" />
        <xs:element name="less-than" type="comparison-oper-type" />
        <xs:element name="less-than-or-equals" type="comparison-oper-type" />
        <xs:element name="equals" type="comparison-oper-type" />
        <xs:element name="equals-ignore-case" type="comparison-oper-type" />
        <xs:element name="does-not-equal" type="comparison-oper-type" />
        <xs:element name="does-not-equal-ignore-case" type="comparison-oper-type" />
        <xs:element name="has-prefix" type="comparison-oper-type" />
        <xs:element name="does-not-have-prefix" type="comparison-oper-type" />
        <xs:element name="has-prefix-ignore-case" type="comparison-oper-type" />
        <xs:element name="does-not-have-prefix-ignore-case" type="comparison-oper-type" />
        <xs:element name="has-substring" type="comparison-oper-type" />
        <xs:element name="does-not-have-substring" type="comparison-oper-type"/>
        <xs:element name="has-substring-ignore-case" type="comparison-oper-type" />
        <xs:element name="does-not-have-substring-ignore-case" type="comparison-oper-type" />
        <xs:element name="has-suffix" type="comparison-oper-type" />
        <xs:element name="does-not-have-suffix" type="comparison-oper-type" />
        <xs:element name="has-suffix-ignore-case" type="comparison-oper-type" />
        <xs:element name="does-not-have-suffix-ignore-case" type="comparison-oper-type" />
        <xs:element name="has-pcre" type="comparison-oper-type" />
        <xs:element name="has-pcre-ignore-case" type="comparison-oper-type" />
        <xs:element name="has-wildcard" type="comparison-oper-type" />
        <xs:element name="has-wildcard-ignore-case" type="comparison-oper-type" />
        <xs:element name="is-derived-from" type="comparison-oper-type" />
        <xs:element name="not-is-derived-from" type="comparison-oper-type" />
	 */
	
	public enum FilterComparator {
		equals, greater_than, greater_than_or_equals, less_than, less_than_or_equals
	};

	class FilterConfig {
		String attr;
		String value;
		FilterComparator compatator;

		public FilterConfig(String attr, String value, FilterComparator compatator) {
			super();
			this.attr = attr;
			this.value = value;
			this.value = value;
			this.compatator = compatator;
		}
	}

	private int throttlesize = Integer.MAX_VALUE;
	private final List<FilterConfig> filters = new ArrayList<PostConfig.FilterConfig>();
//	private Set<String> attributesHandles;
	private Set<String> entityIds;
	private BaseModelAccess modelAccess;
	private String requestTag;

	public PostConfig(BaseModelAccess modelAccess) {
		super();
		this.modelAccess = modelAccess;
		this.requestTag = modelAccess.getRequestTag();
		if (modelAccess.isListMode()) {
			//addFilter(SpectrumAttibute.SEVERITY, "2", FilterComparator.greater_than_or_equals);
		} else {
//			setAttributes(modgetAttributesHandles());
			setEntityIds(entityIds);
		}
	}

	public void setThrottlesize(int throttlesize) {
		if (throttlesize > 0) {
			this.throttlesize = throttlesize;
		}
	}

	private void addDisplayedAttributes(StringBuilder sb) {
		 Set<String> attributesHandles = modelAccess.getAttributesHandles();
		if (attributesHandles  == null) {
			return;
		}
		for (String attr : attributesHandles) {
			sb.append("<rs:requested-attribute id=\"");
			sb.append(attr);
			sb.append("\" />");
			sb.append("\n");
		}
		sb.append("\n");
	}

	private void addRequestHeader(StringBuilder sb) {
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rs:").append(requestTag).append(" ");
		if (throttlesize > 0) {
			sb.append("throttlesize=\"");
			sb.append(throttlesize);
			sb.append("\"");
		}
		sb.append("\n xmlns:rs=\"http://www.ca.com/spectrum/restful/schema/request\"\n xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"  xsi:schemaLocation=\"http://www.ca.com/spectrum/restful/schema/request ../../../xsd/Request.xsd\">\n");
		sb.append("\n");
	}

	private void addRequestFooter(StringBuilder sb) {
		sb.append("\n");
		sb.append("</rs:").append(requestTag).append(">");
		sb.append("\n");
	}

	private void addAttributeFilter(StringBuilder sb) {
		// FIXME I doubt it works with more than one filter
		for (FilterConfig filter : filters) {
			String comp = filter.compatator.toString().replaceAll("_", "-");
			sb.append(modelAccess.getFilterPreamble());
			sb.append("    <search-criteria xmlns=\"http://www.ca.com/spectrum/restful/schema/filter\">\n");
			sb.append("    <filtered-models>\n");
			sb.append("      <").append(comp).append(">\n");
			sb.append("        <attribute id=\"").append(filter.attr).append("\">\n");
			sb.append("          <value>").append(filter.value).append("</value>\n");
			sb.append("        </attribute>\n");
			sb.append("      </").append(comp).append(">\n");
			sb.append("      </filtered-models>\n");
			sb.append("    </search-criteria>\n");
			sb.append(modelAccess.getFilterPostamble());
		}

	}

	public String getConfig() {
		StringBuilder sb = new StringBuilder();
		addRequestHeader(sb);
		sb.append(modelAccess.getGenericFilter());
		addDisplayedAttributes(sb);
		addAttributeFilter(sb);
		addEntities(sb);
		addRequestFooter(sb);
		return sb.toString();
	}

	private void addEntities(StringBuilder sb) {
		if (entityIds == null) {
			return;
		}
		for (String entity : entityIds) { //FIXME
			sb.append("<rs:alarms id=\"").append(entity).append("\" />\n");
		}
	}

	public void addFilter(String attr, String val, FilterComparator cmp) {
		filters.add(new FilterConfig(attr, val, cmp));
	}

//	public void setAttributes(Set<String> attributesHandles) {
//		this.attributesHandles = attributesHandles;
//	}

	public void setEntityIds(Set<String> entityIds) {
		this.entityIds = entityIds;
	}

}
