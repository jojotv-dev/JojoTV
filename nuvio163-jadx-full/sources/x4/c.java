package x4;

import io.sentry.hints.j;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public abstract class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f63424a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public ByteBuffer f63425b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f63426c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f63427d;

    public c() {
        if (j.f25606c == null) {
            j.f25606c = new j();
        }
    }

    public final int a(int i10) {
        if (i10 < this.f63427d) {
            return this.f63425b.getShort(this.f63426c + i10);
        }
        return 0;
    }
}
