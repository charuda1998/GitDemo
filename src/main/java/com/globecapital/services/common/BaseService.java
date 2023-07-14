package com.globecapital.services.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.audit.GCAuditObject;
import com.globecapital.audit.GCAuditTransaction;

import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.config.UnitTesting;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidAPIResponseException;
import com.globecapital.services.exception.InvalidAppID;
import com.globecapital.services.exception.InvalidRequestException;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.Encryption;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

/**
 * Servlet implementation class BaseService
 */

public abstract class BaseService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	public static Logger log = Logger.getLogger(BaseService.class);
	public JSONObject cacheProperty = new JSONObject();

	/**
	 * @param req
	 * @param res 1. To enable all domain requests for CORS 2. takes #Origin from
	 *            XHR header and sets in response
	 */
	private void addOriginDomainToAllowOrigin(HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse) {

		String originDomain = httpServletRequest.getHeader("Origin");

		if (originDomain == null)
			originDomain = "*";

		httpServletResponse.setHeader("Access-Control-Allow-Origin", originDomain);
	}

	@Override
	protected void doOptions(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException, IOException {

		// TODO - This should be properly configured from properties. Should add more
		addOriginDomainToAllowOrigin(httpServletRequest, httpServletResponse); // enable CORS domain specific
																				// restriction.
		httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true"); // enable session sharing in CORS
		httpServletResponse.setHeader("Access-Control-Allow-Headers",
				"cache-control, content-type, DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type,Set-Cookie,origin,accept,X-ENCRYPT,x-encrypt");
		httpServletResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS"); // these methods will be
																								// allowed after
		// preflight
		httpServletResponse.setHeader("Access-Control-Expose-Headers", "Set-Cookie"); // to enable sharing session via
																						// cookie for
		// further requests from browser.
		super.doOptions(httpServletRequest, httpServletResponse);
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	protected String getRequestBody(HttpServletRequest request) throws InvalidRequestException {
		StringBuffer stringBuffer = new StringBuffer();
		String reqString = null;

		try {
			BufferedReader reader = request.getReader();
			int len;
			char[] chars = new char[4 * 1024];

			while ((len = reader.read(chars)) >= 0) {
				stringBuffer.append(chars, 0, len);
			}

		} catch (Exception e) {
			log.error("Error in reading Request : ", e);
			throw new InvalidRequestException();
		}

		reqString = stringBuffer.toString();

		if (reqString == null || reqString.length() == 0) {
			throw new InvalidRequestException();
		}
		return reqString;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		GCRequest gcRequest = null;
		GCResponse gcResponse = null;
		GCAuditObject auditObj = null;
		long requestTime = System.currentTimeMillis();

		try {
			
			String sRequestBody = getRequestBody(request);
			if(request.getHeader("X-ENCRYPT") == null || request.getHeader("X-ENCRYPT").equals("true")) {
				response.setHeader("X-ENCRYPT", "true");
				sRequestBody = decrypt(sRequestBody);
			}
			logRawRequest(sRequestBody, request);

			gcRequest = new GCRequest(sRequestBody);
			gcResponse = new GCResponse(gcRequest);

			gcResponse.setAppID(gcRequest.getAppID());
			gcResponse.setRequestReceiveTime(requestTime);
			gcRequest.setHttpRequest(request);
			gcResponse.setHttpResponse(response);

			auditObj = new GCAuditObject();
			gcRequest.setAuditObj(auditObj);
			
			logRequest(gcRequest);

			
			if(!checkMainenanceORut(gcRequest, gcResponse)){
				if (isValidAppID(gcRequest)) {
					process(gcRequest, gcResponse);
				}else {
					throw new InvalidAppID();
				}
			}
			
			sendResponse(gcRequest, gcResponse, response, request);
			
			doAudit(gcRequest, gcResponse);

		} catch (GCException e) {
			
			log.error("GCException: " , e);
			if (gcResponse == null) {
				gcResponse = new GCResponse(gcRequest);
				gcResponse.setRequestReceiveTime(requestTime);
			}

			gcResponse.setInfoID(e.getInfoId());
			gcResponse.setInfoMsg(e.getMessage());
			sendResponse(gcRequest, gcResponse, response, request);		

		} catch (Exception e) {

			log.error("Exception: ", e);
			if (gcResponse == null) {
				gcResponse = new GCResponse(gcRequest);
				gcResponse.setRequestReceiveTime(requestTime);
			}

			gcResponse.setInfoID(InfoIDConstants.DYNAMIC_MSG);
			gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.invalid.request_failed"));
			sendResponse(gcRequest, gcResponse, response, request);
		}
	}

	protected boolean checkMainenanceORut(GCRequest adomsRequest, GCResponse adomsResponse)
			throws AppConfigNoKeyFoundException, JSONException {
	
		String isTestEnabled = AppConfig.optValue("unit_testing.enabled", "false");
		if (isTestEnabled.equalsIgnoreCase("true")) {

			String testResponse = UnitTesting.getResponse(adomsRequest.getHttpRequest().getServletPath());
			if (testResponse != null) {
				JSONObject respObject = new JSONObject(testResponse);
				adomsResponse.put("response", respObject);
				return true;
			}
		}
		return false;
	}

	protected void logResponse(GCRequest gcRequest, GCResponse gcResponse, HttpServletRequest httpServletRequest,
			String responseString) {

		long responseTime = System.currentTimeMillis();
		
		long reqTimeTaken = responseTime - gcResponse.getRequestReceivedTime();
		
		//log.debug("response time " +reqTimeTaken);
		log.info(String.format("%s -- time_taken=%d Ms User=%s, appID=%s -- response send -- %s",
				httpServletRequest.getServletPath(), reqTimeTaken, getUser(gcRequest), gcRequest.getAppID(),
				responseString));
	}


	protected void logRawRequest(String body, HttpServletRequest request) throws JSONException {

		log.debug(String.format("%s -- raw request received --  %s", body, request.getServletPath()));
	}


	protected void logRequest(GCRequest gcRequest) throws JSONException {

		HttpServletRequest httpServletRequest = gcRequest.getHttpRequest();

		log.info(String.format("%s -- appID=%s, IP=%s -- request received --  %s", httpServletRequest.getServletPath(),
		gcRequest.getAppID(), gcRequest.getClientIP(), gcRequest.toS()));
		//log.info(
		//	httpServletRequest.getServletPath() + " User -- " + getUser(gcRequest) + " -- request received -- " + gcRequest.toS());
	}

	protected void sendResponse(GCRequest gcRequest, GCResponse gcResponse, HttpServletResponse httpServletResponse,
			HttpServletRequest httpServletRequest) {

		setSvcGroupAndName(httpServletRequest, gcResponse);
		String responseStr = gcResponse.toString();
		logResponse(gcRequest, gcResponse, httpServletRequest, responseStr);
		
		try {
			if(gcRequest.getHttpRequest().getHeader("X-ENCRYPT") == null 
					|| gcRequest.getHttpRequest().getHeader("X-ENCRYPT").equals("true"))
				responseStr = encrypt(responseStr);
		} catch (InvalidKeyException | UnsupportedEncodingException | NoSuchAlgorithmException | NoSuchPaddingException
				| InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException
				| AppConfigNoKeyFoundException e) {
			log.warn(e);
			sendResponse("Error in framing response - ENC", httpServletResponse, httpServletRequest);
			return;
		}
		
		sendResponse(responseStr, httpServletResponse, httpServletRequest);
	}

	protected void sendResponse(String sResponse, HttpServletResponse httpServletResponse,
			HttpServletRequest httpServletRequest) {

		try {

			addOriginDomainToAllowOrigin(httpServletRequest, httpServletResponse);

			httpServletResponse.setHeader("Access-Control-Expose-Headers", "Set-Cookie, Cookie, Content-Type");
			httpServletResponse.setContentType("application/json; charset=UTF-8");
			httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST");
			httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");

			httpServletResponse.getWriter().print(sResponse);

		} catch (Exception e) {
			log.error("Exception in sending Response : ", e);
		}
	}

	private String getUser(GCRequest gcRequest) {
		try {
			return gcRequest.getSession().getUserID();
		} catch (Exception e) {
			return "";
		}
	}

	protected void setSvcGroupAndName(HttpServletRequest httpServletRequest, GCResponse gcResponse) {
		String requestPath = httpServletRequest.getServletPath();
		String[] paths = requestPath.split("/");

		if (paths.length < 3)
			return;

		gcResponse.setSvcGroup(paths[1]);
		gcResponse.setSvcName(paths[2]);
	}
	
	protected void doAudit(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		try {

			GCAuditObject auditObj = gcRequest.getAuditObj();
			GCAuditTransaction auditTrans = new GCAuditTransaction();
		
			if ( AppConfig.getValue("app.needLogAudit").equalsIgnoreCase("true") && auditObj.needAudit() ) {
			
				auditObj.setReqTime(DateUtils.getStringDateTime(new Date(gcResponse.getRequestReceivedTime())));
				auditObj.setRespTime(DateUtils.getStringDateTime(new Date()));
				auditObj.setMsgID(gcRequest.getMsgID());
				auditObj.setInfoID(gcResponse.getInfoID());
				auditObj.setInfoMsg(gcResponse.getInfoMsg());
				auditObj.setAppID(gcRequest.getAppID());
				auditObj.setSrcIP(gcRequest.getClientIP());
				auditObj.setSvcGroup(gcResponse.getSvcGroup());
				auditObj.setSvcName(gcResponse.getSvcName());
				auditObj.setSvcVersion("1.0.0");
			
				auditTrans.logTransaction(auditObj);	
			}
		} catch(Exception e) {
			log.error("Exception in Audit: ", e);
		}

	}

	protected boolean isValidAppID(GCRequest gcRequest) throws SQLException {

		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet res = null;

		boolean appIDflag = false;
		String appID = gcRequest.getAppID();
		String Query = "SELECT 1 FROM APP_INFO WHERE APP_ID = ?";

		try {
			con = GCDBPool.getInstance().getConnection();
			pstmt = con.prepareStatement(Query);
			pstmt.setString(1, appID);

			res = pstmt.executeQuery();

			if (res.next())
				appIDflag = true;
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(pstmt);
			Helper.closeConnection(con);			
		}

		return appIDflag;
	}
	
	protected String decrypt(String input) throws InvalidKeyException, UnsupportedEncodingException,
	NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
	IllegalBlockSizeException, BadPaddingException, AppConfigNoKeyFoundException {

		String key = AppConfig.getValue("webservice.encrypt.key");
		return Encryption.decryptText(key, input);
	}

	protected String encrypt(String input) throws InvalidKeyException, UnsupportedEncodingException,
		NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException,
		IllegalBlockSizeException, BadPaddingException, AppConfigNoKeyFoundException {
	
		String key = AppConfig.getValue("webservice.encrypt.key");
		return Encryption.encryptText(key, input);
	}

	// By default all the services payload are encrypted/decrypted based on the config values
	// If a specific service payload should be unencrypted, then this function can be overridden in the
	//respective derived class 
	protected boolean isEncryptionApplicableWS()
	{
		return true; // Default 

	}

	abstract protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception;

}
