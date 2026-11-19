package up;

/* JADX INFO: loaded from: classes5.dex */
public class i0 extends b0 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public byte[] f60158b;

    @Override // up.b0
    public final void a(ce.k kVar) {
        this.f60158b = kVar.q();
    }

    @Override // up.b0
    public final String b() {
        StringBuffer stringBuffer = new StringBuffer("<");
        stringBuffer.append(wp.a.j(this.f60158b));
        stringBuffer.append(">");
        return stringBuffer.toString();
    }

    @Override // up.b0
    public final void c(y yVar) {
        yVar.n(this.f60158b);
    }
}
