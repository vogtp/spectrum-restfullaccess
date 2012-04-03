package test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;
import ch.almana.spectrum.rest.access.AlarmModelAccess;
import ch.almana.spectrum.rest.access.AssociationModelAccess;
import ch.almana.spectrum.rest.access.CollectionModelAccess;
import ch.almana.spectrum.rest.model.GenericModel;
import ch.almana.spectrum.rest.model.SpectrumAttibute;
import ch.almana.spectrum.rest.net.HttpClientRequestHandler;

public class NetworkTest extends TestCase {

	private HttpClientRequestHandler requestHandler;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		requestHandler = new HttpClientRequestHandler(new RequestConfig());
		
	}
	
	public void testLoadAlarms() throws Exception {
		AlarmModelAccess modelAccess = new AlarmModelAccess(requestHandler);
		
		Set<String> alarmIds = modelAccess.getList();
		Map<String, GenericModel> newAlarms = modelAccess.getEntities(alarmIds);
		assertTrue(newAlarms.size() > 0);
	}
	public void testLoadCollections() throws Exception {
		CollectionModelAccess cma = new CollectionModelAccess(requestHandler);
		Map<String, GenericModel> collections = cma.getEntities(null);
		for (GenericModel s : collections.values()) {
			System.out.println(s.get(SpectrumAttibute.MODEL_NAME)+" "+s.get(SpectrumAttibute.MODEL_HANDLE));
		}
		assertTrue(collections.size() > 0);
//		long updateTime = cma.getUpdateTime();
//		Map<String, GenericModel> newAlarms = cma.getEntities(alarmIds);
	}
	
	public void testAlarmsByModelhandle() throws Exception {

		AlarmModelAccess modelAccess = new AlarmModelAccess(requestHandler);
		Set<String> mhs = new HashSet<String>();
		mhs.add("0x600b83"); // collection
		mhs.add("0x600e36"); // host
		Map<String, GenericModel> alarmIds = modelAccess.getAlarmsIdByModelHandle(mhs);
		assertTrue(alarmIds.size() > 0);
	}

	public void testGetAssociationForCollection() throws Exception {
		AssociationModelAccess ama = new AssociationModelAccess(requestHandler);
		Set<String> modelsForCollection = ama.getModelsForCollection("0x600b83");
		assertTrue(modelsForCollection.size() > 0);
	}
	
}
