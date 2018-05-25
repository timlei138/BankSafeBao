package com.android.bsb.util;

import android.util.Base64;
import android.util.Patterns;

import com.android.bsb.AppComm;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Utils {

    private static byte[] iv = {5,1,2,5,7,9,0};



    public static String encodeString(String args){
        String str = "";
        try {
            IvParameterSpec zeroIv = new IvParameterSpec(iv);
            SecretKeySpec key = new SecretKeySpec(AppComm.DES_PWD.getBytes(),"DES");
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCSSPadding");
            cipher.init(Cipher.ENCRYPT_MODE,key,zeroIv);
            byte[] encryptedData = cipher.doFinal(args.getBytes());
            return Base64.encodeToString(encryptedData,0);
        }  catch (Exception e) {
            e.printStackTrace();
            AppLogger.LOGE(null,"encodeString faild !");
        }
        return str;
    }


    public static String decode(String args){
        byte[] bytes = Base64.decode(args,0);
        IvParameterSpec zeroIv = new IvParameterSpec(iv);
        SecretKeySpec key = new SecretKeySpec(AppComm.DES_PWD.getBytes(),"DES");
        try {
            Cipher cipher = Cipher.getInstance("DES/CBC/PKCSSPadding");
            cipher.init(Cipher.DECRYPT_MODE,key,zeroIv);
            byte[] decrytedData = cipher.doFinal(bytes);
            return new String(decrytedData);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
        return args;
    }


    public static boolean checkCellphone(String cellphone) {
        String regex = "^((13[0-9])|(14[5|7])|(15([0-3]|[5-9]))|(18[0,5-9]))\\d{8}$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(cellphone);
        return m.find();
    }

}
