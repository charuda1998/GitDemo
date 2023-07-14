package com.globecapital.services.chart;

import org.json.JSONObject;

import com.globecapital.business.chart.InlineChart;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.services.common.BaseService;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;

public class GetInlineChart extends BaseService{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(GCRequest gcRequest, GCResponse gcResponse) throws Exception {
		JSONObject symObj = gcRequest.getObjectFromData(SymbolConstants.SYMBOL_OBJ);
		try
		{
			gcResponse.setData(InlineChart.getInlineChart(symObj.getString(SymbolConstants.SYMBOL_TOKEN)));
		}
		catch(Exception e)
		{
			gcResponse.setNoDataAvailable();
		}
		
	}

}
