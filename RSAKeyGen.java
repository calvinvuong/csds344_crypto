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
	Random rng = new Random();
	// Find prime numbers p and q
	BigInteger p, q, n;
	do {
	    do {
		p = BigInteger.probablePrime(k/2, rng);
	    } while ( p.mod(e).compareTo(big1) == 0 );	
	    do {
		q = BigInteger.probablePrime(k/2, rng);
	    } while ( q.mod(e).compareTo(big1) == 0 );

	    // Calculate n = p*q
	    n = p.multiply(q);
	} while ( n.bitLength() != k );

	// Calculate key values.
	
	// Uses the Charmichael function instead of the totient function
	BigInteger pMinus1 = p.subtract(big1);
	BigInteger qMinus1 = p.subtract(big1);
	BigInteger L = pMinus1.multiply(qMinus1).divide(pMinus1.gcd(qMinus1));

	BigInteger d = e.modInverse(L);

	return new RSAKeyPair(n, e, d);
    }

    public static void main(String[] args) {
	generateRSAKey(2048);
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
	privateKey = new RSAPrivateKey(d);
    }

    public RSAPublicKey getPublicKey() {
	return publicKey;
    }

    public RSAPrivateKey getPrivateKey() {
	return privateKey;
    }
}

class RSAPublicKey {
    private BigInteger modulus;
    private BigInteger e;

    public RSAPublicKey(BigInteger n, BigInteger e) {
	this.modulus = n;
	this.e = e;
    }

    public BigInteger getModulus() {
	return modulus;
    }

    public BigInteger getE() {
	return e;
    }
}

class RSAPrivateKey {
    private BigInteger d;

    public RSAPrivateKey(BigInteger d) {
	this.d = d;
    }

    public BigInteger getD() {
	return d;
    }
}
    
    
	
