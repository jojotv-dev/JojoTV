package a9;

/* JADX INFO: loaded from: classes4.dex */
public enum f0 implements k9.c {
    f635i("AES_128_CCM", "AES/CCM/NoPadding", 11),
    f636m("AES_128_GCM", "AES/GCM/NoPadding", 12);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f638b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f639c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final int f640e;

    f0(String str, String str2, int i10) {
        this.f638b = j;
        this.f639c = str2;
        this.f640e = i10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f638b;
    }
}
