package com.globecapital.services.init_config;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Arrays;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.config.AppConfig;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.InvalidRequestException;
import com.msf.utils.constants.UtilsConstants;
import com.msf.utils.helper.Helper;
import com.msf.utils.init.VersionUpdateData;

public class Config extends BaseService {

	private static final long serialVersionUID = 1L;

	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		// for storing json request
		String app = "";
		int appVersion = 0;		

		HashMap<String, Integer> versionMap = getVersionMapDetails();
		if (gcRequest.getData().has(AppConstants.APP)) {
			app = gcRequest.getData().getString(AppConstants.APP);
			appVersion = Integer.parseInt(app);
		}
		else
			throw new InvalidRequestException();
	
		if (versionMap.get(AppConstants.APP) > appVersion) {
			sendAppConfig(gcRequest, gcResponse, versionMap.get(AppConstants.APP), appVersion);

		} else {
			JSONObject appObj = new JSONObject();
			appObj.put(AppConstants.VERSION, versionMap.get(AppConstants.APP));
			gcResponse.setInfoID(InfoIDConstants.SUCCESS);
			gcResponse.addToData(AppConstants.APP, appObj);
		}
		
		// check for version update
		checkVersionUpdate( gcRequest, gcResponse);		
	}

	private void checkVersionUpdate( GCRequest gcRequest, GCResponse gcResponse)
			throws Exception {

		com.msf.utils.init.Init init = null;
		init = new com.msf.utils.init.Init(UtilsConstants.MYSQL);
		Connection connection = GCDBPool.getInstance().getConnection();
		try{
			VersionUpdateData versionUpdateData = new VersionUpdateData();
			if(Boolean.parseBoolean(AppConfig.getValue("isSpecialVersionUpdate"))) {
				String[] versionUpdateIgnoreVersions = AppConfig.getArray("versionUpdateIgnoreVersions");
				JSONObject versionDetail = getUserVersionDetails(gcRequest.getAppID(), versionUpdateIgnoreVersions);
				if(versionDetail.getBoolean(DBConstants.HAS_VERSION_UPDATE)) {
					versionDetail.remove(DBConstants.HAS_VERSION_UPDATE);
					gcResponse.addToData(AppConstants.VERSIONDETAIL, versionDetail);
				}
			}else {
				versionUpdateData = init.hasVersionUpdate(connection, gcRequest.getAppID());
				if (versionUpdateData.isUpdateFound()) {
		
					JSONObject versionDetail = new JSONObject();
					versionDetail.put(AppConstants.APP_VERSION, versionUpdateData.getVersion());
					versionDetail.put(AppConstants.MANDATORY, versionUpdateData.isMandatory());
					versionDetail.put(AppConstants.RELEASE_NOTES, versionUpdateData.getReleaseNotes());
					versionDetail.put(AppConstants.URL, versionUpdateData.getUrl());
		
					gcResponse.addToData(AppConstants.VERSIONDETAIL, versionDetail);
				}
			}
		}
		finally 
		{
			Helper.closeConnection(connection);
		}		
	}

	private void sendAppConfig(GCRequest gcRequest, GCResponse gcResponse, int newAppVersion, int reqAppVersion)
			throws Exception {

		String query = "";
		JSONArray config = new JSONArray();
		JSONObject app = new JSONObject();
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		// build query

		query += "SELECT CODE,VALUE " + "FROM APP_CONFIG " + "WHERE CREATED_AT > ( SELECT MAX( CREATED_AT) "
				+ "FROM VERSION_MASTER " + "WHERE BUILD_NAME = ( SELECT BUILD " + "FROM APP_INFO "
				+ "WHERE APP_ID= ? ) " + "AND VERSION = ? AND TYPE    ='app' ) " + "AND IS_DELETED IS NULL "
				+ "AND STATUS ='ACT' AND BUILD_NAME  = ( SELECT BUILD " + "FROM APP_INFO " + "WHERE APP_ID = ? ) ";

		try {
			// prepare statement
			connection = GCDBPool.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(query);
			preparedStatement.setString(1, gcRequest.getAppID());
			preparedStatement.setInt(2, newAppVersion);
			preparedStatement.setString(3, gcRequest.getAppID());
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				JSONObject obj = new JSONObject();
				obj.put(AppConstants.KEY, resultSet.getString(AppConstants.CODE));
				obj.put(AppConstants.VALUE, resultSet.getString(AppConstants.VALUE_UCASE));
				config.put(obj);
			}

		} finally {
			Helper.closeResultSet(resultSet);
			Helper.closeStatement(preparedStatement);
			Helper.closeConnection(connection);
		}
		app.put(AppConstants.CONFIG, config);
		app.put(AppConstants.VERSION, newAppVersion);

		gcResponse.setInfoID(InfoIDConstants.SUCCESS);
		gcResponse.addToData(AppConstants.APP, app);
	}

	// Take build name from APP_INFO and max(created_at) according to TYPE
	private HashMap<String, Integer> getVersionMapDetails() throws Exception {
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;

		HashMap<String, Integer> versionMap = new HashMap<String, Integer>();

		try {
			connection = GCDBPool.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(DBQueryConstants.VERSION_MASTER);
			resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {
				versionMap.put(resultSet.getString(AppConstants.TYPE), resultSet.getInt(AppConstants.VERSION_UCASE));
			}
		}
		finally {

			Helper.closeResultSet(resultSet);
			Helper.closeStatement(preparedStatement);
			Helper.closeConnection(connection);
		}

		return versionMap;
	}
	
	private JSONObject getUserVersionDetails(String appId, String[] versionUpdateIgnoreVersions) throws Exception {
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet res = null;
			String query = DBQueryConstants.VERSION_UPDATE_DATA;
			JSONObject versionDetail = new JSONObject();
			try {
				conn = GCDBPool.getInstance().getConnection();
				ps = conn.prepareStatement(query);
				ps.setString(1, appId);

				res = ps.executeQuery();

				if (res.next()) {
					String channel = String.valueOf(res.getString(DBConstants.CHANNEL));
					String version = String.valueOf(res.getString(DBConstants.APP_VERSION));
					if(Arrays.asList(versionUpdateIgnoreVersions).contains(version)) {
						versionDetail.put(DBConstants.HAS_VERSION_UPDATE, false);
					}else {
						versionDetail.put(DBConstants.HAS_VERSION_UPDATE, true);
						versionDetail.put(AppConstants.APP_VERSION, AppConfig.getValue("versionUpdate.upgradeToVersion"));
						versionDetail.put(AppConstants.MANDATORY, Boolean.parseBoolean(AppConfig.getValue("isMandatory")));
						versionDetail.put(AppConstants.RELEASE_NOTES, AppConfig.getValue("versionUpdate.releaseNotes"));
						if(channel.equalsIgnoreCase(DeviceConstants.ANDROID_MARKET))
							versionDetail.put(AppConstants.URL, AppConfig.getValue("versionUpdate.releaseUrl.android"));
						else
							versionDetail.put(AppConstants.URL, AppConfig.getValue("versionUpdate.releaseUrl.ios"));
					}
				}
			} finally {
				Helper.closeResultSet(res);
				Helper.closeStatement(ps);
				Helper.closeConnection(conn);
			}
			return versionDetail;
	}
}