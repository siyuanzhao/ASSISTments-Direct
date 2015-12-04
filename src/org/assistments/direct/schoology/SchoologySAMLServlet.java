package org.assistments.direct.schoology;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyFactory;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.saml2.core.impl.ResponseImpl;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.Unmarshaller;
import org.opensaml.xml.io.UnmarshallerFactory;
import org.opensaml.xml.io.UnmarshallingException;
import org.opensaml.xml.parse.BasicParserPool;
import org.opensaml.xml.parse.XMLParserException;
import org.opensaml.xml.security.x509.BasicX509Credential;
import org.opensaml.xml.signature.SignatureValidator;
import org.opensaml.xml.validation.ValidationException;
import org.opensaml.xml.signature.Signature;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.assistments.connector.exception.ReferenceNotFoundException;
import org.assistments.direct.LiteUtility;
import org.assistments.service.domain.User;


/**
 * Servlet implemeation class SchoologyAppsLandingPage
 */

@WebServlet("/SchoologySAMLServlet")
public class SchoologySAMLServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     our_api_secret")
                .build();
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		
		String samlResponse = request.getParameter("SAMLResponse");
		String relayState = request.getParameter("RelayState");

		if(relayState!=null)
			session.setAttribute("sectionID",getRealmID(relayState));

		if(samlResponse!=null)	//SAML Response received
		{
			Base64 base64 = new Base64();
			String xmlStr = new String(base64.decode(samlResponse.getBytes()));
			ResponseImpl SAMLResponse;
			SchoologyAPI sapi = null;
			try {
				SAMLResponse = samlResponse(xmlStr);
				sapi = getUserFromSAML(SAMLResponse);
			} catch (ConfigurationException | XMLParserException | UnmarshallingException | CertificateException | InvalidKeySpecException | NoSuchAlgorithmException | URISyntaxException | UnrecoverableKeyException | KeyStoreException | ReferenceNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.println(xmlStr);
			
			session.setAttribute("sapi", sapi);
			
			if(sapi.isSendRedirect()) // User doesn't exist in database; need redirection
			{
				response.sendRedirect(sapi.getAuthUrl());	//redirect to get authorization
			}
			else //User exists in database; no need for redirection; go to apps landing page
			{
				retrieveAccessToken(sapi, (String) session.getAttribute("sectionID"));	// get access token from database
				response.sendRedirect(SchoologyAPI.appsLandingPageURL);
			}
				
		}
		else //Redirected after creating a new user
		{
			SchoologyAPI sapi = (SchoologyAPI)session.getAttribute("sapi");
			retrieveAccessToken(sapi, (String) session.getAttribute("sectionID")); // get access token from schoology
			response.sendRedirect(SchoologyAPI.appsLandingPageURL);
		}
	}
	public void retrieveAccessToken(SchoologyAPI sapi, String sectionID)
	{
		try {
			sapi.retrieveAccessTokenAndConnectAssistmentsUser(sectionID);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getRealmID(String relayState)
	{
		List<NameValuePair> params = null;
		try {
			params = URLEncodedUtils.parse(new URI(relayState), "UTF-8");
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, String> map = new HashMap<String, String>();
		for (NameValuePair el : params) {
			map.put(el.getName(), el.getValue());
		}  
		return map.get("realm_id");
	}
	public ResponseImpl samlResponse(String uncryptedXML) throws ConfigurationException, XMLParserException, UnmarshallingException, CertificateException, IOException, InvalidKeySpecException, NoSuchAlgorithmException, URISyntaxException, UnrecoverableKeyException, KeyStoreException
	{
		  ResponseImpl samlResponse=null;
		  BasicParserPool ppMgr=new BasicParserPool();
		  ppMgr.setNamespaceAware(true);
		  ByteArrayInputStream bais=new ByteArrayInputStream(uncryptedXML.getBytes());
		  Document inCommonMDDoc=ppMgr.parse(bais);
		  Element metadataRoot=inCommonMDDoc.getDocumentElement();
		  DefaultBootstrap.bootstrap();
		  UnmarshallerFactory unmarshallerFactory=org.opensaml.xml.Configuration.getUnmarshallerFactory();
		  Unmarshaller unmarshaller=unmarshallerFactory.getUnmarshaller(metadataRoot);
		  XMLObject xmlObject=unmarshaller.unmarshall(metadataRoot);
		  samlResponse=(ResponseImpl)xmlObject;
		  System.out.println(samlResponse.getIssueInstant().toString());
		  System.out.println("Signature Reference ID: " + samlResponse.getSignatureReferenceID().toString());
		//grab the certificate file
		  File certificateFile = new File(getClass().getResource("app.schoology.com.crt").toURI()); 
		//get the certificate from the file
		  InputStream inputStream2 = new FileInputStream(certificateFile);
		  CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		  X509Certificate certificate = (X509Certificate)certificateFactory.generateCertificate(inputStream2);
		  inputStream2.close();
		  
		//pull out the public key part of the certificate into a KeySpec
		  X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(certificate.getPublicKey().getEncoded());

		  //get KeyFactory object that creates key objects, specifying RSA
		  KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		  System.out.println("Security Provider: " + keyFactory.getProvider().toString());

		  //generate public key to validate signatures
		  PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

		  //we have the public key
		  System.out.println("Public Key created");

		  //create credentials
		  BasicX509Credential publicCredential = new BasicX509Credential();
		  
		//add public key value
		  publicCredential.setPublicKey(publicKey);

		  //create SignatureValidator
		  SignatureValidator signatureValidator = new SignatureValidator(publicCredential);

		  //get the signature to validate from the response object
		  Signature signature = samlResponse.getSignature();

		  //try to validate
		  try 
		  {
		  signatureValidator.validate(signature);
		  }
		  catch (ValidationException ve) 
		  {
		  System.out.println("Signature is NOT valid.");
		  System.out.println(ve.getMessage());
		  }
		  
		//no validation exception was thrown
		  System.out.println("Signature is valid."); 

		  return samlResponse;
	}
	public SchoologyAPI getUserFromSAML(ResponseImpl samlResponse) throws ReferenceNotFoundException
	{
		User user = null;
		String name_first = null;
		String name_last = null;
		String name_display = null;
		String role_name = null;
		String is_admin = null;
		String uid = null;
		String domain = null;
		List<Assertion> assertions = samlResponse.getAssertions();
		  for (Assertion assertion : assertions) 
		  {
		        if (assertion.getAttributeStatements().size() != 0) 
		        {
		          for(AttributeStatement attributeStatement : assertion.getAttributeStatements())
		          {
		        	  List<Attribute> attributes = attributeStatement.getAttributes();
		        	  for (Attribute attribute: attributes)
		        	  {
		        		  switch (attribute.getName())
		        		  {
			        		  case "uid": uid = attribute.getAttributeValues().get(0).getDOM().getTextContent();
			        			  	break;
			        		  case "name_display": name_display = attribute.getAttributeValues().get(0).getDOM().getTextContent();
			        			  	break;
			        		  case "name_first":name_first = attribute.getAttributeValues().get(0).getDOM().getTextContent();
			        			  	break;
			        		  case "name_last": name_last = attribute.getAttributeValues().get(0).getDOM().getTextContent();
			        			  	break;
			        		  case "role_name": role_name = attribute.getAttributeValues().get(0).getDOM().getTextContent();
			        			  	break;
			        		  case "is_admin": is_admin = attribute.getAttributeValues().get(0).getDOM().getTextContent();
			        			  	break;
			        		  case "domain": domain = attribute.getAttributeValues().get(0).getDOM().getTextContent();
			        			  	break;
		        			  default: System.out.println(attribute.getName()+":"+attribute.getAttributeValues().get(0).getDOM().getTextContent());			
			        				break;
		        		  }
		        	  }
		          }
		        }
		  }
		  boolean isTeacher = false;
		  if(role_name.equals("Teacher"))
			  isTeacher = true;
		  else if(role_name.equals("Student"))
			  isTeacher = false;
		  
		  if (isTeacher)
			  user  = LiteUtility.populateTeacherInfo(name_first, name_last,name_display);
		  else
			  user = LiteUtility.populateStudentInfo(name_first, name_last, name_display);
		  
        SchoologyAPI sapi = new SchoologyAPI("https://"+domain, uid, user, isTeacher);
		 
		return sapi;
	}
}
