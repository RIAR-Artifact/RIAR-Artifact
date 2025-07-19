//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package compiler.c1;

import checksum.check_sum;
import compiler.c2.cr6340864.TestShortVect;

public class TestPinnedIntrinsics {
    public static int my_check_sum = 0;

    public static void main(String[] var0) {
        testCurrentTimeMillis();
		System.out.println(my_check_sum);
    }

    public static void testCurrentTimeMillis() {
        long var2 = System.currentTimeMillis();
        my_check_sum = check_sum.run(my_check_sum, var2);
    }
}
