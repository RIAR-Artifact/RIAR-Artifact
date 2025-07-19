package compiler.c2;

import checksum.check_sum;
import java.awt.geom.Line2D;

public class Test7047069 {
    public int limit = 0;
    public float[] hold = new float[14];
    public int holdEnd = 0;
    public int holdIndex = 0;
    public int[] levels = null;
    public int levelIndex = 0;
    public static int my_check_sum = 0;

    public Test7047069() {
        this.holdEnd = this.hold.length - 2;
        this.hold[this.holdIndex + 5] = (float)(Math.random() * 100.0);
        this.limit = 10;
        this.levels = new int[this.limit + 1];
    }

    public static double getFlatnessSq(float[] var0, int var1) {
        int var14 = var1 + 0;
        float var15 = var0[var14];
        double var2 = (double)var15;
        double var4 = (double)var15;
        double var6 = (double)var15;
        var14 = var1 + 5;
        var15 = var0[var14];
        double var8 = (double)var15;
        double var10 = (double)var15;
        my_check_sum = check_sum.run(my_check_sum, var15);
        float[] var10005 = var0;
        int var16 = var1 + 3;
        float var17 = var10005[var16];
        double var12 = (double)var17;
        double var10000 = Line2D.ptSegDistSq(var2, var4, var6, var8, var10, var12);
        return var10000;
    }

    public static void main(String[] var0) {
        Test7047069 var3 = new Test7047069();
		while (var3.next())
		System.out.println(my_check_sum);
    }

    public boolean next() {
        if (this.holdIndex >= this.holdEnd) {
            return false;
        } else {
            int var5 = this.levelIndex;
            int var6 = this.levels[var5];

            for(my_check_sum = check_sum.run(my_check_sum, var5); var6 < this.limit; my_check_sum = check_sum.run(my_check_sum, var5)) {
                double var1 = getFlatnessSq(this.hold, var5);
                ++var6;
            }

            var5 = var6 + 4;
            this.holdIndex = var5;
            return true;
        }
    }
}
