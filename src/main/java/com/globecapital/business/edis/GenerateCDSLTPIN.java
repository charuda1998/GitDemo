package com.globecapital.business.edis;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;

import org.json.JSONObject;

import com.globecapital.api.ft.edis.GetEDISConfigResponseObject;
import com.globecapital.api.gc.backoffice.GetPartyDetailsAPI;
import com.globecapital.api.gc.backoffice.GetPartyDetailsRequest;
import com.globecapital.api.gc.backoffice.GetPartyDetailsResponse;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.utils.AESUtil;
import com.msf.log.Logger;

public class GenerateCDSLTPIN {
	
	private static Logger log = Logger.getLogger(GenerateCDSLTPIN.class);
	
	public static boolean generateTPIN(Session session,ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		AESUtil aes = new AESUtil();
		JSONObject edisConfigJSON = new JSONObject();
		if(session.getUserInfo().has(UserInfoConstants.EDIS_CONFIG_DETAILS))
			edisConfigJSON = new JSONObject(AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"), session.getUserInfo().getString(UserInfoConstants.EDIS_CONFIG_DETAILS)));
		GetEDISConfigResponseObject configResponseObject = EDISHelper.fetchEDISConfigDetails(session, edisConfigJSON,servletContext,gcRequest,gcResponse);
		GetPartyDetailsAPI getPartyDetailsAPI = new GetPartyDetailsAPI();
		GetPartyDetailsRequest getPartyDetailsRequest = new GetPartyDetailsRequest();
		getPartyDetailsRequest.setToken(GCAPIAuthToken.getAuthToken());
		getPartyDetailsRequest.setClientCode(session.getUserID());
		GetPartyDetailsResponse getPartyDetailsResponse = new GetPartyDetailsResponse();
		getPartyDetailsResponse = getPartyDetailsAPI.get(getPartyDetailsRequest, GetPartyDetailsResponse.class, "","GetPartyDetails");
		String clientPAN = getPartyDetailsResponse.getPAN();
		String reqTime = new SimpleDateFormat(DeviceConstants.EDIS_DATE_FORMAT).format(new Date());
		JSONObject tempMap = new JSONObject();
		tempMap.put(DeviceConstants.BO_ID, configResponseObject.getBeneficiaryId().replace("#",""));
		tempMap.put(DeviceConstants.PAN, clientPAN);
		tempMap.put(DeviceConstants.REQ_FLAG, "N");
		tempMap.put(DeviceConstants.REQ_TIME, reqTime);
		
		byte[] encrypt = aes.encrypt(tempMap.toString().getBytes(DeviceConstants.CHARSET_NAME));
		String decryptedResponse = sendPost(new String(encrypt, DeviceConstants.CHARSET_NAME), configResponseObject);
		JSONObject responseObj = new JSONObject(decryptedResponse);
		log.info("Decrypted response CDSL TPIN API : "+responseObj);
		return responseObj.getString(DeviceConstants.STATUS_S).equals("00");
	}
	
	private static String sendPost(String data, GetEDISConfigResponseObject configResponseObject) throws Exception {
		
		AESUtil aes = new AESUtil();
		
		StringBuffer sb = new StringBuffer();
		String beneficiary = configResponseObject.getBeneficiaryId();
		String dpId = beneficiary.split("#")[0];
		dpId = dpId.substring(2,dpId.length());
		URL url = new URL(AppConfig.getValue("cdsl.generate.tpin"));
		HttpURLConnection http = (HttpURLConnection)url.openConnection();
		http.setRequestProperty("User-Agent", "");
		http.setRequestMethod("POST");
		http.setDoOutput(true);
		http.setRequestProperty(DeviceConstants.DP_ID, dpId);
		
		String requestId = EDISHelper.generateRandomString(4);
		http.setRequestProperty(DeviceConstants.REQ_ID, requestId);
		http.setRequestProperty(DeviceConstants.VERSION, "1.0");
		http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

		String body = DeviceConstants.ENCRYPT_DETAILS+"="+URLEncoder.encode(data,DeviceConstants.CHARSET_NAME);

		byte[] out = body.getBytes(StandardCharsets.UTF_8);

		OutputStream stream = http.getOutputStream();
		stream.write(out);

		log.info(http.getResponseCode() + " " + http.getResponseMessage());
		String strCurrentLine;
		BufferedReader br = new BufferedReader(new InputStreamReader(http.getInputStream()));
        while ((strCurrentLine = br.readLine()) != null) {
        	sb.append(aes.decrypt(strCurrentLine));
        }
		http.disconnect();
		return sb.toString();
	}
}	