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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import ch.almana.spectrum.rest.access.BaseModelAccess;
import ch.almana.spectrum.rest.log.Logger;
import ch.almana.spectrum.rest.model.SpectrumAttibute;

public class HttpClientRequestHandler implements IRequestHandler {

	private static final String PATH_SEP = "/";
	private final IRequestConfig settings;
	private final DefaultHttpClient httpClient;

	public HttpClientRequestHandler(IRequestConfig settings) {
		super();
		this.settings = settings;
		final UsernamePasswordCredentials creds = new UsernamePasswordCredentials(settings.getUsername(), settings.getPassword());

		httpClient = new DefaultHttpClient();

		// post 8 : httpClient = AndroidHttpClient.newInstance("onetouch", context);

		AuthScope authscope = AuthScope.ANY;
		httpClient.getCredentialsProvider().setCredentials(authscope, creds);
	}

	public String getPayload(BaseModelAccess modelAccess) throws IOException {
		StringBuilder path = new StringBuilder();
		path.append(PATH_SEP).append(settings.getSpectroServerUrlPath()).append(PATH_SEP);
		path.append("restful/");
		path.append(modelAccess.getRestNoun());
		URL url = new URL(settings.getSpectroServerProtocoll(), settings.getSpectroServerName(), path.toString());
		return httpClientPost(url.toString(), modelAccess.getPostConfig());
	}

	protected String httpClientPost(String url, PostConfig postConfig) throws IOException {
		postConfig.setThrottlesize(settings.getThrottlesize());
		HttpPost post = new HttpPost(url);
		post.setHeader("Content-Type", "application/xml");
		post.setHeader("Accept", "application/json");

		try {
			HttpEntity entity = new StringEntity(postConfig.getConfig());
			post.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			Logger.e("Cannot set post data due to wrong encoding", e);
		}

		HttpResponse resp = null;
		BufferedReader in = null;
		try {

			resp = httpClient.execute(post);

			if (resp == null) {
				throw new IOException("Got null repsonse from spectrum");
			}
			StatusLine statusLine = resp.getStatusLine();
			if (statusLine.getStatusCode() > 299) {
				String string = "Spectrum status response: " + statusLine.toString();
				Logger.w(string);
				throw new IOException(string);
			}

			in = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
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
