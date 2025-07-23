import dynamicReduce.tester.TestCache;
import dynamicReduce.utils.Logger;

public class CacheTest {
    public static void main(String[] args) throws Exception {
        TestCache cache=new TestCache();
        Logger.log(String.valueOf(cache.textHash("(2)(1)(3)")));
        Logger.log(String.valueOf(cache.textHash("((((2)(1)(3))))")));
    }
}
