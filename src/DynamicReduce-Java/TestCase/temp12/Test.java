
package a.b;

public class Test {

    public static long instanceCount = 0;

    public static float fFld = -86.307F;

    public double dFld = 0.0;

    public int iFld = 6874;

    public static void vMeth(double d, int i, long l) {
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