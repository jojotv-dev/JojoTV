package yd;

/* JADX INFO: loaded from: classes4.dex */
public final class c {
    public final boolean equals(Object obj) {
        return this == obj || (obj instanceof c);
    }

    public final int hashCode() {
        return Integer.hashCode(128) + (Integer.hashCode(10000) * 31);
    }

    public final String toString() {
        return "AssHandlerConfig(glyphSize=10000, cacheSize=128)";
    }
}
