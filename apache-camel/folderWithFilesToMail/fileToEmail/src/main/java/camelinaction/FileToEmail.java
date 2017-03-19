package camelinaction;

import org.apache.camel.CamelContext;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class FileToEmail {

	public static void main(String args[]) throws Exception {
		// create CamelContext
		CamelContext context = new DefaultCamelContext();

		// add our route to the CamelContext
		context.addRoutes(new RouteBuilder() {
			public void configure() {



				from("direct:report")
					.setHeader(Exchange.FILE_NAME, constant("report.txt"))
					.to("file:target/reports");


				from("direct:start")
					.to("ahc:https://randomuser.me/api/")
					.setHeader(Exchange.FILE_NAME, constant("message.html"))
					.to("file:folderWithFilesToMail");
			}
		});

		// start the route and let it do its work
		context.start();
		Thread.sleep(10000);

		// stop the CamelContext
		context.stop();
	}
}
