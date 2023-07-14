package com.globecapital.business.order;

import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.order.FetchMarginPlusParametersAPI;
import com.globecapital.api.ft.order.FetchMarginPlusParamsRequest;
import com.globecapital.api.ft.order.FetchMarginPlusParamsResponse;
import com.globecapital.constants.order.InstrumentType;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.ProductType;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.symbology.SymbolRow;


public class GetMarginPlusParameter {
	//Moneyness Option
	public static final int atTheMoney = 1;
	public static final int atTheMoneyPlusOne = 2;
	public static final int atTheMoneyMinusOne = 3;
	public static final int inTheMoney = 4;
	public static final int outOfTheMoney = 5;
	
	public static FetchMarginPlusParamsResponse getMarginPlusParameter(SymbolRow symRow,Session session,int moneyMode) throws GCException {
		FetchMarginPlusParamsRequest marginPlusRequest= new FetchMarginPlusParamsRequest();
		String sUserID = session.getUserID();
		String sMarketSegID = symRow.getMktSegId();
		String sStrikePrice = symRow.getStrikePrice();
		marginPlusRequest.setJKey(session.getjKey());
		marginPlusRequest.setJSession(session.getjSessionID());
		marginPlusRequest.setUserID(sUserID);
		marginPlusRequest.setGroupId(session.getGroupId());
		marginPlusRequest.setDecimalLocator(Integer.toString(symRow.getMultiplier()));
		marginPlusRequest.setMktSegId(Integer.parseInt(sMarketSegID));
		marginPlusRequest.setInstName(symRow.getInstrument());
		marginPlusRequest.setSymbol(symRow.getSymbol());
		marginPlusRequest.setSeries(symRow.getSeries());
		marginPlusRequest.setExpiry(symRow.getExpiry());
		if(InstrumentType.isOptions(symRow.getInstrument())) {
			marginPlusRequest.setStrkPrc(sStrikePrice);
			marginPlusRequest.setOptType(symRow.getOptionType());
			marginPlusRequest.setStrikePrcMoneyMode(String.valueOf(moneyMode));
		}
		else {
			marginPlusRequest.setStrkPrc("");
			marginPlusRequest.setStrikePrcMoneyMode("");
			marginPlusRequest.setOptType("");
		}
		marginPlusRequest.setToken(Integer.parseInt(symRow.gettokenId()));
		marginPlusRequest.setProductType(ProductType.FT_MARGIN_PLUS);
		FetchMarginPlusParametersAPI marginPlusAPI = new FetchMarginPlusParametersAPI();
		return marginPlusAPI.post(marginPlusRequest, FetchMarginPlusParamsResponse.class, session.getAppID(), "FetchMarginPlusParams");
	}
	
	public static int getStrPrcMoneyMode(String buyOrSell, String sStrikePrice, String ordPrc) {
		Double orderPrc=Double.parseDouble(ordPrc),stkPrc=Double.parseDouble(sStrikePrice);
		if((orderPrc-stkPrc)==0)
			return atTheMoney;
		if(buyOrSell.equals(OrderAction.BUY)) {
			if((orderPrc-stkPrc)>0) {
				if((orderPrc-stkPrc)<=1 && (orderPrc-stkPrc)>0)
					return atTheMoneyPlusOne;
				else
					return inTheMoney;
			}else {
				if((orderPrc-stkPrc)>=-1 || (orderPrc-stkPrc)<0)
					return atTheMoneyMinusOne;
				else
					return outOfTheMoney;	
			}	
		}else {
			if((stkPrc-orderPrc)>0) {
				if((stkPrc-orderPrc)<=1 && (stkPrc-orderPrc)>0)
					return atTheMoneyPlusOne;
				else
					return inTheMoney;
			}else {
				if((stkPrc-orderPrc)>=-1 && (stkPrc-orderPrc)<0)
					return atTheMoneyMinusOne;
				else
					return outOfTheMoney;	
			}
		}
	}

}
