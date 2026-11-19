package kotlin.sequences;

import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class g implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31769b;

    public /* synthetic */ g(int i10) {
        this.f31769b = i10;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f31769b) {
            case 0:
                return Boolean.valueOf(SequencesKt___SequencesKt.filterNotNull$lambda$0$SequencesKt___SequencesKt(obj));
            default:
                return SequencesKt___SequencesKt.distinct$lambda$0$SequencesKt___SequencesKt(obj);
        }
    }
}
