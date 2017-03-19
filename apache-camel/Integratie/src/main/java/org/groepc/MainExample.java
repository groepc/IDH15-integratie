package org.groepc;

import org.apache.camel.Exchange;
import org.apache.camel.Main;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

public class MainExample {

	private Main main;

	public static void main(String[] args) throws Exception {
		MainExample example = new MainExample();
		example.boot();
	}

	public void boot() throws Exception {
		// create a Main instance
		main = new Main();
		// bind MyBean into the registry
		main.bind("foo", new MyBean());
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

			InputStream input = new FileInputStream("config.properties");

			// load a properties file
			this.prop.load(input);

			String sendgridApi = prop.getProperty("sendgrid_api");
			String toMail = prop.getProperty("to_mail");

//			from("timer://myTimer?period=2000")
//					.setBody()
//					.simple("Hello World Camel fired at ${header.firedTime}")
//					.to("stream:out");

			from("timer://myTimer?period=5000")
					.setHeader("subject", simple("Duimen omhoog!!"))
					.to("smtp://apikey@smtp.sendgrid.net:587?password=" + sendgridApi + "&to=" + toMail + "&from=camellover@gmail.com")
					.to("stream:out");

//			from("timer:foo?delay=1000")
//					.process(new Processor() {
//						public void process(Exchange exchange) throws Exception {
//							System.out.println("Invoked timer at " + new Date());
//						}
//					})
//					.bean("foo");
		}
	}

	public static class MyBean {
		public void callMe() {
			System.out.println("MyBean.callMe method has been called");
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