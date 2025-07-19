package vm.compiler.jbe.subcommon.subcommon04;

import checksum.check_sum;

class subcommon04{
    int k;
    int m;
    double[] arr=new double[100];
    double[][]arr1=new double[10][10];
    static int my_check_sum;

    public static void main(String[] var0) {
        subcommon04 var1=new subcommon04();
        var1.mat();
        System.out.println(my_check_sum);
    }

    void mat() {
        int var2;
        int var1=k;
        var2=0;
        for(;m<this.arr1.length;){
            double[] var10000=arr;
            double var4=var2;
            double var6=Math.exp(var4);
            var10000[var1]=var6;
            my_check_sum=check_sum.run(my_check_sum,var6);
            var1=m;
            var2=var1+1;
            m=var2;
        }
    }
}