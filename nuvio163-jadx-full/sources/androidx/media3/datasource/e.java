package androidx.media3.datasource;

import java.util.Map;
import q7.i;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class e implements i {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f2827b;

    public /* synthetic */ e(int i10) {
        this.f2827b = i10;
    }

    @Override // q7.i
    public final boolean apply(Object obj) {
        switch (this.f2827b) {
            case 0:
                return DefaultHttpDataSource.NullFilteringHeadersMap.lambda$entrySet$1((Map.Entry) obj);
            case 1:
                return DefaultHttpDataSource.NullFilteringHeadersMap.lambda$keySet$0((String) obj);
            default:
                return HttpDataSource.lambda$static$0((String) obj);
        }
    }
}
