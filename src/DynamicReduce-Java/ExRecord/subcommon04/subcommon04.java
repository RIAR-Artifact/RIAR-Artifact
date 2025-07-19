import checksum.check_sum;

class subcommon04 {
    int m;
    static int my_check_sum;

    public static void main(String[] var0) {
        new subcommon04().mat();
        System.out.println(my_check_sum);
    }

    void mat() {
        int var2= 0;
        for (; m < 10;) {
            double var6 = Math.exp(var2);
            my_check_sum = check_sum.run(my_check_sum, var6);
            var2 = m + 1;
            m = var2;
        }
    }
}
