package simplelookup;

import simplelookup.listener.LookupListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import simplelookup.Lookup.View;
import simplelookup.listener.LookupBasicListener;
import simplelookup.listener.LookupDeltaListener;


/**
 *
 * @author KylaBob
 */
public class LookupTest {

    @Test
    public void testView() {
        Lookup l = new Lookup();
        Lookup.View<String> v1 = l.getView(String.class);

        // Verify add sequence
        Assert.assertEquals(0, v1.list().size());
        v1.add("Badger");
        Assert.assertEquals(1, v1.list().size());
        v1.add("Ferret");

        // Verify remove sequence
        Assert.assertEquals(2, v1.list().size());
        v1.remove("Badger");
        Assert.assertEquals(1, v1.list().size());
        v1.remove("Ferret");
        Assert.assertEquals(0, v1.list().size());

        // Verify both views see the same data.
        View<String> v2 = l.getView(String.class);

        v2.add("Ferret");
        Assert.assertEquals(1, v2.list().size());
        Assert.assertEquals(1, v1.list().size());

        v1.add("Test");
        Assert.assertEquals(2, v2.list().size());
        Assert.assertEquals(2, v1.list().size());

        // Demonstrate the views are interchangable.
        for (String s : v1.list()) {
            v2.remove(s);
        }
        Assert.assertEquals(0, v2.list().size());
        Assert.assertEquals(0, v1.list().size());
    }

    @Test
    public void testViewHierarchy() {
        Lookup l = new Lookup();
        // Strings View
        View<String> strings = l.getView(String.class);
        strings.add("");

        // Objects View
        View<Object> objects = l.getView(Object.class);
        objects.add(new Object());

        // Verify the views contents
        Assert.assertEquals(1, strings.size());
        Assert.assertEquals(2, objects.size());

        // Verify changes
        strings.remove("");
        Assert.assertEquals(0, strings.size());
        Assert.assertEquals(1, objects.size());
    }

    @Test
    public void testMultiple() {
        Lookup l = new Lookup();
        View<String> view = l.getView(String.class);

        // Verify Add/Remove all operations.
        view.addAll(Arrays.asList("Badger", "Ferret", "Weasel", "Stoat"));
        Assert.assertEquals(4, view.size());
        view.removeAll(Arrays.asList("Ferret", "Stoat"));
        Assert.assertEquals(2, view.size());
        Assert.assertTrue(view.list().contains("Badger"));
        Assert.assertTrue(view.list().contains("Weasel"));
        Assert.assertFalse(view.list().contains("Ferret"));
        Assert.assertFalse(view.list().contains("Stoat"));
        view.removeAll(Arrays.asList("Badger", "Weasel"));
        Assert.assertEquals(0, view.size());
    }

    @Test
    public void testRegister() {
        final List<String> total = new LinkedList<String>();

        LookupListener<String> listener = new LookupBasicListener<String>() {
            public void resultChanged(Collection<String> result) {
                total.addAll(result);
            }
        };

        Lookup l = new Lookup();
        l.register(String.class, listener);

        // Verify that adding something of the same class triggers an update.
        View<String> strings = l.getView(String.class);
        strings.add("");
        Assert.assertEquals(1, total.size());
        total.clear();

        // Verify adding something of a different class does not.
        View<Integer> ints = l.getView(Integer.class);
        ints.add(1);
        Assert.assertEquals(0, total.size());

        // Verify that deregistering stops us recieving the changed signal
        l.deregister(String.class, listener);
        strings.add("Badger");
        Assert.assertEquals(0, total.size());
    }

    @Test
    public void testRegisterDelta() {
        final List<String> strings = new LinkedList<String>();
        Lookup l = new Lookup();
        l.register(String.class, new LookupDeltaListener<String>(){
            public void resultAdded(Collection<String> additions) {
                strings.addAll(additions);
            }

            public void resultRemoved(Collection<String> removals) {
                strings.removeAll(removals);
            }
        });

        View<String> view = l.getView(String.class);
        view.add("");
        Assert.assertEquals(1, strings.size());
        view.remove("");
        Assert.assertEquals(0, strings.size());
        view.add("Badger");
        view.add("Ferret");
        Assert.assertEquals(2, strings.size());
        view.add("Badger");
        view.add("Ferret");
        Assert.assertEquals(4, strings.size());
        view.removeAll(view.list());
        Assert.assertEquals(0, strings.size());
    }

    private class A {}
    private class EA extends A {}
    private class EEA extends EA {}

    @Test
    public void testListenerHierarchy() {
        final List<A> aList = new LinkedList<A>();
        final List<EA> eaList = new LinkedList<EA>();

        Lookup l = new Lookup();
        l.register(A.class, new LookupBasicListener<A>(){
            public void resultChanged(Collection<A> result) {
                aList.clear();
                aList.addAll(result);
            }
        });

        l.register(EA.class, new LookupBasicListener<EA>(){
            public void resultChanged(Collection<EA> result) {
                eaList.clear();
                eaList.addAll(result);
            }
        });

        l.getView(A.class).add(new A());
        Assert.assertEquals(1, aList.size());
        Assert.assertEquals(0, eaList.size());

        l.getView(EA.class).add(new EA());
        Assert.assertEquals(1, aList.size());
        Assert.assertEquals(1, eaList.size());

        l.getView(EEA.class).add(new EEA());
        Assert.assertEquals(1, aList.size());
        Assert.assertEquals(1, eaList.size());
    }
}