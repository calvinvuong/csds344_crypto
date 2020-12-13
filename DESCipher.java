package csds344_gui;

import java.io.*;
import java.util.*;

public class DESCipher {
	
	/** 
	 * Encrypts a file using the DES algorithm
	 * @param file File to be encrypted
	 * @param hexKey 64-bit key as Hexadecimal
	 * @param outputFileName Output name of the encrypted file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void encryptFile(File file, String hexKey, String outputFileName) 
    		throws FileNotFoundException, IOException {
		
		if (hexKey.length() != 16)
			throw new IllegalArgumentException("Invalid key");
    	
    	// Generate 16 48-bit subKeys
    	List<String> subkeys = subKeys(hexKey);
    	
    	// Convert file to hexadecimal string
    	String hexPlainText = fileToHexString(file);
    	
    	// Make plain text multiple of 64 bits
    	String mult16Plain = DESUtils.mult16HexString(hexPlainText); 
    	
    	// Encrypt 64-bit chunks
    	StringBuilder hexCipherText = new StringBuilder();
    	
    	for (int i = 0; i < mult16Plain.length(); i += 16) {
    		String chunk64Bit = mult16Plain.substring(i, i + 16);
    		String encrypted = encrypt(chunk64Bit, subkeys);
    		assert encrypted.length() == 16;
    		hexCipherText.append(encrypted);
    	}
    	assert hexCipherText.toString().length() % 16 == 0;
    	
    	writeFile(new File(outputFileName), hexCipherText.toString());
    }
    
	/** 
	 * Decrypts a file using the DES algorithm
	 * @param file File to be decrypted
	 * @param hexKey 64-bit key as Hexadecimal
	 * @param outputFileName Output name of the decrypted file
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
    public static void decryptFile(File file, String hexKey, String outputFileName) 
    		throws FileNotFoundException, IOException {
    	
    	if (hexKey.length() != 16)
			throw new IllegalArgumentException("Invalid key");
    	
    	// Generate 16 48-bit subKeys
    	List<String> subkeys = subKeys(hexKey);
    	
    	// Convert file to hexadecimal string
    	String hexCipherText = fileToHexString(file);
    	assert hexCipherText.length() % 16 == 0: hexCipherText.length();
    	   	
    	// Decrypt 64-bit chunks
    	StringBuilder hexPlainText = new StringBuilder();
    	
    	for (int i = 0; i < hexCipherText.length(); i += 16) {
    		String chunk64Bit = hexCipherText.substring(i, i + 16);
    		hexPlainText.append(decrypt(chunk64Bit, subkeys));
    	}
   
    	writeFile(new File(outputFileName), hexPlainText.toString());
    }
    
    /* Generate 16 48-bit subKeys from 64-bit key */
    private static List<String> subKeys(String key) { 
    	
        // Store 16 subKeys
    	List<String> subkeys = new LinkedList<>(); 
        
        // perKey is 56 bits
        String perKey = DESUtils.permutedHexString(PermutationTables.PC1, key); 
        String subkey = perKey;
        
        for (int i = 0; i < 16; i++) {
        	subkey = DESUtils.leftCircularShifted(subkey.substring(0, 7),  PermutationTables.NUM_SHIFTS[i]) + 
        			 DESUtils.leftCircularShifted(subkey.substring(7, 14), PermutationTables.NUM_SHIFTS[i]);
        	
        	// Each subKey is 48 bits after 2nd permutation
        	subkeys.add(DESUtils.permutedHexString(PermutationTables.PC2, subkey));
        }

        return subkeys;
    } 
	
    /* 64-bit plainText (as Hex) */
    private static String encrypt(String plainText, List<String> subkeys) {
    	
    	// Apply IP permutation
    	String cipherText = DESUtils.permutedHexString(PermutationTables.IP, plainText);
    	
    	// Go through 16 Feistel rounds
    	for (int i = 0; i < 16; i++)
    		cipherText = feistelRound(cipherText, subkeys.get(i)); 
    	
    	// Swap left and right
    	cipherText = cipherText.substring(8, 16) + cipherText.substring(0, 8);
    	
    	// Apply IP-1 permutation
    	return DESUtils.permutedHexString(PermutationTables.IP1, cipherText);
    } 
    
    private static String decrypt(String cipherText, List<String> subkeys) {
    	
    	// Apply IP permutation
        String plainText = DESUtils.permutedHexString(PermutationTables.IP, cipherText); 
        
        // Go through 16 rounds of f function in reverse 
        for (int i = 16; i > 0; i--)
            plainText = feistelRound(plainText, subkeys.get(i - 1));

        // Swap left and right
        plainText = plainText.substring(8, 16) + plainText.substring(0, 8); 
        
        return DESUtils.permutedHexString(PermutationTables.IP1, plainText); 
    }
    
    private static String feistelRound(String cipherText, String subkey) { 
    	
        // Split previous result into left and right 
        String prevLeft = cipherText.substring(0, 8); 
        String prevRight = cipherText.substring(8, 16); 
        String curLeft = prevRight; 
        
        // Expansion permutation of prevRight: from 32 to 48 bits 
        String expansion = DESUtils.permutedHexString(PermutationTables.EP, prevRight); 
        
        expansion = DESUtils.xor(expansion, subkey); 
        
        // Generate S-Box output (32 bits) 
        String sBoxOutput = sBox(expansion); 
        
        // P permutation of S-Box output 
        String pPerm = DESUtils.permutedHexString(PermutationTables.P, sBoxOutput); 
        
        String curRight = DESUtils.xor(prevLeft, pPerm); 
       
        return curLeft + curRight; 
    }

    private static String sBox(String expansion) { 

        String expBinary = DESUtils.hexToBinary(expansion); // 48 bits
        List<String> sixBitBlocks = new LinkedList<>();
        
        // Generate 6-bit blocks from 48-bit input
        for (int i = 0; i < 48; i += 6)
        	sixBitBlocks.add(expBinary.substring(i, i + 6));
        
        StringBuilder result = new StringBuilder();
        int blockNum = 0;
        
        for (String block : sixBitBlocks) {
        	int row = Integer.parseInt(block.charAt(0) + "" + block.charAt(5), 2); //First and last bit
        	int col = Integer.parseInt(block.substring(1, 5), 2); // Middle bits
        	String fourBitOutput = Integer.toHexString(PermutationTables.SBOX[blockNum][row][col]);
        	result.append(fourBitOutput);
        	blockNum++;
        }
        
        // Result is 32 bits
        return result.toString(); 
    }

    private static String fileToHexString(File file) throws FileNotFoundException, IOException {
    	 try (InputStream inputStream = new FileInputStream(file)) {
    		byte[] inputBytes = new byte[(int)file.length()];
    	    inputStream.read(inputBytes);
    	        	
    	    return DESUtils.bytesToHex(inputBytes); 
    	 }
    }
    
    private static void writeFile(File file, String s) throws FileNotFoundException, IOException {
    	try (FileOutputStream outputStream = new FileOutputStream(file)) {
    		outputStream.write(DESUtils.hexToBytes(s));	
    	}
    }
}
