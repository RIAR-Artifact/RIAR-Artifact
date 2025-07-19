// 
// Decompiled by Procyon v0.6.0
// 

package compiler.intrinsics.bigInteger;

import java.util.Random;
import java.math.BigInteger;
import checksum.check_sum;

public class TestSquareToLen
{
    public static int my_check_sum;
    
    static {
        TestSquareToLen.my_check_sum = 0;
        TestSquareToLen.my_check_sum = check_sum.run(TestSquareToLen.my_check_sum, TestSquareToLen.my_check_sum);
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
        for (int i = 0; i < byteArray.length; ++i, TestSquareToLen.my_check_sum = check_sum.run(TestSquareToLen.my_check_sum, i)) {
            final byte b = byteArray[i];
            if (3 > 1) {
                if (b != byteArray2[i]) {
                    return false;
                }
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
        TestSquareToLen.my_check_sum = check_sum.run(TestSquareToLen.my_check_sum, nanoTime);
        random.setSeed(nanoTime2);
        TestSquareToLen.my_check_sum = check_sum.run(TestSquareToLen.my_check_sum, nanoTime2);
        for (int i = 0; i < 100000; ++i, TestSquareToLen.my_check_sum = check_sum.run(TestSquareToLen.my_check_sum, i)) {
            final int nextInt = random.nextInt(3136);
            final int numBits = nextInt + 32;
            TestSquareToLen.my_check_sum = check_sum.run(TestSquareToLen.my_check_sum, nextInt);
            final BigInteger bigInteger = new BigInteger(numBits, rnd);
            TestSquareToLen.my_check_sum = check_sum.run(TestSquareToLen.my_check_sum, numBits);
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
            System.out.println("Failure: oldsum:" + stringify(add) + " newsum:" + stringify(add2));
            throw new Exception("Failed");
        }
        System.out.println("Success");
        System.out.print("my_check_sum_value:");
        System.out.println(TestSquareToLen.my_check_sum);
    }
    
    public static BigInteger new_multiply(final BigInteger val) {
        return val.multiply(val);
    }
    
    public static String stringify(final BigInteger bigInteger) {
        String string = "";
        final byte[] byteArray = bigInteger.toByteArray();
        for (int i = 0; i < byteArray.length; ++i, TestSquareToLen.my_check_sum = check_sum.run(TestSquareToLen.my_check_sum, i)) {
            final StringBuilder sb = new StringBuilder(String.valueOf(string));
            final String format = "%02x";
            final Object[] args = { null };
            final int n = 0;
            final byte b = byteArray[i];
            final Byte value = b;
            TestSquareToLen.my_check_sum = check_sum.run(TestSquareToLen.my_check_sum, b);
            args[n] = value;
            string = sb.append(String.format(format, args)).append(" ").toString();
        }
        return string;
    }
}
