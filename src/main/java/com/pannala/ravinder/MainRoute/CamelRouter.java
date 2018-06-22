package com.pannala.ravinder.MainRoute;

import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JaxbDataFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pannala.ravinder.config.Constant;
import com.pannala.ravinder.filter.DeliveryAddressPredicate;
import com.pannala.ravinder.processor.AreawiseDispachedProcessor;
import com.pannala.ravinder.processor.InvoiceProcessor;
import com.pannala.ravinder.processor.OrderDeliveredProcessor;
import com.pannala.ravinder.processor.PaymentProcessor;
import com.pannala.ravinder.processor.SendEmailProcessor;

@Component
public class CamelRouter extends RouteBuilder {

	final static Logger LOG = LoggerFactory.getLogger(CamelRouter.class);

	@Value("${input_queue}")
	private String queueIn;

	@Value("${areawise_queue_1}")
	private String areawise_queue_1;

	@Value("${areawise_queue_2}")
	private String areawise_queue_2;

	@Value("${areawise_queue_3}")
	private String areawise_queue_3;
	
	@Value("${no_orders_queue}")
	private String NoOrdersQueue;
	
	@Value("${file.input.endpoint}")
	private String fileInput;
	
	@Value("${file.output.endpoint}")
	private String fileOutput;
	
	@Value("${orderplacedqueue}")
	private String orderPlacedQueue;
	
	@Value("${ordercancelqueue}")
	private String orderCancelQueue;
	
	@Value("${orderunavailable}")
	private String orderUnavailable;
	
	@Value("${out_queue}")
	private String outqueue;
	
	@Value("${vehicle_queue}")
	private String vehicleQueue;
	
	@Value("${payment_cod}")
	private String codQueue;
	
	@Value("${payment_credit}")
	private String creditQueue;
	
	@Value("${payment_debit}")
	private String debitQueue;
	
	@Value("${payment_queue}")
	private String paymentqueue;
	
	@Value("${invoice_queue}")
	private String invoicequeue;
	
	@Value("${email.uri}")
	private String emailUri;
	

	@Autowired
	private OrderDeliveredProcessor orderDeliveredProcessor;
	
	@Autowired
	private AreawiseDispachedProcessor  areawiseDispachedProcessor;
	
	@Autowired
	private PaymentProcessor paymentProcessor;
	
	@Autowired
	private InvoiceProcessor invoiceProcessor;
	
	@Autowired
	private SendEmailProcessor sendEmailProcessor;

	@Autowired
	private JaxbDataFormat JaxbDataFormat;

	@Override
	public void configure() throws Exception {
		orderStatusDispachQueue();
		//orderCollectQueue();
		areawiseDispachedQueue();
		deliveredQueue();
		//sendMailQueue();
		paymentTypeQueue();
		invoiceCollectQueue();
	}
	
		
	private void orderStatusDispachQueue() {
		from(queueIn)
		.split(xpath("//order"))
		.log(LoggingLevel.INFO,"OrdersSeparated")
		.setHeader("orderStatus",xpath("//orderStatus" , String.class))
		.choice()
			.when(header("orderStatus").isEqualTo(Constant.ORDER_PLACED))
			.log("------ " + Constant.ORDER_PLACED +" ------")
			.to(orderPlacedQueue)
			.when(header("orderStatus").isEqualTo(Constant.ORDER_CANCEL))
			.log("------ " + Constant.ORDER_CANCEL +" ------")
			.to(orderCancelQueue)
			.otherwise()
			.log("------ " + Constant.ORDER_UNAVAILABLE +" ------")
			.to(orderUnavailable)
		.endChoice()
		.end();
	}
	
	/*private void orderCollectQueue() {
		from(orderPlacedQueue,orderCancelQueue,orderUnavailable)
		.log("-------- collect the orders -----")
		.to(vehicleQueue);
	}*/
	
	private void areawiseDispachedQueue() {
		from(orderPlacedQueue)
		.setHeader("pincode", xpath("//deliveryAddress/postcode/text()", String.class))
		.log("Variable      = ${in.header.pincode}")
		.convertBodyTo(String.class).log(LoggingLevel.DEBUG, "${body}")
		.process(areawiseDispachedProcessor)
		.choice()
			.when(xpath("//deliveryAddress/postcode=500018")).log("------ Separated 500018 area code Orders ------")
			.to(areawise_queue_1)
			.when(xpath("//deliveryAddress/postcode=500016")).log("------ Separated 500016 area code Orders ------")
			.to(areawise_queue_2)
			.otherwise().log(" ----- Separated Other area code Orders ------")
			.to(areawise_queue_3)
		.endChoice()
		.end();
	}
	
	private void deliveredQueue() {
		from(areawise_queue_2,areawise_queue_1,areawise_queue_3)
		.log("------ entering to the Deliver ----")
		.unmarshal(JaxbDataFormat)
		.filter(new DeliveryAddressPredicate())
		.marshal(JaxbDataFormat)
		.convertBodyTo(String.class).log(LoggingLevel.DEBUG, "${body}")
		.log("Converted to Xml")
		.process(e -> {
			String body=e.getIn().getBody().toString();
			System.out.println("order Information ------:"+body);
		})
		.log(LoggingLevel.DEBUG, "${body}")
		.to(outqueue)
		.unmarshal(JaxbDataFormat)
		.process(paymentProcessor)
		.marshal(JaxbDataFormat)
		.convertBodyTo(String.class).log(LoggingLevel.DEBUG, "${body}")
		.wireTap(paymentqueue);
	}
	
	private void sendMailQueue() {
		from(outqueue)
		.log("----enter into the mail-----")
		.unmarshal(JaxbDataFormat)
		.process(sendEmailProcessor)	
		.convertBodyTo(String.class).log(LoggingLevel.DEBUG, "${body}")
		.setHeader("subject", constant("Delivered: order #405-8628129-2601933"))
		.to(emailUri);
	}
	
	private void paymentTypeQueue() {
		from(paymentqueue)
		.log("--------- Enter into the Payment mode -------")
		.unmarshal(JaxbDataFormat)
		.process(orderDeliveredProcessor)
		.marshal(JaxbDataFormat)
		.convertBodyTo(String.class).log(LoggingLevel.DEBUG, "${body}")
		.setHeader("paymentMode",xpath("//payment/paymentMode/text()" , String.class))
		.choice()
			.when(header("paymentMode").isEqualTo(Constant.COD))
			.log("------ Separated Payment mode (COD) Orders ------")
			.to(codQueue)
			.when(header("paymentMode").isEqualTo(Constant.CC))
			.log("------ Separated Payment mode (CC) Orders ------")
			.to(creditQueue)
			.otherwise()
			.log(" ----- Separated Payment mode (DC) Orders ------")
			.to(debitQueue)
		.endChoice()
		.end();
	}
	
	private void invoiceCollectQueue() {
		from(codQueue,creditQueue,debitQueue)
		.log("------- Enter into invoice mode --------")
		.unmarshal(JaxbDataFormat)
		.process(invoiceProcessor)
		.marshal(JaxbDataFormat)
		.convertBodyTo(String.class).log(LoggingLevel.DEBUG, "${body}")
		.setHeader("invoiceStatus",xpath("//invoice/invoiceStatus/text()" , String.class))
		.choice()
		   .when(header("invoiceStatus").isEqualTo(Constant.INV_GENERATED))
		   .to(invoicequeue)
		   .otherwise()
		   .to(paymentqueue)
		.endChoice()
		.end();
	}
	
}