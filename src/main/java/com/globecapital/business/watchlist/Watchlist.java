package com.globecapital.business.watchlist;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.api.ft.order.GetOrderBookResponse;
import com.globecapital.api.ft.watchlist.CreateWatchlistApi;
import com.globecapital.api.ft.watchlist.CreateWatchlistRequest;
import com.globecapital.api.ft.watchlist.CreateWatchlistResponse;
import com.globecapital.api.ft.watchlist.DeleteWatchlistAPI;
import com.globecapital.api.ft.watchlist.DeleteWatchlistRequest;
import com.globecapital.api.ft.watchlist.GetProfileScripsRequest;
import com.globecapital.api.ft.watchlist.GetProfileScripsResponse;
import com.globecapital.api.ft.watchlist.GetSymbolWatchlistAPI;
import com.globecapital.api.ft.watchlist.GetSymbolWatchlistObjTable1;
import com.globecapital.api.ft.watchlist.GetWatchListAPI;
import com.globecapital.api.ft.watchlist.GetWatchListObject;
import com.globecapital.api.ft.watchlist.GetWatchListResponse;
import com.globecapital.api.ft.watchlist.GetWatchlistRequest;
import com.globecapital.api.ft.watchlist.RenameWatchlistAPI;
import com.globecapital.api.ft.watchlist.RenameWatchlistRequest;
import com.globecapital.api.ft.watchlist.SaveWatchlistAPI;
import com.globecapital.api.ft.watchlist.SaveWatchlistResponse;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.GCUtils;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class Watchlist {

	private static Logger log = Logger.getLogger(Watchlist.class);

	public static Boolean createWatchList(Session session, String userID, String watchListName, JSONArray symbols,
			ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse)
			throws SQLException, JSONException, GCException {

		CreateWatchlistRequest createWatchlistReq = new CreateWatchlistRequest();

		/*** User info ***/
		createWatchlistReq.setUserID(session.getUserID());
		createWatchlistReq.setGroupId(session.getGroupId());
		createWatchlistReq.setWatchlistId(FTConstants.NEW_WATCHLIST_ID_VALUE);
		createWatchlistReq.setWatchlistName(watchListName);
		boolean created = true;

		try {

			/*** Symbol info ***/
			if (symbols.length() > 0) {

				for (int i = 0; i < symbols.length(); i++) {

					SymbolRow symobj = (SymbolRow) new SymbolRow(symbols.getJSONObject(i));
					String[] symToken = symobj.getSymbolToken().split("_");
					createWatchlistReq.addSymbols(symToken[1], symToken[0]);
				}
			} else {
				createWatchlistReq.addSymbols();
			}

			createWatchlistReq.setJKey(session.getjKey());
			createWatchlistReq.setJSession(session.getjSessionID());

			CreateWatchlistApi api = new CreateWatchlistApi();
			CreateWatchlistResponse watchlistResponse=new CreateWatchlistResponse();
			try {
			watchlistResponse = api.post(createWatchlistReq, CreateWatchlistResponse.class,session.getAppID(),"AddWatchlist");
			}
			catch (GCException e) {
				log.debug(e);
                if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                    if(GCUtils.reInitiateLogIn(createWatchlistReq,session, servletContext, gcRequest, gcResponse)) {
                    	watchlistResponse = api.post(createWatchlistReq, CreateWatchlistResponse.class,session.getAppID(),"AddWatchlist");
                        session = gcRequest.getSession();
                    }
                    else 
                        throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
                } 
                else 
    				throw new RequestFailedException();
			}

			if (watchlistResponse.getResponseStatus()) {
				created = true;
			} else
				throw new RequestFailedException();

		} catch (Exception e) {
			log.error(e);
		}
		return created;
	}

	public static Boolean deleteWatchList(Session session, String userID, String watchlistID, String watchListName,
			ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse)
			throws SQLException, JSONException, GCException {

		DeleteWatchlistRequest deleteWatchlistRequest = new DeleteWatchlistRequest();

		/*** User info ***/
		deleteWatchlistRequest.setUserID(session.getUserID());
		deleteWatchlistRequest.setGroupId(session.getGroupId());
		boolean deleted = true;
		try {
			deleteWatchlistRequest.setWatchlistId(watchlistID);
			deleteWatchlistRequest.setWatchlistName(watchListName);
			deleteWatchlistRequest.setJKey(session.getjKey());
			deleteWatchlistRequest.setJSession(session.getjSessionID());

			DeleteWatchlistAPI api = new DeleteWatchlistAPI();
			FTResponse resp=new FTResponse();
			try{
				resp = api.post(deleteWatchlistRequest, FTResponse.class, session.getAppID(),"DeleteWatchlistGroup");
			}
			catch(GCException e){
				log.debug(e);
                if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                    if(GCUtils.reInitiateLogIn(deleteWatchlistRequest,session, servletContext, gcRequest, gcResponse)) {
                    	resp = api.post(deleteWatchlistRequest, FTResponse.class, session.getAppID(),"DeleteWatchlistGroup");
                        session = gcRequest.getSession();
                    }
                    else 
                        throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
                } 
                else 
    				throw new RequestFailedException();
			}

			if (resp.getResponseStatus()) {
				deleted = true;
			} else
				throw new RequestFailedException();
		} catch (Exception e) {
			log.error(e);
		}
		return deleted;

	}

	public static Boolean renameWatchList(Session session, String userID, String watchlistID, String watchListName,
			String newWatchListName,ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse) throws SQLException, JSONException, GCException {

		RenameWatchlistRequest renameWatchlistRequest = new RenameWatchlistRequest();

		/*** User info ***/
		renameWatchlistRequest.setUserID(session.getUserID());
		renameWatchlistRequest.setGroupId(session.getGroupId());
		boolean renamed = true;
		try {
			renameWatchlistRequest.setWatchlistId(watchlistID);
			renameWatchlistRequest.setWatchlistName(watchListName);
			renameWatchlistRequest.setNewWatchlistName(newWatchListName);
			renameWatchlistRequest.setJKey(session.getjKey());
			renameWatchlistRequest.setJSession(session.getjSessionID());

			RenameWatchlistAPI api = new RenameWatchlistAPI();
			FTResponse resp=new FTResponse();
			try {
			resp = api.post(renameWatchlistRequest, FTResponse.class, session.getAppID(),"RenameWatchlist");
			}
			catch (GCException e){
				log.debug(e);
                if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                    if(GCUtils.reInitiateLogIn(renameWatchlistRequest,session, servletContext, gcRequest, gcResponse)) {
                    	resp = api.post(renameWatchlistRequest, FTResponse.class, session.getAppID(),"RenameWatchlist");
                        session = gcRequest.getSession();
                    }
                    else 
                        throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
                } 
                else 
    				throw new RequestFailedException();
			}

			if (resp.getResponseStatus()) {
				renamed = true;
			} else
				throw new RequestFailedException();
		} catch (Exception e) {
			log.error(e);
		}
		return renamed;

	}

	public static JSONArray getPredefinedWatchlistSymbols(String watchlistID, String watchListName)
			throws SQLException {
		JSONArray symbolInfo = new JSONArray();
		Connection conn = null;
		ResultSet res = null;
		PreparedStatement ps = null;
		String query = DBQueryConstants.GET_PREDEFINED_WATCHLIST_SYMBOLS;
		log.info("Query :: " + query);
		String exchange = "NSE";
		if (watchListName.equalsIgnoreCase("SENSEX"))
			exchange = "BSE";
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, watchlistID);
			res = ps.executeQuery();
			while(res.next()) {
				String isin = res.getString(DBConstants.S_ISIN);
				isin = isin + "_" + ExchangeSegment.getMarketSegmentID(exchange);
				if (SymbolMap.isValidSymbol(isin)) 
					symbolInfo.put(SymbolMap.getISINSymbolRow(isin).getMinimisedSymbolRow().getJSONObject(SymbolConstants.SYMBOL_OBJ));
				 else
					continue;
			}
		} catch (Exception e) {
			log.info("Error :: " + e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return symbolInfo;
	}

	public static JSONArray getSymbols(Session session, String userID, String watchlistID, String watchListName,
			ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse)
			throws Exception {

		GetProfileScripsRequest profileScripRequest = new GetProfileScripsRequest();

		/*** User info ***/
		profileScripRequest.setUserID(session.getUserID());
		profileScripRequest.setGroupId(session.getGroupId());
		profileScripRequest.setWatchlistId(watchlistID);
		profileScripRequest.setWatchlistName(watchListName);
		profileScripRequest.setJKey(session.getjKey());
		profileScripRequest.setJSession(session.getjSessionID());

		GetSymbolWatchlistAPI api = new GetSymbolWatchlistAPI();
		GetProfileScripsResponse ftresponse=new GetProfileScripsResponse();
		try {
		ftresponse= api.post(profileScripRequest, GetProfileScripsResponse.class,
				session.getAppID(),"GetProfileScrips");
		}
		catch(GCException e) {
			log.debug(e);
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(profileScripRequest,session, servletContext, gcRequest, gcResponse)) {
                	ftresponse= api.post(profileScripRequest, GetProfileScripsResponse.class,
            				session.getAppID(),"GetProfileScrips");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
		}

		List<GetSymbolWatchlistObjTable1> symbolList = ftresponse.getResponseObject().getTable1();

		JSONArray symbol = new JSONArray();

		for (int i = 0; i < symbolList.size(); i++) {

			GetSymbolWatchlistObjTable1 symbols = symbolList.get(i);

			/*** Symbol info ***/
			if (SymbolMap.isValidSymbolTokenSegmentMap(symbols.getScripToken() + "_" + symbols.getMktSegId())) {
				symbol.put(SymbolMap.getSymbolRow(symbols.getScripToken() + "_" + symbols.getMktSegId())
						.getMinimisedSymbolRow().getJSONObject(SymbolConstants.SYMBOL_OBJ));
			}else if(SymbolMap.isValidSymbol(symbols.getIsinNo() +"_"+ symbols.getMktSegId())){
				symbol.put(SymbolMap.getISINSymbolRow((symbols.getIsinNo() +"_"+ symbols.getMktSegId()))
						.getMinimisedSymbolRow().getJSONObject(SymbolConstants.SYMBOL_OBJ)); 
			}else {
				continue;
			}
		}

		return symbol;
	}

	public static JSONObject getWatchlist(Session session, String userID,
			ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		JSONObject watchlists = new JSONObject();
		watchlists.put(DeviceConstants.USER_DEFINED, getUserDefinedWatchlistFT(session, userID,servletContext,gcRequest,gcResponse));
		JSONArray predefined = new JSONArray();
		for (Map.Entry<String, String> mapEntry : PredefinedWatchList.getPredefinedWatchlist().entrySet())
		{			
			JSONObject watchlistObj = new JSONObject();
			watchlistObj.put(DeviceConstants.WATCHLIST_ID, mapEntry.getValue());
			watchlistObj.put(DeviceConstants.WATCHLIST_NAME, mapEntry.getKey());
			watchlistObj.put(DeviceConstants.DEFAULT, true);
			predefined.put(watchlistObj);
		}
		watchlists.put(DeviceConstants.PRE_DEFINED, predefined);
		return watchlists;
	}

	public static JSONArray getUserDefinedWatchlistFT(Session session, String userID,
			ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse)
			throws Exception {

		GetWatchlistRequest watchlistRequest = new GetWatchlistRequest();
		/*** User info ***/
		watchlistRequest.setJKey(session.getjKey());
		watchlistRequest.setUserID(session.getUserID());
		watchlistRequest.setGroupId(session.getGroupId());
		watchlistRequest.setJSession(session.getjSessionID());

		GetWatchListAPI api = new GetWatchListAPI();
		GetWatchListResponse watchlistResp =new GetWatchListResponse();
		try {
		watchlistResp= api.post(watchlistRequest, GetWatchListResponse.class, session.getAppID(),
				"GetProfileList");
		}
		catch(GCException e){
			log.debug(e);
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(watchlistRequest,session, servletContext, gcRequest, gcResponse)) {
                	watchlistResp= api.post(watchlistRequest, GetWatchListResponse.class, session.getAppID(),
            				"GetProfileList");
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
		}

		JSONArray profile = new JSONArray();

		/*** Fetch available watchlists ***/
		if (watchlistResp.getResponseStatus()) {

			for (GetWatchListObject re : watchlistResp.getResponseObject()) {

				JSONObject watchlistObj = new JSONObject();

				watchlistObj.put(DeviceConstants.WATCHLIST_ID, re.getProfileId());
				watchlistObj.put(DeviceConstants.WATCHLIST_NAME, re.getProfileName());

				if (re.getSequenceNum().equals(FTConstants.SEQUENCE_NUM))
					watchlistObj.put(DeviceConstants.DEFAULT, true);
				else
					watchlistObj.put(DeviceConstants.DEFAULT, false);

				profile.put(watchlistObj);
			}
		}
		return profile;
	}

	public static boolean updateWatchList(Session session, String userID, String watchlistId, String watchListName,
			JSONArray symbols,ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse) throws SQLException, JSONException, GCException {

		CreateWatchlistRequest updateRequest = new CreateWatchlistRequest();
		/*** User info ***/
		updateRequest.setUserID(session.getUserID());
		updateRequest.setGroupId(session.getGroupId());
		updateRequest.setWatchlistId(watchlistId);
		updateRequest.setWatchlistName(watchListName);
		boolean updated = true;

		try {
			String[] predefinedWatchlist = AppConfig.getArray("indexNames");
			if (ArrayUtils.contains(predefinedWatchlist, watchListName))
				return false;
			// TO-DO temporary condition for default watchlist to be reviewed
			if (watchListName.equalsIgnoreCase(FTConstants.DEFAULT_WATCHLIST)) {
				return updated;
			}
			/*** Symbol info ***/
			if (symbols.length() > 0) {

				for (int i = 0; i < symbols.length(); i++) {

					SymbolRow symobj = (SymbolRow) new SymbolRow(symbols.getJSONObject(i));
					String[] symToken = symobj.getSymbolToken().split("_");
					updateRequest.addSymbols(symToken[1], symToken[0]);
				}
			} else {
				updateRequest.addSymbols();

			}

			updateRequest.setJKey(session.getjKey());
			updateRequest.setJSession(session.getjSessionID());

			SaveWatchlistAPI api = new SaveWatchlistAPI();
			SaveWatchlistResponse resp =new SaveWatchlistResponse();
			try {
			resp= api.post(updateRequest, SaveWatchlistResponse.class, session.getAppID()
					,"SaveWatchlist");
			}
			catch(GCException e) {
				log.debug(e);
	            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
	                if(GCUtils.reInitiateLogIn(updateRequest,session, servletContext, gcRequest, gcResponse)) {
	                	resp= api.post(updateRequest, SaveWatchlistResponse.class, session.getAppID()
	        					,"SaveWatchlist");
	                    session = gcRequest.getSession();
	                }
	                else 
	                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
	            } 
	            else 
    				throw new RequestFailedException();
			}
			if (resp.getResponseStatus()) {
				updated = true;
			} else
				throw new RequestFailedException();
		} catch (Exception e) {
			log.error(e);
		}
		return updated;
	}

	public static JSONObject getWatchlist_101(Session session, String userID,
			ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse) throws Exception 
	{
		JSONObject watchListObj = new JSONObject();
		watchListObj.put(DeviceConstants.USER_DEFINED, getUserDefinedWatchlistFT_101(session, userID,servletContext,gcRequest,gcResponse));
		watchListObj.put(DeviceConstants.PRE_DEFINED, getPredefinedWatchlist());
		return watchListObj;
	}

	private static List<JSONObject> getUserDefinedWatchlistFT_101(Session session, String userID,ServletContext servletContext,GCRequest gcRequest, GCResponse gcResponse)
			throws Exception {

		GetWatchlistRequest watchlistRequest = new GetWatchlistRequest();
		/*** User info ***/
		watchlistRequest.setJKey(session.getjKey());
		watchlistRequest.setUserID(session.getUserID());
		watchlistRequest.setGroupId(session.getGroupId());
		watchlistRequest.setJSession(session.getjSessionID());

		GetWatchListAPI watchListAPI = new GetWatchListAPI();
		GetWatchListResponse watchlistResp =new GetWatchListResponse();
		try {
		watchlistResp= watchListAPI.post(watchlistRequest, GetWatchListResponse.class,
				session.getAppID(),"GetProfileList");
		}
		catch(GCException e) {
			log.debug(e);
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(watchlistRequest,session, servletContext, gcRequest, gcResponse)) {
                    try {
                	    watchlistResp= watchListAPI.post(watchlistRequest, GetWatchListResponse.class,
            				session.getAppID(),"GetProfileList");
                    }
                    catch(GCException ex) {
                        log.debug(ex+"  After ReInitiate Login");
                        throw new RequestFailedException();
                    }
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
		}

		List<JSONObject> watchObjList = new LinkedList<JSONObject>();

		/*** Fetch available watchlists ***/
		String defaultWatchlistProfileID = "0";
		boolean isWatchlist1Exist = false;
		JSONObject watchList1Obj = new JSONObject();

		for (GetWatchListObject response : watchlistResp.getResponseObject()) 
		{

			JSONObject watchListObj = new JSONObject();
			String profileName = response.getProfileName();
			String profileID = response.getProfileId();
			

			//TO identify the default watchlist profile ID from FT end
			if (response.getProfileId().equals(DeviceConstants.DEFAULT_PROFILE_ID)&&response.getProfileName().equals(DeviceConstants.DEFAULT_PROFILE_NAME)) {	
				defaultWatchlistProfileID = profileID;
			} else {
				watchListObj.put(DeviceConstants.WATCHLIST_ID, profileID);
				watchListObj.put(DeviceConstants.WATCHLIST_NAME, profileName);
				watchListObj.put(DeviceConstants.IS_MODIFIABLE, AppConstants.STR_TRUE);
				watchListObj.put(DeviceConstants.IS_RENAME_ALLOWED, 
				(profileName.equalsIgnoreCase(DeviceConstants.DEFAULT_WATCHLIST1)?AppConstants.STR_FALSE:AppConstants.STR_TRUE));
				watchListObj.put(DeviceConstants.IS_DEFAULT,
				 (profileName.equalsIgnoreCase(DeviceConstants.DEFAULT_WATCHLIST1)?AppConstants.STR_TRUE:AppConstants.STR_FALSE));
				watchListObj.put(DeviceConstants.ECHO_FOR_API, AppConstants.EMPTY_STR);
				watchListObj.put(DeviceConstants.RELOAD_ON_UPDATE_WATCHLIST, AppConstants.STR_FALSE);

				if(profileName.equalsIgnoreCase(DeviceConstants.DEFAULT_WATCHLIST1))
					watchList1Obj = watchListObj;
				else
					watchObjList.add(watchListObj);
			}

			if (!isWatchlist1Exist && profileName.equalsIgnoreCase(DeviceConstants.DEFAULT_WATCHLIST1))
				isWatchlist1Exist = true;
		}

		

		if (!isWatchlist1Exist) {
			JSONObject watchListObj = new JSONObject();
			watchListObj.put(DeviceConstants.WATCHLIST_ID, defaultWatchlistProfileID);
			watchListObj.put(DeviceConstants.WATCHLIST_NAME, DeviceConstants.DEFAULT_WATCHLIST1);
			watchListObj.put(DeviceConstants.IS_MODIFIABLE, AppConstants.STR_TRUE);
			watchListObj.put(DeviceConstants.IS_RENAME_ALLOWED, AppConstants.STR_FALSE);
			watchListObj.put(DeviceConstants.IS_DEFAULT, AppConstants.STR_TRUE);
			watchListObj.put(DeviceConstants.ECHO_FOR_API, DeviceConstants.CREATE_WATCH_ON_UPDATE);
			watchListObj.put(DeviceConstants.RELOAD_ON_UPDATE_WATCHLIST, AppConstants.STR_TRUE);
			watchObjList.add(0, watchListObj);
		}
		else
		{
			watchObjList.add(0, watchList1Obj);
		}

		return watchObjList;
	}

	public static boolean updateWatchList_101(Session session, String userID, String watchlistId, String watchListName,
			String echoForAPI, JSONArray symbols, boolean reloadOnUpdate,ServletContext servletContext,
			GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		CreateWatchlistRequest updateRequest = new CreateWatchlistRequest();
		/*** User info ***/
		updateRequest.setUserID(session.getUserID());
		updateRequest.setGroupId(session.getGroupId());
		updateRequest.setWatchlistName(watchListName);

		/**
		 * If update request received for default watchlist1, then watchlist should be
		 * created.
		 */
		if (echoForAPI.equalsIgnoreCase(DeviceConstants.CREATE_WATCH_ON_UPDATE)
				&& watchListName.equalsIgnoreCase(DeviceConstants.DEFAULT_WATCHLIST1)) {
			updateRequest.setWatchlistId("-1");
			reloadOnUpdate = true;
		} else {
			updateRequest.setWatchlistId(watchlistId);
		}

		if (PredefinedWatchList.isPredefinedWatch(watchListName, watchlistId))
			return false;

		/*** Symbol info ***/
		if (symbols.length() == 0) {
			updateRequest.addSymbols();
		} else {
			List<String> tokenMktSegIdList = new ArrayList<String>();
			for (int i = 0; i < symbols.length(); i++) {
				tokenMktSegIdList.add(symbols.getJSONObject(i).getString(SymbolConstants.SYMBOL_TOKEN));
			}
			updateRequest.addSymbols(tokenMktSegIdList);

		}
		updateRequest.setJKey(session.getjKey());
		updateRequest.setJSession(session.getjSessionID());

		SaveWatchlistAPI api = new SaveWatchlistAPI();
		SaveWatchlistResponse resp =new SaveWatchlistResponse();
		try {
		resp= api.post(updateRequest, SaveWatchlistResponse.class, session.getAppID()
				,"SaveWatchlist");
		}
		catch(GCException e) {
			log.debug(e);
            if(e.getInfoId().equalsIgnoreCase(InfoIDConstants.INVALID_SESSION)) {
                if(GCUtils.reInitiateLogIn(updateRequest,session, servletContext, gcRequest, gcResponse)) {
                    try {
                	    resp= api.post(updateRequest, SaveWatchlistResponse.class, session.getAppID()
            				,"SaveWatchlist");
                    }
                    catch(GCException ex) {
                        log.debug(ex+"  After ReInitiate Login");
                        throw new RequestFailedException();
                    }
                    session = gcRequest.getSession();
                }
                else 
                    throw new GCException (InfoIDConstants.INVALID_SESSION, "User logged out successfully");
            } 
            else 
				throw new RequestFailedException();
		}
		if (resp.getResponseStatus()) {
			return true;
		} else
			throw new RequestFailedException();

	}

	private static JSONArray getPredefinedWatchlist() throws JSONException, SQLException
	{
		JSONArray predefinedWatchListArray = new JSONArray();
		for (Map.Entry<String, String> mapEntry : PredefinedWatchList.getPredefinedWatchlist().entrySet())
		{
			JSONObject watchListObj = new JSONObject();
			watchListObj.put(DeviceConstants.WATCHLIST_NAME, mapEntry.getKey());
			watchListObj.put(DeviceConstants.WATCHLIST_ID, mapEntry.getValue());			
			watchListObj.put(DeviceConstants.IS_MODIFIABLE, AppConstants.STR_FALSE);
			watchListObj.put(DeviceConstants.IS_RENAME_ALLOWED, AppConstants.STR_FALSE);
			watchListObj.put(DeviceConstants.IS_DEFAULT, AppConstants.STR_FALSE);
			watchListObj.put(DeviceConstants.ECHO_FOR_API, AppConstants.EMPTY_STR);
			watchListObj.put(DeviceConstants.RELOAD_ON_UPDATE_WATCHLIST, AppConstants.STR_FALSE);
			predefinedWatchListArray.put(watchListObj);
		}
		return predefinedWatchListArray;
	}
}
