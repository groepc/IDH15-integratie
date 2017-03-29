package org.groepc;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class CallBuienradar implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		NotificationPojo notification = exchange.getIn().getBody(NotificationPojo.class);
		String startLatitude = notification.getLocation_start_lat();
		String startLongitude = notification.getLocation_start_lng();

		// temporarily hold the values of buienradar
		Map<String, String> weatherMap = new HashMap<String, String>();

		HttpClient client = new HttpClient();

		String uri = "http://gpsgadget.buienradar.nl/data/raintext?lat=" + startLatitude + "&lon=" + startLongitude;

		GetMethod method = new GetMethod(uri);
		method.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");

		try {
			int statusCode = client.executeMethod(method);
			String body = method.getResponseBodyAsString();

			Scanner scanner = new Scanner(body);
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				String[] parts = line.split("\\|");
				weatherMap.put(parts[1], parts[0]);
			}
			scanner.close();

			//release connection
			method.releaseConnection();
		} catch (IOException e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}

		// add buienradar data to model
		notification.setWeather(weatherMap);

		// set new message
		exchange.getIn().setBody(notification);
	}
}
