// 
// Decompiled by Procyon v0.6.0
// 

package compiler.exceptions;

import java.io.PrintStream;
import compiler.eliminateAutobox.TestLongBoxing;
import checksum.check_sum;
import compiler.codegen.LoadWithMask;

public class TestRecursiveReplacedException
{
    public static int my_check_sum;
    
    static {
        TestRecursiveReplacedException.my_check_sum = 0;
        final int my_check_sum = TestRecursiveReplacedException.my_check_sum;
        final int my_check_sum2 = TestRecursiveReplacedException.my_check_sum;
        LoadWithMask.x = new int[1];
        final int run = check_sum.run(my_check_sum, my_check_sum2);
        new Long(0L);
        final Long n = new Long(100L);
        final int n3;
        final int n2 = n3 = 2;
        final int[] array = new int[100];
        final int n4 = n2 + n3;
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n3);
        final int n5 = n4;
        final int n6 = n3;
        array[n5] = n6;
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n6);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n4);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n2 + 1);
        final long longValue = n;
        final long n7 = longValue + 1L;
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, longValue);
        final long foo = TestLongBoxing.foo(n7);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n7);
        final Long n8 = new Long(foo);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, foo);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, 0 + 1);
        TestRecursiveReplacedException.my_check_sum = run;
        final char[] array2 = { '\0' };
        int n9 = n4;
        final char c = '1';
        final short[] array3 = new short[2];
        final short n10 = 0;
        final int n11 = n9;
        final int my_check_sum3 = TestRecursiveReplacedException.my_check_sum;
        final int length = array2.length;
        final int n12 = length - 8;
        TestRecursiveReplacedException.my_check_sum = check_sum.run(my_check_sum3, n12);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, length);
        if (n11 < n12) {
            array2[n9] = c;
            TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, c);
            final short[] array4 = array3;
            final int n13 = n9 + 8;
            array4[n13] = n10;
            TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n10);
            TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n13);
            ++n9;
            TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n9);
        }
    }
    
    public static void main(final String[] array) {
        new TestRecursiveReplacedException().run();
        System.out.print("my_check_sum_value:");
        System.out.println(TestRecursiveReplacedException.my_check_sum);
    }
    
    public void run() {
        try {
            this.run();
            final byte b = 100;
            TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, b);
            final PrintStream printStream = new PrintStream("*/");
            if (b != b) {
                printStream.println("TEST FAILED");
            }
        }
        catch (final Throwable t) {}
    }
}
