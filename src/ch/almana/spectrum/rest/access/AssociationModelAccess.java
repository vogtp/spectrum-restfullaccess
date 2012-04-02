package ch.almana.spectrum.rest.access;

import java.util.Set;

import ch.almana.spectrum.rest.net.IRequestHandler;

public class AssociationModelAccess extends BaseModelAccess {

	
	
	private static final String RELATIONID_DYNAMIC_GLOBAL_COLLECTION = "0x1003a";
	private String relationId; 
	private String modelHandle;
	private String side = "left";

	public AssociationModelAccess(IRequestHandler requestHandler) {
		super(requestHandler);
	}

	@Override
	public String getRestNoun() {
		return "associations/relation";
	}

	@Override
	public String getResponseEntityName() {
		return "association";
	}

	@Override
	public Set<String> getAttributesHandles() {
		return null;
	}

	@Override
	public String getFilterPreamble() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getFilterPostamble() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getPostString() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	protected String getResponseIdName() {
		// TODO Auto-generated method stub
		return "@rightmh";
	}

	@Override
	public String getRequestTag() {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getPathParameter() {
		// https://spectrum.urz.unibas.ch/spectrum/restful/associations/
		// relation/0x1003a/model/0x600b83?side=left
		return  relationId + "/model/" + modelHandle + "?side="
				+ side;
	}

	@Override
	public boolean doPostRequest() {
		return false;
	}

	public Set<String> getModelsForCollection(String collectionHandle) throws Exception {
		modelHandle = collectionHandle;
		relationId = RELATIONID_DYNAMIC_GLOBAL_COLLECTION; 
		return getList();
	}
}
