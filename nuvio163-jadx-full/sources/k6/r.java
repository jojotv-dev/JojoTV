package k6;

import kotlin.coroutines.jvm.internal.ContinuationImpl;

/* JADX INFO: loaded from: classes.dex */
public final class r extends ContinuationImpl {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Object f27934b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public c7.p f27935c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public c7.g f27936e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public f f27937i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public Object f27938m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public Object f27939n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public Object f27940o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public int f27941p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public /* synthetic */ Object f27942q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public final /* synthetic */ s f27943r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public int f27944s;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public r(s sVar, ContinuationImpl continuationImpl) {
        super(continuationImpl);
        this.f27943r = sVar;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        this.f27942q = obj;
        this.f27944s |= Integer.MIN_VALUE;
        return this.f27943r.b(null, 0, this);
    }
}
