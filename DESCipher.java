import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class DESCipher {

    // hexadecimal to binary conversion 
    private static String hextoBin(String input) 
    { 
        int n = input.length() * 4; 
        input = Long.toBinaryString( 
            Long.parseUnsignedLong(input, 16)); 
        while (input.length() < n) 
            input = "0" + input; 
        return input; 
    } 

    // binary to hexadecimal conversion 
    static String binToHex(String input) 
    { 
        int n = (int)input.length() / 4; 
        input = Long.toHexString( 
            Long.parseUnsignedLong(input, 2)); 
        while (input.length() < n) 
            input = "0" + input; 
        return input; 
    } 

    // per-mutate input hexadecimal 
    // according to specified sequence 
    static String permutation(int[] sequence, String input) 
    { 
        String output = ""; 
        input = hextoBin(input); 
        for (int i = 0; i < sequence.length; i++) 
            output += input.charAt(sequence[i] - 1); 
        output = binToHex(output); 
        return output; 
    } 

    // xor 2 hexadecimal strings 
    static String xor(String a, String b) 
    { 
        // hexadecimal to decimal(base 10) 
        long t_a = Long.parseUnsignedLong(a, 16); 
        // hexadecimal to decimal(base 10) 
        long t_b = Long.parseUnsignedLong(b, 16); 
        // xor 
        t_a = t_a ^ t_b; 
        // decimal to hexadecimal 
        a = Long.toHexString(t_a); 
        // prepend 0's to maintain length 
        while (a.length() < b.length()) 
            a = "0" + a; 
        return a; 
    } 

    // left Circular Shifting bits 
    static String leftCircularShift(String input, int numBits) 
    { 
        int n = input.length() * 4; 
        int perm[] = new int[n]; 
        for (int i = 0; i < n - 1; i++) 
            perm[i] = (i + 2); 
        perm[n - 1] = 1; 
        while (numBits-- > 0) 
            input = permutation(perm, input); 
        return input; 
    } 

    /* Generate 16 48-bit subKeys from 64-bit key */
    private static List<String> subKeys(String key) { 
    	
        // Store 16 subKeys
    	List<String> subkeys = new LinkedList<>(); 
        
        // perKey is 56 bits
        String perKey = permutation(PermutationTables.PC1, key); 
        String subkey = perKey;
        
        for (int i = 0; i < 16; i++) {
        	subkey = leftCircularShift(subkey.substring(0, 7),  PermutationTables.shiftBits[i]) + 
        			 leftCircularShift(subkey.substring(7, 14), PermutationTables.shiftBits[i]);
        	
        	// Each subKey is 48 bits after 2nd permutation
        	subkeys.add(permutation(PermutationTables.PC2, subkey));
        }

        return subkeys;
    } 

    private String sBox(String expansion) { 

        String expBinary = hextoBin(expansion); // 48 bits
        List<String> sixBitBlocks = new LinkedList<>();
        
        // Generate 6-bit blocks from 48-bit input
        for (int i = 0; i < 48; i += 6)
        	sixBitBlocks.add(expBinary.substring(i, i + 6));
        
        StringBuilder result = new StringBuilder();
        int blockNum = 0;
        
        for (String block : sixBitBlocks) {
        	int row = Integer.parseInt(block.charAt(0) + "" + block.charAt(5), 2); //First and last bit
        	int col = Integer.parseInt(block.substring(1, 5), 2); // Middle bits
        	String fourBitOutput = Integer.toHexString(PermutationTables.sbox[blockNum][row][col]);
        	result.append(fourBitOutput);
        	blockNum++;
        }
        
        // Result is 32 bits
        return result.toString(); 
    }

    private String fFunction(String cipherText, String subkey) { 
    	
        // Split previous result into left and right 
        String prevLeft = cipherText.substring(0, 8); 
        String prevRight = cipherText.substring(8, 16); 
        String curLeft = prevRight; 
        
        // Expansion permutation of prevRight: from 32 to 48 bits 
        String expansion = permutation(PermutationTables.EP, prevRight); 
        
        expansion = xor(expansion, subkey); 
        
        // Generate S-Box output (32 bits) 
        String sBoxOutput = sBox(expansion); 
        
        // P permutation of S-Box output 
        String pPerm = permutation(PermutationTables.P, sBoxOutput); 
        
        String curRight = xor(prevLeft, pPerm); 
       
        return curLeft + curRight; 
    } 

    /* 64-bit plainText and 64-bit key */
    String encrypt(String plainText, List<String> subkeys) {
    	
    	// Apply IP permutation
    	String cipherText = permutation(PermutationTables.IP, plainText);
    	
    	// Go through 16 rounds of f function
    	for (int i = 0; i < 16; i++)
    		cipherText = fFunction(cipherText, subkeys.get(i)); 
    	
    	// Swap left and right
    	cipherText = cipherText.substring(8, 16) + cipherText.substring(0, 8);
    	
    	// Apply IP-1 permutation
    	return permutation(PermutationTables.IP1, cipherText);
    } 
    
    String decrypt(String cipherText, List<String> subkeys) {
        // initial permutation 
        String plainText = permutation(PermutationTables.IP, cipherText); 
        
        // 16-rounds 
        for (int i = 15; i > -1; i--) { 
            plainText = fFunction(plainText, subkeys.get(i)); 
        } 

        // 32-bit swap 
        plainText = plainText.substring(8, 16) + plainText.substring(0, 8); 
        plainText = permutation(PermutationTables.IP1, plainText); 
        
        return plainText; 
    }
    
    
    private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
    public static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
    
    public static byte[] hexToBytes(String s) {
    	return new BigInteger(s, 16).toByteArray();
    }

    String fileToHexString(File file) throws FileNotFoundException, IOException {
    	 try (InputStream inputStream = new FileInputStream(file)) {
    		byte[] inputBytes = new byte[(int) file.length()];
    	    inputStream.read(inputBytes);
    	        	
    	    return bytesToHex(inputBytes); 
    	 }
    }
    
    void writeFile(File file, String s) throws FileNotFoundException, IOException {
    	try (FileOutputStream outputStream = new FileOutputStream(file)) {
    		outputStream.write(hexToBytes(s));	
    	}
    }
    
    public static String randomHexKey(){
    	byte[] bytes = new byte[10];
    	ThreadLocalRandom.current().nextBytes(bytes);
        
        return bytesToHex(bytes).substring(0, 16);
    }
    
    String mult16HexString(String s) {
    	StringBuilder mult16PlainBuilder = new StringBuilder();
    	int plainLength = s.length();
    	int remainder = plainLength % 16;
    	
    	String first = s.substring(0, plainLength - remainder);
    	mult16PlainBuilder.append(first);
    	for (int i = 0; i < 16 - remainder; i++)
    		mult16PlainBuilder.append("0");
    	String last = s.substring(plainLength - remainder, plainLength);
    	mult16PlainBuilder.append(last);
    	
    	assert mult16PlainBuilder.toString().length() % 16 == 0;
    	return mult16PlainBuilder.toString(); 
    }
    
    public void encryptFile(File file, String key, String outputFileName) 
    		throws FileNotFoundException, IOException {
    	
    	// Generate 16 48-bit subKeys
    	List<String> subkeys = subKeys(key);
    	
    	// Convert file to hexadecimal string
    	String hexPlainText = fileToHexString(file);
    	
    	// Make plain text multiple of 64 bits
    	String mult16Plain = mult16HexString(hexPlainText); 
    	
    	// Encrypt 64-bit chunks
    	StringBuilder hexCipherText = new StringBuilder();
    	
    	for (int i = 0; i < mult16Plain.length(); i += 16) {
    		String chunk64Bit = mult16Plain.substring(i, i + 16);
    		hexCipherText.append(encrypt(chunk64Bit, subkeys));
    	}
    	assert hexCipherText.toString().length() % 16 == 0;
    	
    	writeFile(new File(outputFileName), hexCipherText.toString());
    }
    
    public void decryptFile(File file, String key, String outputFileName) 
    		throws FileNotFoundException, IOException {
    	
    	// Generate 16 48-bit subKeys
    	List<String> subkeys = subKeys(key);
    	
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
    
    public static void main(String args[]) throws IOException { 
    	/*File inputFile = new File("cal.pdf");
    	InputStream inputStream = new FileInputStream(inputFile);
    	byte[] inputBytes = new byte[(int) inputFile.length()];
    	inputStream.read(inputBytes);
    	
    	for (byte b : inputBytes) {
            String st = String.format("%02X", b);
            System.out.println(st);
        }
    	String st = String.format("%02X", -2); System.out.print(st);
    	System.out.println(inputBytes);
    	
    	FileOutputStream out = new FileOutputStream(new File("l.txt"));
    	out.write(inputBytes);*/
    	
    	String key = "AABB09182736CCDD"; //1d89b7bb19d06836
    	System.out.println(hexToBytes(key).length);
    	List<String> subkeys = subKeys(key);
    	DESCipher cipher = new DESCipher(); 
    	
    	cipher.encryptFile(new File("l.pdf"), key, "encrypted");
    	cipher.decryptFile(new File("encrypted"), key, "original.pdf");
    	
    	/*String enc = cipher.encrypt("123456ABCD132536", subkeys);
    	System.out.println(enc);
    	System.out.println(cipher.decrypt(enc, subkeys));*/
    	
    } 
}
