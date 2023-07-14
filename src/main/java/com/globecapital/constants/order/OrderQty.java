package com.globecapital.constants.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;

import com.globecapital.config.AppConfig;
import com.globecapital.constants.DBQueryConstants;
import com.globecapital.db.GCDBPool;
import com.globecapital.services.exception.AppConfigNoKeyFoundException;
import com.msf.utils.helper.Helper;

public class OrderQty
{

    /** For derivatives, MCX exchange accepts quantity in lots whereas other segments accepts as quantity.
     *  Example: COTTON Futures, LotSize: 25, API accepts 1, From front end we will receive as 25
     * @throws SQLException 
     * @throws AppConfigNoKeyFoundException 
     */

    public static String formatToAPI(String quantity, Integer lotSize, String marketSegmentID) throws SQLException, AppConfigNoKeyFoundException
    {
        if(!marketSegmentID.equalsIgnoreCase(ExchangeSegment.NSECDS_SEGMENT_ID)) {
			Long l_qty = Long.parseLong(quantity);
			if(l_qty > 0)
			{
				l_qty = l_qty / getLotMultiplier(marketSegmentID, lotSize);
				return l_qty.toString();
			}
    	}
        return quantity;   
    }
    
    public static String getAppVersion(String appId) throws SQLException
	{
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet res = null;
		
		String version = "";
		
		try {
			conn = GCDBPool.getInstance().getConnection();
			ps = conn.prepareStatement(DBQueryConstants.GET_APP_VERSION);
			ps.setString(1, appId);

			res = ps.executeQuery();

			if (res.next()) 
				version = res.getString(1);
		} finally {
			Helper.closeResultSet(res);
			Helper.closeStatement(ps);
			Helper.closeConnection(conn);
		}
		return version;
	}

    public static String formatToDevice(String quantity, Integer lotSize, String marketSegmentID)
    {
        Long l_qty = Long.parseLong(quantity);
        if(l_qty != 0) //Quanity received in negative for Short sell orders
        {
            l_qty = l_qty * getLotMultiplier(marketSegmentID, lotSize);
            return l_qty.toString();
        }
        return quantity;        
    }

    private static Integer getLotMultiplier(String marketSegmentID, Integer lotSize)
    {
        switch(marketSegmentID){

            case ExchangeSegment.MCX_SEGMENT_ID:
            case ExchangeSegment.NSECDS_SEGMENT_ID:
            case ExchangeSegment.BSECDS_SEGMENT_ID:
                return lotSize;
            
            default:
                return 1;
        }

    }
    
    public static String formatToDeviceLot(String quantity, Integer lotSize, String marketSegmentID)
    {
    	if(!marketSegmentID.equalsIgnoreCase(ExchangeSegment.NSECDS_SEGMENT_ID)) {
	        Long l_qty = Long.parseLong(quantity);
	        if(l_qty != 0) //Quanity received in negative for Short sell orders
	        {
	            l_qty = l_qty * getLotMultiplier(marketSegmentID, lotSize);
	            return l_qty.toString();
	        }
    	}
        return quantity;           
    }
    
    public static String formatToAPILot(String quantity, Integer lotSize, String marketSegmentID)
    {
    	if(!marketSegmentID.equalsIgnoreCase(ExchangeSegment.NSECDS_SEGMENT_ID)) {
	        Long l_qty = Long.parseLong(quantity);
	        if(l_qty > 0)
	        {
	            l_qty = l_qty / getLotMultiplier(marketSegmentID, lotSize);
	            return l_qty.toString();
	        }
    	}
        return quantity;   
    }


// public static void main(String[] args) {

//     System.out.println(formatToAPI("9999999999", 1, "1"));
    
// }

}