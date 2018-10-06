package com.hl46000.hlfaker.security.algorithm;

import java.security.MessageDigest;

public class md5 {
    public static byte[] encryptMD5(byte[] data) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(data);
        return md5.digest();
    }

    public static String encodeMD5(byte[] data){
        try{
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(data);
            byte messageDigest[] = md5.digest();
            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));

            return hexString.toString();
        }catch (Exception e){
            return "null";
        }
    }
}
