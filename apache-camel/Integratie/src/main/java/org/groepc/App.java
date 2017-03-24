package org.groepc;

import org.apache.camel.Exchange;
import org.apache.camel.Main;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.camel.model.rest.RestBindingMode;
import com.sendgrid.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
        // bind MyBean into the registry
        main.bind("mySqlBean", new MySqlBean());
        // add routes
        main.addRouteBuilder(new MyRouteBuilder());
        // add event listener
        main.addMainListener(new Events());
        // set the properties from a file
//		main.setPropertyPlaceholderLocations("example.properties");
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
                    //.to("direct:callBuienradar")
                    //.to("direct:sendMail")
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            NotificationPojo notif = exchange.getIn().getBody(NotificationPojo.class);
                            System.out.println(notif.getEmail());

                            Email from = new Email("camellover@gmail.com");
                            String subject = "Is het fiets weer!";
                            Email to = new Email(notif.getEmail());
                            Content content = new Content("text/html", "Beste ...<br><br>Het is vandaag fietsweer!!! Pak je fiets en fiets er op los.<br><br>Met vriendelijke groet, <br>Is het fiets weer");
                            Mail mail = new Mail(from, subject, to, content);

                            System.out.println(exchange.getProperty("sendgridApi"));
                            SendGrid sg = new SendGrid(prop.getProperty("sendgridApi"));
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
                    })
                    .process(new Processor() {
                        public void process(Exchange exchange) throws Exception {
                            String myString = exchange.getIn().getBody(String.class);
                            System.out.println(myString);
                        }
                    })
                    .setBody(constant("{\"resp\": \"hello,summit\"}"));

            // TODO: call buienradar API
            // TODO: send notification
            // Status route
            from("direct:status")
                    .setBody(constant("{\"status\": \"running now!\"}"));

            /*
            from("direct:sendMail")
                    .setHeader("subject", simple("Duimen omhoog!!"))
                    .to("smtp://apikey@smtp.sendgrid.net:587?password=" +  + "&to=" + toMail + "&from=camellover@gmail.com")
                    .to("stream:out");
*/

//			from("direct:hello")
//					.setBody()
//					.simple("Hello World Camel fired at ${header.firedTime}")
//					.to("stream:out");
//			from("scheduler://defaultTimer?delay=5&timeUnit=SECONDS").to("bean:mySqlBean?method=requestDb");
//			from("timer://myTimer?period=2000")
//					.setBody()
//					.simple("Hello World Camel fired at ${header.firedTime}")
//					.to("stream:out");
//			from("timer://myTimer?period=5000")
//					.setHeader("subject", simple("Duimen omhoog!!"))
//					.to("smtp://apikey@smtp.sendgrid.net:587?password=" + sendgridApi + "&to=" + toMail + "&from=camellover@gmail.com")
//					.to("stream:out");
//			String sendgridApi = prop.getProperty("sendgrid_api");
//			String toMail = prop.getProperty("to_mail");
//
//			from("timer://foo?fixedRate=true&delay=0&period=10000")
//					.to("https://randomuser.me/api/")
//					.setHeader(Exchange.FILE_NAME, constant("message.html"))
//					//.to("stream:out");
//					.to("file:target");
//			from("timer:foo?delay=1000")
//					.process(new Processor() {
//						public void process(Exchange exchange) throws Exception {
//							System.out.println("Invoked timer at " + new Date());
//						}
//					})
//					.bean("foo");
        }
    }

    public static class MySqlBean {

        public void callMe() {
            System.out.println("MyBean.callMe method has been called");
        }

        public void requestDb() {
            System.out.println("MySqlBean.requestDB method has been called");
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
