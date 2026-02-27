package puzzlers.valeev.comparing.mistake3;

import java.net.URI;
import java.net.URL;
import java.util.List;

/**
 * Using URL.equals() and hashCode()
 * <p>
 * Inspired by: 100 Java mistakes and how to avoid them (Tagir Valeev) - the mistake 57
 * <p>
 * Problem:
 * Why does comparing two java.net.URL objects sometimes cause the application
 * to hang or produce inconsistent results depending on the network environment?
 * <p>
 * Expected behavior:
 * Most developers expect URL equality to behave like String equality—comparing
 * the protocol, host, port, and path as literal characters.
 * <p>
 * Actual behavior:
 * The equals() and hashCode() methods of java.net.URL perform a blocking
 * network call (DNS resolution) to determine if two hosts resolve to the same
 * IP address.
 * <p>
 * Explanation:
 * - Network Dependency: Calling .equals() triggers a DNS lookup. If the network
 * is slow or the DNS server is down, the thread blocks.
 * - Virtual Hosting: Modern web architecture often hosts multiple domains on
 * the same IP. URL.equals() might consider two completely different websites
 * equal just because they share a server.
 * - Collection Poisoning: Storing URLs in a HashSet or HashMap is dangerous.
 * Every time you call .contains() or .put(), a network request may be initiated.
 * - Static Analysis: Tools like SonarLint (S2112) and IntelliJ IDEA specifically
 * warn against this, but they often miss indirect calls like list.contains(url).
 * <p>
 * Step-by-step (The Mechanics):
 * - Application calls url1.equals(url2).
 * - The JVM identifies that the hosts are strings (e.g., "example.com").
 * - The JVM initiates a DNS resolution request to find the IP address.
 * - The thread waits for the network response.
 * - Once IPs are received, the JVM compares the IPs instead of the host strings.
 * <p>
 * Fixes:
 * - Use java.net.URI instead of java.net.URL for storage and comparison. URI
 * does not perform network lookups and compares components via string logic.
 * - If you must use URL, convert it to a String (url.toString()) or a URI
 * (url.toURI()) before comparing.
 * - Avoid using URL as a key in Maps or an element in Sets.
 * <p>
 * Lesson:
 * - Never allow a simple equality check to perform I/O or network operations.
 * - java.net.URL is considered a "legacy" class with several design flaws;
 * prefer URI for manipulation and only use URL when interacting with legacy
 * APIs that require it.
 * <p>
 * Output:
 * - url1.equals(url2) -> true (after a ¬31ms DNS delay)
 * - uri1.equals(uri2) -> true (instantaneous string comparison)
 */
public class URLEqualityMistake {

    public static void main(String[] args) throws Exception {
        // The problematic URL class
        URL url1 = new URL("https://example.com");
        URL url2 = new URL("https://example.com");

        System.out.println("--- URL Comparison (Blocking) ---");
        long startTime = System.currentTimeMillis();
        // This may block depending on your DNS cache/network
        System.out.println("URL Equality: " + url1.equals(url2));
        System.out.println("Time taken: " + (System.currentTimeMillis() - startTime) + "ms");

        // The safe URI alternative
        URI uri1 = new URI("https://example.com");
        URI uri2 = new URI("https://EXAMPLE.com"); // Case-insensitive host handling

        System.out.println("\n--- URI Comparison (Safe) ---");
        startTime = System.currentTimeMillis();
        System.out.println("URI Equality: " + uri1.equals(uri2));
        System.out.println("Time taken: " + (System.currentTimeMillis() - startTime) + "ms");

        // Collection Trap
        List<URL> urlList = List.of(url1);
        System.out.println("\nList contains URL: " + urlList.contains(url2)); // Triggers DNS again
    }
}