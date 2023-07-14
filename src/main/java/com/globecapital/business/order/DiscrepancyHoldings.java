package com.globecapital.business.order;

import java.text.ParseException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.GetDiscrepancyRow;
import com.globecapital.api.gc.backoffice.GetResolvedViewDiscrepancyRow;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.PriceFormat;

public class DiscrepancyHoldings {

	public static JSONArray getDiscrepancyDetails(List<GetDiscrepancyRow> discrepancyRows) {
		JSONArray finalArr = new JSONArray();
		
		for(int i = 0; i < discrepancyRows.size(); i++)
		{
			GetDiscrepancyRow discrepanyRow = discrepancyRows.get(i);
			
			String isinTokenSegment = discrepanyRow.getISIN();
			JSONObject obj = PositionsHelper.getSymbolObj(isinTokenSegment);
			
			if (obj != null) 
			{
				JSONObject discrepancyObj = new JSONObject();
				JSONObject symObj = obj;
				discrepancyObj.put(SymbolConstants.SYMBOL_OBJ, symObj.getJSONObject(SymbolConstants.SYMBOL_OBJ));
				
				JSONObject scripObj = new JSONObject();
				scripObj.put(DeviceConstants.SCRIP_CODE, discrepanyRow.getScripCode());
				scripObj.put(DeviceConstants.SCRIP_NAME, discrepanyRow.getScripName());
				discrepancyObj.put(DeviceConstants.SCRIP_DETAILS, scripObj);
				
				discrepancyObj.put(DeviceConstants.DISP_DISC_QTY, 
						PriceFormat.addComma(Integer.parseInt(discrepanyRow.getDiffQty()) 
								+ Integer.parseInt(discrepanyRow.getResolvedQty())));
				discrepancyObj.put(DeviceConstants.DISC_QTY, 
						Integer.toString((Integer.parseInt(discrepanyRow.getDiffQty()) 
								+ Integer.parseInt(discrepanyRow.getResolvedQty())
								)));
				
				int iResolvedQty = Integer.parseInt(discrepanyRow.getResolvedQty());
				if(iResolvedQty <= 0)
				{
					discrepancyObj.put(DeviceConstants.RESOLVED_QTY, "NA");
					discrepancyObj.put(DeviceConstants.ADD_TRADE, "true");
					discrepancyObj.put(DeviceConstants.IS_EDIT, "false");
				}
				else
				{
					discrepancyObj.put(DeviceConstants.RESOLVED_QTY, PriceFormat.addComma(iResolvedQty));
					discrepancyObj.put(DeviceConstants.ADD_TRADE, "false");
					discrepancyObj.put(DeviceConstants.IS_EDIT, "true");
				}
				
				finalArr.put(discrepancyObj);
			}
		}
		return finalArr;
		
	}

	public static JSONObject getResolvedDiscrepancyDetails(List<GetResolvedViewDiscrepancyRow> discrepancyRows) 
			throws JSONException, ParseException {
		JSONObject finalObj = new JSONObject();
		JSONArray finalArr = new JSONArray();
		JSONObject scripObj = new JSONObject();
		
		boolean isScripDetailsAdded = false;
		
		for(int i = 0; i < discrepancyRows.size(); i++)
		{
			GetResolvedViewDiscrepancyRow discrepanyRow = discrepancyRows.get(i);
			
			if(!isScripDetailsAdded)
			{
				scripObj.put(DeviceConstants.SCRIP_CODE, discrepanyRow.getScripCode());
				scripObj.put(DeviceConstants.SCRIP_NAME, discrepanyRow.getScripName());
				isScripDetailsAdded = true;
			}
				
			JSONObject discrepancyObj = new JSONObject();
			discrepancyObj.put(DeviceConstants.QTY, discrepanyRow.getQty().replaceAll(",", ""));
			discrepancyObj.put(DeviceConstants.PRICE, discrepanyRow.getRate().replaceAll(",", ""));
			discrepancyObj.put(DeviceConstants.TRANS_DATE, DateUtils.formatDate(discrepanyRow.getTrxDate(), 
					DeviceConstants.TRANS_DATE_API_FORMAT,
					DeviceConstants.TRANS_DATE_FORMAT).toUpperCase());
			discrepancyObj.put(DeviceConstants.REFERENCE_NO, discrepanyRow.getRefNo());
				
			finalArr.put(discrepancyObj);
		}
		
		finalObj.put(DeviceConstants.SCRIP_DETAILS, scripObj);
		finalObj.put(DeviceConstants.TRANSACTIONS, finalArr);
		
		return finalObj;
	}

}
