package ch.almana.spectrum.rest.access;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.json.JSONArray;
import org.json.JSONObject;

import ch.almana.spectrum.rest.log.Logger;
import ch.almana.spectrum.rest.model.GenericModel;
import ch.almana.spectrum.rest.model.SpectrumAttibute;
import ch.almana.spectrum.rest.net.IRequestHandler;

public class AlarmModelAccess extends BaseModelAccess {


	public AlarmModelAccess(IRequestHandler requestHandler) {
		super(requestHandler);
	}

	@Override
	public String getRestNoun() {
		return "alarms";
	}

	@Override
	public Set<String> getAttributesHandles() {
		Set<String> attrs = new TreeSet<String>();
		attrs.add(SpectrumAttibute.ALARM_ID);
		if (listMode) {
			return attrs;
		}

		//		attrs.add("0x11f53");
		//		attrs.add("0x10000");
		//		attrs.add("0x11f56");
		//		attrs.add("0x11f4d");
		attrs.add(SpectrumAttibute.SEVERITY);
		attrs.add(SpectrumAttibute.OCCURENCES);
		attrs.add(SpectrumAttibute.ACKNOWLEDGED);
		attrs.add(SpectrumAttibute.ALARM_TITLE);
		attrs.add(SpectrumAttibute.CREATION_DATE);
		attrs.add(SpectrumAttibute.MODEL_NAME);
		attrs.add(SpectrumAttibute.NETWORK_ADDRESS);
		//				attrs.add(SpectrumAttibute.);
		return attrs;
	}

	@Override
	protected Map<String, GenericModel> processData(String payload) throws Exception {
		Map<String, GenericModel> ret = new HashMap<String, GenericModel>();
		try {
			JSONObject all = new JSONObject(payload);
			JSONObject root = all.getJSONObject("ns1.alarm-response-list");
			parseGernericInformation(root);
			numberOfItems = Long.parseLong(root.getString("@total-alarms"));
			JSONArray alarms = root.getJSONObject("ns1.alarm-responses").getJSONArray("ns1.alarm");
			int totAlarm = alarms.length();
			for (int i = 0; i < totAlarm; i++) {
				if (listMode) {
					Logger.i("Processing alarmid " + i + "/" + totAlarm);
				} else {
					Logger.i("Processing alarm " + i + "/" + totAlarm);
				}
				JSONObject alarm = alarms.getJSONObject(i);
				GenericModel model = null;
				String id = alarm.getString("@id");
				if (!listMode) {
					model = new GenericModel(id, parseAttributes(alarm));
				}
				ret.put(id, model);
			}
		} catch (Exception e) {
			numberOfItems = -1;
			throw new Exception(e);
		}
		return ret;
	}


}
