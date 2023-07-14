package com.globecapital.services.watchlist;

import com.globecapital.business.watchlist.Watchlist;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class RenameWatchlist extends SessionService {

	private static final long serialVersionUID = 1L;

	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		String watchListName=gcRequest.getFromData(DeviceConstants.NEW_WATCHLIST_NAME);
		
		if(!( watchListName.equalsIgnoreCase(DeviceConstants.MYHOLD))){
				
		if (Watchlist.renameWatchList(gcRequest.getSession(), gcRequest.getSession().getUserID(),
				gcRequest.getFromData(DeviceConstants.WATCHLIST_ID),
				gcRequest.getFromData(DeviceConstants.WATCHLIST_NAME),
				gcRequest.getFromData(DeviceConstants.NEW_WATCHLIST_NAME),getServletContext(), gcRequest,  gcResponse)) {
			    gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.watchlist.WATCHLIST_RENAMED"));
			    gcResponse.addToData(DeviceConstants.IS_VALID_WATCHLIST_NAME ,"true");
				}}
	else {
		gcResponse.addToData(DeviceConstants.MSG, String.format(InfoMessage.getInfoMSG("info_msg.watchlist.WATCHLIST_RENAME"), watchListName));
		gcResponse.addToData(DeviceConstants.IS_VALID_WATCHLIST_NAME ,"false");
		}

		
		
	}
}