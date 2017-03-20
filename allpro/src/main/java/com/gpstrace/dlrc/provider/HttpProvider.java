package com.gpstrace.dlrc.provider;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

@SuppressWarnings("deprecation")
public class HttpProvider {
	// region fields

	private static String cookieStr = "Cookie";
	private static String setCookieStr = "Set-Cookie";
	private static String setCookieStr_TONGXUEPAI = "Set-Cookie";

	private static final int CONNECT_TIMEOUT = 15 * 1000;// 设置请求超时20秒钟
	private static final int REQUEST_TIMEOUT = 15 * 1000; // 设置等待数据响应超时时间20秒钟

	private static String agentStr = "";

	// endregion

	/**
	 * @添加请求超时时间和等待时间
	 * @是否需要通过单件来维护，后面在测试
	 * @return HttpClient对象
	 */
	private static synchronized HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, CONNECT_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, REQUEST_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		client.getParams().setParameter(CoreProtocolPNames.USER_AGENT, agentStr);
		// HttpClient client = new DefaultHttpClient();
		return client;
	}

	/**
	 * @设置User-Agent字符串
	 * @return
	 */
	public static void setAgentString(String agent) {
		agentStr = agent;
	}

	/**
	 * @根据value生成Header
	 * @param value
	 * @return
	 */
	private static String getSessionValue(Header[] headers) {
		String sessValue = "";
		if (headers != null) {
			for (int i = 0; i < headers.length; i++) {
				String[] tempStr = headers[i].getValue().split(";");
				for (int j = 0; j < tempStr.length; j++) {
					if (tempStr[j].startsWith("PHPSESSID=")) {
						return tempStr[j];
					}
				}
			}
		}

		return sessValue;
	}
	
	/**
 	 * @采用get方式获取数据
	 * @暂时只提供给同学派使用
	 * @根据value生成Header
	 * @param value
	 * @return
	 */
	private static String getJsonSessionValue(Header[] headers) {
		String sessValue = "";
		if (headers != null) {
			for (int i = 0; i < headers.length; i++) {
				String[] tempStr = headers[i].getValue().split(";");
				for (int j = 0; j < tempStr.length; j++) {
					if (tempStr[j].startsWith("SESSION=")) {					
						return tempStr[j];
					}
				}
			}
		}

		return sessValue;
	}

	/**
	 * @采用get方式获取数据，不发送数据
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String getMessage(String strUrl) throws ClientProtocolException, IOException {
		String strResult = "";

		try {
			URL url = new URL(strUrl);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet httpRequest = new HttpGet(uri);// HttpGet对象

			// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult = EntityUtils.toString(httpEntity);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strResult;
	}

	/**
	 * @采用get方式获取数据，不发送数据
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param encodeStr
	 * @编码控制
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String getMessageWithEncode(String strUrl, String encodeStr)
			throws ClientProtocolException, IOException {
		String strResult = "";
		try {
			URL url = new URL(strUrl);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet httpRequest = new HttpGet(uri);// HttpGet对象

			// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			
			
			
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult = EntityUtils.toString(httpEntity, encodeStr);
			}
//			else {
//				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_NOT_FOUND) {
//					if (httpResponse.containsHeader("Connect Timeout")) {				
//					}
//				}
//			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strResult;
	}

	/**
	 * @采用get方式获取数据
	 * @param strUrl
	 * @访问的服务器url地址
	 * @param args
	 * @要传递的参数
	 * @return
	 */
	public static String getMessage(String strUrl, String args) {

		String strResult = "";
		try {
			URL url = new URL(strUrl + args);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);

			HttpGet httpRequest = new HttpGet(uri);// HttpGet对象
			// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult = EntityUtils.toString(httpEntity);// 取得返回的数据
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strResult;
	}

	/**
	 * @采用get方式获取数据
	 * @param strUrl
	 * @访问的服务器url地址
	 * @param args
	 * @要传递的参数
	 * @param encodeStr
	 * @编码格式
	 * @return
	 */
	public static String getMessage(String strUrl, String args, String encodeStr) {

		String strResult = "";
		try {
			URL url = new URL(strUrl + args);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet httpRequest = new HttpGet(uri);// HttpGet对象
			// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult = EntityUtils.toString(httpEntity, encodeStr);// 取得返回的数据
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strResult;
	}

	/**
	 * @采用post方式获取数据，不带编码解析
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param args
	 * @参数键值对
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postMessage(String strUrl, Map<String, String> args)
			throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象
		List<NameValuePair> pairList = new ArrayList<NameValuePair>();

		for (Entry<String, String> entry : args.entrySet()) {
			pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
		httpRequest.setEntity(requestHttpEntity);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity);
		}

		return strResult;
	}

	/**
	 * @采用post方式获取数据，不带编码解析
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param args
	 * @参数键值对
	 * @param encodeStr
	 * @编码方式
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postMessage(String strUrl, Map<String, String> args, String encodeStr)
			throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象
		List<NameValuePair> pairList = new ArrayList<NameValuePair>();

		for (Entry<String, String> entry : args.entrySet()) {
			pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
		httpRequest.setEntity(requestHttpEntity);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity, encodeStr);
		}

		return strResult;
	}

	/**
	 * @采用post方式获取数据，不带编码解析
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param jsonStr
	 * @已经封装好的json对象字符串
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postJsonMessage(String strUrl, String jsonStr) throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpGet对象
		StringEntity se = new StringEntity(jsonStr);
		httpRequest.setEntity(se);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity);
		}

		return strResult;
	}

	/**
	 * @采用post方式获取数据，有带有编码解析
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param jsonStr
	 * @已经封装好的json对象字符串
	 * @param encodeStr
	 * @编码方式
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postJsonMessage(String strUrl, String jsonStr, String encodeStr)
			throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpGet对象
		StringEntity se = new StringEntity(jsonStr);
		httpRequest.setEntity(se);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity, encodeStr);
		}

		return strResult;
	}

	/**
	 * @采用post方式获取数据，不发送数据
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postEmptyMessage(String strUrl) throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpGet对象

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity);
		}

		return strResult;
	}

	/**
	 * @采用post方式获取数据，不发送数据
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param encodeStr
	 * @编码方式
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postEmptyMessage(String strUrl, String encodeStr) throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpGet对象

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity, encodeStr);
		}

		return strResult;
	}

	// region 带有登录态的操作

	/**
	 * @采用get方式获取数据，不发送数据，带有登录态
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param value
	 * @登录态值
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String getMessageWithStatus(String strUrl, String value) throws ClientProtocolException, IOException {
		String strResult = "";
		try {

			URL url = new URL(strUrl);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet httpRequest = new HttpGet(uri);// HttpGet对象
			httpRequest.addHeader(cookieStr, value);

			// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult = EntityUtils.toString(httpEntity);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strResult;
	}

	/**
	 * @采用get方式获取数据，不发送数据，带有登录态
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param encodeStr
	 * @编码控制
	 * @param value
	 * @登录态值
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String getMessageWithStatus(String strUrl, String encodeStr, String value)
			throws ClientProtocolException, IOException {
		String strResult = "";
		try {
			URL url = new URL(strUrl);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet httpRequest = new HttpGet(uri);// HttpGet对象
			httpRequest.addHeader(cookieStr, value);

			// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult = EntityUtils.toString(httpEntity, encodeStr);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strResult;
	}

	/**
	 * @采用get方式获取数据，不发送数据,用于登陆或注册前准备,返回SessionID
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @author hdchen
	 */
	public static String[] getMessageWithReturnStatus(String strUrl) throws ClientProtocolException, IOException {
		String[] strResult = new String[2];

		try {
			URL url = new URL(strUrl);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet httpRequest = new HttpGet(uri);// HttpGet对象

			// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult[0] = EntityUtils.toString(httpEntity);
				strResult[1] = getSessionValue(httpResponse.getHeaders(setCookieStr));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strResult;

	}
	
	/**
	 * @采用get方式获取数据，不发送数据,用于登陆或注册前准备,返回SessionID
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @author hdchen
	 */
	public static String[] getMessageWithReturnStatus(String strUrl, 
			String value) throws ClientProtocolException, IOException {
		String[] strResult = new String[2];

		try {
			URL url = new URL(strUrl);
			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet httpRequest = new HttpGet(uri);// HttpGet对象
			httpRequest.addHeader(cookieStr, value);

			// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult[0] = EntityUtils.toString(httpEntity);
				strResult[1] = getSessionValue(httpResponse.getHeaders(setCookieStr));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return strResult;

	}

	/**
	 * @发送带有登录态的信息，其不需要保存返回的登录态
	 * @适用其他与登录状态相关的操作(除了登录和注册之外) @精确到具体用户的操作都需要传入登录态
	 * @param strUrl
	 * @param args
	 * @param value
	 * @Header的值：如消息头Cookie:PHPSESSID=q0l13udacb96pgeit3832ns2a4
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String postMessageWithSendStatus(String strUrl, Map<String, String> args, String value)
			throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象
		httpRequest.addHeader(cookieStr, value);

		List<NameValuePair> pairList = new ArrayList<NameValuePair>();

		for (Entry<String, String> entry : args.entrySet()) {
			pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
		httpRequest.setEntity(requestHttpEntity);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity);
		}

		return strResult;
	}

	/**
	 * @发送带有登录态的信息，其不需要保存返回的登录态
	 * @适用其他与登录状态相关的操作(除了登录和注册之外) @精确到具体用户的操作都需要传入登录态
	 * @param strUrl
	 * @param args
	 * @param encodeStr
	 * @编码格式
	 * @param value
	 * @Header的值：如消息头Cookie:PHPSESSID=q0l13udacb96pgeit3832ns2a4
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String postMessageWithSendStatus(String strUrl, Map<String, String> args, String encodeStr,
			String value) throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象
		httpRequest.addHeader(cookieStr, value);

		List<NameValuePair> pairList = new ArrayList<NameValuePair>();

		for (Entry<String, String> entry : args.entrySet()) {
			pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
		httpRequest.setEntity(requestHttpEntity);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity, encodeStr);
		}

		return strResult;
	}

	/**
	 * @不发送带登录态的信息，但需要保存返回的登录态
	 * @适用于登录和注册
	 * @param strUrl
	 * @param args
	 * @param header
	 * @消息头Cookie:PHPSESSID=q0l13udacb96pgeit3832ns2a4
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String[] postMessageWithReturnStatus(String strUrl, Map<String, String> args)
			throws ClientProtocolException, IOException {
		String[] strResult = new String[2];
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象

		List<NameValuePair> pairList = new ArrayList<NameValuePair>();

		for (Entry<String, String> entry : args.entrySet()) {
			pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
		httpRequest.setEntity(requestHttpEntity);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				strResult[0] = EntityUtils.toString(httpEntity);
				strResult[1] = getSessionValue(httpResponse.getHeaders(setCookieStr));
			}
		}

		return strResult;
	}

	/**
	 * @不发送带登录态的信息，但需要保存返回的登录态，可以进行编码控制
	 * @适用于登录和注册
	 * @param strUrl
	 * @param args
	 * @param encodeStr
	 * @编码格式
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String[] postMessageWithReturnStatus(String strUrl, Map<String, String> args, String encodeStr)
			throws ClientProtocolException, IOException {
		String[] strResult = new String[2];
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象

		List<NameValuePair> pairList = new ArrayList<NameValuePair>();

		for (Entry<String, String> entry : args.entrySet()) {
			pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
		httpRequest.setEntity(requestHttpEntity);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				strResult[0] = EntityUtils.toString(httpEntity, encodeStr);
				strResult[1] = getSessionValue(httpResponse.getHeaders(setCookieStr));
			}
		}

		return strResult;
	}

	/**
	 * @采用post方式获取数据，不发送数据，并且传入登录态
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param value
	 * @Header的值：如消息头Cookie:PHPSESSID=q0l13udacb96pgeit3832ns2a4
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postEmptyMessageWithSendStatus(String strUrl, String value)
			throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpGet对象
		httpRequest.addHeader(cookieStr, value);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity);
		}

		return strResult;
	}

	/**
	 * @采用post方式获取数据，不发送数据，并且传入登录态，可以编码控制
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param value
	 * @Header的值：如消息头Cookie:PHPSESSID=q0l13udacb96pgeit3832ns2a4
	 * @param encodeStr
	 * @编码方式
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postEmptyMessageWithSendStatus(String strUrl, String value, String encodeStr)
			throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpGet对象
		httpRequest.addHeader(cookieStr, value);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity, encodeStr);
		}

		return strResult;
	}

	/**
	 * @发送和返回都带登录态的信息，编码控制
	 * @适用于登录检查
	 * @param strUrl
	 * @param args
	 * @param value
	 * @Header的值：如消息头Cookie:PHPSESSID=q0l13udacb96pgeit3832ns2a4
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String[] postMessageWithStatus(String strUrl, Map<String, String> args, String value)
			throws ClientProtocolException, IOException {
		String[] strResult = new String[2];
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象
		httpRequest.addHeader(cookieStr, value);

		List<NameValuePair> pairList = new ArrayList<NameValuePair>();

		for (Entry<String, String> entry : args.entrySet()) {
			pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
		httpRequest.setEntity(requestHttpEntity);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				strResult[0] = EntityUtils.toString(httpEntity);
				strResult[1] = getSessionValue(httpResponse.getHeaders(setCookieStr));
			}
		}

		return strResult;
	}

	/**
	 * @发送和返回都带登录态的信息，编码控制
	 * @适用于登录检查
	 * @param strUrl
	 * @param args
	 * @param value
	 * @Header的值：如消息头Cookie:PHPSESSID=q0l13udacb96pgeit3832ns2a4
	 * @param encodeStr
	 * @编码格式
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	public static String[] postMessageWithStatus(String strUrl, Map<String, String> args, String value,
			String encodeStr) throws ClientProtocolException, IOException {
		String[] strResult = new String[2];
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象
		httpRequest.addHeader(cookieStr, value);

		List<NameValuePair> pairList = new ArrayList<NameValuePair>();

		for (Entry<String, String> entry : args.entrySet()) {
			pairList.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
		}

		HttpEntity requestHttpEntity = new UrlEncodedFormEntity(pairList, HTTP.UTF_8);
		httpRequest.setEntity(requestHttpEntity);

		// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null) {
				strResult[0] = EntityUtils.toString(httpEntity, encodeStr);
				strResult[1] = getSessionValue(httpResponse.getHeaders(setCookieStr));
			}
		}

		return strResult;
	}
	
	//region 同学派专用，针对网络部分进行一些设置处理
	
	/**
	 * @采用post方式使entry按json字符串传输
	 * @暂时只提供给同学派使用
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param args
	 * @参数键值对
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postJsonMessage(String strUrl, Map<String, String> args)
			throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象

		httpRequest.setHeader("Accept", "application/json");
		httpRequest.setHeader("Content-type", "application/json");

		StringEntity entity = new StringEntity(JsonProvider.createJsonString(args), HTTP.UTF_8);

		httpRequest.setEntity(entity);

		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity);
		}

		return strResult;
	}
	
	/**
	 * @采用post方式使entry按json字符串传输,带登录态
	 * @暂时只提供给同学派使用
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param args
	 * @参数键值对
	 * @param value
	 * @登录态
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postJsonMessageWithSendStatus(String strUrl, Map<String, String> args, String value)
			throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象
		httpRequest.addHeader(cookieStr, value);

		httpRequest.setHeader("Accept", "application/json");
		httpRequest.setHeader("Content-type", "application/json");

		StringEntity entity = new StringEntity(JsonProvider.createJsonString(args), HTTP.UTF_8);

		httpRequest.setEntity(entity);

		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity);
		}

		return strResult;
	}
	
	
	/**
	 * @采用post方式使entry按json字符串传输,带登录态
	 * @暂时只提供给同学派使用
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param args
	 * @JSONObject 的字符串
	 * @param value
	 * @登录态
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String postJsonMessageWithSendStatus(String strUrl, String args, String value)
			throws ClientProtocolException, IOException {
		String strResult = "";
		HttpPost httpRequest = new HttpPost(strUrl);// HttpPost对象
		httpRequest.addHeader(cookieStr, value);

		httpRequest.setHeader("Accept", "application/json");
		httpRequest.setHeader("Content-type", "application/json");

		StringEntity entity = new StringEntity(args, HTTP.UTF_8);

		httpRequest.setEntity(entity);

		HttpClient httpClient = getHttpClient();// 增加http超时处理
		HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象

		// 解析返回，若不带返回，则后面的可以不用
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			HttpEntity httpEntity = httpResponse.getEntity();
			if (httpEntity != null)
				strResult = EntityUtils.toString(httpEntity);
		}
		return strResult;
	}
	
	
	/**
	 * @采用get方式获取数据
	 * @暂时只提供给同学派使用
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String getJsonMessage(String strUrl) throws ClientProtocolException, IOException {
		String strResult = "";

		try {
			HttpGet httpRequest = new HttpGet(strUrl);// HttpGet对象

			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult = EntityUtils.toString(httpEntity);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return strResult;
	}
	
	/**
	 * @采用get方式获取数据
	 * @暂时只提供给同学派使用
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String[] getJsonMessageNew(String strUrl) throws ClientProtocolException, IOException {
		String[] strResult = new String[2];

		try {
//			URL url = new URL(strUrl);
//			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet httpRequest = new HttpGet(strUrl);// HttpGet对象

			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult[0] = EntityUtils.toString(httpEntity);
					strResult[1] = getJsonSessionValue(httpResponse.getHeaders(setCookieStr_TONGXUEPAI));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
//		catch (URISyntaxException e) {			
//			e.printStackTrace();
//		}

		return strResult;
	}
	
	/**
	 * @采用get方式获取数据,返回SessionID
	 * @此接口用于同学派
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 * @author hdchen
	 */
	public static String[] getTMessageWithReturnStatus(String strUrl, 
			String value) throws ClientProtocolException, IOException {
		String[] strResult = new String[2];

		try {
//			URL url = new URL(strUrl);
//			URI uri = new URI(url.getProtocol(), url.getHost(), url.getPath(), url.getQuery(), null);
			HttpGet httpRequest = new HttpGet(strUrl);// HttpGet对象
			httpRequest.addHeader(cookieStr, value);

			// HttpClient httpClient = new DefaultHttpClient();// HttpClient对象
			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult[0] = EntityUtils.toString(httpEntity);
					strResult[1] = getJsonSessionValue(httpResponse.getHeaders(setCookieStr_TONGXUEPAI));
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
//		catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		return strResult;

	}
	
	/**
	 * @采用get方式获取数据，带登录态
	 * @暂时只提供给同学派使用
	 * @param strUrl
	 * @远程访问服务器端的url
	 * @param value
	 * @登录态
	 * @return
	 * @返回json字符串，可直接用于解析json对象
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public static String getJsonMessageWithStatus(String strUrl,String value) throws ClientProtocolException, IOException {
		String strResult = "";

		try {
			HttpGet httpRequest = new HttpGet(strUrl);// HttpGet对象
			httpRequest.addHeader(cookieStr, value);

			HttpClient httpClient = getHttpClient();// 增加http超时处理
			HttpResponse httpResponse = httpClient.execute(httpRequest);// 获得HttpResponse对象
			if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity httpEntity = httpResponse.getEntity();
				if (httpEntity != null)
					strResult = EntityUtils.toString(httpEntity);
			}
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return strResult;
	}
	
	//endregion

	// endregion
}
