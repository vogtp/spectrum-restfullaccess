package ch.almana.spectrum.rest.model;

import java.util.Map;

public class GenericModel {

	private final String id;
	private final Map<String, String> attributes;

	public GenericModel(String id, Map<String, String> attributes) {
		super();
		this.id = id;
		this.attributes = attributes;
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}
}
