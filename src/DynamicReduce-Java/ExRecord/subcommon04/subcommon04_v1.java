//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package vm.compiler.jbe.subcommon.subcommon04;

import checksum.check_sum;
import java.io.PrintStream;
import java.util.Arrays;

public class subcommon04 {
    public int k = 0;
    public int m = 0;
    public double[] arr = new double[100];
    public double[][] arr1 = new double[10][10];
    public static int my_check_sum = 0;

    public static void main(String[] var0) {
        subcommon04 var1 = new subcommon04();
        var1.mat();
        System.out.println(my_check_sum);
    }

    public void mat() {
        int var2 = 0;
        for(this.k = 0; this.k < this.arr1[0].length; my_check_sum = check_sum.run(my_check_sum, var2)) {
            int var1 = this.k;
            for(this.m = 0; this.m < this.arr1[0].length; my_check_sum = check_sum.run(my_check_sum, var2)) {
                double[] var10000 = this.arr;
                double var4 = this.arr[var2];
                double var6 = Math.exp(var4);
                var10000[var1] = var6;
                my_check_sum = check_sum.run(my_check_sum, var6);
                var1 = this.m;
                var2 = var1 + 1;
                this.m = var2;
            }

            var1 = this.k;
            var2 = var1 + 1;
            this.k = var2;
        }

    }
}
