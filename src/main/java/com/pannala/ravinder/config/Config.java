package com.pannala.ravinder.config;

import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

	@Bean
	public JaxbDataFormat jaxbDataFormat() {
		JaxbDataFormat jaxbDataFormat = new JaxbDataFormat(true);
		jaxbDataFormat.setContextPath("com.camel.jaxB.example");
		return jaxbDataFormat;

	}
}

/*private void ordersDispachQueue() {
		onException(Exception.class).to(NoOrdersQueue);
		System.out.println("Please come to CamelRouter-----------------------------------");
		from(queueIn).routeId("ORDERS DISPACH").process(exchange-> {
			String order = (String) exchange.getIn().getBody();
			if(!order.contains("orders")) {
				throw new Exception("No orders Found");
			}
		})
		//.unmarshal(JaxbDataFormat)
		.split(xpath("//order"))
		.log("Orders Spiltted")
		.setHeader("pincode", xpath("//deliveryAddress/postcode/text()", String.class))
		.log("Variable      = ${in.header.pincode}")
		.convertBodyTo(String.class).log(LoggingLevel.DEBUG, "${body}")
		.choice()
		.when(xpath("//deliveryAddress/postcode=500018"))
		.to(areawise_queue_1)
		.when(xpath("//deliveryAddress/postcode=500016"))
		.to(areawise_queue_2).otherwise()
		.to(areawise_queue_3).endChoice().end();
		/*.when(header("pincode").isEqualTo("500018"))
		        .log("body        = ${body}")
				.to(queueOut_1)
				.when(header("pincode").isEqualTo("500016"))
			    .to(queueOut_2)
			    .otherwise()
			    .to(queueOut_3)
			    .endChoice()
			    .end();*/
	

