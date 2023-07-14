package com.globecapital.business.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.api.gc.backoffice.CommodityTransactionEmailAPI;
import com.globecapital.api.gc.backoffice.CurrencyTransactionEmailAPI;
import com.globecapital.api.gc.backoffice.DerivativeTransactionEmailAPI;
import com.globecapital.api.gc.backoffice.EmailResponse;
import com.globecapital.api.gc.backoffice.EquityTransactionEmailAPI;
import com.globecapital.api.gc.backoffice.GetCNEmailRequest;
import com.globecapital.api.gc.backoffice.GetCommoditySaudaEmailAPI;
import com.globecapital.api.gc.backoffice.GetCommodityTaxEmailAPI;
import com.globecapital.api.gc.backoffice.GetContractEmailAPI;
import com.globecapital.api.gc.backoffice.GetCurrencySaudaEmailAPI;
import com.globecapital.api.gc.backoffice.GetCurrencyTaxEmailAPI;
import com.globecapital.api.gc.backoffice.GetDerivativeSaudaEmailAPI;
import com.globecapital.api.gc.backoffice.GetDerivativeTaxEmailAPI;
import com.globecapital.api.gc.backoffice.GetEquitySaudaEmailAPI;
import com.globecapital.api.gc.backoffice.GetEquityTaxEmailAPI;
import com.globecapital.api.gc.backoffice.GetHoldingPortfolioEmailRequest;
import com.globecapital.api.gc.backoffice.GetHoldingsEmailAPI;
import com.globecapital.api.gc.backoffice.GetLedgerEmailAPI;
import com.globecapital.api.gc.backoffice.GetLedgerEmailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetPortfolioEmailAPI;
import com.globecapital.api.gc.backoffice.GetRLCommodityPLEmailAPI;
import com.globecapital.api.gc.backoffice.GetRLCurrencyPLEmailAPI;
import com.globecapital.api.gc.backoffice.GetRLDerivativePLEmailAPI;
import com.globecapital.api.gc.backoffice.GetRLEmailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetRLEquityPLEmailAPI;
import com.globecapital.api.gc.backoffice.GetSaudaEmailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetTaxEmailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetTrxnMailDownloadRequest;
import com.globecapital.api.gc.backoffice.GetUnPLEmailRequest;
import com.globecapital.api.gc.backoffice.GetUnRelCommodityEmailAPI;
import com.globecapital.api.gc.backoffice.GetUnRelCurrencyEmailAPI;
import com.globecapital.api.gc.backoffice.GetUnRelDerivativeEmailAPI;
import com.globecapital.api.gc.backoffice.GetUnRelEquityEmailAPI;
import com.globecapital.api.gc.generics.GCAPIAuthToken;
import com.globecapital.api.gc.generics.GCConstants;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.MessageConstants;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.utils.DateUtils;
import com.globecapital.utils.GCUtils;
import com.msf.log.Logger;

public class Email {

	public static Logger log = Logger.getLogger(Email.class);

	public static String getTransactionEmailStatus(Session session, String segmentType, JSONObject filterObj,
			String reportType) {

		String status = "";
		String segment = "";
		String userId = session.getUserID();
		try {

			EmailResponse emailResponse = new EmailResponse();
			String filterType = filterObj.getString(DeviceConstants.DATE_FILTER);
			JSONObject reportDates = FilterType.getFilterDates(filterType, filterObj);
			String currentFinancialYear = DateUtils.getFinancialYearByDate(reportDates);

			if (reportType.equalsIgnoreCase(DeviceConstants.TRANSACTION)) {

				GetTrxnMailDownloadRequest trxnMailRequest = new GetTrxnMailDownloadRequest();
				trxnMailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				
				trxnMailRequest.setClientCode(userId);
				
				trxnMailRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
				trxnMailRequest.setToDate(reportDates.getString(DeviceConstants.TO_DATE));

				if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
					trxnMailRequest.setYear(currentFinancialYear);
				}
				emailResponse = performTrxnEmailAPICall(session, segmentType, emailResponse, trxnMailRequest);
				
				if(emailResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					trxnMailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
					emailResponse = performTrxnEmailAPICall(session, segmentType, emailResponse, trxnMailRequest);
				}
			} else if (reportType.equalsIgnoreCase(DeviceConstants.LEDGER)) {

				JSONArray filterBy = filterObj.getJSONArray((DeviceConstants.FILTER_BY));

				GetLedgerEmailDownloadRequest ledgerEmailRequest = new GetLedgerEmailDownloadRequest();
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
//				ledgerEmailRequest.setYear(currentFinancialYear);
				ledgerEmailRequest.setSegment(segment);
				ledgerEmailRequest.setYear(currentFinancialYear);
				ledgerEmailRequest.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
				ledgerEmailRequest.setToDate(reportDates.getString(DeviceConstants.TO_DATE));
				GetLedgerEmailAPI ledgerEmail = new GetLedgerEmailAPI();
				emailResponse = ledgerEmail.get(ledgerEmailRequest, EmailResponse.class, session.getAppID(),
						"GetLedgerEmail");
				if(emailResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					ledgerEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
					emailResponse = ledgerEmail.get(ledgerEmailRequest, EmailResponse.class, session.getAppID()
							,"GetLedgerEmail");
				}
			} else if (reportType.equalsIgnoreCase(DeviceConstants.CONTRACT_NOTES)) {

				GetCNEmailRequest cnEmailRequest = new GetCNEmailRequest();
				cnEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				cnEmailRequest.setDate(DateUtils.formatDate(filterObj.getString(DeviceConstants.TO_DATE)));
				
				cnEmailRequest.setClientCode(userId);
				
				if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY))
					cnEmailRequest.setSegment("EQ");
				else 
					cnEmailRequest.setSegment("CO");
				
				cnEmailRequest.setYear(currentFinancialYear);
				GetContractEmailAPI contractEmail = new GetContractEmailAPI();
				emailResponse = contractEmail.get(cnEmailRequest, EmailResponse.class, session.getAppID()
						,"GetContractEmail");
				if(emailResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					cnEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
					emailResponse = contractEmail.get(cnEmailRequest, EmailResponse.class, session.getAppID()
							,"GetContractEmail");
				}

			} else if (reportType.equalsIgnoreCase(DeviceConstants.SAUDA_BILL)) {

				GetSaudaEmailDownloadRequest saudaEmailRequest = new GetSaudaEmailDownloadRequest();
//				saudaEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				
				saudaEmailRequest.setClientCode(userId);
				
				saudaEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
				saudaEmailRequest.setDate(reportDates.getString(DeviceConstants.TO_DATE));
				if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
					saudaEmailRequest.setYear(currentFinancialYear);
				}

				emailResponse = performSaudaEmailAPICall(session, segmentType, emailResponse, currentFinancialYear, saudaEmailRequest);
				if(emailResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					saudaEmailRequest.setAuthCode(GCAPIAuthToken.getAuthToken());
					emailResponse = performSaudaEmailAPICall(session, segmentType, emailResponse, currentFinancialYear, saudaEmailRequest);
				}

			} else if (reportType.equalsIgnoreCase(DeviceConstants.HOLDINGS)) {

				GetHoldingPortfolioEmailRequest holdingEmailReq = new GetHoldingPortfolioEmailRequest();
				holdingEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
				holdingEmailReq.setClientCode(userId);
				GetHoldingsEmailAPI holdingsEmail = new GetHoldingsEmailAPI();
				emailResponse = holdingsEmail.get(holdingEmailReq, EmailResponse.class, session.getAppID()
						,"GetHoldingsEmail");
				if(emailResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					holdingEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
					emailResponse = holdingsEmail.get(holdingEmailReq, EmailResponse.class, session.getAppID()
							,"GetHoldingsEmail");
				}
			} else if (reportType.equalsIgnoreCase(DeviceConstants.PORTFOLIO)) {

				GetHoldingPortfolioEmailRequest holdingEmailReq = new GetHoldingPortfolioEmailRequest();
				holdingEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
				holdingEmailReq.setClientCode(userId);
				GetPortfolioEmailAPI portfolioEmail = new GetPortfolioEmailAPI();
				emailResponse = portfolioEmail.get(holdingEmailReq, EmailResponse.class, session.getAppID()
						,"GetPortfolioEmail");
				if(emailResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					holdingEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
					emailResponse = portfolioEmail.get(holdingEmailReq, EmailResponse.class, session.getAppID()
							,"GetPortfolioEmail");
				}
			} else if (reportType.equalsIgnoreCase(DeviceConstants.TAX)) {

				GetTaxEmailDownloadRequest taxEmailReq = new GetTaxEmailDownloadRequest();
//				taxEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
				
				taxEmailReq.setClientCode(userId);
				taxEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
				if (filterType.contains(DeviceConstants.FINANCIAL_YEAR+DateUtils.getFinancialYear()) ||
						filterType.contains(DeviceConstants.FINANCIAL_YEAR+DateUtils.getPreviousFinancialYear()) || filterType.isEmpty()) {
					taxEmailReq.setYear(GCUtils.getFinancialYear(filterObj.getString(DeviceConstants.DATE_FILTER)));
				}
//				taxEmailReq.setYear(currentFinancialYear);
				emailResponse = performTaxEmailAPICall(session, segmentType, emailResponse, taxEmailReq);
				if(emailResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					taxEmailReq.setAuthCode(GCAPIAuthToken.getAuthToken());
					emailResponse = performTaxEmailAPICall(session, segmentType, emailResponse, taxEmailReq);
				}
			} else if (reportType.equalsIgnoreCase(DeviceConstants.REALISED_PROFITLOSS)) {

				GetRLEmailDownloadRequest rlEmailReq = new GetRLEmailDownloadRequest();
//				rlEmailReq.setToken(GCAPIAuthToken.getAuthToken());
				
				rlEmailReq.setClientCode(userId);
				
				rlEmailReq.setFromDate(reportDates.getString(DeviceConstants.FROM_DATE));
				rlEmailReq.setToDate(reportDates.getString(DeviceConstants.TO_DATE));

				if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
					rlEmailReq.setType("N");
					rlEmailReq.setYear(currentFinancialYear);
				}
				rlEmailReq.setToken(GCAPIAuthToken.getAuthToken());
				
				emailResponse = performRLEmailAPICall(session, segmentType, emailResponse, rlEmailReq);
				if(emailResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					rlEmailReq.setToken(GCAPIAuthToken.getAuthToken());
					emailResponse = performRLEmailAPICall(session, segmentType, emailResponse, rlEmailReq);
				}
			} else if (reportType.equalsIgnoreCase(DeviceConstants.UNREALISED_PROFITLOSS)) {

				GetUnPLEmailRequest unRLEmailReq = new GetUnPLEmailRequest();
				unRLEmailReq.setToken(GCAPIAuthToken.getAuthToken());

				unRLEmailReq.setClientCode(userId);
				

				if (!segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
					unRLEmailReq.setDate(reportDates.getString(DeviceConstants.TO_DATE));
					unRLEmailReq.setYear(currentFinancialYear);
				}

				emailResponse = performUnRLEmailAPICall(session, segmentType, emailResponse, unRLEmailReq);
				
				if(emailResponse.getMessage().equalsIgnoreCase(MessageConstants.SESSION_EXPIRED)) {
					unRLEmailReq.setToken(GCAPIAuthToken.getAuthToken());
					emailResponse = performUnRLEmailAPICall(session, segmentType, emailResponse, unRLEmailReq);
				}
			}
			status = emailResponse.getStatus();
//			status = "Success";

		} catch (Exception e) {
			log.error(e);
		}
		return status;
	}

	public static EmailResponse performUnRLEmailAPICall(Session session, String segmentType, EmailResponse emailResponse,
			GetUnPLEmailRequest unRLEmailReq) throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			GetUnRelEquityEmailAPI equityEmail = new GetUnRelEquityEmailAPI();
			emailResponse = equityEmail.get(unRLEmailReq, EmailResponse.class, session.getAppID()
					,"GetUnRelEquityPLEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			GetUnRelDerivativeEmailAPI derivativeEmail = new GetUnRelDerivativeEmailAPI();
			emailResponse = derivativeEmail.get(unRLEmailReq, EmailResponse.class, session.getAppID(),
					"GetUnRelDerivativePLEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			GetUnRelCommodityEmailAPI commodityEmail = new GetUnRelCommodityEmailAPI();
			emailResponse = commodityEmail.get(unRLEmailReq, EmailResponse.class, session.getAppID()
					,"GetUnRelCommodityPLEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			GetUnRelCurrencyEmailAPI currencyEmail = new GetUnRelCurrencyEmailAPI();
			emailResponse = currencyEmail.get(unRLEmailReq, EmailResponse.class, session.getAppID()
					,"GetUnRelCurrencyPLEmail");
		}
		return emailResponse;
	}

	public static EmailResponse performRLEmailAPICall(Session session, String segmentType, EmailResponse emailResponse,
			GetRLEmailDownloadRequest rlEmailReq) throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			GetRLEquityPLEmailAPI equityEmail = new GetRLEquityPLEmailAPI();
			emailResponse = equityEmail.get(rlEmailReq, EmailResponse.class, session.getAppID()
					,"GetRLEquityEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			GetRLDerivativePLEmailAPI derivativeEmail = new GetRLDerivativePLEmailAPI();
			emailResponse = derivativeEmail.get(rlEmailReq, EmailResponse.class, session.getAppID()
					,"GetRLDerivativeEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			GetRLCommodityPLEmailAPI commodityEmail = new GetRLCommodityPLEmailAPI();
			emailResponse = commodityEmail.get(rlEmailReq, EmailResponse.class, session.getAppID()
					,"GetRLCommodityEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			GetRLCurrencyPLEmailAPI currencyEmail = new GetRLCurrencyPLEmailAPI();
			emailResponse = currencyEmail.get(rlEmailReq, EmailResponse.class, session.getAppID()
					,"GetRLCurrencyEmail");
		}
		return emailResponse;
	}

	public static EmailResponse performTaxEmailAPICall(Session session, String segmentType, EmailResponse emailResponse,
			GetTaxEmailDownloadRequest taxEmailReq) throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {

			GetEquityTaxEmailAPI equityEmail = new GetEquityTaxEmailAPI();
			emailResponse = equityEmail.get(taxEmailReq, EmailResponse.class, session.getAppID()
					,"GetEquityTaxEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			GetDerivativeTaxEmailAPI derivativeEmail = new GetDerivativeTaxEmailAPI();
			emailResponse = derivativeEmail.get(taxEmailReq, EmailResponse.class, session.getAppID()
					,"GetDerivativeTaxEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			GetCommodityTaxEmailAPI commodityEmail = new GetCommodityTaxEmailAPI();
			emailResponse = commodityEmail.get(taxEmailReq, EmailResponse.class, session.getAppID()
					,"GetCommodityTaxEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {

			GetCurrencyTaxEmailAPI currencyEmail = new GetCurrencyTaxEmailAPI();
			emailResponse = currencyEmail.get(taxEmailReq, EmailResponse.class, session.getAppID()
					,"GetCurrencyTaxEmail");
		}
		return emailResponse;
	}

	public static EmailResponse performSaudaEmailAPICall(Session session, String segmentType, EmailResponse emailResponse,
			String currentFinancialYear, GetSaudaEmailDownloadRequest saudaEmailRequest) throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {
			GetEquitySaudaEmailAPI equityEmail = new GetEquitySaudaEmailAPI();
			emailResponse = equityEmail.get(saudaEmailRequest, EmailResponse.class, session.getAppID()
					,"GetEquitySaudaEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			saudaEmailRequest.setYear(currentFinancialYear);
			GetDerivativeSaudaEmailAPI derivativeEmail = new GetDerivativeSaudaEmailAPI();
			emailResponse = derivativeEmail.get(saudaEmailRequest, EmailResponse.class, session.getAppID()
					,"GetDerivativeSaudaEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			saudaEmailRequest.setYear(currentFinancialYear);
			GetCommoditySaudaEmailAPI commodityEmail = new GetCommoditySaudaEmailAPI();
			emailResponse = commodityEmail.get(saudaEmailRequest, EmailResponse.class, session.getAppID()
					,"GetCommoditySaudaEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			saudaEmailRequest.setYear(currentFinancialYear);
			GetCurrencySaudaEmailAPI currencyEmail = new GetCurrencySaudaEmailAPI();
			emailResponse = currencyEmail.get(saudaEmailRequest, EmailResponse.class, session.getAppID()
					,"GetCurrencySaudaEmail");
		}
		return emailResponse;
	}

	public static EmailResponse performTrxnEmailAPICall(Session session, String segmentType, EmailResponse emailResponse,
			GetTrxnMailDownloadRequest trxnMailRequest) throws GCException {
		if (segmentType.equalsIgnoreCase(DeviceConstants.EQUITY)) {

			EquityTransactionEmailAPI eqEmail = new EquityTransactionEmailAPI();
			emailResponse = eqEmail.get(trxnMailRequest, EmailResponse.class, session.getAppID()
					,"GetEquityTransactionEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.DERIVATIVE)) {
			DerivativeTransactionEmailAPI foEmail = new DerivativeTransactionEmailAPI();
			emailResponse = foEmail.get(trxnMailRequest, EmailResponse.class, session.getAppID()
					,"GetDerivativeTransactionEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.CURRENCY)) {
			CurrencyTransactionEmailAPI curEmail = new CurrencyTransactionEmailAPI();
			emailResponse = curEmail.get(trxnMailRequest, EmailResponse.class, session.getAppID()
					,"GetCurrencyTransactionEmail");
		} else if (segmentType.equalsIgnoreCase(DeviceConstants.COMMODITY)) {
			CommodityTransactionEmailAPI commEmail = new CommodityTransactionEmailAPI();
			emailResponse = commEmail.get(trxnMailRequest, EmailResponse.class, session.getAppID()
					,"GetCommodityTransactionEmail");
		}
		return emailResponse;
	}

}
