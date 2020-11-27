package csds344_gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This is an implementation of the VigenereCipher cipher.
 * That is, if the plaintext is found it is encrypted with the key and
 * a new ciphertext file is created.
 * If the ciphertext is found it is decrypted into a new plaintext.
 */
public class VigenereCipher {

    String plaintextFileName, keyFileName, ciphertextFileName;
    String plaintext = null, key = null, ciphertext = null;

    int operation; // 0 = encrypt, 1 = decrypt

    /**
     * If the pair (plaintext, key) is found encryption is performed.
     * If the pair (ciphertext, key) is found decryption is performed.
     * If both plaintext and ciphertext are present decryption is performed.
     * Key is necessary.
     */
    VigenereCipher(String plaintextFileName, String ciphertextFileName, String keyFileName) {
        this.plaintextFileName = plaintextFileName;
        this.ciphertextFileName = ciphertextFileName;
        this.keyFileName = keyFileName;

        boolean noPlain = false, noCipher = false;

        Path plaintextFile = FileSystems.getDefault().getPath(".", plaintextFileName);
        Path ciphertextFile = FileSystems.getDefault().getPath(".", ciphertextFileName);
        Path keyFile = FileSystems.getDefault().getPath(".", keyFileName);
        try {
            this.plaintext = new String(Files.readAllBytes(plaintextFile)).trim();
            operation = 0;
        } catch (IOException ioe) {
            System.out.println("No plaintext found, trying ciphertext...");
            noPlain = true;
        }
        try {
            this.ciphertext = new String(Files.readAllBytes(ciphertextFile)).trim();
            operation = 1;
        } catch (IOException ioe) {
            if (noPlain) {
                System.out.println("You haven't provided a valid ciphertext nor a valid plaintext. Exiting...");
                System.exit(1);
            }
            System.out.println("Plaintext ok but no ciphertext found...I will create one for you");
        }
        try {
            this.key = new String(Files.readAllBytes(keyFile)).trim();
        } catch (IOException ioe) {
            System.out.println("Key not found! Cannot continue without a key and either a plaintext or a ciphertext");
            System.exit(1);
        }
    }

    /**
     * Encrypt/Decrypt a string with the key previously set.
     * If decrypt is true decryption is performed.
     */
    public void encrypt() {
        boolean decrypt = (operation == 1) ? true : false;
        String inputtext = (decrypt) ? ciphertext : plaintext;
        String outputtext = (decrypt) ? plaintext : ciphertext;
        int kp = 0, ip; //keypointer and input pointer
        byte k, i, o; //current byte at key, input and output
        int keyLength = key.length(), offset = 10;
        StringBuilder outputtextBuilder = new StringBuilder();
        for (ip = 0; ip < inputtext.length(); ip++) {
            // Convert chars from a = 97 to a = 0.
            i = (byte) (inputtext.charAt(ip) - 'a');
            k = (byte) (key.charAt(kp) - 'a');
            o = (byte) ((decrypt) ? (i - k) : (i + k) % 26);
            // Make sure we apply modulo 26 even if the result is negative
            if (o < 0) {
                o = (byte) (26 + (i - k));
            }
            o = (byte) (o + 'a');  // Revert back to Unicode space to print actual characters
            outputtextBuilder.append((char) o);
            kp = (kp + 1) % keyLength;
        }
        if (decrypt) {
            this.plaintext = outputtextBuilder.toString();
        } else {
            this.ciphertext = outputtextBuilder.toString();
        }
    }

    /**
     * Write files to the given paths.
     */
    public void writeFiles() {
        PrintWriter ciphertextWriter = null, plaintextWriter = null;
        try {
            ciphertextWriter = new PrintWriter(ciphertextFileName);
            plaintextWriter = new PrintWriter(plaintextFileName);
        } catch (FileNotFoundException fnfe) {
            System.out.println("One or more files not found");
            System.exit(1);
        }
        ciphertextWriter.print(ciphertext);
        plaintextWriter.print(plaintext);
        ciphertextWriter.close();
        plaintextWriter.close();
    }

    /**
     * Entry point for the enc/dec process.
     */
    public void execute() {
        if (operation == 0) {
            System.out.println("Encrypting...");
        }
        if (operation == 1) {
            System.out.println("Decrypting...");
        }
        encrypt();
        writeFiles();
    }

    @Override
    public String toString() {
        return "Plaintext -> " + plaintext + "\nCiphertext -> " + ciphertext + "\nKey -> " + key;
    }
}