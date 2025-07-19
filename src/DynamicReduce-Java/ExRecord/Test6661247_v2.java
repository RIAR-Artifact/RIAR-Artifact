package compiler.c2;

import checksum.check_sum;
import compiler.c2.cr6340864.TestShortVect;
import java.nio.LongBuffer;
import java.util.Random;

public class Test6661247 {
    public static int my_check_sum = 0;

    public static void main(String[] var0) {
        Random var16 = new Random();
        long var10 = 0;
		var10 = var16.nextLong();
		my_check_sum = check_sum.run(my_check_sum, var10);
		System.out.println(my_check_sum);
    }
}
