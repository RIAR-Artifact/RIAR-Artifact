import java.util.Iterator;
import java.util.List;
import java.lang.management.MemoryManagerMXBean;
import compiler.c2.cr6340864.TestByteVect;
import java.lang.management.ManagementFactory;
import compiler.c2.cr6340864.TestShortVect;
import compiler.c2.Test6724218;
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
        new Test6724218();
        TestShortVect.test_unpack2(new short[1], new int[1]);
        TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, 0 + 1);
    }
    
    public static void main(final String[] array) throws Exception {
        final List<MemoryManagerMXBean> memoryManagerMXBeans = ManagementFactory.getMemoryManagerMXBeans();
        if (memoryManagerMXBeans == null) {
            final String s = "test_andc: ";
            final byte[] array2 = new byte[2];
            final int n = 100;
            final byte b = array2[n];
            final int n2 = 63 + n;
            final int n3 = n;
            final byte b2 = b;
            final byte b3 = (byte)n2;
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n2);
            final int n4 = b3 & 0xB7;
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, b3);
            final byte b4 = (byte)n4;
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, n4);
            final int verify = TestByteVect.verify(s, n3, b2, b4);
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, b4);
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, b);
            TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, verify + n);
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
            final String[] array3 = memoryPoolNames;
            for (int length = memoryPoolNames.length, i = 0; i < length; ++i, TestMemoryPools.my_check_sum = check_sum.run(TestMemoryPools.my_check_sum, i)) {
                final String s2 = array3[i];
                if (s2 == null) {
                    throw new RuntimeException("pool name is null");
                }
                if (s2.length() == 0) {
                    throw new RuntimeException("pool name is empty");
                }
            }
        }
        System.out.print("my_check_sum_value:");
        System.out.println(TestMemoryPools.my_check_sum);
    }
}
