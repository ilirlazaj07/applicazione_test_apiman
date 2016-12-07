package it.capgemini.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axiom.util.UIDGenerator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.opensaml.Configuration;
import org.opensaml.DefaultBootstrap;
import org.opensaml.common.SAMLVersion;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.AuthnContextClassRef;
import org.opensaml.saml2.core.AuthnContextComparisonTypeEnumeration;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.core.Issuer;
import org.opensaml.saml2.core.LogoutRequest;
import org.opensaml.saml2.core.NameID;
import org.opensaml.saml2.core.NameIDPolicy;
import org.opensaml.saml2.core.RequestAbstractType;
import org.opensaml.saml2.core.RequestedAuthnContext;
import org.opensaml.saml2.core.Response;
import org.opensaml.saml2.core.SessionIndex;
import org.opensaml.saml2.core.impl.AuthnContextClassRefBuilder;
import org.opensaml.saml2.core.impl.AuthnRequestBuilder;
import org.opensaml.saml2.core.impl.IssuerBuilder;
import org.opensaml.saml2.core.impl.LogoutRequestBuilder;
import org.opensaml.saml2.core.impl.NameIDBuilder;
import org.opensaml.saml2.core.impl.NameIDPolicyBuilder;
import org.opensaml.saml2.core.impl.RequestedAuthnContextBuilder;
import org.opensaml.saml2.core.impl.SessionIndexBuilder;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Marshaller;
import org.opensaml.xml.io.MarshallingException;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.util.Base64;
import org.opensaml.xml.util.XMLHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class SamlConsumerManager {

	private String consumerUrl = null;
	private String authReqRandomId = Integer.toHexString(new Double(Math.random()).intValue());
	private String relayState = null;
	private String issuerId = null;
	private String idpUrl = null;
	private String attribIndex = null;
	private static Log log = LogFactory.getLog(SamlConsumerManager.class);
	
	public SamlConsumerManager(ServletConfig servletConfig) throws ConfigurationException {
		
		consumerUrl = Util.getConfiguration(servletConfig, "ConsumerUrl");
		idpUrl = Util.getConfiguration(servletConfig, "IdpUrl");
		issuerId = Util.getConfiguration(servletConfig, "Issuer");
		attribIndex = Util.getConfiguration(servletConfig, "AttributeConsumingServiceIndex");
		
		/* Inizializzazione della OpenSAML library, caricamento impostazioni di default */
		DefaultBootstrap.bootstrap();
	}

	/**
	 * 
         * Ritorna la redirection URL con appesa la richiesta SAML2
     * @param request
     * @return 
	 */
	public String buildRequestMessage(HttpServletRequest request) {

		RequestAbstractType requestMessage = null;

		// Creazione del messaggio della richiesta di autenticazione
		if (request.getParameter("logout") == null) {
			requestMessage = buildAuthnRequestObject();

		} else { // Logout singolo
			requestMessage = buildLogoutRequest((String) request.getSession().getAttribute("user"));
		}

		String encodedRequestMessage = null;
		try {
			encodedRequestMessage = encodeRequestMessage(requestMessage);
		} catch (MarshallingException e) {
		} catch (IOException e) {
		}

		/* La richiesta di autenticazione SAML2 viene appesa alla URL dell IP */
		return idpUrl + "?SAMLRequest=" + encodedRequestMessage + "&RelayState=" +
		       relayState;
	}

	private LogoutRequest buildLogoutRequest(String user) {

		LogoutRequest logoutReq = new LogoutRequestBuilder().buildObject();

		logoutReq.setID(Util.createID());

		DateTime issueInstant = new DateTime();
		logoutReq.setIssueInstant(issueInstant);
		logoutReq.setNotOnOrAfter(new DateTime(issueInstant.getMillis() + 5 * 60 * 1000));

		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issuer = issuerBuilder.buildObject();
		issuer.setValue(issuerId);
		logoutReq.setIssuer(issuer);

		NameID nameId = new NameIDBuilder().buildObject();
		nameId.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:entity");
		nameId.setValue(user);
		logoutReq.setNameID(nameId);

		SessionIndex sessionIndex = new SessionIndexBuilder().buildObject();
		sessionIndex.setSessionIndex(UIDGenerator.generateUID());
		logoutReq.getSessionIndexes().add(sessionIndex);

		logoutReq.setReason("Logout singolo");

		return logoutReq;
	}

	private AuthnRequest buildAuthnRequestObject() {

		/* Costruzione dello Issuer object */
		IssuerBuilder issuerBuilder = new IssuerBuilder();
		Issuer issuer =
		                issuerBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion",
		                                          "Issuer", "samlp");
		issuer.setValue(issuerId); 

		/* NameIDPolicy */
		NameIDPolicyBuilder nameIdPolicyBuilder = new NameIDPolicyBuilder();
		NameIDPolicy nameIdPolicy = nameIdPolicyBuilder.buildObject();
		nameIdPolicy.setFormat("urn:oasis:names:tc:SAML:2.0:nameid-format:persistent");
		nameIdPolicy.setSPNameQualifier("Isser");
		nameIdPolicy.setAllowCreate(true);

		/* AuthnContextClass */
		AuthnContextClassRefBuilder authnContextClassRefBuilder = new AuthnContextClassRefBuilder();
		AuthnContextClassRef authnContextClassRef =
		                                            authnContextClassRefBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:assertion",
		                                                                                    "AuthnContextClassRef",
		                                                                                    "saml");
		authnContextClassRef.setAuthnContextClassRef("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");

		/* AuthnContex */
		RequestedAuthnContextBuilder requestedAuthnContextBuilder =
		                                                            new RequestedAuthnContextBuilder();
		RequestedAuthnContext requestedAuthnContext = requestedAuthnContextBuilder.buildObject();
		requestedAuthnContext.setComparison(AuthnContextComparisonTypeEnumeration.EXACT);
		requestedAuthnContext.getAuthnContextClassRefs().add(authnContextClassRef);

		DateTime issueInstant = new DateTime();

		/* Creazione del AuthRequestObject */
		AuthnRequestBuilder authRequestBuilder = new AuthnRequestBuilder();
		AuthnRequest authRequest =
		                           authRequestBuilder.buildObject("urn:oasis:names:tc:SAML:2.0:protocol",
		                                                          "AuthnRequest", "samlp");
		authRequest.setForceAuthn(false);
		authRequest.setIsPassive(false);
		authRequest.setIssueInstant(issueInstant);
		authRequest.setProtocolBinding("urn:oasis:names:tc:SAML:2.0:bindings:HTTP-POST");
		authRequest.setAssertionConsumerServiceURL(consumerUrl);
		authRequest.setIssuer(issuer);
		authRequest.setNameIDPolicy(nameIdPolicy);
		authRequest.setRequestedAuthnContext(requestedAuthnContext);
		authRequest.setID(authReqRandomId);
		authRequest.setVersion(SAMLVersion.VERSION_20);

		/* Richiesta deli attributi, l indice viene registrato nell IDP */
		if (attribIndex != null && !attribIndex.equals("")) {
			authRequest.setAttributeConsumingServiceIndex(Integer.parseInt(attribIndex));
		}

		return authRequest;
	}

	private String encodeRequestMessage(RequestAbstractType requestMessage)
	                                                                       throws MarshallingException,
	                                                                       IOException {

		Marshaller marshaller = Configuration.getMarshallerFactory().getMarshaller(requestMessage);
		Element authDOM = marshaller.marshall(requestMessage);

		Deflater deflater = new Deflater(Deflater.DEFLATED, true);
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		DeflaterOutputStream deflaterOutputStream =
		                                            new DeflaterOutputStream(byteArrayOutputStream,
		                                                                     deflater);

		StringWriter rspWrt = new StringWriter();
		XMLHelper.writeNode(authDOM, rspWrt);
		deflaterOutputStream.write(rspWrt.toString().getBytes());
		deflaterOutputStream.close();

		/* Encoding del messaggio compresso */
		String encodedRequestMessage =
		                               Base64.encodeBytes(byteArrayOutputStream.toByteArray(),
		                                                  Base64.DONT_BREAK_LINES);
		return URLEncoder.encode(encodedRequestMessage, "UTF-8").trim();
	}

	public Map<String, String> processResponseMessage(String responseMessage) {

		XMLObject responseXmlObj = null;

		try {
			responseXmlObj = unmarshall(responseMessage);

		} catch (ConfigurationException e) {
		} catch (ParserConfigurationException e) {
		} catch (SAXException e) {
		} catch (IOException e) {
		} catch (UnmarshallingException e) {
		}

		return getResult(responseXmlObj);
	}

	private XMLObject unmarshall(String responseMessage) throws ConfigurationException,
	                                                    ParserConfigurationException, SAXException,
	                                                    IOException, UnmarshallingException {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder docBuilder = documentBuilderFactory.newDocumentBuilder();

		byte[] base64DecodedResponse = Base64.decode(responseMessage);
		
		log.info("Response: " + new String(base64DecodedResponse));
		
		ByteArrayInputStream is = new ByteArrayInputStream(base64DecodedResponse);

		Document document = docBuilder.parse(is);
		Element element = document.getDocumentElement();
		UnmarshallerFactory unmarshallerFactory = Configuration.getUnmarshallerFactory();
		Unmarshaller unmarshaller = unmarshallerFactory.getUnmarshaller(element);
		return unmarshaller.unmarshall(element);

	}

	/*
	 * Gestione della risposta e ritorno dei risultati
	 */
	private Map<String, String> getResult(XMLObject responseXmlObj) {

		if (responseXmlObj.getDOM().getNodeName().equals("saml2p:LogoutResponse")) {
			log.info("SAML Logout response received");
			return null;
		}

		Response response = (Response) responseXmlObj;
		log.info("SAML Response: "+ response);
                System.out.println("SAML Response: "+ response);
		Assertion assertion = response.getAssertions().get(0);
		Map<String, String> resutls = new HashMap<String, String>();

		
		if (assertion != null) {

			String subject = assertion.getSubject().getNameID().getValue();
			resutls.put("Subject", subject); // Ottieni il subject

			List<AttributeStatement> attributeStatementList = assertion.getAttributeStatements();

			if (attributeStatementList != null) {
                            System.out.println("BECCATO ATTRIBUTO !!! ---> " + attributeStatementList.size());
				// attributi dello user esistenti e ricevuti
				Iterator<AttributeStatement> attribStatIter = attributeStatementList.iterator();
				while (attribStatIter.hasNext()) {
					AttributeStatement statment = attribStatIter.next();
					List<Attribute> attributesList = statment.getAttributes();
					Iterator<Attribute> attributesIter = attributesList.iterator();
					while (attributesIter.hasNext()) {
						Attribute attrib = attributesIter.next();
						Element value = attrib.getAttributeValues().get(0).getDOM();
						String attribValue = value.getTextContent();
                                                System.out.println("BECCATO ATTRIBUTO !!!: " + attribValue);
						resutls.put(attrib.getName(), attribValue);
					}
                                       
				}
			}
		}
		return resutls;
	}
}
