package q7;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public final class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f45746a;

    public g(String str) {
        str.getClass();
        this.f45746a = str;
    }

    public static g d() {
        return new g(", ");
    }

    public static g e() {
        return new g(String.valueOf(','));
    }

    public final void a(StringBuilder sb2, Iterable iterable) {
        b(sb2, iterable.iterator());
    }

    public final void b(StringBuilder sb2, Iterator it) {
        try {
            if (it.hasNext()) {
                Object next = it.next();
                Objects.requireNonNull(next);
                sb2.append(next instanceof CharSequence ? (CharSequence) next : next.toString());
                while (it.hasNext()) {
                    sb2.append((CharSequence) this.f45746a);
                    Object next2 = it.next();
                    Objects.requireNonNull(next2);
                    sb2.append(next2 instanceof CharSequence ? (CharSequence) next2 : next2.toString());
                }
            }
        } catch (IOException e10) {
            throw new AssertionError(e10);
        }
    }

    public final String c(List list) {
        Iterator it = list.iterator();
        StringBuilder sb2 = new StringBuilder();
        b(sb2, it);
        return sb2.toString();
    }
}
