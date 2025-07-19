// 
// Decompiled by Procyon v0.6.0
// 

package jit.FloatingPoint.gen_math.Loops01;

import compiler.codegen.TestCharVect2;
import java.io.PrintStream;
import nsk.share.TestFailure;
import checksum.check_sum;

public class Loops01
{
    static final int N = 500;
    public static int my_check_sum;
    
    static {
        Loops01.my_check_sum = 0;
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, Loops01.my_check_sum);
    }
    
    Loops01() {
    }
    
    public void Echeck(final double d, final double d2) {
        if (d < d2) {
            System.out.println("test PASS");
            return;
        }
        final PrintStream out = System.out;
        final StringBuilder append = new StringBuilder("expected error: ").append(d2);
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, d2);
        out.println(append.toString());
        final PrintStream out2 = System.out;
        final StringBuilder append2 = new StringBuilder("   found error: ").append(d);
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, d);
        out2.println(append2.toString());
        throw new TestFailure("test FAIL");
    }
    
    public double Norma(final int n, final double[][] array) {
        double n2 = 0.0;
        final double n3 = n;
        final double n4 = n3 * n3;
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n3);
        for (int i = 0; i < n; ++i, Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, i)) {
            for (int j = 0; j < n; ++j, Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, j)) {
                final double n5 = n2;
                final double n6 = array[i][j];
                final double n7 = array[i][j];
                final double n8 = n6 * n7;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n6);
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n7);
                n2 = n5 + n8;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n8);
            }
        }
        final double n9 = n2 / n4;
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n2);
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n4);
        return n9;
    }
    
    public static void main(final String[] array) {
        int n = 2;
        final char[] array3;
        final char[] array2 = array3 = new char[100];
        final int n2 = n;
        if (n >= 997) {
            TestCharVect2.test_srlc_n(array2, array3);
            Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, 0);
        }
        else {
            final int n3 = n2;
            final String s = "test_sllv_n: ";
            final int n4 = n;
            final char c = array2[n];
            final int n5 = 65035 + n;
            final char c2 = (char)n5;
            Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n5);
            final int n6 = c2 << -7;
            Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, c2);
            final char c3 = (char)n6;
            Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n6);
            final int verify = TestCharVect2.verify(s, n4, (int)c, (int)c3);
            Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, c3);
            Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, c);
            Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n3 + verify);
            Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, verify);
            ++n;
            Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n);
        }
        final double n7 = 0.01;
        final double[][] array4 = new double[500][500];
        final double n8 = 500.0;
        final double n9 = 1.0 / n8;
        final double n10 = 1.0 / n8;
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n8);
        final Loops01 loops01 = new Loops01();
        for (int i = 0; i < 500; ++i, Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, i)) {
            for (int j = 0; j < 500; ++j, Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, j)) {
                final int my_check_sum = Loops01.my_check_sum;
                final double n11 = i;
                final double n12 = n11 * n9;
                Loops01.my_check_sum = check_sum.run(my_check_sum, n11);
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n9);
                final double n13 = j;
                final double n14 = n13 * n10;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n10);
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n13);
                final double n15 = n12 * n12;
                final double n16 = n14 * n14;
                final double a = n15 + n16;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n15);
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n16);
                final double sqrt = Math.sqrt(a);
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, a);
                final double n17 = 2.0;
                final double a2 = 4.0 * n12;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n12);
                final double sin = Math.sin(a2);
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, a2);
                final double a3 = 4.0 * n14;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n14);
                final double cos = Math.cos(a3);
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, a3);
                final double n18 = sin + cos;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, cos);
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, sin);
                final double n19 = n17 + n18;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n18);
                final double n20 = sqrt * n19;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n19);
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, sqrt);
                array4[i][j] = n20;
                Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n20);
            }
        }
        final Loops01 loops2 = loops01;
        final double n21 = 2.5;
        final double norma = loops01.Norma(500, array4);
        final double a4 = n21 - norma;
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, norma);
        final double abs = Math.abs(a4);
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, a4);
        loops2.Echeck(abs, n7);
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, abs);
        Loops01.my_check_sum = check_sum.run(Loops01.my_check_sum, n7);
        System.out.print("my_check_sum_value:");
        System.out.println(Loops01.my_check_sum);
    }
}
