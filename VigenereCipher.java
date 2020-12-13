package csds344_gui;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This is an implementation of the VigenereCipher cipher.
 * That is, if the plaintext is found it is encrypted with the key and
 * a new ciphertext file is created.
 * If the ciphertext is found it is decrypted into a new plaintext.
 */
public class VigenereCipher {

    String plaintextFileName, keyFileName, ciphertextFileName;
    byte[] plaintext = null, key = null, ciphertext = null;
    File plaintextFile = null, ciphertextFile = null;

    int operation; // 0 = encrypt, 1 = decrypt

    /**
     * If the pair (plaintext, key) is found encryption is performed.
     * If the pair (ciphertext, key) is found decryption is performed.
     * If both plaintext and ciphertext are present decryption is performed.
     * Key is necessary.
     */
    VigenereCipher(String plaintextFileName, String ciphertextFileName, String keyFileName) throws IOException {
    	
    	if (plaintextFileName.equals("") || ciphertextFileName.equals("") || keyFileName.equals("")) {
    		throw new IOException();
    	}
    	
        this.plaintextFileName = plaintextFileName;
        this.ciphertextFileName = ciphertextFileName;
        this.keyFileName = keyFileName;
           
        File keyFile = new File(keyFileName);
        plaintextFile = new File(plaintextFileName);
        ciphertextFile = new File(ciphertextFileName);
        
        boolean noPlain = false, noCipher = false;
        try {
        	plaintext = new byte[(int)plaintextFile.length()];
        	try (InputStream inputStream = new FileInputStream(plaintextFile)) {
        	    inputStream.read(this.plaintext);
            }
            operation = 0;
        } catch (IOException ioe) {
            noPlain = true;
        }
        
        try {
        	ciphertext = new byte[(int)ciphertextFile.length()];
        	try (InputStream inputStream = new FileInputStream(ciphertextFile)) {
        	    inputStream.read(this.ciphertext);
            }

            plaintext = new byte[ciphertext.length];
            operation = 1;
        } catch (IOException ioe) {
            if (noPlain) {
                throw new IOException();
            }
            
            if(plaintext != null){
                ciphertext = new byte[plaintext.length];
            }else{
                throw new IOException();
            }
        }
        
    	key = new byte[(int)keyFile.length()];
        try (InputStream inputStream = new FileInputStream(keyFile)) {
    	    inputStream.read(this.key);
        }
            
    }

    /**
     * Encrypt/Decrypt a string with the key previously set.
     * If decrypt is true decryption is performed.
     */
    public void encrypt() {
        boolean decrypt = (operation == 1) ? true : false;
        byte[] inputtext = (decrypt) ? ciphertext : plaintext;
        byte[] outputtext = (decrypt) ? plaintext : ciphertext;
        int kp = 0, ip; //keypointer and input pointer
        byte k, i, o; //current byte at key, input and output
        int keyLength = key.length, offset = 10;
        for (ip = 0; ip < inputtext.length; ip++) {
            // Convert chars from a = 97 to a = 0.
            i = inputtext[ip];
            k = key[kp];
            o = (byte) ((decrypt) ? (i - k) : (i + k) % 256);
            // Make sure we apply modulo 26 even if the result is negative
           /* if (o < 0) {
                o = (byte) (256 + (i - k));
            }*/

            outputtext[ip] = o;
            kp = (kp + 1) % keyLength;
        }
        if (decrypt) {
            this.plaintext = outputtext;
        } else {
            this.ciphertext = outputtext;
        }
    }

    /**
     * Write files to the given paths.
     */
    public void writeFiles() {
    	try {
    		try (FileOutputStream outputStream = new FileOutputStream(ciphertextFile)) {
        		outputStream.write(ciphertext);	
        	}
        	
        	try (FileOutputStream outputStream = new FileOutputStream(plaintextFile)) {
        		outputStream.write(plaintext);	
        	}
    		
    	}catch(IOException e) {
    	}
    }

    /**
     * Entry point for the enc/dec process.
     */
    public void execute() {
        encrypt();
        writeFiles();
    }

    @Override
    public String toString() {

        return "Plaintext -> " + new String(plaintext) + "\nCiphertext -> " + new String(ciphertext) + "\nKey -> " + new String(key);
    }
}