package puzzlers.effectivejava.ch12.item87;

import java.io.*;

/**
 * <h2>Consider using a custom serialized form</h2>
 *
 * <p>
 * <b>Core Principle:</b> Do not accept the default serialized form without first
 * considering whether it is appropriate. The ideal serialized form contains only the
 * logical data represented by the object, independent of its physical representation.
 * Use the {@code transient} modifier to exclude implementation details from the
 * serialized form.
 * </p>
 *
 * <h3>Advantages of a Custom Serialized Form</h3>
 * <ul>
 * <li><b>Implementation Flexibility:</b> Decouples the exported API from the internal
 * representation, allowing you to change the underlying data structure (e.g., moving
 * from a linked list to an array) without breaking compatibility.</li>
 * <li><b>Resource Efficiency:</b> Consumes less space and time by avoiding the
 * serialization of unnecessary implementation details (like linked-list pointers).</li>
 * <li><b>Stack Overflow Prevention:</b> Avoids the recursive traversal of the object
 * graph used by default serialization, which can crash on moderately sized graphs.</li>
 * <li><b>Correctness:</b> Prevents bugs in classes whose invariants are tied to
 * implementation-specific details, such as hash buckets in a hash table.</li>
 * </ul>
 *
 * <h3>Limitations of the Default Serialized Form</h3>
 * <ul>
 * <li><b>API Leakage:</b> Private and package-private fields become part of the public
 * API, effectively rendering information hiding (Item 15) ineffective.</li>
 * <li><b>Permanent Maintenance:</b> You are required to support the initial internal
 * representation forever to maintain backward and forward compatibility.</li>
 * <li><b>Performance Overhead:</b> Expensive graph traversals and large serialized
 * payloads slow down network transmission and disk I/O.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch4.item15 InformationHiding
 * @see puzzlers.effectivejava.ch8.item51 MethodSignatures
 * @see puzzlers.effectivejava.ch11.item82 ThreadSafetyDoc
 * @see puzzlers.effectivejava.ch11.item83 LazyInitialization
 * @see puzzlers.effectivejava.ch12.item86 ImplementSerializableWithCaution
 * @see puzzlers.effectivejava.ch12.item88 DefensiveReadObject
 * @see puzzlers.effectivejava.ch12.item90 SerializationProxy
 */
public final class StringList implements Serializable {

    /**
     * Explicit serial version UID for compatibility and performance.
     */
    private static final long serialVersionUID = 87L;

    /**
     * The number of elements in the list.
     */
    private transient int size = 0;

    /**
     * The head of the doubly linked list.
     */
    private transient Entry head = null;

    // Implementation detail: No longer Serializable!
    private static class Entry {
        String data;
        Entry next;
        Entry previous;
    }

    public void add(String s) {
        Entry newEntry = new Entry();
        newEntry.data = s;
        if (head == null) {
            head = newEntry;
        } else {
            Entry temp = head;
            while (temp.next != null) temp = temp.next;
            temp.next = newEntry;
            newEntry.previous = temp;
        }
        size++;
    }

    /**
     * Serialize this {@code StringList} instance.
     *
     * @serialData The size of the list (the number of strings
     * it contains) is emitted ({@code int}), followed by all of
     * its elements (each a {@code String}), in the proper
     * sequence.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        // Required for backward/forward compatibility even if all fields are transient
        s.defaultWriteObject();
        s.writeInt(size);

        // Write out all elements in the proper order.
        for (Entry e = head; e != null; e = e.next) {
            s.writeObject(e.data);
        }
    }

    private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
        // Required for backward/forward compatibility
        s.defaultReadObject();
        int numElements = s.readInt();

        // Read in all elements and insert them into the list
        for (int i = 0; i < numElements; i++) {
            add((String) s.readObject());
        }
    }

    /**
     * If the class were thread-safe, we would need to synchronize writeObject.
     */
    /*
    private synchronized void writeObjectSynchronized(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
    }
    */

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Entry e = head; e != null; e = e.next) {
            sb.append(e.data).append(e.next == null ? "" : ", ");
        }
        return sb.append("]").toString();
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        StringList list = new StringList();
        list.add("Effective");
        list.add("Java");
        list.add("Custom");
        list.add("Serialization");

        System.out.println("Original list: " + list);

        byte[] serializedData;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(list);
            serializedData = baos.toByteArray();
            System.out.println("Serialized size: " + serializedData.length + " bytes.");
        } catch (IOException e) {
            throw new RuntimeException("Serialization failed", e);
        }

        try (ByteArrayInputStream bais = new ByteArrayInputStream(serializedData);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            StringList deserializedList = (StringList) ois.readObject();
            System.out.println("Deserialized list: " + deserializedList);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }
}