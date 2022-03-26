import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 继承LinkedHashMap
 * https://juejin.cn/post/6844903896322670599
 */

public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int MAX_CACHE_SIZE;

    public LRUCache(int cacheSize) {
        // 使用构造方法 public LinkedHashMap(int initialCapacity, float loadFactor, boolean accessOrder)
        // initialCapacity、loadFactor都不重要
        // accessOrder要设置为true，按访问排序
        super((int) Math.ceil(cacheSize / 0.75) + 1, 0.75f, true);
        MAX_CACHE_SIZE = cacheSize;
    }

    public LRUCache(int initialCapacity, int MAX_CACHE_SIZE) {
        super(initialCapacity);
        this.MAX_CACHE_SIZE = MAX_CACHE_SIZE;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        // 超过阈值时返回true，进行LRU淘汰
        return size() > MAX_CACHE_SIZE;
    }

}
