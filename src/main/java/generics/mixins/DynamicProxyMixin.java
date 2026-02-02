package generics.mixins;

import java.lang.reflect.*;
import java.util.*;

public class DynamicProxyMixin {

    // ============================================================
    // Minimal Tuple implementation (replacing onjava.Tuple2)
    // ============================================================
    //
    // This is a simple generic container for holding two related values.
    // In our case, it holds (Object implementation, Class<?> interface).
    //
    static class Tuple2<A1, A2> {
        public final A1 a1;
        public final A2 a2;

        public Tuple2(A1 a1, A2 a2) {
            this.a1 = a1;
            this.a2 = a2;
        }
    }

    // Convenience factory method for creating Tuple2 instances
    static <A1, A2> Tuple2<A1, A2> tuple(A1 a1, A2 a2) {
        return new Tuple2<>(a1, a2);
    }

    // ============================================================
    // MixinProxy: InvocationHandler implementation
    // ============================================================
    //
    // This class is responsible for handling method calls made on the
    // dynamic proxy object. It decides which underlying delegate object
    // should execute the method.
    //
    static class MixinProxy implements InvocationHandler {

        // Map of method name -> delegate object
        // Example: "getStamp" -> TimeStampedImp instance
        private Map<String, Object> delegatesByMethod;

        // Constructor: builds the mapping between method names and delegates
        @SuppressWarnings("unchecked")
        MixinProxy(Tuple2<Object, Class<?>>... pairs) {
            delegatesByMethod = new HashMap<>();

            // For each (object, interface) pair:
            for (Tuple2<Object, Class<?>> pair : pairs) {
                // For each method in the interface:
                for (Method method : pair.a2.getMethods()) {
                    String methodName = method.getName();

                    // If this method name hasn’t been assigned yet,
                    // associate it with the current object.
                    if (!delegatesByMethod.containsKey(methodName)) {
                        delegatesByMethod.put(methodName, pair.a1);
                    }
                }
            }
        }

        // ============================================================
        // invoke(): Core of InvocationHandler
        // ============================================================
        //
        // Every time a method is called on the proxy object, Java routes
        // the call here. We look up the correct delegate and forward the call.
        //
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            Object delegate = delegatesByMethod.get(methodName);
            return method.invoke(delegate, args);
        }

        // ============================================================
        // newInstance(): Factory method to build a proxy
        // ============================================================
        //
        // Creates a new proxy object that implements all given interfaces
        // and delegates method calls to the appropriate objects.
        //
        @SuppressWarnings("unchecked")
        public static Object newInstance(Tuple2... pairs) {
            // Collect all interfaces
            Class[] interfaces = new Class[pairs.length];
            for (int i = 0; i < pairs.length; i++) {
                interfaces[i] = (Class) pairs[i].a2;
            }

            // Use the class loader of the first object
            ClassLoader cl = pairs[0].a1.getClass().getClassLoader();

            // Create the proxy object
            return Proxy.newProxyInstance(
                    cl, interfaces, new MixinProxy(pairs));
        }
    }

    // ============================================================
    // Example Interfaces and Implementations
    // ============================================================
    //
    // These are simple interfaces and classes used to demonstrate
    // the mixin mechanism.
    //
    interface Basic {
        void set(String val);

        String get();
    }

    static class BasicImp implements Basic {
        private String value;

        public void set(String val) {
            value = val;
        }

        public String get() {
            return value;
        }
    }

    interface TimeStamped {
        long getStamp();
    }

    static class TimeStampedImp implements TimeStamped {
        private final long timeStamp;

        public TimeStampedImp() {
            timeStamp = System.currentTimeMillis();
        }

        public long getStamp() {
            return timeStamp;
        }
    }

    interface SerialNumbered {
        long getSerialNumber();
    }

    static class SerialNumberedImp implements SerialNumbered {
        private static long counter = 1;
        private final long serialNumber = counter++;

        public long getSerialNumber() {
            return serialNumber;
        }
    }

    // ============================================================
    // Main Demonstration
    // ============================================================
    public static void main(String[] args) {
        Object mixin = MixinProxy.newInstance(
                tuple(new BasicImp(), Basic.class),
                tuple(new TimeStampedImp(), TimeStamped.class),
                tuple(new SerialNumberedImp(), SerialNumbered.class));

        // Cast to each interface before using
        Basic b = (Basic) mixin;
        TimeStamped t = (TimeStamped) mixin;
        SerialNumbered s = (SerialNumbered) mixin;

        // Step-by-step trace:
        // 1. Call b.set("Hello")
        //    -> Proxy intercepts call
        //    -> invoke() in MixinProxy is triggered
        //    -> methodName = "set"
        //    -> delegate = BasicImp instance
        //    -> method.invoke(delegate, args) executes BasicImp.set("Hello")
        b.set("Hello");

        // 2. Call b.get()
        //    -> Proxy intercepts call
        //    -> invoke() triggered
        //    -> methodName = "get"
        //    -> delegate = BasicImp instance
        //    -> method.invoke(delegate, args) executes BasicImp.get()
        System.out.println(b.get());

        // 3. Call t.getStamp()
        //    -> Proxy intercepts call
        //    -> invoke() triggered
        //    -> methodName = "getStamp"
        //    -> delegate = TimeStampedImp instance
        //    -> method.invoke(delegate, args) executes TimeStampedImp.getStamp()
        System.out.println(t.getStamp());

        // 4. Call s.getSerialNumber()
        //    -> Proxy intercepts call
        //    -> invoke() triggered
        //    -> methodName = "getSerialNumber"
        //    -> delegate = SerialNumberedImp instance
        //    -> method.invoke(delegate, args) executes SerialNumberedImp.getSerialNumber()
        System.out.println(s.getSerialNumber());
    }
}
/* ============================================================
   How the Code Works
   ============================================================
   1. Tuple2 holds pairs of (object, interface).
   2. MixinProxy builds a map of method names -> delegate objects.
   3. Proxy.newProxyInstance() creates a runtime proxy that implements
      all specified interfaces.
   4. When a method is called on the proxy, invoke() forwards it to
      the correct delegate object.
   5. The result is a single object that appears to implement multiple
      interfaces, with behavior delegated to different implementations.

   ============================================================
   Advantages vs Limitations
   ============================================================
   Advantages:
   - Allows combining multiple behaviors into one object at runtime.
   - Closer to a true mixin than the Decorator pattern.
   - Flexible: you can mix different implementations dynamically.

   Limitations:
   - Only works with interfaces (not concrete classes).
   - Requires downcasting to the correct interface before calling methods.
   - Not as seamless as multiple inheritance in languages like C++.
*/