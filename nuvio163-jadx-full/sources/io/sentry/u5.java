package io.sentry;

import java.util.concurrent.CopyOnWriteArraySet;

/* JADX INFO: loaded from: classes.dex */
public abstract class u5 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final CopyOnWriteArraySet f26229a = new CopyOnWriteArraySet();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final CopyOnWriteArraySet f26230b = new CopyOnWriteArraySet();

    public final void a(String str) {
        this.f26229a.add(str);
        this.f26230b.remove(str);
    }

    public void b(boolean z2) {
        CopyOnWriteArraySet copyOnWriteArraySet = this.f26230b;
        CopyOnWriteArraySet copyOnWriteArraySet2 = this.f26229a;
        if (z2) {
            copyOnWriteArraySet2.add("android.widget.ImageView");
            copyOnWriteArraySet.remove("android.widget.ImageView");
        } else {
            copyOnWriteArraySet.add("android.widget.ImageView");
            copyOnWriteArraySet2.remove("android.widget.ImageView");
        }
    }

    public void c(boolean z2) {
        CopyOnWriteArraySet copyOnWriteArraySet = this.f26230b;
        CopyOnWriteArraySet copyOnWriteArraySet2 = this.f26229a;
        if (z2) {
            copyOnWriteArraySet2.add("android.widget.TextView");
            copyOnWriteArraySet.remove("android.widget.TextView");
        } else {
            copyOnWriteArraySet.add("android.widget.TextView");
            copyOnWriteArraySet2.remove("android.widget.TextView");
        }
    }

    public abstract void d();
}
