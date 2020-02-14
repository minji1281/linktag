package com.linktag.base.util;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class ClsAES {
    private String key = "LINKTAG!ESANSOFT";
    private String initVector = "ESANSOFT!LINKTAG";

    public ClsAES() {

    }

    public String Encrypt(String str){
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(str.getBytes());
            return Base64.encodeToString(encrypted, Base64.NO_WRAP);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String Decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.NO_WRAP));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public String Base64Encoding(String text){
        try{
            byte[] bytes = text.getBytes("UTF-8");
            String str = Base64.encodeToString(bytes, Base64.NO_WRAP);

            return str;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public String Base64Decoding(String text){
        try{
            byte[] bytes = Base64.decode(text.getBytes("UTF-8"), Base64.NO_WRAP);
            String str = new String(bytes);

            return str;
        } catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }



}
