// 
// Decompiled by Procyon v0.6.0
// 

package compiler.runtime;

import checksum.check_sum;

public class StackOverflowBug
{
    public static int my_check_sum;
    
    static {
        StackOverflowBug.my_check_sum = 0;
        StackOverflowBug.my_check_sum = check_sum.run(StackOverflowBug.my_check_sum, StackOverflowBug.my_check_sum);
    }
    
    public static void main(final String[] array) {
        final byte[] array2 = new byte[100];
        final long[] array3 = new long[0];
        StackOverflowBug.my_check_sum = check_sum.run(StackOverflowBug.my_check_sum, 0);
        run();
        System.out.print("my_check_sum_value:");
        System.out.println(StackOverflowBug.my_check_sum);
    }
    
    public static int run() {
        new Integer(100);
        final byte b = 1;
        new Integer(0);
        final int value = b + 1;
        final int run = check_sum.run(StackOverflowBug.my_check_sum, (int)b);
        final byte[] array = { 0 };
        final int n = value;
        final byte b2 = b;
        final short[] array2 = new short[0];
        final short n2 = 1;
        final int n3 = n;
        final int my_check_sum = StackOverflowBug.my_check_sum;
        final int length = array.length;
        final int n4 = length - 5;
        StackOverflowBug.my_check_sum = check_sum.run(my_check_sum, n4);
        StackOverflowBug.my_check_sum = check_sum.run(StackOverflowBug.my_check_sum, length);
        if (n3 < n4) {
            array[n] = b2;
            StackOverflowBug.my_check_sum = check_sum.run(StackOverflowBug.my_check_sum, b2);
            final short[] array3 = array2;
            final int n5 = n + 5;
            array3[n5] = n2;
            StackOverflowBug.my_check_sum = check_sum.run(StackOverflowBug.my_check_sum, n2);
            StackOverflowBug.my_check_sum = check_sum.run(StackOverflowBug.my_check_sum, n5);
            StackOverflowBug.my_check_sum = check_sum.run(StackOverflowBug.my_check_sum, n + 1);
        }
        StackOverflowBug.my_check_sum = run;
        final Integer n6 = new Integer(value);
        StackOverflowBug.my_check_sum = check_sum.run(StackOverflowBug.my_check_sum, value);
        try {
            return run();
        }
        catch (final Throwable t) {
            return 42;
        }
    }
}
