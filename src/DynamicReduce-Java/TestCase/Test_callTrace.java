// Generated by Java* Fuzzer test generator (1.0.001) and Artemis program mutator.

public class Test {

    public static final int N = 256;

    public static long instanceCount = 4L;

    public static short sFld = 30418;

    public static int iFld = 6;

    public static byte byFld = 74;

    public static volatile byte byFld1 = 67;

    public boolean bFld = false;

    public static int[] iArrFld = new int[N];

    public static int[] iArrFld1 = new int[N];

    public static double[] dArrFld = new double[N];

    public static float[][] fArrFld = new float[N][N];

    public static long[] lArrFld = new long[N];

    public static long vMeth_check_sum = 0;

    public static long iMeth_check_sum = 0;

    public static long iMeth1_check_sum = 0;

    public static void vMeth(long l) {
        if (ax$17) {
            try {
                short[] ax$18 = new short[] { 20, 10, 0, -10, -20 };
                int ax$19 = 1238;
                ax$19 += Math.abs(ax$18[iFld] - sFld);
            } catch (Throwable ax$22) {
            } finally {
            }
            return;
        }
        int i = -18613, i1 = -1, i17 = 54886, i18 = 200, i19 = -159;
        float f2 = 2.944F;
        boolean b1 = false;
        double d2 = 37.123551, dArr[][] = new double[N][N];
        long l3 = -4920907919678785144L;
        short[] sArr = new short[N];
        for (i = 2; i < 165; i++) {
            l = Test.iFld;
			for (d2 = 2; d2 > 1; d2--) {
				Test.lArrFld[i - 1] >>= i1;
				Test.iFld *= Test.byFld1;
			}
        }
        vMeth_check_sum += l + i + i1 + Float.floatToIntBits(f2) + i17 + i18 + (b1 ? 1 : 0) + Double.doubleToLongBits(d2) + i19 + l3 + FuzzerUtils.checkSum(sArr) + Double.doubleToLongBits(FuzzerUtils.checkSum(dArr));
    }

    public void mainTest(String[] strArr1) {
        try {
			ax$17 = true;
			for (int ax$23 = 0; ax$23 < 6873; ax$23 += 1)
				vMeth(8502057914922338455L);
		} catch (java.lang.Throwable ax$24) {
		} finally {
			ax$17 = false;
		}
		vMeth(Test.instanceCount);
        FuzzerUtils.out.println("vMeth_check_sum: " + vMeth_check_sum);
    }

    public static void main(String[] strArr) {
        Test _instance = new Test();
		for (int i = 0; i < 10; i++) {
			_instance.mainTest(strArr);
		}
    }

    private static Boolean ax$17 = false;
}
// /////////////////////////////////////////////////////////////////////
// DEBUG  Test ->  Test
// DEBUG  main ->  main
// DEBUG  mainTest ->  mainTest
// DEBUG  vMeth ->  vMeth mainTest
// DEBUG  iMeth ->  iMeth vMeth mainTest
// DEBUG  iMeth1 ->  iMeth1 iMeth vMeth mainTest
// DEBUG  Depth = 3
// DEBUG  Classes = 1
// DEBUG  static objects = {}