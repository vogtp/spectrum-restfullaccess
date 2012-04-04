package ch.almana.spectrum.rest.access;

import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import ch.almana.spectrum.rest.log.Logger;
import ch.almana.spectrum.rest.model.GenericModel;
import ch.almana.spectrum.rest.model.SpectrumAttibute;
import ch.almana.spectrum.rest.net.IRequestHandler;
import ch.almana.spectrum.rest.net.PostConfig;

public class AlarmModelAccess extends BaseModelAccess {

	public AlarmModelAccess(IRequestHandler requestHandler) {
		super(requestHandler);
	}

	@Override
	public String getRestNoun() {
		return "alarms";
	}
	@Override
	public String getResponseEntityName() {
		return "alarm";
	}

	@Override
	public Set<String> getAttributesHandles() {
		Set<String> attrs = new TreeSet<String>();
		if (listMode) {
			return attrs;
		}
		attrs.add(SpectrumAttibute.ALARM_ID);
		attrs.add(SpectrumAttibute.SEVERITY);
		attrs.add(SpectrumAttibute.OCCURENCES);
		attrs.add(SpectrumAttibute.ACKNOWLEDGED);
		attrs.add(SpectrumAttibute.ALARM_TITLE);
		attrs.add(SpectrumAttibute.CREATION_DATE);
		attrs.add(SpectrumAttibute.MODEL_NAME);
		attrs.add(SpectrumAttibute.NETWORK_ADDRESS);
		return attrs;
	}


	public static String severityToString(int severity) {
		switch (severity) {
		case 3:
			return "critical";
		case 2:
			return "major";
		case 1:
			return "minor";
		default:
			return "undefined (" + severity + ")";
		}

	}

	@Override
	public String getRequestTag() {
		return "alarm-request";
	}

	@Override
	public String getFilterPreamble() {
		return "<rs:attribute-filter>\n";
	}

	@Override
	public String getFilterPostamble() {
		return "</rs:attribute-filter>\n";
	}

	@Override
	public String getPostString() {
		 return new PostConfig(this).getConfig();
	}

	@Override
	protected String getResponseIdName() {
		return "@id";
	}

	public Map<String, GenericModel> getAlarmsIdByModelHandle(Set<String> mhs) throws Exception {
		try {
			listMode = false;
			setModelHandles(mhs);
			jsonPayload = requestHandler.getPayload(this);
			Logger.i("Processing model data");
			return processData(jsonPayload);
		} finally {
			Logger.i("Finished processing model data");
		}
	}


}
