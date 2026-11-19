package a9;

/* JADX INFO: loaded from: classes4.dex */
public enum f implements k9.c {
    /* JADX INFO: Fake field, exist only in values array */
    FILE_SUPERSEDED(0),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_OPENED(1),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_CREATED(2),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_OVERWRITTEN(3);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f634b;

    f(long j10) {
        this.f634b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f634b;
    }
}
