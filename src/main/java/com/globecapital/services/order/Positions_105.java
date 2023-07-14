package com.globecapital.services.order;

import java.text.SimpleDateFormat;
import java.util.List;

import org.json.JSONObject;
import com.globecapital.api.ft.order.GetNetPositionAPI;
import com.globecapital.api.ft.order.GetNetPositionObject;
import com.globecapital.api.ft.order.GetNetPositionRequest;
import com.globecapital.api.ft.order.GetNetPositionResponse;
import com.globecapital.api.ft.order.GetNetPositionRows;
import com.globecapital.business.order.DerivativePositions_104;
import com.globecapital.business.order.TodaysPosition_104;
import com.globecapital.config.AppConfig;
import com.globecapital.config.UnitTesting;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.RedisConstants;
import com.globecapital.db.RedisPool;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.omex.GetOMEXMessageResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.utils.GCUtils;
import com.google.gson.Gson;

public class Positions_105 extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		JSONObject todaysFilterObj = gcRequest.getObjectFromData(DeviceConstants.TODAYS_FILTER_OBJ);
		JSONObject derivativesFilterObj = gcRequest.getObjectFromData(DeviceConstants.DERIVATIVES_FILTER_OBJ);

		String sGroupID = session.getGroupId();
		String sUserID = session.getUserID();

		String isForceRefresh = gcRequest.getOptFromData(DeviceConstants.IS_FORCE_REFRESH, "false");

		RedisPool redisPool = new RedisPool();


		if (AppConfig.getValue("isPositionUpdate").equals("true")) {

			if (isForceRefresh.equalsIgnoreCase("true")) {
				try {
					callPositionAPI(session, sGroupID, sUserID, todaysFilterObj, derivativesFilterObj, gcRequest,
							gcResponse);
				} catch (Exception e) {
					log.error(e);
					callDefault(session, sGroupID, sUserID, todaysFilterObj, derivativesFilterObj, gcRequest,
							gcResponse);
				}
			}

			try {
				if (redisPool.isExists(sUserID + "_" + RedisConstants.POSITIONS)) {

					String orderData = redisPool.getValue(sUserID + "_" + RedisConstants.POSITIONS);
				
					JSONObject orderDataObj = new JSONObject(orderData);
					String refreshInfo = orderDataObj.getString(RedisConstants.IS_REFRESH_REQUIRED);
					log.info("OMEX POSITION_104" + sUserID + " Refresh Requirement :"+refreshInfo);

					if (refreshInfo.equalsIgnoreCase("true")) {

						callPositionAPI(session, sGroupID, sUserID, todaysFilterObj, derivativesFilterObj, gcRequest,
								gcResponse);
					}

					else {
						log.info("OMEX POSITION_104 " + sUserID + ":refresh info is false");
						orderData = redisPool.getValue(sUserID + "_" + RedisConstants.POSITIONS);
						orderDataObj = new JSONObject(orderData);
						String todays = orderDataObj.getString(RedisConstants.TODAYS);
						String derivatives = orderDataObj.getString(RedisConstants.DERIVATIVES);


						JSONObject derivativePositions = new JSONObject();

						Gson gson = new Gson();
						GetNetPositionResponse todaysPos = gson.fromJson(todays, GetNetPositionResponse.class);
						GetNetPositionResponse derivativePos = gson.fromJson(derivatives, GetNetPositionResponse.class);

						GetNetPositionObject positionObj = todaysPos.getResponseObject();
						List<GetNetPositionRows> positionRows = positionObj.getObjJSONRows();

						GetNetPositionObject positionObj1 = derivativePos.getResponseObject();
						List<GetNetPositionRows> positionRows1 = positionObj1.getObjJSONRows();

						JSONObject todaysPositionObj = TodaysPosition_104.getPositions(positionRows, sUserID,
								session.getAppID(), todaysFilterObj);
						JSONObject derivativePositionObj = DerivativePositions_104
								.getDerivativePositions_101(positionRows, positionRows1, session, derivativesFilterObj);

						String totalCount = "0";
						if (derivativePositionObj.has(DeviceConstants.TOTAL_COUNT)) {
							totalCount = String.valueOf(derivativePositionObj.getInt(DeviceConstants.TOTAL_COUNT));
							derivativePositionObj.remove(DeviceConstants.TOTAL_COUNT);
							if (totalCount.equals("0") && todaysPositionObj.has(DeviceConstants.TOTAL_SUMMARY))
								totalCount = String
										.valueOf(todaysPositionObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY)
												.getString(DeviceConstants.RECORDS_COUNT));
						} else if (todaysPositionObj.has(DeviceConstants.TOTAL_SUMMARY)) {
							totalCount = String.valueOf(todaysPositionObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY)
									.getString(DeviceConstants.RECORDS_COUNT));
						}
						derivativePositions.put(DeviceConstants.TOTAL_COUNT, totalCount);
						derivativePositions.put(DeviceConstants.TODAYS_POSITIONS, todaysPositionObj);
						derivativePositions.put(DeviceConstants.DERIVATIVE_POSITIONS, derivativePositionObj);
						if (todaysPositionObj.getJSONArray(DeviceConstants.POSITION_LIST).isEmpty()
								&& derivativePositionObj.getJSONArray(DeviceConstants.POSITION_LIST).isEmpty())
							gcResponse.setNoDataAvailable();
						else
							gcResponse.setData(derivativePositions);

					}
				} else {
					callPositionAPI(session, sGroupID, sUserID, todaysFilterObj, derivativesFilterObj, gcRequest,
							gcResponse);
				}

			} catch (Exception e) {
				log.error(e);
				callDefault(session, sGroupID, sUserID, todaysFilterObj, derivativesFilterObj, gcRequest, gcResponse);

			}
		}

//      default settings
		else {
			callDefault(session, sGroupID, sUserID, todaysFilterObj, derivativesFilterObj, gcRequest, gcResponse);
		}
	}

	private void callDefault(Session session, String sGroupID, String sUserID, JSONObject todaysFilterObj,
			JSONObject derivativesFilterObj, GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		log.info("OMEX DEFAULT CODE IS RUNNING ");

		GetNetPositionRequest positionRequest = new GetNetPositionRequest();
		positionRequest.setUserID(sUserID);
		positionRequest.setGroupId(sGroupID);
		positionRequest.setJKey(session.getjKey());
		positionRequest.setJSession(session.getjSessionID());

		/**
		 * * Position type set to ZERO, as we are getting only today's positios date
		 * here for all the tabs 'Today, Derivative and Equity'
		 */
		positionRequest.setPosType(AppConstants.STR_ZERO);

		GetNetPositionAPI positionApi = new GetNetPositionAPI();
		GetNetPositionResponse positionResponse = new GetNetPositionResponse();

		try {
			positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class, session.getAppID(),
					"GetNetPosition");
		} catch (GCException e) {
			log.debug(e);
			if (e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
				if (GCUtils.reInitiateLogIn(positionRequest, session, getServletContext(), gcRequest, gcResponse)) {
					try {
						positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class,
								session.getAppID(), "GetNetPosition");
					} catch (GCException ex) {
						log.debug(ex + "  After ReInitiate Login");
						throw new RequestFailedException();
					}
					session = gcRequest.getSession();
				} else {
					throw new GCException(InfoIDConstants.INVALID_SESSION, "User logged out successfully");
				}
			} else
				throw new RequestFailedException();
		}

		GetNetPositionObject positionObj = positionResponse.getResponseObject();
		List<GetNetPositionRows> positionRows = positionObj.getObjJSONRows();

		try {
			JSONObject derivativePositions = new JSONObject();
			JSONObject todaysPositionObj = TodaysPosition_104.getPositions(positionRows, sUserID,
					session.getAppID(), todaysFilterObj);
			JSONObject derivativePositionObj = DerivativePositions_104.getDerivativePositions(positionRows, session,
					derivativesFilterObj, getServletContext(), gcRequest, gcResponse);
			String totalCount = "0";
			if (derivativePositionObj.has(DeviceConstants.TOTAL_COUNT)) {
				totalCount = String.valueOf(derivativePositionObj.getInt(DeviceConstants.TOTAL_COUNT));
				derivativePositionObj.remove(DeviceConstants.TOTAL_COUNT);
				if (totalCount.equals("0") && todaysPositionObj.has(DeviceConstants.TOTAL_SUMMARY))
					totalCount = String.valueOf(todaysPositionObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY)
							.getString(DeviceConstants.RECORDS_COUNT));
			} else if (todaysPositionObj.has(DeviceConstants.TOTAL_SUMMARY)) {
				totalCount = String.valueOf(todaysPositionObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY)
						.getString(DeviceConstants.RECORDS_COUNT));
			}
			derivativePositions.put(DeviceConstants.TOTAL_COUNT, totalCount);
			derivativePositions.put(DeviceConstants.TODAYS_POSITIONS, todaysPositionObj);
			derivativePositions.put(DeviceConstants.DERIVATIVE_POSITIONS, derivativePositionObj);
			if (todaysPositionObj.getJSONArray(DeviceConstants.POSITION_LIST).isEmpty()
					&& derivativePositionObj.getJSONArray(DeviceConstants.POSITION_LIST).isEmpty())
				gcResponse.setNoDataAvailable();
			else
				gcResponse.setData(derivativePositions);
		} catch (Exception e) {
			log.error(e);
			gcResponse.setNoDataAvailable();
		}

	}

	private void callPositionAPI(Session session, String sGroupID, String sUserID, JSONObject todaysFilterObj,
			JSONObject derivativesFilterObj, GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		log.info("OMEX POSITION_104 " + sUserID + " :refresh info true");
		GetNetPositionRequest positionRequest = new GetNetPositionRequest();
		positionRequest.setUserID(sUserID);
		positionRequest.setGroupId(sGroupID);
		positionRequest.setJKey(session.getjKey());
		positionRequest.setJSession(session.getjSessionID());

		/**
		 * * Position type set to ZERO, as we are getting only today's positios date
		 * here for all the tabs 'Today, Derivative and Equity'
		 */
		positionRequest.setPosType(AppConstants.STR_ZERO);

		GetNetPositionAPI positionApi = new GetNetPositionAPI();
		GetNetPositionResponse positionResponse = new GetNetPositionResponse();

		try {
			positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class, session.getAppID(),
					"GetNetPosition");
		} catch (GCException e) {
			log.debug(e);
			if (e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
				if (GCUtils.reInitiateLogIn(positionRequest, session, getServletContext(), gcRequest, gcResponse)) {
					try {
						positionResponse = positionApi.post(positionRequest, GetNetPositionResponse.class,
								session.getAppID(), "GetNetPosition");
					} catch (GCException ex) {
						log.debug(ex + "  After ReInitiate Login");
						throw new RequestFailedException();
					}
					session = gcRequest.getSession();
				} else {
					throw new GCException(InfoIDConstants.INVALID_SESSION, "User logged out successfully");
				}
			}
		}

		positionRequest.setPosType(AppConstants.STR_ONE);
		GetNetPositionResponse derivativesPositions = positionApi.post(positionRequest, GetNetPositionResponse.class,
				session.getAppID(), "GetNetPosition");

		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");  
		java.util.Date dt = new java.util.Date(); 
		GetOMEXMessageResponse.insertIntoRedis(sUserID + "_" + RedisConstants.POSITIONS, "false",
				new Gson().toJson(positionResponse), new Gson().toJson(derivativesPositions));

		GetNetPositionObject positionObj = positionResponse.getResponseObject();
		List<GetNetPositionRows> positionRows = positionObj.getObjJSONRows();
		
		GetNetPositionObject positionObj1 = derivativesPositions.getResponseObject();
        List<GetNetPositionRows> positionRows1 = positionObj1.getObjJSONRows();

		try {
			JSONObject derivativePositions = new JSONObject();
			JSONObject todaysPositionObj = TodaysPosition_104.getPositions(positionRows, sUserID,
					session.getAppID(), todaysFilterObj);
			JSONObject derivativePositionObj = DerivativePositions_104.getDerivativePositions_101(positionRows, positionRows1, session,
					derivativesFilterObj);
			String totalCount = "0";
			if (derivativePositionObj.has(DeviceConstants.TOTAL_COUNT)) {
				totalCount = String.valueOf(derivativePositionObj.getInt(DeviceConstants.TOTAL_COUNT));
				derivativePositionObj.remove(DeviceConstants.TOTAL_COUNT);
				if (totalCount.equals("0") && todaysPositionObj.has(DeviceConstants.TOTAL_SUMMARY))
					totalCount = String.valueOf(todaysPositionObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY)
							.getString(DeviceConstants.RECORDS_COUNT));
			} else if (todaysPositionObj.has(DeviceConstants.TOTAL_SUMMARY)) {
				totalCount = String.valueOf(todaysPositionObj.getJSONObject(DeviceConstants.TOTAL_SUMMARY)
						.getString(DeviceConstants.RECORDS_COUNT));
			}
			derivativePositions.put(DeviceConstants.TOTAL_COUNT, totalCount);
			derivativePositions.put(DeviceConstants.TODAYS_POSITIONS, todaysPositionObj);
			derivativePositions.put(DeviceConstants.DERIVATIVE_POSITIONS, derivativePositionObj);
			if (todaysPositionObj.getJSONArray(DeviceConstants.POSITION_LIST).isEmpty()
					&& derivativePositionObj.getJSONArray(DeviceConstants.POSITION_LIST).isEmpty())
				gcResponse.setNoDataAvailable();
			else
				gcResponse.setData(derivativePositions);
		} catch (Exception e1) {
			log.error(e1);
			gcResponse.setNoDataAvailable();
		}
	}
}