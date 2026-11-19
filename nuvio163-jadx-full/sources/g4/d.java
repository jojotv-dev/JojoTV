package g4;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes3.dex */
public final class d implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f18781b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ String f18782c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Context f18783e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ int f18784i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final /* synthetic */ Object f18785m;

    public /* synthetic */ d(String str, Context context, Object obj, int i10, int i11) {
        this.f18781b = i11;
        this.f18782c = str;
        this.f18783e = context;
        this.f18785m = obj;
        this.f18784i = i10;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f18781b) {
            case 0:
                Object[] objArr = {(c) this.f18785m};
                ArrayList arrayList = new ArrayList(1);
                Object obj = objArr[0];
                Objects.requireNonNull(obj);
                arrayList.add(obj);
                return g.b(this.f18782c, this.f18783e, Collections.unmodifiableList(arrayList), this.f18784i);
            default:
                try {
                    return g.b(this.f18782c, this.f18783e, (List) this.f18785m, this.f18784i);
                } catch (Throwable unused) {
                    return new f(-3);
                }
        }
    }
}
