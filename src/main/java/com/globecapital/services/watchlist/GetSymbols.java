package com.globecapital.services.watchlist;

import org.apache.commons.lang3.ArrayUtils;
import org.json.JSONArray;

import com.globecapital.business.watchlist.Watchlist;
import com.globecapital.config.AppConfig;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.SessionService;

public class GetSymbols extends SessionService {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		JSONArray symbolArray = new JSONArray();
		String watchlist = gcRequest.getFromData(DeviceConstants.WATCHLIST_NAME);
		String[] predefinedWatchlist = AppConfig.getArray("indexNames");
		if( ArrayUtils.contains(predefinedWatchlist, watchlist)) {
			symbolArray = Watchlist.getPredefinedWatchlistSymbols(gcRequest.getFromData(DeviceConstants.WATCHLIST_ID),watchlist);
		} else {
			symbolArray = Watchlist.getSymbols(gcRequest.getSession(), gcRequest.getSession().getUserID(),
				gcRequest.getFromData(DeviceConstants.WATCHLIST_ID),
				watchlist,getServletContext(), gcRequest,  gcResponse);
		}
		if (symbolArray.length() > 0) {
			gcResponse.addToData(DeviceConstants.SYMBOL_LIST, symbolArray);
		} else
			throw new GCException(InfoIDConstants.NO_DATA,
					InfoMessage.getInfoMSG("info_msg.watchlist.SYMBOL_NOT_EXIST"));

	}

}
