package id;

import com.nuvio.vpn.android.o0;
import java.util.function.Consumer;
import kotlin.Unit;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class f implements Consumer {
    @Override // java.util.function.Consumer
    public final void accept(Object obj) {
        o0 o0Var = (o0) obj;
        if (o0Var != null) {
            o0Var.close();
        }
        Unit unit = Unit.INSTANCE;
    }
}
