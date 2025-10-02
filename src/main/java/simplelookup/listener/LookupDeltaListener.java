package simplelookup.listener;

import java.util.Collection;

/**
 * Delta LookupListener will receive only delta notifications when the Lookup
 * changes.
 * 
 * @author Robert Wapshott
 */
public interface LookupDeltaListener<T> extends LookupListener<T> {
    public void resultAdded(Collection<T> additions);
    public void resultRemoved(Collection<T> removals);
}
