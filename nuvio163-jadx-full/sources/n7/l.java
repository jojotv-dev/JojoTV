package n7;

/* JADX INFO: loaded from: classes3.dex */
public final class l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public m f39647a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public s0 f39648b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f39649c;

    public final String toString() {
        String str;
        StringBuilder sb2 = new StringBuilder();
        sb2.append(String.valueOf(this.f39647a));
        sb2.append(" {...} (src=");
        int i10 = this.f39649c;
        if (i10 != 1) {
            str = i10 != 2 ? "null" : "RenderOptions";
        } else {
            str = "Document";
        }
        sb2.append(str);
        sb2.append(")");
        return sb2.toString();
    }
}
