// 
// Decompiled by Procyon v0.6.0
// 

package compiler.intrinsics.bigInteger;

import compiler.codegen.TestCharVect;
import java.util.Arrays;
import java.util.Random;
import java.math.BigInteger;
import checksum.check_sum;

public class TestMultiplyToLen
{
    public static int my_check_sum;
    
    static {
        TestMultiplyToLen.my_check_sum = 0;
        TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, TestMultiplyToLen.my_check_sum);
    }
    
    public static BigInteger base_multiply(final BigInteger bigInteger, final BigInteger val) {
        return bigInteger.multiply(val);
    }
    
    public static boolean bytecompare(final BigInteger bigInteger, final BigInteger bigInteger2) {
        final byte[] byteArray = bigInteger.toByteArray();
        final byte[] byteArray2 = bigInteger2.toByteArray();
        if (byteArray.length != byteArray2.length) {
            return false;
        }
        for (int i = 0; i < byteArray.length; ++i, TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, i)) {
            if (byteArray[i] != byteArray2[i]) {
                return false;
            }
        }
        return true;
    }
    
    public static void main(final String[] array) throws Exception {
        BigInteger bigInteger = new BigInteger("0");
        BigInteger bigInteger2 = new BigInteger("0");
        final Random random2;
        final Random random = random2 = new Random();
        final long nanoTime = System.nanoTime();
        final Random random3 = new Random();
        final long nanoTime2 = System.nanoTime();
        random2.setSeed(nanoTime);
        TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, nanoTime);
        random3.setSeed(nanoTime2);
        TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, nanoTime2);
        for (int i = 0; i < 1000000; ++i, TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, i)) {
            final int nextInt = random3.nextInt(3136);
            TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, nextInt);
            final int nextInt2 = random3.nextInt(3136);
            TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, nextInt2);
            final int numBits = nextInt + 32;
            final BigInteger bigInteger3 = new BigInteger(numBits, random);
            TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, numBits);
            final BigInteger x = bigInteger3;
            final int numBits2 = nextInt2 + 32;
            final BigInteger bigInteger4 = new BigInteger(numBits2, random);
            TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, numBits2);
            final BigInteger x2 = bigInteger4;
            final BigInteger base_multiply = base_multiply(x, x2);
            final BigInteger new_multiply = new_multiply(x, x2);
            bigInteger = bigInteger.add(base_multiply);
            bigInteger2 = bigInteger2.add(new_multiply);
            if (!bytecompare(base_multiply, new_multiply)) {
                System.out.println(x);
                System.out.println(x2);
                System.out.print("mismatch for:b1:" + stringify(x) + " :b2:" + stringify(x2) + " :oldres:" + stringify(base_multiply) + " :newres:" + stringify(new_multiply));
                throw new Exception("Failed");
            }
        }
        for (int j = 4; j <= 396; j += 4, TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, j)) {
            final byte[] val = new byte[j];
            Arrays.fill(val, (byte)(-1));
            final BigInteger x3 = new BigInteger(val);
            final BigInteger x4 = new BigInteger(val);
            final BigInteger base_multiply2 = base_multiply(x3, x4);
            final BigInteger new_multiply2 = new_multiply(x3, x4);
            bigInteger = bigInteger.add(base_multiply2);
            bigInteger2 = bigInteger2.add(new_multiply2);
            if (!bytecompare(base_multiply2, new_multiply2)) {
                System.out.print("mismatch for:b1:" + stringify(x3) + " :b2:" + stringify(x4) + " :oldres:" + stringify(base_multiply2) + " :newres:" + stringify(new_multiply2));
                System.out.println(x3);
                System.out.println(x4);
                throw new Exception("Failed");
            }
        }
        if (!bytecompare(bigInteger, bigInteger2)) {
            System.out.println("Failure: oldsum:" + stringify(bigInteger) + " newsum:" + stringify(bigInteger2));
            throw new Exception("Failed");
        }
        System.out.println("Success");
        System.out.print("my_check_sum_value:");
        System.out.println(TestMultiplyToLen.my_check_sum);
    }
    
    public static BigInteger new_multiply(final BigInteger bigInteger, final BigInteger val) {
        return bigInteger.multiply(val);
    }
    
    public static String stringify(final BigInteger bigInteger) {
        String string = "";
        final byte[] byteArray = bigInteger.toByteArray();
        for (int i = 0; i < byteArray.length; ++i, TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, i)) {
            final StringBuilder sb = new StringBuilder(String.valueOf(string));
            final String format = "%02x";
            final Object[] args = { null };
            final int n = 0;
            final byte b = byteArray[i];
            final Byte value = b;
            TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, b);
            final String s = "test_cp_unalnsrc: a1";
            final char[] array = new char[2];
            int n2 = i;
            final char c = array[n2];
            final int verify = TestCharVect.verify(s, n2, c, '{');
            TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, c);
            TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, verify + n2);
            TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, verify);
            ++n2;
            TestMultiplyToLen.my_check_sum = check_sum.run(TestMultiplyToLen.my_check_sum, n2);
            args[n] = value;
            string = sb.append(String.format(format, args)).append(" ").toString();
        }
        return string;
    }
}
