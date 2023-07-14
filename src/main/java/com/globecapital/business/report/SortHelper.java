package com.globecapital.business.report;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.globecapital.constants.DeviceConstants;
import com.msf.log.Logger;

public class SortHelper {

	private static Logger log = Logger.getLogger(SortHelper.class);
	
	public static JSONArray sortByInteger(JSONArray toBeSortedArray, final String sortOrder,  final String filter) {
		JSONArray sorted;
		List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
		for (int i = 0; i < toBeSortedArray.length(); i++)
			toBeSorted.add(toBeSortedArray.getJSONObject(i));

		Collections.sort(toBeSorted, new Comparator<JSONObject>() {

			@Override
			public int compare(JSONObject obj1, JSONObject obj2) {

				if (sortOrder.equalsIgnoreCase(DeviceConstants.ASCENDING))
					return Integer.parseInt(obj1.getString(filter))
							- (Integer.parseInt(obj2.getString(filter)));
				else
					return Integer.parseInt(obj2.getString(filter))
							- (Integer.parseInt(obj1.getString(filter)));
			}
		});
		sorted = new JSONArray(toBeSorted);
		return sorted;
	}
	
	public static JSONArray sortByInteger(JSONArray toBeSortedArray, final String sortOrder,  final String filter, final String replaceChar) {
		JSONArray sorted;
		List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
		for (int i = 0; i < toBeSortedArray.length(); i++)
			toBeSorted.add(toBeSortedArray.getJSONObject(i));

		Collections.sort(toBeSorted, new Comparator<JSONObject>() {

			@Override
			public int compare(JSONObject obj1, JSONObject obj2) {

				if (sortOrder.equalsIgnoreCase(DeviceConstants.ASCENDING))
					return Integer.parseInt(obj1.getString(filter).replace(replaceChar, ""))
							- (Integer.parseInt(obj2.getString(filter).replace(replaceChar, "")));
				else
					return Integer.parseInt(obj2.getString(filter).replace(replaceChar, ""))
							- (Integer.parseInt(obj1.getString(filter).replace(replaceChar, "")));
			}
		});
		sorted = new JSONArray(toBeSorted);
		return sorted;
	}
	
	public static JSONArray sortBySymbol(JSONArray toBeSortedArray, final String sortOrder) {
		JSONArray sorted;
		List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
		for (int i = 0; i < toBeSortedArray.length(); i++)
			toBeSorted.add(toBeSortedArray.getJSONObject(i));

		Collections.sort(toBeSorted, new Comparator<JSONObject>() {

			@Override
			public int compare(JSONObject obj1, JSONObject obj2) {

				if (sortOrder.equalsIgnoreCase(DeviceConstants.ASCENDING)||sortOrder.isEmpty() || sortOrder.contains(DeviceConstants.DEFAULT_ORDER))
					return obj1.getString(DeviceConstants.SYMBOL).compareTo(obj2.getString(DeviceConstants.SYMBOL));
				else
					return obj2.getString(DeviceConstants.SYMBOL).compareTo(obj1.getString(DeviceConstants.SYMBOL));

			}
		});

		sorted = new JSONArray(toBeSorted);
		return sorted;
	}
	
	public static JSONArray sortByDate(JSONArray recordArray, String sortOrder , final String sortBy ) {
		JSONArray sorted = new JSONArray();
		final SimpleDateFormat format = new SimpleDateFormat(DeviceConstants.DATE_FORMAT_FILTER);
		List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
		try {
			for (int i = recordArray.length()-1; i >=0; i--) 
				toBeSorted.add(recordArray.getJSONObject(i));
			
			Collections.sort(toBeSorted, new Comparator<JSONObject>() {

				@Override
				public int compare(JSONObject obj1, JSONObject obj2) {
					try {
						return format.parse(obj2.getString(sortBy)).compareTo(format.parse(obj1.getString(sortBy)))>0?1:0;
					} catch (JSONException | ParseException e) {
						log.error(e.getMessage());
						e.printStackTrace();
					}
					return 0;
				}
			});
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if (sortOrder.equalsIgnoreCase(DeviceConstants.ASCENDING))
			sorted = new JSONArray(toBeSorted);
		else {
			Collections.reverse(toBeSorted);
			sorted = new JSONArray(toBeSorted);
		}
		return sorted;
	}
	
	public static JSONArray sortByTransDate(JSONArray recordArray, String sortOrder , final String sortBy ) {
        JSONArray sorted = new JSONArray();
        List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
        try {
            for (int i = recordArray.length()-1; i >=0; i--) 
                toBeSorted.add(recordArray.getJSONObject(i));
            
            Collections.sort(toBeSorted, new Comparator<JSONObject>() {
                final SimpleDateFormat format = new java.text.SimpleDateFormat(DeviceConstants.DATE_FORMAT_FILTER);
                @Override
                public int compare(JSONObject obj1, JSONObject obj2) {
                    int result = -1;
                    try {
                        return format.parse(obj2.getString(sortBy)).compareTo(format.parse(obj1.getString(sortBy)));
                    } catch (JSONException | ParseException e) {
                        log.error(e.getMessage());
                        e.printStackTrace();
                    }
                    return result;
                }
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        if (sortOrder.equalsIgnoreCase(DeviceConstants.DESCENDING))
            sorted = new JSONArray(toBeSorted);
        else {
            Collections.reverse(toBeSorted);
            sorted = new JSONArray(toBeSorted);
        }
        return sorted;
    }
	
	public static List<JSONObject> sortByCreditDebit(JSONArray ledgerArray , String sortBy, String key,String replaceChar) {
		List<JSONObject> toBeSorted = new ArrayList<JSONObject>();
		for (int i = 0; i < ledgerArray.length(); i++) {
			if(ledgerArray.getJSONObject(i).getString(DeviceConstants.TYPE).equalsIgnoreCase(sortBy))
				toBeSorted.add(ledgerArray.getJSONObject(i));
		}	
		sortByDouble(key, toBeSorted,replaceChar);
		return toBeSorted;
	}

	public static void sortByDouble(final String key, List<JSONObject> toBeSorted, final String replaceChar) {
		Collections.sort(toBeSorted, new Comparator<JSONObject>() {

			@Override
			public int compare(JSONObject obj1, JSONObject obj2) {
				if( Double.parseDouble(obj1.getString(key).replaceAll(replaceChar, ""))
						< (Double.parseDouble(obj2.getString(key).replaceAll(replaceChar, "")))) {
					return -1;
				}
				if( Double.parseDouble(obj1.getString(key).replaceAll(replaceChar, ""))
					> (Double.parseDouble(obj2.getString(key).replaceAll(replaceChar, "")))) {
				return 1;
				}
				return 0;
			}
		});
	}
	
	public static void sortByDouble(final String key, List<JSONObject> toBeSorted) {
	Collections.sort(toBeSorted, new Comparator<JSONObject>() {
		@Override
		public int compare(JSONObject obj1, JSONObject obj2) {
			if( Double.parseDouble(obj1.getString(key))
					< (Double.parseDouble(obj2.getString(key)))) {
				return -1;
			}
			if( Double.parseDouble(obj1.getString(key))
				> (Double.parseDouble(obj2.getString(key)))) {
				return 1;
			}
			return 0;
		}
	});
	}	
			
}
