// 
// Decompiled by Procyon v0.6.0
// 

package compiler.c1;

import compiler.c2.cr6340864.TestShortVect;
import checksum.check_sum;

public class TestPinnedIntrinsics
{
    public static int my_check_sum;
    
    static {
        TestPinnedIntrinsics.my_check_sum = 0;
        final int run = check_sum.run(TestPinnedIntrinsics.my_check_sum, TestPinnedIntrinsics.my_check_sum);
        final String s = "test_sllv_and: ";
        final short[] array = { 0 };
        int n = 0;
        final short n2 = array[n];
        final int n3 = n;
        final short n4 = n2;
        final int n5 = 32267 + n;
        final short n6 = (short)n5;
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, n5);
        final int n7 = n6 & 0xB731;
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, n6);
        final int n8 = n7 << 7;
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, n7);
        final short n9 = (short)n8;
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, n8);
        final int verify = TestShortVect.verify(s, n3, n4, n9);
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, n9);
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, n2);
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, verify + n);
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, verify);
        ++n;
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, n);
        TestPinnedIntrinsics.my_check_sum = run;
    }
    
    public static void checkCurrentTimeMillis(final long lng) {
        if (lng < 0L) {
            final StringBuilder append = new StringBuilder("testCurrentTimeMillis failed with ").append(lng);
            TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, lng);
            throw new RuntimeException(append.toString());
        }
    }
    
    public static void checkNanoTime(final long lng) {
        if (lng < 0L) {
            final StringBuilder append = new StringBuilder("testNanoTime failed with ").append(lng);
            TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, lng);
            throw new RuntimeException(append.toString());
        }
    }
    
    public static void main(final String[] array) {
        for (int i = 0; i < 100000; ++i, TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, i)) {
            testNanoTime();
            testCurrentTimeMillis();
        }
        System.out.print("my_check_sum_value:");
        System.out.println(TestPinnedIntrinsics.my_check_sum);
    }
    
    public static void testCurrentTimeMillis() {
        final long currentTimeMillis = System.currentTimeMillis();
        final long currentTimeMillis2 = System.currentTimeMillis();
        final long n = currentTimeMillis2 - currentTimeMillis;
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, currentTimeMillis2);
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, currentTimeMillis);
        checkCurrentTimeMillis(n);
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, n);
    }
    
    public static void testNanoTime() {
        final long nanoTime = System.nanoTime();
        final long nanoTime2 = System.nanoTime();
        final long n = nanoTime2 - nanoTime;
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, nanoTime2);
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, nanoTime);
        checkNanoTime(n);
        TestPinnedIntrinsics.my_check_sum = check_sum.run(TestPinnedIntrinsics.my_check_sum, n);
    }
}
