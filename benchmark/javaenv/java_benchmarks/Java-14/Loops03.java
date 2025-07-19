// 
// Decompiled by Procyon v0.6.0
// 

package jit.FloatingPoint.gen_math.Loops03;

import java.io.PrintStream;
import nsk.share.TestFailure;
import compiler.codegen.TestIntDoubleVect;
import checksum.check_sum;

public class Loops03
{
    static final int N = 100000;
    public static int my_check_sum;
    
    static {
        final double d = 100.0;
        new Double(-1.0);
        final Double value = d;
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, d);
        final int[] array = new int[3];
        final double[] array2 = new double[3];
        TestIntDoubleVect.test_cp(array, array, array2, array2);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, 0);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, (double)value);
        Loops03.my_check_sum = 0;
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, Loops03.my_check_sum);
    }
    
    Loops03() {
    }
    
    public void Echeck(final double d, final double d2) {
        if (d < d2) {
            System.out.println("test PASS");
            return;
        }
        final PrintStream out = System.out;
        final StringBuilder append = new StringBuilder("expected error: ").append(d2);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, d2);
        out.println(append.toString());
        final PrintStream out2 = System.out;
        final StringBuilder append2 = new StringBuilder("   found error: ").append(d);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, d);
        out2.println(append2.toString());
        throw new TestFailure("test FAIL");
    }
    
    public double Random_arg(final int n) {
        final double random = Math.random();
        final double n2 = n;
        final double n3 = random * n2;
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n2);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, random);
        return n3;
    }
    
    public static void main(final String[] array) {
        final Loops03 loops03 = new Loops03();
        int i = 1;
        double n = 0.0;
        double n2 = 1.0;
        while (i < 100000) {
            final double random_arg = loops03.Random_arg(i);
            final double sin = Math.sin(random_arg);
            final double n3 = n;
            final double n4 = sin * sin;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, sin);
            final double cos = Math.cos(random_arg);
            final double n5 = cos * cos;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, cos);
            final double n6 = n4 + n5;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n5);
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n4);
            final int my_check_sum = Loops03.my_check_sum;
            final int n7 = i + i;
            final double n8 = n7;
            Loops03.my_check_sum = check_sum.run(my_check_sum, n7);
            final double n9 = n8 * n8;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n8);
            final double n10 = n6 / n9;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n6);
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n9);
            n = n3 + n10;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n10);
            loops03.Random_arg(i);
            final double sin2 = Math.sin(random_arg);
            final double n11 = n2;
            final double n12 = 1.0;
            final double n13 = sin2 * sin2;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, sin2);
            final double cos2 = Math.cos(random_arg);
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, random_arg);
            final double n14 = cos2 * cos2;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, cos2);
            final double n15 = n13 + n14;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n14);
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n13);
            final int my_check_sum2 = Loops03.my_check_sum;
            final int n16 = i + i;
            final double n17 = n16;
            Loops03.my_check_sum = check_sum.run(my_check_sum2, n16);
            final double n18 = n17 * n17;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n17);
            final double n19 = n15 / n18;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n15);
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n18);
            final double n20 = n12 + n19;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n19);
            n2 = n11 * n20;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n20);
            ++i;
            Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, i);
        }
        final double a = n - 0.411;
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n);
        final double abs = Math.abs(a);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, a);
        final Loops03 loops4 = loops03;
        final double n21 = abs * abs;
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, abs);
        final double a2 = n2 - 1.465;
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n2);
        final double abs2 = Math.abs(a2);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, a2);
        final double n22 = abs2 * abs2;
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, abs2);
        new Throwable();
        final double a3 = n21 + n22;
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n22);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, n21);
        final double sqrt = Math.sqrt(a3);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, a3);
        loops4.Echeck(sqrt, 0.001);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, sqrt);
        Loops03.my_check_sum = check_sum.run(Loops03.my_check_sum, 0.001);
        System.out.print("my_check_sum_value:");
        System.out.println(Loops03.my_check_sum);
    }
}
