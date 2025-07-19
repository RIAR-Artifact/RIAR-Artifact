import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicIntegerArray;

public class Test {
    public static final int N = 256;

    public static long instanceCount = 170L;

    public float fFld = 1.246F;

    public static int iFld = -8278;

    public static boolean bFld = true;

    public static byte byFld = -70;

    public volatile int iArrFld[] = new int[N];

    public static volatile int iArrFld1[] = new int[N];

    public volatile short sArrFld[] = new short[N];

    public static long iMeth_check_sum = 0;

    public static long vMeth_check_sum = 0;

    public static long iMeth1_check_sum = 0;

    public void mainTest(String[] strArr1) {
        if (Test.ax$21) {
            return;
        }
        int i = 13;
        int i1 = 3412;
        int i33 = 10;
        int i34 = 137;
        int i35 = -211;
        int i36 = -40;
        int i37 = 34296;
        int iArr2[] = new int[N];
        int iArr3[] = new int[N];
        long l3 = 12341L;
        long lArr2[] = new long[N];
        short s1 = 25759;
        float fArr[] = new float[N];
        double dArr1[] = new double[N];
        while ((++i) < 123) {
            i1 += (-205) + (i * i);
        }
        {
			int i26 = 128;
			int i27 = -14;
			int i28 = 15597;
			int i29 = -45416;
			int i30 = 64;
			int i31 = -53766;
			double d2 = 2.102008;
			long l2 = -203563527L;
			float f4 = -121.535F;
			float fArr2[] = new float[N];
			long meth_res = ((((((((((i + i) + i26) + i27) + i28) + i29) + Double.doubleToLongBits(d2)) + l2)
					+ Float.floatToIntBits(f4)) + i30) + i31) + Double.doubleToLongBits(FuzzerUtils.checkSum(fArr2));
		}
		fArr[(i1 >>> 1) % N] += fFld = ((int) (meth_res));
        for (int i32 : Test.iArrFld1) {
            do {
                double d3 = -72.12834;
                iArr2[i33 + 1] += i34;
                s1 += ((short) (((i35 * i1) + Test.instanceCount) - l3));
                i36 = ((int) (d3));
                if (Test.bFld)
                    break;
                i34 >>>= i;
                l3 = Test.byFld;
            } while ((++i35) < 2);
        }
        FuzzerUtils.out.println((((("i36 s1 i37 = " + i36) + ",") + s1) + ",") + i37);
        FuzzerUtils.out.println((((("fArr dArr1 lArr2 = " + Double.doubleToLongBits(FuzzerUtils.checkSum(fArr))) + ",") + Double.doubleToLongBits(FuzzerUtils.checkSum(dArr1))) + ",") + FuzzerUtils.checkSum(lArr2));
        FuzzerUtils.out.println((("iArr2 iArr3 = " + FuzzerUtils.checkSum(iArr2)) + ",") + FuzzerUtils.checkSum(iArr3));
    }

    public static void main(String[] strArr) {
        for (int ax$12 = -109; ax$12 < (-109 + 15641); ax$12 += 1) {
            try {
                for (int ax$15 = 0; ax$15 < 100; ax$15++) {
                    for (int ax$16 = ax$15; ax$16 < 100; ax$16++) {
                    }
                }
            } catch (Throwable ax$19) {
            } finally {
            }
        }
        try {
            Test _instance = new Test();
            for (int i = 0; i < 10; i++) {
                for (int ax$26 = -170; ax$26 < (-170 + 5253); ax$26 += 1) {
                    try {
                    } catch (Throwable ax$29) {
                    } finally {
                    }
                    Test.ax$21 = true;
                    _instance.mainTest(new String[] { "s", "s", "s" });
                    Test.ax$21 = false;
                }
                _instance.mainTest(strArr);
            }
        } catch (Exception ex) {
        }
    }

    public static final class AxOutputRedirectionHelper {
        private static final PrintStream devNull = new PrintStream(new OutputStream() {
            @Override
            public void write(int i) throws IOException {
            }
        });

        private static final PrintStream stdOutBk = System.out;

        private static final PrintStream stdErrBk = System.err;
    }

    static Boolean ax$21 = false;
}