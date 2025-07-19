package compiler.c2;

import checksum.check_sum;

class Test7047069 {
    int limit;
    float[] hold = new float[14];
    int holdEnd;
    int holdIndex;
    int levelIndex;
    static int my_check_sum;

    Test7047069() {
        holdEnd = 2;
        hold[0] = (float) Math.random();
        limit = 10;
    }

    double getFlatnessSq(float[] var0, int var1) {
        int var14 = var1;
        float var15 = var0[var14];
        double var2 = var15;
        double var10000 = var2;
        return var10000;
    }

    public static void main(String[] var0) {
        for (;;) {
            Test7047069 var3 = new Test7047069();
            while (var3.next()) {

            }
            break;
        }
        System.out.println(my_check_sum);
    }

    boolean next() {
        if (holdIndex >= holdEnd)
            return false;
        else {
            int var5 = levelIndex;
            int var6 = var5;
            for (; var6 < limit;) {
                double var1 = getFlatnessSq(hold, var5);
                my_check_sum = check_sum.run(my_check_sum, var1);
                ++var6;
            }
            var5 = var6;
            holdIndex = var5;
            return true;
        }
    }
}