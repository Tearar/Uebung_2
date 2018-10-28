package baziCrypt;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class BaziCrypt {

    private static final String BAZI_CRYPT_FILE_ONE = "assets/baziCrypt/n01.txt.enc";
    private static final String BAZI_CRYPT_FILE_TWO = "assets/baziCrypt/n02.txt.enc";
    private static final String BAZI_CRYPT_FILE_THREE = "assets/baziCrypt/n03.txt.enc";
    private static final String BAZI_CRYPT_FILE_FOUR = "assets/baziCrypt/n02_extended.txt.enc";


    private static byte[][] baziFiles;

    private static List<List<Byte>> keyPossibility = new ArrayList<>();
    private static List<List<Byte>> keyPossibilityWithoutDuplicates;

    public static void decryptBaziCryptFiles() throws UnsupportedEncodingException {
        readInBaziCryptFiles();
        getEncryptionKeyFromPadding(baziFiles[3]);
    }
    // for every position in the array:
    private static void getEncryptionKeyFromPadding(byte[] cipher) throws UnsupportedEncodingException {
        List<List<Byte>> keyPossibilities = new ArrayList<>();
        for (int startPos = 0; startPos < cipher.length; startPos++) {
            // check if there is a repeating sequence here:
            // check every sequence length which is lower or equal to half the
            // remaining array length: (this is important, otherwise we'll go out of bounds)
            for (int sequenceLength = 1; sequenceLength <= (cipher.length - startPos) / 2; sequenceLength++) {

                // check if the sequences of length sequenceLength which start
                // at startPos and (startPos + sequenceLength (the one
                // immediately following it)) are equal:
                boolean sequencesAreEqual = true;
                for (int i = 0; i < sequenceLength; i++) {
                    if (cipher[startPos + i] != cipher[startPos + sequenceLength + i]) {
                        sequencesAreEqual = false;
                        break;
                    }
                }
                if (sequencesAreEqual) {
                    System.out.println("Found repeating sequence at pos " + startPos + " Endposition: " + (startPos + sequenceLength));
                    List<Byte> byteArray = new ArrayList<>();
                    for (int i = startPos; i < (startPos + sequenceLength); i++) {
                        byteArray.add(cipher[i]);
                    }
                    keyPossibilities.add(byteArray);
                }
            }

        }

        keyPossibilityWithoutDuplicates = new ArrayList<>(
                new HashSet<>(keyPossibilities)); // remove duplicates


        for (int i = 0; i < keyPossibilityWithoutDuplicates.size(); i++) {
            int keyLength = keyPossibilityWithoutDuplicates.get(i).size();
            int counter = 0;
            byte[] arr = new byte[cipher.length];
            for (int j = 0; j < cipher.length; j++) {
                int cipherChar = (int) cipher[j];
                int keyChar = (int) keyPossibilityWithoutDuplicates.get(i).get(counter);
                int result = cipherChar ^ keyChar;
                byte b = (byte) (0xff & result);
                arr[j] = b;
                counter = counter + 1;
                if (counter == keyLength) {
                    counter = 0;
                }
            }
            System.out.println("Key: " + keyPossibilityWithoutDuplicates.get(i) + " Result: " + new String(arr, "UTF-8"));
        }
    }

    private static void readInBaziCryptFiles() {
        baziFiles = new byte[4][];
        try {
            baziFiles[0] = Files.readAllBytes(Paths.get(BAZI_CRYPT_FILE_ONE));
            baziFiles[1] = Files.readAllBytes(Paths.get(BAZI_CRYPT_FILE_TWO));
            baziFiles[2] = Files.readAllBytes(Paths.get(BAZI_CRYPT_FILE_THREE));
            baziFiles[3] = Files.readAllBytes(Paths.get(BAZI_CRYPT_FILE_FOUR)); // additional file
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
