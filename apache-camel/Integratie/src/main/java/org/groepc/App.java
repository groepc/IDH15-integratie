package org.groepc;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

/**
 * Hello world!
 */
public class App {

    public static void main(String[] args) throws Exception {
        // create CamelContext
        CamelContext context = new DefaultCamelContext();

        // add our route to the CamelContext
        context.addRoutes(new RouteBuilder() {
            public void configure() {

                
                from("timer://myTimer?period=2000")
                        .setBody()
                        .simple("Hello World Camel fired at ${header.firedTime}")
                        .to("stream:out");

                from("timer://foo?fixedRate=true&delay=0&period=10000")
                        .to("https://randomuser.me/api/")
                        .setHeader(Exchange.FILE_NAME, constant("message.html"))
                        //.to("stream:out");
                        .to("file:target");

            }
        });

        // start the route and let it do its work
        context.start();

        Thread.sleep(10000);

        // stop the CamelContext
        context.stop();

        System.out.println("== Done ==");
    }
}
