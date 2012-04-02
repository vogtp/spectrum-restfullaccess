package test;

import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import ch.almana.spectrum.rest.access.AlarmModelAccess;
import ch.almana.spectrum.rest.access.CollectionModelAccess;
import ch.almana.spectrum.rest.model.GenericModel;
import ch.almana.spectrum.rest.model.SpectrumAttibute;
import ch.almana.spectrum.rest.net.HttpClientRequestHandler;

public class NetworkTest extends TestCase {

	private AlarmModelAccess modelAccess;
	private HttpClientRequestHandler requestHandler;
	public void testLoadAlarms() throws Exception {
		requestHandler = new HttpClientRequestHandler(new RequestConfig());
		modelAccess = new AlarmModelAccess(requestHandler);
		
		Set<String> alarmIds = modelAccess.getList();
		long updateTime = modelAccess.getUpdateTime();
		Map<String, GenericModel> newAlarms = modelAccess.getEntities(alarmIds);
	}
	public void testLoadCollections() throws Exception {
		HttpClientRequestHandler requestHandler = new HttpClientRequestHandler(new RequestConfig());
		CollectionModelAccess cma = new CollectionModelAccess(requestHandler);
		Map<String, GenericModel> alarmIds = cma.getEntities(null);
		for (GenericModel s : alarmIds.values()) {
			System.out.println(s.get(SpectrumAttibute.MODEL_NAME)+" "+s.get(SpectrumAttibute.MODEL_HANDLE));
		}
//		long updateTime = cma.getUpdateTime();
//		Map<String, GenericModel> newAlarms = cma.getEntities(alarmIds);
	}

}
