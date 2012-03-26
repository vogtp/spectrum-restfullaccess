package ch.almana.spectrum.rest.access;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.almana.spectrum.rest.log.Logger;
import ch.almana.spectrum.rest.model.GenericModel;
import ch.almana.spectrum.rest.model.SpectrumAttibute;
import ch.almana.spectrum.rest.net.IRequestHandler;
import ch.almana.spectrum.rest.net.PostConfig;

public abstract class BaseModelAccess {

	private static final String JSON_KEY_MORE_DATA_LINK = "ns1.link";

	private static final String JSON_KEY_THROTTLE = "@throttle";

	protected long throttle = -1;

	protected long numberOfItems = -1;

	protected IRequestHandler requestHandler;

	protected String nextDataUrl;

	protected String jsonPayload;

	private long updateTime;

	protected boolean listMode;

	private Set<String> entityIds;

	public abstract String getRestNoun();

	public abstract Set<String> getAttributesHandles();

	protected abstract Map<String, GenericModel> processData(String jsonPayload) throws Exception;

	public BaseModelAccess(IRequestHandler requestHandler) {
		super();
		this.requestHandler = requestHandler;
	}

	public Set<String> getList() throws Exception {
		try {
			listMode = true;
			setEntitiesIds(null);
			jsonPayload = requestHandler.getPayload(this);
			updateTime = System.currentTimeMillis();
			Logger.i("Processing model data");
			return processData(jsonPayload).keySet();
		} finally {
			Logger.i("Finished processing model data");
		}
	}

	public Map<String, GenericModel> getEntities(Set<String> ids) throws Exception {
		try {
			listMode = false;
			setEntitiesIds(ids);
			jsonPayload = requestHandler.getPayload(this);
			updateTime = System.currentTimeMillis();
			Logger.i("Processing model data");
			return processData(jsonPayload);
		} finally {
			Logger.i("Finished processing model data");
		}
	}

	public Set<String> getEntityIds() {
		return entityIds;
	}

	private void setEntitiesIds(Set<String> ids) {
		entityIds = ids;
	}

	public void parseGernericInformation(JSONObject root) {
		try {
			throttle = Long.parseLong(root.getString(JSON_KEY_THROTTLE));
		} catch (Exception e) {
			Logger.e("Cannot get throttle from response", e);
			throttle = -1;
		}
		// "ns1.link":
		// {"@rel":"next","@href":"https:\/\/spectrum.urz.unibas.ch\/spectrum\/restful\/alarms?id=e4b75c2f-fdca-47f9-b6f8-fea892b3b8be&start=40&throttlesize=40","@type":"application\/xml"}
		if (root.has(JSON_KEY_MORE_DATA_LINK)) {
			try {
				JSONObject linkInfo = root.getJSONObject(JSON_KEY_MORE_DATA_LINK);
				if (linkInfo.getString("@rel").equals("next")) {
					nextDataUrl = linkInfo.getString("@href");
				} else {
					nextDataUrl = null;
				}
			} catch (JSONException e) {
				Logger.e("Cannot get next link", e);
				nextDataUrl = null;
			}
		}

	}

	public Map<String, String> parseAttributes(JSONObject json) throws JSONException {
		// "ns1.attribute":[{"@id":"0x11f53","$":"0x61e9f1"},{"@id":"0x10000","$":"HubCat29xx"},{"@id":"0x11f56","$":"2"},{"@id":"0x12b4c","$":"HIGH MEMORY UTILIZATION"}]}
		JSONArray attrsJson = json.getJSONArray("ns1.attribute");
		Map<String, String> attrs = new HashMap<String, String>(attrsJson.length());
		for (int i = 0; i < attrsJson.length(); i++) {
			JSONObject attr = attrsJson.getJSONObject(i);
			try {
				attrs.put(attr.getString("@id"), attr.getString("$"));
			} catch (JSONException e) {
				Logger.i("Attribute not found");
			}
		}
		return attrs;
	}

	@Override
	public String toString() {
		return jsonPayload;
	}

	public long getUpdateTime() {
		return updateTime;
	}

	public PostConfig getPostConfig() {
		PostConfig postConfig = new PostConfig();
		if (listMode) {
			postConfig.addFilter(SpectrumAttibute.SEVERITY, "3");
		} else {
			postConfig.setAttributes(getAttributesHandles());
			postConfig.setEntityIds(entityIds);
		}
		return postConfig;
	}

}
