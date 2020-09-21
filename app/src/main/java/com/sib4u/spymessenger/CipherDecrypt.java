package com.sib4u.spymessenger;

import android.util.Log;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.Signature;

import javax.crypto.Cipher;

public class CipherDecrypt {
    public CipherDecrypt() {
    }

    public void rsa() throws Exception {
        //Creating a Signature object
        Signature sign = Signature.getInstance ( "SHA256withRSA" );

        //Creating KeyPair generator object
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance ( "RSA" );

        //Initializing the key pair generator
        keyPairGen.initialize ( 2048 );

        //Generate the pair of keys
        KeyPair pair = keyPairGen.generateKeyPair ( );

        //Getting the public key from the key pair
        PublicKey publicKey = pair.getPublic ( );

        //Creating a Cipher object
        Cipher cipher = Cipher.getInstance ( "RSA/ECB/PKCS1Padding" );

        //Initializing a Cipher object
        cipher.init ( Cipher.ENCRYPT_MODE, publicKey );

        //Add data to the cipher
        byte[] input = "Welcome to Tutorialspoint".getBytes ( );
        cipher.update ( input );

        //encrypting the data
        byte[] cipherText = cipher.doFinal ( );
        Log.d ( "TAG", "rsa: " + new String ( cipherText, "UTF8" ) );

        //Initializing the same cipher for decryption
        cipher.init ( Cipher.DECRYPT_MODE, pair.getPrivate ( ) );

        //Decrypting the text
        byte[] decipheredText = cipher.doFinal ( cipherText );
        Log.d ( "TAG", "rsa: " + new String ( decipheredText ) );
    }
}
