// Calvin Vuong
// Implements RSA public-private key generation

import java.math.BigInteger;
import java.util.Random;
import java.io.*;

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

class RSAKey implements Serializable {
    protected BigInteger modulus;
    protected BigInteger exponent;
    protected String type;
    
    public RSAKey(BigInteger n, BigInteger exp) {
	modulus = n;
	exponent = exp;
	type = null;
    }

    public BigInteger getModulus() {
	return modulus;
    }

    public BigInteger getExponent() {
	return exponent;
    }

    public String getType() {
	return type;
    }
    
    // Writes the contents of RSAKey key to file fileName
    public static void saveKey(String fileName, RSAKey key) {
	try {
	    FileWriter writer = new FileWriter(fileName);
	    // write public private type
	    writer.write("type=" + key.getType() + "\n");
	    // write exponent
	    writer.write("exp=" + key.getExponent() + "\n");
	    // write modulus
	    writer.write("mod=" + key.getModulus());
	    writer.close();
	    /*
	    ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName));
	    writer.writeObject(key);
	    writer.close();
	    */
	}
	catch (IOException e) {
	    e.printStackTrace();
	}
    }

    // Reads the RSA key from file fileName and returns an RSAKey object
    // Returns null if key could not be loaded.
    public static RSAKey loadKey(String fileName) {
	try {
	    BigInteger exponent = null;
	    BigInteger modulus = null;
	    String type = null;
	    
	    BufferedReader reader = new BufferedReader(new FileReader(fileName));
	    String line;
	    while ( (line = reader.readLine()) != null ) {
		// read exponent
		if ( line.startsWith("exp=") )
		    exponent = new BigInteger(line.split("=")[1]);
		// read modulus
		else if ( line.startsWith("mod=") )
		    modulus = new BigInteger(line.split("=")[1]);
		else if ( line.startsWith("type=") )
		    type = line.split("=")[1];
	    }
	    reader.close();

	    // Create the RSAKey object and return
	    RSAKey key = null;
	    if ( exponent != null && modulus != null ) {
		if ( type.equals("private") )
		    key = new RSAPrivateKey(modulus, exponent);
		else if ( type.equals("public") )
		    key = new RSAPublicKey(modulus, exponent);
	    }
	    return key;
	    /*
	    ObjectInputStream reader = new ObjectInputStream(new FileInputStream(fileName));
	    RSAKey key = (RSAKey) reader.readObject();
	    reader.close();
	    return key;
	    */
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	return null;
    }

}

class RSAPublicKey extends RSAKey {

    public RSAPublicKey(BigInteger n, BigInteger e) {
	super(n, e);
	type = "public";
    }
	
    public BigInteger getE() {
	return getExponent();
    }
}

class RSAPrivateKey extends RSAKey {
    // modulus not strictly required, but still good to have
    public RSAPrivateKey(BigInteger n, BigInteger d) {
	super(n, d);
	type = "private";
    }
    public BigInteger getD() {
	return getExponent();
    }
}
    
    
	
