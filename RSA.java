// Calvin Vuong
// Implements the RSA algorithm

import java.math.BigInteger;
import java.util.Arrays;
import java.io.*;
import java.util.Random;

public class RSA {
    private static final int BLOCK_SIZE = 128; // in bytes
    private static final int KEY_LEN = 2048; // in bits
    
    private static BigInteger big256 = new BigInteger("256");
    private static BigInteger big0 = new BigInteger("0");
    private static BigInteger big1 = new BigInteger("1");
    
    private static BigInteger intermediate;
    
    // Converts a nonnegative integer* to an octet string of specified length.
    // Takes as input nonnegative integer* x and octet length k.
    // RFC 3447 4.1
    private static byte[] i2osp(BigInteger x, int xLen) throws Exception {
	if ( x.compareTo(big256.pow(xLen)) >= 0 ) {
	    throw new Exception("integer too large.");
	}
	byte[] output = new byte[xLen];
	for ( int i = xLen-1; i >= 0; i-- ) {
	    byte coefficient = x.mod(big256).byteValue();
	    output[i] = coefficient;
	    x = x.divide(big256);
	}
	return output;
    }

    // Converts an octet string to a nonnegative integer*.
    // Input strLen is the size of the octet string in byte array string
    // RFC 3447 4.2
    private static BigInteger os2ip(byte[] string, int strLen) {
	BigInteger output = big0;
	for ( int i = 0; i < strLen; i++ ) {
	    BigInteger term = BigInteger.valueOf(Byte.toUnsignedInt(string[i])).multiply(big256.pow(strLen-i-1));
	    //BigInteger term = BigInteger.valueOf((long) string[i]).multiply(big256.pow(strLen-i-1));
	    output = output.add(term);
	}
	return output;
    }


    // Performs either the encrypting or decrypting on the octet string block of octet length strLen.
    // Key (n, e or d) given by RSAKey key.
    // The only difference between encrypting and decrypting is the key it is given.
    private static byte[] cryptBlock(byte[] block, int strLen, RSAKey key) throws Exception {
	
	// Convert octet string block to integer.
	BigInteger m = os2ip(block, strLen);
	System.out.println(m);
	System.out.println();
	// Perform modular exonentiation (efficiently).
	BigInteger c = m.modPow(key.getExponent(), key.getModulus());
	System.out.println(c);
	System.out.println();
	
	byte[] cryptedBlock = i2osp(c, KEY_LEN/8);
	System.out.println(cryptedBlock);
	return cryptedBlock;
	
    }


    // Takes as input a path to a file and a block size.
    private static void cryptFile(String inputFile, int blockSize, String outputFile, RSAKey key) throws Exception {
	BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inputFile));
	BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));
	
	byte[] blockBuf = new byte[blockSize];
	int bytesRead = reader.read(blockBuf, 0, blockSize);
	BigInteger m;
	while ( bytesRead != -1 ) {
	    // encrypt or decrypt
	    byte[] cryptedBlock = cryptBlock(blockBuf, bytesRead, key);
	    // Write crypted block to output file
	    writer.write(cryptedBlock, 0, bytesRead);
	    // read next block from the file
	    bytesRead = reader.read(blockBuf, 0, blockSize);
	}
	  
	reader.close();
	writer.close();

    }

    
    
    public static void main(String[] args) throws Exception {
	String inputFile = "files/input1.txt";
	String outputFile = "files/encrypt1.txt";
	
	String rInputFile = outputFile;
	String rOutputFile = "files/decrypt1.txt";

	// Generate RSA key

	
	RSAKeyPair key = RSAKeyGen.generateRSAKey(KEY_LEN);
	RSAPublicKey publicKey = key.getPublicKey();
	RSAPrivateKey privateKey = key.getPrivateKey();
	byte[] plaintext = "Hello there. I need a string that is exactly 256 bytes long. Ugh I really wish this were easier. Why am I having so much trouble debugging? I do not like this one bit. I do not like it at all.".getBytes();
	//System.out.println(plaintext.length);

	/*
	BigInteger m = os2ip(plaintext, plaintext.length);
	//BigInteger m = new BigInteger("283923409888938234");
	System.out.println(m);
	System.out.println();
	BigInteger c = crypt(m, publicKey);
	System.out.println(c);
	System.out.println();
	BigInteger m1 = crypt(c, privateKey);
	System.out.println(m1);
	*/	

	/*
	byte[] ciphertext = cryptBlock(plaintext, plaintext.length, publicKey);
	System.out.println(Arrays.toString(plaintext));
	System.out.println("Plaintext text length: " + plaintext.length);
		
	System.out.println();
	System.out.println(Arrays.toString(ciphertext));
	System.out.println("Cipher text length: " + ciphertext.length);

	System.out.println();
	byte[] decrypttext = cryptBlock(ciphertext, ciphertext.length, privateKey);
	System.out.println(Arrays.toString(decrypttext));
	System.out.println("Decrypt text length: " + decrypttext.length);
	*/

	System.out.println("Encryption");
	cryptFile(inputFile, BLOCK_SIZE, outputFile, publicKey);
	System.out.println();
	System.out.println("Decryption");
	cryptFile(rInputFile, BLOCK_SIZE, rOutputFile, privateKey);
	
	/*
	System.out.println(Long.MAX_VALUE);
	//String inputString = "hello my";
	String inputString = "hello there, my name is calvin vuong. there is something perhaps wrong with this? idk dude.";
	System.out.println(inputString);
	byte[] input = inputString.getBytes();
	System.out.println(input.length);
	BigInteger encoded = os2ip(input);
	System.out.println(encoded);
	byte[] decoded = i2osp(encoded, input.length);
	System.out.println(new String(decoded));
	System.out.println(big0);
	//System.out.println(Arrays.toString(input));
	//System.out.println(Arrays.toString(decoded));
	*/
	
    }


    


}
