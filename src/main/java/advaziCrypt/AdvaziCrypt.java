package advaziCrypt;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AdvaziCrypt {
/** constants **/
    private static final String ADVAZI_CRYPT_FILE_FIVE = "assets/advaziCrypt/n04.txt.enc";
    private static final String ADVAZI_CRYPT_FILE_SIX = "assets/advaziCrypt/n05.txt.enc";
    private static final String ADVAZI_CRYPT_FILE_SEVEN = "assets/advaziCrypt/n06.txt.enc";
/** variables **/
    private static byte[][] advaziFiles;
    private static List<Integer> keylengthList = new ArrayList<>();
    private static byte lengthOfPadding;
    private static byte[] key;
    private static byte[] result;
    private static int keySize;

    public static void decryptAdvaziCryptFiles() {
        readInAdvaziCryptFiles();
        for (int i = 0; i < 3; i++) {
            getPossibleKeyLengths(advaziFiles[i]);
            byte[] cipher = advaziFiles[i];
            keySize = findMostPopularSequenceLength(keylengthList);
            getPaddingLength(cipher);
            printInformation();
            calculateKey(cipher, lengthOfPadding);
            cipher = removePadding(cipher, lengthOfPadding);
            getPlainByte(cipher, key);
            printResult();
        }
    }

    private static void printResult() {
        try {
            System.out.println(new String(result, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
/** prints key size and length of padding **/
    private static void printInformation() {
        System.out.println("Länge der sich meisten wiederholenden Sequenz: " + keySize);
        System.out.println("Länge des Paddings: " + lengthOfPadding);
    }

    private static void getPaddingLength(byte[] chiff) {
        byte padd_size = 0x00;
        byte[] keyInPadd = Arrays.copyOfRange(chiff, chiff.length - keySize, chiff.length);

        int currChiffre = chiff.length - 1;
        int currKey = keySize - 1;
        while (true) {
            //End of padding found if currKey byte is not equal to currChiffre byte
            if (chiff[currChiffre] != keyInPadd[currKey]) {
                lengthOfPadding = padd_size;
                break;
            }

            currChiffre--;
            currKey--;
            if (currKey < 0) {
                currKey = keySize - 1;
            }
            padd_size++;
        }
    }

    private static void calculateKey(byte[] chiff, byte padd_size) {
        for (int counter = 0; counter < padd_size; counter++) {
            chiff[chiff.length - 1 - counter] = (byte) (chiff[chiff.length - 1 - counter] ^ padd_size);
        }

        key = new byte[keySize];
        for (int i = 0; i < keySize; i++) {
            key[keySize - 1 - i] = chiff[chiff.length - 1 - i];
        }
    }

    private static byte[] removePadding(byte[] chiff, byte padd_size) {
        byte[] unpaddedChiff = new byte[chiff.length - padd_size];
        for (int i = 0; i < unpaddedChiff.length; i++) {
            unpaddedChiff[i] = chiff[i];
        }
        return unpaddedChiff;
    }

/** calculates plain result text **/
    private static void getPlainByte(byte[] chiffreText, byte[] ourKey) {
        result = new byte[chiffreText.length];
        for (int i = 0; i < chiffreText.length; i++) {
            int position = i % (ourKey.length);
            result[i] = (byte) (chiffreText[i] ^ ourKey[position]);
        }
    }
/** reads in all advaziFiles into byte[][] **/
    private static void readInAdvaziCryptFiles() {
        advaziFiles = new byte[3][];
        try {
            advaziFiles[0] = Files.readAllBytes(Paths.get(ADVAZI_CRYPT_FILE_FIVE));
            advaziFiles[1] = Files.readAllBytes(Paths.get(ADVAZI_CRYPT_FILE_SIX));
            advaziFiles[2] = Files.readAllBytes(Paths.get(ADVAZI_CRYPT_FILE_SEVEN));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

/** checks file for repeating sequences **/
    private static void getPossibleKeyLengths(byte[] cipher) {
        List<List<Byte>> keyPossibilities = new ArrayList<>();
        for (int startPos = 0; startPos < cipher.length; startPos++) {
            for (int sequenceLength = 1; sequenceLength <= (cipher.length - startPos) / 2; sequenceLength++) {
                boolean sequencesAreEqual = true;
                for (int i = 0; i < sequenceLength; i++) {
                    if (cipher[startPos + i] != cipher[startPos + sequenceLength + i]) {
                        sequencesAreEqual = false;
                        break;
                    }
                }
                if (sequencesAreEqual) {
                    List<Byte> byteArray = new ArrayList<>();
                    for (int i = startPos; i < (startPos + sequenceLength); i++) {
                        byteArray.add(cipher[i]);
                    }
                    keyPossibilities.add(byteArray);
                    for (List<Byte> keyPossibility : keyPossibilities) {
                        keylengthList.add(keyPossibility.size());
                    }
                }
            }
        }
    }

    /** returns most populated integer in list **/
    private static int findMostPopularSequenceLength(List<Integer> a) {

        if (a == null || a.size() == 0)
            return 0;

        Collections.sort(a);

        int previous = a.get(0);
        int popular = a.get(0);
        int count = 1;
        int maxCount = 1;

        for (int i = 1; i < a.size(); i++) {
            if (a.get(i) == previous)
                count++;
            else {
                if (count > maxCount) {
                    popular = a.get(i - 1);
                    maxCount = count;
                }
                previous = a.get(i);
                count = 1;
            }
        }
        return count > maxCount ? a.get(a.size() - 1) : popular;
    }
}
