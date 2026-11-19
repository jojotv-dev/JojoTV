package c7;

import android.content.Context;
import kotlin.jvm.internal.Intrinsics;
import okio.FileSystem;

/* JADX INFO: loaded from: classes.dex */
public final class o {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f6567a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final d7.h f6568b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final d7.f f6569c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final d7.d f6570d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final String f6571e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final FileSystem f6572f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final b f6573g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final b f6574h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final b f6575i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final k6.j f6576j;

    public o(Context context, d7.h hVar, d7.f fVar, d7.d dVar, String str, FileSystem fileSystem, b bVar, b bVar2, b bVar3, k6.j jVar) {
        this.f6567a = context;
        this.f6568b = hVar;
        this.f6569c = fVar;
        this.f6570d = dVar;
        this.f6571e = str;
        this.f6572f = fileSystem;
        this.f6573g = bVar;
        this.f6574h = bVar2;
        this.f6575i = bVar3;
        this.f6576j = jVar;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof o)) {
            return false;
        }
        o oVar = (o) obj;
        return Intrinsics.areEqual(this.f6567a, oVar.f6567a) && Intrinsics.areEqual(this.f6568b, oVar.f6568b) && this.f6569c == oVar.f6569c && this.f6570d == oVar.f6570d && Intrinsics.areEqual(this.f6571e, oVar.f6571e) && Intrinsics.areEqual(this.f6572f, oVar.f6572f) && this.f6573g == oVar.f6573g && this.f6574h == oVar.f6574h && this.f6575i == oVar.f6575i && Intrinsics.areEqual(this.f6576j, oVar.f6576j);
    }

    public final int hashCode() {
        int iHashCode = (this.f6570d.hashCode() + ((this.f6569c.hashCode() + ((this.f6568b.hashCode() + (this.f6567a.hashCode() * 31)) * 31)) * 31)) * 31;
        String str = this.f6571e;
        return this.f6576j.f27920a.hashCode() + ((this.f6575i.hashCode() + ((this.f6574h.hashCode() + ((this.f6573g.hashCode() + ((this.f6572f.hashCode() + ((iHashCode + (str == null ? 0 : str.hashCode())) * 31)) * 31)) * 31)) * 31)) * 31);
    }

    public final String toString() {
        return "Options(context=" + this.f6567a + ", size=" + this.f6568b + ", scale=" + this.f6569c + ", precision=" + this.f6570d + ", diskCacheKey=" + this.f6571e + ", fileSystem=" + this.f6572f + ", memoryCachePolicy=" + this.f6573g + ", diskCachePolicy=" + this.f6574h + ", networkCachePolicy=" + this.f6575i + ", extras=" + this.f6576j + ')';
    }
}
