package ch.almana.spectrum.rest.access;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.almana.spectrum.rest.log.Logger;
import ch.almana.spectrum.rest.model.GenericModel;
import ch.almana.spectrum.rest.model.SpectrumAttibute;
import ch.almana.spectrum.rest.net.IRequestHandler;
import ch.almana.spectrum.rest.net.PostConfig;

public class CollectionModelAccess extends BaseModelAccess {

	public String payload;

	public CollectionModelAccess(IRequestHandler requestHandler) {
		super(requestHandler);
	}

	@Override
	public String getRestNoun() {
		return "models";
	}
	@Override
	public String getResponseEntityName() {
		return "model";
	}

	@Override
	public Set<String> getAttributesHandles() {
		Set<String> attrs = new TreeSet<String>();
		attrs.add(SpectrumAttibute.MODEL_NAME);
		attrs.add(SpectrumAttibute.MODEL_HANDLE);
		if (listMode) {
			return attrs;
		}
		return attrs;
	}


	@Override
	public String getRequestTag() {
		return "model-request";
	}

	public String getGenericFilter() {
		return " <rs:target-models>\n" + 
				"      <rs:models-search>\n" + 
				"        <rs:search-criteria \n" + 
				"             xmlns=\"http://www.ca.com/spectrum/restful/schema/filter\">\n" + 
				"          <filtered-models>\n" + 
				"            <is-derived-from>\n" + 
				"              <attribute id=\"AttributeID.MTYPE_HANDLE\">\n" + 
				"                <value>0x10474</value> <!-- Collecion -->\n" + 
				"              </attribute>\n" + 
				"            </is-derived-from>\n" + 
				"          </filtered-models>\n" + 
				"        </rs:search-criteria>\n" + 
				"      </rs:models-search>\n" + 
				"    </rs:target-models>";
	}

	@Override
	public String getFilterPreamble() {
		return "<rs:target-models>\n<rs:models-search>";
	}

	@Override
	public String getFilterPostamble() {
		return "</rs:target-models>\n</rs:models-search>";
	}

	@Override
	public String getPostString() {
		return new PostConfig(this).getConfig();
	}

	@Override
	protected String getResponseIdName() {
		return "@mh";
	}

}
