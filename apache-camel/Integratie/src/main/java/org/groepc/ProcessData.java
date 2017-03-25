package org.groepc;

import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ProcessData implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        NotificationPojo notification = exchange.getIn().getBody(NotificationPojo.class);
        Map<String, String> map = notification.getWeather();

        int counter = 0;
        int value = 0;
        int rainIndex = 0;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            value += Integer.parseInt(entry.getValue());
            counter++;
        }

        System.out.println(value);
        System.out.println(counter);

        if (value != 0) {
            rainIndex = Math.floor(value / counter);
        }

        System.out.println(rainIndex);

        notification.setRainIndex(rainIndex);

        // set new message
        exchange.getIn().setBody(notification);
    }
}
