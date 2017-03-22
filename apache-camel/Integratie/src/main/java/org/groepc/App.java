package org.groepc;

import org.apache.camel.Main;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.camel.model.rest.RestBindingMode;

import java.io.FileInputStream;
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

			// load a properties file
			InputStream input = new FileInputStream("config.properties");
			this.prop.load(input);


			// setup rest configuration
			restConfiguration()
					.component("jetty")
					.host("localhost")
					.port(8080)
					.bindingMode(RestBindingMode.json)
					.dataFormatProperty("prettyPrint", "true");

			// define rest endpoints
			rest("/api")
					.description("Notification API")
					.consumes("application/json")
					.produces("application/json")

					.get("/notify").to("direct:process");

			// define routes
//			from("direct:hello")
//					.setBody()
//					.simple("Hello World Camel fired at ${header.firedTime}")
//					.to("stream:out");

			from("direct:process")
					.to("log:org.groepc.app?level=DEBUG&showAll=true&multiline=true")
					.setBody(constant("{\"resp\": \"hello,summit\"}"));

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

		@Override
		public void afterStart(MainSupport main) {
			System.out.println("MainExample with Camel is now started!");
		}

		@Override
		public void beforeStop(MainSupport main) {
			System.out.println("MainExample with Camel is now being stopped!");
		}
	}
}