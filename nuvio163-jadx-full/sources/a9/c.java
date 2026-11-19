package a9;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class c implements k9.c {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final c f615b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ c[] f616c;

    static {
        c cVar = new c("WATCH_TREE", 0);
        f615b = cVar;
        f616c = new c[]{cVar};
    }

    public static c valueOf(String str) {
        return (c) Enum.valueOf(c.class, str);
    }

    public static c[] values() {
        return (c[]) f616c.clone();
    }

    @Override // k9.c
    public final long getValue() {
        return 1L;
    }
}
