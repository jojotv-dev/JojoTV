package aj;

import okhttp3.Response;
import w6.s;
import xp.u0;

/* JADX INFO: loaded from: classes5.dex */
public class c extends RuntimeException {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f1839b;

    @Override // java.lang.Throwable
    public String getMessage() {
        switch (this.f1839b) {
            case 4:
                return "Chain of Causes for CompositeException In Order Received =>";
            default:
                return super.getMessage();
        }
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ c(String str, int i10) {
        super(str);
        this.f1839b = i10;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ c(String str, Throwable th2, int i10) {
        super(str, th2);
        this.f1839b = i10;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ c(Throwable th2, int i10) {
        super(th2);
        this.f1839b = i10;
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public c(s sVar) {
        super("HTTP " + sVar.f62097a);
        this.f1839b = 9;
    }

    public c(u0 u0Var) {
        this.f1839b = 11;
        StringBuilder sb2 = new StringBuilder("HTTP ");
        Response response = u0Var.f64280a;
        sb2.append(response.code());
        sb2.append(" ");
        sb2.append(response.message());
        super(sb2.toString());
        response.code();
        response.message();
    }
}
