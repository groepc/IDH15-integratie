package org.groepc;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class GenerateMessage implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        NotificationPojo notification = exchange.getIn().getBody(NotificationPojo.class);

        String message_email = "Ls,<br><br>";

        message_email += "Je wilt gaan fietsen van " + notification.getLocation_start()
                + " naar " + notification.getLocation_end() +  ". Hieronder je fiets advies:<br><br>";

        if (notification.getRainIndex() == 0) {
            message_email += "Je kunt vandaag droog fietsen!";
        } else if (notification.getRainIndex() <= 50) {
            message_email += "Je fietst zo snel dat je de regen niet eens kan zien. Pak lekker de fiets! ";
        } else if (notification.getRainIndex() <= 100) {
            message_email += "Er is wel wat kans op regen, dus neem voor de zekerheid je regenpak mee. ";
        } else if (notification.getRainIndex() <= 200) {
            message_email += "Regenpak heb je zeker nodig, maar je kunt nog wel fietsen! ";
        } else if (notification.getRainIndex() <= 254) {
            message_email += "Als je een echte storm overwinner bent, dan pak je je fiets anders lekker laten staan! ";
        } else if (notification.getRainIndex() == 255) {
            message_email += "Pak zeker niet je fiets, het is niet te doen! ";
        }

        message_email += "<br><br>Met vriendelijke groet, <br>Is het fiets weer";

        notification.setMessage_email(message_email);

        // set new message
        exchange.getIn().setBody(notification);
    }
}
