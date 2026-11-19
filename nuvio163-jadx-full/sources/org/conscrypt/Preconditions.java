package org.conscrypt;

import a0.i;
import h1.f;

/* JADX INFO: loaded from: classes5.dex */
final class Preconditions {
    private Preconditions() {
    }

    private static String badPositionIndex(int i10, int i11, String str) {
        if (i10 < 0) {
            return str + " (" + i10 + ") must not be negative";
        }
        if (i11 < 0) {
            throw new IllegalArgumentException(i.k("negative size: ", i11));
        }
        return str + " (" + i10 + ") must not be greater than size (" + i11 + ")";
    }

    private static String badPositionIndexes(int i10, int i11, int i12) {
        if (i10 < 0 || i10 > i12) {
            return badPositionIndex(i10, i12, "start index");
        }
        return (i11 < 0 || i11 > i12) ? badPositionIndex(i11, i12, "end index") : f.i(i11, "end index (", ") must not be less than start index (", ")", i10);
    }

    public static void checkArgument(boolean z2, String str) {
        if (!z2) {
            throw new IllegalArgumentException(str);
        }
    }

    public static <T> T checkNotNull(T t10, String str) {
        if (t10 != null) {
            return t10;
        }
        throw new NullPointerException(str);
    }

    public static void checkPositionIndexes(int i10, int i11, int i12) {
        if (i10 < 0 || i11 < i10 || i11 > i12) {
            throw new IndexOutOfBoundsException(badPositionIndexes(i10, i11, i12));
        }
    }

    public static void checkArgument(boolean z2, String str, Object obj) {
        if (!z2) {
            throw new IllegalArgumentException(String.format(str, obj));
        }
    }
}
