package org.groepc;

import java.util.Map;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class ProcessData implements Processor {
    
    @Override
    public void process(Exchange exchange) throws Exception {
        NotificationPojo notification = exchange.getIn().getBody(NotificationPojo.class);
        Map<String, String> map = notification.getWeather();
        
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + "/" + entry.getValue());
        }
        
        notification.setRainIndex(100);

        // set new message
        exchange.getIn().setBody(notification);
    }
}