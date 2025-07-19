import java.io.PrintStream;
import java.util.Random;
import checksum.check_sum;

// 
// Decompiled by Procyon v0.6.0
// 

public class TestByteArrays
{
    public static long SEED;
    public static int COUNT;
    public static byte[][] arr;
    public static int my_check_sum;
    
    static {
        TestByteArrays.SEED = Long.getLong("seed", System.nanoTime());
        TestByteArrays.COUNT = Integer.getInteger("count", 3000);
        TestByteArrays.my_check_sum = 0;
        TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, TestByteArrays.my_check_sum);
    }
    
    public static void main(final String[] array) throws Exception {
        final long seed = TestByteArrays.SEED;
        final Random random = new Random(seed);
        TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, seed);
        final Random random2 = random;
        final int count = TestByteArrays.COUNT;
        final int n = count * 100;
        TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, count);
        final byte[][] arr = new byte[n][];
        TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, n);
        TestByteArrays.arr = arr;
        for (int i = 0; i < TestByteArrays.COUNT; ++i, TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, i)) {
            final byte[][] arr2 = TestByteArrays.arr;
            final int n2 = i;
            final int n3 = i * 100;
            final byte[] array2 = new byte[n3];
            TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, n3);
            arr2[n2] = array2;
            for (int j = 0; j < i; ++j, TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, j)) {
                final byte[] array3 = TestByteArrays.arr[i];
                final int n4 = j;
                final int nextInt = random2.nextInt(255);
                final int n5 = nextInt & 0xFF;
                TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, nextInt);
                final byte b = (byte)n5;
                TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, n5);
                array3[n4] = b;
                TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, b);
            }
        }
        final long seed2 = TestByteArrays.SEED;
        final Random random3 = new Random(seed2);
        TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, seed2);
        final Random random4 = random3;
        for (int k = 0; k < TestByteArrays.COUNT; ++k, TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, k)) {
            final byte[] array4 = TestByteArrays.arr[k];
            if (array4.length != k * 100) {
                final StringBuilder sb = new StringBuilder("Failure: length = ");
                final int length = array4.length;
                final StringBuilder append = sb.append(length);
                TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, length);
                final StringBuilder append2 = append.append(", need = ");
                final int l = k * 100;
                final StringBuilder append3 = append2.append(l);
                final int my_check_sum = TestByteArrays.my_check_sum;
                new PrintStream("");
                System.out.println("Test constprop02 Passed.");
                TestByteArrays.my_check_sum = check_sum.run(my_check_sum, l);
                throw new IllegalStateException(append3.toString());
            }
            for (int n6 = 0; n6 < k; ++n6, TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, n6)) {
                final byte m = array4[n6];
                final int nextInt2 = random4.nextInt(255);
                final int n7 = nextInt2 & 0xFF;
                TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, nextInt2);
                final byte i2 = (byte)n7;
                TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, n7);
                if (m != i2) {
                    final StringBuilder append4 = new StringBuilder("Failure: expected = ").append(i2);
                    TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, i2);
                    final StringBuilder append5 = append4.append(", actual = ").append(m);
                    TestByteArrays.my_check_sum = check_sum.run(TestByteArrays.my_check_sum, m);
                    throw new IllegalStateException(append5.toString());
                }
            }
        }
        System.out.print("my_check_sum_value:");
        System.out.println(TestByteArrays.my_check_sum);
    }
}
