package com.globecapital.business.report;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.report.GetAdvanceFundsViewRows;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.utils.PriceFormat;

public class FundsView {
	
	public static JSONArray getLimits(List<GetAdvanceFundsViewRows> fundsObj) {
		
		JSONArray limitList = new JSONArray();
		float fCashEquivalents = 0, fCollateral = 0, fProfitLoss = 0, fTotalUtilization = 0, fFundsAvailable = 0, fManualCollateral = 0;
		
		for(GetAdvanceFundsViewRows fund : fundsObj)
		{
			if(fund.getDescription().equals(FTConstants.CASH_ADHOC_DEPOSIT) ||
					fund.getDescription().equals(FTConstants.CASH_OVERDRAFT_LIMIT) ||
					fund.getDescription().equals(FTConstants.CASH_NOTIONAL_DEPOSIT) ||
					fund.getDescription().equals(FTConstants.CASH_MISC_DEPOSIT) ||
					fund.getDescription().equals(FTConstants.CASH_DP_CFS) ||
					fund.getDescription().equals(FTConstants.CASH_POOL_CFS) ||
					fund.getDescription().equals(FTConstants.CASH_SAR_CFS) ||
					fund.getDescription().equals(FTConstants.CASH_OPTION_CFS) ||
					fund.getDescription().equals(FTConstants.CASH_DEPOSIT)||
					fund.getDescription().equals(FTConstants.FUNDS_TRANSFERRED_TODAY))
				fCashEquivalents = fCashEquivalents + fund.getnTrading();
			else if(fund.getDescription().equals(FTConstants.DP_COLLATERAL) ||
					fund.getDescription().equals(FTConstants.POOL_COLLATERAL) ||
					fund.getDescription().equals(FTConstants.SAR_COLLATERAL) ||
					fund.getDescription().equals(FTConstants.DP_PLEDGED_COLLATERAL) ||
					fund.getDescription().equals(FTConstants.POOL_PLEDGED_COLLATERAL))
				fCollateral = fCollateral + fund.getnTrading();
			else if(fund.getDescription().equals(FTConstants.BOOKED_PROFIT_LOSS) ||
					fund.getDescription().equals(FTConstants.MTM_PROFIT_LOSS))
				fProfitLoss = fProfitLoss + fund.getnTrading();
			else if(fund.getDescription().equals(FTConstants.TOTAL_UTILIZATION))
				fTotalUtilization = fund.getnTrading();
			else if(fund.getDescription().equals(FTConstants.NET_AVAILABLE_FUNDS))
				fFundsAvailable = fund.getnTrading();
			else if(fund.getDescription().equals(FTConstants.CASH_MANUAL_COLLATERAL))
				fManualCollateral = fund.getnTrading();
		}
		
		limitList.put(getCashEquivalents(fCashEquivalents));
		limitList.put(getCollateral(fCollateral));
		limitList.put(getProfitLoss(fProfitLoss));
		limitList.put(getFundUtilization(fTotalUtilization));
		limitList.put(getFundsAvailable(fFundsAvailable - fManualCollateral));
		return limitList;
	}
	
	public static JSONArray getDefaultLimits() {
		JSONArray limitList = new JSONArray();
		limitList.put(getCashEquivalents(0));
		limitList.put(getCollateral(0));
		limitList.put(getProfitLoss(0));
		limitList.put(getFundUtilization(0));
		limitList.put(getFundsAvailable(0));
		return limitList;
	}
	
	public static JSONObject getCashEquivalents(float fCashEquivalents)
	{
		JSONObject cashObj = new JSONObject();
		cashObj.put(DeviceConstants.DESCRIPTION, DeviceConstants.CASH_EQUIVALENTS);
		cashObj.put(DeviceConstants.BALANCE,PriceFormat.formatFloat(fCashEquivalents, 2));
		
		return cashObj;
	}

	public static JSONObject getCollateral(float fCollateral)
	{
		JSONObject collateralObj = new JSONObject();
		collateralObj.put(DeviceConstants.DESCRIPTION, DeviceConstants.COLLATERAL);
		collateralObj.put(DeviceConstants.BALANCE,PriceFormat.formatFloat(fCollateral, 2));
		
		return collateralObj;
	}
	
	public static JSONObject getProfitLoss(float fProfitLoss)
	{
		JSONObject profitLossObj = new JSONObject();
		profitLossObj.put(DeviceConstants.DESCRIPTION, FTConstants.BOOKED_PROFIT_LOSS);
		profitLossObj.put(DeviceConstants.BALANCE,PriceFormat.formatFloat(fProfitLoss, 2));
		
		return profitLossObj;
	}
	
	public static JSONObject getFundUtilization(float fTotalUtilization)
	{
		JSONObject fundUtilizedObj = new JSONObject();
		fundUtilizedObj.put(DeviceConstants.DESCRIPTION, DeviceConstants.FUNDS_UTILIZED);
		fundUtilizedObj.put(DeviceConstants.BALANCE,PriceFormat.formatFloat(fTotalUtilization, 2));
		
		return fundUtilizedObj;
	}
	
	
	
	public static JSONObject getFundsAvailable(float fFundsAvailable)
	{
		JSONObject fundsAvailableObj = new JSONObject();
		fundsAvailableObj.put(DeviceConstants.DESCRIPTION, DeviceConstants.FUNDS_AVAILABLE_FOR_TRADING);
		fundsAvailableObj.put(DeviceConstants.BALANCE,PriceFormat.formatFloat(fFundsAvailable, 2));
		
		return fundsAvailableObj;
	}
	
	public static String getAmountAvailable(List<GetAdvanceFundsViewRows> fundsObj) {
		
		float fFundsAvailable = 0;
		
		for(GetAdvanceFundsViewRows fund : fundsObj)
		{
			
			if(fund.getDescription().equals(FTConstants.NET_AVAILABLE_FUNDS))
				fFundsAvailable = fund.getnTrading();
				
		}
		
		return Float.toString(fFundsAvailable);
	}

	
}
