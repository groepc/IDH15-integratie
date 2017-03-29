package org.groepc;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.*;

public class CallGoogleMaps implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        NotificationPojo notification = exchange.getIn().getBody(NotificationPojo.class);
        String startLatitude = notification.getLocation_start_lat();
        String startLongitude = notification.getLocation_start_lng();

        String endLatitude = notification.getLocation_end_lat();
        String endLongitude = notification.getLocation_end_lng();

        HttpClient client = new HttpClient();

        String uri = "https://maps.googleapis.com/maps/api/directions/json?origin=" + startLatitude + "," + startLongitude + "&destination=" + endLatitude + "," + endLongitude + "&key=" + (String) exchange.getIn().getHeader("googleApiKey") + "&mode=bicycling";

        GetMethod method = new GetMethod(uri);
        method.setRequestHeader("Content-type", "text/xml; charset=ISO-8859-1");
        System.out.println("We komen hier");
        try {
            int statusCode = client.executeMethod(method);
            Gson g = new Gson();
            JsonObject jsonObject = new JsonParser().parse(method.getResponseBodyAsString()).getAsJsonObject();

            System.out.println(jsonObject.getAsJsonArray("routes").getAsJsonObject().getAsJsonArray("legs").getAsJsonObject().getAsJsonObject("distance").get("text")); //John
            
            //System.out.println(jsonObject.get("routes").getAsString()); //John

            //release connection
            method.releaseConnection();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }

        // add buienradar data to model
        //notification.setWeather(weatherMap);
        // set new message
        //	exchange.getIn().setBody(notification);
    }
}
