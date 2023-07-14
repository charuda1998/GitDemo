package com.globecapital.services.watchlist;

import com.globecapital.business.watchlist.Watchlist;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.AppConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.SessionService;

public class UpdateWatchlist_101 extends SessionService {

	private static final long serialVersionUID = 1L;

	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		boolean reloadOnUpdate = false;
		String echoForAPI = gcRequest.getFromData(DeviceConstants.ECHO_FOR_API);
		String watchListName = gcRequest.getFromData(DeviceConstants.WATCHLIST_NAME);
		if (Watchlist.updateWatchList_101(gcRequest.getSession(), gcRequest.getSession().getUserID(),
				gcRequest.getFromData(DeviceConstants.WATCHLIST_ID), watchListName, echoForAPI,
				gcRequest.getArrayFromData(DeviceConstants.SYMBOL_LIST), reloadOnUpdate,getServletContext(), gcRequest,  gcResponse)) {

			if (echoForAPI.equalsIgnoreCase(DeviceConstants.CREATE_WATCH_ON_UPDATE)
					&& watchListName.equalsIgnoreCase(DeviceConstants.DEFAULT_WATCHLIST1)) {
				reloadOnUpdate = true;
			}

			gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.watchlist.WATCHLIST_UPDATED"));
			gcResponse.addToData(DeviceConstants.RELOAD_ON_UPDATE_WATCHLIST,
					(reloadOnUpdate) ? AppConstants.STR_TRUE : AppConstants.STR_FALSE);
		} else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG,
					InfoMessage.getInfoMSG("info_msg.watchlist.WATCHLIST_EXIST"));
	}
}
