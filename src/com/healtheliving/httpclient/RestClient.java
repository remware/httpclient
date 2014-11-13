package com.healtheliving.httpclient;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreProtocolPNames;



public class RestClient {

	public enum Method
	{
		GET,
		POST,
		PUT,
		DELETE
	}

	public static final String CONTENT_TYPE_TEXT_PLAIN = "text/plain";
	public static final String CONTENT_TYPE_APPLICATION_JSON = "application/json";
	private static final String CONTENT_TYPE = "Content-type";

	/**
	 *  A helper utility class used to get the string representation of an HttpResponse
	 *
	 *  @param response An HttpResponse
	 *  @return a string representation of the HttpResponse
	 */
	public static String convertStreamToString(HttpResponse response)
	{
		StringBuilder sb = new StringBuilder();;
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			InputStream is = null;
			try
			{
				is = entity.getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line);
					sb.append('\n');
				}
			} catch (IOException e) {

			} catch (IllegalStateException e) {

			} finally {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}

		return sb.toString();
	}

	public static HttpResponse requestWithoutBody(Method method, String uri) throws IOException
	{
		return requestWithEntity(method, uri, null, null);
	}

	public static HttpResponse requestWithJSONBody(Method method, String uri, String body) throws Exception
	{
		Header[] headers = new Header[1];
		headers[0] = new BasicHeader(CONTENT_TYPE, CONTENT_TYPE_APPLICATION_JSON);
		return requestWithBody(method, uri, headers, body);
	}

	public static HttpResponse requestWithBody(Method method, String uri, Header[] headers, String body) throws Exception
	{
		StringEntity stringEntity;
		try {
			stringEntity = new StringEntity(body);
		} catch (UnsupportedEncodingException e) {
			throw new Exception("Error when encoding http request content", e);
		}
		return requestWithEntity(method, uri, headers, stringEntity);
	}




	/**
	 * Sends an Http request.
	 *
	 * @param method The http Method from the enum defined by this class
	 * @param uri The target uri of the server
	 * @param headers An array of Http Headers, for example the HTTP.CONTENT_TYPE of other ones.
	 * @param entity the entity containing the data to be sent
	 * @return HttpResponse the server response
	 */
	public static HttpResponse requestWithEntity(Method method, String uri, Header[] headers, HttpEntity entity) throws IOException
	{
		HttpClient httpclient = new DefaultHttpClient();

		HttpRequestBase request = null;
		if (Method.POST.equals(method)) {
			request = new HttpPost(uri);
			addContent((HttpPost) request, headers, entity);
		} else if (Method.PUT.equals(method)) {
			request = new HttpPut(uri);
			addContent((HttpPut) request, headers, entity);
		} else if (Method.GET.equals(method)) {
			request = new HttpGet(uri);
		} else if (Method.DELETE.equals(method)) {
			request = new HttpDelete();
		}

		HttpResponse response = null;
		trustCAServer();
		try {
			response = httpclient.execute(request);
			HttpEntity responseEntity = response.getEntity();


			if (response.getStatusLine().getStatusCode() < 200 || response.getStatusLine().getStatusCode() >= 300)
			{
				String result = null;
				if (responseEntity!=null) {
					InputStream instream = responseEntity.getContent();
					result = convertStreamToString(instream);
				}
				else throw new IOException("Error when connecting to HeLi server" + result);
			}
		} catch (ClientProtocolException e) {
			throw new IOException("Error in Http protocol when executing http request");
		}

		return response;
	}

	private static void addContent(HttpEntityEnclosingRequestBase request, Header[] headers, HttpEntity entity)
	{
		request = (HttpEntityEnclosingRequestBase) request;
		request.setEntity(entity);
		if (headers != null) {
			request.setHeaders(headers);
		}
		request.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
	}

	private static void trustCAServer() {
		try {
			HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier(){
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}});
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[]{new X509TrustManager(){
				public void checkClientTrusted(X509Certificate[] chain,
											   String authType) throws CertificateException {}
				public void checkServerTrusted(X509Certificate[] chain,
											   String authType) throws CertificateException {}
				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}}}, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(
					context.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}

	private static String convertStreamToString(InputStream is)
	{
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * until no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line);
				sb.append('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}

}