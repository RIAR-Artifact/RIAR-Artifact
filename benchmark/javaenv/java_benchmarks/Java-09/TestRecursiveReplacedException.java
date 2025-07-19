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
        final int n5 = n3;
        array[n4] = n5;
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n5);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n4);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n2 + 1);
        final long longValue = n;
        final long n6 = longValue + 1L;
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, longValue);
        final long foo = TestLongBoxing.foo(n6);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, n6);
        final Long n7 = new Long(foo);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, foo);
        TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, 0 + 1);
        TestRecursiveReplacedException.my_check_sum = run;
    }
    
    public static void main(final String[] array) {
        new TestRecursiveReplacedException().run();
        System.out.print("my_check_sum_value:");
        System.out.println(TestRecursiveReplacedException.my_check_sum);
    }
    
    public void run() {
        try {
            this.run();
            final int n2;
            final int n;
            TestRecursiveReplacedException.my_check_sum = check_sum.run(TestRecursiveReplacedException.my_check_sum, (byte)(n = (n2 = 100)));
            final PrintStream printStream = new PrintStream("*/");
            if (n2 != n) {
                printStream.println("TEST FAILED");
            }
        }
        catch (final Throwable t) {}
    }
}
