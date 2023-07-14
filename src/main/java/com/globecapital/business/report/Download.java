package com.globecapital.business.report;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import com.globecapital.api.gc.backoffice.CommodityPLDownloadAPI;
import com.globecapital.api.gc.backoffice.CommoditySaudaDownloadAPI;
import com.globecapital.api.gc.backoffice.CommodityTaxDownloadAPI;
import com.globecapital.api.gc.backoffice.CommodityTrxnDownloadAPI;
import com.globecapital.api.gc.backoffice.ContractNotesDownloadAPI;
import com.globecapital.api.gc.backoffice.CurrencyPLDownloadAPI;
import com.globecapital.api.gc.backoffice.CurrencySaudaDownloadAPI;
import com.globecapital.api.gc.backoffice.CurrencyTaxDownloadAPI;
import com.globecapital.api.gc.backoffice.CurrencyTrxnDownloadAPI;
import com.globecapital.api.gc.backoffice.DerivativePLDownloadAPI;
import com.globecapital.api.gc.backoffice.DerivativeSaudaDownloadAPI;
import com.globecapital.api.gc.backoffice.DerivativeTaxDownloadAPI;
import com.globecapital.api.gc.backoffice.DerivativeTrxnDownloadAPI;
import com.globecapital.api.gc.backoffice.DownloadResponse;
import com.globecapital.api.gc.backoffice.EquityPLDownloadAPI;
import com.globecapital.api.gc.backoffice.EquitySaudaDownloadAPI;
import com.globecapital.api.gc.backoffice.EquityTaxDownloadAPI;
import com.globecapital.api.gc.backoffice.EquityTrxnDownloadAPI;
import com.globecapital.api.gc.backoffice.GetCNEmailRequest;
import com.globecapital.api.gc.backoffice.GetHoldingPortfolioEmailRequest;
import com.globecapital.api.gc.backoffice.GetLedgerEmailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetRLEmailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetSaudaEmailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetTaxEmailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetTrxnMailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetUnPLEmailRequest;
import com.globecapital.api.gc.backoffice.GetUnRelCommodityDownloadAPI;
import com.globecapital.api.gc.backoffice.GetUnRelCurrencyDownloadAPI;
import com.globecapital.api.gc.backoffice.GetUnRelDerivativeDownloadAPI;
import com.globecapital.api.gc.backoffice.GetUnRelEquityDownloadAPI;
import com.globecapital.api.gc.backoffice.LedgerDownloadAPI;
import com.globecapital.api.gc.backoffice.PortfolioDownloadAPI;
import com.globecapital.api.gc.backoffice.StockDownloadAPI;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.api.spyder.generics.SpyderConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.GCUtils;
import com.msf.log.Logger;

public class Download {

	private static Logger log = Logger.getLogger(Ledger.class);

	public static void downloadProcess(GCRequest gcRequest, GCResponse gcResponse) throws Exception {

		Session session = gcRequest.getSession();
		String userId = session.getUserID();
		
		String segment = "";
		String reportType = gcRequest.getFromData(DeviceConstants.REPORT_TYPE);
		String segmentType = gcRequest.getFromData(DeviceConstants.SEGMENT_TYPE);
		JSONObject filterObj = gcRequest.getObjectFromData(DeviceConstants.FILTER_OBJ);

		JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));
		JSONObject reportDates = FilterType.getFilterDates(filterObj.getString(DeviceConstants.DATE_FILTER), filterObj);

		HttpServletResponse response = gcResponse.getHttpResponse();
		String currentFinancialYear = DateUtils.getFinancialYearByDate(reportDates);
		GCHttpConnection con = null;

		try {
			
			GetTrxnMailDownloadRequest trxnMailRequest = new GetTrxnMailDownloadRequest();
			GetHoldingPortfolioEmailRequest holdingEmailReq = new GetHoldingPortfolioEmailRequest();
			GetRLEmailDownloadRequest rlEmailReq = new GetRLEmailDownloadRequest();
			GetLedgerEmailDownloadRequest ledgerEmailRequest = new GetLedgerEmailDownloadRequest();
			GetHoldingPortfolioEmailRequest portfolioEmailReq = new GetHoldingPortfolioEmailRequest();
			GetCNEmailRequest cnEmailRequest = new GetCNEmailRequest();
			GetSaudaEmailDownloadRequest saudaEmailRequest = new GetSaudaEmailDownloadRequest();
			GetTaxEmailDownloadRequest taxEmailReq = new GetTaxEmailDownloadRequest();
			GetUnPLEmailRequest unRLEmailReq = new GetUnPLEmailRequest();

			if (reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION)) {

				trxnMailRequest.setClientCode(userId);
				trxnMailRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
				trxnMailRequest.setToDate(reportDates.getString(DeviceConstants.TO_DATE));
				
				con = performTrnsDownloadAPI(session, segmentType, currentFinancialYear, trxnMailRequest);

			} else if (reportType.equalsIgnoreCase(DeviceConstants.HOLDINGS)) {

				holdingEmailReq.setClientCode(userId);
				holdingEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
				StockDownloadAPI download = new StockDownloadAPI();
				con = download.getDownloadResp(holdingEmailReq, DownloadResponse.class, session.getAppID());
			} else if (reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS)) {

				rlEmailReq.setClientCode(userId);
				rlEmailReq.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
				rlEmailReq.setToDate(reportDates.getString(DeviceConstants.TO_DATE));

				con = performRLEmailDownloadAPICall(session, segmentType, currentFinancialYear, rlEmailReq);

			} else if (reportType.equalsIgnoreCase(DeviceConstants.LEDGER)) {

				ledgerEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				ledgerEmailRequest.setClientCode(userId);
				
				if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) 
					segment = GCConstants.EQUITY;
				else
					segment = GCConstants.COMMODITY;
				
				if (filterBy.toString().contains(DeviceConstants.WITH_MARGIN_FILTER)) {
					ledgerEmailRequest.setEntryType("WM");
				} else {
					ledgerEmailRequest.setEntryType("A");
				}
				ledgerEmailRequest.setSegment(segment);
				ledgerEmailRequest.setYear(currentFinancialYear);
				ledgerEmailRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
				ledgerEmailRequest.setToDate(reportDates.getString(DeviceConstants.TO_DATE));

				LedgerDownloadAPI download = new LedgerDownloadAPI();
				con = download.getDownloadResp(ledgerEmailRequest, DownloadResponse.class, session.getAppID());
			} else if (reportType.equalsIgnoreCase(DeviceConstants.PORTFOLIO)) {

				portfolioEmailReq.setClientCode(userId);
				portfolioEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
				PortfolioDownloadAPI download = new PortfolioDownloadAPI();
				con = download.getDownloadResp(portfolioEmailReq, DownloadResponse.class, session.getAppID());
			} else if (reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES)) {

				JSONObject reportContractDates = FilterType.getOtherReportFilterDates(filterObj);
				cnEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				cnEmailRequest.setDate(reportContractDates.getString(DeviceConstants.TO_DATE));
				
				cnEmailRequest.setClientCode(userId);
				
				if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) 
					cnEmailRequest.setSegment("EQ");
				else 
					cnEmailRequest.setSegment("CO");
				
				cnEmailRequest.setYear(currentFinancialYear);

				ContractNotesDownloadAPI download = new ContractNotesDownloadAPI();
				con = download.getDownloadResp(cnEmailRequest, DownloadResponse.class, session.getAppID());

			} else if (reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL)) {
				JSONObject reportContractDates = FilterType.getOtherReportFilterDates(filterObj);

				saudaEmailRequest.setClientCode(userId);
				saudaEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				saudaEmailRequest.setDate(reportContractDates.getString(DeviceConstants.TO_DATE));
				con = performSaudaEmailDownloadAPICall(session, segmentType, currentFinancialYear, saudaEmailRequest);
			} else if (reportType.equalsIgnoreCase(DeviceConstants.TAX)) {

				
				taxEmailReq.setClientCode(userId);
				
				taxEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
				taxEmailReq.setYear(GCUtils.getFinancialYear(filterObj.getString(DeviceConstants.DATE_FILTER)));
				con = performTaxEmailDownloadAPICall(session, segmentType, con, taxEmailReq);
			} else if (reportType.equalsIgnoreCase(DeviceConstants.UNREALISED_PROFIT_LOSS)) {


				unRLEmailReq.setClientCode(userId);
				
				unRLEmailReq.setToken(GCAPIAuthToken.getAuthToken());

				if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
					unRLEmailReq.setDate(reportDates.getString(DeviceConstants.TO_DATE));
					unRLEmailReq.setYear(currentFinancialYear);
				}

				con = performUnPLEmailDownloadAPICall(session, segmentType, con, unRLEmailReq);

			}

			
			ServletOutputStream os = response.getOutputStream();
			HttpURLConnection httpURLCon = con.get();
	        Map<String, List<String>> responseHeaders = httpURLCon.getHeaderFields();
			if((Integer.parseInt(responseHeaders.get("Content-Length").get(0))==0 && !responseHeaders.containsKey("Content-Type"))) {
				GCAPIAuthToken.resetAuthCode();
				if (reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION)) {
					con = performTrnsDownloadAPI(session, segmentType, currentFinancialYear, trxnMailRequest);
				}
				else if (reportType.equalsIgnoreCase(DeviceConstants.HOLDINGS)) {
					StockDownloadAPI download = new StockDownloadAPI();
					holdingEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
					con = download.getDownloadResp(holdingEmailReq, DownloadResponse.class, session.getAppID());
				}
				else if (reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFIT_LOSS)) {
					con = performRLEmailDownloadAPICall(session, segmentType, currentFinancialYear, rlEmailReq);
				}
				else if (reportType.equalsIgnoreCase(DeviceConstants.LEDGER)) {
					ledgerEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
					LedgerDownloadAPI download = new LedgerDownloadAPI();
					con = download.getDownloadResp(ledgerEmailRequest, DownloadResponse.class, session.getAppID());
				}
				else if (reportType.equalsIgnoreCase(DeviceConstants.PORTFOLIO)) {
					portfolioEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
					PortfolioDownloadAPI download = new PortfolioDownloadAPI();
					con = download.getDownloadResp(portfolioEmailReq, DownloadResponse.class, session.getAppID());
				}
				else if (reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES)) {
					cnEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
					ContractNotesDownloadAPI download = new ContractNotesDownloadAPI();
					con = download.getDownloadResp(cnEmailRequest, DownloadResponse.class, session.getAppID());
				}
				else if (reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL)) {
					saudaEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
					con = performSaudaEmailDownloadAPICall(session, segmentType, currentFinancialYear, saudaEmailRequest);
				}
				else if (reportType.equalsIgnoreCase(DeviceConstants.TAX)) {
					taxEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
					con = performTaxEmailDownloadAPICall(session, segmentType, con, taxEmailReq);
				}
				else if (reportType.equalsIgnoreCase(DeviceConstants.UNREALISED_PROFIT_LOSS)) {
					unRLEmailReq.setToken(GCAPIAuthToken.getAuthToken());
					con = performUnPLEmailDownloadAPICall(session, segmentType, con, unRLEmailReq);
				}
			}
			readResponse(con, os, response, session.getAppID());

		} catch (Exception e) {
			log.error(e);
		}

	}

	public static GCHttpConnection performUnPLEmailDownloadAPICall(Session session, String segmentType,
			GCHttpConnection con, GetUnPLEmailRequest unRLEmailReq) throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {

			GetUnRelEquityDownloadAPI download = new GetUnRelEquityDownloadAPI();
			con = download.getDownloadResp(unRLEmailReq, DownloadResponse.class, session.getAppID());

		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {

			GetUnRelDerivativeDownloadAPI download = new GetUnRelDerivativeDownloadAPI();
			con = download.getDownloadResp(unRLEmailReq, DownloadResponse.class, session.getAppID());

		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {

			GetUnRelCurrencyDownloadAPI download = new GetUnRelCurrencyDownloadAPI();
			con = download.getDownloadResp(unRLEmailReq, DownloadResponse.class, session.getAppID());

		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {

			GetUnRelCommodityDownloadAPI download = new GetUnRelCommodityDownloadAPI();
			con = download.getDownloadResp(unRLEmailReq, DownloadResponse.class, session.getAppID());
		}
		return con;
	}

	public static GCHttpConnection performTaxEmailDownloadAPICall(Session session, String segmentType,
			GCHttpConnection con, GetTaxEmailDownloadRequest taxEmailReq) throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			EquityTaxDownloadAPI download = new EquityTaxDownloadAPI();
			con = download.getDownloadResp(taxEmailReq, DownloadResponse.class, session.getAppID());
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			DerivativeTaxDownloadAPI download = new DerivativeTaxDownloadAPI();
			con = download.getDownloadResp(taxEmailReq, DownloadResponse.class, session.getAppID());
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			CurrencyTaxDownloadAPI download = new CurrencyTaxDownloadAPI();
			con = download.getDownloadResp(taxEmailReq, DownloadResponse.class, session.getAppID());
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			CommodityTaxDownloadAPI download = new CommodityTaxDownloadAPI();
			con = download.getDownloadResp(taxEmailReq, DownloadResponse.class, session.getAppID());
		}
		return con;
	}

	public static GCHttpConnection performSaudaEmailDownloadAPICall(Session session, String segmentType,
			String currentFinancialYear, GetSaudaEmailDownloadRequest saudaEmailRequest) throws GCException {
		GCHttpConnection con;
		if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			saudaEmailRequest.setYear(currentFinancialYear);
		}

		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			EquitySaudaDownloadAPI download = new EquitySaudaDownloadAPI();
			con = download.getDownloadResp(saudaEmailRequest, DownloadResponse.class, session.getAppID());
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			DerivativeSaudaDownloadAPI download = new DerivativeSaudaDownloadAPI();
			con = download.getDownloadResp(saudaEmailRequest, DownloadResponse.class, session.getAppID());
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			CurrencySaudaDownloadAPI download = new CurrencySaudaDownloadAPI();
			con = download.getDownloadResp(saudaEmailRequest, DownloadResponse.class, session.getAppID());
		} else {
			CommoditySaudaDownloadAPI download = new CommoditySaudaDownloadAPI();
			con = download.getDownloadResp(saudaEmailRequest, DownloadResponse.class, session.getAppID());
		}
		return con;
	}

	public static GCHttpConnection performRLEmailDownloadAPICall(Session session, String segmentType,
			String currentFinancialYear, GetRLEmailDownloadRequest rlEmailReq) throws GCException {
		GCHttpConnection con;
		if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			rlEmailReq.setType("N");
			rlEmailReq.setYear(currentFinancialYear);
		}
		rlEmailReq.setToken(GCAPIAuthToken.getAuthToken());

		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {

			EquityPLDownloadAPI download = new EquityPLDownloadAPI();
			con = download.getDownloadResp(rlEmailReq, DownloadResponse.class, session.getAppID());

		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {

			DerivativePLDownloadAPI download = new DerivativePLDownloadAPI();
			con = download.getDownloadResp(rlEmailReq, DownloadResponse.class, session.getAppID());

		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			CurrencyPLDownloadAPI download = new CurrencyPLDownloadAPI();
			con = download.getDownloadResp(rlEmailReq, DownloadResponse.class, session.getAppID());

		} else {
			CommodityPLDownloadAPI download = new CommodityPLDownloadAPI();
			con = download.getDownloadResp(rlEmailReq, DownloadResponse.class, session.getAppID());
		}
		return con;
	}

	public static GCHttpConnection performTrnsDownloadAPI(Session session, String segmentType,
			String currentFinancialYear, GetTrxnMailDownloadRequest trxnMailRequest) throws GCException {
		GCHttpConnection con;
		if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			trxnMailRequest.setYear(currentFinancialYear);
		}
		trxnMailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {

			EquityTrxnDownloadAPI download = new EquityTrxnDownloadAPI();
			con = download.getDownloadResp(trxnMailRequest, DownloadResponse.class, session.getAppID());

		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {

			DerivativeTrxnDownloadAPI download = new DerivativeTrxnDownloadAPI();
			con = download.getDownloadResp(trxnMailRequest, DownloadResponse.class, session.getAppID());

		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			CurrencyTrxnDownloadAPI download = new CurrencyTrxnDownloadAPI();
			con = download.getDownloadResp(trxnMailRequest, DownloadResponse.class, session.getAppID());

		} else {
			CommodityTrxnDownloadAPI download = new CommodityTrxnDownloadAPI();
			con = download.getDownloadResp(trxnMailRequest, DownloadResponse.class, session.getAppID());
		}
		return con;
	}
	
	private static void readResponse(GCHttpConnection connection, ServletOutputStream os, 
			HttpServletResponse response, String appIDForLogging) throws IOException {

		long requestTime = System.currentTimeMillis();
		
		HttpURLConnection httpURLCon = null;
		InputStream in = null;
		long reqTimeTaken, responseTime;

		try {
			
		httpURLCon = connection.get();
		responseTime = System.currentTimeMillis();
		reqTimeTaken = responseTime - requestTime;
		log.info("AppID: " + appIDForLogging + " time_taken=" + reqTimeTaken + " API Response " +SpyderConstants.VENDOR_NAME + ":");
        int returnCode = httpURLCon.getResponseCode();
        log.debug("HTTP Return code : " + returnCode);
        log.debug("Content type : "+httpURLCon.getContentType());
        Map<String, List<String>> responseHeaders = httpURLCon.getHeaderFields();
		Set<String> keys = responseHeaders.keySet();

		for (String keyHeader : keys) {
			for (String headerVal : responseHeaders.get(keyHeader)) {
				log.debug("" + keyHeader + "" + headerVal);
				response.setHeader(keyHeader, headerVal);
			}
		}
		
        // https://developer.mozilla.org/en-US/docs/Web/HTTP/Status
        if (returnCode >= 400 && returnCode <= 599)
            in = httpURLCon.getErrorStream();
        else
            in = httpURLCon.getInputStream();

        byte[] buffer = new byte[4096];
        int readLength = 0;
        while ((readLength = in.read(buffer, 0, buffer.length)) != -1) {
        	os.write(buffer, 0, readLength);
        }

        httpURLCon.disconnect();
        os.close();
        in.close();
        
		} catch (SocketTimeoutException e) {
			responseTime = System.currentTimeMillis();
			reqTimeTaken = responseTime - requestTime;
			log.info("AppID: " + appIDForLogging + " time_taken=" + reqTimeTaken + " API Response " +SpyderConstants.VENDOR_NAME + ":");
			if(httpURLCon!=null) {
	        httpURLCon.disconnect();
	        in.close();
			}
			os.close();
			
		}

    }
	
}