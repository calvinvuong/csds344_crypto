package csds344_gui;

// Calvin Vuong
// Implements the RSA algorithm

import java.math.BigInteger;
import java.util.Arrays;
import java.io.*;
import java.util.Random;

public class RSA {
    // NOTE: For the current padding scheme to work, BLOCK_SIZE must be no more than 256 bytes.
    private static final int BLOCK_SIZE = 256; // in bytes
    private static final int KEY_LEN = 2049; // in bits
    
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
    //private static BigInteger os2ip(byte[] string, int strLen) {
    private static BigInteger os2ip(byte[] string) {
	BigInteger output = big0;
	for ( int i = 0; i < string.length; i++ ) {
	    BigInteger term = BigInteger.valueOf(Byte.toUnsignedInt(string[i])).multiply(big256.pow(string.length-i-1));
	    output = output.add(term);
	}
	return output;
    }


    // Performs either the encrypting or decrypting on the octet string block of octet length strLen.
    // Key (n, e or d) given by RSAKey key.
    // The only difference between encrypting and decrypting is the key it is given.
    private static byte[] cryptBlock(byte[] block, int strLen, RSAKey key) throws Exception {

	BigInteger m = os2ip(block);
	// Perform modular exonentiation.
	BigInteger c = m.modPow(key.getExponent(), key.getModulus());

	byte[] cryptedBlock = i2osp(c, strLen);
	return cryptedBlock;
	
    }

    // Fills the last padBytes bytes in block with padBytes.
    // padBytes < 256
    private static void padBlock(byte[] block, int padBytes) {
	//System.out.println("PADDING ACTIVATED.");
	for ( int i = block.length - padBytes; i < block.length; i++ ) {
	    block[i] = (byte) padBytes;
	}
    }

    // Returns a byte array of zeros
    private static byte[] padZero(int blockSize) {
	return new byte[blockSize];
    }


    // Returns block, trimmed of any padding bytes
    private static byte[] trimPadding(byte[] block) {
	// Last byte in block tells length of pad
	int last = Byte.toUnsignedInt(block[block.length-1]);
	if ( last == 0 ) {
	    return new byte[0];
	}
	byte[] trimmed = new byte[block.length-last];
	//System.out.println(trimmed.length);
	for ( int i = 0; i < trimmed.length; i++ )
	    trimmed[i] = block[i];
	return trimmed;
    }
    
    // Takes as input a path to a file and a block size.
    // Encrypts the file
    private static void encryptFile(String inputFile, int blockSize, String outputFile, RSAKey key) throws Exception {
	BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inputFile));
	BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));
	
	byte[] blockBuf = new byte[blockSize];
	int bytesRead = reader.read(blockBuf, 0, blockSize);
	BigInteger m;
	boolean padded = false;
	while ( bytesRead != -1 ) {
	    if ( bytesRead < blockSize ) {
		padBlock(blockBuf, blockSize-bytesRead);
		padded = true;
	    }
	    // encrypt or decrypt
	    byte[] cryptedBlock = cryptBlock(blockBuf, blockSize+1, key);
	    // Write crypted block to output file
	    writer.write(cryptedBlock, 0, cryptedBlock.length);
	    // read next block from the file
	    bytesRead = reader.read(blockBuf, 0, blockSize);
	}
	if ( ! padded ) {
	    byte[] cryptedBlock = cryptBlock( padZero(blockSize+1), blockSize+1, key );
	    writer.write( cryptedBlock, 0, cryptedBlock.length );
	}
	  
	reader.close();
	writer.close();

    }

    // Takes as input a path to a file and a block size.
    // Decrypts the file
    private static void decryptFile(String inputFile, int blockSize, String outputFile, RSAKey key) throws Exception {
	BufferedInputStream reader = new BufferedInputStream(new FileInputStream(inputFile));
	BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(outputFile));
	
	byte[] blockBuf = new byte[blockSize];
	int bytesRead = reader.read(blockBuf, 0, blockSize);
	BigInteger m;
	while ( bytesRead != -1 ) {
	    // encrypt or decrypt
	    byte[] cryptedBlock = cryptBlock(blockBuf, blockSize-1, key);
	    // read next block from the file
	    bytesRead = reader.read(blockBuf, 0, blockSize);
	    if ( bytesRead == -1 ) { // current cryptedBlock is the last block (no next block to read)
		cryptedBlock = trimPadding(cryptedBlock); // trim this block of padding
	    }
	    // Write crypted block to output file
	    writer.write(cryptedBlock, 0, cryptedBlock.length);
		
	}
	reader.close();
	writer.close();

    }


    // Wrapper functions for encrypt and decrypt file
    public static void encryptFile(String inputFile, String outputFile, RSAKey publicKey) throws Exception {
	encryptFile(inputFile, BLOCK_SIZE, outputFile, publicKey);
    }

    public static void decryptFile(String inputFile, String outputFile, RSAKey privateKey) throws Exception {
	decryptFile(inputFile, BLOCK_SIZE+1, outputFile, privateKey);
    }
    
    public static void main(String[] args) throws Exception {

	/*
	String inputFile = "files/lecture.pdf";
	String outputFile = "files/lecture_E.pdf";
	
	String rInputFile = outputFile;
	String rOutputFile = "files/lecture_D.pdf";
	*/
	
	String inputFile = "files/isengard.jpg";
	String outputFile = "files/isengard_E.jpg";
	String rInputFile = outputFile;
	String rOutputFile = "files/isengard_D.jpg";
	
	// Generate RSA key

	
	RSAKeyPair key = RSAKeyGen.generateRSAKey(KEY_LEN);
	RSAPublicKey pubKey = key.getPublicKey();
	RSAPrivateKey priKey = key.getPrivateKey();

	// Save keys first.
	RSAKey.saveKey("rsaKey.pub", pubKey);
	RSAKey.saveKey("rsaKey", priKey);

	// Read keys back.
	RSAPublicKey publicKey = (RSAPublicKey) RSAKey.loadKey("rsaKey.pub");
	RSAPrivateKey privateKey = (RSAPrivateKey) RSAKey.loadKey("rsaKey");
	
	System.out.println("Encryption");
	encryptFile(inputFile, outputFile, publicKey);
	System.out.println();
	System.out.println("Decryption");
	decryptFile(rInputFile, rOutputFile, privateKey);
	
	
    }


    


}
