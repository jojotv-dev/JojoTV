package ej;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes5.dex */
public final class f {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final f f15734b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final f f15735c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ f[] f15736e;

    static {
        f fVar = new f("Synchronously", 0);
        f15734b = fVar;
        f fVar2 = new f("Asynchronously", 1);
        f15735c = fVar2;
        f15736e = new f[]{fVar, fVar2};
    }

    public static f valueOf(String str) {
        return (f) Enum.valueOf(f.class, str);
    }

    public static f[] values() {
        return (f[]) f15736e.clone();
    }
}
