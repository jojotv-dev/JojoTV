package g2;

import d2.a0;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Lambda;
import okhttp3.internal.ws.WebSocketProtocol;

/* JADX INFO: loaded from: classes.dex */
public final class a extends Lambda implements Function1 {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final a f18654c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final a f18655e;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f18656b;

    static {
        int i10 = 1;
        f18654c = new a(i10, 0);
        f18655e = new a(i10, 1);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ a(int i10, int i11) {
        super(i10);
        this.f18656b = i11;
    }

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        switch (this.f18656b) {
            case 0:
                break;
            default:
                f2.d.F((f2.d) obj, a0.k, 0L, 0L, 0.0f, null, null, 0, WebSocketProtocol.PAYLOAD_SHORT);
                break;
        }
        return Unit.INSTANCE;
    }
}
