package test;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import ch.almana.spectrum.rest.access.AlarmModelAccess;
import ch.almana.spectrum.rest.model.GenericModel;
import ch.almana.spectrum.rest.net.HttpClientRequestHandler;

public class NetworkTest extends TestCase {

	public void testLoadModels() throws Exception {
		HttpClientRequestHandler requestHandler = new HttpClientRequestHandler(new RequestConfig());
		AlarmModelAccess modelAccess = new AlarmModelAccess(requestHandler);
		//DBProvider.setNotifyChange(false);
		Set<String> alarmIds = modelAccess.getList();
		long updateTime = modelAccess.getUpdateTime();
		//DBProvider.setNotifyChange(true);
		Map<String, GenericModel> newAlarms = modelAccess.getEntities(alarmIds);
	}

}
