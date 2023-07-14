package com.globecapital.business.wcf.soap;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;

import org.w3c.dom.NodeList;

import com.globecapital.business.wcf.helper.WCFHelper;
import com.msf.log.Logger;

public class SoapReqResHandler {


	private static Logger log = Logger.getLogger(SoapReqResHandler.class);

	public static String element1 = null;

	public static String element2 = null;

	public static String responseElement = null;

	private static void createSoapEnvelope(SOAPMessage soapMessage, String request) throws SOAPException {
		SOAPPart soapPart = soapMessage.getSOAPPart();

		// SOAP Envelope
		SOAPEnvelope envelope = soapPart.getEnvelope();
		envelope.setPrefix("soapenv");
		envelope.removeChild(envelope.getHeader());
		envelope.removeAttribute("xmlns:SOAP-ENV");
		envelope.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
		envelope.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");

		// SOAP Body
		SOAPBody soapBody = envelope.getBody();
		soapBody.setPrefix("soapenv");
		soapBody.addNamespaceDeclaration("xmlns", "http://tempuri.org/");
		SOAPElement soapBodyElem = soapBody.addChildElement(element1);

		SOAPElement soapBodyElem1 = soapBodyElem.addChildElement(element2);
		soapBodyElem1.addTextNode(request);
		log.debug("SOAP WCF Request = " + request);
	}

	public static String callSoapWebService(String soapEndpointUrl, String soapAction, String request) {

		String response = null;
		try {
			WCFHelper.setElements(soapAction);

			// Create SOAP Connection
			SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
			SOAPConnection soapConnection = soapConnectionFactory.createConnection();
			log.info(soapEndpointUrl);
			// Send SOAP Message to SOAP Server
			SOAPMessage soapResponse = soapConnection.call(createSOAPRequest(soapAction, request), soapEndpointUrl);
			response = handleSoapResponse(soapResponse);

			soapConnection.close();
		} catch (Exception e) {
			log.error(
					"\nError occurred while sending SOAP Request to Server!\nMake sure you have the correct endpoint URL and SOAPAction!\n");
			//e.printStackTrace();
		}
		return response;
	}

	private static String handleSoapResponse(SOAPMessage soapResponse) throws SOAPException {
		SOAPBody soapBody = soapResponse.getSOAPBody();
		NodeList list = soapBody.getElementsByTagName(responseElement);

		String result = list.item(0).getTextContent();
		log.debug("SOAP WCF Response = " + result);
		return result;

	}

	private static SOAPMessage createSOAPRequest(String soapAction, String request) throws Exception {
		MessageFactory messageFactory = MessageFactory.newInstance();
		SOAPMessage soapMessage = messageFactory.createMessage();
		soapMessage.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");

		createSoapEnvelope(soapMessage, request);

		MimeHeaders headers = soapMessage.getMimeHeaders();
		headers.addHeader("SOAPAction", "\"" + soapAction + "\"");
		headers.addHeader("User-Agent", "Axis/1.4");
		headers.addHeader("Accept", "application/soap+xml,application/dime,multipart/related,text/*");

		soapMessage.saveChanges();

		return soapMessage;
	}
}
