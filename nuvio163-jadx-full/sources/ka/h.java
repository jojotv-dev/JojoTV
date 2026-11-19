package ka;

/* JADX INFO: loaded from: classes4.dex */
public abstract class h implements Comparable {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final e f28021c = new e(null);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f28022b;

    public h(Object obj) {
        this.f28022b = obj;
    }

    @Override // java.lang.Comparable
    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public int compareTo(h hVar) {
        return b().toString().compareTo(hVar.b().toString()) * (-1);
    }

    public abstract Object b();
}
