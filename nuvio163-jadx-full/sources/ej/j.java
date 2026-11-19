package ej;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes5.dex */
public final class j {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final j f15748b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final j f15749c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ j[] f15750e;

    static {
        j jVar = new j("Strong", 0);
        f15748b = jVar;
        j jVar2 = new j("Weak", 1);
        f15749c = jVar2;
        f15750e = new j[]{jVar, jVar2};
    }

    public static j valueOf(String str) {
        return (j) Enum.valueOf(j.class, str);
    }

    public static j[] values() {
        return (j[]) f15750e.clone();
    }
}
