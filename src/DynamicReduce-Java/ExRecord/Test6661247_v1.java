package compiler.c2;

import checksum.check_sum;
import compiler.c2.cr6340864.TestShortVect;
import java.nio.LongBuffer;
import java.util.Random;

public class Test6661247 {
    public static int my_check_sum = 0;

    static {
    }

    public Test6661247() {
    }

    public static void main(String[] var0) {
        Random var16 = new Random();
        short var23 = 1000;
        for(int var8 = 0; var8 < 2000; my_check_sum = check_sum.run(my_check_sum, var8)) {
            long var10;
            for(int var9 = 0; var9 < var23; my_check_sum = check_sum.run(my_check_sum, var9)) {
                var10 = var16.nextLong();

                my_check_sum = check_sum.run(my_check_sum, var10);
                ++var9;
            }

            ++var8;
        }

        System.out.println(my_check_sum);
    }

    public static void test(boolean[] var0, int var1, LongBuffer var2, long var3, int var5) {

    }
}
