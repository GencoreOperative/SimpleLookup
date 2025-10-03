package simplelookup;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author KylaBob
 */
public class IndexedQueueTest {
    @Test
    public void testAdditions() {
        IndexedQueue q = new IndexedQueue();
        q.add(1);
        q.add(2);
        q.add(3);
        Assert.assertEquals(3, q.list().size());

        q.add(1);
        q.add(2);
        q.add(3);
        Assert.assertEquals(6, q.list().size());
    }

    public void testRandom() {
        IndexedQueue q = new IndexedQueue();
        // Add all
        for (int ii = 0; ii < 10000; ii++) {
            q.add(Math.round(Math.round((double)100*Math.random())));
        }
        Assert.assertEquals(10000, q.size());

        // Remove all
        for (Object o : q.list()) {
            Assert.assertEquals(true, q.remove(o));
        }
        Assert.assertEquals(0, q.size());
    }
}
