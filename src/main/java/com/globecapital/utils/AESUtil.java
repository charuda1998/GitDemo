package com.globecapital.utils;

import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.lang.StringUtils;
import com.globecapital.config.AppConfig;
import com.globecapital.constants.DeviceConstants;
public class AESUtil {

	public byte[] encrypt(byte[] bs) {
		byte[] result = null;
		try {
			String secretKey = AppConfig.getValue("edis.tpin.secretKey");
			Cipher cipher = Cipher.getInstance(DeviceConstants.ALGORITHM);
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(DeviceConstants.CHARSET_NAME), DeviceConstants.AES_NAME);
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(new byte[16]);
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
			result = cipher.doFinal(bs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Base64.getEncoder().encode(result);
	}

	public String decrypt(String content) {
		try {
			String secretKey = AppConfig.getValue("edis.tpin.secretKey");
			Cipher cipher = Cipher.getInstance(DeviceConstants.ALGORITHM);
			SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(DeviceConstants.CHARSET_NAME), DeviceConstants.AES_NAME);
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(new byte[16]);
			cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
			return new String(cipher.doFinal(Base64.getDecoder().decode(content.getBytes(DeviceConstants.CHARSET_NAME))), DeviceConstants.CHARSET_NAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return StringUtils.EMPTY;
	}
}
