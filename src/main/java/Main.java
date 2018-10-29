import advaziCrypt.AdvaziCrypt;
import baziCrypt.*;
import java.io.UnsupportedEncodingException;

public class Main {


    public static void main(String[] args) throws UnsupportedEncodingException {
        // baziCrypt();
         advaziCrypt();
    }

    private static void advaziCrypt() throws UnsupportedEncodingException {
        AdvaziCrypt.decryptAdvaziCryptFiles();
    }

    private static void baziCrypt() throws UnsupportedEncodingException {
        BaziCrypt.decryptBaziCryptFiles();
    }
}