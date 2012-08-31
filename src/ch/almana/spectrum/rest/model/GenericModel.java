package ch.almana.spectrum.rest.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GenericModel implements Serializable {

	private static final long serialVersionUID = -1018376056217832189L;
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

	public String get(String key){
		return attributes.get(key);
	}
	
	public Map<String, String> getAttributes() {
		return attributes;
	}

	public Map<String, String> getAttributesNamed() {
		Map<String, String> namedAttriubtes = new HashMap<String, String>(attributes.size());
		for (String key : attributes.keySet()) {
			String name = SpectrumAttibute.attributeNames.get(key);
			name = name == null ? key : name;
			namedAttriubtes.put(name, attributes.get(key));
		}
		return namedAttriubtes;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		for (String key : attributes.keySet()) {
			sb.append(key).append(" -> ").append(attributes.get(key)).append("; ");
		}

		return super.toString();
	}
}
