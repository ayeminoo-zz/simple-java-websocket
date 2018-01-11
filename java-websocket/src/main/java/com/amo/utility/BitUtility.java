package com.amo.utility;

/**
 * Created by ayeminoo on 1/5/18.
 */
public class BitUtility {
    public static byte[] toBytes(boolean[] input) {
        byte[] toReturn = new byte[input.length / 8];
        for (int entry = 0; entry < toReturn.length; entry++) {
            for (int bit = 0; bit < 8; bit++) {
                if (input[entry * 8 + bit]) {
                    toReturn[entry] |= (128 >> bit);
                }
            }
        }

        return toReturn;
    }

    /**
     * convert given data into an array of bit(boolean)
     * @param data data to be converted to bits
     * @param arraysize size of bit array to be return
     * @return bit array in big indian representation
     */
    public static boolean[] toBitArray(int data, int arraySize){
        boolean bits[] = new boolean[arraySize];
        while(arraySize!=0 && data != 0){
            bits[--arraySize] = (data % 2) != 0;
            data /= 2;
        }
        return bits;
    }
}
