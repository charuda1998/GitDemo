package com.globecapital.symbology;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.constants.DBConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;

public class SymbolRow extends JSONObject {

	public SymbolRow() {

		setSymbol(new Symbol());
	}

	public SymbolRow(JSONObject symbolobj) {

		this.put(SymbolConstants.SYMBOL_OBJ, new Symbol(symbolobj));
	}

	public void setSymbol(Symbol symBlock) {
		this.put(SymbolConstants.SYMBOL_OBJ, new Symbol(symBlock));
	}

	public void extend(SymbolRow otherRow) {

		String[] fields = JSONObject.getNames(otherRow);
		for (String f : fields) {
			this.put(f, otherRow.get(f));
		}

	}

	public void extend(JSONObject jsonObject) {

		String[] fields = JSONObject.getNames(jsonObject);
		for (String f : fields) {
			this.put(f, jsonObject.get(f));
		}
	}

	public SymbolRow(ResultSet rs) throws JSONException, SQLException {

		JSONObject streamInfo = new JSONObject();

		this.put(SymbolConstants.SYMBOL_OBJ, new Symbol());
		this.setSymDetail(rs.getString(DBConstants.SEARCH_SYMBOL_DETAILS));
		this.setSymbol(rs.getString(DBConstants.SYMBOL_NAME));
		this.setInstrument(rs.getString(DBConstants.INSTRUMENT_NAME));
		this.setTickPrice(rs.getString(DBConstants.TICK_PRICE));
		if(rs.getString(DBConstants.SEGMENT).equalsIgnoreCase(ExchangeSegment.NSECDS_SEGMENT_ID))
			this.setDispLotSize("1");
		else
			this.setDispLotSize(rs.getString(DBConstants.LOT_SIZE));
		this.setLotSize(rs.getString(DBConstants.LOT_SIZE));
		this.setBasePrice(rs.getString(DBConstants.BASE_PRICE));
		this.setSeries(rs.getString(DBConstants.SERIES));
		this.setMktSegId(rs.getString(DBConstants.SEGMENT));
		this.settokenId(rs.getString(DBConstants.TOKEN));
		this.setExpiry(rs.getString(DBConstants.EXPIRY_DATE));
		this.setStrikePrice(rs.getString(DBConstants.STRIKE_PRICE));
		this.setExchange(rs.getString(DBConstants.EXCHANGE_NAME));
		this.setPrecision(rs.getString(DBConstants.PRECISION));
		streamInfo.put(SymbolConstants.MKT_SEG_ID, rs.getString(DBConstants.SEGMENT));
		streamInfo.put(SymbolConstants.TOKEN, rs.getString(DBConstants.TOKEN));
		this.setStreamInfo(streamInfo);
		this.setSymbolToken(rs.getString(DBConstants.TOKEN) + "_" + rs.getString(DBConstants.SEGMENT));
		this.setDisplaySymbol(rs.getString(DBConstants.SYMBOL_NAME));
		this.setDisplaySymbolDetails(rs.getString(DBConstants.SEARCH_SYMBOL_DETAILS));
		this.setOptionType(rs.getString(DBConstants.OPTION));
		this.setCompanyName(rs.getString(DBConstants.COMPANY_NAME));
		this.setDecimalLocator(rs.getString(DBConstants.DECIMAL_LOCATOR));
		this.setISIN(rs.getString(DBConstants.ISIN));
		this.setDispPriceTick(rs.getString(DBConstants.DISP_PRICE_TICK));
		if (isThere(rs, DBConstants.MAPPING_SYMBOL_UNIQ_DESC)) {
			this.put(DBConstants.MAPPING_SYMBOL_UNIQ_DESC, rs.getString(DBConstants.MAPPING_SYMBOL_UNIQ_DESC));
			this.setMappingSymbolUniqDesc(rs.getString(DBConstants.MAPPING_SYMBOL_UNIQ_DESC));
		}
		if (isThere(rs, DBConstants.CM_CO_CODE))

		{
			String sCoCode = rs.getString(DBConstants.CM_CO_CODE);
			this.setCMCoCode(rs.wasNull() ? "" : sCoCode);
		}
		if (isThere(rs, DBConstants.CM_SECTOR_NAME)) {
			String sector = rs.getString(DBConstants.CM_SECTOR_NAME);
			this.setCMSectorName(rs.wasNull() ? "" : sector);
		}
		if (isThere(rs, DBConstants.ASSET_TOKEN)) {
			String sAssetToken = rs.getString(DBConstants.ASSET_TOKEN);
			this.setAssetToken(rs.wasNull() ? "" : sAssetToken);
		}
		if(isThere(rs, DBConstants.CM_SECTOR_FORMAT)) {
			String sSectorFormat = rs.getString(DBConstants.CM_SECTOR_FORMAT);
			this.setCMSectorFormat(sSectorFormat);
		}
		if(isThere(rs, DBConstants.SYMBOL_UNIQ_DESC)) {
			String sSymbolUniqDesc = rs.getString(DBConstants.SYMBOL_UNIQ_DESC);
			this.setSymbolUniqDesc(sSymbolUniqDesc);
		}
		if(isThere(rs, DBConstants.PRICE_NUM)) {
			String sPriceNum = rs.getString(DBConstants.PRICE_NUM);
			this.setPriceNum(sPriceNum);
		}
		if(isThere(rs, DBConstants.PRICE_DEN)) {
			String sPriceDen = rs.getString(DBConstants.PRICE_DEN);
			this.setPriceDen(sPriceDen);
		}
		this.setSymbolDetails1(rs.getString(DBConstants.SYMBOL_DETAILS_1));
		this.setSymbolDetails2(rs.getString(DBConstants.SYMBOL_DETAILS_2));
	}

	private boolean isThere(ResultSet rs, String column) {
		try {
			rs.findColumn(column);
			return true;
		} catch (SQLException sqlex) {
		}
		return false;
	}

	public JSONObject getMinimisedSymbolRow() {

		JSONObject minimisedsymbolObject = new JSONObject();

		minimisedsymbolObject.put(SymbolConstants.SYMBOL, this.getSymBlock().getString(SymbolConstants.SYMBOL));
		minimisedsymbolObject.put(SymbolConstants.COMPANY_NAME,
				this.getSymBlock().getString(SymbolConstants.COMPANY_NAME));
		minimisedsymbolObject.put(SymbolConstants.SYMBOL_DETAILS,
				this.getSymBlock().getString(SymbolConstants.SYMBOL_DETAILS));
		minimisedsymbolObject.put(SymbolConstants.TICK_PRICE, this.getSymBlock().getString(SymbolConstants.DISP_PRICE_TICK));
		if(this.getSymBlock().getString(SymbolConstants.EXCHANGE).equalsIgnoreCase(ExchangeSegment.NSECDS))
			minimisedsymbolObject.put(SymbolConstants.DISP_LOT_SIZE, "1");
		else
			minimisedsymbolObject.put(SymbolConstants.DISP_LOT_SIZE, this.getSymBlock().getString(SymbolConstants.DISP_LOT_SIZE));
		minimisedsymbolObject.put(SymbolConstants.LOT_SIZE, this.getSymBlock().getString(SymbolConstants.LOT_SIZE));
		minimisedsymbolObject.put(SymbolConstants.EXCHANGE, this.getSymBlock().getString(SymbolConstants.EXCHANGE));
		minimisedsymbolObject.put(SymbolConstants.PRECISION, this.getSymBlock().getString(SymbolConstants.PRECISION));
		minimisedsymbolObject.put(SymbolConstants.STREAM_SYMBOL,
				this.getSymBlock().getJSONObject(SymbolConstants.STREAM_SYMBOL));
		minimisedsymbolObject.put(SymbolConstants.SYMBOL_TOKEN,
				this.getSymBlock().getString(SymbolConstants.SYMBOL_TOKEN));
		minimisedsymbolObject.put(SymbolConstants.DISP_PRICE_TICK,
				this.getSymBlock().getString(SymbolConstants.DISP_PRICE_TICK));
		minimisedsymbolObject.put(SymbolConstants.SYMBOL_DETAILS_1,
				this.getSymBlock().optString(SymbolConstants.SYMBOL_DETAILS_1));
		minimisedsymbolObject.put(SymbolConstants.SYMBOL_DETAILS_2,
				this.getSymBlock().optString(SymbolConstants.SYMBOL_DETAILS_2));
		if(this.getSymBlock().has(DBConstants.MAPPING_SYMBOL_UNIQ_DESC))
			minimisedsymbolObject.put(DBConstants.MAPPING_SYMBOL_UNIQ_DESC, this.getSymBlock().optString(DBConstants.MAPPING_SYMBOL_UNIQ_DESC));
		return new SymbolRow(minimisedsymbolObject);

	}

	public SymbolRow(Object object) {

		String[] fields = JSONObject.getNames(object);
		for (String f : fields) {
			this.put(f, ((JSONObject) object).get(f));
		}
	}

	@Override
	public JSONObject put(String key, Object value) throws JSONException {
		return super.put(key, value);
	}

	public void setCMCoCode(String sCoCode) {
		this.getSymBlock().put(SymbolConstants.CO_CODE, sCoCode);
	}

	public String getCMCoCode() {
		return this.getSymBlock().getString((SymbolConstants.CO_CODE));
	}

	public void setCMSectorName(String sSectorName) {
		this.getSymBlock().put(SymbolConstants.SECTOR_NAME, sSectorName);
	}

	public String getCMSectorName() {
		return this.getSymBlock().getString((SymbolConstants.SECTOR_NAME));
	}

	public void setAssetToken(String sAssetToken) {
		this.getSymBlock().put(SymbolConstants.ASSET_TOKEN, sAssetToken);
	}

	public String getAssetToken() {
		return this.getSymBlock().getString((SymbolConstants.ASSET_TOKEN));
	}

	public void setCMSectorFormat(String sSectorFormat) {
		this.getSymBlock().put(SymbolConstants.SECTOR_FORMAT, sSectorFormat);
	}

	public String getCMSectorFormat() {
		return this.getSymBlock().getString((SymbolConstants.SECTOR_FORMAT));
	}
	
	public void setSymbolUniqDesc(String sSymbolUniqDesc) {
		this.getSymBlock().put(SymbolConstants.SYMBOL_UNIQ_DESC, sSymbolUniqDesc);
	}

	public String getSymbolUniqDesc() {
		return this.getSymBlock().getString((SymbolConstants.SYMBOL_UNIQ_DESC));
	}
	
	public String getMappingSymbolUniqDesc() {
		return this.getSymBlock().getString((DBConstants.MAPPING_SYMBOL_UNIQ_DESC));
	}
	
	public JSONObject getSymBlock() {
		return this.getJSONObject(SymbolConstants.SYMBOL_OBJ);
	}

	public String getTokenSegmentID() {
		return this.getSymBlock().getString(SymbolConstants.TOKEN_SEGMENTID);
	}

	public void setTokenSegmentID(String id) {
		this.getSymBlock().put(SymbolConstants.TOKEN_SEGMENTID, id);
	}

	public String getPrecision() {
		return this.getSymBlock().getString(SymbolConstants.PRECISION);
	}

	public int getPrecisionInt() {
		return Integer.parseInt(this.getSymBlock().getString(SymbolConstants.PRECISION));
	}

	public void setPrecision(String precision) {
		this.getSymBlock().put(SymbolConstants.PRECISION, precision);
	}

	public String getBasePrice() {
		return this.getSymBlock().getString(SymbolConstants.BASE_PRICE);
	}

	public void setBasePrice(String basePrice) {
		this.getSymBlock().put(SymbolConstants.BASE_PRICE, basePrice);
	}

	public String getDispPriceTick() {
		return this.getSymBlock().getString(SymbolConstants.DISP_PRICE_TICK);
	}

	public void setDispPriceTick(String dispPriceTick) {
		this.getSymBlock().put(SymbolConstants.DISP_PRICE_TICK, dispPriceTick);
	}

	public void setISIN(String isin) {
		this.getSymBlock().put(SymbolConstants.ISIN, isin);
	}

	public String getISIN() {
		return this.getSymBlock().getString(SymbolConstants.ISIN);
	}
	
	public String getSymbol() {
		return this.getSymBlock().getString(SymbolConstants.SYMBOL);
	}

	public void setSymbol(String symbol) {
		this.getSymBlock().put(SymbolConstants.SYMBOL, symbol);
	}

	public String getSymbolToken() {
		return this.getSymBlock().getString(SymbolConstants.SYMBOL_TOKEN);
	}

	public void setSymbolToken(String symbolToken) {
		this.getSymBlock().put(SymbolConstants.SYMBOL_TOKEN, symbolToken);
	}

	public void setLotSize(String lot) throws JSONException {
		this.getSymBlock().put(SymbolConstants.LOT_SIZE, lot);
	}
	
	public void setDispLotSize(String lot) throws JSONException {
		this.getSymBlock().put(SymbolConstants.DISP_LOT_SIZE, lot);
	}

	public void setTickPrice(String tick) throws JSONException {
		this.getSymBlock().put(SymbolConstants.TICK_PRICE, tick);
	}

	public void setSymDetail(String symDetail) throws JSONException {
		this.getSymBlock().put(SymbolConstants.SYMBOL_DETAILS, symDetail);
	}

	public void setMktSegId(String segId) throws JSONException {
		this.getSymBlock().put(SymbolConstants.MKT_SEG_ID, segId);
	}

	public void settokenId(String tokenId) throws JSONException {
		this.getSymBlock().put(SymbolConstants.TOKEN, tokenId);
	}

	public void setExchange(String exch) throws JSONException {
		this.getSymBlock().put(SymbolConstants.EXCHANGE, exch);
	}

	public void setInstrument(String inst) throws JSONException {
		this.getSymBlock().put(SymbolConstants.INSTRUMENT_NAME, inst);

	}

	public void setSeries(String series) throws JSONException {
		this.getSymBlock().put(SymbolConstants.SERIES, series);
	}

	public void setExpiry(String expiry) throws JSONException {
		this.getSymBlock().put(SymbolConstants.EXPIRY, expiry);
	}

	public void setStrikePrice(String strikePrice) throws JSONException {
		this.getSymBlock().put(SymbolConstants.STRIKE_PRICE, strikePrice);
	}

	public void setDisplaySymbol(String dispSymbol) throws JSONException {
		this.getSymBlock().put(SymbolConstants.DISPLAY_SYMBOL, dispSymbol);
	}

	public void setDisplaySymbolDetails(String dispSymbolDetails) throws JSONException {
		this.getSymBlock().put(SymbolConstants.DISPLAY_SYMBOL_DETAILS, dispSymbolDetails);
	}

	public void setOptionType(String option) throws JSONException {
		this.getSymBlock().put(SymbolConstants.OPTION, option);
	}

	public void setCompanyName(String companyName) throws JSONException {
		this.getSymBlock().put(SymbolConstants.COMPANY_NAME, companyName);
	}

	public String getCompanyName() {
		return this.getSymBlock().getString(SymbolConstants.COMPANY_NAME);
	}

	public String getExchange() {
		return this.getSymBlock().getString(SymbolConstants.EXCHANGE);
	}

	public void setDecimalLocator(String decimalLocator) throws JSONException {

		this.getSymBlock().put(SymbolConstants.MULTIPLIER, Integer.parseInt(decimalLocator));
		this.getSymBlock().put(SymbolConstants.DIVISOR, decimalLocator);
	}

	public int getMultiplier() {
		return this.getSymBlock().getInt(SymbolConstants.MULTIPLIER);
	}

	public String getInstrument() {
		return this.getSymBlock().getString(SymbolConstants.INSTRUMENT_NAME);
	}

	public String getSymDetail() {
		return this.getSymBlock().getString(SymbolConstants.SYMBOL_DETAILS);
	}

	public String getTickPrice() {
		return this.getSymBlock().getString(SymbolConstants.TICK_PRICE);
	}

	public String getLotSize() {
		return this.getSymBlock().getString(SymbolConstants.LOT_SIZE);
	}

	public Integer getLotSizeInt() {
		return Integer.parseInt(this.getSymBlock().getString(SymbolConstants.LOT_SIZE));
	}

	public Integer getDispLotSizeInt() {
		return Integer.parseInt(this.getSymBlock().getString(SymbolConstants.DISP_LOT_SIZE));
	}
	
	public String gettokenId() {
		return this.getSymBlock().getString(SymbolConstants.TOKEN);
	}

	public String getSeries() {
		return this.getSymBlock().getString(SymbolConstants.SERIES);
	}

	public String getExpiry() {
		return this.getSymBlock().getString(SymbolConstants.EXPIRY);
	}

	public String getStrikePrice() {
		return this.getSymBlock().getString(SymbolConstants.STRIKE_PRICE);
	}

	public String getMktSegId() {
		return this.getSymBlock().getString(SymbolConstants.MKT_SEG_ID);
	}

	public void setStreamInfo(JSONObject streamInfo) throws JSONException {

		this.getSymBlock().put(SymbolConstants.STREAM_SYMBOL, streamInfo);

	}

	public String getSymbolDetails1() {
		return this.getSymBlock().getString(SymbolConstants.SYMBOL_DETAILS_1);
	}

	public void setSymbolDetails1(String symbolDetails1) {
		this.getSymBlock().put(SymbolConstants.SYMBOL_DETAILS_1, symbolDetails1);
	}

	public String getSymbolDetails2() {
		return this.getSymBlock().getString(SymbolConstants.SYMBOL_DETAILS_2);
	}

	public void setSymbolDetails2(String symbolDetails2) {
		this.getSymBlock().put(SymbolConstants.SYMBOL_DETAILS_2, symbolDetails2);
	}
	
	public void setMappingSymbolUniqDesc(String mappingSymbolUniqDesc) {
		this.getSymBlock().put(DBConstants.MAPPING_SYMBOL_UNIQ_DESC, mappingSymbolUniqDesc);
	}
	
	public String getPriceNum() {
		return this.getSymBlock().getString(SymbolConstants.PRICE_NUM);
	}

	public void setPriceNum(String sPriceNum) {
		this.getSymBlock().put(SymbolConstants.PRICE_NUM, sPriceNum);
	}

	public String getPriceDen() {
		return this.getSymBlock().getString(SymbolConstants.PRICE_DEN);
	}

	public void setPriceDen(String sPriceDen) {
		this.getSymBlock().put(SymbolConstants.PRICE_DEN, sPriceDen);
	}

	public String getOptionType() {
		return this.getSymBlock().getString(SymbolConstants.OPTION);
	}

}
