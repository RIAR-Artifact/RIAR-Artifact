// 
// Decompiled by Procyon v0.6.0
// 

package compiler.runtime;

import compiler.codegen.Test7100757;
import java.util.BitSet;
import java.util.Random;
import checksum.check_sum;

public class Test6892265
{
    static final int NCOPY = 1;
    static final int OVERFLOW = 1;
    public static int[] src2;
    public static int[] dst2;
    public static int my_check_sum;
    
    static {
        Test6892265.src2 = new int[1];
        Test6892265.my_check_sum = 0;
        Test6892265.my_check_sum = check_sum.run(Test6892265.my_check_sum, Test6892265.my_check_sum);
        final long[] array = new long[3];
        int n = 0;
        final Random random = new Random();
        final BitSet set = new BitSet();
        int n2 = n;
        final int n3 = n;
        final int my_check_sum = Test6892265.my_check_sum;
        final int length = array.length;
        Test6892265.my_check_sum = check_sum.run(my_check_sum, length);
        if (n3 >= length) {
            Test7100757.test(array, set);
            ++n2;
            Test6892265.my_check_sum = check_sum.run(Test6892265.my_check_sum, n2);
        }
        final long[] array2 = array;
        final int n4 = n;
        final long nextLong = random.nextLong();
        array2[n4] = nextLong;
        Test6892265.my_check_sum = check_sum.run(Test6892265.my_check_sum, nextLong);
        ++n;
        Test6892265.my_check_sum = check_sum.run(Test6892265.my_check_sum, n);
    }
    
    public static void main(final String[] array) {
        try {
            Test6892265.dst2 = new int[536870913];
        }
        catch (final OutOfMemoryError outOfMemoryError) {
            System.exit(95);
        }
        System.out.println("warmup");
        for (int i = 0; i < 11000; ++i, Test6892265.my_check_sum = check_sum.run(Test6892265.my_check_sum, i)) {
            test();
            Test6892265.my_check_sum = check_sum.run(Test6892265.my_check_sum, 989);
        }
        System.out.println("start");
        for (int j = 0; j < 1000; ++j, Test6892265.my_check_sum = check_sum.run(Test6892265.my_check_sum, j)) {
            test();
        }
        System.out.println("finish");
        System.out.print("my_check_sum_value:");
        System.out.println(Test6892265.my_check_sum);
    }
    
    public static void test() {
        final int[] src2 = Test6892265.src2;
        final int n = 0;
        final int[] dst2 = Test6892265.dst2;
        final int n2 = 536870912;
        System.arraycopy(src2, n, dst2, n2, 1);
        System.arraycopy(Test6892265.dst2, n2, Test6892265.src2, 0, 1);
        Test6892265.my_check_sum = check_sum.run(Test6892265.my_check_sum, n2);
    }
}
