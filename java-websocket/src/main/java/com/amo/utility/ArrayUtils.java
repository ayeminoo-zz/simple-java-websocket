package com.amo.utility;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ayeminoo on 4/9/17.
 */
public class ArrayUtils {

    public static <T> T concatenate(T a, T b) {
        if (!a.getClass().isArray() || !b.getClass().isArray()) {
            throw new IllegalArgumentException();
        }

        Class<?> resCompType;
        Class<?> aCompType = a.getClass().getComponentType();
        Class<?> bCompType = b.getClass().getComponentType();

        if (aCompType.isAssignableFrom(bCompType)) {
            resCompType = aCompType;
        } else if (bCompType.isAssignableFrom(aCompType)) {
            resCompType = bCompType;
        } else {
            throw new IllegalArgumentException();
        }

        int aLen = Array.getLength(a);
        int bLen = Array.getLength(b);

        @SuppressWarnings("unchecked")
        T result = (T) Array.newInstance(resCompType, aLen + bLen);
        System.arraycopy(a, 0, result, 0, aLen);
        System.arraycopy(b, 0, result, aLen, bLen);

        return result;
    }

    public static int[] getSeive(int number){
        int[]  seive= new int[number+1];
        // write your code in Java SE 8

        for(int i = 2 ; (long)i * i <= number; i++){
            int j= 2;
            while(j*i <= number){
                seive[j*i] = seive[j*i] == 0? Math.min(j,i): Math.min(seive[i*j], Math.min(j, i));
                j++;
            }
        }
        return seive;
    }

    public static List<Integer> toList(int[]A){
        List<Integer> myList = new ArrayList<>(A.length);
        for(int value: A){
            myList.add(value);
        }
        return myList;
    }

    public static List<Long> toLongList(int[]A){
        List<Long> myList = new ArrayList<>(A.length);
        for(int value: A){
            myList.add(Long.valueOf(value));
        }
        return myList;
    }

    //assume array is in sorted form
    public static int binaryPossibleMaxSearch(int value, Integer[]array){
        if(array.length == 1 && array[0] > value) return array[0];
        if(array.length == 1)return Integer.MIN_VALUE;
        int left = 0;
        int right = array.length - 1;
        int max = Integer.MIN_VALUE;
        while(left <= right){
            int mid = left + (right - left)/2;

            if(array[mid] == value){
                return value;
            }
            if(array[mid]> value){
                right = mid -1;
                max = array[mid];
            }else{
                left = mid + 1;
            }
        }
        return max;
    }

    public static void main(String[]args){
        System.out.println(Integer.MIN_VALUE == binaryPossibleMaxSearch(3, new Integer[]{1,2}));
        System.out.println(4 == binaryPossibleMaxSearch(3, new Integer[]{4}));
        System.out.println(4 == binaryPossibleMaxSearch(3, new Integer[]{1,2, 4, 5}));
        System.out.println(4 == binaryPossibleMaxSearch(4, new Integer[]{1,2, 4, 5}));
        System.out.println(5 == binaryPossibleMaxSearch(4, new Integer[]{1,2,3, 5, 6}));
        System.out.println(1 == binaryPossibleMaxSearch(1, new Integer[]{1,2,3, 5, 6}));
        System.out.println(6 == binaryPossibleMaxSearch(6, new Integer[]{1,2,3, 5, 6}));
        System.out.println(6 == binaryPossibleMaxSearch(3, new Integer[]{6}));
    }
}
