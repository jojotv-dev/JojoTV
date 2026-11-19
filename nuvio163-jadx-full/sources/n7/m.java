package n7;

import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public final class m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public ArrayList f39650a = null;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f39651b = 0;

    public final void a() {
        this.f39651b += 1000;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder();
        Iterator it = this.f39650a.iterator();
        while (it.hasNext()) {
            sb2.append((n) it.next());
            sb2.append(' ');
        }
        sb2.append('[');
        return o2.a.p(sb2, this.f39651b, ']');
    }
}
