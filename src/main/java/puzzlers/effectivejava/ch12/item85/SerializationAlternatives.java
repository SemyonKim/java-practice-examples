package puzzlers.effectivejava.ch12.item85;

import java.io.*;
import java.util.*;

/**
 * <h2>Prefer alternatives to Java serialization</h2>
 *
 * <p>
 * <b>Core Principle:</b> The best way to avoid serialization exploits is never to
 * deserialize anything. Java serialization is a "clear and present danger" because its
 * attack surface is too large to protect. For new systems, use cross-platform
 * structured-data representations like JSON or Protocol Buffers (protobuf).
 * </p>
 *
 * <h3>Advantages of Cross-Platform Alternatives (JSON/Protobuf)</h3>
 * <ul>
 * <li><b>Simplicity:</b> They support simple attribute-value pairs rather than arbitrary
 * object graphs, avoiding "magic constructor" vulnerabilities.</li>
 * <li><b>Interoperability:</b> Language-neutral and designed for modern distributed systems.</li>
 * <li><b>Performance:</b> Protobuf offers highly efficient binary encoding; JSON offers
 * excellent performance for a text-based format.</li>
 * <li><b>Safety:</b> They do not execute arbitrary code during the translation from
 * bytes back to data.</li>
 * </ul>
 *
 * <h3>Limitations & Risks of Java Serialization</h3>
 * <ul>
 * <li><b>Gadget Chains:</b> Attackers can chain methods called during deserialization
 * (gadgets) to execute arbitrary native code (RCE).</li>
 * <li><b>Deserialization Bombs:</b> Small payloads can cause exponential processing
 * (e.g., hash code collisions in nested collections), leading to Denial of Service (DoS).</li>
 * <li><b>Invisible Constructors:</b> {@code readObject} creates objects without
 * calling standard constructors, blurring lines between API and implementation.</li>
 * <li><b>Maintenance Burden:</b> Once a class is {@code Serializable}, its internal
 * representation becomes part of its exported API forever.</li>
 * </ul>
 *
 * <h3>Defense Mechanisms (If Serialization is Unavoidable)</h3>
 * <ul>
 * <li><b>Never deserialize untrusted data:</b> Especially via RMI, JMX, or JMS.</li>
 * <li><b>Object Deserialization Filtering:</b> Use {@code java.io.ObjectInputFilter}
 * (Java 9+) to whitelist safe classes and reject all others.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch12.item86 ImplementSerializableWithCaution
 * @see puzzlers.effectivejava.ch12.item87 CustomSerializedForm
 */
public class SerializationAlternatives {

    // --- 1. The Anti-Pattern: The Deserialization Bomb ---

    /**
     * Produces a "deserialization bomb".
     * This 5KB stream causes over 2^100 hashCode() calls during deserialization.
     */
    public static byte[] produceSerializationBomb() {
        Set<Object> root = new HashSet<>();
        Set<Object> s1 = root;
        Set<Object> s2 = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            Set<Object> t1 = new HashSet<>();
            Set<Object> t2 = new HashSet<>();
            t1.add("foo"); // Ensure t1 != t2
            s1.add(t1); s1.add(t2);
            s2.add(t1); s2.add(t2);
            s1 = t1;
            s2 = t2;
        }
        return serialize(root);
    }

    // --- 2. The Best Practice: Simple Data Transfer Objects (DTOs) ---

    /**
     * A simple POJO suitable for JSON or Protobuf.
     * It relies on explicit fields rather than hidden serialization logic.
     */
    public static class UserDto {
        private String username;
        private List<String> roles;

        public UserDto() {} // Standard constructor used by modern frameworks

        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public List<String> getRoles() { return roles; }
        public void setRoles(List<String> roles) { this.roles = roles; }

        @Override
        public String toString() {
            return String.format("User[name=%s, roles=%s]", username, roles);
        }
    }

    // --- 3. Defensive Fallback: Deserialization Filtering ---

    /**
     * Demonstrates how to use a filter to prevent the deserialization of
     * unauthorized classes. Whitelisting is always preferred.
     */
    public static Object safeDeserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             ObjectInputStream ois = new ObjectInputStream(bais)) {

            // Set a filter to allow only String and UserDto
            ois.setObjectInputFilter(filterInfo -> {
                Class<?> serialClass = filterInfo.serialClass();
                if (serialClass != null) {
                    if (serialClass == String.class || serialClass == UserDto.class) {
                        return ObjectInputFilter.Status.ALLOWED;
                    }
                    return ObjectInputFilter.Status.REJECTED;
                }
                return ObjectInputFilter.Status.UNDECIDED;
            });

            return ois.readObject();
        }
    }

    // --- Utility Methods ---

    private static byte[] serialize(Object o) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(o);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        System.out.println("--- Serialization Alternatives ---");

        // 1. Illustrating why we avoid Java Serialization
        byte[] bomb = produceSerializationBomb();
        System.out.println("Generated a bomb of size: " + bomb.length + " bytes.");
        System.out.println("Attempting to deserialize this would hang the JVM forever.");

        // 2. Modern Approach
        System.out.println("\n--- Best Practice: Use structured data (JSON/Protobuf) ---");
        UserDto user = new UserDto();
        user.setUsername("Joshua");
        user.setRoles(Arrays.asList("ADMIN", "DEVELOPER"));
        System.out.println("Data to be sent (as JSON/Protobuf): " + user);

        // 3. Defensive Approach
        System.out.println("\n--- Fallback: Whitelisting Filter ---");
        try {
            // This would fail if 'bomb' contained a HashSet (which is not in our whitelist)
            Object result = safeDeserialize(serialize("Hello World"));
            System.out.println("Safely deserialized: " + result);
        } catch (Exception e) {
            System.err.println("Deserialization rejected: " + e.getMessage());
        }
    }
}