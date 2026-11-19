package kotlin.comparisons;

import java.util.Comparator;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class a implements Comparator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31703b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Comparator f31704c;

    public /* synthetic */ a(Comparator comparator, int i10) {
        this.f31703b = i10;
        this.f31704c = comparator;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        switch (this.f31703b) {
            case 0:
                return ComparisonsKt__ComparisonsKt.nullsLast$lambda$0$ComparisonsKt__ComparisonsKt(this.f31704c, obj, obj2);
            default:
                return ComparisonsKt__ComparisonsKt.nullsFirst$lambda$0$ComparisonsKt__ComparisonsKt(this.f31704c, obj, obj2);
        }
    }
}
