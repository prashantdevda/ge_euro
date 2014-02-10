package com.devda.java;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.Iterator;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Prashant 
 * GoEuroClient {@link GoEuroClient} A utility to get data 
 * from remote API and store data in .csv file
 */
public class GoEuroClient {

	public static final String fileExt = ".csv";
	public static final String spliter = ",";
	public static final String nextLine = "\n";
	public static final String header = "_type" + spliter + "_id" + spliter
			+ "name" + spliter + "type" + spliter + "latitude" + spliter
			+ "longitude" + nextLine;

	public static void main(String[] args) {

		if (isOnlyCharacter(args[0].trim()) && args[0].trim().length() != 0) {
			connectToApi(args[0]);
		} else {
			System.err
					.println("Invalid Input !! Empty String, Numbers and special characters are not allowed.");
		}

	}

	/**
	 * @param s String 
	 * @return true if string has only characters
	 */
	private static boolean isOnlyCharacter(String s) {
		return s.matches("[a-zA-Z\\s]{" + s.length() + "}");
	}

	/**
	 * Connect to the JSON API
	 */
	private static void connectToApi(String inString) {
		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(
					java.security.cert.X509Certificate[] certs, String authType) {
			}
		} };

		try {
			String inputString = inString;

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());

			URL url = new URL(
					"https://api.goeuro.com/api/v1/suggest/position/en/name/"
							+ inputString);

			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();

			getData(connection, inputString);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param connection
	 *            HttpsURLConnection Get data from JSON API as String
	 * @param inputString String which passed as argument. 
	 */
	private static void getData(HttpsURLConnection connection,
			String inputString) {
		if (connection != null) {

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String jsonString;
				while ((jsonString = br.readLine()) != null) {
					writeToCsv(jsonString, inputString);
				}
				br.close();
			} catch (FileNotFoundException e) {
				System.err.println("Input String is not valid !! :-> "
						+ inputString);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param jsonString 
	 * @param fileName
	 *            name of csv file. Default place of file is C: drive.
	 */
	@SuppressWarnings("rawtypes")
	private static void writeToCsv(String jsonString, String fileName) {

		JSONParser parser = new JSONParser();
		Object object;
		try {
			object = parser.parse(jsonString);
			JSONObject jsonObject = (JSONObject) object;
			JSONArray results = (JSONArray) jsonObject.get("results");
			StringBuffer writeToFile = new StringBuffer();
			writeToFile.append(header);
			if (results.size() != 0) {
				FileWriter fileWriter = new FileWriter("c:\\" + fileName
						+ fileExt);
				for (Iterator iterator = results.iterator(); iterator.hasNext();) {
					JSONObject jsonRow = (JSONObject) iterator.next();
					JSONObject geo_position = (JSONObject) jsonRow
							.get("geo_position");
					writeToFile.append(jsonRow.get("_type") + spliter);
					writeToFile.append(jsonRow.get("_id") + spliter);
					String temp = "\"" + jsonRow.get("name").toString() + "\"";
					writeToFile.append(temp + spliter);
					writeToFile.append(jsonRow.get("type") + spliter);
					writeToFile.append(geo_position.get("latitude") + spliter);
					writeToFile
							.append(geo_position.get("longitude") + nextLine);
				}
				fileWriter.append(writeToFile.toString());
				fileWriter.flush();
				fileWriter.close();
				System.out
						.println("File is Successfuly crested!! you can check at : c:\\"
								+ fileName + fileExt);
			} else {
				System.err.println("Invalid input please try again");
			}

		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
