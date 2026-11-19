package a6;

import android.content.ContentValues;
import android.graphics.Bitmap;

/* JADX INFO: loaded from: classes3.dex */
public final class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public ContentValues f593a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public volatile Bitmap f594b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f595c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public volatile boolean f596d;

    public final boolean equals(Object obj) {
        if (obj instanceof f) {
            return this.f593a.equals(((f) obj).f593a);
        }
        return false;
    }

    public final int hashCode() {
        return this.f593a.hashCode();
    }

    public final String toString() {
        return "Channel{" + this.f593a.toString() + "}";
    }
}
