package com.chat.utils;


import org.apache.commons.codec.binary.Base64;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Hashtable;

public class MD5Utils {

    /**
     *      对字符串进行MD5加密
     * @param strValue
     * @return
     * @throws Exception
     */
    public static String getMD5Str(String strValue) throws Exception{

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        String newstr = Base64.encodeBase64String(md5.digest(strValue.getBytes()));
        return newstr;
    }

    public static void main(String[] args) {
        try {
            String md5Str = getMD5Str("chatgroup");
            System.out.println(md5Str);
        }catch (Exception e){
            e.printStackTrace();
        }


        Hashtable<Integer,Integer> hashtable = new Hashtable<>();
        HashMap<Integer,Integer> hashMap = new HashMap<>();

    }
}
