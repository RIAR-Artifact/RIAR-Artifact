import java.util.Random;
import checksum.check_sum;
import compiler.c2.cr6340864.TestLongVect;

// 
// Decompiled by Procyon v0.6.0
// 

public class TestElasticTLAB
{
    public static long SEED;
    public static int COUNT;
    public static byte[][] arr;
    public static int my_check_sum;
    
    static {
        TestElasticTLAB.SEED = Long.getLong("seed", System.nanoTime());
        final Integer integer = Integer.getInteger("count", 3000);
        int n = 0;
        final long[] array2;
        final long[] array = array2 = new long[3];
        final int n2 = n;
        if (n >= 997) {
            TestLongVect.test_mula(array, array2, array2);
            TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, 0);
        }
        else {
            final int n3 = n2;
            final String s = "test_mulv: ";
            final int n4 = n;
            final long n5 = array[n];
            final long n6 = 9223372036854775307L;
            final long n7 = n;
            final long n8 = n6 + n7;
            TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n7);
            final long n9 = n8 * 31L;
            TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n8);
            final int verify = TestLongVect.verify(s, n4, n5, n9);
            TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n9);
            TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n5);
            TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n3 + verify);
            TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, verify);
            ++n;
            TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n);
        }
        TestElasticTLAB.COUNT = integer;
        TestElasticTLAB.my_check_sum = 0;
        TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, TestElasticTLAB.my_check_sum);
    }
    
    public static void main(final String[] array) throws Exception {
        final long seed = TestElasticTLAB.SEED;
        final Random random = new Random(seed);
        TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, seed);
        final Random random2 = random;
        final int count = TestElasticTLAB.COUNT;
        final int n = count * 100;
        TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, count);
        final byte[][] arr = new byte[n][];
        TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n);
        TestElasticTLAB.arr = arr;
        for (int i = 0; i < TestElasticTLAB.COUNT; ++i, TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, i)) {
            final byte[][] arr2 = TestElasticTLAB.arr;
            final int n2 = i;
            final int n3 = i * 100;
            final byte[] array2 = new byte[n3];
            TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n3);
            arr2[n2] = array2;
            for (int j = 0; j < i; ++j, TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, j)) {
                final byte[] array3 = TestElasticTLAB.arr[i];
                final int n4 = j;
                final int nextInt = random2.nextInt(255);
                final int n5 = nextInt & 0xFF;
                TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, nextInt);
                final byte b = (byte)n5;
                TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n5);
                array3[n4] = b;
                TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, b);
            }
        }
        final long seed2 = TestElasticTLAB.SEED;
        final Random random3 = new Random(seed2);
        TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, seed2);
        final Random random4 = random3;
        for (int k = 0; k < TestElasticTLAB.COUNT; ++k, TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, k)) {
            final byte[] array4 = TestElasticTLAB.arr[k];
            if (array4.length != k * 100) {
                final StringBuilder sb = new StringBuilder("Failure: length = ");
                final int length = array4.length;
                final StringBuilder append = sb.append(length);
                TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, length);
                final StringBuilder append2 = append.append(", need = ");
                final int l = k * 100;
                final StringBuilder append3 = append2.append(l);
                TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, l);
                throw new IllegalStateException(append3.toString());
            }
            for (int n6 = 0; n6 < k; ++n6, TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n6)) {
                final byte m = array4[n6];
                final int nextInt2 = random4.nextInt(255);
                final int n7 = nextInt2 & 0xFF;
                TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, nextInt2);
                final byte i2 = (byte)n7;
                TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, n7);
                if (m != i2) {
                    final StringBuilder append4 = new StringBuilder("Failure: expected = ").append(i2);
                    TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, i2);
                    final StringBuilder append5 = append4.append(", actual = ").append(m);
                    TestElasticTLAB.my_check_sum = check_sum.run(TestElasticTLAB.my_check_sum, m);
                    throw new IllegalStateException(append5.toString());
                }
            }
        }
        System.out.print("my_check_sum_value:");
        System.out.println(TestElasticTLAB.my_check_sum);
    }
}
