package csds344_gui;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ThreadLocalRandom;

public class DESUtils {
	
	private static final byte[] HEX_ARRAY = "0123456789ABCDEF".getBytes(StandardCharsets.US_ASCII);
	
	static String hexToBinary(String hexString) {
        int numberOfBits = hexString.length() * 4; 
        String binaryString = Long.toBinaryString(Long.parseUnsignedLong(hexString, 16));
        
        return stringWithLeadingZeros(binaryString, numberOfBits - binaryString.length());
    }

    static String binaryToHex(String binString) { 
    	int numberOfHex = (int)binString.length() / 4; 
        String hexString = Long.toHexString(Long.parseUnsignedLong(binString, 2));
        
        return stringWithLeadingZeros(hexString, numberOfHex - hexString.length());
    } 
    
    // Applies sequence permutation
    static String permutedHexString(int[] sequence, String hexString) { 
        StringBuilder output = new StringBuilder();
        String binString = hexToBinary(hexString);
        
        for (int i = 0; i < sequence.length; i++)
        	output.append(binString.charAt(sequence[i] - 1));
        
        return binaryToHex(output.toString()); 
    } 

    static String xor(String aHex, String bHex) { 
        // Convert to decimal and xor
    	long decA = Long.parseUnsignedLong(aHex, 16); 
        long decB = Long.parseUnsignedLong(bHex, 16); 
        long xor = decA ^ decB; 
       
        // Convert to Hex
        String xorHex = Long.toHexString(xor); 
        
        return stringWithLeadingZeros(xorHex, bHex.length() - xorHex.length());
    }

    // left Circular Shifting bits 
    static String leftCircularShifted(String hexString, int numShifts) { 
        int numBits = hexString.length() * 4; 
        int leftShiftPerm[] = new int[numBits]; 
        
        // Construct permutation sequence [2, 3, 4,...,1]
        for (int i = 0; i < numBits - 1; i++) 
        	leftShiftPerm[i] = i + 2; 
        leftShiftPerm[numBits - 1] = 1;

        // Perform left shift 'numShifts' times
        for (int i = 0; i < numShifts; i++)
        	hexString = permutedHexString(leftShiftPerm, hexString); 
        	
        return hexString; 
    }
    
    static String bytesToHex(byte[] bytes) {
        byte[] hexChars = new byte[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = HEX_ARRAY[v >>> 4];
            hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
        }
        return new String(hexChars, StandardCharsets.UTF_8);
    }
      
    static byte[] hexToBytes(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + 
            					   Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
    
    static String mult16HexString(String s) {
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
    
    private static String stringWithLeadingZeros(String s, int numberOfZeros) {
		StringBuilder leadingZeros = new StringBuilder();
        for (int i = 0; i < numberOfZeros; i++)
        	leadingZeros.append("0");
        
        return leadingZeros.toString() + s;
	}
    
    public static String randomHexKey(){
    	byte[] bytes = new byte[8];
    	ThreadLocalRandom.current().nextBytes(bytes);
    	String key = bytesToHex(bytes);
        
        return stringWithLeadingZeros(key, 16 - key.length());
    }
}
