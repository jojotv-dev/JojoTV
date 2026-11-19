package jh;

import co.c;
import java.util.concurrent.atomic.AtomicReference;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes5.dex */
public final class a implements gh.b {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final a f27498b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ a[] f27499c;

    static {
        a aVar = new a("DISPOSED", 0);
        f27498b = aVar;
        f27499c = new a[]{aVar};
    }

    public static void b(AtomicReference atomicReference) {
        gh.b bVar;
        gh.b bVar2 = (gh.b) atomicReference.get();
        a aVar = f27498b;
        if (bVar2 == aVar || (bVar = (gh.b) atomicReference.getAndSet(aVar)) == aVar || bVar == null) {
            return;
        }
        bVar.dispose();
    }

    public static boolean c(gh.b bVar) {
        return bVar == f27498b;
    }

    public static boolean d(AtomicReference atomicReference, gh.b bVar) {
        while (true) {
            gh.b bVar2 = (gh.b) atomicReference.get();
            if (bVar2 != f27498b) {
                while (!atomicReference.compareAndSet(bVar2, bVar)) {
                    if (atomicReference.get() != bVar2) {
                    }
                }
                return true;
            }
            if (bVar == null) {
                return false;
            }
            bVar.dispose();
            return false;
        }
    }

    public static boolean e(AtomicReference atomicReference, gh.b bVar) {
        kh.a.a(bVar, "d is null");
        while (!atomicReference.compareAndSet(null, bVar)) {
            if (atomicReference.get() != null) {
                bVar.dispose();
                if (atomicReference.get() == f27498b) {
                    return false;
                }
                bo.a.H(new c("Disposable already set!"));
                return false;
            }
        }
        return true;
    }

    public static a valueOf(String str) {
        return (a) Enum.valueOf(a.class, str);
    }

    public static a[] values() {
        return (a[]) f27499c.clone();
    }

    @Override // gh.b
    public final boolean a() {
        return true;
    }

    @Override // gh.b
    public final void dispose() {
    }
}
