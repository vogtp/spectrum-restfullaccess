package ch.almana.spectrum.rest.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import ch.almana.spectrum.rest.access.BaseModelAccess;
import ch.almana.spectrum.rest.log.Logger;

public class HttpClientRequestHandler implements IRequestHandler {

	private static final String PATH_SEP = "/";
	private final IRequestConfig settings;

	public HttpClientRequestHandler(IRequestConfig settings) {
		super();
		this.settings = settings;
	}

	public String getPayload(BaseModelAccess modelAccess) throws IOException {
		StringBuilder path = new StringBuilder();
		path.append(PATH_SEP).append(settings.getSpectroServerUrlPath()).append(PATH_SEP);
		path.append("restful/");
		path.append(modelAccess.getRestNoun());
		URL url = new URL(settings.getSpectroServerProtocoll(), settings.getSpectroServerName(), path.toString());
		return httpClientPost(url.toString(), modelAccess);
	}

	protected HttpURLConnection doGetRequest(BaseModelAccess modelAccess, StringBuilder path) throws MalformedURLException, IOException, ProtocolException {
		int throttlesize = settings.getThrottlesize();
		String attrSep = "?";
		if (throttlesize > 0) {
			path.append(attrSep);
			path.append("throttlesize=").append(throttlesize);
			attrSep = "&";
		}
		for (String attr : modelAccess.getAttributesHandles()) {
			path.append(attrSep).append("attr=").append(attr);
			attrSep = "&";
		}

		URL url = new URL(settings.getSpectroServerProtocoll(), settings.getSpectroServerName(), path.toString());
		Logger.i("Loading " + url.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		return conn;
	}

	protected HttpURLConnection doPostRequest(BaseModelAccess modelAccess, StringBuilder path) throws MalformedURLException, IOException,
			ProtocolException {

		URL url = new URL(settings.getSpectroServerProtocoll(), settings.getSpectroServerName(), path.toString());
		Logger.i("Loading " + url.toString());
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// conn.setRequestMethod("GET");
		conn.setRequestMethod("POST");
		conn.setDoOutput(true);
		conn.setRequestProperty("Accept", "application/json");

		BufferedWriter postWriter = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
		postWriter.write(getPostConfig(modelAccess));
		//		postWriter.write("");
		return conn;
	}

	private String getPostConfig(BaseModelAccess modelAccess) {
		StringBuilder sb = new StringBuilder();
		sb.append("<rs:alarm-request ");
		int throttlesize = settings.getThrottlesize();
		if (throttlesize > 0) {
			sb.append("throttlesize=\"");
			sb.append(throttlesize);
			sb.append("\"");
		}
		sb.append(" xmlns:rs=\"http://www.ca.com/spectrum/restful/schema/request\"\n" +
				"  xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
				"  xsi:schemaLocation=\"http://www.ca.com/spectrum/restful/schema/request ../../../xsd/Request.xsd \">");
		sb.append("\n");
		for (String attr : modelAccess.getAttributesHandles()) {
			sb.append("<rs:requested-attribute id=\"");
			sb.append(attr);
			sb.append("\" />");
			sb.append("\n");
		}

		sb.append("</rs:alarm-request>");
		sb.append("\n");
		return sb.toString();
	}

	protected String httpClientPost(String url, BaseModelAccess modelAccess) {
		final UsernamePasswordCredentials creds = new UsernamePasswordCredentials(settings.getUsername(), settings.getPassword());

		HttpPost post = new HttpPost(url);
		post.setHeader("Accept", "application/json");
		//		get.setHeader("Content-type", "application/json");

		try {
			String postConfig = getPostConfig(modelAccess);
			HttpEntity entity = new StringEntity(postConfig);
			post.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			Logger.e("Cannot set post data", e);
		}

		HttpResponse resp = null;
		BufferedReader in = null;
		try {
			DefaultHttpClient httpClient = new DefaultHttpClient();
			// post 8 : httpClient = AndroidHttpClient.newInstance("onetouch", context);

			AuthScope authscope = AuthScope.ANY;
			httpClient.getCredentialsProvider().setCredentials(authscope, creds);

			resp = httpClient.execute(post);

			if (resp != null) {
				in = new BufferedReader(new InputStreamReader(resp.getEntity().getContent()));
				StringBuffer sb = new StringBuffer("");
				String line = "";
				String NL = System.getProperty("line.separator");
				while ((line = in.readLine()) != null) {
					sb.append(line + NL);
				}
				in.close();
				String page = sb.toString();
				System.out.println(page);
				return sb.toString();
			} else {
				return "no data";
			}
		} catch (ClientProtocolException e) {
			Logger.e("HTTP protocol error", e);
			return e.getMessage();
		} catch (IOException e) {
			Logger.e("Communication error", e);
			return e.getMessage();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
