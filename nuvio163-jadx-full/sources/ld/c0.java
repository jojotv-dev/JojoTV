package ld;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class c0 {
    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof c0) && Intrinsics.areEqual("", "") && Intrinsics.areEqual("1.1.46", "1.1.46") && Intrinsics.areEqual("https://api.mdblist.com", "https://api.mdblist.com");
    }

    public final int hashCode() {
        return -1291963171;
    }

    public final String toString() {
        return "MdbListConfiguration(clientId=, appVersion=1.1.46, baseUrl=https://api.mdblist.com)";
    }
}
