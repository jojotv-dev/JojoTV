package xn;

import java.util.ArrayList;

/* JADX INFO: loaded from: classes5.dex */
public final class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ArrayList f64029a = new ArrayList(1);

    public a(int i10, String str) {
    }

    public final synchronized boolean a(int i10) {
        if (this.f64029a.size() >= 256) {
            return false;
        }
        this.f64029a.add(Integer.valueOf(i10));
        return true;
    }
}
