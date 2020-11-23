// Calvin Vuong
// Implements the RSA algorithm

import java.math.BigInteger;
import java.util.Arrays;

public class RSA {

    // Converts a nonnegative integer* to an octet string of specified length.
    // Takes as input nonnegative integer* x and octet length k.
    private static byte[] i2osp(long x, int xLen) throws Exception {
	if ( x >= Math.pow(256, xLen) ) {
	    throw new Exception("integer too large.");
	}
	byte[] output = new byte[xLen];
	for ( int i = xLen-1; i >= 0; i-- ) {
	    output[i] = ((byte) (x % 256));
	    x /= 256;
	}
	return output;
    }

    // Converts an octet string to a nonnegative integer*.
    // RFC 3447 4.2
    private static long os2ip(byte[] string) {
	long output = 0;
	for ( int i = 0; i < string.length; i++ ) {
	    output += ((int) string[i]) * Math.pow(256, string.length-i-1);
	}
	return output;
    }

    // Implements modular exponentiation (efficiently)
    // Returns b^e mod m
    // Note: consider implementing method using BigInts
    private static int mod_exponent(int b, int e, int m) {
	if (m == 1)
	    return 0;

	int c = 1;
	for ( int i = 0; i < e; i++ ) {
	    c = (c * b) % m;
	}

	return c;
    }

    /*
    private static BigInteger mod2(BigInteger b, BigInteger e, BigInteger m) {
	if (m.equals(1))
	    return new BigInteger("0");
	BigInteger c = new BigInteger("1");
	for ( BigInteger i = new BigInteger("0"); i.compareTo(e) < 0; i = i.add(new BigInteger("1")) ) {
	    c = (c * b) % m;
	}
	return c;
    }
    */
    public static void main(String[] args) throws Exception {
	System.out.println(Long.MAX_VALUE);
	//String inputString = "hello my";
	String inputString = "hell";
	byte[] input = inputString.getBytes();
	System.out.println(Arrays.toString(input));
	long encoded = os2ip(input);
	System.out.println(encoded);
	//String decoded = new String(i2osp(encoded, inputString.length()+3));
	byte[] decoded = i2osp(encoded, 7);//inputString.length());
	System.out.println(Arrays.toString(decoded));
	
	//System.out.println(mod_exponent(2, 3, 7));
	//System.out.println(mod2(new BigInteger("2"), new BigInteger("3"), new BigInteger("7")));
	
    }
	    
}
