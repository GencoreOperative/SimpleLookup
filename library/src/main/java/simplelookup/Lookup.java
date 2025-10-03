package simplelookup;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import simplelookup.listener.LookupBasicListener;
import simplelookup.listener.LookupDeltaListener;
import simplelookup.listener.LookupListener;

/**
 * Lookup is an adaption based on the Netbeans Lookup concept. A general
 * bag into which objects can be placed, and listeners can be notified when
 * the bag changes.
 *
 * The Lookup supports Views on a particular type of data in the Lookup. The
 * View provides control to manipulate the lookup for that type.
 * 
 * @author Robert Wapshott
 */
public class Lookup {
    private Map<Class, List<LookupListener>> listenerMap = new HashMap<Class, List<LookupListener>>();
    private Map<Class, IndexedQueue> values = new HashMap<Class, IndexedQueue>();

    /**
     * Register interest in the Lookup and receive notifications when the Lookup
     * changes for the Type that you are interested in. If this method is
     * called multiple times with the same Class and Listener, then multiple
     * records of the Listeners interest will be recorded.
     *
     * @param listener Listener which will receive updates. May not be null.
     *
     * @param classToMap The class for the type of updates to receive. May not
     * be null.
     */
    public synchronized <T> void register(Class<T> classToMap, LookupListener<T> listener) {
        if (listener == null) throw new IllegalArgumentException("listener");
        if (classToMap == null) throw new IllegalArgumentException("classToMap");

        List<LookupListener> listeners = listenerMap.get(classToMap);
        if (listeners == null) {
            listeners = new LinkedList<LookupListener>();
            listenerMap.put(classToMap, listeners);
        }
        listeners.add(listener);
    }

    /**
     * Removes interest in the Lookup for the given Listener against the
     * given type. If this Listener has been registered a number of times, then
     * the Listener will have to be deregistered the same number of times for it
     * to no longer receive updates.
     *
     * @param listener Listener to remove from the Lookup. May not be null.
     *
     * @param classToMap Type the Listener should be removed from. May not be
     * null.
     *
     * @throws IllegalArgumentException If the Listener has not been registered.
     */
    public synchronized <T> void deregister(Class<T> classToMap, LookupListener<T> listener) {
        if (listener == null) throw new IllegalArgumentException("listener");
        if (classToMap == null) throw new IllegalArgumentException("classToMap");
        
        List<LookupListener> list = listenerMap.get(classToMap);
        if(list == null || !list.remove(listener)) {
            throw new IllegalArgumentException("Listener is not registered");
        }
    }

    /**
     * Returns a view of the Lookup that is typed based on the class passed
     * into this call.
     *
     * @param classToView Class which will control the type of this View.  May
     * not be null.
     *
     * @return A non null View of the Lookup.
     */
    public <T> View<T> getView(Class<T> classToView) {
        if (classToView == null) throw new IllegalArgumentException("class");
        return new View<T>(classToView);
    }

    public class View<S> {

        private final List<S> additions = new LinkedList<S>();
        private final List<S> removals = new LinkedList<S>();

        private Class<S> c;
        View(Class<S> c) {
            this.c = c;
        }

        /**
         * Add an item to the Lookup. If there are any listeners registered
         * in the Class of this View, then they will be notified of the change.
         *
         * @param t Item to add to the Lookup.
         */
        public synchronized void add(S t) {
            // Add the value to the list
            getValues().add(t);
            List<LookupListener> listeners = getListeners();
            if (listeners == null) return;
            clear();
            additions.add(t);
            notifyListeners(listeners);
        }

        /**
         * Removes an item to the Lookup. If there are any listeners registered
         * in the Class of this View, then they will be notified of the change.
         *
         * @param t Item to remove from the Lookup.
         */
        public synchronized void remove(S t) {
            if (getValues().remove(t)) {
                List<LookupListener> listeners = getListeners();
                if (listeners == null) return;
                clear();
                removals.add(t);
                notifyListeners(listeners);
            }
        }

        /**
         * Adds a collection of items to the Lookup. If there are any listeners
         * registered in the Class of this View, then they will be notified of
         * the change.
         *
         * @param list List of items to add to the Lookup.
         */
        public synchronized void addAll(Collection<S> list) {
            IndexedQueue<S> queue = getValues();
            for (S t : list) {
                queue.add(t);
            }
            List<LookupListener> listeners = getListeners();
            if (listeners == null) return;
            clear();
            additions.addAll(list);
            notifyListeners(listeners);
        }

        /**
         * Removes a collection of items to the Lookup. If there are any
         * listeners registered in the Class of this View, then they will be
         * notified of the change.
         *
         * @param list List of items to remove from the Lookup.
         */
        public synchronized void removeAll(Collection<S> list) {
            IndexedQueue<S> queue = getValues();
            for (S s : list) {
                queue.remove(s);
            }
            List<LookupListener> listeners = getListeners();
            if (listeners == null) return;
            clear();
            removals.addAll(list);
            notifyListeners(listeners);
        }

        public synchronized void replaceAllWith(S t) {
            clear();
            IndexedQueue<S> q = getValues();
            if (q.size() > 0) {
                removals.addAll(q.list());
                q.clear();
            }
            q.add(t);
            
            List<LookupListener> listeners = getListeners();
            if (listeners == null) return;
            additions.add(t);
            notifyListeners(listeners);
        }

        public synchronized void replaceAllWith(Collection<S> list) {
            clear();
            IndexedQueue<S> q = getValues();
            if (q.size() > 0) {
                removals.addAll(q.list());
                q.clear();
            }
            for (S s : list) {
                q.add(s);
            }
            additions.addAll(list);
            List<LookupListener> listeners = getListeners();
            if (listeners == null) return;
            notifyListeners(listeners);
        }

        /**
         * Returns a IndexedQueue for this View, creating one if there is not
         * one already.
         */
        private IndexedQueue<S> getValues() {
            IndexedQueue<S> queue = values.get(c);
            if (queue == null) {
                queue = new IndexedQueue<S>();
                values.put(c, queue);
            }
            return queue;
        }

        /**
         * Get the Listener associated for this View.
         * Check all super classes for further interested listeners
         */
        private List<LookupListener> getListeners() {
            // Lazy initialise return value
            List<LookupListener> r = null;
            Class search = c;
            while (search != null) {
                List<LookupListener> l = listenerMap.get(search);
                if (l != null) {
                    if (r == null) r = new LinkedList<LookupListener>();
                    r.addAll(l);
                }
                search = search.getSuperclass();
            }
            return r;
        }

        // Signal all listeners for this View
        private void notifyListeners(List<LookupListener> listeners) {
            if (listeners == null) throw new IllegalArgumentException();
            for (LookupListener l : listeners) {
                // Basic mode
                if (l instanceof LookupBasicListener) {
                    LookupBasicListener basic = (LookupBasicListener) l;
                    basic.resultChanged(getValues().list());
                } else if (l instanceof LookupDeltaListener) {
                    LookupDeltaListener delta = (LookupDeltaListener) l;
                    if (!removals.isEmpty()) {
                    	delta.resultRemoved(removals);
                    }
                    if (!additions.isEmpty()) {
                    	delta.resultAdded(additions);
                    }
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        // Clears delta changes
        private void clear() {
            additions.clear();
            removals.clear();
        }

        /**
         * Returns a list of all items in the Lookup that match the class or
         * any assignable sub-type that has been stored in the Lookup.
         *
         * @return Typed list of zero or more elements. This is an unmodifiable
         * list. Changes to the Lookup must be performed via the View accessor
         * methods.
         */
        public synchronized Collection<S> list() {
            Collection<S> r = new LinkedList<S>();
            // The assumption on this search is that the different types of
            // class stored will generally be low vs the data for each type.
            for (Class k : values.keySet()) {
                if (c.isAssignableFrom(k)) {
                    r.addAll(values.get(k).list());
                }
            }
            return Collections.unmodifiableCollection(r);
        }

        /**
         * Returns the first object from the Lookup that is part of this View.
         * @return Null if the view does not contain any entries.
         */
        public synchronized S first() {
            for (Class k : values.keySet()) {
                if (c.isAssignableFrom(k)) {
                    return (S) values.get(k).list().get(0);
                }
            }
            return null;
        }

        /**
         * Returns a count of all entries associated with this View. This will
         * include all assignable Sub Types for this View.
         *
         * @returns Zero or greater count of all entries in this View. This
         * will be equal to view.list().size().
         */
        public synchronized int size() {
            int total = 0;
            for (Class k : values.keySet()) {
                if (c.isAssignableFrom(k)) {
                    total += values.get(k).size();
                }
            }
            return total;
        }

        /**
         * Indicates if the Lookup contains any entries for this View.
         * 
         * @return True if the Lookup has no entries for this View. False if
         * there is something stored.
         */
        public synchronized boolean isEmpty() {
            return size() == 0;
        }
    }

    private static int intCount = 0;
    private static int longCount = 0;
    public static void main(String [] args) {
        Lookup lookup = new Lookup();
        lookup.register(Integer.class, new LookupDeltaListener<Integer>() {
            public void resultAdded(Collection<Integer> additions) {
                intCount++;
            }
            public void resultRemoved(Collection<Integer> removals) {}
        });
        lookup.register(Long.class, new LookupDeltaListener<Long>() {
            public void resultAdded(Collection<Long> additions) {
                longCount++;
            }
            public void resultRemoved(Collection<Long> removals) {}
            
        });
        View<Integer> ints = lookup.getView(Integer.class);
        View<Long> longs = lookup.getView(Long.class);
        int total = 100000;
        for (int ii = 0; ii < total; ii++) {
            long l = Math.round(Math.random()*1000);
            longs.add(l);
            ints.add((int)l);
        }
        if (ints.size() != total) throw new IllegalStateException();
        if (intCount != longCount) throw new IllegalStateException();
    }
}
