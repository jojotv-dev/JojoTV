package kotlin.collections;

import java.util.Map;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.markers.KMappedMarker;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class a implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31694b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ KMappedMarker f31695c;

    public /* synthetic */ a(KMappedMarker kMappedMarker, int i10) {
        this.f31694b = i10;
        this.f31695c = kMappedMarker;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f31694b) {
            case 0:
                return AbstractCollection.toString$lambda$0((AbstractCollection) this.f31695c, obj);
            default:
                return AbstractMap.toString$lambda$0((AbstractMap) this.f31695c, (Map.Entry) obj);
        }
    }
}
