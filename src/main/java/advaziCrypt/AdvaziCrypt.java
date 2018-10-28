package advaziCrypt;

import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AdvaziCrypt {

    private static final String ADVAZI_CRYPT_FILE_FIVE = "assets/advaziCrypt/n04.txt.enc";
    private static final String ADVAZI_CRYPT_FILE_SIX = "assets/advaziCrypt/n05.txt.enc";
    private static final String ADVAZI_CRYPT_FILE_SEVEN = "assets/advaziCrypt/n06.txt.enc";

    private static byte[][] advaziFiles;
    private static List<Integer> keylengthList = new ArrayList<>();
    private static byte lengthOfPadding;
    private static byte[] key;


    private static int keySize;

    public static void decryptAdvaziCryptFiles() throws UnsupportedEncodingException {
        readInBaziCryptFiles();
        for (int i = 0; i < 3; i++) {
            getPossibleKeyLengths(advaziFiles[i]);
            byte[] cipher = advaziFiles[i];
            keySize = findPopular(keylengthList);
            getPaddingLength(cipher);
            System.out.println("Länge der sich meisten wiederholenden Sequenz: " + keySize);
            System.out.println("Länge des Paddings: " + lengthOfPadding);

            calculateKey(cipher, lengthOfPadding);

            //remove padding
            cipher = removePadding(cipher, lengthOfPadding);

            //decrypt chiffre
            byte[] plainText = getPlainByte(cipher, key);

            try {
                System.out.println(new String(plainText, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
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


    private static byte[] getPlainByte(byte[] chiffreText, byte[] ourKey) {
        byte[] plain = new byte[chiffreText.length];
        for (int i = 0; i < chiffreText.length; i++) {
            int position = i % (ourKey.length);
            // XOR chiffre with key
            plain[i] = (byte) (chiffreText[i] ^ ourKey[position]);
        }
        return plain;
    }

    private static void readInBaziCryptFiles() {
        advaziFiles = new byte[3][];
        try {
            advaziFiles[0] = Files.readAllBytes(Paths.get(ADVAZI_CRYPT_FILE_FIVE));
            advaziFiles[1] = Files.readAllBytes(Paths.get(ADVAZI_CRYPT_FILE_SIX));
            advaziFiles[2] = Files.readAllBytes(Paths.get(ADVAZI_CRYPT_FILE_SEVEN));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void getPossibleKeyLengths(byte[] cipher) throws UnsupportedEncodingException {
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
                    // System.out.println("Found repeating sequence at pos " + startPos + " Endposition: " + (startPos + sequenceLength));
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

    private static int findPopular(List<Integer> a) {

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
