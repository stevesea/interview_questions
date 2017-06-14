package org.stevesea;

/*
 * locate index of int array where sum(prefix elements) == sum(suffix elements)
 *
 * return -1 if no pivot
 */
public class FindPivotIndex {
    public int findPivot(int[] A) {
        long[] leftSums = new long[A.length];
        long[] rightSums = new long[A.length];
        int i = 0;
        int j = A.length - 1;
        while (i < A.length && j >= 0) {
            long sumleft = 0;
            if (i > 0) {
                sumleft = leftSums[i - 1] + A[i - 1];
            }
            long sumright = 0;
            if (j < A.length - 1) {
                sumright = rightSums[j + 1] + A[j + 1];
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
