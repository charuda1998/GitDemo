package com.globecapital.services.watchlist;

import com.globecapital.business.watchlist.Watchlist;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.SessionService;

public class DeleteWatchlist extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		if (Watchlist.deleteWatchList(gcRequest.getSession(), gcRequest.getSession().getUserID(),
				gcRequest.getFromData(DeviceConstants.WATCHLIST_ID),
				gcRequest.getFromData(DeviceConstants.WATCHLIST_NAME),getServletContext(), gcRequest,  gcResponse)) {
			gcResponse.addToData(DeviceConstants.MSG, InfoMessage.getInfoMSG("info_msg.watchlist.WATCHLIST_DELETED"));
		}
	}

}
