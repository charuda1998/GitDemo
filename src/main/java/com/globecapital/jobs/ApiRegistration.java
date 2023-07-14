package com.globecapital.jobs;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.globecapital.constants.DBQueryConstants;
import com.globecapital.db.GCDBPool;
import com.msf.log.Logger;
import com.msf.utils.helper.Helper;

class ApiRegistration {
	@SuppressWarnings("resource")
	public void checkConnection() {

		Logger log = Logger.getLogger(ApiRegistration.class);
		Date date=new Date(System.currentTimeMillis());

		String lastTime="";
		Connection conn = null;
		PreparedStatement st = null;
		try {
			System.out.println("Inside APIRegistration");
			log.info("Inside APIRegistration class");
			conn = GCDBPool.getInstance().getConnection();
//			System.out.println("date inside APIRegistration :");
//			System.out.println(date);
			st = conn.prepareStatement(DBQueryConstants.IS_TABLE_EMPTY);
			ResultSet rs = st.executeQuery();
			int check_val=-1;
			while(rs.next()) {
				 check_val=rs.getInt("IsEmpty");
			}
			if(check_val==1) {
				System.out.println("DB is  empty");
				log.info("DB is  empty");
				OmexApiCall.callApi();
			}
			else if(check_val==0){
				System.out.println("DB is not empty");
				log.info("DB is not empty");
			
			st = conn.prepareStatement(DBQueryConstants.GET_LAST_UPDATE_TIME);
			st.setDate(1, date);
			ResultSet rs1 = st.executeQuery();
			
			while(rs1.next()) {
				 lastTime=rs1.getString("LAST_UPDATE_TIME");
			}
			long val=Long.parseLong(lastTime);  
			System.out.println(lastTime);
			log.info(lastTime);
			
			//last updated time in db is greater than 3 min
			log.info("current time and db time difference");
			log.info(System.currentTimeMillis()-val>180000);
			if(System.currentTimeMillis()-val>180000) {
				OmexApiCall.callApi();
			} 
		}
			
		} catch (SQLException e) {
			
			e.printStackTrace();
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		} finally {
			Helper.closeStatement(st);
			Helper.closeConnection(conn);
		}
		
	}
}
