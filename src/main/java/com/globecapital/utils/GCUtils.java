package com.globecapital.utils;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.servlet.ServletContext;

import org.apache.commons.codec.binary.Base64;
import com.globecapital.api.ft.generics.FTRequest;
import com.globecapital.api.ft.user.LoginResponse;
import com.globecapital.audit.GCAuditObject;
import com.globecapital.business.user.AdvanceLogin;
import com.globecapital.business.user.Login;
import com.globecapital.config.AppConfig;
import com.globecapital.security.AESEncryption;
import com.globecapital.services.common.GCRequest;
import com.globecapital.services.common.GCResponse;
import com.globecapital.services.exception.GCException;
import com.globecapital.services.session.Session;
import com.globecapital.services.session.SessionHelper;
import com.msf.log.Logger;

public class GCUtils {
    private static Logger log = Logger.getLogger(GCUtils.class);

    public static String encryptPassword(String sPassword) throws GCException {

        try {
            String modulusString = AppConfig.getValue("moon.login.encryption.modulus");
            String publicExponentString = AppConfig.getValue("moon.login.encryption.exponent");
            byte[] modulusBytes = Base64.decodeBase64(modulusString.getBytes());
            byte[] exponentBytes = Base64.decodeBase64(publicExponentString.getBytes());
            BigInteger modulus = new BigInteger(1, modulusBytes);
            BigInteger publicExponent = new BigInteger(1, exponentBytes);

            RSAPublicKeySpec rsaPubKey = new RSAPublicKeySpec(modulus, publicExponent);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            PublicKey pubKey = fact.generatePublic(rsaPubKey);
            Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA1AndMGF1Padding");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);

            byte[] plainBytes = sPassword.getBytes("UTF-8");
            byte[] cipherData = cipher.doFinal(plainBytes);
            byte[] encryptedStringBase64 = Base64.encodeBase64(cipherData);

            return new String(encryptedStringBase64);

        } catch (Exception e) {
            e.printStackTrace();
            throw new GCException("Not able to encrypt " + sPassword);

        }

    }

    public static String getFinancialYear(String sFinancialYear) {

        String[] financialYear = sFinancialYear.split(" ");
        String sYear = financialYear[2];

        String year[] = sYear.split("-");

        return year[0].substring(year[0].length() - 2) + year[1].substring(year[1].length() - 2);
    }

    public static void main(String args[]) {

        String sFinancialYear = "Financial Year 2019-2020";
        System.out.println(getFinancialYear(sFinancialYear));

    }

//    public static boolean reInitiateLogInBase(FTRequest ftRequest, Session session, ServletContext servletContext,
//            GCRequest gcRequest, GCResponse gcResponse) throws Exception {
//        GCAuditObject auditObj = gcRequest.getAuditObj();
//        try {
//            String password = AdvanceLogin.getEncryptedPwd(AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"), 
//                    session.getUserID()),session.getAppID());
//            log.info("user ID :"+session.getUserID()+" AppID :"+gcRequest.getAppID()+"API session expired");
//            LoginResponse loginResponse = Login.verify_login_101(session.getUserID(),AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"), 
//                    password), "", gcRequest.getClientIP(), gcRequest.getAppID());
//            if (loginResponse.isLogonSucccess()) {
//                SessionHelper.addSession(gcRequest, gcResponse, loginResponse, servletContext);
//                gcRequest.setSession(loginResponse.getSession());
//                ftRequest.setJKey(loginResponse.getSession().getjKey());
//                ftRequest.setJSession(loginResponse.getSession().getjSessionID());
//                auditObj.setAuditInfo(loginResponse.getSession());
//                // TO-DO : audit for failure cases
//                return true;
//            } else {
//                return false;
//            }
//        }catch(Exception e) {
//            return false;
//        }
//    }   
    
    public static boolean reInitiateLogIn(FTRequest ftRequest, Session session, ServletContext servletContext,
            GCRequest gcRequest, GCResponse gcResponse) throws Exception {
        GCAuditObject auditObj = gcRequest.getAuditObj();
        try {
            String password = AdvanceLogin.getEncryptedPwd(AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"), 
                    session.getUserID()),session.getAppID());
            log.info("user ID :"+session.getUserID()+" AppID :"+gcRequest.getAppID()+"API session expired");
            LoginResponse loginResponse = Login.verify_login_102(session.getUserID(),AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"), 
                    password), "", gcRequest.getClientIP(), gcRequest.getAppID());
            if (loginResponse.isLogonSucccess()) {
                SessionHelper.updateSession(gcRequest, gcResponse, loginResponse, servletContext);
                gcRequest.setSession(loginResponse.getSession());
                ftRequest.setJKey(loginResponse.getSession().getjKey());
                ftRequest.setJSession(loginResponse.getSession().getjSessionID());
                auditObj.setAuditInfo(loginResponse.getSession());
                // TO-DO : audit for failure cases
                return true;
            } else {
                boolean otpStatus=AdvanceLogin.setOtpStatus("Y",session.getUserID(), session.getAppID());
                if(otpStatus==true)
                    log.info("OTP updated successfully on false from API response");
                return false;
            }
        } catch(Exception e) {
            if(SessionHelper.isUserOtpReqdFlag(gcRequest.getAppID(),new Session())) {
                boolean otpStatus=AdvanceLogin.setOtpStatus("Y",session.getUserID(), session.getAppID());
                if(otpStatus==true)
                    log.info("OTP updated successfully on Exception");
            }
            return false;
        }
    }
    
    public static boolean reInitiateLogInBase(FTRequest ftRequest, Session session, ServletContext servletContext,
            GCRequest gcRequest, GCResponse gcResponse) throws Exception {
        GCAuditObject auditObj = gcRequest.getAuditObj();
        try {
            String password = AdvanceLogin.getEncryptedPwd(AESEncryption.encrypt(AppConfig.getValue("webservice.encrypt.key"), 
                    session.getUserID()),session.getAppID());
            log.info("user ID :"+session.getUserID()+" AppID :"+gcRequest.getAppID()+"API session expired");
            LoginResponse loginResponse = Login.verify_login_103(session.getUserID(),AESEncryption.decrypt(AppConfig.getValue("webservice.encrypt.key"), 
                    password), "", gcRequest.getClientIP(), gcRequest.getAppID());
            if (loginResponse.isLogonSucccess()) {
                SessionHelper.updateSessionOnBiometric(gcRequest, gcResponse, loginResponse, servletContext);
                gcRequest.setSession(loginResponse.getSession());
                ftRequest.setJKey(loginResponse.getSession().getjKey());
                ftRequest.setJSession(loginResponse.getSession().getjSessionID());
                auditObj.setAuditInfo(loginResponse.getSession());
                // TO-DO : audit for failure cases
                return true;
            } else {
                boolean otpStatus=AdvanceLogin.setOtpStatus("Y",session.getUserID(), session.getAppID());
                if(otpStatus==true)
                    log.info("OTP updated successfully on false from API response");
                return false;
            }
        } catch(Exception e) {
            if(SessionHelper.isUserOtpReqdFlag(gcRequest.getAppID(),new Session())) {
                boolean otpStatus=AdvanceLogin.setOtpStatus("Y",session.getUserID(), session.getAppID());
                if(otpStatus==true)
                    log.info("OTP updated successfully on Exception");
            }
            return true;
        }
    }

}
