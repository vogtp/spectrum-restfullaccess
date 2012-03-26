package ch.almana.spectrum.rest.net;

import ch.almana.spectrum.rest.access.BaseModelAccess;
import ch.almana.spectrum.rest.model.SpectrumAttibute;

public class PostConfig {


	private int throttlesize = -1;
	private BaseModelAccess modelAccess;


	public PostConfig(BaseModelAccess modelAccess) {
		super();
		this.modelAccess = modelAccess;
	}
	
	public void setThrottlesize(int throttlesize) {
		this.throttlesize = throttlesize;
	}
	
	private static final String ALL_ALARMS_FILTER = "<rs:alarm-request throttlesize=\"10\"\n" + 
			"  xmlns:rs=\"http://www.ca.com/spectrum/restful/schema/request\"\n" + 
			"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" + 
			"  xsi:schemaLocation=\"http://www.ca.com/spectrum/restful/schema/request ../../../xsd/Request.xsd \">\n" + 
			"\n" + 
			"   <!-- \n" + 
			"         This xml can be posted to the Alarms URL to obtain all alarms\n" + 
			"         on all devices\n" + 
			"      -->\n" + 
			"\n" + 
			"  <!-- Attributes of Interest -->\n" + 
			"  <rs:requested-attribute id=\"0x11f53\" />\n" + 
			"  <rs:requested-attribute id=\"0x10000\" />\n" + 
			"  <rs:requested-attribute id=\"0x11f56\" />\n" + 
			"  <rs:requested-attribute id=\"0x12b4c\" />\n" + 
			"  <rs:requested-attribute id=\"0x11f4d\" />\n" + 
			"\n" + 
			"  <!-- Uses the existing All Devices Search delivered with Spectrum -->\n" + 
			"  <rs:target-models>\n" + 
			"    <rs:models-search>\n" + 
			"      <rs:search-criteria-file>topo/config/search-devices-criteria.xml</rs:search-criteria-file>\n" + 
			"    </rs:models-search>\n" + 
			"  </rs:target-models>\n" + 
			"\n" + 
			"</rs:alarm-request>" +
			"\n";
	
	
	private String getPostConfig() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rs:alarm-request ");
		if (throttlesize > 0) {
			sb.append("throttlesize=\"");
			sb.append(throttlesize);
			sb.append("\"");
		}
		sb.append("\n xmlns:rs=\"http://www.ca.com/spectrum/restful/schema/request\"\n xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"  xsi:schemaLocation=\"http://www.ca.com/spectrum/restful/schema/request ../../../xsd/Request.xsd\">\n");
		sb.append("\n");
		for (String attr : modelAccess.getAttributesHandles()) {
			sb.append("<rs:requested-attribute id=\"");
			sb.append(attr);
			sb.append("\" />");
			sb.append("\n");
		}
		sb.append("\n");

		sb.append("<rs:attribute-filter>\n" + 
				"    <search-criteria xmlns=\"http://www.ca.com/spectrum/restful/schema/filter\">\n" + 
				"    <filtered-models>\n" + 
				"      <equals>\n" + 
				"        <attribute id=\""+SpectrumAttibute.SEVERITY+"\">\n" + 
				"          <value>1</value>\n" + 
				"        </attribute>\n" + 
				"      </equals>\n" + 
				"      </filtered-models>\n" + 
				"    </search-criteria>\n" + 
				"  </rs:attribute-filter>\n");
		
		sb.append("</rs:alarm-request>");
		sb.append("\n");
		return sb.toString();
	}

	private String getSeverityFilter() {
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<rs:alarm-request ");
		if (throttlesize > 0) {
			sb.append("throttlesize=\"");
			sb.append(throttlesize);
			sb.append("\"");
		}
		sb.append("\n xmlns:rs=\"http://www.ca.com/spectrum/restful/schema/request\"\n xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"  xsi:schemaLocation=\"http://www.ca.com/spectrum/restful/schema/request ../../../xsd/Request.xsd\">\n");
		sb.append("\n");
		

		sb.append("<rs:attribute-filter>\n" + 
				"    <search-criteria xmlns=\"http://www.ca.com/spectrum/restful/schema/filter\">\n" + 
				"    <filtered-models>\n" + 
				"      <equals>\n" + 
				"        <attribute id=\""+SpectrumAttibute.SEVERITY+"\">\n" + 
				"          <value>1</value>\n" + 
				"        </attribute>\n" + 
				"      </equals>\n" + 
				"      </filtered-models>\n" + 
				"    </search-criteria>\n" + 
				"  </rs:attribute-filter>\n");
		
		sb.append("</rs:alarm-request>");
		sb.append("\n");
		return sb.toString();
	}


	public String getConfig() {
		String pc = getSeverityFilter();
		System.out.println(pc);
		return pc;
	}

}
