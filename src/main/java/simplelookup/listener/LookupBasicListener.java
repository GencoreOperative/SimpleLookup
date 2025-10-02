package simplelookup.listener;

import java.util.Collection;

/**
 * The basic LookupListener will receive an update each time the Lookup changes
 * for the registered type. The entire contents of the Lookup for the
 * registered type will be received.
 * 
 * @author Robert Wapshott
 */
public interface LookupBasicListener<T> extends LookupListener<T> {
    /**
     * Signals that the Lookup this Listener is registered to has been updated.
     * @param result The current contents of the Lookup. This is an unmodifiable
     * List.
     */
    public void resultChanged(Collection<T> result);
}
