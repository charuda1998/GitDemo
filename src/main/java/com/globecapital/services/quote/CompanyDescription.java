package com.globecapital.services.quote;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.globecapital.business.quote.AdvanceQuote;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.RedisConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.db.RedisPool;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.RequestFailedException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionService;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.google.gson.Gson;
import com.msf.cmots.api.corporateInfo_v1.GetCompanyDescription;
import com.msf.cmots.api.data_v1.CompanyDesc;
import com.msf.cmots.api.data_v1.CompanyDescriptionList;

import redis.clients.jedis.exceptions.JedisConnectionException;

public class CompanyDescription  extends SessionService{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doPostProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		Session session = gcRequest.getSession();
		
		String sSymbolToken = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ).getString(SymbolConstants.SYMBOL_TOKEN);
		SymbolRow symRow = SymbolMap.getSymbolRow(sSymbolToken);
		
		String sCoCode = symRow.getCMCoCode();
		int index = sCoCode.indexOf(".");
		if (index != -1)
			sCoCode = sCoCode.substring(0, index);
		
		GetCompanyDescription companyDescObj = 
				new GetCompanyDescription(AdvanceQuote.getAppIDForLogging(session.getAppID()));
		RedisPool redisPool=new RedisPool();
		companyDescObj.setCoCode(sCoCode);
		try {
			CompanyDescriptionList companyDescList =  new CompanyDescriptionList();
			try {
			if(redisPool.isExists(RedisConstants.COMPANY_DESCRIPTION_LIST+"_"+sCoCode)) {
				companyDescList=new Gson().fromJson(redisPool.getValue(RedisConstants.COMPANY_DESCRIPTION_LIST+"_"+sCoCode), CompanyDescriptionList.class);
			}
			else {
				companyDescList=companyDescObj.invoke();
				redisPool.setValues(RedisConstants.COMPANY_DESCRIPTION_LIST+"_"+sCoCode, new Gson().toJson(companyDescObj.invoke()));		
			}
			}catch (Exception e) {
				log.error(e);
				companyDescList=companyDescObj.invoke();	
			}
			
			JSONArray finalCompanyDesc = new JSONArray();
		
			for( CompanyDesc companyDesc : companyDescList) {
				JSONObject obj = new JSONObject();
				obj.put(DeviceConstants.COMPANY_NAME, companyDesc.getLName());
				obj.put(DeviceConstants.COMPANY_DESC, StringUtils.chomp(companyDesc.getMemo()));
				finalCompanyDesc.put(obj);
			}
			gcResponse.addToData(DeviceConstants.ABOUT_COMPANY, finalCompanyDesc);
		}catch(IllegalStateException e) {
			log.debug(e);
			gcResponse.setNoDataAvailable();
		}
		catch(Exception e) {
			log.debug(e);
			throw new RequestFailedException();
		}
	}
}
