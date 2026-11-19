package ka;

/* JADX INFO: loaded from: classes4.dex */
public final class f extends h {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f28019e;

    @Override // ka.h
    /* JADX INFO: renamed from: a */
    public final int compareTo(h hVar) {
        throw null;
    }

    @Override // ka.h
    public final Object b() {
        return Integer.valueOf(this.f28019e);
    }

    @Override // ka.h, java.lang.Comparable
    public final int compareTo(Object obj) {
        h hVar = (h) obj;
        return hVar instanceof f ? Integer.valueOf(((f) hVar).f28019e).compareTo(Integer.valueOf(this.f28019e)) : super.compareTo(hVar);
    }
}
