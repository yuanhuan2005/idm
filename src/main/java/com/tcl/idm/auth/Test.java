package com.tcl.idm.auth;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.util.EntityUtils;

public class Test
{
	final static private String HOST = "http://127.0.0.1:8080";

	//	final static private String HOST = "http://115.29.76.100:8080";
	//	final static private String HOST = "https://app.rideo.cn";

	public static HttpClient wrapClient(HttpClient base)
	{
		try
		{
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager()
			{
				@Override
				public X509Certificate[] getAcceptedIssuers()
				{
					return null;
				}

				@Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1)
				{
				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1)
				{
				}

			};
			ctx.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", 443, ssf));
			ClientConnectionManager connManager = new PoolingClientConnectionManager(registry);
			return new DefaultHttpClient(connManager, base.getParams());
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public static String testRegister() throws ClientProtocolException, IOException
	{
		String url = Test.HOST + "/api/user/register";
		HttpPost httpPost = new HttpPost(url);
		System.out.println("post url:" + url);
		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		httpPost.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.7");
		httpPost.setHeader("Connection", "keep-alive");

		MultipartEntity mutiEntity = new MultipartEntity();
		File file = new File("D:/test.jpg");
		mutiEntity.addPart("userImageName", new FileBody(file));
		mutiEntity.addPart("nickname", new StringBody("¥Û…µ", Charset.forName("utf-8")));
		mutiEntity.addPart("age", new StringBody("23", Charset.forName("utf-8")));
		mutiEntity.addPart("gender", new StringBody("1", Charset.forName("utf-8")));
		//		mutiEntity.addPart("phoneNumber", new StringBody("13555665263", Charset.forName("utf-8")));
		mutiEntity.addPart("email", new StringBody("13555665263@126.com", Charset.forName("utf-8")));
		mutiEntity.addPart("password", new StringBody("As2222S22", Charset.forName("utf-8")));

		httpPost.setEntity(mutiEntity);
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		String content = EntityUtils.toString(httpEntity);
		System.out.println("httpResponse code: " + httpResponse.getStatusLine().getStatusCode());
		System.out.println("httpResponse message: " + content);

		String accessToken = JSONObject.fromObject(content).getString("access_token");
		return accessToken;
	}

	public static void testUpdateUserInfo(String accessToken) throws ClientProtocolException, IOException
	{
		String url = Test.HOST + "/api/user/info?access_token=" + accessToken;
		HttpPost httpPost = new HttpPost(url);
		System.out.println("post url: " + url);
		httpPost.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
		httpPost.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
		httpPost.setHeader("Accept-Charset", "GBK,utf-8;q=0.7,*;q=0.7");
		httpPost.setHeader("Connection", "keep-alive");

		MultipartEntity mutiEntity = new MultipartEntity();
		//		File file = new File("D:/test.jpg");
		//		mutiEntity.addPart("userImageName", new FileBody(file));
		mutiEntity.addPart("nickname", new StringBody("New_¥Û…µ_002", Charset.forName("utf-8")));
		mutiEntity.addPart("age", new StringBody("27", Charset.forName("utf-8")));
		mutiEntity.addPart("gender", new StringBody("0", Charset.forName("utf-8")));
		mutiEntity.addPart("password", new StringBody("New_As2222S22", Charset.forName("utf-8")));

		httpPost.setEntity(mutiEntity);
		//		HttpClient httpClient = Test.wrapClient(new DefaultHttpClient());
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse httpResponse = httpClient.execute(httpPost);
		HttpEntity httpEntity = httpResponse.getEntity();
		String content = EntityUtils.toString(httpEntity);
		System.out.println("httpResponse code: " + httpResponse.getStatusLine().getStatusCode());
		System.out.println("httpResponse message: " + content);
	}

	public static void main(String[] args) throws ClientProtocolException, IOException
	{
		long beginTime = System.currentTimeMillis();
		String accessToken = "7baa051f-487c-4069-ae97-080d06127d14";
		accessToken = Test.testRegister();
		Test.testUpdateUserInfo(accessToken);

		System.out.println("Cost Time: " + (System.currentTimeMillis() - beginTime));
	}
}
