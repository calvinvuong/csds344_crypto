// Calvin Vuong
// Implements RSA public-private key generation

import java.math.BigInteger;
import java.util.Random;

public class RSAKeyGen {

    // In practice, e is fixed.
    private static final BigInteger e = new BigInteger("65537");
    private static final BigInteger big1 = new BigInteger("1");
    
    // Returns an RSAKeyPair
    // Takes as input k, the bit-length of modulus
    public static RSAKeyPair generateRSAKey(int k) {
	//Random rng = new Random(12345);
	Random rng = new Random();
	// Find prime numbers p and q
	BigInteger p, q, n;
	//p = BigInteger.probablePrime(k/2, rng);
	//q = BigInteger.probablePrime(k/2, rng);
	//n = p.multiply(q);
	
	do {
	    do {
		p = BigInteger.probablePrime(k/2, rng);
	    } while ( p.mod(e).compareTo(big1) == 0 );	
	    do {
		q = BigInteger.probablePrime(k - k/2, rng);
	    } while ( q.mod(e).compareTo(big1) == 0 );

	    // Calculate n = p*q
	    n = p.multiply(q);
	} while ( n.bitLength() != k );
	
	// Calculate key values.
	
	// Uses the Charmichael function instead of the totient function
	BigInteger pMinus1 = p.subtract(big1);
	BigInteger qMinus1 = q.subtract(big1);
	BigInteger L = pMinus1.multiply(qMinus1).divide(pMinus1.gcd(qMinus1));
	//BigInteger L = pMinus1.multiply(qMinus1);

	BigInteger d = e.modInverse(L);

	return new RSAKeyPair(n, e, d);
    }

    public static void main(String[] args) {
	RSAKeyPair keyPair = generateRSAKey(2048);

	RSAPublicKey publicK = keyPair.getPublicKey();
	RSAPrivateKey privateK = keyPair.getPrivateKey();

	BigInteger m = new BigInteger("78");
	System.out.println(m);
	System.out.println();
	//BigInteger c = m.modPow(publicK.getExponent(), publicK.getModulus());
	BigInteger c = m.modPow(new BigInteger("17"), new BigInteger("77"));
	System.out.println(c);
	System.out.println();
	//BigInteger m2 = c.modPow(privateK.getExponent(), privateK.getModulus());
	BigInteger m2 = c.modPow(new BigInteger("53"), new BigInteger("77"));
	System.out.println(m2);
	
	/*
	System.out.println(publicK.getModulus());
	System.out.println(publicK.getE());
	System.out.println();
	System.out.println(privateK.getModulus());
	System.out.println(privateK.getD());
	*/
    }


}

class RSAKeyPair {
    private BigInteger modulus; // n
    private BigInteger e;
    private BigInteger d;

    private RSAPublicKey publicKey;
    private RSAPrivateKey privateKey;
    
    public RSAKeyPair(BigInteger n, BigInteger e, BigInteger d) {
	this.modulus = n;
	this.e = e;
	this.d = d;

	publicKey = new RSAPublicKey(modulus, e);
	privateKey = new RSAPrivateKey(modulus, d);
    }

    public RSAPublicKey getPublicKey() {
	return publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
	return privateKey;
    }
}

class RSAKey {
    protected BigInteger modulus;
    protected BigInteger exponent;

    public RSAKey(BigInteger n, BigInteger exp) {
	modulus = n;
	exponent = exp;
    }

    public BigInteger getModulus() {
	return modulus;
    }

    public BigInteger getExponent() {
	return exponent;
    }

}
class RSAPublicKey extends RSAKey {

    public RSAPublicKey(BigInteger n, BigInteger e) {
	super(n, e);
    }
	
    public BigInteger getE() {
	return getExponent();
    }
}

class RSAPrivateKey extends RSAKey {
    // modulus not strictly required, but still good to have
    public RSAPrivateKey(BigInteger n, BigInteger d) {
	super(n, d);
    }
    public BigInteger getD() {
	return getExponent();
    }
}
    
    
	
