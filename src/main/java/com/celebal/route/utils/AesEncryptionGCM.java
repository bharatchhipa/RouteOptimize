package com.celebal.route.utils;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;




public class AesEncryptionGCM {


   private final static SecureRandom secureRandom = new SecureRandom();


   /**
    * Length of Initialization Vector : 12
    */
   private final static int GCM_IV_LENGTH = 12;


   /**
    * GCM Tag Length : 128
    */
   private final static int GCM_TAG_LENGHT=128;




   /**
    * Encrypt a plaintext with given key.
    *
    * @param plaintext      to encrypt (utf-8 encoding will be used)
    * @param secretKey      to encrypt, must be AES type, see {@link SecretKeySpec}
    * @return encrypted message
    * @throws Exception if anything goes wrong
    */
   public static String encrypt(String plaintext) throws Exception {
	   SecretKey secretKey =  convertStringToKey("2269075592060038");
       // generating Initialization Vector
       byte[] iv = new byte[GCM_IV_LENGTH];
       secureRandom.nextBytes(iv);


       final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
       GCMParameterSpec parameterSpec = new GCMParameterSpec(GCM_TAG_LENGHT, iv); //128 bit auth tag length
       cipher.init(Cipher.ENCRYPT_MODE, secretKey, parameterSpec);


       byte[] cipherText = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));
       ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + cipherText.length);
       byteBuffer.put(iv);
       byteBuffer.put(cipherText);
       return encode(byteBuffer.array());
   }


   /**
    * Decrypts encrypted message
    *
    * @param message  iv with ciphertext
    * @param secretKey      used to decrypt
    * @return original plaintext
    * @throws Exception if anything goes wrong
    */
   public static String decrypt(String message) throws Exception {
	   SecretKey secretKey =  convertStringToKey("2269075592060038");
       byte[] cipherMessage = decode(message);
       final Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
       //use first 12 bytes for iv
       AlgorithmParameterSpec gcmIv = new GCMParameterSpec(GCM_TAG_LENGHT, cipherMessage, 0, GCM_IV_LENGTH);
       cipher.init(Cipher.DECRYPT_MODE, secretKey, gcmIv);


       //use everything from 12 bytes on as ciphertext
       byte[] plainText = cipher.doFinal(cipherMessage, GCM_IV_LENGTH, cipherMessage.length - GCM_IV_LENGTH);


       return new String(plainText, StandardCharsets.UTF_8);
   }




   /**
    * Converts Byte Array Data to base64 String
    * @param data
    * @return String
    */
   private static String encode(byte[] data) {
       return java.util.Base64.getEncoder().encodeToString(data);
   }


   /**
    * Converts base64 string to byte Array
    * @param data
    * @return byte[]
    */
   private static byte[] decode(String data) {
       return java.util.Base64.getDecoder().decode(data);
   }


   /**
    * Returns SecretKey object from the key String
    * @param keyString - this will be the secret key
    * @return SecretKey
    */
   private static SecretKey convertStringToKey(String keyString) {
       if (keyString != null && !keyString.isEmpty()) {
           byte[] keyBytes = keyString.getBytes(StandardCharsets.UTF_8);
           return new SecretKeySpec(keyBytes, "AES");
       }
       return null;
   }




   /**
    * Main method to test the working of concept
    * @param args
    * @throws Exception
    */
   public static void main(String[] args) throws Exception {
       AesEncryptionGCM aesEncryption= new AesEncryptionGCM();


       SecretKey secretKey =  aesEncryption.convertStringToKey("....SecretKey...");
       if(secretKey != null ){
           String encrypted = aesEncryption.encrypt("{\n"
           		+ "    \"token\": {\n"
           		+ "        \"accessToken\": \"f6d0ae8c-cf66-4845-835e-c59221c832e2\",\n"
           		+ "        \"type\": 0,\n"
           		+ "        \"roleId\": [\n"
           		+ "            0\n"
           		+ "        ],\n"
           		+ "        \"timeToLive\": null,\n"
           		+ "        \"creationTime\": null\n"
           		+ "    },\n"
           		+ "    \"data\": {\n"
           		+ "        \"mobile\": 7777755555,\n"
           		+ "        \"password\": \"123456\",\n"
           		+ "        \"accountType\": \"AGENT\"\n"
           		+ "    }\n"
           		+ "}");
           System.out.println("encrypted : " + encrypted);
           String decrypted = aesEncryption.decrypt("6KASuKbL3PlbfJFz7nXZ0nU4yq0fBAWwk3bg8ZLMjaj/djycKUth2oOFvhhnBNvX5h0k23ht1ts1Tw7r2rDZe/slJcO6LdX+2LiJUn4UO3NioHBsFZeWQ8D8eQcIjHy29xu6LFmVtzsOnMJdjC+/Js9OCL+jD+jLYAovKjZTMuUk");
           System.out.println("decrypted : " + decrypted);
       }
   }
}
