package puzzlers.bloch.libraryadvanced.puzzle1.client;

import puzzlers.bloch.libraryadvanced.puzzle1.library.Api;

public class Client { // Example 2: The Compilation Access Trap
    public static void main(String[] args) {
        // ERROR: The qualifying type (PackagePrivate) is inaccessible
        // System.out.println(Api.member.hashCode());

        // FIX: Cast to an accessible type (Object)
        System.out.println(((Object) Api.member).hashCode());
    }
}
