package a9;

/* JADX INFO: loaded from: classes4.dex */
public enum d0 implements k9.c {
    NONE(0),
    /* JADX INFO: Fake field, exist only in values array */
    LZNT1(1),
    /* JADX INFO: Fake field, exist only in values array */
    LZ77(2),
    /* JADX INFO: Fake field, exist only in values array */
    LZ77_HUFFMAN(3);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f629b;

    d0(long j10) {
        this.f629b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f629b;
    }
}
