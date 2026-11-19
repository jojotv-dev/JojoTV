package androidx.recyclerview.widget;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes.dex */
public final class e0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final e0 f3920b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final /* synthetic */ e0[] f3921c;

    static {
        e0 e0Var = new e0("ALLOW", 0);
        f3920b = e0Var;
        f3921c = new e0[]{e0Var, new e0("PREVENT_WHEN_EMPTY", 1), new e0("PREVENT", 2)};
    }

    public static e0 valueOf(String str) {
        return (e0) Enum.valueOf(e0.class, str);
    }

    public static e0[] values() {
        return (e0[]) f3921c.clone();
    }
}
