package com.amo.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Created by ayeminoo on 1/8/18.
 */
public class SubArrayCollection {
    private byte[] array = new byte[0];
    List<SubArray> subArrays = new ArrayList<>();
    public class SubArray{
        private int startIndex;
        private int length;

        private SubArray(int startIndex, int length){
            this.startIndex = startIndex;
            this.length = length;
        }

        public byte[] getSubArray(){
            return Arrays.copyOfRange(array, startIndex, startIndex + length);
        }
    }

    public SubArrayCollection(byte[] array, int lengthOfSubArray){
        this.array = Objects.requireNonNull(array);
        if(lengthOfSubArray <= 0) throw new RuntimeException("invalid lengthOfSubArray");
        int length = array.length;
        int index = 0;
        while(length> lengthOfSubArray){
            subArrays.add(new SubArray(index, lengthOfSubArray));
            length -= lengthOfSubArray;
            index += lengthOfSubArray;
        }
        subArrays.add(new SubArray(index, length));
    }

    public List<SubArray> getSubArrays(){
        return subArrays;
    }
}

