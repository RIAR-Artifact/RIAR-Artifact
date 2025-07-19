// 
// Decompiled by Procyon v0.6.0
// 

package compiler.intrinsics.bigInteger;

import java.io.PrintStream;
import jit.regression.b4427606.b4427606;
import java.util.Random;
import java.math.BigInteger;
import checksum.check_sum;

public class TestMulAdd
{
    public static int my_check_sum;
    
    static {
        TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, new String[0].length);
        TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, 0);
        TestMulAdd.my_check_sum = 0;
        TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, TestMulAdd.my_check_sum);
    }
    
    public static BigInteger base_multiply(final BigInteger val) {
        return val.multiply(val);
    }
    
    public static boolean bytecompare(final BigInteger bigInteger, final BigInteger bigInteger2) {
        final byte[] byteArray = bigInteger.toByteArray();
        final byte[] byteArray2 = bigInteger2.toByteArray();
        if (byteArray.length != byteArray2.length) {
            return false;
        }
        for (int i = 0; i < byteArray.length; ++i, TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, i), TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, (byte)6)) {
            if (byteArray[i] != byteArray2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static void main(final String[] array) throws Exception {
        BigInteger add = new BigInteger("0");
        BigInteger add2 = new BigInteger("0");
        final Random rnd = new Random();
        final long nanoTime = System.nanoTime();
        final Random random = new Random();
        final long nanoTime2 = System.nanoTime();
        rnd.setSeed(nanoTime);
        TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, nanoTime);
        random.setSeed(nanoTime2);
        TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, nanoTime2);
        for (int i = 0; i < 100000; ++i, TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, i)) {
            final int nextInt = random.nextInt(3136);
            final int numBits = nextInt + 32;
            TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, nextInt);
            final BigInteger bigInteger = new BigInteger(numBits, rnd);
            TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, numBits);
            final BigInteger x = bigInteger;
            final BigInteger base_multiply = base_multiply(x);
            final BigInteger new_multiply = new_multiply(x);
            add = add.add(base_multiply);
            add2 = add2.add(new_multiply);
            if (!bytecompare(base_multiply, new_multiply)) {
                System.out.print("mismatch for:b1:" + stringify(x) + " :oldres:" + stringify(base_multiply) + " :newres:" + stringify(new_multiply));
                System.out.println(x);
                throw new Exception("Failed");
            }
        }
        if (!bytecompare(add, add2)) {
            final PrintStream out = System.out;
            final StringBuilder append = new StringBuilder("Failure: oldsum:").append(stringify(add)).append(" newsum:");
            final String[] array2 = new String[2];
            new b4427606().run();
            out.println(append.append(stringify(add2)).toString());
            throw new Exception("Failed");
        }
        System.out.println("Success");
        System.out.print("my_check_sum_value:");
        System.out.println(TestMulAdd.my_check_sum);
    }
    
    public static BigInteger new_multiply(final BigInteger val) {
        return val.multiply(val);
    }
    
    public static String stringify(final BigInteger bigInteger) {
        final double[] array = new double[3];
        final long[] array2 = new long[100];
        String string = "";
        final byte[] byteArray = bigInteger.toByteArray();
        for (int i = 0; i < byteArray.length; ++i, TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, i)) {
            final StringBuilder sb = new StringBuilder(String.valueOf(string));
            final Object[] args = { null };
            final float[] array3 = new float[0];
            final int n = i;
            final String[] array4 = new String[100];
            final boolean[] array5 = new boolean[100];
            final boolean[] array6 = new boolean[2001];
            (new boolean[2001])[1000] = true;
            (new boolean[2001])[2000] = true;
            TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, 0);
            final float[] array7 = array3;
            final float n2 = 2.0f;
            final int n3 = n;
            final int my_check_sum = TestMulAdd.my_check_sum;
            final int length = array3.length;
            TestMulAdd.my_check_sum = check_sum.run(my_check_sum, length);
            if (n3 < length) {
                final float n4 = n2;
                final float n5 = array3[n];
                final float n6 = array7[n];
                final float n7 = n5 - n6;
                TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, n6);
                TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, n5);
                TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, n4 * n7);
                TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, n7);
                TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, n + 1);
            }
            final Object[] array8 = args;
            final int n8 = 0;
            final byte b = byteArray[i];
            final Byte value = b;
            TestMulAdd.my_check_sum = check_sum.run(TestMulAdd.my_check_sum, b);
            array8[n8] = value;
            string = sb.append(String.format("%02x", args)).append(" ").toString();
        }
        return string;
    }
}
