# SimpleLookup

SimpleLookup is a lightweight, simplified implementation of the 
[NetBeans Lookup mechanism](https://netbeans.apache.org/wiki/main/netbeansdevelopperfaq/DevFaqLookup/). It provides a 
simple way to decouple different parts of an application by using a centralized registry for objects and services.

The core idea is to have a "bag" of objects where you can query for instances of a specific class. This avoids direct 
dependencies between components, making the application more modular and easier to maintain.

## Core Concepts

*   **Lookup:** The central registry that holds all the objects.
*   **View:** A typed view of the Lookup for a specific class. You use views to add, remove, and query objects of a certain type.
*   **Listeners:** You can register listeners to be notified when the contents of the Lookup change for a specific type.

There are two types of listeners:

*   `LookupBasicListener`: Receives the complete list of items every time a change occurs.
*   `LookupDeltaListener`: Receives only the items that were added or removed.

## How to Use

### 1. Create a Lookup instance

```java
Lookup lookup = new Lookup();
```

### 2. Get a typed View

To interact with the Lookup, you get a `View` for the class you are interested in.

```java
View<String> stringView = lookup.getView(String.class);
```

### 3. Add and remove objects

Use the `View` to manipulate the objects in the Lookup.

```java
// Add a single object
stringView.add("Hello");

// Add a collection of objects
List<String> names = Arrays.asList("Alice", "Bob");
stringView.addAll(names);

// Remove an object
stringView.remove("Hello");
```

### 4. Query the Lookup

You can retrieve objects from the Lookup using the `View`.

```java
// Get all objects of a certain type
Collection<String> allStrings = stringView.list();
for (String s : allStrings) {
    System.out.println(s);
}

// Get the first object
String firstString = stringView.first();

// Get the number of objects
int count = stringView.size();

// Check if the view is empty
boolean isEmpty = stringView.isEmpty();
```

### 5. Listen for changes

You can register listeners to react to changes in the Lookup.

#### Using `LookupDeltaListener` (Recommended)

This listener is efficient as it only receives the changes (additions or removals).

```java
lookup.register(String.class, new LookupDeltaListener<String>() {
    @Override
    public void resultAdded(Collection<String> additions) {
        System.out.println("Added: " + additions);
    }

    @Override
    public void resultRemoved(Collection<String> removals) {
        System.out.println("Removed: " + removals);
    }
});

// This will trigger the resultAdded method of the listener
stringView.add("World");
```

#### Using `LookupBasicListener`

This listener receives the entire collection of items for the given type whenever there is a change.

```java
lookup.register(Integer.class, new LookupBasicListener<Integer>() {
    @Override
    public void resultChanged(Collection<Integer> result) {
        System.out.println("Current integers: " + result);
    }
});

View<Integer> integerView = lookup.getView(Integer.class);
integerView.add(42); // This will trigger the resultChanged method
```

### 6. Deregistering a listener

When you no longer need to listen for changes, you can deregister the listener.

```java
LookupListener<String> myListener = ...;
lookup.register(String.class, myListener);
// ...
lookup.deregister(String.class, myListener);
```

## API

The main classes and interfaces are located in the `simplelookup` and `simplelookup.listener` packages.

*   `simplelookup.Lookup`: The main entry point for the Lookup mechanism.
*   `simplelookup.Lookup.View`: A typed view for interacting with the Lookup.
*   `simplelookup.listener.LookupListener`: Base interface for all listeners.
*   `simplelookup.listener.LookupBasicListener`: A listener that receives the full result on every change.
*   `simplelookup.listener.LookupDeltaListener`: A listener that receives only the delta (added/removed items).
