package i9;

/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX WARN: Unknown enum class pattern. Please report as an issue! */
/* JADX INFO: loaded from: classes4.dex */
public final class h implements k9.c {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final h f23879c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final /* synthetic */ h[] f23880e;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public long f23881b;

    static {
        h hVar = new h("NTLMSSP_REVISION_W2K3", 0);
        hVar.f23881b = 15;
        f23879c = hVar;
        f23880e = new h[]{hVar};
    }

    public static h valueOf(String str) {
        return (h) Enum.valueOf(h.class, str);
    }

    public static h[] values() {
        return (h[]) f23880e.clone();
    }

    @Override // k9.c
    public final long getValue() {
        return this.f23881b;
    }
}
