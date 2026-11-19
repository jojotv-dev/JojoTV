package mf;

import java.util.List;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final class c1 implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f35857b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ List f35858c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ int f35859e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ g1.n1 f35860i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final /* synthetic */ g1.n1 f35861m;

    public /* synthetic */ c1(List list, int i10, g1.n1 n1Var, g1.n1 n1Var2, int i11) {
        this.f35857b = i11;
        this.f35858c = list;
        this.f35859e = i10;
        this.f35860i = n1Var;
        this.f35861m = n1Var2;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f35857b) {
            case 0:
                Boolean bool = (Boolean) obj;
                boolean zBooleanValue = bool.booleanValue();
                int i10 = this.f35859e;
                if (zBooleanValue) {
                    this.f35860i.setValue((String) this.f35858c.get(i10));
                }
                if (i10 == 0) {
                    this.f35861m.setValue(bool);
                }
                break;
            default:
                Boolean bool2 = (Boolean) obj;
                boolean zBooleanValue2 = bool2.booleanValue();
                int i11 = this.f35859e;
                if (zBooleanValue2) {
                    this.f35860i.setValue((String) this.f35858c.get(i11));
                }
                if (i11 == 0) {
                    this.f35861m.setValue(bool2);
                }
                break;
        }
        return Unit.INSTANCE;
    }
}
