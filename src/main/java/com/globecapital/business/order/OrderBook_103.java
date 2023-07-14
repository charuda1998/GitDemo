package com.globecapital.business.order;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.globecapital.api.ft.generics.FTConstants;
import com.globecapital.api.ft.order.GetGTDOrderBookObjectRow;
import com.globecapital.api.ft.order.GetOrderBookObjectRow;
import com.globecapital.api.ft.order.GetTradeBookObjectRow;
import com.globecapital.business.quote.Quote;
import com.globecapital.business.quote.QuoteDetails;
import com.globecapital.config.InfoMessage;
import com.globecapital.constants.DeviceConstants;
import com.globecapital.constants.OrderConstants;
import com.globecapital.constants.SymbolConstants;
import com.globecapital.constants.order.ExchangeSegment;
import com.globecapital.constants.order.OrderAction;
import com.globecapital.constants.order.OrderQty;
import com.globecapital.constants.order.OrderType;
import com.globecapital.constants.order.ProductType;
import com.globecapital.constants.order.Validity;
import com.globecapital.symbology.SymbolMap;
import com.globecapital.symbology.SymbolRow;
import com.globecapital.utils.PriceFormat;
import com.msf.log.Logger;

public class OrderBook_103 {

    private static Logger log = Logger.getLogger(OrderBook_102.class);

    /*** trade book object details ***/
    public static Map<String, JSONObject> mapAvgPriceTradeValue = new HashMap<String, JSONObject>();
    public static Map<String, JSONArray> tradeSummary = new HashMap<String, JSONArray>();

    public static JSONObject getOrderBook(List<GetOrderBookObjectRow> orderRows, List<GetTradeBookObjectRow> tradeRows,
            String sOrderStatus, List<GetGTDOrderBookObjectRow> gtdOrderRow) throws Exception {

        JSONArray orderList = new JSONArray(); // Final Order List
        JSONObject orderBookObj = new JSONObject();
        GTDOrderBook gtdOrderBook = new GTDOrderBook(gtdOrderRow);
        Map<String, Collection<GetGTDOrderBookObjectRow>> gtdOrderBookRow = new HashMap<String, Collection<GetGTDOrderBookObjectRow>>();
        Map<String, Collection<GetGTDOrderBookObjectRow>> pendingGTDOrderBookRow = new HashMap<String, Collection<GetGTDOrderBookObjectRow>>();
        Map<String, Collection<GetGTDOrderBookObjectRow>> executedGTDOrderBookRow = new HashMap<String, Collection<GetGTDOrderBookObjectRow>>();
        /***
         * to get average price, trade value and trade summary from the trade book
         * objects
         ***/
        TradeBook tradeBook = new TradeBook(tradeRows);
        mapAvgPriceTradeValue = tradeBook.getAvgPriceTradedValue();
        tradeSummary = tradeBook.getTradeSummary();

        // Parsed normal order collection
        Collection<SymbolRow> orderCollection = new ArrayList<SymbolRow>();

        // Parsed bracket order map
        Map<String, SymbolRow> mapBracketOrdersParsed = new HashMap<String, SymbolRow>();

        // Parsed Cover Order
        Map<String, SymbolRow> mapCoverOrdersParsed = new HashMap<String, SymbolRow>();

        // To get quote for the list of symbol token
        LinkedHashSet<String> linkedsetSymbolToken = new LinkedHashSet<String>();

        // Contains array of bracket orders based on bracket order ID
        Map<String, JSONArray> mapBracketOrders = new HashMap<String, JSONArray>();

        Map<String, JSONArray> mapCoverOrders = new HashMap<String, JSONArray>();

        // int count=0;
        boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ? true : false);

        for (int i = 0; i < orderRows.size(); i++) {
            try {

                GetOrderBookObjectRow orderRow = orderRows.get(i);

                if (gtdOrderRow == null && orderRow.getIsGTD().equals("1")) {
                    continue;
                }

                if (gtdOrderBookRow.containsKey(orderRow.getGatewayOrdNo())
                        && (sOrderStatus.equalsIgnoreCase(OrderConstants.PENDING) || isAll)) {
                    continue;
                } else if (pendingGTDOrderBookRow.containsKey(orderRow.getGatewayOrdNo())) {
                    continue;
                }

                /*** Temporavary fix as quanity change requires modification in many places */
                String tempMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst());
                SymbolRow tempSymbolRow = SymbolMap.getSymbolRow(orderRow.getScripCode(), tempMktSegID);
                String tempQty = OrderQty.formatToDeviceLot(orderRow.getQty(), tempSymbolRow.getDispLotSizeInt(),
                        tempMktSegID);
                orderRow.setQty(tempQty);
                tempQty = OrderQty.formatToDeviceLot(orderRow.getPendQty(), tempSymbolRow.getDispLotSizeInt(),
                        tempMktSegID);
                orderRow.setPendQty(tempQty);
                tempQty = OrderQty.formatToDeviceLot(orderRow.getDiscQty(), tempSymbolRow.getDispLotSizeInt(),
                        tempMktSegID);
                orderRow.setDiscQty(tempQty);

                /* ***************/

                if (!orderRow.getBracketOrdId().isEmpty() && !orderRow.getBracketOrdId().equals("0")) { // Bracket order
                    // log.info(count++ + " Baracket order");
                    // TODO: Temp fix, need to uncomment when bracket order approved for live
                    /*** If it is main leg, get normal order details also ***/
                    if (orderRow.getLegIndicator().equals(OrderConstants.MAIN_LEG_INDICATOR_9)) {
                        SymbolRow order = getNormalOrderDetails(orderRow, linkedsetSymbolToken, sOrderStatus,
                                orderBookObj, gtdOrderBookRow);

                        if (order.length() > 1)
                            orderCollection.add(order);

                    }

                    if (mapBracketOrders.containsKey(orderRow.getBracketOrdId())) {
                        /*
                         * SymbolRow order = getNormalOrderDetails(orderRow, linkedsetSymbolToken,
                         * sOrderStatus, orderBookObj, gtdOrderBookRow); if (order.length() > 1)
                         * orderCollection.add(order);
                         */
                        mapBracketOrders.get(orderRow.getBracketOrdId()).put(orderRow);
                    } else {

                        /*
                         * SymbolRow order = getNormalOrderDetails(orderRow, linkedsetSymbolToken,
                         * sOrderStatus, orderBookObj, gtdOrderBookRow); if (order.length() > 1)
                         * orderCollection.add(order);
                         */
                        JSONArray arr = new JSONArray();
                        arr.put(orderRow);
                        mapBracketOrders.put(orderRow.getBracketOrdId(), arr);
                    }
                    continue; // bracket orders should be parsed once we done
                              // grouping bracket orders
                } else if (!orderRow.getRecoId().isEmpty()
                        && orderRow.getProdType().equalsIgnoreCase(FTConstants.MARGIN_PLUS)) { // Cover order
                    /*** If it is main leg, get normal order details also ***/
                    if ((orderRow.getLegIndicator().equals("0"))
                            ? orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL_MKT)
                            : orderRow.getLegIndicator().equals(FTConstants.ONE_FLAG)) {
                        SymbolRow order = getNormalOrderDetails(orderRow, linkedsetSymbolToken, sOrderStatus,
                                orderBookObj, gtdOrderBookRow);
                        if (order.length() > 1)
                            orderCollection.add(order);

                    }

                    if (mapCoverOrders.containsKey(orderRow.getRecoId().split(" ")[0])) {
                        mapCoverOrders.get(orderRow.getRecoId().split(" ")[0]).put(orderRow);
                    } else {
                        JSONArray arr = new JSONArray();
                        arr.put(orderRow);
                        mapCoverOrders.put(orderRow.getRecoId().split(" ")[0], arr);
                    }
                    continue; // Cover orders should be parsed once we done
                              // grouping Cover orders
                } else { // Normal order
                    SymbolRow order = getNormalOrderDetails(orderRow, linkedsetSymbolToken, sOrderStatus, orderBookObj,
                            gtdOrderBookRow, gtdOrderBook);
                    if (order.length() > 1)
                        orderCollection.add(order);
                }

            } catch (Exception e) {
                log.warn(e);
            }
        }

        /*** to get details of all the bracket orders ***/
        // TODO: Temp fix, need to uncomment when bracket order approved for live
        mapBracketOrdersParsed = getBracketOrderDetails(mapBracketOrders, linkedsetSymbolToken, sOrderStatus);

        /***
         * to replace main order leg in normal order collections from the parsed bracket
         * order map
         ***/
        Iterator<SymbolRow> iterator = orderCollection.iterator();

        while (iterator.hasNext()) {
            SymbolRow order = iterator.next();
            if (mapBracketOrdersParsed.containsKey(order.getString(OrderConstants.BRACKET_ORDER_ID)))
                orderList.put(mapBracketOrdersParsed.get(order.getString(OrderConstants.BRACKET_ORDER_ID)));
            else if (mapCoverOrdersParsed.containsKey(
                    (order.has(OrderConstants.RECO_ID)) ? order.getString(OrderConstants.RECO_ID) : null)) {
                orderList.put(mapCoverOrdersParsed.get(order.getString(OrderConstants.RECO_ID)));
            } else
                orderList.put(order);
        }

        getQuote(orderList, linkedsetSymbolToken); // To fetch quote for list of
                                                   // symbol token
        orderBookObj.put(OrderConstants.ORDERS, orderList);
        return orderBookObj;
    }

//head start
    /*** Returns parsed bracket order details map, key - bracket order id ***/
    private static Map<String, SymbolRow> getBracketOrderDetails(Map<String, JSONArray> mapBracketOrders,

            /*
             * LinkedHashSet<String> linkedsetSymbolToken, String sOrderStatus, Map<String,
             * JSONArray> mapExeBracketOrders, Map<String, JSONArray>
             * mapSqaureOffBracketOrders, JSONObject orderBook)
             * throws Exception {
             *
             * boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ?
             * true : false);
             * Map<String, SymbolRow> orderMap = new HashMap<String, SymbolRow>();
             * String isSquareOff = "false";
             * for (Map.Entry<String, JSONArray> entry : mapBracketOrders.entrySet()) {
             *
             * SymbolRow order = new SymbolRow();
             * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow)
             * entry.getValue().get(0);
             *
             *//*** To fetch quote details and symbol object ***/
            /*
             * String sScripCode = orderRow.getScripCode();
             * String sMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(),
             * orderRow.getInst());
             * String sTokenMktSegID = sScripCode + "_" + sMktSegID;
             * order.extend(SymbolMap.getSymbolRow(sScripCode,
             * sMktSegID).getMinimisedSymbolRow());
             *
             * order.put(OrderConstants.BRACKET_ORDER_DETAILS,
             * getBracketOrderDetails(entry.getValue(), order, mapExeBracketOrders,
             * mapSqaureOffBracketOrders, orderBook));
             * order.put(OrderConstants.IS_BRACKET_ORDER, "true");
             *
             * order.put(OrderConstants.IS_BRACKET_MAIN_ORDER, "true");
             * order.put(OrderConstants.IS_BRACKET_STOPLOSS_ORDER, "false");
             * order.put(OrderConstants.IS_BRACKET_SQUAREOFF_ORDER, "false");
             * order.put(OrderConstants.IS_BRACKET_PROFIT_ORDER, "false");
             *//*** To send orders based on order status ***/
            /*
             * String sStatus =
             * OrderStatus.getStatus(order.getString(OrderConstants.STATUS));
             * if (isAll) {
             * linkedsetSymbolToken.add(sTokenMktSegID);
             * orderMap.put(order.getString(OrderConstants.BRACKET_ORDER_ID), order);
             * } else if (sStatus.equals(sOrderStatus)) {
             * linkedsetSymbolToken.add(sTokenMktSegID);
             * orderMap.put(order.getString(OrderConstants.BRACKET_ORDER_ID), order);
             * }
             * }
             * return orderMap;
             *
             * }
             *
             * private static Map<String, SymbolRow>
             * getBracketOrderSquareOffDetails(Map<String, JSONArray> mapBracketOrders,
             * LinkedHashSet<String> linkedsetSymbolToken, String sOrderStatus) throws
             * Exception {
             *
             * boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ?
             * true : false);
             * Map<String, SymbolRow> orderMap = new HashMap<String, SymbolRow>();
             * for (Map.Entry<String, JSONArray> entry : mapBracketOrders.entrySet()) {
             *
             * SymbolRow order = new SymbolRow();
             * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow)
             * entry.getValue().get(0);
             *
             *//*** To fetch quote details and symbol object ***/
            /*
             * String sScripCode = orderRow.getScripCode();
             * String sMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(),
             * orderRow.getInst());
             * String sTokenMktSegID = sScripCode + "_" + sMktSegID;
             * order.extend(SymbolMap.getSymbolRow(sScripCode,
             * sMktSegID).getMinimisedSymbolRow());
             * order.put(OrderConstants.BRACKET_ORDER_DETAILS,
             * getBracketSquareOffOrderDetails(entry.getValue(), order));
             * order.put(OrderConstants.IS_BRACKET_ORDER, "true");
             *
             * order.put(OrderConstants.IS_BRACKET_MAIN_ORDER, "false");
             * order.put(OrderConstants.IS_BRACKET_STOPLOSS_ORDER, "false");
             * order.put(OrderConstants.IS_BRACKET_SQUAREOFF_ORDER, "true");
             * order.put(OrderConstants.IS_BRACKET_PROFIT_ORDER, "false");
             *
             *//*** To send orders based on order status ***/
            /*
             * String sStatus =
             * OrderStatus.getStatus(order.getString(OrderConstants.STATUS));
             * if (isAll) {
             * linkedsetSymbolToken.add(sTokenMktSegID);
             * orderMap.put(order.getString(OrderConstants.BRACKET_ORDER_ID), order);
             * } else if (sStatus.equals(sOrderStatus)) {
             * linkedsetSymbolToken.add(sTokenMktSegID);
             * orderMap.put(order.getString(OrderConstants.BRACKET_ORDER_ID), order);
             * }
             * }
             * return orderMap;
             *
             *
             * }
             *
             *//*** Returns parsed bracket order details map, key - bracket order id ***/
            /*
             * private static Map<String, SymbolRow> getBracketOrderSLDetails(Map<String,
             * JSONArray> mapBracketOrders,
             * LinkedHashSet<String> linkedsetSymbolToken, String sOrderStatus) throws
             * Exception {
             *
             * boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ?
             * true : false);
             * Map<String, SymbolRow> orderMap = new HashMap<String, SymbolRow>();
             *
             * for (Map.Entry<String, JSONArray> entry : mapBracketOrders.entrySet()) {
             *
             * SymbolRow order = new SymbolRow();
             * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow)
             * entry.getValue().get(0);
             *
             *//*** To fetch quote details and symbol object ***/
            /*
             * String sScripCode = orderRow.getScripCode();
             * String sMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(),
             * orderRow.getInst());
             * String sTokenMktSegID = sScripCode + "_" + sMktSegID;
             * order.extend(SymbolMap.getSymbolRow(sScripCode,
             * sMktSegID).getMinimisedSymbolRow());
             * order.put(OrderConstants.BRACKET_ORDER_DETAILS,
             * getBracketSLOrderDetails(entry.getValue(), order));
             * order.put(OrderConstants.IS_BRACKET_ORDER, "true");
             * order.put(OrderConstants.IS_BRACKET_MAIN_ORDER, "false");
             * order.put(OrderConstants.IS_BRACKET_STOPLOSS_ORDER, "true");
             * order.put(OrderConstants.IS_BRACKET_SQUAREOFF_ORDER, "false");
             * order.put(OrderConstants.IS_BRACKET_PROFIT_ORDER, "false");
             *
             *//*** To send orders based on order status ***//*
                                                              * String sStatus =
                                                              * OrderStatus.getStatus(order.getString(OrderConstants.
                                                              * STATUS));
                                                              * if (isAll) {
                                                              * linkedsetSymbolToken.add(sTokenMktSegID);
                                                              * orderMap.put(order.getString(OrderConstants.
                                                              * BRACKET_ORDER_ID), order);
                                                              * } else if (sStatus.equals(sOrderStatus)) {
                                                              * linkedsetSymbolToken.add(sTokenMktSegID);
                                                              * orderMap.put(order.getString(OrderConstants.
                                                              * BRACKET_ORDER_ID), order);
                                                              * }
                                                              * }
                                                              * return orderMap;
                                                              *
                                                              * }
                                                              *
                                                              * private static Map<String, SymbolRow>
                                                              * getBracketOrderPDetails(Map<String, JSONArray>
                                                              * mapBracketOrders,
                                                              * >>>>>>> 0c78b562 (Separated cancelled orders from
                                                              * Executed tab in Orderbook)
                                                              */
            LinkedHashSet<String> linkedsetSymbolToken, String sOrderStatus) throws Exception {

        boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ? true : false);
        Map<String, SymbolRow> orderMap = new HashMap<String, SymbolRow>();

        for (Map.Entry<String, JSONArray> entry : mapBracketOrders.entrySet()) {

            SymbolRow order = new SymbolRow();
            GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow) entry.getValue().get(0);

            /*** To fetch quote details and symbol object ***/
            String sScripCode = orderRow.getScripCode();
            String sMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst());
            String sTokenMktSegID = sScripCode + "_" + sMktSegID;
            order.extend(SymbolMap.getSymbolRow(sScripCode, sMktSegID).getMinimisedSymbolRow());

            order.put(OrderConstants.BRACKET_ORDER_DETAILS, getBracketOrderDetails(entry.getValue(), order));
            order.put(OrderConstants.IS_BRACKET_ORDER, "true");

            /*** To send orders based on order status ***/
            String sStatus = OrderStatus.getStatus(order.getString(OrderConstants.STATUS));
            if (isAll) {
                linkedsetSymbolToken.add(sTokenMktSegID);
                orderMap.put(order.getString(OrderConstants.BRACKET_ORDER_ID), order);
            } else if (sStatus.equals(sOrderStatus)) {
                linkedsetSymbolToken.add(sTokenMktSegID);
                orderMap.put(order.getString(OrderConstants.BRACKET_ORDER_ID), order);
            }
        }
        return orderMap;

    }

    /***
     * To get normal order detail
     *
     * @param orderBookObj
     ***/
    private static SymbolRow getNormalOrderDetails(GetOrderBookObjectRow orderRow,
            LinkedHashSet<String> linkedsetSymbolToken, String sOrderStatus, JSONObject orderBookObj,
            Map<String, Collection<GetGTDOrderBookObjectRow>> gtdOrderBookRow) throws Exception {

        SymbolRow order = new SymbolRow();
        /*** To fetch quote details and symbol object ***/
        String sScripCode = orderRow.getScripCode();
        String sMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst());
        String sTokenMktSegID = sScripCode + "_" + sMktSegID;
        order.extend(SymbolMap.getSymbolRow(sScripCode, sMktSegID).getMinimisedSymbolRow());
        getOrderDetails(orderRow, order); // To fetch all the common fields for
                                          // the order
        /*** To display status based on the order status ***/
        String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());
        order.put(OrderConstants.STATUS, sStatus);
        order.put(OrderConstants.DISP_STATUS, OrderStatus.getDispOrderStatus(orderRow.getOrdStat()));
        order.put(OrderConstants.IS_BRACKET_ORDER, "false");
        boolean isGTD = (orderRow.getIsGTD().equals("1") || gtdOrderBookRow.containsKey(orderRow.getGatewayOrdNo()));
        if (isGTD) {// To Identify GTD Order
            order.put(OrderConstants.IS_GTD_ORDER, "true");
            if (gtdOrderBookRow.containsKey(orderRow.getGatewayOrdNo())) {
                GetGTDOrderBookObjectRow gtdDetails = gtdOrderBookRow.get(orderRow.getGatewayOrdNo()).iterator().next();
                order.put(OrderConstants.VALIDITY, OrderConstants.GTD);
                order.put(OrderConstants.EXPIRY_DATE, gtdDetails.getGTDDate());
                order.put(OrderConstants.STATUS, OrderStatus.getGTDStatus(gtdDetails.getGTDOrderStatus()));
                order.put(OrderConstants.DISP_STATUS,
                        OrderStatus.getDispGTDOrderStatus(gtdDetails.getGTDOrderStatus()));
            }
        } else
            order.put(OrderConstants.IS_GTD_ORDER, "false");

        /*** Fetch details from the parsed trade book response ***/
        JSONObject objAvgTradeValue = getAvgTradeValue(orderRow);
        order.put(OrderConstants.AVG_PRICE, objAvgTradeValue.getString(OrderConstants.AVG_PRICE));
        order.put(OrderConstants.TRADED_VALUE,
                getOrderTradeValue(objAvgTradeValue.getString(OrderConstants.TRADED_VALUE),
                        SymbolMap.getSymbolRow(order.getSymbolToken())));
        order.put(OrderConstants.TRADE_SUMMARY, getTradeSummary(orderRow));
        if (!isGTD) {
            if (sStatus.equalsIgnoreCase(OrderConstants.PENDING)) {
                orderBookObj.put(DeviceConstants.PENDING_COUNT,
                        orderBookObj.has(DeviceConstants.PENDING_COUNT)
                                ? String.valueOf(
                                        Integer.parseInt(orderBookObj.getString(DeviceConstants.PENDING_COUNT)) + 1)
                                : "1");
            } else if (sStatus.equalsIgnoreCase(OrderConstants.EXECUTED)) {
                orderBookObj.put(DeviceConstants.EXECUTED_COUNT,
                        orderBookObj.has(DeviceConstants.EXECUTED_COUNT)
                                ? String.valueOf(
                                        Integer.parseInt(orderBookObj.getString(DeviceConstants.EXECUTED_COUNT)) + 1)
                                : "1");
            } else if (sStatus.equalsIgnoreCase(OrderConstants.CANCELLED)) {
                orderBookObj.put(DeviceConstants.CANCELLED_COUNT,
                        orderBookObj.has(DeviceConstants.CANCELLED_COUNT)
                                ? String.valueOf(
                                        Integer.parseInt(orderBookObj.getString(DeviceConstants.CANCELLED_COUNT)) + 1)
                                : "1");
            }
        }
        /*** To set, modifiable and cancellable flags ***/
        order.put(OrderConstants.IS_MODIFIABLE, Boolean.toString(OrderStatus.isModifiable(sStatus)));
        order.put(OrderConstants.IS_CANCELLABLE, Boolean.toString(OrderStatus.isCancellable(sStatus)));

        OrderStatus.getBuyOrSellMoreFlags(sStatus, orderRow.getBuySell(), order);

        /*** If status is cancelled, to fetch reason ***/
        order.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sStatus, orderRow.getError()));

        /*** To send orders based on order status ***/
        boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ? true : false);
        if (isAll) {
            linkedsetSymbolToken.add(sTokenMktSegID);
            return order;
        } else {

            /*
             * if ((sStatus.equalsIgnoreCase(OrderConstants.CANCELLED)
             * && sOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED))
             * || (sStatus.equalsIgnoreCase(OrderConstants.EXECUTED)
             * && sOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED))) {
             * linkedsetSymbolToken.add(sTokenMktSegID);
             * return order;
             * } else if (sStatus.equals(sOrderStatus) || (isGTD &&
             * sOrderStatus.equalsIgnoreCase(OrderConstants.GTC))) {
             * linkedsetSymbolToken.add(sTokenMktSegID);
             * return order;
             * } else
             */
            if (sStatus.equals(sOrderStatus) || (isGTD && sOrderStatus.equalsIgnoreCase(OrderConstants.GTC))) {
                linkedsetSymbolToken.add(sTokenMktSegID);
                return order;
            }
        }

        return new SymbolRow();
    }
    
    private static SymbolRow getNormalOrderDetails(GetOrderBookObjectRow orderRow,
            LinkedHashSet<String> linkedsetSymbolToken, String sOrderStatus, JSONObject orderBookObj,
            Map<String, Collection<GetGTDOrderBookObjectRow>> gtdOrderBookRow, GTDOrderBook gtdOrderBook) throws Exception {

        SymbolRow order = new SymbolRow();
        /*** To fetch quote details and symbol object ***/
        String sScripCode = orderRow.getScripCode();
        String sMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst());
        String sTokenMktSegID = sScripCode + "_" + sMktSegID;
        order.extend(SymbolMap.getSymbolRow(sScripCode, sMktSegID).getMinimisedSymbolRow());
        getOrderDetails(orderRow, order); // To fetch all the common fields for
                                          // the order
        /*** To display status based on the order status ***/
        String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());
        order.put(OrderConstants.STATUS, sStatus);
        order.put(OrderConstants.DISP_STATUS, OrderStatus.getDispOrderStatus(orderRow.getOrdStat()));
        order.put(OrderConstants.IS_BRACKET_ORDER, "false");
        boolean isGTD = (orderRow.getIsGTD().equals("1") || gtdOrderBookRow.containsKey(orderRow.getGatewayOrdNo())
                || gtdOrderBook.getGTDCancelledSummary().containsKey(orderRow.getGatewayOrdNo()));
        if (isGTD) {// To Identify GTD Order
            order.put(OrderConstants.IS_GTD_ORDER, "true");
            if (gtdOrderBookRow.containsKey(orderRow.getGatewayOrdNo())) {
                GetGTDOrderBookObjectRow gtdDetails = gtdOrderBookRow.get(orderRow.getGatewayOrdNo()).iterator().next();
                order.put(OrderConstants.VALIDITY, OrderConstants.GTD);
                order.put(OrderConstants.EXPIRY_DATE, gtdDetails.getGTDDate());
                order.put(OrderConstants.STATUS, OrderStatus.getGTDStatus(gtdDetails.getGTDOrderStatus()));
                order.put(OrderConstants.DISP_STATUS,
                        OrderStatus.getDispGTDOrderStatus(gtdDetails.getGTDOrderStatus()));
            }
        } else
            order.put(OrderConstants.IS_GTD_ORDER, "false");

        /*** Fetch details from the parsed trade book response ***/
        JSONObject objAvgTradeValue = getAvgTradeValue(orderRow);
        order.put(OrderConstants.AVG_PRICE, objAvgTradeValue.getString(OrderConstants.AVG_PRICE));
        order.put(OrderConstants.TRADED_VALUE,
                getOrderTradeValue(objAvgTradeValue.getString(OrderConstants.TRADED_VALUE),
                        SymbolMap.getSymbolRow(order.getSymbolToken())));
        order.put(OrderConstants.TRADE_SUMMARY, getTradeSummary(orderRow));
        if (!isGTD) {
            if (sStatus.equalsIgnoreCase(OrderConstants.PENDING)) {
                orderBookObj.put(DeviceConstants.PENDING_COUNT,
                        orderBookObj.has(DeviceConstants.PENDING_COUNT)
                                ? String.valueOf(
                                        Integer.parseInt(orderBookObj.getString(DeviceConstants.PENDING_COUNT)) + 1)
                                : "1");
            } else if (sStatus.equalsIgnoreCase(OrderConstants.EXECUTED)) {
                orderBookObj.put(DeviceConstants.EXECUTED_COUNT,
                        orderBookObj.has(DeviceConstants.EXECUTED_COUNT)
                                ? String.valueOf(
                                        Integer.parseInt(orderBookObj.getString(DeviceConstants.EXECUTED_COUNT)) + 1)
                                : "1");
            } else if (sStatus.equalsIgnoreCase(OrderConstants.CANCELLED)) {
                orderBookObj.put(DeviceConstants.CANCELLED_COUNT,
                        orderBookObj.has(DeviceConstants.CANCELLED_COUNT)
                                ? String.valueOf(
                                        Integer.parseInt(orderBookObj.getString(DeviceConstants.CANCELLED_COUNT)) + 1)
                                : "1");
            }
        }
        /*** To set, modifiable and cancellable flags ***/
        order.put(OrderConstants.IS_MODIFIABLE, Boolean.toString(OrderStatus.isModifiable(sStatus)));
        order.put(OrderConstants.IS_CANCELLABLE, Boolean.toString(OrderStatus.isCancellable(sStatus)));

        OrderStatus.getBuyOrSellMoreFlags(sStatus, orderRow.getBuySell(), order);

        /*** If status is cancelled, to fetch reason ***/
        order.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sStatus, orderRow.getError()));

        /*** To send orders based on order status ***/
        boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ? true : false);
        if (isAll) {
            linkedsetSymbolToken.add(sTokenMktSegID);
            return order;
        } else {

            /*
             * if ((sStatus.equalsIgnoreCase(OrderConstants.CANCELLED)
             * && sOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED))
             * || (sStatus.equalsIgnoreCase(OrderConstants.EXECUTED)
             * && sOrderStatus.equalsIgnoreCase(OrderConstants.EXECUTED))) {
             * linkedsetSymbolToken.add(sTokenMktSegID);
             * return order;
             * } else if (sStatus.equals(sOrderStatus) || (isGTD &&
             * sOrderStatus.equalsIgnoreCase(OrderConstants.GTC))) {
             * linkedsetSymbolToken.add(sTokenMktSegID);
             * return order;
             * } else
             */
            if (sStatus.equals(sOrderStatus) || (isGTD && sOrderStatus.equalsIgnoreCase(OrderConstants.GTC))) {
                linkedsetSymbolToken.add(sTokenMktSegID);
                return order;
            }
        }

        return new SymbolRow();
    }

    /***
     * To fetch average price and trade value from the parsed trade book map
     ***/
    private static JSONObject getAvgTradeValue(GetOrderBookObjectRow orderRow) {

        JSONObject objAvgTradeValue = new JSONObject();
        if (mapAvgPriceTradeValue.containsKey(orderRow.getExOrderNo())) {
            JSONObject avgPriceAndTradeValueObj = mapAvgPriceTradeValue.get(orderRow.getExOrderNo());
            objAvgTradeValue.put(OrderConstants.AVG_PRICE,
                    avgPriceAndTradeValueObj.getString(OrderConstants.AVG_PRICE));
            objAvgTradeValue.put(OrderConstants.TRADED_VALUE,
                    avgPriceAndTradeValueObj.getString(OrderConstants.TRADED_VALUE));
        } else {
            objAvgTradeValue.put(OrderConstants.AVG_PRICE, "0.00");
            objAvgTradeValue.put(OrderConstants.TRADED_VALUE, "0.00");
        }

        return objAvgTradeValue;
    }

    /*** To fetch trade summary from the parsed trade book map ***/
    public static JSONArray getTradeSummary(GetOrderBookObjectRow orderRow) throws JSONException, Exception {
        JSONArray tradeSummaryArr = new JSONArray();

        if (tradeSummary.containsKey(orderRow.getExOrderNo())) {
            tradeSummaryArr = tradeSummary.get(orderRow.getExOrderNo());
            JSONObject tradeSummaryObjFirst = new JSONObject();
            tradeSummaryObjFirst.put(OrderConstants.SHOW_ID, "false");
            tradeSummaryObjFirst.put(OrderConstants.TRADE_ORD_ID, "");
            tradeSummaryObjFirst.put(OrderConstants.ORDER_TIME,
                    tradeSummaryArr.getJSONObject(tradeSummaryArr.length() - 1).getString(OrderConstants.ORDER_TIME));

            if (orderRow.getOrdStat().equalsIgnoreCase(OrderConstants.PENDING))
                tradeSummaryObjFirst.put(OrderConstants.DETAIL, String.format(
                        InfoMessage.getInfoMSG("info_msg.trade_summary_detail_first_partial"),
                        Integer.toString(Integer.parseInt(orderRow.getQty()) - Integer.parseInt(orderRow.getPendQty())),
                        mapAvgPriceTradeValue.get(orderRow.getExOrderNo()).getString(OrderConstants.AVG_PRICE)));
            else
                tradeSummaryObjFirst.put(OrderConstants.DETAIL, String.format(
                        InfoMessage.getInfoMSG("info_msg.trade_summary_detail_first"), orderRow.getQty(),
                        mapAvgPriceTradeValue.get(orderRow.getExOrderNo()).getString(OrderConstants.AVG_PRICE)));

            tradeSummaryArr.put(0, tradeSummaryObjFirst);

            JSONObject tradeSummaryObjLast = new JSONObject();
            tradeSummaryObjLast.put(OrderConstants.SHOW_ID, "true");
            tradeSummaryObjLast.put(OrderConstants.TRADE_ORD_ID, "Order ID: " + orderRow.getClientOrdNo());
            tradeSummaryObjLast.put(OrderConstants.ORDER_TIME, orderRow.getTime());
            tradeSummaryObjLast.put(OrderConstants.DETAIL,
                    String.format(InfoMessage.getInfoMSG("info_msg.trade_summary_detail_last"),
                            OrderType.formatToDeviceDisplay2(orderRow.getOrderType()),
                            OrderAction.formatToDevice2(orderRow.getBuySell()), orderRow.getQty(), orderRow.getPrc()));

            tradeSummaryArr.put(tradeSummaryObjLast);
        }

        return tradeSummaryArr;

    }

    /*** To fetch all the common fields for the order ***/
    private static void getOrderDetails(GetOrderBookObjectRow orderRow, SymbolRow order) throws Exception {

        String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());
        if (sStatus.equals(OrderConstants.EXECUTED) && orderRow.getPendQty().equals("0"))
            order.put(OrderConstants.PENDING_QTY, orderRow.getQty());
        else
            order.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());

        order.put(OrderConstants.ORDER_QTY, orderRow.getQty());
        order.put(OrderConstants.TRADE_QTY,
                Long.toString(Long.parseLong(orderRow.getQty()) - Long.parseLong(orderRow.getPendQty())));
        order.put(OrderConstants.PRICE, PriceFormat.formatPrice(orderRow.getPrc(), order.getPrecisionInt(), false));

        if (order.getExchange().equalsIgnoreCase(ExchangeSegment.NSECDS))
            order.put(OrderConstants.ORDER_VALUE,
                    getOrderTradeValue(
                            PriceFormat.formatPrice(
                                    String.valueOf((Double.parseDouble(orderRow.getPrc())
                                            * Long.parseLong(orderRow.getQty()) * order.getLotSizeInt())),
                                    Integer.parseInt(OrderConstants.PRECISION_2), false),
                            SymbolMap.getSymbolRow(order.getSymbolToken())));
        else
            order.put(OrderConstants.ORDER_VALUE, getOrderTradeValue(
                    PriceFormat.formatPrice(
                            String.valueOf((Double.parseDouble(orderRow.getPrc()) * Long.parseLong(orderRow.getQty()))),
                            Integer.parseInt(OrderConstants.PRECISION_2), false),
                    SymbolMap.getSymbolRow(order.getSymbolToken())));

        order.put(OrderConstants.ORDER_TIME, orderRow.getOrderTime());
        order.put(OrderConstants.TRIG_PRICE, orderRow.getTrigPrc());
        order.put(OrderConstants.EXCH_ORDER_TIME, orderRow.getTime());
        if (orderRow.getTime().isEmpty())
            order.put(OrderConstants.EXCH_ORDER_DATE, "");
        else
            order.put(OrderConstants.EXCH_ORDER_DATE, orderRow.getOrderDate());
        order.put(OrderConstants.ORDER_ID, orderRow.getClientOrdNo());
        if (!orderRow.getBracketOrdId().isEmpty()) {
            JSONObject bracketPrdType = new JSONObject();
            bracketPrdType.put(DeviceConstants.PRODUCT_TYPE, ProductType.formatToDisplay(orderRow.getProdType(),
                    ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst())));
            bracketPrdType.put(DeviceConstants.DISP_PRODUCT_TYPE, OrderConstants.INTRADAY);
            order.put(OrderConstants.PRODUCT_TYPE, bracketPrdType);
        } else if (ProductType.formatToDisplay(orderRow.getProdType(),
                ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst())).equals(ProductType.MTF)) {
            JSONObject mtfObj = new JSONObject();
            mtfObj.put(DeviceConstants.PRODUCT_TYPE, OrderConstants.MTF);
            mtfObj.put(DeviceConstants.DISP_PRODUCT_TYPE, OrderConstants.BUY_NOW_PAYLATER);
            order.put(OrderConstants.PRODUCT_TYPE, mtfObj);
        } else
            order.put(OrderConstants.PRODUCT_TYPE, ProductType.formatToDisplay(orderRow.getProdType(),
                    ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst())));
        order.put(OrderConstants.ORDER_ACTION, OrderAction.formatToDevice(orderRow.getBuySell()));
        order.put(OrderConstants.GATEWAY_ORD_NO, orderRow.getGatewayOrdNo());
        order.put(OrderConstants.CLIENT_ORD_NO, orderRow.getClientOdrNo());
        order.put(OrderConstants.ORDER_TYPE, orderRow.getOrderType());

        if (orderRow.getValidity().equals(""))// To handle empty validity in Order Error Case
            order.put(OrderConstants.VALIDITY, "");
        else
            order.put(OrderConstants.VALIDITY, Validity.formatToDevice(orderRow.getValidity()));

        if (orderRow.getBracketOrdId().isEmpty())
            order.put(OrderConstants.DISC_QTY, orderRow.getDiscQty());
        else
            order.put(OrderConstants.DISC_QTY, "0");

        order.put(OrderConstants.AMO_DETAILS, "");
        order.put(OrderConstants.DISP_ORDER_TYPE, OrderType.formatToDeviceDisplay(orderRow.getOrderType()));
        order.put(OrderConstants.DISP_ORDER_TIME, orderRow.getTime());

        if (orderRow.getExOrderNo().isEmpty())
            order.put(OrderConstants.EXCH_ORD_NO, "--");
        else
            order.put(OrderConstants.EXCH_ORD_NO, orderRow.getExOrderNo());

        if (!orderRow.getRecoId().isEmpty() && orderRow.getProdType().equalsIgnoreCase(FTConstants.MARGIN_PLUS))
            order.put(OrderConstants.RECO_ID, orderRow.getRecoId().split(" ")[0]);

        order.put(OrderConstants.BRACKET_ORDER_ID, orderRow.getBracketOrdId());
        order.put(OrderConstants.IS_AMO, Boolean.toString(isAMOOrder(orderRow.getProdType())));
    }

    static String getOrderTradeValue(String formatPrice, SymbolRow order) {

        if (order.getExchange().equals(ExchangeSegment.MCX) || order.getExchange().equals(ExchangeSegment.NSECDS))
            return PriceFormat.formatPrice(
                    String.valueOf(Float.parseFloat(formatPrice.replaceAll(",", ""))
                            * (Float.parseFloat(order.getPriceNum()) / Float.parseFloat(order.getPriceDen()))),
                    order.getPrecisionInt(), false);
        else
            return formatPrice;
    }

    private static boolean isAMOOrder(String sProductType) {
        if (sProductType.equalsIgnoreCase(ProductType.FT_AMO_DELIVERY_FULL_TEXT)
                || sProductType.equalsIgnoreCase(ProductType.FT_AMO_MARGIN_FULL_TEXT)
                || sProductType.equalsIgnoreCase(ProductType.FT_AMO_CARRYFORWARD_FULL_TEXT)
                || sProductType.equalsIgnoreCase(ProductType.FT_AMO_INTRADAY_FULL_TEXT))
            return true;
        else
            return false;
    }

    /*** Parse group of bracket orders into single order ***/
    private static JSONObject getBracketOrderDetails(JSONArray brackerOrders, SymbolRow order)
            throws JSONException, Exception {

        JSONObject bracketOrdObj = new JSONObject(); // Final Object contains bracket order details
        JSONObject legOrderObj = new JSONObject(); // Leg order short detail
        JSONArray bracketOrderArr = new JSONArray(); // Array of pending and executed order details
        JSONArray cancelledLegOrderArr = new JSONArray(); // Array of cancelled order details
        JSONObject modifyOrderDetailsObj = new JSONObject(); // To be forwarded while sending bracket order modify
                                                             // request

        String sMainOrderStatus = "", sSLOrderStatus = "", sProfitOrderStatus = "";
        String sSquareoffStatus = "";
        String sExch = "", sInst = "";

        String sBuyOrSell = "";

        for (int i = 0; i < brackerOrders.length(); i++) {

            GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow) brackerOrders.get(i);

            if (orderRow.getLegIndicator().equals(OrderConstants.MAIN_LEG_INDICATOR_9)) {

                /***
                 * To fetch all the common fields for the order, main order leg is same as
                 * normal order
                 ***/
                getOrderDetails(orderRow, order);
                sExch = orderRow.getExch();
                sInst = orderRow.getInst();

                /*** To fetch main order details ***/
                bracketOrderArr.put(getMainOrderDetails(orderRow, order));

                // TODO: API issue, temporary fix at line 529
                /*
                 * if (orderRow.getPosConvFlag().equals("1")) {
                 *
                 * order.put(OrderConstants.CONVERT_OPTION, "true");
                 * modifyOrderDetailsObj.put(DeviceConstants.CONVERTABLE_TYPES,
                 * ProductType.getConvertableProductTypes(ProductType.
                 * FT_BRACKET_ORDER_FULL_TEXT,
                 * ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst())));
                 * } else order.put(OrderConstants.CONVERT_OPTION, "false");
                 */

                /*** To display status based on the main leg order status ***/
                sMainOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
                sBuyOrSell = orderRow.getBuySell();
                order.put(OrderConstants.STATUS, sMainOrderStatus);
                if (sMainOrderStatus.equals(OrderConstants.EXECUTED)) {
                    modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
                    order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_EXECUTED);
                } else if (sMainOrderStatus.equals(OrderConstants.CANCELLED)) {
                    order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);

                    bracketOrderArr.put(getSLOrderDetails(orderRow, true, order));
                    bracketOrderArr.put(getProfitOrderDetails(orderRow, true, order));

                    legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
                    legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
                    legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
                    legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
                    legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
                    legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
                    legOrderObj.put(OrderConstants.ORDER_ACTION, getLegOrderAction(orderRow.getBuySell()));
                } else if (sMainOrderStatus.equals(OrderConstants.PENDING)) { // If main leg order is pending, still
                                                                              // order is not pushed to the exchange
                    order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);

                    /*** flag is to identify main order is pending or not ***/
                    bracketOrderArr.put(getSLOrderDetails(orderRow, true, order));
                    bracketOrderArr.put(getProfitOrderDetails(orderRow, true, order));

                    /*** Leg order short description ***/
                    legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
                    legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
                    legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
                    legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
                    legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
                    legOrderObj.put(OrderConstants.IS_EXECUTED, "false");

                    /* If main order action is buy,then leg order action is sell **/
                    legOrderObj.put(OrderConstants.ORDER_ACTION, getLegOrderAction(orderRow.getBuySell()));

                }

                getOrderModifyFlags(orderRow.getBracketOrdModifyBit(), modifyOrderDetailsObj);
                order.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sMainOrderStatus, orderRow.getError()));

                /*** Details needed for bracket order modification ***/
                modifyOrderDetailsObj.put(OrderConstants.MAIN_ORDER_MODIFY, getModifyOrderDetails(orderRow, false));
                modifyOrderDetailsObj.put(OrderConstants.BRACKET_ORDER_ID, orderRow.getBracketOrdId());
                modifyOrderDetailsObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
                modifyOrderDetailsObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
                modifyOrderDetailsObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
                modifyOrderDetailsObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
                modifyOrderDetailsObj.put(OrderConstants.PRICE, orderRow.getPrc());
                modifyOrderDetailsObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
                modifyOrderDetailsObj.put(OrderConstants.PENDING_QTY, orderRow.getQty());

            } else if (orderRow.getLegIndicator().equals(OrderConstants.SL_LEG_INDICATOR_10)) {

                /***
                 * To fetch Stop loss order details, flag is to identify main order is pending
                 * or not
                 ***/
                JSONObject slOrderObj = getSLOrderDetails(orderRow, false, order);

                /*** Leg order short description ***/
                legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
                legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
                legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
                legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
                legOrderObj.put(OrderConstants.ORDER_ACTION, OrderAction.formatToDevice(orderRow.getBuySell()));

                sSLOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
                if (sSLOrderStatus.equals(OrderConstants.EXECUTED)) {
                    modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
                    legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_SL_ORDER);
                    legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
                    legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
                    legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());

                }

                if (sSLOrderStatus.equals(OrderConstants.CANCELLED)) {
                    modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
                    cancelledLegOrderArr.put(slOrderObj);
                } else
                    bracketOrderArr.put(slOrderObj);

                if (sSLOrderStatus.equals(OrderConstants.PENDING)) {
                    modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
                    modifyOrderDetailsObj.put(OrderConstants.SL_ORDER_MODIFY, getModifyOrderDetails(orderRow, false));
                    modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE,
                            Boolean.toString(getIsTrailingSLModifiable(orderRow.getLtpJumpPrc())));
                }

            } else if (orderRow.getLegIndicator().equals(OrderConstants.PROFIT_LEG_INDICATOR_11)) {

                /***
                 * To fetch target order details, flag is to identify main order is pending or
                 * not
                 ***/
                JSONObject targetOrderObj = getProfitOrderDetails(orderRow, false, order);

                sProfitOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
                if (sProfitOrderStatus.equals(OrderConstants.EXECUTED)) {
                    modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "false");
                    legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_PROFIT_ORDER);
                    legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
                    legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
                    legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
                    legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
                }

                if (sProfitOrderStatus.equals(OrderConstants.CANCELLED)) {
                    modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "false");
                    cancelledLegOrderArr.put(targetOrderObj);
                } else
                    bracketOrderArr.put(targetOrderObj);

                if (sProfitOrderStatus.equals(OrderConstants.PENDING)) {
                    modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "true");
                    modifyOrderDetailsObj.put(OrderConstants.TARGET_ORDER_MODIFY,
                            getModifyOrderDetails(orderRow, false));
                }
            } else // If leg indicator is not any of the above case, then it is square off order
            {
                sSquareoffStatus = OrderStatus.getStatus(orderRow.getOrdStat());
                if (sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
                    legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_SQUAREOFF_ORDER);
                    legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
                    legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
                    legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
                }

                if (sSquareoffStatus.equals(OrderConstants.CANCELLED)) {
                    cancelledLegOrderArr.put(getSquareoffOrderDetails(orderRow, order));
                } else
                    bracketOrderArr.put(getSquareoffOrderDetails(orderRow, order));

                // bracketOrderArr.put(getSquareoffOrderDetails(orderRow));

            }

        } // End of for loop

        /***
         * If any one of the orders is pending, then user can modify or cancel the order
         ***/
        if (sMainOrderStatus.equals(OrderConstants.PENDING) || sSLOrderStatus.equals(OrderConstants.PENDING)
                || sProfitOrderStatus.equals(OrderConstants.PENDING))
            // modify object is needed only when user can modify or cancel the order
            bracketOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, modifyOrderDetailsObj);
        else
            bracketOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, new JSONObject());

        order.put(OrderConstants.IS_MODIFIABLE, Boolean
                .toString(OrderStatus.isBracketOrderModifiable(sMainOrderStatus, sSLOrderStatus, sProfitOrderStatus)));
        order.put(OrderConstants.IS_CANCELLABLE, Boolean
                .toString(OrderStatus.isBracketOrderCancellable(sMainOrderStatus, sSLOrderStatus, sProfitOrderStatus)));
        order.put(OrderConstants.EXIT_OPTION, Boolean
                .toString(OrderStatus.getBracketOrderExitOption(sMainOrderStatus, sSLOrderStatus, sProfitOrderStatus)));

        // Temporary fix
        order.put(OrderConstants.CONVERT_OPTION, "false");

        /*** To show, leg order short description status ***/
        if (sSLOrderStatus.equals(OrderConstants.EXECUTED) || sProfitOrderStatus.equals(OrderConstants.EXECUTED)
                || sSquareoffStatus.equals(OrderConstants.EXECUTED)) {

            /*** For executed order, to show only price and qty ***/
            legOrderObj.remove(OrderConstants.PROFIT_PRICE);
            legOrderObj.remove(OrderConstants.SL_PRICE);
            legOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
            legOrderObj.remove(OrderConstants.TRAILING_SL);
        }
        legOrderObj.put(OrderConstants.IS_EXECUTED, Boolean
                .toString(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus, sProfitOrderStatus, sSquareoffStatus)));
        legOrderObj.put(OrderConstants.DISP_STATUS, OrderStatus.getLegOrderDisplayStatus(sMainOrderStatus,
                sSLOrderStatus, sProfitOrderStatus, sSquareoffStatus));

        OrderStatus.getBuyOrSellMoreFlags(sMainOrderStatus, sSLOrderStatus, sProfitOrderStatus, sSquareoffStatus,
                sBuyOrSell, order);

        /*** To identify, any one of the leg orders cancelled ***/
        bracketOrdObj.put(OrderConstants.IS_LEG_ORDER_CANCELLED,
                Boolean.toString(OrderStatus.isLegOrderCancelled(sSLOrderStatus, sProfitOrderStatus)));

        bracketOrdObj.put(OrderConstants.LEG_ORDER_SHORT_DESC, legOrderObj);
        bracketOrdObj.put(OrderConstants.LEG_ORDER_DETAILS, bracketOrderArr);
        bracketOrdObj.put(OrderConstants.CANCELLED_ORDER_DETAILS, cancelledLegOrderArr);

        return bracketOrdObj;
    }

    private static void getOrderModifyFlags(String bracketOrdModifyBit, JSONObject modifyOrderDetailsObj) {

        int iOrderModifyBit = Integer.parseInt(bracketOrdModifyBit);

        if (iOrderModifyBit == 7) {
            modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "true");
            modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
            modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "true");
            modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE, "true");
        } else if (iOrderModifyBit == 6) {
            modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
            modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "true");
            modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE, "true");
        } else if (iOrderModifyBit == 4) {
            modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "true");
            modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE, "false");
        } else if (iOrderModifyBit == 2) {
            modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
            modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE, "true");
        } else if (iOrderModifyBit == 1) {
            modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "true");
            modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE, "false");
        } else {
            modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "false");
            modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE, "false");
        }
    }

    private static boolean getIsTrailingSLModifiable(String sLTPJumpPrc) {

        if (sLTPJumpPrc.equals("0.00") || sLTPJumpPrc.equals("0") || sLTPJumpPrc.isEmpty())
            return false;
        return true;

    }

    private static JSONObject getModifyOrderDetails(GetOrderBookObjectRow orderRow, boolean isDefault) {
        JSONObject modifyOrderDetailsObj = new JSONObject();
        if (!isDefault) {
            modifyOrderDetailsObj.put(OrderConstants.EXCH_ORD_NO, orderRow.getExOrderNo());
            modifyOrderDetailsObj.put(OrderConstants.ORDER_TIME, orderRow.getOrderTime());
            modifyOrderDetailsObj.put(OrderConstants.GATEWAY_ORD_NO, orderRow.getGatewayOrdNo());
        } else {
            modifyOrderDetailsObj.put(OrderConstants.EXCH_ORD_NO, "--");
            modifyOrderDetailsObj.put(OrderConstants.ORDER_TIME, "--");
            modifyOrderDetailsObj.put(OrderConstants.GATEWAY_ORD_NO, "--");
        }
        return modifyOrderDetailsObj;
    }

    private static String getLegOrderAction(String buySell) {
        if (buySell.equals(OrderAction.FT_B))
            return OrderAction.SELL;
        else if (buySell.equals(OrderAction.FT_S))
            return OrderAction.BUY;
        return buySell;
    }

    private static JSONObject getProfitOrderDetails(GetOrderBookObjectRow orderRow, boolean isMainOrderPending,
            SymbolRow order) throws JSONException, Exception {

        JSONObject targetOrderObj = new JSONObject();

        targetOrderObj.put(OrderConstants.LEG_ORDER_TYPE, OrderConstants.TARGET_ORDER);
        targetOrderObj.put(OrderConstants.DISP_LEG_ORDER, OrderConstants.DISP_TARGET_ORDER);
        targetOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());

        if (!isMainOrderPending) {
            targetOrderObj.put(OrderConstants.ORDER_ACTION, OrderAction.formatToDevice(orderRow.getBuySell()));
            String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());
            String sDispStatus = OrderStatus.getDisplayStatus(sStatus);
            if (sStatus.equals(OrderConstants.EXECUTED)) {

                JSONObject objAvgTradeValue = getAvgTradeValue(orderRow);
                targetOrderObj.put(OrderConstants.TRADED_VALUE,
                        getOrderTradeValue(objAvgTradeValue.getString(OrderConstants.TRADED_VALUE),
                                SymbolMap.getSymbolRow(order.getSymbolToken())));
            }
            targetOrderObj.put(OrderConstants.DISP_STATUS, sDispStatus);
        } else
            targetOrderObj.put(OrderConstants.ORDER_ACTION, getLegOrderAction((orderRow.getBuySell())));

        targetOrderObj.put(OrderConstants.REASON, orderRow.getError());

        targetOrderObj.put(OrderConstants.TRADE_SUMMARY, new JSONArray());

        targetOrderObj.put(OrderConstants.ORDER_VALUE,
                getOrderTradeValue(
                        PriceFormat.formatPrice(
                                String.valueOf((new BigDecimal(orderRow.getProfitOrdPrc())
                                        .multiply(new BigDecimal(orderRow.getQty())))),
                                Integer.parseInt(OrderConstants.PRECISION_2), false),
                        SymbolMap.getSymbolRow(order.getSymbolToken())));

        return targetOrderObj;
    }

    private static JSONObject getSLOrderDetails(GetOrderBookObjectRow orderRow, boolean isMainOrderPending,
            SymbolRow order) throws JSONException, Exception {
        JSONObject slOrderObj = new JSONObject();

        slOrderObj.put(OrderConstants.LEG_ORDER_TYPE, OrderConstants.STOPLOSS_ORDER);
        slOrderObj.put(OrderConstants.DISP_LEG_ORDER, OrderConstants.DISP_STOPLOSS_ORDER);
        slOrderObj.put(OrderConstants.ORDER_ACTION, OrderAction.formatToDevice(orderRow.getBuySell()));

        if (!isMainOrderPending) {
            String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());
            String sDispStatus = OrderStatus.getDisplayStatus(sStatus);
            if (sStatus.equals(OrderConstants.EXECUTED)) {
                JSONObject objAvgTradeValue = getAvgTradeValue(orderRow);
                slOrderObj.put(OrderConstants.TRADED_VALUE,
                        getOrderTradeValue(objAvgTradeValue.getString(OrderConstants.TRADED_VALUE),
                                SymbolMap.getSymbolRow(order.getSymbolToken())));
            }
            slOrderObj.put(OrderConstants.DISP_STATUS, sDispStatus);

        } else
            slOrderObj.put(OrderConstants.ORDER_ACTION, getLegOrderAction((orderRow.getBuySell())));

        slOrderObj.put(OrderConstants.REASON, orderRow.getError());

        slOrderObj.put(OrderConstants.TRADE_SUMMARY, new JSONArray());
        slOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
        slOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
        slOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getSlJumpPrc());
        slOrderObj
                .put(OrderConstants.ORDER_VALUE,
                        getOrderTradeValue(
                                PriceFormat.formatPrice(
                                        String.valueOf((new BigDecimal(orderRow.getSlOrdPrc())
                                                .multiply(new BigDecimal(orderRow.getQty())))),
                                        Integer.parseInt(OrderConstants.PRECISION_2), false),
                                SymbolMap.getSymbolRow(order.getSymbolToken())));

        return slOrderObj;
    }

    private static JSONObject getMainOrderDetails(GetOrderBookObjectRow orderRow, SymbolRow order)
            throws JSONException, Exception {

        JSONObject mainOrderObj = new JSONObject();

        mainOrderObj.put(OrderConstants.LEG_ORDER_TYPE, OrderConstants.MAIN_ORDER);
        mainOrderObj.put(OrderConstants.DISP_LEG_ORDER, OrderConstants.DISP_MAIN_ORDER);

        JSONObject objAvgTradeValue = getAvgTradeValue(orderRow);
        mainOrderObj.put(OrderConstants.AVG_PRICE, objAvgTradeValue.getString(OrderConstants.AVG_PRICE));
        mainOrderObj.put(OrderConstants.TRADED_VALUE,
                getOrderTradeValue(objAvgTradeValue.getString(OrderConstants.TRADED_VALUE),
                        SymbolMap.getSymbolRow(order.getSymbolToken())));

        mainOrderObj.put(OrderConstants.TRADE_SUMMARY, new JSONArray());

        mainOrderObj
                .put(OrderConstants.ORDER_VALUE,
                        getOrderTradeValue(
                                PriceFormat.formatPrice(
                                        String.valueOf((new BigDecimal(orderRow.getPrc())
                                                .multiply(new BigDecimal(orderRow.getQty())))),
                                        Integer.parseInt(OrderConstants.PRECISION_2), false),
                                SymbolMap.getSymbolRow(order.getSymbolToken())));

        mainOrderObj.put(OrderConstants.ORDER_ID, orderRow.getClientOrdNo());
        mainOrderObj.put(OrderConstants.DISP_ORDER_TIME, orderRow.getTime());
        mainOrderObj.put(OrderConstants.EXCH_ORDER_TIME, orderRow.getTime());
        mainOrderObj.put(OrderConstants.ORDER_ACTION, OrderAction.formatToDevice(orderRow.getBuySell()));

        String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());
        String sDispStatus = OrderStatus.getDisplayStatus(sStatus);
        mainOrderObj.put(OrderConstants.DISP_STATUS, sDispStatus);
        mainOrderObj.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sStatus, orderRow.getError()));

        return mainOrderObj;
    }

    private static JSONObject getSquareoffOrderDetails(GetOrderBookObjectRow orderRow, SymbolRow order)
            throws JSONException, Exception {

        JSONObject squareOffOrderObj = new JSONObject();

        squareOffOrderObj.put(OrderConstants.LEG_ORDER_TYPE, OrderConstants.SQUARE_OFF_ORDER);
        squareOffOrderObj.put(OrderConstants.DISP_LEG_ORDER, OrderConstants.DISP_SQUARE_OFF_ORDER);

        JSONObject objAvgTradeValue = getAvgTradeValue(orderRow);
        squareOffOrderObj.put(OrderConstants.AVG_PRICE, objAvgTradeValue.getString(OrderConstants.AVG_PRICE));
        squareOffOrderObj.put(OrderConstants.TRADED_VALUE,
                getOrderTradeValue(objAvgTradeValue.getString(OrderConstants.TRADED_VALUE),
                        SymbolMap.getSymbolRow(order.getSymbolToken())));

        squareOffOrderObj.put(OrderConstants.TRADE_SUMMARY, new JSONArray());

        squareOffOrderObj
                .put(OrderConstants.ORDER_VALUE,
                        getOrderTradeValue(
                                PriceFormat.formatPrice(
                                        String.valueOf((new BigDecimal(orderRow.getPrc())
                                                .multiply(new BigDecimal(orderRow.getQty())))),
                                        Integer.parseInt(OrderConstants.PRECISION_2), false),
                                SymbolMap.getSymbolRow(order.getSymbolToken())));

        squareOffOrderObj.put(OrderConstants.ORDER_ID, orderRow.getClientOrdNo());
        squareOffOrderObj.put(OrderConstants.DISP_ORDER_TIME, orderRow.getTime());
        squareOffOrderObj.put(OrderConstants.EXCH_ORDER_TIME, orderRow.getTime());
        squareOffOrderObj.put(OrderConstants.ORDER_ACTION, OrderAction.formatToDevice(orderRow.getBuySell()));

        String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());
        String sDispStatus = OrderStatus.getDisplayStatus(sStatus);
        squareOffOrderObj.put(OrderConstants.DISP_STATUS, sDispStatus);
        squareOffOrderObj.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sStatus, orderRow.getError()));

        return squareOffOrderObj;
    }

    public static void getQuote(JSONArray orderList, LinkedHashSet<String> linkedsetSymbolToken) throws SQLException {
        Map<String, QuoteDetails> mQuoteDetails = Quote.getLTP(linkedsetSymbolToken);

        for (int i = 0; i < orderList.length(); i++) {
            SymbolRow order = (SymbolRow) orderList.getJSONObject(i);
            String sSymbolToken = (orderList.getJSONObject(i)).getJSONObject(SymbolConstants.SYMBOL_OBJ)
                    .getString(SymbolConstants.SYMBOL_TOKEN);

            if (mQuoteDetails.containsKey(sSymbolToken)) {
                QuoteDetails quoteDetails = mQuoteDetails.get(sSymbolToken);
                order.put(DeviceConstants.LTP,
                        PriceFormat.formatPrice(quoteDetails.sLTP, order.getPrecisionInt(), false));
                order.put(DeviceConstants.CHANGE,
                        PriceFormat.formatPrice(quoteDetails.sChange, order.getPrecisionInt(), false));
                order.put(DeviceConstants.CHANGE_PERCENT,
                        PriceFormat.formatPrice(quoteDetails.sChangePercent, 2, false));

            } else {
                order.put(DeviceConstants.LTP, "0.00");
                order.put(DeviceConstants.CHANGE, "0.00");
                order.put(DeviceConstants.CHANGE_PERCENT, "0");
            }

        }

    }

    // <<<<<<<HEAD
    /*
     * private static Map<String, SymbolRow> getCoverOrderDetails(Map<String,
     * JSONArray> mapCoverOrders, LinkedHashSet<String> linkedsetSymbolToken, String
     * sOrderStatus) throws Exception {
     *
     * boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ?
     * true : false); Map<String, SymbolRow> orderMap = new HashMap<String,
     * SymbolRow>();
     *
     * for (Map.Entry<String, JSONArray> entry : mapCoverOrders.entrySet()) {
     *
     * SymbolRow order = new SymbolRow(); GetOrderBookObjectRow orderRow =
     * (GetOrderBookObjectRow) entry.getValue().get(0);
     *
     *//*** To fetch quote details and symbol object ***/
    /*
     * String sScripCode = orderRow.getScripCode(); String sMktSegID =
     * ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst());
     * String sTokenMktSegID = sScripCode + "_" + sMktSegID;
     * order.extend(SymbolMap.getSymbolRow(sScripCode,
     * sMktSegID).getMinimisedSymbolRow()); order.put(FTConstants.CO_DETAILS,
     * getCoverOrderDetails(entry.getValue(), order));
     * order.put(OrderConstants.IS_BRACKET_ORDER, "false");
     *
     *//*** To send orders based on order status ***/
    /*
     * String sStatus =
     * OrderStatus.getStatus(order.getString(OrderConstants.STATUS)); if (isAll) {
     * linkedsetSymbolToken.add(sTokenMktSegID);
     * orderMap.put(order.getString(OrderConstants.RECO_ID), order); } else { if
     * (sStatus.equals(sOrderStatus)) { linkedsetSymbolToken.add(sTokenMktSegID);
     * orderMap.put(order.getString(OrderConstants.RECO_ID), order); } } } return
     * orderMap;
     *
     * }
     */
    /*
     * private static JSONObject getCoverOrderDetails(JSONArray coverOrders,
     * SymbolRow order) throws JSONException, Exception {
     *
     * JSONObject coverOrdObj = new JSONObject(); // Final Object contains bracket
     * order details JSONObject legOrderObj = new JSONObject(); // Leg order short
     * detail JSONArray coverOrderArr = new JSONArray(); // Array of pending and
     * executed order details JSONArray cancelledLegOrderArr = new JSONArray(); //
     * Array of cancelled order details JSONObject modifyOrderDetailsObj = new
     * JSONObject(); // To be forwarded while sending bracket order modify //
     * request
     *
     * String sMainOrderStatus = "", sSLOrderStatus = ""; String sSquareoffStatus =
     * ""; String sExch = "", sInst = ""; String sBuyOrSell = ""; for (int i = 0; i
     * < coverOrders.length(); i++) { GetOrderBookObjectRow orderRow =
     * (GetOrderBookObjectRow) coverOrders.get(i); if
     * ((orderRow.getLegIndicator().equals("0")) ?
     * orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL_MKT) :
     * orderRow.getLegIndicator().equals("1")) {
     *
     *//***
        * To fetch all the common fields for the order, main order leg is same as
        * normal order
        ***/
    /*
     * getOrderDetails(orderRow, order); sExch = orderRow.getExch(); sInst =
     * orderRow.getInst();
     *//*** To fetch main order details ***/
    /*
     * coverOrderArr.put(getMainOrderDetails(orderRow, order));
     *
     *//*** To display status based on the main leg order status ***/
    /*
     * sMainOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat()); sBuyOrSell =
     * orderRow.getBuySell(); order.put(OrderConstants.STATUS, sMainOrderStatus); if
     * (sMainOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_EXECUTED); } else
     * if (sMainOrderStatus.equals(OrderConstants.CANCELLED) ) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, true, order);
     *//*** flag is to identify main order is pending or not ***/
    /*
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL); coverOrderArr.put(slOrderObj);
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell())); } else if
     * (sMainOrderStatus.equals(OrderConstants.PENDING)) { // If main leg order is
     * pending, still // order is not pushed to the exchange
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, true, order);
     *//*** flag is to identify main order is pending or not ***/
    /*
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL); coverOrderArr.put(slOrderObj);
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     *
     * If main order action is buy,then leg order action is sell *
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     *
     * } order.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sMainOrderStatus,
     * orderRow.getError()));
     *
     *//*** Details needed for cover order modification ***/
    /*
     * modifyOrderDetailsObj.put(OrderConstants.MAIN_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false).put(FTConstants.CLIENT_ORD_NO,
     * orderRow.getClientOrdNo()));
     * modifyOrderDetailsObj.put(OrderConstants.BRACKET_ORDER_ID,
     * orderRow.getBracketOrdId()); modifyOrderDetailsObj.put(OrderConstants.PRICE,
     * orderRow.getPrc()); modifyOrderDetailsObj.put(OrderConstants.ORDER_QTY,
     * orderRow.getQty()); modifyOrderDetailsObj.put(OrderConstants.PENDING_QTY,
     * orderRow.getQty());
     *
     * } else if ((orderRow.getLegIndicator().equals("0")) ?
     * orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL) :
     * orderRow.getLegIndicator().equals("2")) {
     *//***
        * To fetch Stop loss order details, flag is to identify main order is pending
        * or not
        ***/
    /*
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, false, order);
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * slOrderObj.remove(OrderConstants.ORDER_VALUE);
     * slOrderObj.put(OrderConstants.SL_PRICE, orderRow.getPrc());
     * slOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getTrigPrc());
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * OrderAction.formatToDevice(orderRow.getBuySell()));
     *
     * sSLOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat()); if
     * (sSLOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_SL_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     *
     * }
     *
     * if (sSLOrderStatus.equals(OrderConstants.CANCELLED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * cancelledLegOrderArr.put(slOrderObj); } else coverOrderArr.put(slOrderObj);
     *
     * if (sSLOrderStatus.equals(OrderConstants.PENDING)) {
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getTrigPrc());
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.SL_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false).put(FTConstants.CLIENT_ORD_NO,
     * orderRow.getClientOrdNo())); } }
     *
     * } // End of for loop
     *//***
        * If any one of the orders is pending, then user can modify or cancel the order
        ***/
    /*
     * if (sMainOrderStatus.equals(OrderConstants.PENDING) ||
     * sSLOrderStatus.equals(OrderConstants.PENDING)) // modify object is needed
     * only when user can modify or cancel the order
     * coverOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, modifyOrderDetailsObj);
     * else coverOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, new JSONObject());
     *
     * order.put(OrderConstants.IS_MODIFIABLE,
     * Boolean.toString(OrderStatus.isCoverOrderModifiable(sMainOrderStatus,
     * sSLOrderStatus))); order.put(OrderConstants.IS_CANCELLABLE,
     * Boolean.toString(OrderStatus.isCoverOrderCancellable(sMainOrderStatus,
     * sSLOrderStatus))); order.put(OrderConstants.EXIT_OPTION,
     * Boolean.toString(OrderStatus.getCoverOrderExitOption(sMainOrderStatus,
     * sSLOrderStatus)));
     *
     * //Temporary fix order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *
     *//*** To show, leg order short description status ***/
    /*
     *
     * legOrderObj.put(OrderConstants.IS_EXECUTED, Boolean.toString(OrderStatus.
     * checkAnyLegOrderExecuted(sSLOrderStatus,"", sSquareoffStatus)));
     * legOrderObj.put(OrderConstants.DISP_STATUS,
     * OrderStatus.getCoverLegOrderDisplayStatus(sMainOrderStatus, sSLOrderStatus,
     * sSquareoffStatus));
     *
     * OrderStatus.getCoverBuyOrSellMoreFlags(sMainOrderStatus, sSLOrderStatus
     * ,sSquareoffStatus, sBuyOrSell, order);
     *
     *
     *//*** To identify, any one of the leg orders cancelled ***//*
                                                                  * coverOrdObj.put(OrderConstants.
                                                                  * IS_LEG_ORDER_CANCELLED,
                                                                  * Boolean.toString(OrderStatus.isLegOrderCancelled(
                                                                  * sSLOrderStatus,"")));
                                                                  *
                                                                  * coverOrdObj.put(OrderConstants.
                                                                  * LEG_ORDER_SHORT_DESC, legOrderObj);
                                                                  * coverOrdObj.put(OrderConstants.LEG_ORDER_DETAILS,
                                                                  * coverOrderArr); coverOrdObj.put(OrderConstants.
                                                                  * CANCELLED_ORDER_DETAILS, cancelledLegOrderArr);
                                                                  * return coverOrdObj; }
                                                                  */
    // =======

    /*
     * private static Map<String, SymbolRow> getCoverOrderDetails(Map<String,
     * JSONArray> mapCoverOrders,
     * LinkedHashSet<String> linkedsetSymbolToken, String sOrderStatus, Map<String,
     * JSONArray> mapExeCoverOrders,
     * Map<String, JSONArray> mapSqaureOffCoverOrders, JSONObject orderBook) throws
     * Exception {
     *
     * boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ?
     * true : false);
     * Map<String, SymbolRow> orderMap = new HashMap<String, SymbolRow>();
     *
     * for (Map.Entry<String, JSONArray> entry : mapCoverOrders.entrySet()) {
     *
     * SymbolRow order = new SymbolRow();
     * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow)
     * entry.getValue().get(0);
     *
     *//*** To fetch quote details and symbol object ***/
    /*
     * String sScripCode = orderRow.getScripCode();
     * String sMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(),
     * orderRow.getInst());
     * String sTokenMktSegID = sScripCode + "_" + sMktSegID;
     * order.extend(SymbolMap.getSymbolRow(sScripCode,
     * sMktSegID).getMinimisedSymbolRow());
     * order.put(FTConstants.CO_DETAILS, getCoverOrderDetails(entry.getValue(),
     * order, mapExeCoverOrders,
     * mapSqaureOffCoverOrders, orderBook));
     * order.put(OrderConstants.IS_COVER_ORDER, "true");
     * order.put(OrderConstants.IS_COVER_MAIN_ORDER, "true");
     * order.put(OrderConstants.IS_COVER_SL_ORDER, "false");
     * order.put(OrderConstants.IS_COVER_SQUAREOFF_ORDER, "false");
     *
     *//*** To send orders based on order status ***/
    /*
     * String sStatus =
     * OrderStatus.getStatus(order.getString(OrderConstants.STATUS));
     * if (isAll) {
     * linkedsetSymbolToken.add(sTokenMktSegID);
     * orderMap.put(order.getString(OrderConstants.RECO_ID), order);
     * } else if (sStatus.equals(sOrderStatus)) {
     * linkedsetSymbolToken.add(sTokenMktSegID);
     * orderMap.put(order.getString(OrderConstants.RECO_ID), order);
     * }
     * }
     * return orderMap;
     *
     * }
     *
     * private static Map<String, SymbolRow> getCoverLegOrderDetails(Map<String,
     * JSONArray> mapCoverOrders,
     * LinkedHashSet<String> linkedsetSymbolToken, String sOrderStatus) throws
     * Exception {
     *
     * boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ?
     * true : false);
     * Map<String, SymbolRow> orderMap = new HashMap<String, SymbolRow>();
     *
     * for (Map.Entry<String, JSONArray> entry : mapCoverOrders.entrySet()) {
     *
     * SymbolRow order = new SymbolRow();
     * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow)
     * entry.getValue().get(0);
     *
     *//*** To fetch quote details and symbol object ***/
    /*
     * String sScripCode = orderRow.getScripCode();
     * String sMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(),
     * orderRow.getInst());
     * String sTokenMktSegID = sScripCode + "_" + sMktSegID;
     * order.extend(SymbolMap.getSymbolRow(sScripCode,
     * sMktSegID).getMinimisedSymbolRow());
     * order.put(FTConstants.CO_DETAILS, getCoverLegOrderDetails(entry.getValue(),
     * order));
     * order.put(OrderConstants.IS_COVER_ORDER, "true");
     * order.put(OrderConstants.IS_COVER_MAIN_ORDER, "false");
     * order.put(OrderConstants.IS_COVER_SL_ORDER, "true");
     * order.put(OrderConstants.IS_COVER_SQUAREOFF_ORDER, "false");
     *//*** To send orders based on order status ***/
    /*
     * String sStatus =
     * OrderStatus.getStatus(order.getString(OrderConstants.STATUS));
     *
     * if (isAll) {
     * linkedsetSymbolToken.add(sTokenMktSegID);
     * orderMap.put(order.getString(OrderConstants.RECO_ID), order);
     * } else if (sStatus.equals(sOrderStatus)) {
     * linkedsetSymbolToken.add(sTokenMktSegID);
     * orderMap.put(order.getString(OrderConstants.RECO_ID), order);
     * }
     * }
     * return orderMap;
     *
     * }
     *
     * private static Map<String, SymbolRow>
     * getCoverSquareOffOrderDetails(Map<String, JSONArray> mapCoverOrders,
     * LinkedHashSet<String> linkedsetSymbolToken, String sOrderStatus) throws
     * Exception {
     *
     * boolean isAll = (sOrderStatus.toLowerCase().contains(OrderConstants.ALL) ?
     * true : false);
     * Map<String, SymbolRow> orderMap = new HashMap<String, SymbolRow>();
     *
     * for (Map.Entry<String, JSONArray> entry : mapCoverOrders.entrySet()) {
     *
     * SymbolRow order = new SymbolRow();
     * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow)
     * entry.getValue().get(0);
     *
     *//*** To fetch quote details and symbol object ***/
    /*
     * String sScripCode = orderRow.getScripCode();
     * String sMktSegID = ExchangeSegment.getMarketSegmentID(orderRow.getExch(),
     * orderRow.getInst());
     * String sTokenMktSegID = sScripCode + "_" + sMktSegID;
     * order.extend(SymbolMap.getSymbolRow(sScripCode,
     * sMktSegID).getMinimisedSymbolRow());
     * order.put(FTConstants.CO_DETAILS,
     * getCoverSquareOffOrderDetails(entry.getValue(), order));
     * order.put(OrderConstants.IS_COVER_ORDER, "true");
     * order.put(OrderConstants.IS_COVER_MAIN_ORDER, "false");
     * order.put(OrderConstants.IS_COVER_SL_ORDER, "false");
     * order.put(OrderConstants.IS_COVER_SQUAREOFF_ORDER, "true");
     *//*** To send orders based on order status ***/
    /*
     * String sStatus =
     * OrderStatus.getStatus(order.getString(OrderConstants.STATUS));
     * if (isAll) {
     * linkedsetSymbolToken.add(sTokenMktSegID);
     * orderMap.put(order.getString(OrderConstants.RECO_ID), order);
     * } else if (sStatus.equals(sOrderStatus)) {
     * linkedsetSymbolToken.add(sTokenMktSegID);
     * orderMap.put(order.getString(OrderConstants.RECO_ID), order);
     * }
     * }
     * return orderMap;
     *
     * }
     *
     * private static JSONObject getCoverOrderDetails(JSONArray coverOrders,
     * SymbolRow order,
     * Map<String, JSONArray> mapExeCoverOrders,
     * Map<String, JSONArray> mapSqaureOffCoverOrders, JSONObject orderBook)
     * throws JSONException, Exception {
     *
     * JSONObject coverOrdObj = new JSONObject(); // Final Object contains bracket
     * order details
     * JSONObject legOrderObj = new JSONObject(); // Leg order short detail
     * JSONArray coverOrderArr = new JSONArray(); // Array of pending and executed
     * order details
     * JSONArray cancelledLegOrderArr = new JSONArray(); // Array of cancelled order
     * details
     * JSONObject modifyOrderDetailsObj = new JSONObject(); // To be forwarded while
     * sending bracket order modify order
     * String sMainOrderStatus = "", sSLOrderStatus = "";
     * String sSquareoffStatus = "";
     * String sExch = "", sInst = "";
     * String sBuyOrSell = "";
     * for (int i = 0; i < coverOrders.length(); i++) {
     * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow) coverOrders.get(i);
     * if (!((orderRow.getLegIndicator().equals("0"))
     * ? orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL_MKT)
     * : orderRow.getLegIndicator().equals("1"))) {
     * String sStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sStatus.equalsIgnoreCase(OrderConstants.PENDING)) {
     * orderBook.put(DeviceConstants.PENDING_COUNT,
     * orderBook.has(DeviceConstants.PENDING_COUNT)
     * ? String.valueOf(Integer.parseInt(orderBook.getString(DeviceConstants.
     * PENDING_COUNT)) + 1)
     * : "1");
     * } else if (sStatus.equalsIgnoreCase(OrderConstants.EXECUTED)
     * || sStatus.equalsIgnoreCase(OrderConstants.CANCELLED)) {
     * orderBook.put(DeviceConstants.EXECUTED_COUNT,
     * orderBook.has(DeviceConstants.EXECUTED_COUNT)
     * ? String.valueOf(Integer.parseInt(orderBook.getString(DeviceConstants.
     * EXECUTED_COUNT)) + 1)
     * : "1");
     * }
     * }
     * if ((orderRow.getLegIndicator().equals("0")) ?
     * orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL_MKT)
     * : orderRow.getLegIndicator().equals("1")) {
     *
     *//***
        * To fetch all the common fields for the order, main order leg is same as
        * normal order
        ***/
    /*
     * getOrderDetails(orderRow, order);
     * sExch = orderRow.getExch();
     * sInst = orderRow.getInst();
     *//*** To fetch main order details ***/
    /*
     * coverOrderArr.put(getMainOrderDetails(orderRow, order));
     *
     *//*** To display status based on the main leg order status ***/
    /*
     * sMainOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * sBuyOrSell = orderRow.getBuySell();
     * order.put(OrderConstants.STATUS, sMainOrderStatus);
     * if (sMainOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_EXECUTED);
     * } else if (sMainOrderStatus.equals(OrderConstants.CANCELLED)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, true, order);
     *//*** flag is to identify main order is pending or not ***/
    /*
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * coverOrderArr.put(slOrderObj);
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     * } else if (sMainOrderStatus.equals(OrderConstants.PENDING)) { // If main leg
     * order is pending, still
     * // order is not pushed to the exchange
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, true, order);
     *//*** flag is to identify main order is pending or not ***/
    /*
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * coverOrderArr.put(slOrderObj);
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     *
     * If main order action is buy,then leg order action is sell *
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     *
     * }
     * order.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sMainOrderStatus,
     * orderRow.getError()));
     *
     *//*** Details needed for cover order modification ***/
    /*
     * modifyOrderDetailsObj.put(OrderConstants.MAIN_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false)
     * .put(FTConstants.CLIENT_ORD_NO, orderRow.getClientOrdNo()));
     * modifyOrderDetailsObj.put(OrderConstants.BRACKET_ORDER_ID,
     * orderRow.getBracketOrdId());
     * modifyOrderDetailsObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * modifyOrderDetailsObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * modifyOrderDetailsObj.put(OrderConstants.PENDING_QTY, orderRow.getQty());
     *
     * } else if ((orderRow.getLegIndicator().equals("0"))
     * ? orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL)
     * : orderRow.getLegIndicator().equals("2")) {
     * mapExeCoverOrders.put(orderRow.getRecoId().split(" ")[0], coverOrders);
     *//***
        * To fetch Stop loss order details, flag is to identify main order is pending
        * or not
        ***/
    /*
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, false, order);
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * slOrderObj.remove(OrderConstants.ORDER_VALUE);
     * slOrderObj.put(OrderConstants.SL_PRICE, orderRow.getPrc());
     * slOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getTrigPrc());
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * OrderAction.formatToDevice(orderRow.getBuySell()));
     *
     * sSLOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sSLOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_SL_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     *
     * }
     *
     * if (sSLOrderStatus.equals(OrderConstants.CANCELLED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * cancelledLegOrderArr.put(slOrderObj);
     * } else
     * coverOrderArr.put(slOrderObj);
     *
     * if (sSLOrderStatus.equals(OrderConstants.PENDING)) {
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getTrigPrc());
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.SL_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false)
     * .put(FTConstants.CLIENT_ORD_NO, orderRow.getClientOrdNo()));
     * }
     * } else {
     * mapSqaureOffCoverOrders.put(orderRow.getRecoId().split(" ")[0], coverOrders);
     * sSquareoffStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER,
     * OrderConstants.DISP_SQUAREOFF_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * }
     *
     * if (sSquareoffStatus.equals(OrderConstants.CANCELLED)) {
     * cancelledLegOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     * } else
     * coverOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     * }
     *
     * } // End of for loop
     *//***
        * If any one of the orders is pending, then user can modify or cancel the order
        ***/
    /*
     * if (sMainOrderStatus.equals(OrderConstants.PENDING) ||
     * sSLOrderStatus.equals(OrderConstants.PENDING))
     * // modify object is needed only when user can modify or cancel the order
     * coverOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, modifyOrderDetailsObj);
     * else
     * coverOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, new JSONObject());
     *
     * order.put(OrderConstants.IS_MODIFIABLE,
     * Boolean.toString(OrderStatus.isCoverOrderModifiable(sMainOrderStatus)));
     * order.put(OrderConstants.IS_CANCELLABLE,
     * Boolean.toString(OrderStatus.isCoverOrderCancellable(sMainOrderStatus)));
     * order.put(OrderConstants.EXIT_OPTION,
     * Boolean.toString(false));
     *
     * // Temporary fix
     * order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *//*** To show, leg order short description status ***/
    /*
     *
     * legOrderObj.put(OrderConstants.IS_EXECUTED,
     * Boolean.toString(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus, "",
     * sSquareoffStatus)));
     * legOrderObj.put(OrderConstants.DISP_STATUS,
     * OrderStatus.getCoverLegOrderDisplayStatus(sMainOrderStatus, sSLOrderStatus,
     * sSquareoffStatus));
     *
     * OrderStatus.getCoverBuyOrSellMoreFlags(sMainOrderStatus, sSLOrderStatus,
     * sSquareoffStatus, sBuyOrSell, order);
     *
     *//*** To identify, any one of the leg orders cancelled ***/
    /*
     * coverOrdObj.put(OrderConstants.IS_LEG_ORDER_CANCELLED,
     * Boolean.toString(OrderStatus.isLegOrderCancelled(sSLOrderStatus, "")));
     *
     * coverOrdObj.put(OrderConstants.LEG_ORDER_SHORT_DESC, legOrderObj);
     * coverOrdObj.put(OrderConstants.LEG_ORDER_DETAILS, coverOrderArr);
     * coverOrdObj.put(OrderConstants.CANCELLED_ORDER_DETAILS,
     * cancelledLegOrderArr);
     * return coverOrdObj;
     * }
     *
     * private static JSONObject getCoverLegOrderDetails(JSONArray coverOrders,
     * SymbolRow order)
     * throws JSONException, Exception {
     *
     * JSONObject coverOrdObj = new JSONObject(); // Final Object contains bracket
     * order details
     * JSONObject legOrderObj = new JSONObject(); // Leg order short detail
     * JSONArray coverOrderArr = new JSONArray(); // Array of pending and executed
     * order details
     * JSONArray cancelledLegOrderArr = new JSONArray(); // Array of cancelled order
     * details
     * JSONObject modifyOrderDetailsObj = new JSONObject(); // To be forwarded while
     * sending bracket order modify
     * // request
     *
     * String sMainOrderStatus = "", sSLOrderStatus = "";
     * String sSquareoffStatus = "";
     * String sExch = "", sInst = "";
     * String sBuyOrSell = "";
     * for (int i = 0; i < coverOrders.length(); i++) {
     * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow) coverOrders.get(i);
     * if ((orderRow.getLegIndicator().equals("0")) ?
     * orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL_MKT)
     * : orderRow.getLegIndicator().equals("1")) {
     *
     * sExch = orderRow.getExch();
     * sInst = orderRow.getInst();
     *//*** To fetch main order details ***/
    /*
     * coverOrderArr.put(getMainOrderDetails(orderRow, order));
     *
     *//*** To display status based on the main leg order status ***/
    /*
     * sMainOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * sBuyOrSell = orderRow.getBuySell();
     * if (sMainOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
     * } else if (sMainOrderStatus.equals(OrderConstants.CANCELLED)) {
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, true, order);
     *//*** flag is to identify main order is pending or not ***/
    /*
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * coverOrderArr.put(slOrderObj);
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     * } else if (sMainOrderStatus.equals(OrderConstants.PENDING)) { // If main leg
     * order is pending, still
     * // order is not pushed to the exchange
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, true, order);
     *//*** flag is to identify main order is pending or not ***/
    /*
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * coverOrderArr.put(slOrderObj);
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     *
     * If main order action is buy,then leg order action is sell *
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     *
     * }
     *
     *//*** Details needed for cover order modification ***/
    /*
     * modifyOrderDetailsObj.put(OrderConstants.MAIN_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false)
     * .put(FTConstants.CLIENT_ORD_NO, orderRow.getClientOrdNo()));
     * modifyOrderDetailsObj.put(OrderConstants.BRACKET_ORDER_ID,
     * orderRow.getBracketOrdId());
     * modifyOrderDetailsObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * modifyOrderDetailsObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * modifyOrderDetailsObj.put(OrderConstants.PENDING_QTY, orderRow.getQty());
     *
     * } else if ((orderRow.getLegIndicator().equals("0"))
     * ? (orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL))
     * : orderRow.getLegIndicator().equals("2")) {
     *
     * getOrderDetails(orderRow, order);
     *
     *//***
        * To fetch Stop loss order details, flag is to identify main order is pending
        * or not
        ***/
    /*
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, false, order);
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * slOrderObj.remove(OrderConstants.ORDER_VALUE);
     * slOrderObj.put(OrderConstants.SL_PRICE, orderRow.getPrc());
     * slOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getTrigPrc());
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * OrderAction.formatToDevice(orderRow.getBuySell()));
     *
     * sSLOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * order.put(OrderConstants.STATUS, sSLOrderStatus);
     *
     * if (sSLOrderStatus.equals(OrderConstants.EXECUTED)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_EXECUTED);
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_SL_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     *
     * }
     *
     * if (sSLOrderStatus.equals(OrderConstants.CANCELLED)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * cancelledLegOrderArr.put(slOrderObj);
     * } else
     * coverOrderArr.put(slOrderObj);
     *
     * if (sSLOrderStatus.equals(OrderConstants.PENDING)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getTrigPrc());
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.SL_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false)
     * .put(FTConstants.CLIENT_ORD_NO, orderRow.getClientOrdNo()));
     * }
     * order.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sSLOrderStatus,
     * orderRow.getError()));
     * } else {
     * sSquareoffStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER,
     * OrderConstants.DISP_SQUAREOFF_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * }
     *
     * if (sSquareoffStatus.equals(OrderConstants.CANCELLED)) {
     * cancelledLegOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     * } else
     * coverOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     * }
     *
     * } // End of for loop
     *//***
        * If any one of the orders is pending, then user can modify or cancel the order
        ***/
    /*
     * if (sMainOrderStatus.equals(OrderConstants.PENDING) ||
     * sSLOrderStatus.equals(OrderConstants.PENDING))
     * // modify object is needed only when user can modify or cancel the order
     * coverOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, modifyOrderDetailsObj);
     * else
     * coverOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, new JSONObject());
     *
     * order.put(OrderConstants.IS_MODIFIABLE,
     * Boolean.toString(OrderStatus.isCoverOrderModifiable(sSLOrderStatus)));
     * order.put(OrderConstants.IS_CANCELLABLE,
     * Boolean.toString(OrderStatus.isCoverOrderCancellable(sSLOrderStatus)));
     * order.put(OrderConstants.EXIT_OPTION,
     * Boolean.toString(OrderStatus.getCoverOrderExitOption(sMainOrderStatus,
     * sSLOrderStatus)));
     *
     * // Temporary fix
     * order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *//*** To show, leg order short description status ***/
    /*
     *
     * legOrderObj.put(OrderConstants.IS_EXECUTED,
     * Boolean.toString(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus, "",
     * sSquareoffStatus)));
     * legOrderObj.put(OrderConstants.DISP_STATUS,
     * OrderStatus.getCoverLegOrderDisplayStatus(sMainOrderStatus, sSLOrderStatus,
     * sSquareoffStatus));
     * OrderStatus.getCoverBuyOrSellMoreFlags(sMainOrderStatus, sSLOrderStatus,
     * sBuyOrSell, order);
     *
     *//*** To identify, any one of the leg orders cancelled ***/
    /*
     * coverOrdObj.put(OrderConstants.IS_LEG_ORDER_CANCELLED,
     * Boolean.toString(OrderStatus.isLegOrderCancelled(sSLOrderStatus, "")));
     *
     * coverOrdObj.put(OrderConstants.LEG_ORDER_SHORT_DESC, legOrderObj);
     * coverOrdObj.put(OrderConstants.LEG_ORDER_DETAILS, coverOrderArr);
     * coverOrdObj.put(OrderConstants.CANCELLED_ORDER_DETAILS,
     * cancelledLegOrderArr);
     * return coverOrdObj;
     * }
     *
     * private static JSONObject getCoverSquareOffOrderDetails(JSONArray
     * coverOrders, SymbolRow order)
     * throws JSONException, Exception {
     *
     * JSONObject coverOrdObj = new JSONObject(); // Final Object contains bracket
     * order details
     * JSONObject legOrderObj = new JSONObject(); // Leg order short detail
     * JSONArray coverOrderArr = new JSONArray(); // Array of pending and executed
     * order details
     * JSONArray cancelledLegOrderArr = new JSONArray(); // Array of cancelled order
     * details
     * JSONObject modifyOrderDetailsObj = new JSONObject(); // To be forwarded while
     * sending bracket order modify
     * // request
     *
     * String sMainOrderStatus = "", sSLOrderStatus = "";
     * String sSquareoffStatus = "";
     * String sExch = "", sInst = "";
     * String sBuyOrSell = "";
     * for (int i = 0; i < coverOrders.length(); i++) {
     * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow) coverOrders.get(i);
     * if ((orderRow.getLegIndicator().equals("0")) ?
     * orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL_MKT)
     * : orderRow.getLegIndicator().equals("1")) {
     *
     *//***
        * To fetch all the common fields for the order, main order leg is same as
        * normal order
        ***/
    /*
     * sExch = orderRow.getExch();
     * sInst = orderRow.getInst();
     *//*** To fetch main order details ***/
    /*
     * coverOrderArr.put(getMainOrderDetails(orderRow, order));
     *
     *//*** To display status based on the main leg order status ***/
    /*
     * sMainOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * sBuyOrSell = orderRow.getBuySell();
     *
     * if (sMainOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
     * } else if (sMainOrderStatus.equals(OrderConstants.CANCELLED)) {
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, true, order);
     *//*** flag is to identify main order is pending or not ***/
    /*
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * coverOrderArr.put(slOrderObj);
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     * } else if (sMainOrderStatus.equals(OrderConstants.PENDING)) { // If main leg
     * order is pending, still
     * // order is not pushed to the exchange
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, true, order);
     *//*** flag is to identify main order is pending or not ***/
    /*
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * coverOrderArr.put(slOrderObj);
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     *
     * If main order action is buy,then leg order action is sell *
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     *
     * }
     *
     *//*** Details needed for cover order modification ***/
    /*
     * modifyOrderDetailsObj.put(OrderConstants.MAIN_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false)
     * .put(FTConstants.CLIENT_ORD_NO, orderRow.getClientOrdNo()));
     * modifyOrderDetailsObj.put(OrderConstants.BRACKET_ORDER_ID,
     * orderRow.getBracketOrdId());
     * modifyOrderDetailsObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * modifyOrderDetailsObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * modifyOrderDetailsObj.put(OrderConstants.PENDING_QTY, orderRow.getQty());
     *
     * } else if ((orderRow.getLegIndicator().equals("0"))
     * ? orderRow.getOrderType().equalsIgnoreCase(FTConstants.RL)
     * : orderRow.getLegIndicator().equals("2")) {
     *//***
        * To fetch Stop loss order details, flag is to identify main order is pending
        * or not
        ***/
    /*
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, false, order);
     * slOrderObj.remove(OrderConstants.SL_PRICE);
     * slOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * slOrderObj.remove(OrderConstants.TRAILING_SL);
     * slOrderObj.remove(OrderConstants.ORDER_VALUE);
     * slOrderObj.put(OrderConstants.SL_PRICE, orderRow.getPrc());
     * slOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getTrigPrc());
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * OrderAction.formatToDevice(orderRow.getBuySell()));
     *
     * sSLOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sSLOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_SL_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     *
     * }
     *
     * if (sSLOrderStatus.equals(OrderConstants.CANCELLED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * cancelledLegOrderArr.put(slOrderObj);
     * } else
     * coverOrderArr.put(slOrderObj);
     *
     * if (sSLOrderStatus.equals(OrderConstants.PENDING)) {
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getTrigPrc());
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.SL_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false)
     * .put(FTConstants.CLIENT_ORD_NO, orderRow.getClientOrdNo()));
     * }
     * } else {
     * sSquareoffStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * getOrderDetails(orderRow, order);
     * order.put(OrderConstants.STATUS, sSquareoffStatus);
     * if (sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER,
     * OrderConstants.DISP_SQUAREOFF_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * }
     *
     * if (sSquareoffStatus.equals(OrderConstants.CANCELLED)) {
     * cancelledLegOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     * } else
     * coverOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     * order.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sMainOrderStatus,
     * orderRow.getError()));
     * }
     *
     * } // End of for loop
     *//***
        * If any one of the orders is pending, then user can modify or cancel the order
        ***/
    /*
     * if (sMainOrderStatus.equals(OrderConstants.PENDING) ||
     * sSLOrderStatus.equals(OrderConstants.PENDING))
     * // modify object is needed only when user can modify or cancel the order
     * coverOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, modifyOrderDetailsObj);
     * else
     * coverOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, new JSONObject());
     *
     * order.put(OrderConstants.IS_MODIFIABLE,
     * Boolean.toString(false));
     * order.put(OrderConstants.IS_CANCELLABLE,
     * Boolean.toString(false));
     * order.put(OrderConstants.EXIT_OPTION,
     * Boolean.toString(OrderStatus.getCoverOrderExitOption(sMainOrderStatus,
     * sSquareoffStatus)));
     *
     * // Temporary fix
     * order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *//*** To show, leg order short description status ***/
    /*
     *
     * legOrderObj.put(OrderConstants.IS_EXECUTED,
     * Boolean.toString(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus, "",
     * sSquareoffStatus)));
     * legOrderObj.put(OrderConstants.DISP_STATUS,
     * OrderStatus.getCoverLegOrderDisplayStatus(sMainOrderStatus, sSLOrderStatus,
     * sSquareoffStatus));
     *
     * OrderStatus.getCoverBuyOrSellMoreFlags(sMainOrderStatus, sSquareoffStatus,
     * sBuyOrSell, order);
     *
     *//*** To identify, any one of the leg orders cancelled ***/
    /*
     * coverOrdObj.put(OrderConstants.IS_LEG_ORDER_CANCELLED,
     * Boolean.toString(OrderStatus.isLegOrderCancelled(sSLOrderStatus, "")));
     *
     * coverOrdObj.put(OrderConstants.LEG_ORDER_SHORT_DESC, legOrderObj);
     * coverOrdObj.put(OrderConstants.LEG_ORDER_DETAILS, coverOrderArr);
     * coverOrdObj.put(OrderConstants.CANCELLED_ORDER_DETAILS,
     * cancelledLegOrderArr);
     * return coverOrdObj;
     * }
     *
     * private static JSONObject getBracketSLOrderDetails(JSONArray brackerOrders,
     * SymbolRow order)
     * throws JSONException, Exception {
     *
     * JSONObject bracketOrdObj = new JSONObject(); // Final Object contains bracket
     * order details
     * JSONObject legOrderObj = new JSONObject(); // Leg order short detail
     * JSONArray bracketOrderArr = new JSONArray(); // Array of pending and executed
     * order details
     * JSONArray cancelledLegOrderArr = new JSONArray(); // Array of cancelled order
     * details
     * JSONObject modifyOrderDetailsObj = new JSONObject(); // To be forwarded while
     * sending bracket order modify
     * // request
     *
     * String sMainOrderStatus = "", sSLOrderStatus = "", sProfitOrderStatus = "";
     * String sSquareoffStatus = "";
     * String sExch = "", sInst = "";
     *
     * String sBuyOrSell = "";
     *
     * for (int i = 0; i < brackerOrders.length(); i++) {
     *
     * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow)
     * brackerOrders.get(i);
     *
     * if (orderRow.getLegIndicator().equals(OrderConstants.MAIN_LEG_INDICATOR_9)) {
     *
     *//***
        * To fetch all the common fields for the order, main order leg is same as
        * normal order
        ***/
    /*
     *
     * sExch = orderRow.getExch();
     * sInst = orderRow.getInst();
     *
     *//*** To fetch main order details ***/
    /*
     * bracketOrderArr.put(getMainOrderDetails(orderRow, order));
     *
     * // TODO: API issue, temporary fix at line 529
     *
     * if (orderRow.getPosConvFlag().equals("1")) {
     *
     * order.put(OrderConstants.CONVERT_OPTION, "true");
     * modifyOrderDetailsObj.put(DeviceConstants.CONVERTABLE_TYPES,
     * ProductType.getConvertableProductTypes(ProductType.
     * FT_BRACKET_ORDER_FULL_TEXT,
     * ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst())));
     * } else order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *
     *//*** To display status based on the main leg order status ***/
    /*
     * sMainOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * sBuyOrSell = orderRow.getBuySell();
     * if (sMainOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
     * } else if (sMainOrderStatus.equals(OrderConstants.CANCELLED)) {
     *
     * bracketOrderArr.put(getSLOrderDetails(orderRow, true, order));
     * bracketOrderArr.put(getProfitOrderDetails(orderRow, true, order));
     *
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
     * legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     * } else if (sMainOrderStatus.equals(OrderConstants.PENDING)) { // If main leg
     * order is pending, still
     * // order is not pushed to the exchange
     *
     *//*** flag is to identify main order is pending or not ***/
    /*
     * bracketOrderArr.put(getSLOrderDetails(orderRow, true, order));
     * bracketOrderArr.put(getProfitOrderDetails(orderRow, true, order));
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
     * legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     *
     * If main order action is buy,then leg order action is sell *
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     *
     * }
     *
     * getOrderModifyFlags(orderRow.getBracketOrdModifyBit(),
     * modifyOrderDetailsObj);
     *
     *//*** Details needed for bracket order modification ***/
    /*
     * modifyOrderDetailsObj.put(OrderConstants.MAIN_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false));
     * modifyOrderDetailsObj.put(OrderConstants.BRACKET_ORDER_ID,
     * orderRow.getBracketOrdId());
     * modifyOrderDetailsObj.put(OrderConstants.TRAILING_SL,
     * orderRow.getLtpJumpPrc());
     * modifyOrderDetailsObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * modifyOrderDetailsObj.put(OrderConstants.SL_TRIG_PRICE,
     * orderRow.getSlTrigPrc());
     * modifyOrderDetailsObj.put(OrderConstants.PROFIT_PRICE,
     * orderRow.getProfitOrdPrc());
     * modifyOrderDetailsObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * modifyOrderDetailsObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * modifyOrderDetailsObj.put(OrderConstants.PENDING_QTY, orderRow.getQty());
     *
     * } else if
     * (orderRow.getLegIndicator().equals(OrderConstants.SL_LEG_INDICATOR_10)) {
     *
     *//***
        * To fetch Stop loss order details, flag is to identify main order is pending
        * or not
        ***/
    /*
     * getOrderDetails(orderRow, order);
     * order.put(OrderConstants.REASON, orderRow.getError());
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, false, order);
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
     * legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * OrderAction.formatToDevice(orderRow.getBuySell()));
     *
     * sSLOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * order.put(OrderConstants.STATUS, sSLOrderStatus);
     * if (sSLOrderStatus.equals(OrderConstants.EXECUTED)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_EXECUTED);
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_SL_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     *
     * }
     *
     * if (sSLOrderStatus.equals(OrderConstants.CANCELLED)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * cancelledLegOrderArr.put(slOrderObj);
     * } else
     * bracketOrderArr.put(slOrderObj);
     *
     * if (sSLOrderStatus.equals(OrderConstants.PENDING)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.SL_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false));
     * modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE,
     * Boolean.toString(getIsTrailingSLModifiable(orderRow.getLtpJumpPrc())));
     * }
     *
     * } else if
     * (orderRow.getLegIndicator().equals(OrderConstants.PROFIT_LEG_INDICATOR_11)) {
     *
     *//***
        * To fetch target order details, flag is to identify main order is pending or
        * not
        ***/
    /*
     * JSONObject targetOrderObj = getProfitOrderDetails(orderRow, false, order);
     *
     * sProfitOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sProfitOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE,
     * "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER,
     * OrderConstants.DISP_PROFIT_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * }
     *
     * if (sProfitOrderStatus.equals(OrderConstants.CANCELLED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE,
     * "false");
     * cancelledLegOrderArr.put(targetOrderObj);
     * } else
     * bracketOrderArr.put(targetOrderObj);
     *
     * if (sProfitOrderStatus.equals(OrderConstants.PENDING)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.TARGET_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false));
     * }
     * } else // If leg indicator is not any of the above case, then it is square
     * off order
     * {
     * sSquareoffStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER,
     * OrderConstants.DISP_SQUAREOFF_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * }
     *
     * if (sSquareoffStatus.equals(OrderConstants.CANCELLED)) {
     * cancelledLegOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     * } else
     * bracketOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     *
     * // bracketOrderArr.put(getSquareoffOrderDetails(orderRow));
     *
     * }
     *
     * } // End of for loop
     *
     *//***
        * If any one of the orders is pending, then user can modify or cancel the order
        ***/
    /*
     * if (sMainOrderStatus.equals(OrderConstants.PENDING) ||
     * sSLOrderStatus.equals(OrderConstants.PENDING)
     * || sProfitOrderStatus.equals(OrderConstants.PENDING))
     * // modify object is needed only when user can modify or cancel the order
     * bracketOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS,
     * modifyOrderDetailsObj);
     * else
     * bracketOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, new JSONObject());
     *
     * order.put(OrderConstants.IS_MODIFIABLE, Boolean
     * .toString(OrderStatus.isBracketOrderModifiable_101(sMainOrderStatus,
     * sSLOrderStatus, sProfitOrderStatus,
     * OrderConstants.IS_BRACKET_STOPLOSS_ORDER)));
     * order.put(OrderConstants.IS_CANCELLABLE, Boolean
     * .toString(OrderStatus.isBracketOrderModifyOrCancellable(sSLOrderStatus,
     * sMainOrderStatus,
     * sProfitOrderStatus)));
     * order.put(OrderConstants.EXIT_OPTION, Boolean
     * .toString(OrderStatus.getBracketOrderExitOption_101(sMainOrderStatus,
     * sSLOrderStatus, false)));
     *
     * // Temporary fix
     * order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *//*** To show, leg order short description status ***/
    /*
     * if (sSLOrderStatus.equals(OrderConstants.EXECUTED) ||
     * sProfitOrderStatus.equals(OrderConstants.EXECUTED)
     * || sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
     *
     *//*** For executed order, to show only price and qty ***/
    /*
     * legOrderObj.remove(OrderConstants.PROFIT_PRICE);
     * legOrderObj.remove(OrderConstants.SL_PRICE);
     * legOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * legOrderObj.remove(OrderConstants.TRAILING_SL);
     * }
     * legOrderObj.put(OrderConstants.IS_EXECUTED, Boolean
     * .toString(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus,
     * sProfitOrderStatus, sSquareoffStatus)));
     * legOrderObj.put(OrderConstants.DISP_STATUS,
     * OrderStatus.getLegOrderDisplayStatus(sMainOrderStatus,
     * sSLOrderStatus, sProfitOrderStatus, sSquareoffStatus));
     * OrderStatus.getBuyOrSellMoreFlags_101(sMainOrderStatus, sSLOrderStatus,
     * sProfitOrderStatus, sSquareoffStatus,
     * sBuyOrSell, order, OrderConstants.IS_BRACKET_STOPLOSS_ORDER);
     *
     *//*** To identify, any one of the leg orders cancelled ***/
    /*
     * bracketOrdObj.put(OrderConstants.IS_LEG_ORDER_CANCELLED,
     * Boolean.toString(OrderStatus.isLegOrderCancelled(sSLOrderStatus,
     * sProfitOrderStatus)));
     *
     * bracketOrdObj.put(OrderConstants.LEG_ORDER_SHORT_DESC, legOrderObj);
     * bracketOrdObj.put(OrderConstants.LEG_ORDER_DETAILS, bracketOrderArr);
     * bracketOrdObj.put(OrderConstants.CANCELLED_ORDER_DETAILS,
     * cancelledLegOrderArr);
     *
     * return bracketOrdObj;
     * }
     *
     *//*** Parse group of bracket orders into single order ***/
    /*
     * private static JSONObject getBracketProfitOrderDetails(JSONArray
     * brackerOrders, SymbolRow order)
     * throws JSONException, Exception {
     *
     * JSONObject bracketOrdObj = new JSONObject(); // Final Object contains bracket
     * order details
     * JSONObject legOrderObj = new JSONObject(); // Leg order short detail
     * JSONArray bracketOrderArr = new JSONArray(); // Array of pending and executed
     * order details
     * JSONArray cancelledLegOrderArr = new JSONArray(); // Array of cancelled order
     * details
     * JSONObject modifyOrderDetailsObj = new JSONObject(); // To be forwarded while
     * sending bracket order modify
     * // request
     *
     * String sMainOrderStatus = "", sSLOrderStatus = "", sProfitOrderStatus = "";
     * String sSquareoffStatus = "";
     * String sExch = "", sInst = "";
     *
     * String sBuyOrSell = "";
     *
     * for (int i = 0; i < brackerOrders.length(); i++) {
     *
     * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow)
     * brackerOrders.get(i);
     *
     * if (orderRow.getLegIndicator().equals(OrderConstants.MAIN_LEG_INDICATOR_9)) {
     *
     *//***
        * To fetch all the common fields for the order, main order leg is same as
        * normal order
        ***/
    /*
     * sExch = orderRow.getExch();
     * sInst = orderRow.getInst();
     *
     *//*** To fetch main order details ***/
    /*
     * bracketOrderArr.put(getMainOrderDetails(orderRow, order));
     *
     * // TODO: API issue, temporary fix at line 529
     *
     * if (orderRow.getPosConvFlag().equals("1")) {
     *
     * order.put(OrderConstants.CONVERT_OPTION, "true");
     * modifyOrderDetailsObj.put(DeviceConstants.CONVERTABLE_TYPES,
     * ProductType.getConvertableProductTypes(ProductType.
     * FT_BRACKET_ORDER_FULL_TEXT,
     * ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst())));
     * } else order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *
     *//*** To display status based on the main leg order status ***/
    /*
     * sMainOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * sBuyOrSell = orderRow.getBuySell();
     * if (sMainOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
     * } else if (sMainOrderStatus.equals(OrderConstants.CANCELLED)) {
     *
     * bracketOrderArr.put(getSLOrderDetails(orderRow, true, order));
     * bracketOrderArr.put(getProfitOrderDetails(orderRow, true, order));
     *
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
     * legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     * } else if (sMainOrderStatus.equals(OrderConstants.PENDING)) { // If main leg
     * order is pending, still
     * // order is not pushed to the exchange
     *
     *//*** flag is to identify main order is pending or not ***/
    /*
     * bracketOrderArr.put(getSLOrderDetails(orderRow, true, order));
     * bracketOrderArr.put(getProfitOrderDetails(orderRow, true, order));
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
     * legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     *
     * If main order action is buy,then leg order action is sell *
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     *
     * }
     *
     * getOrderModifyFlags(orderRow.getBracketOrdModifyBit(),
     * modifyOrderDetailsObj);
     *
     *//*** Details needed for bracket order modification ***/
    /*
     * modifyOrderDetailsObj.put(OrderConstants.MAIN_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false));
     * modifyOrderDetailsObj.put(OrderConstants.BRACKET_ORDER_ID,
     * orderRow.getBracketOrdId());
     * modifyOrderDetailsObj.put(OrderConstants.TRAILING_SL,
     * orderRow.getLtpJumpPrc());
     * modifyOrderDetailsObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * modifyOrderDetailsObj.put(OrderConstants.SL_TRIG_PRICE,
     * orderRow.getSlTrigPrc());
     * modifyOrderDetailsObj.put(OrderConstants.PROFIT_PRICE,
     * orderRow.getProfitOrdPrc());
     * modifyOrderDetailsObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * modifyOrderDetailsObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * modifyOrderDetailsObj.put(OrderConstants.PENDING_QTY, orderRow.getQty());
     *
     * } else if
     * (orderRow.getLegIndicator().equals(OrderConstants.SL_LEG_INDICATOR_10)) {
     *
     *//***
        * To fetch Stop loss order details, flag is to identify main order is pending
        * or not
        ***/
    /*
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, false, order);
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
     * legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * OrderAction.formatToDevice(orderRow.getBuySell()));
     *
     * sSLOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sSLOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_SL_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     *
     * }
     *
     * if (sSLOrderStatus.equals(OrderConstants.CANCELLED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * cancelledLegOrderArr.put(slOrderObj);
     * } else
     * bracketOrderArr.put(slOrderObj);
     *
     * if (sSLOrderStatus.equals(OrderConstants.PENDING)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.SL_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false));
     * modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE,
     * Boolean.toString(getIsTrailingSLModifiable(orderRow.getLtpJumpPrc())));
     * }
     *
     * } else if
     * (orderRow.getLegIndicator().equals(OrderConstants.PROFIT_LEG_INDICATOR_11)) {
     *
     *//***
        * To fetch target order details, flag is to identify main order is pending or
        * not
        ***/
    /*
     * JSONObject targetOrderObj = getProfitOrderDetails(orderRow, false, order);
     * getOrderDetails(orderRow, order);
     *
     * sProfitOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * order.put(OrderConstants.STATUS, sProfitOrderStatus);
     * if (sProfitOrderStatus.equals(OrderConstants.EXECUTED)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_EXECUTED);
     * modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE,
     * "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER,
     * OrderConstants.DISP_PROFIT_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * }
     *
     * if (sProfitOrderStatus.equals(OrderConstants.CANCELLED)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE,
     * "false");
     * cancelledLegOrderArr.put(targetOrderObj);
     * } else
     * bracketOrderArr.put(targetOrderObj);
     *
     * if (sProfitOrderStatus.equals(OrderConstants.PENDING)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.TARGET_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false));
     * }
     *
     * order.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sProfitOrderStatus,
     * orderRow.getError()));
     * } else // If leg indicator is not any of the above case, then it is square
     * off order
     * {
     * sSquareoffStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER,
     * OrderConstants.DISP_SQUAREOFF_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * }
     *
     * if (sSquareoffStatus.equals(OrderConstants.CANCELLED)) {
     * cancelledLegOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     * } else
     * bracketOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     *
     * // bracketOrderArr.put(getSquareoffOrderDetails(orderRow));
     *
     * }
     *
     * } // End of for loop
     *
     *//***
        * If any one of the orders is pending, then user can modify or cancel the order
        ***/
    /*
     * if (sMainOrderStatus.equals(OrderConstants.PENDING) ||
     * sSLOrderStatus.equals(OrderConstants.PENDING)
     * || sProfitOrderStatus.equals(OrderConstants.PENDING))
     * // modify object is needed only when user can modify or cancel the order
     * bracketOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS,
     * modifyOrderDetailsObj);
     * else
     * bracketOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, new JSONObject());
     *
     * order.put(OrderConstants.IS_MODIFIABLE, Boolean
     * .toString(OrderStatus.isBracketOrderModifiable_101(sMainOrderStatus,
     * sSLOrderStatus, sProfitOrderStatus,
     * OrderConstants.IS_BRACKET_PROFIT_ORDER)));
     * order.put(OrderConstants.IS_CANCELLABLE, Boolean
     * .toString(OrderStatus.isBracketOrderModifyOrCancellable(sProfitOrderStatus,
     * sMainOrderStatus,
     * sSLOrderStatus)));
     * order.put(OrderConstants.EXIT_OPTION, Boolean
     * .toString(OrderStatus.getBracketOrderExitOption_101(sMainOrderStatus,
     * sProfitOrderStatus, false)));
     *
     * // Temporary fix
     * order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *//*** To show, leg order short description status ***/
    /*
     * if (sSLOrderStatus.equals(OrderConstants.EXECUTED) ||
     * sProfitOrderStatus.equals(OrderConstants.EXECUTED)
     * || sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
     *
     *//*** For executed order, to show only price and qty ***/
    /*
     * legOrderObj.remove(OrderConstants.PROFIT_PRICE);
     * legOrderObj.remove(OrderConstants.SL_PRICE);
     * legOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * legOrderObj.remove(OrderConstants.TRAILING_SL);
     * }
     * legOrderObj.put(OrderConstants.IS_EXECUTED, Boolean
     * .toString(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus,
     * sProfitOrderStatus, sSquareoffStatus)));
     * legOrderObj.put(OrderConstants.DISP_STATUS,
     * OrderStatus.getLegOrderDisplayStatus(sMainOrderStatus,
     * sSLOrderStatus, sProfitOrderStatus, sSquareoffStatus));
     *
     * OrderStatus.getBuyOrSellMoreFlags_101(sMainOrderStatus, sSLOrderStatus,
     * sProfitOrderStatus, sSquareoffStatus,
     * sBuyOrSell, order, OrderConstants.IS_BRACKET_PROFIT_ORDER);
     *
     *//*** To identify, any one of the leg orders cancelled ***/
    /*
     * bracketOrdObj.put(OrderConstants.IS_LEG_ORDER_CANCELLED,
     * Boolean.toString(OrderStatus.isLegOrderCancelled(sSLOrderStatus,
     * sProfitOrderStatus)));
     *
     * bracketOrdObj.put(OrderConstants.LEG_ORDER_SHORT_DESC, legOrderObj);
     * bracketOrdObj.put(OrderConstants.LEG_ORDER_DETAILS, bracketOrderArr);
     * bracketOrdObj.put(OrderConstants.CANCELLED_ORDER_DETAILS,
     * cancelledLegOrderArr);
     *
     * return bracketOrdObj;
     * }
     *
     * private static JSONObject getBracketSquareOffOrderDetails(JSONArray
     * brackerOrders, SymbolRow order)
     * throws JSONException, Exception {
     *
     * JSONObject bracketOrdObj = new JSONObject(); // Final Object contains bracket
     * order details
     * JSONObject legOrderObj = new JSONObject(); // Leg order short detail
     * JSONArray bracketOrderArr = new JSONArray(); // Array of pending and executed
     * order details
     * JSONArray cancelledLegOrderArr = new JSONArray(); // Array of cancelled order
     * details
     * JSONObject modifyOrderDetailsObj = new JSONObject(); // To be forwarded while
     * sending bracket order modify
     * // request
     *
     * String sMainOrderStatus = "", sSLOrderStatus = "", sProfitOrderStatus = "";
     * String sSquareoffStatus = "";
     * String sExch = "", sInst = "";
     *
     * String sBuyOrSell = "";
     *
     * for (int i = 0; i < brackerOrders.length(); i++) {
     *
     * GetOrderBookObjectRow orderRow = (GetOrderBookObjectRow)
     * brackerOrders.get(i);
     *
     * if (orderRow.getLegIndicator().equals(OrderConstants.MAIN_LEG_INDICATOR_9)) {
     *
     *//***
        * To fetch all the common fields for the order, main order leg is same as
        * normal order
        ***/
    /*
     * sExch = orderRow.getExch();
     * sInst = orderRow.getInst();
     *
     *//*** To fetch main order details ***/
    /*
     * bracketOrderArr.put(getMainOrderDetails(orderRow, order));
     *
     * // TODO: API issue, temporary fix at line 529
     *
     * if (orderRow.getPosConvFlag().equals("1")) {
     *
     * order.put(OrderConstants.CONVERT_OPTION, "true");
     * modifyOrderDetailsObj.put(DeviceConstants.CONVERTABLE_TYPES,
     * ProductType.getConvertableProductTypes(ProductType.
     * FT_BRACKET_ORDER_FULL_TEXT,
     * ExchangeSegment.getMarketSegmentID(orderRow.getExch(), orderRow.getInst())));
     * } else order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *
     *//*** To display status based on the main leg order status ***/
    /*
     * sMainOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * sBuyOrSell = orderRow.getBuySell();
     * if (sMainOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_MAIN_ORDER_MODIFIABLE, "false");
     * } else if (sMainOrderStatus.equals(OrderConstants.CANCELLED)) {
     *
     * bracketOrderArr.put(getSLOrderDetails(orderRow, true, order));
     * bracketOrderArr.put(getProfitOrderDetails(orderRow, true, order));
     *
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
     * legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     * } else if (sMainOrderStatus.equals(OrderConstants.PENDING)) { // If main leg
     * order is pending, still
     * // order is not pushed to the exchange
     *
     *//*** flag is to identify main order is pending or not ***/
    /*
     * bracketOrderArr.put(getSLOrderDetails(orderRow, true, order));
     * bracketOrderArr.put(getProfitOrderDetails(orderRow, true, order));
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
     * legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
     * legOrderObj.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_PENDING);
     * legOrderObj.put(OrderConstants.IS_EXECUTED, "false");
     *
     * If main order action is buy,then leg order action is sell *
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * getLegOrderAction(orderRow.getBuySell()));
     *
     * }
     *
     * getOrderModifyFlags(orderRow.getBracketOrdModifyBit(),
     * modifyOrderDetailsObj);
     *
     *//*** Details needed for bracket order modification ***/
    /*
     * modifyOrderDetailsObj.put(OrderConstants.MAIN_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false));
     * modifyOrderDetailsObj.put(OrderConstants.BRACKET_ORDER_ID,
     * orderRow.getBracketOrdId());
     * modifyOrderDetailsObj.put(OrderConstants.TRAILING_SL,
     * orderRow.getLtpJumpPrc());
     * modifyOrderDetailsObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * modifyOrderDetailsObj.put(OrderConstants.SL_TRIG_PRICE,
     * orderRow.getSlTrigPrc());
     * modifyOrderDetailsObj.put(OrderConstants.PROFIT_PRICE,
     * orderRow.getProfitOrdPrc());
     * modifyOrderDetailsObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * modifyOrderDetailsObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * modifyOrderDetailsObj.put(OrderConstants.PENDING_QTY, orderRow.getQty());
     *
     * } else if
     * (orderRow.getLegIndicator().equals(OrderConstants.SL_LEG_INDICATOR_10)) {
     *
     *//***
        * To fetch Stop loss order details, flag is to identify main order is pending
        * or not
        ***/
    /*
     * JSONObject slOrderObj = getSLOrderDetails(orderRow, false, order);
     *
     *//*** Leg order short description ***/
    /*
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.SL_PRICE, orderRow.getSlOrdPrc());
     * legOrderObj.put(OrderConstants.SL_TRIG_PRICE, orderRow.getSlTrigPrc());
     * legOrderObj.put(OrderConstants.TRAILING_SL, orderRow.getLtpJumpPrc());
     * legOrderObj.put(OrderConstants.ORDER_ACTION,
     * OrderAction.formatToDevice(orderRow.getBuySell()));
     *
     * sSLOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sSLOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER, OrderConstants.DISP_SL_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     *
     * }
     *
     * if (sSLOrderStatus.equals(OrderConstants.CANCELLED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "false");
     * cancelledLegOrderArr.put(slOrderObj);
     * } else
     * bracketOrderArr.put(slOrderObj);
     *
     * if (sSLOrderStatus.equals(OrderConstants.PENDING)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_SL_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.SL_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false));
     * modifyOrderDetailsObj.put(OrderConstants.IS_TRAILING_SL_MODIFIABLE,
     * Boolean.toString(getIsTrailingSLModifiable(orderRow.getLtpJumpPrc())));
     * }
     *
     * } else if
     * (orderRow.getLegIndicator().equals(OrderConstants.PROFIT_LEG_INDICATOR_11)) {
     *
     *//***
        * To fetch target order details, flag is to identify main order is pending or
        * not
        ***/
    /*
     * JSONObject targetOrderObj = getProfitOrderDetails(orderRow, false, order);
     *
     * sProfitOrderStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * if (sProfitOrderStatus.equals(OrderConstants.EXECUTED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE,
     * "false");
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER,
     * OrderConstants.DISP_PROFIT_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PROFIT_PRICE, orderRow.getProfitOrdPrc());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * }
     *
     * if (sProfitOrderStatus.equals(OrderConstants.CANCELLED)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE,
     * "false");
     * cancelledLegOrderArr.put(targetOrderObj);
     * } else
     * bracketOrderArr.put(targetOrderObj);
     *
     * if (sProfitOrderStatus.equals(OrderConstants.PENDING)) {
     * modifyOrderDetailsObj.put(OrderConstants.IS_TARGET_ORDER_MODIFIABLE, "true");
     * modifyOrderDetailsObj.put(OrderConstants.TARGET_ORDER_MODIFY,
     * getModifyOrderDetails(orderRow, false));
     * }
     *
     * } else // If leg indicator is not any of the above case, then it is square
     * off order
     * {
     * getOrderDetails(orderRow, order);
     * sSquareoffStatus = OrderStatus.getStatus(orderRow.getOrdStat());
     * order.put(OrderConstants.REASON, OrderStatus.getErrorMsg(sSquareoffStatus,
     * orderRow.getError()));
     * order.put(OrderConstants.STATUS, sSquareoffStatus);
     * if (sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
     * legOrderObj.put(OrderConstants.EXECUTED_ORDER,
     * OrderConstants.DISP_SQUAREOFF_ORDER);
     * legOrderObj.put(OrderConstants.ORDER_QTY, orderRow.getQty());
     * legOrderObj.put(OrderConstants.PENDING_QTY, orderRow.getPendQty());
     * legOrderObj.put(OrderConstants.PRICE, orderRow.getPrc());
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_EXECUTED);
     * }
     *
     * if (sSquareoffStatus.equals(OrderConstants.CANCELLED)) {
     * order.put(OrderConstants.DISP_STATUS, OrderConstants.DISP_CANCELLED);
     * cancelledLegOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     * } else
     * bracketOrderArr.put(getSquareoffOrderDetails(orderRow, order));
     *
     * // bracketOrderArr.put(getSquareoffOrderDetails(orderRow));
     *
     * }
     *
     * } // End of for loop
     *
     *//***
        * If any one of the orders is pending, then user can modify or cancel the order
        ***/
    /*
     * if (sMainOrderStatus.equals(OrderConstants.PENDING) ||
     * sSLOrderStatus.equals(OrderConstants.PENDING)
     * || sProfitOrderStatus.equals(OrderConstants.PENDING))
     * // modify object is needed only when user can modify or cancel the order
     * bracketOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS,
     * modifyOrderDetailsObj);
     * else
     * bracketOrdObj.put(OrderConstants.MODIFY_ORDER_DETAILS, new JSONObject());
     *
     * order.put(OrderConstants.IS_MODIFIABLE, Boolean
     * .toString(OrderStatus.isBracketOrderModifiable_101(sMainOrderStatus,
     * sSLOrderStatus, sProfitOrderStatus,
     * OrderConstants.IS_BRACKET_SQUAREOFF_ORDER)));
     * order.put(OrderConstants.IS_CANCELLABLE, Boolean
     * .toString(false));//
     * OrderStatus.isBracketOrderModifyOrCancellable(sSquareoffStatus,
     * // sSLOrderStatus, sProfitOrderStatus)));
     * order.put(OrderConstants.EXIT_OPTION, Boolean
     * .toString(OrderStatus.getBracketOrderExitOption_101(sMainOrderStatus,
     * sSquareoffStatus, false)));
     *
     * // Temporary fix
     * order.put(OrderConstants.CONVERT_OPTION, "false");
     *
     *//*** To show, leg order short description status ***/
    /*
     * if (sSLOrderStatus.equals(OrderConstants.EXECUTED) ||
     * sProfitOrderStatus.equals(OrderConstants.EXECUTED)
     * || sSquareoffStatus.equals(OrderConstants.EXECUTED)) {
     *
     *//*** For executed order, to show only price and qty ***/
    /*
     * legOrderObj.remove(OrderConstants.PROFIT_PRICE);
     * legOrderObj.remove(OrderConstants.SL_PRICE);
     * legOrderObj.remove(OrderConstants.SL_TRIG_PRICE);
     * legOrderObj.remove(OrderConstants.TRAILING_SL);
     * }
     * legOrderObj.put(OrderConstants.IS_EXECUTED, Boolean
     * .toString(OrderStatus.checkAnyLegOrderExecuted(sSLOrderStatus,
     * sProfitOrderStatus, sSquareoffStatus)));
     * legOrderObj.put(OrderConstants.DISP_STATUS,
     * OrderStatus.getLegOrderDisplayStatus(sMainOrderStatus,
     * sSLOrderStatus, sProfitOrderStatus, sSquareoffStatus));
     *
     * OrderStatus.getBuyOrSellMoreFlags_101(sMainOrderStatus, sSLOrderStatus,
     * sProfitOrderStatus, sSquareoffStatus,
     * sBuyOrSell, order, OrderConstants.IS_BRACKET_SQUAREOFF_ORDER);
     *
     *//*** To identify, any one of the leg orders cancelled ***//*
                                                                  * bracketOrdObj.put(OrderConstants.
                                                                  * IS_LEG_ORDER_CANCELLED,
                                                                  * Boolean.toString(OrderStatus.isLegOrderCancelled(
                                                                  * sSLOrderStatus, sProfitOrderStatus)));
                                                                  *
                                                                  * bracketOrdObj.put(OrderConstants.
                                                                  * LEG_ORDER_SHORT_DESC, legOrderObj);
                                                                  * bracketOrdObj.put(OrderConstants.LEG_ORDER_DETAILS,
                                                                  * bracketOrderArr);
                                                                  * bracketOrdObj.put(OrderConstants.
                                                                  * CANCELLED_ORDER_DETAILS, cancelledLegOrderArr);
                                                                  *
                                                                  * return bracketOrdObj;
                                                                  * }
                                                                  */

}
