package ch.almana.spectrum.rest.net;

import java.io.IOException;

import ch.almana.spectrum.rest.access.BaseModelAccess;

public interface IRequestHandler {

	String getPayload(BaseModelAccess baseModelAccess) throws IOException;

}
