package com.saxophone.saxopia.community;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import android.util.Base64;

/**
 * @File : AES256Chiper
 * @Date : 2018. 5. 4. AM 9:19
 * @Author : Andrew Kim
 * @Version : 1.0.0
 * @Description : 암호화 처리
**/
public class AES256Chiper {

    public static byte[] ivBytes = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };
    public static String secretKey = "saxopiabandstory";

    //AES256 암호화
    public static String AES_Encode(String str) throws UnsupportedEncodingException
            , NoSuchPaddingException
            , NoSuchAlgorithmException
            , InvalidAlgorithmParameterException
            , InvalidKeyException
            , IllegalBlockSizeException
            , BadPaddingException {

        byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        Cipher cipher = null;
        cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, newKey, ivSpec);

        return Base64.encodeToString(cipher.doFinal(textBytes), 0);
    }

    //AES256 복호화
    public static String AES_Decode(String str) throws UnsupportedEncodingException
            , NoSuchPaddingException
            , NoSuchAlgorithmException
            , InvalidAlgorithmParameterException
            , InvalidKeyException
            , IllegalBlockSizeException
            , BadPaddingException {

        byte[] textBytes =Base64.decode(str,0);
        //byte[] textBytes = str.getBytes("UTF-8");
        AlgorithmParameterSpec ivSpec = new IvParameterSpec(ivBytes);
        SecretKeySpec newKey = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, newKey, ivSpec);
        return new String(cipher.doFinal(textBytes), "UTF-8");
    }

    public static String convertMd5(final String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
                String h = Integer.toHexString(0xFF & messageDigest[i]);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void main(String[] args) throws UnsupportedEncodingException
            , NoSuchPaddingException
            , NoSuchAlgorithmException
            , InvalidAlgorithmParameterException
            , InvalidKeyException
            , IllegalBlockSizeException
            , BadPaddingException {
        String str = "saxopiatest";
        String encodeStr = AES256Chiper.AES_Encode(str);
        String decodeStr = AES256Chiper.AES_Decode(encodeStr);

        System.out.println("encode string : " + encodeStr);
        System.out.println("decode string : " + decodeStr);
    }
}