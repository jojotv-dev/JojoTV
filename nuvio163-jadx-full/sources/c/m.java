package c;

import af.p1;
import androidx.lifecycle.h0;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Executor;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes.dex */
public final class m {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Executor f6341a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final p1 f6342b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f6343c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f6344d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f6345e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public boolean f6346f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final ArrayList f6347g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public final h0 f6348h;

    public m(Executor executor, p1 reportFullyDrawn) {
        Intrinsics.checkNotNullParameter(executor, "executor");
        Intrinsics.checkNotNullParameter(reportFullyDrawn, "reportFullyDrawn");
        this.f6341a = executor;
        this.f6342b = reportFullyDrawn;
        this.f6343c = new Object();
        this.f6347g = new ArrayList();
        this.f6348h = new h0(this, 4);
    }

    public final void a() {
        synchronized (this.f6343c) {
            try {
                this.f6346f = true;
                Iterator it = this.f6347g.iterator();
                while (it.hasNext()) {
                    ((Function0) it.next()).invoke();
                }
                this.f6347g.clear();
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }
}
