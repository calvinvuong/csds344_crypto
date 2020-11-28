import java.io.IOException;


public class Vigenere {

    public static void main(String[] args) throws IOException {

        VigenereCipher vc;

        String input = "jssus.png";
        String key = "key";
        String output = "output";
        vc = new VigenereCipher(input, output, key);
        vc.execute();
        System.out.println(vc);
    }
}