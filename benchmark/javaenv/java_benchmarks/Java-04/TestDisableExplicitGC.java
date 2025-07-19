import jdk.test.lib.Asserts;
import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.lang.management.GarbageCollectorMXBean;
import java.util.List;
import checksum.check_sum;

// 
// Decompiled by Procyon v0.6.0
// 

public class TestDisableExplicitGC
{
    public static int my_check_sum;
    
    static {
        TestDisableExplicitGC.my_check_sum = 0;
        final int my_check_sum = TestDisableExplicitGC.my_check_sum;
        TestDisableExplicitGC.my_check_sum = check_sum.run(TestDisableExplicitGC.my_check_sum, 0);
        TestDisableExplicitGC.my_check_sum = check_sum.run(my_check_sum, TestDisableExplicitGC.my_check_sum);
    }
    
    public static long getCollectionCount(final List<GarbageCollectorMXBean> list) {
        int n = 0;
        final Iterator<GarbageCollectorMXBean> iterator = list.iterator();
        while (iterator.hasNext()) {
            final long collectionCount = iterator.next().getCollectionCount();
            final long n2 = n;
            final long n3 = collectionCount + n2;
            TestDisableExplicitGC.my_check_sum = check_sum.run(TestDisableExplicitGC.my_check_sum, n2);
            TestDisableExplicitGC.my_check_sum = check_sum.run(TestDisableExplicitGC.my_check_sum, collectionCount);
            n = (int)n3;
            TestDisableExplicitGC.my_check_sum = check_sum.run(TestDisableExplicitGC.my_check_sum, n3);
        }
        final long n4 = n;
        TestDisableExplicitGC.my_check_sum = check_sum.run(TestDisableExplicitGC.my_check_sum, n);
        return n4;
    }
    
    public static void main(final String[] array) throws InterruptedException {
        final List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
        final long collectionCount = getCollectionCount(garbageCollectorMXBeans);
        System.gc();
        final long collectionCount2 = getCollectionCount(garbageCollectorMXBeans);
        final Long value = collectionCount;
        TestDisableExplicitGC.my_check_sum = check_sum.run(TestDisableExplicitGC.my_check_sum, collectionCount);
        final Long value2 = collectionCount2;
        TestDisableExplicitGC.my_check_sum = check_sum.run(TestDisableExplicitGC.my_check_sum, collectionCount2);
        Asserts.assertLT((Comparable)value, (Comparable)value2);
        System.out.print("my_check_sum_value:");
        System.out.println(TestDisableExplicitGC.my_check_sum);
    }
}
