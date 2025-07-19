
package a.b;

public class Test {

    public static long instanceCount = 0;

    public static float fFld = -86.307F;

    public double dFld = 0.0;

    public int iFld = 6874;

    public static double dMeth(long l1) {
        int i1 = 155, i2 = 20026, i3 = 67, i4 = 5, i17 = 55387, i18 = 9, i19 = 78, i20 = 251;
        for (i1 = 6; 191 > i1; i1++) {
            boolean ax$10 = false;
            for (int ax$9 = -4400; ax$9 < 1999; ax$9 += 9) {
                if (!ax$10) {
                    ax$10 = true;
                    i18 += i17 ^ (long) Test.fFld;
                }
                try {
                    int[] ax$3 = new int[20];
                    while (ax$3[i18] != i1) {
                    }
                } catch (Throwable ax$8) {
                }
            }
        }
        return 0;
    }

    public static void vMeth(double d, int i, long l) {
        i = (int) dMeth(Test.instanceCount);
    }

    public void mainTest(String[] strArr1) {
        vMeth(dFld, iFld, Test.instanceCount);
        System.out.println(",");
    }

    public static void main(String[] strArr) {
        try {
            Test _instance = new Test();
            for (int i = 0; i < 10; i++) {
                _instance.mainTest(strArr);
            }
        } catch (Exception ex) {
        }
    }
}