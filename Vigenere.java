import java.io.IOException;


public class Vigenere {

    public static void main(String[] args) throws IOException {

        VigenereCipher vc;
        //Message if improper arguments are given
            /*if (args.length != 3) {
                System.out.println("Usage: Vigenere plaintext_file ciphertext_file key_file");
                return;
            }*/
        String input = "text";
        String key = "key.txt";
        String output = "output";
        vc = new VigenereCipher(input, output, key);
        vc.execute();
        System.out.println(vc);
    }
}