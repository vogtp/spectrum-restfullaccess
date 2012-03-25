package ch.almana.spectrum.rest.net;

public interface IRequestConfig {

	public String getSpectroServerProtocoll();

	public String getSpectroServerName();

	public String getSpectroServerUrlPath();

	public String getUsername();

	public String getPassword();

	public int getThrottlesize();

}
