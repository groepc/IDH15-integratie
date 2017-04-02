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
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApi.RouteRestriction;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;
import com.google.maps.model.Unit;

public class CallGoogleMaps implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        NotificationPojo notification = exchange.getIn().getBody(NotificationPojo.class);
        String startLatitude = notification.getLocation_start_lat();
        String startLongitude = notification.getLocation_start_lng();

        String endLatitude = notification.getLocation_end_lat();
        String endLongitude = notification.getLocation_end_lng();

        GeoApiContext context = new GeoApiContext().setApiKey((String) exchange.getIn().getHeader("googleApiKey"));

        DirectionsResult results = DirectionsApi.newRequest(context)
                .mode(TravelMode.BICYCLING)
                .units(Unit.METRIC)
                .region("nl")
                .language("nl")
                .origin(startLatitude + "," + startLongitude)
                .destination(endLatitude + "," + endLongitude).await();

        notification.setDistance(results.routes[0].legs[0].distance.humanReadable);
        notification.setTimeToCycle(results.routes[0].legs[0].duration.humanReadable);

        exchange.getIn().setBody(notification);
    }
}
