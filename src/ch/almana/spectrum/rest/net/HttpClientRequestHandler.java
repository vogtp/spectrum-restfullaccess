package ch.almana.spectrum.rest.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import ch.almana.spectrum.rest.access.BaseModelAccess;
import ch.almana.spectrum.rest.log.Logger;

public class HttpClientRequestHandler implements IRequestHandler {

	private static final String PATH_SEP = "/";
	private final IRequestConfig settings;
	private final DefaultHttpClient httpClient;

	public HttpClientRequestHandler(IRequestConfig settings) {
		super();
		this.settings = settings;
		final UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
				settings.getUsername(), settings.getPassword());

		httpClient = new DefaultHttpClient();

		// post 8 : httpClient = AndroidHttpClient.newInstance("onetouch",
		// context);

		AuthScope authscope = AuthScope.ANY;
		httpClient.getCredentialsProvider().setCredentials(authscope, creds);
	}

	@Override
	public String getPayload(BaseModelAccess modelAccess) throws IOException {
		StringBuilder path = new StringBuilder();
		path.append(PATH_SEP).append(settings.getSpectroServerUrlPath())
				.append(PATH_SEP);
		path.append("restful/");
		path.append(modelAccess.getRestNoun());
		String pathParameter = modelAccess.getPathParameter();
		if (pathParameter != null) {
			path.append("/").append(pathParameter);
		}
		URL url;
		int port = settings.getServerPort();
		if (port > 0) {
			url = new URL(settings.getSpectroServerProtocoll(),
					settings.getSpectroServerName(), port, path.toString());
		} else {
			url = new URL(settings.getSpectroServerProtocoll(),
					settings.getSpectroServerName(), path.toString());
		}
		return httpClientPost(url.toString(), modelAccess);
	}

	protected String httpClientPost(String url, BaseModelAccess modelAccess)
			throws IOException {
		Logger.i("Reading from URL:"+url);
		HttpUriRequest httpUriRequest;

		if (modelAccess.doPostRequest()) {
			httpUriRequest = new HttpPost(url);
			try {
				String postString = modelAccess.getPostString();
				Logger.d("Post data: >" + postString + "<");
				HttpEntity entity = new StringEntity(postString);
				((HttpPost) httpUriRequest).setEntity(entity);
			} catch (UnsupportedEncodingException e) {
				Logger.e("Cannot set post data due to wrong encoding", e);
			}
		} else {
			httpUriRequest = new HttpGet(url);
		}

		httpUriRequest.setHeader("Content-Type", "application/xml");
		httpUriRequest.setHeader("Accept", "application/json");
		HttpResponse resp = null;
		BufferedReader in = null;
		try {

			resp = httpClient.execute(httpUriRequest);

			if (resp == null) {
				throw new IOException("Got null repsonse from spectrum");
			}
			StatusLine statusLine = resp.getStatusLine();
			if (statusLine.getStatusCode() > 299) {
				String string = "Spectrum status response: "
						+ statusLine.toString();
				Logger.w(string);
				throw new IOException(string);
			}

			in = new BufferedReader(new InputStreamReader(resp.getEntity()
					.getContent()));
			StringBuffer sb = new StringBuffer("");
			String line = "";
			String NL = System.getProperty("line.separator");
			while ((line = in.readLine()) != null) {
				sb.append(line + NL);
				Logger.v(line);
			}
			in.close();
			String page = sb.toString();
			System.out.println(page);
			return sb.toString();
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}
}
