package puzzlers.effectivejava.ch5.item33;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * <h2>Consider typesafe heterogeneous containers</h2>
 *
 * <p>
 * <b>Core Principle:</b> Instead of parameterizing the container (which limits you to
 * a fixed number of type parameters), parameterize the <b>key</b>. By using a
 * <i>type token</i> (a class literal like {@code String.class}) as the key, you can
 * store and retrieve values of many different types in a single, typesafe container.
 * </p>
 *
 * <h3>Advantages</h3>
 * <ul>
 * <li><b>Heterogeneity:</b> Unlike a standard {@code Map<K, V>}, every key can
 * represent a different type.</li>
 * <li><b>Type Safety:</b> The container uses the type information in the key to
 * ensure the value is cast correctly, eliminating manual casting for the client.</li>
 * <li><b>Flexibility:</b> Ideal for representing structures with dynamic or
 * arbitrary attributes, such as database rows or attribute sets.</li>
 * </ul>
 *
 * <h3>Limitations</h3>
 * <ul>
 * <li><b>Non-reifiable Types:</b> You cannot store types that cannot be represented
 * by a class literal, such as {@code List<String>}. Only reifiable types work.</li>
 * <li><b>Raw Type Corruption:</b> A client using a raw {@code Class} object can
 * bypass compile-time checks, though this can be mitigated with a runtime
 * dynamic cast during insertion.</li>
 * </ul>
 *
 * @see puzzlers.effectivejava.ch5.item26 RawTypes
 * @see puzzlers.effectivejava.ch5.item28 Reification
 * @see puzzlers.effectivejava.ch6.item39 Annotations
 */
public class Favorites {

    private final Map<Class<?>, Object> favorites = new HashMap<>();

    /**
     * Maps the specific type token to the instance.
     * To ensure runtime type safety against raw type usage, we use type.cast.
     */
    public <T> void putFavorite(Class<T> type, T instance) {
        // The dynamic cast ensures the instance actually matches the type token.
        favorites.put(Objects.requireNonNull(type), type.cast(instance));
    }

    /**
     * Returns the favorite instance of the requested type.
     * Uses the Class.cast method to dynamically cast the Object from the map to T.
     */
    public <T> T getFavorite(Class<T> type) {
        // Class<T>.cast(Object) returns T
        return type.cast(favorites.get(type));
    }

    /**
     * Demonstrates the use of bounded type tokens via the asSubclass method.
     */
    public static Annotation getAnnotation(AnnotatedElement element, String annotationTypeName) {
        Class<?> annotationType = null;
        try {
            annotationType = Class.forName(annotationTypeName);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException(e);
        }
        // Safely cast an unbounded type token to a bounded one.
        return element.getAnnotation(annotationType.asSubclass(Annotation.class));
    }

    // --- Client Usage ---

    public static void main(String[] args) {
        Favorites f = new Favorites();

        // Storing different types in the same container
        f.putFavorite(String.class, "Java");
        f.putFavorite(Integer.class, 0xcafebabe);
        f.putFavorite(Class.class, Favorites.class);

        // Retrieving them without manual casts
        String favoriteString = f.getFavorite(String.class);
        int favoriteInteger = f.getFavorite(Integer.class);
        Class<?> favoriteClass = f.getFavorite(Class.class);

        System.out.printf("%s %x %s%n", favoriteString, favoriteInteger, favoriteClass.getName());
    }
}