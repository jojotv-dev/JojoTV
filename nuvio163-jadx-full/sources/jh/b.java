package jh;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes5.dex */
public final class b implements lh.a {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final b f27500b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ b[] f27501c;

    static {
        b bVar = new b("INSTANCE", 0);
        f27500b = bVar;
        f27501c = new b[]{bVar, new b("NEVER", 1)};
    }

    public static b valueOf(String str) {
        return (b) Enum.valueOf(b.class, str);
    }

    public static b[] values() {
        return (b[]) f27501c.clone();
    }

    @Override // gh.b
    public final boolean a() {
        return this == f27500b;
    }

    @Override // lh.b
    public final boolean isEmpty() {
        return true;
    }

    @Override // lh.a
    public final int k() {
        return 2;
    }

    @Override // lh.b
    public final boolean offer(Object obj) {
        throw new UnsupportedOperationException("Should not be called!");
    }

    @Override // lh.b
    public final Object poll() {
        return null;
    }

    @Override // lh.b
    public final void clear() {
    }

    @Override // gh.b
    public final void dispose() {
    }
}
