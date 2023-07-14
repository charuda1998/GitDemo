package com.globecapital.api.ft.esip;

import java.util.HashMap;

import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.api.ft.generics.FTResponse;
import com.globecapital.api.generics.Api;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.InfoIDConstants;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.exception.InvalidSession;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class FtEQsipApi <Req extends FTRequest, Resp extends FtEQsipResponse> extends Api<Req, Resp> {

	//Below bean initialized with value in ApplicationContextListener.
	public static String API_BEAN = "";
	public FtEQsipApi(String serviceUrl) throws GCException {
		super(serviceUrl, API_BEAN);
		setConnectionHeaders();
	}

	protected void setConnectionHeaders() {
		HashMap<String, String> map = new HashMap<>();
		map.put("Content-Type", "application/json");
		map.put("Access-Control-Expose-Headers", "Set-Cookie, Cookie, Content-Type");
		map.put("Access-Control-Allow-Methods", "POST");
		map.put("Access-Control-Allow-Credentials", "true");
		setHeaders(map);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Resp createJSONRespObj(Class<?> reponseClass, String rawResponse) throws GCException {
		Gson gson = new Gson();
		JsonObject body = gson.fromJson(rawResponse, JsonObject.class);

		Boolean el = body.get("ResponseStatus").getAsBoolean();
		if (el)
			return (Resp) gson.fromJson(body, reponseClass);
		else
		{
			int code = body.get("ResponseCode").getAsInt();
			JsonArray errorArr = body.get("ErrorMessages").getAsJsonArray();
			log.info("array msg "+errorArr.getAsString());
			
			if(code == 112)
				throw new InvalidSession (errorArr.getAsString());
			else if(errorArr.size() > 0) {
				return (Resp) gson.fromJson(body, reponseClass);
				//throw new GCException (InfoIDConstants.DYNAMIC_MSG, errorArr.getAsString());
			}
			else if(body.get("ResponseObject").getAsJsonObject().get("Status").getAsString().equalsIgnoreCase(InfoMessage.getInfoMSG("info_msg.order_error")))
				throw new InvalidSession ();
			else
				throw new GCException (InfoIDConstants.DYNAMIC_MSG, 
						body.get("ResponseObject").getAsJsonObject().get("Status").getAsString());
			
		}
	}

}

