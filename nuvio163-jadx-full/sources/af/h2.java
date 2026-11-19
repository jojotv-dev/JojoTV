package af;

import java.util.List;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final class h2 implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f1079b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Function1 f1080c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ List f1081e;

    public /* synthetic */ h2(List list, int i10, Function1 function1) {
        this.f1079b = i10;
        this.f1080c = function1;
        this.f1081e = list;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f1079b) {
            case 0:
                return this.f1080c.invoke(this.f1081e.get(((Number) obj).intValue()));
            case 1:
                return this.f1080c.invoke(this.f1081e.get(((Number) obj).intValue()));
            case 2:
                return this.f1080c.invoke(this.f1081e.get(((Number) obj).intValue()));
            case 3:
                return this.f1080c.invoke(this.f1081e.get(((Number) obj).intValue()));
            default:
                return this.f1080c.invoke(this.f1081e.get(((Number) obj).intValue()));
        }
    }
}
