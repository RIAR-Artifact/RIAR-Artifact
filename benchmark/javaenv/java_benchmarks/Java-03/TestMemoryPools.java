import java.util.Iterator;
import java.util.List;
import java.lang.management.MemoryManagerMXBean;
import compiler.codegen.TestByteDoubleVect;
import compiler.c2.cr6340864.TestByteVect;
import compiler.codegen.TestShortVect;
import java.lang.management.ManagementFactory;
import checksum.check_sum;

// 
// Decompiled by Procyon v0.6.0
// 

public class TestMemoryPools
{
    public static int my_check_sum;
    
    static {
        TestMemoryPools.my_check_sum = 0;
        TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, TestMemoryPools.my_check_sum);
        final int n = 100;
        Integer n2 = new Integer(0);
        final int my_check_sum = TestMemoryPools.my_check_sum;
        final int n3 = n & 0x1;
        TestMemoryPools.my_check_sum = check_sum.run(my_check_sum, n3);
        if (n3 == 0) {
            final int value = n + 1;
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n);
            final Integer n4 = new Integer(value);
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, value);
            n2 = n4;
        }
        TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, (int)n2);
    }
    
    public static void main(final String[] array) throws Exception {
        final List<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
        if (memoryManagerMXBeans == null) {
            final byte[] array2 = new byte[2];
            final int n = 100;
            final byte b = array2[n];
            int n2 = 0;
            final short[] array3 = { 0 };
            if (n2 >= 997) {
                TestShortVect.test_2ci_aln(array3, array3);
                TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, 0);
            }
            else {
                array3[n2] = -1;
                ++n2;
                TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n2);
            }
            final int n3 = 63 + n;
            final String s = "test_andc: ";
            final int n4 = n;
            final byte b2 = b;
            final byte b3 = (byte)n3;
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n3);
            final int n5 = b3 & 0xB7;
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, b3);
            final byte b4 = (byte)n5;
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n5);
            final int verify = TestByteVect.verify(s, n4, b2, b4);
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, b4);
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, b);
            final int my_check_sum = TestMemoryPools.my_check_sum;
            final int n6 = verify + n;
            final int n7 = n3;
            final double[] array4 = { 0.0 };
            final int n8 = verify;
            if (n7 >= 5) {
                TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, 5);
            }
            else {
                final int n9 = n8;
                final String s2 = "test_vi_unaln: b1";
                final int n10 = n7;
                final double n11 = array4[n7];
                final int verify2 = TestByteDoubleVect.verify(s2, n10, n11, -1.0);
                TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n11);
                TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n9 + verify2);
                TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, verify2);
                TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n7 + 1);
            }
            TestMemoryPools.my_check_sum = check_sum.run(my_check_sum, n6);
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, verify);
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n + 1);
            throw new RuntimeException("getMemoryManagerMXBeans is null");
        }
        if (memoryManagerMXBeans.isEmpty()) {
            throw new RuntimeException("getMemoryManagerMXBeans is empty");
        }
        final Iterator<MemoryManagerMXBean> iterator = memoryManagerMXBeans.iterator();
        while (iterator.hasNext()) {
            final String[] memoryPoolNames = iterator.next().getMemoryPoolNames();
            if (memoryPoolNames == null) {
                throw new RuntimeException("getMemoryPoolNames() is null");
            }
            if (memoryPoolNames.length == 0) {
                throw new RuntimeException("getMemoryPoolNames() is empty");
            }
            final String[] array5 = memoryPoolNames;
            for (int length = memoryPoolNames.length, i = 0; i < length; ++i, TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, i)) {
                final String s3 = array5[i];
                if (s3 == null) {
                    throw new RuntimeException("pool name is null");
                }
                if (s3.length() == 0) {
                    throw new RuntimeException("pool name is empty");
                }
            }
        }
        System.out.print("my_check_sum_value:");
        System.out.println(TestMemoryPools.my_check_sum);
    }
}
