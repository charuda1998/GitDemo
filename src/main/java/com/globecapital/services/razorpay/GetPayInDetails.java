package com.globecapital.services.razorpay;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import org.json.JSONObject;
import com.globecapital.api.razorpay.generics.CreateOrderAPI;
import com.globecapital.api.razorpay.generics.CreateOrderRequest;
import com.globecapital.api.razorpay.generics.CreateOrderResponse;
import com.globecapital.api.razorpay.generics.RazorPayConstants;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

public class GetPayInDetails extends SessionService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	static SecureRandom rand = new SecureRandom();

	private static Logger log = Logger.getLogger(GetPayInDetails.class);
	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
		Session session = gcRequest.getSession();
		JSONObject pgResponse =  new JSONObject();
		JSONObject pgData = new JSONObject();
		JSONObject pgEchoData = new JSONObject();
		String amount = gcRequest.getFromData(RazorPayConstants.AMOUNT);
		String type = gcRequest.getFromData(RazorPayConstants.TYPE);
		String bankName = gcRequest.getFromData(RazorPayConstants.BANK_NAME);
		String bankCode = gcRequest.getFromData(DeviceConstants.BANK_CODE);
		String vpa = gcRequest.getFromData(RazorPayConstants.VPA);
		String bankAccount = gcRequest.getFromData(DeviceConstants.BANK_ACCOUNT);
		String ifsc = gcRequest.getFromData(DeviceConstants.IFSC);
		String merchantTransNo = generateRandomString(8);
		String additionalDetails = bankAccount+"|"+ifsc+"|"+bankName+"|"+session.getUserInfo().getString(UserInfoConstants.USER_NAME)+"|"+session.getGroupId();
		String encryptedContent = AESEncryption.encrypt(merchantTransNo, additionalDetails);
		JSONObject bankAccountObj = new JSONObject();
		bankAccountObj.put(RazorPayConstants.ACCOUNT_NUMBER, bankAccount);
		bankAccountObj.put(RazorPayConstants.IFSC, ifsc);
		bankAccountObj.put(RazorPayConstants.NAME, "");
		
		JSONObject notesObj = new JSONObject();
		notesObj.put(RazorPayConstants.ACCOUNT_NUMBER, bankAccount);
		notesObj.put(RazorPayConstants.CLIENT_CODE, session.getUserID());
		notesObj.put(RazorPayConstants.BANK_NAME_, bankName);
		
		CreateOrderAPI createOrderAPI = new CreateOrderAPI();
		CreateOrderRequest razorPayRequest = new CreateOrderRequest();
		CreateOrderResponse razorPayResponse = new CreateOrderResponse();
		
		razorPayRequest.setAmount(new BigDecimal((amount)).multiply(new BigDecimal("100")).setScale(2));
		razorPayRequest.setMethod(type);
		razorPayRequest.setBankAccount(bankAccountObj);
		razorPayRequest.setReceipt(merchantTransNo);
		razorPayRequest.setCurrency(DeviceConstants.INR);
		razorPayRequest.setNotes(notesObj);

		razorPayResponse = createOrderAPI.post(razorPayRequest, CreateOrderResponse.class, session.getAppID(),"CreateOrder");
		
		pgResponse.put(DeviceConstants.PG_CHANNEL , DeviceConstants.RAZORPAY);
		pgResponse.put(DeviceConstants.URL , "");
		
		pgData.put(RazorPayConstants.CURRENCY, razorPayResponse.getCurrency());
		pgData.put(RazorPayConstants.ORDER_ID, razorPayResponse.getId());
		pgData.put(RazorPayConstants.AMOUNT, razorPayResponse.getAmount());
		pgData.put(RazorPayConstants.EMAIL, "void@razorpay.com");
		pgData.put(RazorPayConstants.CONTACT, "9999999999");
		pgData.put(RazorPayConstants.METHOD, type);
		if(type.equalsIgnoreCase(RazorPayConstants.NETBANKING))
			pgData.put(RazorPayConstants.BANK, bankCode);
		else if(type.equalsIgnoreCase(RazorPayConstants.UPI))
			pgData.put(RazorPayConstants.VPA, vpa);
		
		pgResponse.put(DeviceConstants.PG_DATA, pgData);
		pgResponse.put(DeviceConstants.METHOD, DeviceConstants.GET_METHOD);
		
		pgEchoData.put(DeviceConstants.MERCHANT_TRANS_NO, razorPayResponse.getReceipt());
		pgEchoData.put(DeviceConstants.PG_ORDER_ID, razorPayResponse.getId());
		pgEchoData.put(RazorPayConstants.AMOUNT,amount);
		pgResponse.put(DeviceConstants.ECHO_DETAILS, pgEchoData);
		gcResponse.setData(pgResponse);

		if(Objects.isNull(razorPayResponse.getError()) && razorPayResponse.getStatus().equalsIgnoreCase(DeviceConstants.CREATED)) {
			String clientCode = gcRequest.getSession().getUserID();
			String pgOrderId = razorPayResponse.getId();
			String displayBankDetails = gcRequest.getFromData(DeviceConstants.DISP_BANK_DETAILS);
			insertToDatabase(merchantTransNo,clientCode,amount, displayBankDetails,pgOrderId,type, encryptedContent);
			pgResponse.put(DeviceConstants.PG_CHANNEL , DeviceConstants.RAZORPAY);
			pgResponse.put(DeviceConstants.URL , "");
			
			pgData.put(RazorPayConstants.CURRENCY, razorPayResponse.getCurrency());
			pgData.put(RazorPayConstants.ORDER_ID, razorPayResponse.getId());
			pgData.put(RazorPayConstants.AMOUNT, razorPayResponse.getAmount());
			pgData.put(RazorPayConstants.EMAIL, "void@razorpay.com");
			pgData.put(RazorPayConstants.CONTACT, "9999999999");
			pgData.put(RazorPayConstants.METHOD, type);
			if(type.equalsIgnoreCase(RazorPayConstants.UPI)) {
				pgData.put(RazorPayConstants.BANK, bankCode);
				pgData.put(RazorPayConstants.VPA, vpa);
			}
			
			pgResponse.put(DeviceConstants.PG_DATA, pgData);
			pgResponse.put(DeviceConstants.METHOD, DeviceConstants.GET_METHOD);
			
			pgEchoData.put(DeviceConstants.MERCHANT_TRANS_NO, razorPayResponse.getReceipt());
			pgEchoData.put(DeviceConstants.PG_ORDER_ID, razorPayResponse.getId());
			pgEchoData.put(RazorPayConstants.METHOD, type);
			pgEchoData.put(RazorPayConstants.BANK_NAME, bankName);
			pgEchoData.put(RazorPayConstants.BANK_ACCOUNT, bankAccount);
			pgEchoData.put(RazorPayConstants.IFSC, ifsc);
			pgResponse.put(DeviceConstants.ECHO_DETAILS, pgEchoData);
			gcResponse.setData(pgResponse);
		}else {
			if(razorPayResponse.getError().getDescription().equals(InfoMessage.getInfoMSG("info_msg.bank_not_enabled")))
				throw new GCException(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.try_other_payment_mode"));
			else
				throw new GCException(InfoIDConstants.DYNAMIC_MSG, razorPayResponse.getError().getDescription());
		}
			
	}
	
	public static String generateRandomString(int len){
	   SimpleDateFormat sdf = new SimpleDateFormat(DeviceConstants.CURRENT_TIMESTAMP);
	   Date date = new Date();
	   StringBuilder sb = new StringBuilder(len);
	   for(int i = 0; i < len; i++)
	      sb.append(DeviceConstants.POSSIBLE_INPUTS .charAt(rand.nextInt(DeviceConstants.POSSIBLE_INPUTS.length())));
	   return sb.toString().concat(sdf.format(date));
	}
	
	public static void insertToDatabase(String merchantTransNo,String clientCode,String amount,String  bankAccount,String pgOrderId ,String type, String encryptedContent)throws SQLException{
		 
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet res = null;
			Double damount=Double.parseDouble(amount);
			try {
				conn = GCDBPool.getInstance().getConnection();
				ps = conn.prepareStatement(DBQueryConstants.INSERT_PAYIN_DETAILS);
				ps.setString(1 , merchantTransNo);
				ps.setString(2 , clientCode);
				ps.setDouble(3 , damount); 
				ps.setString(4 , bankAccount);
				ps.setString(5 , pgOrderId);
				ps.setString(6 , DeviceConstants.NOT_INITIATED);
				ps.setString(7 , DeviceConstants.PENDING);
				ps.setString(8 , RazorPayConstants.VENDOR_NAME );
				ps.setString(9 , type);
				ps.setString(10 , DeviceConstants.TRANS_INITIATED);
				ps.setString(11 , encryptedContent);
				ps.executeUpdate();
			}
			catch(Exception e) {
				log.warn(e);
			}finally {
				Helper.closeResultSet(res);
				Helper.closeStatement(ps);
				Helper.closeConnection(conn);
			}
	}

}
