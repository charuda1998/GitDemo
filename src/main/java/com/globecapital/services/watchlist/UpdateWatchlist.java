package com.globecapital.services.watchlist;

import com.globecapital.business.watchlist.Watchlist;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.SessionService;

public class UpdateWatchlist extends SessionService {

	private static final long serialVersionUID = 1L;

	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		
	String  watchListName=gcRequest.getFromData(DeviceConstants.WATCHLIST_NAME);
		
		if(!( watchListName.equalsIgnoreCase(DeviceConstants.MYHOLD))){
				
		if (Watchlist.updateWatchList(gcRequest.getSession(), gcRequest.getSession().getUserID(),
				gcRequest.getFromData(DeviceConstants.WATCHLIST_ID),
				gcRequest.getFromData(DeviceConstants.WATCHLIST_NAME),
				gcRequest.getArrayFromData(DeviceConstants.SYMBOL_LIST),getServletContext(), gcRequest,  gcResponse)) {
				gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.watchlist.WATCHLIST_UPDATED"));
				gcResponse.addToData(DeviceConstants.IS_VALID_WATCHLIST_NAME ,"true");
		
		} else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG, InfoMessage.getInfoMSG("info_msg.watchlist.WATCHLIST_EXIST"));
		}
		else {
			gcResponse.addToData(DeviceConstants.MSG, String.format(InfoMessage.getInfoMSG("info_msg.watchlist.WATCHLIST_UPDATE"), watchListName));
			gcResponse.addToData(DeviceConstants.IS_VALID_WATCHLIST_NAME ,"false");
			}
	}
}
