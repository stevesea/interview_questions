package org.stevesea;

/*
 * locate index of int array where sum(prefix elements) == sum(suffix elements)
 *
 * return -1 if no pivot
 */
public class FindPivotIndex {
    public int findPivot(int[] inArray) {
        // so this method can be O(N) instead of O(N^2), iterate just once over list
        //   - iterate from both directions, and keep track of sum to left/right of current index.
        long[] leftSums = new long[inArray.length];
        long[] rightSums = new long[inArray.length];
        int i = 0;
        int j = inArray.length - 1;
        while (i < inArray.length && j >= 0) {
            long sumleft = 0;
            if (i > 0) {
                sumleft = leftSums[i - 1] + inArray[i - 1];
            }
            long sumright = 0;
            if (j < inArray.length - 1) {
                sumright = rightSums[j + 1] + inArray[j + 1];
            }

            leftSums[i] = sumleft;
            rightSums[j] = sumright;

            if (j <= i) {
                if (leftSums[i] == rightSums[i])
                    return i;
                else if (leftSums[j] == rightSums[j])
                    return j;
            }

            i++;
            j--;
        }
        return -1;
    }
}
