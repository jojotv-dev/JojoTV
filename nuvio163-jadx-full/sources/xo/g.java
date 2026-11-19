package xo;

/* JADX INFO: loaded from: classes5.dex */
public final class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public i f64090a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public i f64091b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public i f64092c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public String f64093d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f64094e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public g f64095f;

    public static g a(g gVar, i iVar, i iVar2) {
        if (gVar == null) {
            return null;
        }
        g gVarA = a(gVar.f64095f, iVar, iVar2);
        gVar.f64095f = gVarA;
        int i10 = gVar.f64090a.f64107c;
        i iVar3 = gVar.f64091b;
        int i11 = iVar3.f64107c;
        int i12 = iVar.f64107c;
        int i13 = iVar2 == null ? Integer.MAX_VALUE : iVar2.f64107c;
        if (i12 < i11 && i13 > i10) {
            if (i12 <= i10) {
                if (i13 >= i11) {
                    return gVarA;
                }
                gVar.f64090a = iVar2;
                return gVar;
            }
            if (i13 >= i11) {
                gVar.f64091b = iVar;
                return gVar;
            }
            g gVar2 = new g();
            gVar2.f64090a = iVar2;
            gVar2.f64091b = iVar3;
            gVar2.f64092c = gVar.f64092c;
            gVar2.f64093d = gVar.f64093d;
            gVar2.f64094e = gVar.f64094e;
            gVar2.f64095f = gVarA;
            gVar.f64091b = iVar;
            gVar.f64095f = gVar2;
        }
        return gVar;
    }
}
