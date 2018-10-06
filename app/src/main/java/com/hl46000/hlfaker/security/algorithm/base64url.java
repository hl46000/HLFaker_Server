package com.hl46000.hlfaker.security.algorithm;

import android.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by ZEROETC on 4/24/2018.
 */

public class base64url {
    /**
     * Decodes a Base64Url encoded String
     *
     * @param input Base64Url encoded String
     * @return Decoded result from input
     */
    public static String decode(String input) {
        String result = null;
        byte[] decodedBytes = Base64.decode(input, Base64.URL_SAFE);
        try {
            result = new String(decodedBytes, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        return result;
    }

    /**
     * Encodes a String with Base64Url and no padding
     *
     * @param input String to be encoded
     * @return Encoded result from input
     */
    public static String encode(String input) {
        String result = null;
        try {
            byte[] encodeBytes = input.getBytes("UTF-8");
            result = Base64.encodeToString(encodeBytes, Base64.NO_PADDING | Base64.NO_WRAP | Base64.URL_SAFE);
        }
        catch (UnsupportedEncodingException e) {
            return null;
        }
        return result;
    }
}
