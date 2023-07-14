package com.globecapital.services.edis;

import com.globecapital.business.edis.GenerateCDSLTPIN;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.constants.UserInfoConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;

public class GenerateTPIN extends SessionService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		boolean isPOAUser = Boolean.parseBoolean(session.getUserInfo().getString(UserInfoConstants.POA_STATUS));
		try
		{
			if(!isPOAUser) {
				boolean status = GenerateCDSLTPIN.generateTPIN(session,getServletContext(),gcRequest,gcResponse);
				if(status) {
					gcResponse.setInfoID(InfoIDConstants.SUCCESS);
					gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.tpin_generated_successfully"));
				}else {
					gcResponse.setInfoID(InfoIDConstants.DYNAMIC_MSG);
					gcResponse.setInfoMsg(InfoMessage.getInfoMSG("info_msg.tpin_generation_failed"));
				}
			}
			else
				gcResponse.setNoDataAvailable();
		}
		catch(Exception e)
		{
			log.info(e);
			gcResponse.setNoDataAvailable();
		}
		
	}

}
