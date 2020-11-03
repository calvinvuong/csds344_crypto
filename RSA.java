// Calvin Vuong
// Implements the RSA algorithm

import java.math.BigInteger;

public class RSA {
    

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
    public static void main(String[] args) {
	System.out.println(mod_exponent(2, 3, 7));
	//System.out.println(mod2(new BigInteger("2"), new BigInteger("3"), new BigInteger("7")));
	
    }
	    
}
