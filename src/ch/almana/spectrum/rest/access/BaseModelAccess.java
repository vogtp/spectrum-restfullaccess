package ch.almana.spectrum.rest.access;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.almana.spectrum.rest.log.Logger;
import ch.almana.spectrum.rest.model.GenericModel;
import ch.almana.spectrum.rest.net.IRequestHandler;

public abstract class BaseModelAccess {

	private static final String JSON_KEY_MORE_DATA_LINK = "ns1.link";

	private static final String JSON_KEY_THROTTLE = "@throttle";

	protected long throttle = -1;

//	protected long numberOfItems = -1;

	protected IRequestHandler requestHandler;

	protected String nextDataUrl;

	protected String jsonPayload;

	private long updateTime;

	protected boolean listMode;

	private Set<String> entityIds;
	private Set<String> modelHandles;

	public abstract String getRestNoun();
	
	public abstract String getResponseEntityName();

	public abstract Set<String> getAttributesHandles();

	public abstract String getFilterPreamble();

	public abstract String getFilterPostamble();

	public abstract String getPostString();

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

	public Map<String, GenericModel> getEntities(Set<String> ids)
			throws Exception {
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
			String throttleString = root.optString(JSON_KEY_THROTTLE);
			if (throttleString != null && !"".equals(throttleString.trim()))
			throttle = Long.parseLong(throttleString);
		} catch (Exception e) {
			Logger.i("Cannot get throttle from response", e);
			throttle = -1;
		}
		// "ns1.link":
		// {"@rel":"next","@href":"https:\/\/spectrum.urz.unibas.ch\/spectrum\/restful\/alarms?id=e4b75c2f-fdca-47f9-b6f8-fea892b3b8be&start=40&throttlesize=40","@type":"application\/xml"}
		if (root.has(JSON_KEY_MORE_DATA_LINK)) {
			try {
				JSONObject linkInfo = root
						.getJSONObject(JSON_KEY_MORE_DATA_LINK);
				if (linkInfo.optString("@rel").equals("next")) {
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
	

	protected Map<String, GenericModel> processData(String jsonPayload)
			throws Exception {
		Map<String, GenericModel> ret = new HashMap<String, GenericModel>();
		/*
	{"ns1.model-response-list":
	{"@error":"EndOfResults","@throttle":"21","@total-models":"21",
	"ns1.model-responses":{"ns1.model":[{"@mh":"0x60bc98","ns1.attribute":[{"@id":"0x1006e","$":"unibil-lo-0.physik.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x609980","ns1.attribute":[{"@id":"0x1006e","$":"unibim-lo-0.ub.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x617ba9","ns1.attribute":[{"@id":"0x1006e","$":"unirr2-lo-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x609179","ns1.attribute":[{"@id":"0x1006e","$":"uniba3-fa-0-0.mikrobio.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x6163ba","ns1.attribute":[{"@id":"0x1006e","$":"uniban-tu-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x60a332","ns1.attribute":[{"@id":"0x1006e","$":"unibad-lo-0.borromaeum.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x6124f3","ns1.attribute":[{"@id":"0x1006e","$":"uni-dialin-lo-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x602783","ns1.attribute":[{"@id":"0x1006e","$":"unibiz-fa-0-1.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x620814","ns1.attribute":[{"@id":"0x1006e","$":"unibsl-tu-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x60b5cc","ns1.attribute":[{"@id":"0x1006e","$":"unibav-tu-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x60282c","ns1.attribute":[{"@id":"0x1006e","$":"unibiy-fa-0-1.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x6175f5","ns1.attribute":[{"@id":"0x1006e","$":"unibau-tu-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x60b595","ns1.attribute":[{"@id":"0x1006e","$":"unibid-lo-0.dfzlf.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x60abdd","ns1.attribute":[{"@id":"0x1006e","$":"unibik-tu-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x617a96","ns1.attribute":[{"@id":"0x1006e","$":"unirr1-lo-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x61628e","ns1.attribute":[{"@id":"0x1006e","$":"unibsp-lo-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x609d0e","ns1.attribute":[{"@id":"0x1006e","$":"unibi8-lo-0.zuv.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x616cc7","ns1.attribute":[{"@id":"0x1006e","$":"unibax-lo-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x6028cf","ns1.attribute":[{"@id":"0x1006e","$":"unibic-lo-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x6027af","ns1.attribute":[{"@id":"0x1006e","$":"unibih-lo-0.sti.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}},{"@mh":"0x6003af","ns1.attribute":[{"@id":"0x1006e","$":"unibix-lo-0.urz.p.unibas.ch"},{"@id":"0x10000","$":"Rtr_Cisco"},{"@id":"0x10032","$":"Cisco Systems, Inc."}],"ns1.attribute-list":{"@id":"0x12de2","ns1.instance":[{"@value":"(?<!Hardware)(?<!H\\\/W)(?=\\s+Version\\s*[=:]?\\s*(?:\\w*OS\\s+)?(\\d[^\\s;,\\n\\r]+))","@oid":"1"},{"@value":"\\s+F\\\/?W\\s*(?:Rev)?:?(?:[^\\d]\\w*\\s)\\s*(\\d[^\\s;,\\n\\r]+)","@oid":"2"},{"@value":"(?<!h\\\/w)s\\\/?w\\_?\\s*Rev(?:ision)?:?\\s*([^\\s;,\\n\\r]+)","@oid":"3"},{"@value":"(?<!hw\\_)(?=\\s+\\(?Rev\\.?(?:ision)?:?\\s*([^\\s;,\\n\\r\\)]+))","@oid":"4"},{"@value":"\\s+\\(?(?:(?:SW)|(?:Software))\\_?(?:Version)?\\s*(?:Rev)?(?:[:]|=?(?!itch))\\s*([^\\s;,\\n\\r]+)","@oid":"5"},{"@value":"\\s+Rel(?:ease)?[:.]?\\s*\\=?\\s*([^\\s;,\\n\\r:]+)","@oid":"6"},{"@value":"(?:s\\\/w)?\\sVer(?:sion)?[\\:.]?(?:\\s+[^\\d]\\w*)?\\s+([^\\s;,\\n\\r>]+)","@oid":"7"},{"@value":"\\s+kernel\\s*(?:\\w*OS\\s*)?([^\\s;,\\n\\r>]+)","@oid":"8"},{"@value":"\\s+BOOTR\\s*\\:?\\s*([^\\s;,\\n\\r>]+)","@oid":"9"},{"@value":"(?<!Vendor)(?=\\s+v\\s*)([^\\s;,\\n\\r>]+)","@oid":"10"}]}}]}}}
		 */

		try {
			String entityName = getResponseEntityName();
			String idName = getResponseIdName();
			JSONObject all = new JSONObject(jsonPayload);
			JSONObject root = all.getJSONObject("ns1."+entityName+"-response-list");
			parseGernericInformation(root);
//			numberOfItems = Long.parseLong(root.getString("@total-"+entityName+"s"));
			JSONObject repsonses = root.getJSONObject("ns1."+entityName+"-responses");
			JSONArray entitiess;
			try {
				entitiess = repsonses.getJSONArray("ns1."+entityName+"");
			} catch (JSONException e) {
				entitiess = repsonses.getJSONObject("ns1."+entityName+"").getJSONArray("ns1.attribute");
			}
			int totAlarm = entitiess.length();
			for (int i = 0; i < totAlarm; i++) {
				if (listMode) {
					Logger.i("Processing "+entityName+"id " + i + "/" + totAlarm);
				} else {
					Logger.i("Processing "+entityName+" " + i + "/" + totAlarm);
				}
				JSONObject entity = entitiess.getJSONObject(i);
				GenericModel model = null;
				String id = entity.getString(idName);
				if (!listMode) {
					model = new GenericModel(id, parseAttributes(entity));
				}
				ret.put(id, model);
			}
		} catch (Exception e) {
//			numberOfItems = -1;
			e.printStackTrace();
			throw new Exception(e);
		}
		
		return ret;
	}

	protected abstract String getResponseIdName();

	public Map<String, String> parseAttributes(JSONObject json)
			throws JSONException {
		// "ns1.attribute":[{"@id":"0x11f53","$":"0x61e9f1"},{"@id":"0x10000","$":"HubCat29xx"},{"@id":"0x11f56","$":"2"},{"@id":"0x12b4c","$":"HIGH MEMORY UTILIZATION"}]}
		JSONArray attrsJsonArray = json.optJSONArray("ns1.attribute");
		if (attrsJsonArray != null){
			return parseAttributesFromArray(attrsJsonArray);
		}
		JSONObject attrsJsonObject = json.getJSONObject("ns1.attribute");
		return parseAttributesFromObject(attrsJsonObject);
		
	}

	private Map<String, String> parseAttributesFromObject(JSONObject attrsJsonObject) throws JSONException {
		Map<String, String> attrs = new HashMap<String, String>(1);
		attrs.put(attrsJsonObject.getString("@id"), attrsJsonObject.getString("$"));
		return attrs;
	}

	private Map<String, String> parseAttributesFromArray(JSONArray attrsJson) throws JSONException {
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

	public abstract String getRequestTag();

	public String getGenericFilter() {
		return "";
	}

	public boolean isListMode() {
		return listMode;
	}

	public Set<String> getModelHandles() {
		return modelHandles;
	}

	public void setModelHandles(Set<String> modelHandles) {
		this.modelHandles = modelHandles;
	}

	public String getPathParameter() {
		return null;
	}

	public boolean doPostRequest() {
		return true;
	}

}
