package com.celebal.route.utils;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.persistence.AttributeConverter;
import java.nio.charset.StandardCharsets;

@Log4j2
@Component
public class AttributeEncryptor implements AttributeConverter<String, String> {
    @Value("${encryption.key}")
    private String key;
    @Value("${encryption.initVector}")
    private String initVector;
    @Value("${encryption.algo}")
    private String algo;

    @Override
    public String convertToDatabaseColumn(String s) {
        try {
            if (s != null) {
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
                SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance(algo);
                cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

                byte[] encrypted = cipher.doFinal(s.getBytes());
                return Base64.encodeBase64String(encrypted);
            }
        } catch (Exception ex) {
            log.error("An error occurred", ex);
        }
        return null;
    }

    @Override
    public String convertToEntityAttribute(String s) {
        try {
            if (s != null) {
                IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(StandardCharsets.UTF_8));
                SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance(algo);
                cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

                byte[] original = cipher.doFinal(Base64.decodeBase64(s));
                return new String(original);
            }
        } catch (Exception ex) {
            log.error("An error occurred", ex);
        }
        return null;
    }

}
