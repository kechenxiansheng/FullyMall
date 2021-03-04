package com.cm.fm.mall.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import android.annotation.SuppressLint;
import android.text.TextUtils;
import android.util.Log;

public class HttpUtils {

	private final static String PARAMETER_SEPARATOR = "&";
	private final static String NAME_VALUE_SEPARATOR = "=";
	private static String tag = "HttpUtils";
	/**
	 * Http GET 请求
	 * @param urlStr
	 * @param params
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String httpGet(String urlStr, Map<String, String> params) {

		String result = null;
		URL url = null;
		HttpsURLConnection httpsURLConnection = null;
		HttpURLConnection httpURLConnection = null;
		InputStreamReader in = null;
		try {
			String paramsEncoded = "";
			if (params != null) {
				paramsEncoded = urlParamsFormat(params, "UTF-8");
			}
			String fullUrl = "";
			if(!paramsEncoded.equals(""))
				fullUrl = urlStr + "?" + paramsEncoded;
			else
				fullUrl = urlStr;

			Log.d(tag, "the fullUrl is " + fullUrl);

			url = new URL(fullUrl);
			Object connection = url.openConnection();
			if (url.getProtocol().toLowerCase().equals("https")) {
				httpsURLConnection = (HttpsURLConnection) connection;
				if (httpsURLConnection.getResponseCode() == 200) {

					in = new InputStreamReader(httpsURLConnection.getInputStream());
					BufferedReader bufferedReader = new BufferedReader(in);
					StringBuffer strBuffer = new StringBuffer();
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						strBuffer.append(line);
					}
					result = strBuffer.toString();
				} else {
					Log.e(tag, "get connection failed. code:" + httpsURLConnection.getResponseCode() + ";url:" + fullUrl);
				}
			} else {
				httpURLConnection = (HttpURLConnection) connection;
				if (httpURLConnection.getResponseCode() == 200) {

					in = new InputStreamReader(httpURLConnection.getInputStream());
					BufferedReader bufferedReader = new BufferedReader(in);
					StringBuffer strBuffer = new StringBuffer();
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						strBuffer.append(line);
					}
					result = strBuffer.toString();
				} else {
					Log.e(tag, "get connection failed. code:" + httpURLConnection.getResponseCode() + ";url:" + fullUrl);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpsURLConnection != null) {
				httpsURLConnection.disconnect();
			}
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return result;
	}

	/**
	 * Http POST 请求
	 * 
	 * @param urlStr
	 * @param params
	 * @return
	 */
	@SuppressLint("DefaultLocale")
	public static String httpPost(String urlStr, Map<String, String> params) {

		String result = null;
		URL url = null;
		HttpsURLConnection httpsURLConnection = null;
		HttpURLConnection httpURLConnection = null;
		InputStreamReader in = null;
		try {
			String paramsEncoded = "";
			if (params != null) {
				paramsEncoded = urlParamsFormatToJson(params, "UTF-8");
			}

			url = new URL(urlStr);
			Object connection = url.openConnection();
			if (url.getProtocol().toLowerCase().equals("https")) {
				httpsURLConnection = (HttpsURLConnection) connection;
				httpsURLConnection.setDoInput(true);
				httpsURLConnection.setDoOutput(true);
				httpsURLConnection.setRequestMethod("POST");
//				httpsURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				httpURLConnection.setRequestProperty("Content-Type", "application/json");
				httpsURLConnection.setRequestProperty("Charset", "utf-8");
				DataOutputStream dop = new DataOutputStream(httpsURLConnection.getOutputStream());
				dop.writeBytes(paramsEncoded);
				dop.flush();
				dop.close();

				if (httpsURLConnection.getResponseCode() == 200) {
					in = new InputStreamReader(httpsURLConnection.getInputStream());
					BufferedReader bufferedReader = new BufferedReader(in);
					StringBuffer strBuffer = new StringBuffer();
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						strBuffer.append(line);
					}
					result = strBuffer.toString();
				} else {
					Log.e(tag, "post connection failed. code:" + httpsURLConnection.getResponseCode() + ";url:" + urlStr);
				}
			} else {
				httpURLConnection = (HttpURLConnection) connection;
				httpURLConnection.setDoInput(true);
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setRequestMethod("POST");
//				httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
				httpURLConnection.setRequestProperty("Content-Type", "application/json");
				httpURLConnection.setRequestProperty("Charset", "utf-8");
				DataOutputStream dop = new DataOutputStream(httpURLConnection.getOutputStream());
				dop.writeBytes(paramsEncoded);
				dop.flush();
				dop.close();

				if (httpURLConnection.getResponseCode() == 200) {
					in = new InputStreamReader(httpURLConnection.getInputStream());
					BufferedReader bufferedReader = new BufferedReader(in);
					StringBuffer strBuffer = new StringBuffer();
					String line = null;
					while ((line = bufferedReader.readLine()) != null) {
						strBuffer.append(line);
					}
					result = strBuffer.toString();
				} else {
					Log.e(tag, "post connection failed. code:" + httpURLConnection.getResponseCode() + ";url:" + urlStr);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (httpsURLConnection != null) {
				httpsURLConnection.disconnect();
			}
			if (httpURLConnection != null) {
				httpURLConnection.disconnect();
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return result;
	}

	/**
	 * Returns a String that is suitable for use as an
	 * application/x-www-form-urlencoded list of parameters in an HTTP PUT or
	 * HTTP POST.
	 * 
	 * @param params
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String urlParamsFormat(Map<String, String> params, String charset) throws UnsupportedEncodingException {
		final StringBuilder sb = new StringBuilder();
		for (String key : params.keySet()) {
			final String val = params.get(key);
			if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(val)) {
				final String encodedName = URLEncoder.encode(key, charset);
				final String encodedValue = URLEncoder.encode(val, charset);
				if (sb.length() > 0) {
					sb.append(PARAMETER_SEPARATOR);
				}
				sb.append(encodedName).append(NAME_VALUE_SEPARATOR).append(encodedValue);
			}
		}
		return sb.toString();
	}

	/**
	 * Returns a String that is suitable for use as an
	 * application/x-www-form-urlencoded list of parameters in an HTTP PUT or
	 * HTTP POST.
	 *
	 * @param params
	 * @param charset
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String urlParamsFormatToJson(Map<String, String> params, String charset) throws UnsupportedEncodingException {
		final StringBuilder sb = new StringBuilder();
		sb.append("{");
		for (String key : params.keySet()) {
			final String val = params.get(key);
			if (!TextUtils.isEmpty(key) && !TextUtils.isEmpty(val)) {
				final String encodedName = URLEncoder.encode(key, charset);
				final String encodedValue = URLEncoder.encode(val, charset);
				if(sb.length() > 1){
					sb.append(",");
				}
				sb.append("'").append(encodedName).append("':'").append(encodedValue).append("'");
			}
		}
		sb.append("}");
		LogUtil.d(tag,"params : " + sb.toString());
		return sb.toString();
	}
}
