package simplelookup;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Fast Add/Remove, but slow List characteristics.
 *
 * This structure creates an Entry for each Object added. The structure supports
 * duplicates by linking Entries together under the same Object reference in the
 * map.
 *
 * Adds are simply appended to the end of an Entry stack. Removes pop an Entry
 * off the top of the stack.
 * 
 * @author KylaBob
 */
public class IndexedQueue<T> {
    private int total = 0;
    private Map<T, Integer> map = new HashMap<T, Integer>();
    
    public void add(T t) {
        Integer count = map.get(t);
        if (count != null) {
            map.put(t, count+1);
        } else {
            map.put(t, 1);
        }
        total++;
    }

    public boolean remove(T t) {
        Integer count = map.get(t);
        if (count == null) return false;
        map.put(t, count-1);
        total--;
        return true;
    }

    public int size() {
        return total;
    }

    public List<T> list() {
        List<T> r = new LinkedList<T>();
        for (T t : map.keySet()) {
            int count = map.get(t);
            while (count > 0) {
                r.add(t);
                count--;
            }
        }
        return r;
    }

    public void clear() {
        map.clear();
        total = 0;
    }
}