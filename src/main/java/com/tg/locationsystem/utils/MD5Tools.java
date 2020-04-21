package com.tg.locationsystem.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * MD5技术加密解密
 */
public class MD5Tools {
            //加密类型
            private static final String ALGORITHM = "DESede";

        // 默认为 DESede/ECB/PKCS5Padding
        private static final String CIPHER_TRANSFORMAT = "DESede/ECB/PKCS5Padding";

        //编码类型
        private static final String ENCODING = "UTF-8";

        //密钥
        private static final String PRIVATE_KEY = "u218ehweishfushf";

        //经过MD5的密钥
         private static byte[] PRIVATE_KEY_BYTES = {};

         //构造方法  将密钥进行加密
         public MD5Tools() {
            MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
            PRIVATE_KEY_BYTES = this.getKey(md5.digest(PRIVATE_KEY.getBytes(ENCODING)));
            } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
        /** 
      * ECB加密,不要IV 
      * @param data 明文 
      * @return Base64编码的密文 
      * @throws Exception 
      */
         public static String des3EncodeECB(String data) throws Exception {
            SecretKey deskey = new SecretKeySpec(PRIVATE_KEY_BYTES, ALGORITHM);
            Cipher c1 = Cipher.getInstance(CIPHER_TRANSFORMAT);
            c1.init(Cipher.ENCRYPT_MODE, deskey);
             byte[] result = c1.doFinal(data.getBytes(ENCODING));
            return Base64.encodeBase64String(result);
    }

             /** 
          * ECB解密,不要IV 
          * @param data Base64编码的密文 
          * @return 明文 
          * @throws Exception 
          */
             public static String ees3DecodeECB(String data) throws Exception {
                SecretKey deskey = new SecretKeySpec(PRIVATE_KEY_BYTES, ALGORITHM);
                Cipher c1 = Cipher.getInstance(CIPHER_TRANSFORMAT);
                c1.init(Cipher.DECRYPT_MODE, deskey);
                byte[] result = c1.doFinal(Base64.decodeBase64(data));
                return new String(result, ENCODING);
        }

             //如果key的长度小于24
             public byte[] getKey(byte[] key) {
            byte[] MD5key = new byte[24];
            if(key.length < 24) {
            System.arraycopy(key, 0, MD5key, 0, key.length);
            System.arraycopy(key, 0, MD5key, 16, 8);
            }else {
                MD5key = key;
            }
        return MD5key;
 }



    public static void main(String[] args) throws Exception {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        String s = sdf.format(new Date());
        s="2020-04-18#3";
       MD5Tools md5=new MD5Tools();
        String s1 = des3EncodeECB(s);
        s1=s1.replace("=","");
        System.out.println(s1);
        String s2 = ees3DecodeECB("Kdzw6I7Z4v4bzIfTCtwxPg");
        System.out.println(s2);

    }
}
