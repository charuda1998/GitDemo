package com.globecapital.business.chart;

import java.sql.Timestamp;

public class ChartData {
	

		double open, high, low, close;
		long volume;
		Timestamp timestamp;

		public double getOpen() {
			return open;
		}

		public void setOpen(double open) {
			this.open = open;
		}

		public double getHigh() {
			return high;
		}

		public void setHigh(double high) {
			this.high = high;
		}

		public double getLow() {
			return low;
		}

		public void setLow(double low) {
			this.low = low;
		}

		public double getClose() {
			return close;
		}

		public void setClose(double close) {
			this.close = close;
		}

		public long getVolume() {
			return volume;
		}

		public void setVolume(long volume) {
			this.volume = volume;
		}

		public Timestamp getTimestamp() {
			return timestamp;
		}

		public void setTimestamp(Timestamp timestamp) {
			this.timestamp = timestamp;
		}
		
		@Override
		public String toString(){
			
			return "OPEN " + this.getOpen() + " HIGH " + this.getHigh() + " LOW " + this.getLow()  + " CLOSE " +  this.getClose() + " VOLUME " +  this.getVolume()+ "TIME" + this.getTimestamp().toLocaleString() ;
		}

}
