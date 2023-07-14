package com.globecapital.business.report;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.msf.connections.http.HTTPConnection;
import com.msf.log.Logger;

public class GCHttpConnection {

	/**
	 * 
	 */
	private URI URI;
	private URL URL;
	private Integer connectionTimeout = 240000; // ms
	private Integer readTimeout = 240000;
	private Map<String, String> requestHeaders;
	private Proxy proxy = null;
	private Map<String, List<String>> responseheaders;
	private int returnCode;

	private static Logger log = Logger.getLogger(HTTPConnection.class);

	protected URI getUri() {
		return URI;
	}

	protected Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	protected Proxy getProxy() {
		return this.proxy;
	}

	protected Integer getConnectionTimeout() {
		return connectionTimeout;
	}

	protected Integer getReadTimeout() {
		return readTimeout;
	}

	public GCHttpConnection(String url) throws URISyntaxException {
		log.debug("URL received : " + url);
		this.URI = new URI(url);
	}

	public GCHttpConnection(URL url) throws URISyntaxException {
		log.debug("URL received : " + url);
		this.URL = url;
	}

	public void setConnectionTimeout(Integer timeout) {
		this.connectionTimeout = timeout;
	}

	public void setReadTimeout(Integer timeout) {
		this.readTimeout = timeout;
	}

	public void setHeaders(final Map<String, String> headers) {
		this.requestHeaders = headers;
	}

	public void setProxy(String proxyHost, Integer proxyPort) {
		this.proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxyHost, proxyPort));
	}

	protected HttpURLConnection createConnection(String method)
			throws MalformedURLException, IOException, SocketTimeoutException {

		HttpURLConnection connection = null;

		log.debug("Proxy null flag is " + (this.proxy == null));

		if (this.proxy != null)
			connection = (HttpURLConnection) getURL().openConnection(this.proxy);
		else
			connection = (HttpURLConnection) getURL().openConnection();

		connection.setRequestMethod(method);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setUseCaches(false);
		connection.setConnectTimeout(this.connectionTimeout);
		connection.setReadTimeout(this.readTimeout);
		connection.setDoOutput(true);

		if (requestHeaders != null) {
			log.debug("Setting request headers of size " + requestHeaders.size());
			Iterator<Entry<String, String>> it = requestHeaders.entrySet().iterator();
			while (it.hasNext()) {
				Entry<String, String> pairs = it.next();
				connection.setRequestProperty(pairs.getKey(), pairs.getValue());
			}
		}

		return connection;
	}

	public URL getURL() throws MalformedURLException {
		if (this.URL == null) {
			this.URL = this.URI.toURL();
		}

		return this.URL;
	}

	public byte[] readResponse(HttpURLConnection connection) throws IOException {

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StringBuilder stringBuilder = new StringBuilder();
		if (connection.getResponseCode() >= 200 && connection.getResponseCode() <= 400) {

			this.returnCode = connection.getResponseCode();
			log.debug("HTTP Return code : " + this.returnCode);

			if (!connection.getHeaderFields().isEmpty()) {

				responseheaders = (connection.getHeaderFields());

			}

			InputStream in = connection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			byte[] buffer = new byte[10000000];

			int readLength = 0;
			String s = "";

			while ((readLength = in.read(buffer, 0, buffer.length)) != -1) {
				out.write(buffer, 0, readLength);
			}
			while ((s = br.readLine()) != null) {
				log.debug("br.readLine()" + br.readLine());
				stringBuilder.append(s + "\n");
			}

			connection.disconnect();
			out.close();

		}

		return out.toByteArray();
	}

	public HttpURLConnection get() throws MalformedURLException, IOException, SocketTimeoutException {

		HttpURLConnection connection = createConnection("GET");
		return connection;
	}


	/**
	 * Get HTTP response code
	 * 
	 * @return
	 */
	public int getReturnCode() {
		return returnCode;
	}

	/**
	 * Get HTTP response headers
	 * 
	 * @return
	 */
	public Map<String, List<String>> getResponseHeaders() {

		return responseheaders;

	}

}