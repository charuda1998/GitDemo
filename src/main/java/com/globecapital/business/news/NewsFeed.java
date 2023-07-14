package com.globecapital.business.news;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ls.news.GetCompaniesRows;
import com.globecapital.api.ls.news.GetElementsRows;
import com.globecapital.api.ls.news.GetNewsRows;
import com.globecapital.api.ls.news.NewsFeedAPI;
import com.globecapital.api.ls.news.NewsFeedRequest;
import com.globecapital.api.ls.news.NewsFeedResponse;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.db.NewsDBPool;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.globecapital.services.exception.GCException;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.DateUtils;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class NewsFeed {

	private static Logger log = Logger.getLogger(NewsFeed.class);

	public static JSONArray getNewsFeed() throws JSONException, ParseException {

		String serviceUrl;

		JSONArray newsArray = new JSONArray();

		try {

			serviceUrl = AppConfig.getValue("lsUrl");

			NewsFeedRequest newsrequest = new NewsFeedRequest();

			NewsFeedAPI api = new NewsFeedAPI(serviceUrl);

			NewsFeedResponse newsResponse = api.get(newsrequest, NewsFeedResponse.class, "NewsJob","NewsFeed");

			List<GetNewsRows> newsRows = newsResponse.getItems();

			for (GetNewsRows rows : newsRows) {

				JSONObject newsObj = new JSONObject();

				newsObj.put(DeviceConstants.GUID, rows.getGuid());

				String desc = rows.getDescription();
				if ( desc.startsWith(" ")) {
					int indx = desc.indexOf(" ");
					desc = desc.substring(indx+1);
				}
				newsObj.put(DeviceConstants.NEWS_DESCRIPTION, desc);

				String date = rows.getDate();
				String time = DateUtils.formatDate(date, DeviceConstants.NEWS_TIME_FROM_FORMAT, DeviceConstants.NEWS_TIME_TO_FORMAT);

				newsObj.put(DeviceConstants.TIME, time);

				List<String> categories = rows.getCategories();
				if (categories.get(0).equalsIgnoreCase(DeviceConstants.N_DEFAULT)) {
					newsObj.put(DeviceConstants.CATEGORY, DeviceConstants.EQUITY);
					newsObj.put(DeviceConstants.NEWS_WEIGHTAGE, "3");
				} else if (categories.get(0).equalsIgnoreCase(DeviceConstants.BLOCK_DETAILS)) {
					newsObj.put(DeviceConstants.CATEGORY, DeviceConstants.BLOCK_DEALS);
					newsObj.put(DeviceConstants.NEWS_WEIGHTAGE, "1");
				} else if (categories.get(0).equalsIgnoreCase(DeviceConstants.FIXED_INCOME)) {
					newsObj.put(DeviceConstants.CATEGORY, DeviceConstants.N_FIXED_INCOME);
					newsObj.put(DeviceConstants.NEWS_WEIGHTAGE, "2");
				} else {
					newsObj.put(DeviceConstants.CATEGORY, categories.get(0));
					newsObj.put(DeviceConstants.NEWS_WEIGHTAGE, "4");
				}

				List<GetElementsRows> customRows = rows.getCustom();
				for (GetElementsRows elements : customRows) {
					List<GetCompaniesRows> companyRows = elements.getCompanies();
					if (companyRows == null)
						continue;
					for (GetCompaniesRows company : companyRows) {
						String nse = company.getNSE();
						String bse = company.getBSE();

						if (!nse.isEmpty()) {
							// nse+="_NSE";
							newsObj.put(DeviceConstants.SYMBOL, nse);
						} else if (!bse.isEmpty()) {
							// bse+="_BSE";
							newsObj.put(DeviceConstants.SYMBOL, bse);
						}
						// newsObj.put("scriptFlag", 1);
					}
				}

				date = DateUtils.formatDate(date, DeviceConstants.NEWS_TIME_FROM_FORMAT, DeviceConstants.NEWS_DATE_TO_FORMAT);
				newsObj.put(DeviceConstants.DATE_LS, date);
				newsArray.put(newsObj);

			}

		} catch (GCException e) {
			log.info(e);
		}

		return newsArray;
	}

	public static JSONObject getNewsByCategoryFromDB(String appID, String category, JSONObject paginationObj)
			throws SQLException, JSONException, ParseException, AppConfigNoKeyFoundException {

		JSONObject finalObj = new JSONObject();
		JSONArray newsArray = new JSONArray();
		ArrayList<String> dateList = new ArrayList<String>();
		ArrayList<JSONArray> newsList = new ArrayList<JSONArray>();
		JSONArray news = new JSONArray();

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = "";
		
		int limit = AppConfig.getIntValue("news_limit");
		
		try {
			con = NewsDBPool.getInstance().getConnection();
			if(paginationObj==null)
				query = DBQueryConstants.GET_NEWS_NOW;
			else
				query = DBQueryConstants.GET_NEWS;
			ps = con.prepareStatement(query);
			ps.setString(1, category);
			ps.setInt(2, limit);
			if(paginationObj!=null) 
				ps.setString(3, paginationObj.getString(DeviceConstants.TIME));
			else
				paginationObj = new JSONObject();
			rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject newsObj = new JSONObject();
				JSONObject newsArrayObj = new JSONObject();

				String title = "", hasTitle = "false";
				String desc = rs.getString(DBConstants.NEWS_DESCRIPTION);
				int index = desc.indexOf(":");
				if (index >= 0) {
					title = desc.substring(0, index);
					desc = desc.substring(index + 1, desc.length());
					hasTitle = "true";
					if ( desc.startsWith(" ")) {
						int indx = desc.indexOf(" ");
						desc = desc.substring(indx+1);
					}
				}

				newsObj.put(DeviceConstants.HAS_TITLE, hasTitle);
				newsObj.put(DeviceConstants.TITLE, title);
				newsObj.put(DeviceConstants.NEWS_DESCRIPTION, desc);
				newsObj.put(DeviceConstants.TIME, rs.getString(DBConstants.TIME));
				if(category.equalsIgnoreCase("All"))
					newsObj.put(DeviceConstants.CATEGORY, rs.getString(DBConstants.CATEGORY));
				// newsObj.put("symbol", rs.getString("symbol"));

				String date = rs.getString(DBConstants.DATE);
				if (!dateList.contains(date)) {
					dateList.add(date);
					news = new JSONArray();
				} else {
					news = newsArray.getJSONObject(dateList.indexOf(date)).getJSONArray(DeviceConstants.NEWS);

				}
				news.put(newsObj);
				newsList.add(dateList.indexOf(date), news);

				newsArrayObj.put(DeviceConstants.DATE_LS, date);

				if (date.equals(DateUtils.formatDate(DateUtils.getCurrentDate(), DBConstants.NEWS_FROM_FORMAT, DBConstants.NEWS_TO_FORMAT)))
					newsArrayObj.put(DeviceConstants.DATE_LS, DeviceConstants.TODAY);
				else
					newsArrayObj.put(DeviceConstants.DATE_LS, date);

				newsArrayObj.put(DeviceConstants.NEWS, newsList.get(dateList.indexOf(date)));
				newsArray.put(dateList.indexOf(date), newsArrayObj);
				paginationObj.put(DeviceConstants.TIME, rs.getString(DBConstants.NEWS_TIME));
			}
	
			finalObj.put(DeviceConstants.PAGINATION_OBJ, paginationObj);
			finalObj.put(DeviceConstants.NEWSARRAY,newsArray);
		} finally {
			Helper.closeResultSet(rs);
			Helper.closeStatement(ps);
			Helper.closeConnection(con);
		}
		return finalObj;

	}

	public static JSONObject getNewsByCompanyFromDB(String appID, String symToken, JSONObject paginationObj)
			throws SQLException, JSONException, ParseException, AppConfigNoKeyFoundException {

		JSONObject finalObj = new JSONObject();
		
		SymbolRow symRow = SymbolMap.getSymbolRow(symToken);
		String company = symRow.getSymbol();
		String mrktSegId = symRow.getMktSegId();
		
		JSONArray newsArray = new JSONArray();
		ArrayList<String> dateList = new ArrayList<String>();
		ArrayList<JSONArray> newsList = new ArrayList<JSONArray>();
		JSONArray news = new JSONArray();

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = "";
		
		int limit = AppConfig.getIntValue("news_limit");
		
		try {
			con = NewsDBPool.getInstance().getConnection();
			if(paginationObj==null)
				query = DBQueryConstants.GET_NEWS_BY_COMPANY_NOW;
			else 
				query = DBQueryConstants.GET_NEWS_BY_COMPANY;
			

			ps = con.prepareStatement(query);
			ps.setString(1, company);
			if(ExchangeSegment.isCommoditySegment(mrktSegId))
				ps.setString(2,"1");
			else
				ps.setString(2,"0");
			ps.setInt(3, limit);
			if(paginationObj!=null) 
				ps.setString(4, paginationObj.getString(DeviceConstants.TIME));
			else
				paginationObj = new JSONObject();
			rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject newsObj = new JSONObject();
				JSONObject newsArrayObj = new JSONObject();

				String title = "", hasTitle = "false";
				String desc = rs.getString(DBConstants.NEWS_DESCRIPTION);
				int index = desc.indexOf(":");
				if (index >= 0) {
					title = desc.substring(0, index);
					desc = desc.substring(index + 1, desc.length());
					hasTitle = "true";
					if ( desc.startsWith(" ")) {
						int indx = desc.indexOf(" ");
						desc = desc.substring(indx+1);
					}
				}

				newsObj.put(DeviceConstants.HAS_TITLE, hasTitle);
				newsObj.put(DeviceConstants.TITLE, title);
				newsObj.put(DeviceConstants.NEWS_DESCRIPTION, desc);
				newsObj.put(DeviceConstants.CATEGORY, rs.getString(DBConstants.CATEGORY));
				newsObj.put(DeviceConstants.TIME, rs.getString(DBConstants.TIME));
				// newsObj.put("symbol", rs.getString("symbol"));

				String date = rs.getString(DBConstants.DATE);
				if (!dateList.contains(date)) {
					dateList.add(date);
					news = new JSONArray();
				} else {
					news = newsArray.getJSONObject(dateList.indexOf(date)).getJSONArray(DeviceConstants.NEWS);

				}
				news.put(newsObj);
				newsList.add(dateList.indexOf(date), news);

				newsArrayObj.put(DeviceConstants.DATE_LS, date);

				if (date.equals(DateUtils.formatDate(DateUtils.getCurrentDate(), DBConstants.NEWS_FROM_FORMAT, DBConstants.NEWS_TO_FORMAT)))
					newsArrayObj.put(DeviceConstants.DATE_LS, "Today");
				else
					newsArrayObj.put(DeviceConstants.DATE_LS, date);

				newsArrayObj.put(DeviceConstants.NEWS, newsList.get(dateList.indexOf(date)));
				newsArray.put(dateList.indexOf(date), newsArrayObj);

				paginationObj.put(DeviceConstants.TIME, rs.getString(DBConstants.NEWS_TIME));
			}
			finalObj.put(DeviceConstants.PAGINATION_OBJ, paginationObj);
			finalObj.put(DeviceConstants.NEWSARRAY,newsArray);
		} finally {
			Helper.closeResultSet(rs);
			Helper.closeStatement(ps);
			Helper.closeConnection(con);
		}
		return finalObj;

	}

	public static JSONArray getNewsBySymbolFromDB(String appID, String company)
			throws SQLException, JSONException, ParseException {

		JSONArray newsArray = new JSONArray();

		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = "";
		try {
			con = NewsDBPool.getInstance().getConnection();
			query = DBQueryConstants.GET_NEWS_BY_SYMBOL;

			ps = con.prepareStatement(query);
			ps.setString(1, company);
			ps.setInt(2, DBConstants.LIMIT);
			rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject newsObj = new JSONObject();
				
				newsObj.put(DeviceConstants.CATEGORY, rs.getString(DBConstants.CATEGORY));
				newsObj.put(DeviceConstants.DESCRIPTION, rs.getString(DBConstants.NEWS_DESCRIPTION));
				Date date = DateUtils.getDate(rs.getString(DBConstants.DATE), DBConstants.EXPIRY_DATE_FROM_FORMAT);
				String formattedDate = DateUtils.getFormattedDayMonth(date);
				String formattedTime = DateUtils.formatDate(rs.getString(DBConstants.TIME), DBConstants.NEWS_TIME_FORMAT, DeviceConstants.NEWS_TIME_FORMAT);
				newsObj.put(DeviceConstants.DATE_LS, formattedDate + " " + formattedTime);
				newsArray.put(newsObj);
				
			}
		} finally {
			Helper.closeResultSet(rs);
			Helper.closeStatement(ps);
			Helper.closeConnection(con);
		}
		return newsArray;

	}
	public static JSONObject getNewsByMarketsFromDB (String appID) throws SQLException {
		JSONObject news = new JSONObject();
	
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = "";
		try {
			con = NewsDBPool.getInstance().getConnection();
			query = DBQueryConstants.GET_NEWS_BY_MARKET;

			ps = con.prepareStatement(query);
			
			rs = ps.executeQuery();

			while (rs.next()) {
				news.put(DeviceConstants.CATEGORY, rs.getString(DBConstants.CATEGORY));
				news.put(DeviceConstants.DESCRIPTION, rs.getString(DBConstants.NEWS_DESCRIPTION));
				
			}
		} finally {
			Helper.closeResultSet(rs);
			Helper.closeStatement(ps);
			Helper.closeConnection(con);
		}
		return news;
	}
	
	public static JSONArray searchNews(String searchString, String category) throws Exception {
		JSONArray newsArray = new JSONArray();
		
		Connection conn = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = "";
		
		int limit = AppConfig.getIntValue("news_limit");
		
		try {
			conn = NewsDBPool.getInstance().getConnection();
			query = DBQueryConstants.SEARCH_NEWS;
			ps = conn.prepareStatement(query);
			String search = "%" + searchString + "%";
			ps.setString(1,search);
			ps.setString(2, category);
			ps.setInt(3, limit);
			rs = ps.executeQuery();
			while (rs.next()) {
				JSONObject newsObj = new JSONObject();
				String title = "", hasTitle = "false";
				String desc = rs.getString(DBConstants.NEWS_DESCRIPTION);
				int index = desc.indexOf(":");
				if (index >= 0) {
					title = desc.substring(0, index);
					desc = desc.substring(index + 1, desc.length());
					hasTitle = "true";
					if ( desc.startsWith(" ")) {
						int indx = desc.indexOf(" ");
						desc = desc.substring(indx+1);
					}
				}

				newsObj.put(DeviceConstants.HAS_TITLE, hasTitle);
				newsObj.put(DeviceConstants.TITLE, title);
				String symbolUniqDesc = "";
				symbolUniqDesc = rs.getString(DBConstants.SYMBOL)+"_"+ExchangeSegment.NSE;
				SymbolRow symbolObj = new SymbolRow();
				symbolObj = SymbolMap.getSymbolUniqDescRow(symbolUniqDesc);
				if(symbolObj==null) {
					symbolUniqDesc = rs.getString(DBConstants.SYMBOL)+"_"+ExchangeSegment.BSE;
					symbolObj = (SymbolRow) SymbolMap.getSymbolUniqDescRow(symbolUniqDesc);
				}if(symbolObj!=null) {
					if(hasTitle.equals("false")) {
						newsObj.put(DeviceConstants.IS_CLICKABLE, "false");
					}else {
						newsObj.put(DeviceConstants.SYMBOL_OBJECT, symbolObj.getMinimisedSymbolRow().getJSONObject(SymbolConstants.SYMBOL_OBJ));
						newsObj.put(DeviceConstants.IS_CLICKABLE, "true");
					}
				}else
					newsObj.put(DeviceConstants.IS_CLICKABLE, "false");
				newsObj.put(DeviceConstants.NEWS_DESCRIPTION, desc);
				newsObj.put(DeviceConstants.CATEGORY, rs.getString(DBConstants.CATEGORY));
				newsObj.put(DeviceConstants.TIME, rs.getString(DBConstants.TIME));
				newsObj.put(DeviceConstants.DATE, rs.getString(DBConstants.DATE));
				newsArray.put(newsObj);
				
			}
		} finally {
			Helper.closeResultSet(rs);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return newsArray;
		}
	
	public static JSONObject getNewsByCategoryFromDB_101(String appID, String category, JSONObject paginationObj)
			throws SQLException, JSONException, ParseException, AppConfigNoKeyFoundException {

		JSONObject finalObj = new JSONObject();
		JSONArray newsArray = new JSONArray();
		ArrayList<String> dateList = new ArrayList<String>();
		ArrayList<JSONArray> newsList = new ArrayList<JSONArray>();
		JSONArray news = new JSONArray();
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement ps = null;
		String query = "";
		
		int limit = AppConfig.getIntValue("news_limit");
		
		try {
			con = NewsDBPool.getInstance().getConnection();
			if(paginationObj==null)
				query = DBQueryConstants.GET_NEWS_NOW;
			else
				query = DBQueryConstants.GET_NEWS;
			ps = con.prepareStatement(query);
			ps.setString(1, category);
			ps.setInt(2, limit);
			if(paginationObj!=null) 
				ps.setString(3, paginationObj.getString(DeviceConstants.TIME));
			else
				paginationObj = new JSONObject();
			rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject newsObj = new JSONObject();
				JSONObject newsArrayObj = new JSONObject();

				String title = "", hasTitle = "false";
				String desc = rs.getString(DBConstants.NEWS_DESCRIPTION);
				int index = desc.indexOf(":");
				if (index >= 0) {
					title = desc.substring(0, index);
					desc = desc.substring(index + 1, desc.length());
					hasTitle = "true";
					if ( desc.startsWith(" ")) {
						int indx = desc.indexOf(" ");
						desc = desc.substring(indx+1);
					}
				}

				newsObj.put(DeviceConstants.HAS_TITLE, hasTitle);
				newsObj.put(DeviceConstants.TITLE, title);
				String symbolUniqDesc = "";
				symbolUniqDesc = rs.getString(DBConstants.SYMBOL)+"_"+ExchangeSegment.NSE;
				SymbolRow symbolObj = new SymbolRow();
				symbolObj = SymbolMap.getSymbolUniqDescRow(symbolUniqDesc);
				if(symbolObj==null) {
					symbolUniqDesc = rs.getString(DBConstants.SYMBOL)+"_"+ExchangeSegment.BSE;
					symbolObj = (SymbolRow) SymbolMap.getSymbolUniqDescRow(symbolUniqDesc);
				}if(symbolObj!=null) {
					if(hasTitle.equals("false")) {
						newsObj.put(DeviceConstants.IS_CLICKABLE, "false");
					}else {
						newsObj.put(DeviceConstants.SYMBOL_OBJECT, symbolObj.getMinimisedSymbolRow().getJSONObject(SymbolConstants.SYMBOL_OBJ));
						newsObj.put(DeviceConstants.IS_CLICKABLE, "true");
					}
				}else
					newsObj.put(DeviceConstants.IS_CLICKABLE, "false");
				newsObj.put(DeviceConstants.NEWS_DESCRIPTION, desc);
				newsObj.put(DeviceConstants.TIME, rs.getString(DBConstants.TIME));
				if(category.equalsIgnoreCase("All"))
					newsObj.put(DeviceConstants.CATEGORY, rs.getString(DBConstants.CATEGORY));
				// newsObj.put("symbol", rs.getString("symbol"));

				String date = rs.getString(DBConstants.DATE);
				if (!dateList.contains(date)) {
					dateList.add(date);
					news = new JSONArray();
				} else {
					news = newsArray.getJSONObject(dateList.indexOf(date)).getJSONArray(DeviceConstants.NEWS);

				}
				news.put(newsObj);
				newsList.add(dateList.indexOf(date), news);

				newsArrayObj.put(DeviceConstants.DATE_LS, date);

				if (date.equals(DateUtils.formatDate(DateUtils.getCurrentDate(), DBConstants.NEWS_FROM_FORMAT, DBConstants.NEWS_TO_FORMAT)))
					newsArrayObj.put(DeviceConstants.DATE_LS, DeviceConstants.TODAY);
				else
					newsArrayObj.put(DeviceConstants.DATE_LS, date);

				newsArrayObj.put(DeviceConstants.NEWS, newsList.get(dateList.indexOf(date)));
				newsArray.put(dateList.indexOf(date), newsArrayObj);
				paginationObj.put(DeviceConstants.TIME, rs.getString(DBConstants.NEWS_TIME));
			}
	
			finalObj.put(DeviceConstants.PAGINATION_OBJ, paginationObj);
			finalObj.put(DeviceConstants.NEWSARRAY,newsArray);
		} finally {
			Helper.closeResultSet(rs);
			Helper.closeStatement(ps);
			Helper.closeConnection(con);
		}
		return finalObj;

	}

}