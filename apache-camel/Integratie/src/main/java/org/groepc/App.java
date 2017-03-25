package org.groepc;

import org.apache.camel.Exchange;
import org.apache.camel.Main;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.camel.model.rest.RestBindingMode;
import com.sendgrid.*;
import org.apache.camel.processor.aggregate.AggregationStrategy;

import javax.management.Notification;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class App {

    private Main main;

    public static void main(String[] args) throws Exception {
        App example = new App();
        example.boot();
    }

    public void boot() throws Exception {
        // create a Main instance
        main = new Main();
        // add routes
        main.addRouteBuilder(new MyRouteBuilder());
        // add event listener
        main.addMainListener(new Events());
        // set the properties from a file
        // main.setPropertyPlaceholderLocations("example.properties");
        // run until you terminate the JVM
        System.out.println("Starting Camel. Use ctrl + c to terminate the JVM.\n");
        main.run();
    }

    private static class MyRouteBuilder extends RouteBuilder {

        private Properties prop = new Properties();

        @Override
        public void configure() throws Exception {

            // set how to handle errors
            errorHandler(deadLetterChannel("mock:error"));

            // load a properties/configuration file
            InputStream input = new FileInputStream("config.properties");
            this.prop.load(input);
            String host = prop.getProperty("host");
            Integer port = Integer.parseInt(prop.getProperty("port"));

            /**
             * Setup REST configuration
             */
            restConfiguration()
                    .component("jetty")
                    .host(host)
                    .port(port)
                    .bindingMode(RestBindingMode.json)
                    .dataFormatProperty("prettyPrint", "true");

            /**
             * Define our REST API routes, so we can call a Camel route through
             * an HTTP request
             */
            rest("/api")
                    .description("Notification API")
                    .consumes("application/json")
                    .produces("application/json")
                    .get("/status")
                    .to("direct:status")
                    .post("/notify").type(NotificationPojo.class)
                    .to("direct:process");

            /**
             * Define our Camel specific routes
             */
            // kick-off processing of notification
            from("direct:process")
                    .to("log:org.groepc.app?level=DEBUG&showAll=true&multiline=true")
                    .setHeader("sendgridApi", simple(prop.getProperty("sendgrid_api"), String.class))
                    .to("direct:callBuienradar")
                    .to("direct:processData")
                    .to("direct:generateMessage");
                    //.to("direct:sendMail");

            from("direct:callBuienradar")
                    .process(new CallBuienradar());

            from("direct:processData")
                    .process(new ProcessData());

            from("direct:generateMessage")
                    .process(new GenerateMessage());
            from("direct:sendMail")
                    .process(new SendMail());

            // Status route
            from("direct:status")
                    .setBody(constant("{\"status\": \"running now!\"}"));

        }
    }

    public static class Events extends MainListenerSupport {

        private Properties prop = new Properties();

        @Override
        public void afterStart(MainSupport main) {

            // load a properties/configuration file
            InputStream input = null;
            try {
                input = new FileInputStream("config.properties");
                this.prop.load(input);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            String host = prop.getProperty("host");
            Integer port = Integer.parseInt(prop.getProperty("port"));

            System.out.println("MainExample with Camel is now started on http://" + host + ":" + port);
        }

        @Override
        public void beforeStop(MainSupport main) {
            System.out.println("MainExample with Camel is now being stopped!");
        }
    }
}
