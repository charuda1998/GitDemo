package com.globecapital.services.watchlist;

import org.json.JSONObject;

import com.globecapital.business.watchlist.Watchlist;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.SessionService;

public class GetWatchlist extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		JSONObject watchlist = Watchlist.getWatchlist(gcRequest.getSession(), gcRequest.getSession().getUserID(),getServletContext(), gcRequest,  gcResponse);
		if (watchlist.length() > 0) {

			gcResponse.addToData(DeviceConstants.WATCHLIST, watchlist);
		} else
			throw new GCException(InfoIDConstants.DYNAMIC_MSG,
					InfoMessage.getInfoMSG("info_msg.watchlist.GROUP_NOT_EXIST"));
	}

}
