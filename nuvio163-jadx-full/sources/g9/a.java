package g9;

/* JADX INFO: loaded from: classes4.dex */
public enum a implements k9.c {
    MsvAvEOL(0),
    MsvAvNbComputerName(1),
    /* JADX INFO: Fake field, exist only in values array */
    MsvAvNbDomainName(2),
    MsvAvDnsComputerName(3),
    /* JADX INFO: Fake field, exist only in values array */
    MsvAvDnsDomainName(4),
    /* JADX INFO: Fake field, exist only in values array */
    MsvAvDnsTreeName(5),
    MsvAvFlags(6),
    MsvAvTimestamp(7),
    MsvAvSingleHost(8),
    MsvAvTargetName(9),
    MsvAvChannelBindings(10);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f18840b;

    a(long j10) {
        this.f18840b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f18840b;
    }
}
