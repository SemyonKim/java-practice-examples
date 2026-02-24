package puzzlers.bloch.libraryadvanced.puzzle1.library;

public class Api {
    static class PackagePrivate {} // Not visible to outside world
    public static PackagePrivate member = new PackagePrivate();
}
