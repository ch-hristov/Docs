package com.sismics.docs.core.util;

import java.io.InputStream;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import com.google.common.base.Strings;

/**
 * Encryption utilities.
 * 
 * @author bgamard
 */
public class EncryptionUtil {

    /**
     * Salt.
     */
    private static final String SALT = "LEpxZmm2SMu2PeKzPNrar2rhVAS6LrrgvXKeL9uyXC4vgKHg";
    
    static {
        // Initialize Bouncy Castle provider
        Security.insertProviderAt(new BouncyCastleProvider(), 1);
    }
    
    /**
     * Generate a private key.
     * 
     * @return New random private key
     * @throws NoSuchAlgorithmException 
     */
    public static String generatePrivateKey() throws NoSuchAlgorithmException {
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
        return new BigInteger(176, random).toString(32);
    }
    
    /**
     * Decrypt an InputStream using the specified private key.
     * 
     * @param is InputStream to encrypt
     * @param privateKey Private key
     * @return Encrypted stream
     * @throws Exception 
     */
    public static InputStream decryptInputStream(InputStream is, String privateKey) throws Exception {
        return new CipherInputStream(is, getCipher(privateKey, Cipher.DECRYPT_MODE));
    }
    
    /**
     * Return an encryption cipher.
     * 
     * @param privateKey Private key
     * @return Encryption cipher
     * @throws Exception
     */
    public static Cipher getEncryptionCipher(String privateKey) throws Exception {
        if (Strings.isNullOrEmpty(privateKey)) {
            throw new IllegalArgumentException("The private key is null or empty");
        }
        return getCipher(privateKey, Cipher.ENCRYPT_MODE);
    }
    
    /**
     * Initialize a Cipher.
     * 
     * @param privateKey Private key
     * @param mode Mode (encrypt or decrypt)
     * @return Cipher
     * @throws Exception
     */
    private static Cipher getCipher(String privateKey, int mode) throws Exception {
        PBEKeySpec keySpec = new PBEKeySpec(privateKey.toCharArray(), SALT.getBytes(), 2000, 256);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC");
        SecretKey desKey = skf.generateSecret(keySpec);
        Cipher cipher = Cipher.getInstance("AES/CTR/NOPADDING");
        cipher.init(mode, desKey);
        return cipher;
    }
}