package kotlin.comparisons;

import java.util.Comparator;

/* JADX INFO: loaded from: classes5.dex */
public final /* synthetic */ class b implements Comparator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f31705b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Comparator f31706c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Comparator f31707e;

    public /* synthetic */ b(Comparator comparator, Comparator comparator2, int i10) {
        this.f31705b = i10;
        this.f31706c = comparator;
        this.f31707e = comparator2;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        switch (this.f31705b) {
            case 0:
                return ComparisonsKt__ComparisonsKt.thenDescending$lambda$0$ComparisonsKt__ComparisonsKt(this.f31706c, this.f31707e, obj, obj2);
            default:
                return ComparisonsKt__ComparisonsKt.then$lambda$0$ComparisonsKt__ComparisonsKt(this.f31706c, this.f31707e, obj, obj2);
        }
    }
}
