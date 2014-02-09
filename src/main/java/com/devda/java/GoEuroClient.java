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
import java.util.Scanner;

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
 *	GoEuroClient {@link GoEuroClient} 
 *	A utility to get data from remote API and store data in .csv file  
 */
public class GoEuroClient {
	
	public static final String fileExt = ".csv";
	public static final String spliter = ",";
	public static final String nextLine = "\n";
	public static final String header = "_type"+spliter+"_id"+spliter+"name"+spliter+"type"+spliter+"latitude"+spliter+"longitude"+nextLine;
	public static String inputString;

	public static Scanner scanner;
	
	static {
		scanner = new Scanner(System.in);
	}

	public static void main(String[] args) {
		connectToApi();
	}

	/**
	 * Connect to the JSON API 
	 */
	private static void connectToApi() {
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
			inputString = getString();

			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection
					.setDefaultSSLSocketFactory(sc.getSocketFactory());

			URL url = new URL(
					"https://api.goeuro.com/api/v1/suggest/position/en/name/"
							+ inputString);

			HttpsURLConnection connection = (HttpsURLConnection) url
					.openConnection();

			getData(connection);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return String 
	 * Takes input string
	 */
	public static String getString() {
		System.out.println("Please Enter The String");
		String string = scanner.next();
		return string;
	}

	/**
	 * @param connection HttpsURLConnection
	 * Get data from JSON API as String  
	 */
	private static void getData(HttpsURLConnection connection) {
		if (connection != null) {

			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(
						connection.getInputStream()));
				String input;				
				while ((input = br.readLine()) != null) {
					writeToCsv(input, inputString);
				}
				br.close();
			} catch(FileNotFoundException e){
				System.err.println("Input String is not valid !! :-> " + inputString);
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param input 
	 * @param fileName name of csv file.
	 * Default place of file is C: drive. 
	 */
	@SuppressWarnings("rawtypes")
	private static void writeToCsv(String input, String fileName) {
		
		JSONParser parser = new JSONParser();
		Object object;
		try {
			object = parser.parse(input);
			JSONObject jsonObject = (JSONObject) object;
			JSONArray results = (JSONArray) jsonObject.get("results");
			StringBuffer writeToFile = new StringBuffer();
			writeToFile.append(header);
			if (results.size()!= 0) {
				FileWriter fileWriter = new FileWriter("c:\\"+fileName+fileExt);
				for (Iterator iterator = results.iterator(); iterator.hasNext();) {
					JSONObject jsonRow = (JSONObject) iterator.next();
					JSONObject geo_position = (JSONObject) jsonRow.get("geo_position");
					writeToFile.append(jsonRow.get("_type")+spliter);
					writeToFile.append(jsonRow.get("_id")+spliter);
					String temp = "\""+jsonRow.get("name").toString()+"\"";
					writeToFile.append(temp+spliter);
					writeToFile.append(jsonRow.get("type")+spliter);
					writeToFile.append(geo_position.get("latitude")+spliter);
					writeToFile.append(geo_position.get("longitude")+nextLine);
				}
				fileWriter.append(writeToFile.toString());
				fileWriter.flush();
				fileWriter.close();				
			}else {
				System.out.println("No data available !!");
				connectToApi();
			}
			
		} catch (ParseException e) {			
			e.printStackTrace();
		} catch (IOException e) {			
			e.printStackTrace();
		}
		finally{
			scanner.close();
		}
	}

}
