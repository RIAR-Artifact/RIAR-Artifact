
public class test1 extends class2 {
    int s1;

    static int s2;

    public static int a(int p) {
        return p ^ 2;
    }

    public static int b(int c) {
        return c + 10;
    }

    @Override
    public int d(int f) {
        System.out.println(10);
        return f + 10;
    }

    public static void main(String[] args) {




        int i = 0;
        test1 t1=new test1();
        b(9);
        t1.d(1);
        if(i>0){
            i=0;            int b=0;            b=1;
        }else {
            i--;
            int b=0;
            b=b+1;
        }
        int b=0;
        //b=new class2().d(1)+1;
    }
}
class class2{
    public int d(int c) {
        return c + 10;
    }
}
