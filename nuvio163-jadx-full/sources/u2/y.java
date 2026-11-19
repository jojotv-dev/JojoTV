package u2;

import java.util.Map;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes.dex */
public final class y implements v0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f58247a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f58248b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Map f58249c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final /* synthetic */ Function1 f58250d;

    public y(int i10, int i11, Map map, Function1 function1) {
        this.f58247a = i10;
        this.f58248b = i11;
        this.f58249c = map;
        this.f58250d = function1;
    }

    @Override // u2.v0
    public final Map a() {
        return this.f58249c;
    }

    @Override // u2.v0
    public final Function1 c() {
        return this.f58250d;
    }

    @Override // u2.v0
    public final int getHeight() {
        return this.f58248b;
    }

    @Override // u2.v0
    public final int getWidth() {
        return this.f58247a;
    }

    @Override // u2.v0
    public final void b() {
    }
}
