package com.globecapital.constants.order;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.exception.InvalidRequestKeyException;


public class ProductType {

	/****************** Display Product Types ****************** */
	// Equity Segments
	public static final String INTRADAY = "INTRADAY";
	public static final String DELIVERY = "DELIVERY";
	public static final String MTF = "MTF";
	public static final String MARGIN = "MARGIN";
	public static final String BRACKET_ORDER = "BRACKET ORDER";
	public static final String COVER_ORDER = "COVER ORDER";
	
	public static final String DISCREPANCY = "DISCREPANCY";

	// Derivative Segments
	// <INTRADAY> Declared above
	public static final String CARRYFORWARD = "CARRYFORWARD";
	/****************** Display Product Types ****************** */

	/****************** API Product Types ****************** */
	// Equity Segments
	public static final String FT_INTRADAY_OR_MARGIN = "M";
	public static final String FT_DELIVERY_OR_CARRYFORWARD = "D";
	public static final String FT_BRACKET_ORDER = "B";
	public static final String FT_MARGIN_PLUS = "MP";
	public static final String FT_MARGIN_TRADE_FUNDING = "MF";
	public static final String FT_LOGIN_MARGIN_TRADE_FUNDING = "MTF";
	public static final String FT_MARGIN_FULL_TEXT = "MARGIN";
	public static final String FT_DELIVERY_FULL_TEXT = "DELIVERY";
	public static final String FT_CARRYFORWARD_FULL_TEXT = "CARRYFORWARD";
	public static final String FT_INTRADAY_FULL_TEXT = "INTRADAY";
	public static final String FT_MARGIN_TRADE_FUNDING_FULL_TEXT = "MARGIN TRADE FUNDING";
	public static final String FT_BRACKET_ORDER_FULL_TEXT = "BRACKET ORDER";
	public static final String FT_MARGIN_PLUS_FULL_TEXT = "MARGINPLUS";
	public static final String FT_AMO_DELIVERY_OR_CARRYFORWARD = "AD";
	public static final String FT_AMO_MARGIN_OR_INTRADAY = "AM";
	public static final String FT_AMO_DELIVERY_FULL_TEXT = "AMO DELIVERY";
	public static final String FT_AMO_MARGIN_FULL_TEXT = "AMO MARGIN";
	public static final String FT_AMO_CARRYFORWARD_FULL_TEXT = "AMO CARRYFORWARD";
	public static final String FT_AMO_INTRADAY_FULL_TEXT = "AMO INTRADAY";
	public static final String FT_AMO_MARGIN_TRADE_FUNDING = "AMF";
	public static final String FT_BO_AND_MP_MARGIN_INFO = "M";

	// Derivative Segments
	// <FT_INTRADAY> & <FT_CARRYFORWARD> Declared above
	/******************* API Product Types *********************/
	// TODO: AMO product type yet to handle

	private static List<String> allowedProductTypeList;
	private static JSONArray defaultProductTypeEquity;
	private static JSONArray defaultProductTypeDerivative;
	private static JSONArray normalOrderProductTypes;
	static {
		allowedProductTypeList = new ArrayList<String>();
		allowedProductTypeList.add(FT_INTRADAY_OR_MARGIN);
		allowedProductTypeList.add(FT_DELIVERY_OR_CARRYFORWARD);
		allowedProductTypeList.add(FT_MARGIN_FULL_TEXT);
		allowedProductTypeList.add(FT_DELIVERY_FULL_TEXT);
		allowedProductTypeList.add(FT_CARRYFORWARD_FULL_TEXT);
		allowedProductTypeList.add(FT_INTRADAY_FULL_TEXT);
		allowedProductTypeList.add(FT_MARGIN_TRADE_FUNDING_FULL_TEXT);
		allowedProductTypeList.add(FT_BRACKET_ORDER_FULL_TEXT);
		allowedProductTypeList.add(FT_MARGIN_PLUS_FULL_TEXT);
		allowedProductTypeList.add(FT_LOGIN_MARGIN_TRADE_FUNDING);

		defaultProductTypeEquity = new JSONArray();
		defaultProductTypeEquity.put(DELIVERY);
		defaultProductTypeEquity.put(INTRADAY);

		defaultProductTypeDerivative = new JSONArray();
		defaultProductTypeDerivative.put(CARRYFORWARD);
		defaultProductTypeDerivative.put(INTRADAY);

		normalOrderProductTypes = new JSONArray();
		normalOrderProductTypes.put(DELIVERY);
		normalOrderProductTypes.put(INTRADAY);
		normalOrderProductTypes.put(CARRYFORWARD);
		normalOrderProductTypes.put(MTF);
	}

	public static final String formatToAPI(final String displayProductType, boolean isAMO) throws Exception {

		if (isAMO) {
			if (displayProductType.equalsIgnoreCase(INTRADAY) || displayProductType.equalsIgnoreCase(MARGIN))
				return FT_AMO_MARGIN_OR_INTRADAY;
			else if (displayProductType.equalsIgnoreCase(DELIVERY) || displayProductType.equalsIgnoreCase(CARRYFORWARD)
					|| displayProductType.equalsIgnoreCase(DISCREPANCY))
				return FT_AMO_DELIVERY_OR_CARRYFORWARD;
			else if (displayProductType.equalsIgnoreCase(MTF))
				return FT_AMO_MARGIN_TRADE_FUNDING;
		} else {
			if (displayProductType.equalsIgnoreCase(INTRADAY) || displayProductType.equalsIgnoreCase(MARGIN))
				return FT_INTRADAY_OR_MARGIN;
			else if (displayProductType.equalsIgnoreCase(DELIVERY) || displayProductType.equalsIgnoreCase(DISCREPANCY))
				return FT_DELIVERY_OR_CARRYFORWARD;
			else if (displayProductType.equalsIgnoreCase(MTF))
				return FT_MARGIN_TRADE_FUNDING;
			else if (displayProductType.equalsIgnoreCase(CARRYFORWARD))
				return FT_DELIVERY_OR_CARRYFORWARD;
			else if (displayProductType.equalsIgnoreCase(BRACKET_ORDER))
				return FT_BRACKET_ORDER;
			else if (displayProductType.equalsIgnoreCase(COVER_ORDER))
				return FT_MARGIN_PLUS;
		}
		throw new InvalidRequestKeyException(MessageConstants.DEVICE_INVALID_PRODUCT_TYPE_RECEIVED);
	}

	public static final String formatToDisplay(final String apiProductType, String marketSegmentID) throws Exception {
		if (apiProductType.equalsIgnoreCase(FT_DELIVERY_OR_CARRYFORWARD)
				|| apiProductType.equalsIgnoreCase(FT_DELIVERY_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_CARRYFORWARD_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_DELIVERY_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_CARRYFORWARD_FULL_TEXT)) {
			if (ExchangeSegment.isEquitySegment(marketSegmentID))
				return DELIVERY;
			else
				return CARRYFORWARD;
		} else if (apiProductType.equalsIgnoreCase(FT_INTRADAY_OR_MARGIN)
				|| apiProductType.equalsIgnoreCase(FT_INTRADAY_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_MARGIN_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_MARGIN_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_INTRADAY_FULL_TEXT)) {
			return INTRADAY;

		} else if (apiProductType.equalsIgnoreCase(FT_MARGIN_TRADE_FUNDING)
				|| apiProductType.equalsIgnoreCase(FT_LOGIN_MARGIN_TRADE_FUNDING)) {
			return MTF;
		} else if (apiProductType.equalsIgnoreCase(FT_BRACKET_ORDER_FULL_TEXT) 
				|| apiProductType.equalsIgnoreCase(FT_BRACKET_ORDER)) {
			return BRACKET_ORDER;
		}
		else if (apiProductType.equalsIgnoreCase(FT_MARGIN_PLUS_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_MARGIN_PLUS)) {
			return COVER_ORDER;
		}else {
			return apiProductType;
			// throw new
			// InvalidAPIResponseException(MessageConstants.API_INVALID_PRODUCT_TYPE_RECEIVED);
		}
	}
	
	public static final String formatToDisplayForBrokerage(final String apiProductType, String marketSegmentID) throws Exception {
		if (apiProductType.equalsIgnoreCase(FT_DELIVERY_OR_CARRYFORWARD)
				|| apiProductType.equalsIgnoreCase(FT_DELIVERY_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_CARRYFORWARD_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_DELIVERY_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_CARRYFORWARD_FULL_TEXT)) {
			if (ExchangeSegment.isEquitySegment(marketSegmentID))
				return DELIVERY;
			else
				return CARRYFORWARD;
		} else if (apiProductType.equalsIgnoreCase(FT_INTRADAY_OR_MARGIN)
				|| apiProductType.equalsIgnoreCase(FT_INTRADAY_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_MARGIN_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_MARGIN_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_INTRADAY_FULL_TEXT)) {
			return MARGIN;

		} else if (apiProductType.equalsIgnoreCase(FT_MARGIN_TRADE_FUNDING)
				|| apiProductType.equalsIgnoreCase(FT_LOGIN_MARGIN_TRADE_FUNDING)) {
			return MTF;
		} else if (apiProductType.equalsIgnoreCase(FT_BRACKET_ORDER_FULL_TEXT) 
				|| apiProductType.equalsIgnoreCase(FT_BRACKET_ORDER)) {
			return BRACKET_ORDER;
		}
		else if (apiProductType.equalsIgnoreCase(FT_MARGIN_PLUS_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_MARGIN_PLUS)) {
			return FT_MARGIN_PLUS_FULL_TEXT;
		}else {
			return apiProductType;
			// throw new
			// InvalidAPIResponseException(MessageConstants.API_INVALID_PRODUCT_TYPE_RECEIVED);
		}
	}

	/***
	 * MTF product type displayed as MTF also as Margin Trade Funding. This function
	 * returns 'MTF'
	 */
	public static final String formatToDisplay2(final String apiProductType, String marketSegmentID) throws Exception {
		if (apiProductType.equalsIgnoreCase(FT_DELIVERY_OR_CARRYFORWARD)
				|| apiProductType.equalsIgnoreCase(FT_DELIVERY_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_CARRYFORWARD_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_DELIVERY_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_CARRYFORWARD_FULL_TEXT)) {
			if (ExchangeSegment.isEquitySegment(marketSegmentID))
				return DELIVERY;
			else
				return CARRYFORWARD;
		} else if (apiProductType.equalsIgnoreCase(FT_INTRADAY_OR_MARGIN)
				|| apiProductType.equalsIgnoreCase(FT_INTRADAY_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_MARGIN_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_MARGIN_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_AMO_INTRADAY_FULL_TEXT)) {
			return INTRADAY;

		} else if (apiProductType.equalsIgnoreCase(FT_MARGIN_TRADE_FUNDING)) {
			return MTF;
		} else if (apiProductType.equalsIgnoreCase(FT_BRACKET_ORDER_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_BRACKET_ORDER)) { // TODO: temporary fix for Bracket
																					// order, designs yet to confirm
			return BRACKET_ORDER;
		}
		else if (apiProductType.equalsIgnoreCase(FT_MARGIN_PLUS_FULL_TEXT)
				|| apiProductType.equalsIgnoreCase(FT_MARGIN_PLUS)) {
			return COVER_ORDER;
		}
		else {
			return apiProductType;
			// throw new
			// InvalidAPIResponseException(MessageConstants.API_INVALID_PRODUCT_TYPE_RECEIVED);
		}
	}

	public static final boolean isValidProduct(String productType) {
		return allowedProductTypeList.contains(productType);
	}

	public static final JSONArray getDefaultProductTypes(String marketSegmentID) {
		if (ExchangeSegment.isEquitySegment(marketSegmentID))
			return defaultProductTypeEquity;
		else
			return defaultProductTypeDerivative;
	}

	/**********
	 * Applicable product type list: For last used product type Implementation at
	 * device level
	 */
	public static final JSONArray getApplicableProductTypesForFEPrefernces() {
		return normalOrderProductTypes;
	}

	/***********************
	 * Net Positions Convertable Product Types
	 ************************/

	public static JSONArray getConvertableProductTypes(String apiProductType, String marketSegmentID) {
		JSONArray convertableProdTypes = new JSONArray();

		switch (apiProductType) {

			case FT_DELIVERY_OR_CARRYFORWARD:
			case FT_DELIVERY_FULL_TEXT:
			case FT_CARRYFORWARD_FULL_TEXT: {
				if (ExchangeSegment.isEquitySegment(marketSegmentID)) {
					convertableProdTypes.put(ProductType.INTRADAY);
					convertableProdTypes.put(ProductType.MTF);
				} else {
					convertableProdTypes.put(ProductType.INTRADAY);
				}
				return convertableProdTypes;
			}

			case FT_INTRADAY_OR_MARGIN:
			case FT_INTRADAY_FULL_TEXT:
			case FT_MARGIN_FULL_TEXT: {
				if (ExchangeSegment.isEquitySegment(marketSegmentID)) {
					convertableProdTypes.put(ProductType.DELIVERY);
					convertableProdTypes.put(ProductType.MTF);
				} else {
					convertableProdTypes.put(ProductType.CARRYFORWARD);
				}
				return convertableProdTypes;
			}

			case FT_MARGIN_TRADE_FUNDING:
			case FT_LOGIN_MARGIN_TRADE_FUNDING: {
				if (ExchangeSegment.isEquitySegment(marketSegmentID)) {
					convertableProdTypes.put(ProductType.DELIVERY);
					convertableProdTypes.put(ProductType.INTRADAY);
				} else {
					convertableProdTypes.put(ProductType.CARRYFORWARD);
				}

				return convertableProdTypes;
			}

			case FT_BRACKET_ORDER_FULL_TEXT:
			case COVER_ORDER: {
				if (ExchangeSegment.isEquitySegment(marketSegmentID)) {
					convertableProdTypes.put(ProductType.DELIVERY);
				} else {
					convertableProdTypes.put(ProductType.CARRYFORWARD);
				}
				return convertableProdTypes;
			}

			default:
				return convertableProdTypes;
		}

	}
	
	public static final String formatToMarginInfoAPI(final String displayProductType, boolean isAMO) throws Exception {

		if (isAMO) {
			if (displayProductType.equalsIgnoreCase(INTRADAY) || displayProductType.equalsIgnoreCase(MARGIN))
				return FT_AMO_MARGIN_OR_INTRADAY;
			else if (displayProductType.equalsIgnoreCase(DELIVERY) || displayProductType.equalsIgnoreCase(CARRYFORWARD)
					|| displayProductType.equalsIgnoreCase(DISCREPANCY))
				return FT_AMO_DELIVERY_OR_CARRYFORWARD;
			else if (displayProductType.equalsIgnoreCase(MTF))
				return FT_AMO_MARGIN_TRADE_FUNDING;
		} else {
			if (displayProductType.equalsIgnoreCase(INTRADAY) || displayProductType.equalsIgnoreCase(MARGIN))
				return FT_INTRADAY_OR_MARGIN;
			else if (displayProductType.equalsIgnoreCase(DELIVERY) || displayProductType.equalsIgnoreCase(DISCREPANCY))
				return FT_DELIVERY_OR_CARRYFORWARD;
			else if (displayProductType.equalsIgnoreCase(MTF))
				return FT_MARGIN_TRADE_FUNDING;
			else if (displayProductType.equalsIgnoreCase(CARRYFORWARD))
				return FT_DELIVERY_OR_CARRYFORWARD;
			else if (displayProductType.equalsIgnoreCase(BRACKET_ORDER) || displayProductType.equalsIgnoreCase(COVER_ORDER))
				return FT_BO_AND_MP_MARGIN_INFO;
		}
		throw new InvalidRequestKeyException(MessageConstants.DEVICE_INVALID_PRODUCT_TYPE_RECEIVED);
	}
	
	public static JSONArray getChartProductType(JSONArray prodList, String exch) {
        
        JSONArray allowedProdList = new JSONArray();
        
        for (int i = 0; i < prodList.length(); i++) {  
            JSONObject productList = prodList.getJSONObject(i);
            if(productList.getString(DeviceConstants.EXCH).equals(exch)) {
                JSONArray listProd = productList.getJSONArray(UserInfoConstants.PRODUCT_LIST);
                for (int j = 0; j < listProd.length(); j++) { 
                    switch(listProd.getString(j)){    
                        case ProductType.INTRADAY : 
                            allowedProdList.put(ProductType.INTRADAY);    
                            break;  
                        case ProductType.CARRYFORWARD : 
                            allowedProdList.put(ProductType.CARRYFORWARD);   
                            break; 
                        case ProductType.DELIVERY: 
                            allowedProdList.put(ProductType.DELIVERY);
                            break;
                    }    
                }
            }
        }
        return allowedProdList;
    }

}