package ek;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes5.dex */
public final class f {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final f f15760b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final f f15761c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ f[] f15762e;

    /* JADX INFO: Fake field, exist only in values array */
    f EF0;

    static {
        f fVar = new f("AGREEMENT", 0);
        f fVar2 = new f("ENCRYPTION", 1);
        f fVar3 = new f("DECRYPTION", 2);
        f fVar4 = new f("KEYGEN", 3);
        f fVar5 = new f("SIGNING", 4);
        f fVar6 = new f("VERIFYING", 5);
        f fVar7 = new f("AUTHENTICATION", 6);
        f fVar8 = new f("VERIFICATION", 7);
        f fVar9 = new f("PRF", 8);
        f15760b = fVar9;
        f fVar10 = new f("ANY", 9);
        f15761c = fVar10;
        f15762e = new f[]{fVar, fVar2, fVar3, fVar4, fVar5, fVar6, fVar7, fVar8, fVar9, fVar10};
    }

    public static f valueOf(String str) {
        return (f) Enum.valueOf(f.class, str);
    }

    public static f[] values() {
        return (f[]) f15762e.clone();
    }
}
