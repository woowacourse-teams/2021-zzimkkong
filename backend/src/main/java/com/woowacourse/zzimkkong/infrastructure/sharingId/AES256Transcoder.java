package com.woowacourse.zzimkkong.infrastructure.sharingId;

import com.woowacourse.zzimkkong.exception.infrastructure.DecodingException;
import com.woowacourse.zzimkkong.exception.infrastructure.EncodingException;
import com.woowacourse.zzimkkong.exception.infrastructure.InsufficientSecretKeyLengthException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
@PropertySource("classpath:config/AES256Transcoder.properties")
public class AES256Transcoder implements Transcoder {
    private static final int MINIMUM_LENGTH_OF_SECRET_KEY = 32;
    private static final int LENGTH_OF_INITIALIZATION_VECTOR = 16;

    private final SecretKey secureKey;
    private final IvParameterSpec ivParameterSpec;

    public AES256Transcoder(@Value("${transcoder.secret-key}") String secretKey) {
        validateLengthOfSecretKey(secretKey);
        this.secureKey = new SecretKeySpec(secretKey.substring(0, MINIMUM_LENGTH_OF_SECRET_KEY).getBytes(), "AES");
        this.ivParameterSpec = new IvParameterSpec(secretKey.substring(0, LENGTH_OF_INITIALIZATION_VECTOR).getBytes());
    }

    private void validateLengthOfSecretKey(String secretKey) {
        if (secretKey.length() < MINIMUM_LENGTH_OF_SECRET_KEY) {
            throw new InsufficientSecretKeyLengthException();
        }
    }

    @Override
    public String encode(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secureKey, ivParameterSpec);

            byte[] encrypted = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));

            return Base64.encodeBase64URLSafeString(encrypted);
        } catch (InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException |
                NoSuchPaddingException | NoSuchAlgorithmException |
                InvalidAlgorithmParameterException |
                NullPointerException exception) {
            throw new EncodingException(exception);
        }
    }

    @Override
    public String decode(String input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secureKey, ivParameterSpec);

            byte[] byteStr = Base64.decodeBase64(input.getBytes());

            return new String(cipher.doFinal(byteStr), StandardCharsets.UTF_8);
        } catch (InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException |
                NoSuchPaddingException | NoSuchAlgorithmException |
                InvalidAlgorithmParameterException exception) {
            throw new DecodingException(exception);
        }
    }
}
