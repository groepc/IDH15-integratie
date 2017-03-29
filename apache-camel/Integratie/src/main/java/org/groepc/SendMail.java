package org.groepc;

import com.sendgrid.*;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import java.io.IOException;

public class SendMail implements Processor {

    @Override
    public void process(Exchange exchange) throws Exception {
        NotificationPojo notif = exchange.getIn().getBody(NotificationPojo.class);
        System.out.println(notif.getEmail());

        Email from = new Email("camellover@gmail.com");
        String subject = "Is het fiets weer?!";
        Email to = new Email(notif.getEmail());
        Content content = new Content();
        content.setType("text/html");
        content.setValue(notif.getMessage_email());
        Mail mail = new Mail(from, subject, to, content);
        
        System.out.println(exchange.getIn().getHeader("sendgridApi"));
        SendGrid sg = new SendGrid((String) exchange.getIn().getHeader("sendgridApi"));
   
        Request request = new Request();
        try {
            request.method = Method.POST;
            request.endpoint = "mail/send";
            request.body = mail.build();
            Response response = sg.api(request);
            System.out.println(response.statusCode);
            System.out.println(response.body);
            System.out.println(response.headers);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            // throw ex;
        }
        System.out.println(notif.getEmail());

        // do something with the payload and/or exchange here
        exchange.getIn().setBody("Changed body");
    }
}
