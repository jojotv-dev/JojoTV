package kotlin.sequences;

import kotlin.collections.IndexedValue;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class c implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31764b;

    public /* synthetic */ c(int i10) {
        this.f31764b = i10;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f31764b) {
            case 0:
                return SequencesKt__SequencesKt.flatten$lambda$0$SequencesKt__SequencesKt((Sequence) obj);
            case 1:
                return SequencesKt__SequencesKt.flatten$lambda$1$SequencesKt__SequencesKt((Iterable) obj);
            case 2:
                return SequencesKt__SequencesKt.flatten$lambda$2$SequencesKt__SequencesKt(obj);
            default:
                return SequencesKt___SequencesKt.filterIndexed$lambda$1$SequencesKt___SequencesKt((IndexedValue) obj);
        }
    }
}
