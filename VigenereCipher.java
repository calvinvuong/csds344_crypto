import java.io.*;
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
    byte[] plaintext = null, key = null, ciphertext = null;
    Path plaintextFile = null, ciphertextFile = null;

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
        plaintextFile = FileSystems.getDefault().getPath(".", plaintextFileName);
        ciphertextFile = FileSystems.getDefault().getPath(".", ciphertextFileName);
        Path keyFile = FileSystems.getDefault().getPath(".", keyFileName);
        boolean noPlain = false, noCipher = false;

        try {
            this.plaintext = Files.readAllBytes(plaintextFile);
            operation = 0;
        } catch (IOException ioe) {
            System.out.println("No plaintext found, trying ciphertext...");
            noPlain = true;
        }
        try {
            this.ciphertext = Files.readAllBytes(ciphertextFile);
            plaintext = new byte[ciphertext.length];
            operation = 1;
        } catch (IOException ioe) {
            if (noPlain) {
                System.out.println("You haven't provided a valid ciphertext nor a valid plaintext. Exiting...");
                System.exit(1);
            }
            System.out.println("Plaintext ok but no ciphertext found...I will create one for you");
            ciphertext = new byte[plaintext.length];
        }
        try {
            this.key = Files.readAllBytes(keyFile);
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
            Files.write(ciphertextFile,ciphertext);
            Files.write(plaintextFile,plaintext);
        } catch (IOException ioe) {
            System.out.println("One or more files not found");
            System.exit(1);
        }
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

        return "Plaintext -> " + new String(plaintext) + "\nCiphertext -> " + new String(ciphertext) + "\nKey -> " + new String(key);
    }
}