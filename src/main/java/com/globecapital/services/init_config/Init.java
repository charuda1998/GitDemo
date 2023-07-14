package com.globecapital.services.init_config;

import java.sql.Connection;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.msf.log.Logger;
import com.msf.utils.constants.UtilsConstants;
import com.msf.utils.helper.Helper;
import com.msf.utils.init.AppData;

public class Init extends BaseService {

	private static final long serialVersionUID = 1L;

	private static Logger log = Logger.getLogger(Init.class);

	private static final String APPLICATION = "app";

	private static final String SOFTWARE = "software";

	private static final String HARDWARE = "hardware";

	private static final String NETWORK = "network";

	@Override
	protected void process(GCRequest ssRequest, GCResponse ssResponse) throws Exception {
		
		JSONObject dataObject = ssRequest.getData();
		JSONObject appJsonObject = dataObject.getJSONObject(APPLICATION);
		JSONObject softwareJsonObject = dataObject.getJSONObject(SOFTWARE);
		JSONObject hardwareJsonObject = dataObject.getJSONObject(HARDWARE);
		JSONObject networkJsonObject = dataObject.getJSONObject(NETWORK);

		String appID = ssRequest.getAppID();
		if (("0").equals(appID)) {
			appID = com.msf.utils.helper.Helper.generateAPPID(ssRequest.toString());
		}
		Connection connection = null;
		try {

			AppData appData = new AppData();

			appData.setAppID(appID);
			appData.setAppName(appJsonObject.getString("name"));
			appData.setAppVersion(appJsonObject.getString("version"));
			appData.setChannel(appJsonObject.getString("channel"));
			appData.setBuild(appJsonObject.getString("build"));
			appData.setUUID(appJsonObject.optString("uuid", ""));

			
			if (softwareJsonObject.has("osType")) {
				appData.setOsType(softwareJsonObject.getString("osType"));
			} else {
				appData.setOsType(null);
			}
			if (softwareJsonObject.has("osVersion")) {
				appData.setOsVersion(softwareJsonObject.getString("osVersion"));
			} else {
				appData.setOsVersion(null);
			}
			if (softwareJsonObject.has("osName")) {
				appData.setOsName(softwareJsonObject.getString("osName"));
			} else {
				appData.setOsName(null);
			}
			if (softwareJsonObject.has("osVendor")) {
				appData.setOsVendor(softwareJsonObject.getString("osVendor"));
				if(softwareJsonObject.getString("osVendor").equals(DeviceConstants.VENDOR_APPLE) && 
						(appData.getOsType() == null || appData.getOsType().isEmpty()))
					appData.setOsType(appData.getOsVersion());
			} else {
				appData.setOsVendor(null);
			}
			if (hardwareJsonObject.has("vendor")) {
				appData.setVendor(hardwareJsonObject.getString("vendor"));
			} else {
				appData.setVendor(null);
			}
			if (hardwareJsonObject.has("imei")) {
				appData.setIMEI(hardwareJsonObject.getString("imei"));
			} else {
				appData.setIMEI(null);
			}
			if (hardwareJsonObject.has("model")) {
				appData.setModel(hardwareJsonObject.getString("model"));
			} else {
				appData.setModel(null);
			}
			if (hardwareJsonObject.has("screen")) {
				appData.setScreen(hardwareJsonObject.getString("screen"));
			} else {
				appData.setScreen(null);
			}
			if (hardwareJsonObject.has("keyboard")) {
				appData.setKeyboard(hardwareJsonObject.getString("keyboard"));
			} else {
				appData.setKeyboard(null);
			}
			if (hardwareJsonObject.has("deviceType")) {
				appData.setDeviceType(hardwareJsonObject.getString("deviceType"));
			} else {
				appData.setDeviceType(null);
			}

			if (networkJsonObject.has("gps")) {
				appData.setGps(networkJsonObject.getString("gps"));
			} else {
				appData.setGps(null);
			}

			if (networkJsonObject.has("imsi")) {
				appData.setIMSI(networkJsonObject.getString("imsi"));
			} else {
				appData.setIMSI(null);
			}
			if (networkJsonObject.has("cellular")) {
				appData.setCellular(networkJsonObject.getString("cellular"));
			} else {
				appData.setCellular(null);
			}
			
			com.msf.utils.init.Init init = null;
			init = new com.msf.utils.init.Init(UtilsConstants.MYSQL);
			connection = GCDBPool.getInstance().getConnection();
            int result = init.insertAppData(connection, appData);
            
			if ((0 == result) || (-2 == result)) // success
			{
				ssResponse.addToData(DeviceConstants.APP_ID, appID);
				// This is for Audit
				ssResponse.setAppID(appID);
				ssResponse.setInfoID(InfoIDConstants.SUCCESS);

				return;
			} else {
				ssResponse.setInfoID(InfoIDConstants.DYNAMIC_MSG);
				ssResponse.setInfoMsg(InfoMessage.getValue("info_msg.invalid.build"));
			}

		} catch(JSONException e)
		{
			log.error("", e);
			ssResponse.setInfoID(InfoIDConstants.DYNAMIC_MSG);
			ssResponse.setInfoMsg(InfoMessage.getValue("info_msg.invalid.build"));
		}
		
		finally {
			Helper.closeConnection(connection);

		}

	}
	
	@Override
	protected boolean isValidAppID(GCRequest adomsRequest) throws SQLException {
		return true;
	}

	
}

