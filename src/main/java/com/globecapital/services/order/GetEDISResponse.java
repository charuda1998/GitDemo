package com.globecapital.services.order;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.exception.GCException;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class GetEDISResponse extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(GetEDISResponse.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
		String transactionStatus = "Transaction Successful";
		InputStream inp = req.getInputStream();
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(inp, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
			responseStrBuilder.append(inputStr);
		resp.setHeader("Content-type", "text/html");
		
		log.info("Request details EDISGateway :"+responseStrBuilder);
		
		/*
		 * Sample response : -> URL decode this response and get the substring starting from { which corresponds to %7B in url 
		 * encoded version.
		 *  
		 * __VIEWSTATE=1cJFBNFicpIBOgBhPGw5bCorYyf6HBELhNbATwgfC5W4p9bptO3b7LEbhN1NtTOrsGR7GHLHBjVt5A4XneOI8KrLkZzV9JLAVr5cZOK
		 * GGuOlfJlNzvPXF%2BfzTqOd1tgdhUgSaX0bKlJ%2Bb1UUP2Xqr%2FBtEudNSUeWuxWacTTorLTm%2FapaJQnCDqlCVApU3mGrf4k3FelVvbo%2FNfl4
		 * SfT2DXpSpF2rq6TFv0sfIyAthNwYCgIpzEDEtp3CongKaTuIXwugbjy9Fv%2BFLNrmuAq7Yw%3D%3D&__VIEWSTATEGENERATOR=B7E6AD66&__EVEN
		 * TVALIDATION=uTddxbTPOUWlqks2%2Fc8p8U1pXCou%2BMfuGwml3m5DV9aogJ21ysFD%2FdBftg9gzo%2FKAr3rYug6gcXYVX%2BMh4OmtD9MehkIW
		 * xkYtsphkojBcUu%2FQA5ecBFthufmBcvcuN39&Response=%7B%22Depository%22%3A%22NSDL%22%2C%22dpId%22%3A%22IN300966%22%2C%22
		 * clientId%22%3A%2212020600%22%2C%22isin%22%3A%22INE062A01020%22%2C%22OriginalQty%22%3Anull%2C%22BlkQty%22%3A%221%22%
		 * 2C%22Channel%22%3Anull%2C%22Status%22%3A%22Success%22%2C%22Remark%22%3Anull%2C%22RefNum%22%3A%2216%22%2C%22TransRef
		 * Num%22%3A%229980239005295007%22%2C%22StatusCode%22%3Anull%2C%22ScripDetails%22%3A%7B%22ISIN%22%3A%22INE062A01020%22
		 * %2C%22Qty%22%3A%221%22%2C%22Status%22%3A%22Success%22%2C%22StatusCode%22%3Anull%2C%22RefNo%22%3Anull%2C%22ISINName%
		 * 22%3A%22%22%2C%22ErrorDesc%22%3A%22%22%7D%2C%22BrokerOrdrNo%22%3Anull%2C%22ReqId%22%3A%22L025162988246854%22%2C%22U
		 * serId%22%3A%22L025%22%2C%22GroupId%22%3A%22HO%22%7D
		 */
		
		String result = URLDecoder.decode(responseStrBuilder.toString(), "UTF-8");
		JSONObject response = new JSONObject(result.substring(result.indexOf("{")));
		
		String status = response.getString(DeviceConstants.STATUS_S);
		if(status.equalsIgnoreCase(DeviceConstants.SUCCESS))
			transactionStatus = "Transaction Successful";
		else
			transactionStatus = "Transaction Failed";
		
		String successResponse = "<html>\r\n"
				+ "  <head >\r\n"
				+ "  </head>\r\n"
				+ "    <style>\r\n"
				+ "      body {\r\n"
				+ "        text-align: center;\r\n"
				+ "        padding: 40px 0;\r\n"
				+ "        background: #EBF0F5;\r\n"
				+ "      }\r\n"
				+ "        h1 {\r\n"
				+ "          color: #88B04B;\r\n"
				+ "          font-family: \"Nunito Sans\", \"Helvetica Neue\", sans-serif;\r\n"
				+ "          font-weight: 900;\r\n"
				+ "          font-size: 40px;\r\n"
				+ "          margin-bottom: 10px;\r\n"
				+ "        }\r\n"
				+ "        p {\r\n"
				+ "          color: #404F5E;\r\n"
				+ "          font-family: \"Nunito Sans\", \"Helvetica Neue\", sans-serif;\r\n"
				+ "          font-size:20px;\r\n"
				+ "          margin: 0;\r\n"
				+ "        }\r\n"
				+ "      i {\r\n"
				+ "        color: #9ABC66;\r\n"
				+ "        font-size: 100px;\r\n"
				+ "        line-height: 200px;\r\n"
				+ "        margin-left:-15px;\r\n"
				+ "      }\r\n"
				+ "      .card {\r\n"
				+ "        background: white;\r\n"
				+ "        padding: 60px;\r\n"
				+ "        border-radius: 4px;\r\n"
				+ "        box-shadow: 0 2px 3px #C8D0D8;\r\n"
				+ "        display: inline-block;\r\n"
				+ "        margin: 0 auto;\r\n"
				+ "      }\r\n"
				+ "	  .button {\r\n"
				+ "  border: none;\r\n"
				+ "  color: white;\r\n"
				+ "  padding: 15px 32px;\r\n"
				+ "  text-align: center;\r\n"
				+ "  text-decoration: none;\r\n"
				+ "  display: inline-block;\r\n"
				+ "  font-size: 16px;\r\n"
				+ "  margin: 4px 2px;\r\n"
				+ "  cursor: pointer;\r\n"
				+ "}\r\n"
				+ ".button1 {background-color: #4CAF50;}\r\n"
				+ "    </style>\r\n"
				+ "    <body>\r\n"
				+ "        <h1>" + transactionStatus
				+ " </h1> \r\n"
				+ "		</br>\r\n"
				+ "		<button type=\"button\" class=\"button button1\">Ok</button>\r\n"
				+ "		\r\n"
				+ "      </div>\r\n"
				+ "    </body>\r\n"
				+ "</html>";
		
		resp.getWriter().write(successResponse);

		log.info("Parsed Request details EDISGateway :"+response);
		if(response.getString(FTConstants.DEPOSITORY).equalsIgnoreCase(DeviceConstants.DEPOSITORY_CDSL)) {
			try {
				String userId = response.getString(FTConstants.USERID);
				String groupId = response.getString(FTConstants.GROUPID);
				JSONObject sessionDetails = getSessionDetailsFromDB(userId);
				sessionDetails.put(DBConstants.USER_ID, userId);
				sessionDetails.put(FTConstants.GROUPID, groupId);
				Boolean responseStatus = com.globecapital.business.edis.UpdateEDISApprovedQtyDetails.updateEDISApprovedQtyDetails(sessionDetails, response,getServletContext(),req);
				log.info("EDIS UpdateQtyDetails Response status :"+responseStatus);
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			}
		}
	}

	private static JSONObject getSessionDetailsFromDB(String userId) throws GCException, SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		String query = DBQueryConstants.GET_SESSION_INFO;
		JSONObject sessionDetails = new JSONObject();
		log.debug(query);
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, userId);
			res = ps.executeQuery();
			while(res.next()) {
				sessionDetails.put(DBConstants.J_KEY, res.getString(DBConstants.J_KEY));
				sessionDetails.put(DBConstants.FT_SESSION_ID, res.getString(DBConstants.FT_SESSION_ID));
				sessionDetails.put(DBConstants.APP_ID, res.getString(DBConstants.APP_ID));
			}
		}catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return sessionDetails;
	}
}
