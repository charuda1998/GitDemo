package com.globecapital.services.razorpay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.security.GeneralSecurityException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.globecapital.api.razorpay.generics.RazorPayConstants;
import com.globecapital.business.wcf.client.WCFClient;
import com.globecapital.business.wcf.db.WCFDbHandler;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBConstants;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.constants.WCFConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class GetPaymentGatewayResponse extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static Logger log = Logger.getLogger(GetPaymentGatewayResponse.class);

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
	
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
				+ "        <h1>Transaction Successful</h1> \r\n"
				+ "		</br>\r\n"
				+ "		<button type=\"button\" class=\"button button1\">Ok</button>\r\n"
				+ "		\r\n"
				+ "      </div>\r\n"
				+ "    </body>\r\n"
				+ "</html>";
		InputStream inp = req.getInputStream();
		BufferedReader streamReader = new BufferedReader(new InputStreamReader(inp, "UTF-8"));
		StringBuilder responseStrBuilder = new StringBuilder();
		String inputStr;
		while ((inputStr = streamReader.readLine()) != null)
			responseStrBuilder.append(inputStr);
		resp.setHeader("Content-type", "text/html");
		resp.getWriter().write(successResponse);
		
		log.info("Request details PaymentGateway :"+responseStrBuilder);
		
		String result = URLDecoder.decode(responseStrBuilder.toString(), "UTF-8");
		JSONObject response = new JSONObject(result.substring(result.indexOf("{")));
		try {
			UpdateTransactionResponse(response);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
		}
		log.info("Parsed Request details PaymentGateway :"+response);

	}
	
	private void UpdateTransactionResponse(JSONObject response) throws GeneralSecurityException{
		JSONObject paymentDetails = response.getJSONObject(RazorPayConstants.PAYLOAD).getJSONObject(RazorPayConstants.PAYMENT).getJSONObject(RazorPayConstants.ENTITY);
		String userId = paymentDetails.getJSONObject(RazorPayConstants.NOTES).getString(RazorPayConstants.CLIENT_CODE);
		String paymentId = paymentDetails.getString(RazorPayConstants.ID);
		try {
			String respStatus;
			JSONObject bankObj= new JSONObject();
			JSONObject userInfo= new JSONObject();
			JSONObject merchRef = getMerchantTransactionDetails(paymentDetails.getString(RazorPayConstants.ORDER_ID));
			log.info("Merchant Details from database"+merchRef);
			String amount = formatPaiseToPrice(String.valueOf( paymentDetails.getDouble(RazorPayConstants.AMOUNT)),100);
			if(!merchRef.isEmpty()) {
				String encryptedDetails=merchRef.getString(RazorPayConstants.BANK_INFO);
				String merchTransNo=merchRef.getString(DeviceConstants.MERCHANT_TRANS_NO);
				String bankInfo=AESEncryption.decrypt(merchTransNo,encryptedDetails);
				getBankDetails(bankObj,bankInfo,amount);  
				String groupId=bankObj.getString(RazorPayConstants.GROUP_ID);
				userInfo.put(UserInfoConstants.USER_NAME,bankObj.getString(UserInfoConstants.USER_NAME));
				Session session=setSession(userId,userInfo,groupId);
				if (paymentDetails.getString(DeviceConstants.STATUS).equals(RazorPayConstants.AUTHORIZED)) {
					WCFDbHandler.gatewayResReceived(DeviceConstants.SUCCESS,paymentId, merchTransNo);
					respStatus=WCFClient.limitUpdate(userId,amount,merchTransNo, AppConfig.getValue(RazorPayConstants.CLIENT_IP), session.getGroupId(), session.getAppID());
					if(WCFConstants.FUNDS_ADDED_SUCCESSFULLY.equals(respStatus))
						PostTransactionDetailsBackOffice.updateTransactionDetails(session,paymentDetails.getString(RazorPayConstants.METHOD),bankObj,paymentId,DeviceConstants.SUCCESS,DeviceConstants.SUCCESS, merchTransNo,respStatus);
					else
						PostTransactionDetailsBackOffice.updateTransactionDetails(session,paymentDetails.getString(RazorPayConstants.METHOD),bankObj,paymentId,RazorPayConstants.FAILURE,DeviceConstants.SUCCESS, merchTransNo,respStatus);
						log.info(respStatus+" "+session.getAppID());	
				}
			}
		} catch (GCException | SQLException e) {
			// TODO Auto-generated catch block
			log.error(e);
			e.printStackTrace();
		}
	}
	
	private static String getAppIdFromDB(String userId) throws GCException, SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		String query =DBQueryConstants.GET_MERCHANT_APP_ID; 
		String appId = "";
		log.debug(query);
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, userId);
			res = ps.executeQuery();
			while(res.next()) {
				appId= res.getString(DBConstants.APP_ID);
			}
		}catch (Exception e) {
			log.warn(e);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return appId;
	}
	
	public static JSONObject getMerchantTransactionDetails(String orderId) throws SQLException {
		Connection conn = null;
		PreparedStatement st = null;
		JSONObject merchRefNo = new JSONObject();
		try {
			conn = GCDBPool.getInstance().getConnection();
			log.debug("Update Query = "+DBQueryConstants.GET_MERCHANT_DETAILS);
			st = conn.prepareStatement(DBQueryConstants.GET_MERCHANT_DETAILS);
			st.setString(1,orderId);
			st.setString(2, DeviceConstants.SUCCESS);
			
			ResultSet rs = st.executeQuery();
			while(rs.next()) {
				merchRefNo.put(DeviceConstants.MERCHANT_TRANS_NO, rs.getString(RazorPayConstants.MERCHANT_REF_NO));
				merchRefNo.put(RazorPayConstants.BANK_INFO,rs.getString(RazorPayConstants.TRANS_ADDITIONAL_INFO));
			}
			log.info(rs+"    Updated Succesfully");
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
		return merchRefNo;
	}
	
	public static void getBankDetails(JSONObject bankObj, String bankInfo, String amount) {
		if (!bankInfo.isEmpty()){
			String[] bankDetails=bankInfo.split("\\|");
		    bankObj.put(RazorPayConstants.BANK_ACCOUNT,bankDetails[0]);
		    bankObj.put(RazorPayConstants.IFSC,bankDetails[1]);
		    bankObj.put(RazorPayConstants.BANK_NAME,bankDetails[2]);
		    bankObj.put(UserInfoConstants.USER_NAME,bankDetails[3]);
		    bankObj.put(RazorPayConstants.AMOUNT,amount);
		    bankObj.put(RazorPayConstants.GROUP_ID, bankDetails[4]);
		}
	}
	
	public static Session setSession(String userId,JSONObject userInfo,String groupId) throws GCException, SQLException {
		Session session =new Session();
		session.setUserId(userId);
		session.setAppID(getAppIdFromDB(userId));
		session.setUserInfo(userInfo);
		session.setGroupId(groupId);
		return session;
	}

	public static String formatPaiseToPrice(final String price, final double multiplier) {
		final Double dPrice = Double.parseDouble(price);
		DecimalFormat df = new DecimalFormat("0.00");
		if(dPrice > 0)
			return df.format((dPrice / multiplier));
		else
			return price;
	}
}
