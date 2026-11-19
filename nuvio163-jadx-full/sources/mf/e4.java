package mf;

import androidx.media3.exoplayer.upstream.Allocation;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class e4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Allocation f36086a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ByteBuffer f36087b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final AtomicInteger f36088c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final AtomicReference f36089d;

    public e4(Allocation allocation, ByteBuffer byteBuffer) {
        Intrinsics.checkNotNullParameter(byteBuffer, "byteBuffer");
        this.f36086a = allocation;
        this.f36087b = byteBuffer;
        this.f36088c = new AtomicInteger(1);
        this.f36089d = new AtomicReference(null);
    }

    public final void a() {
        Function1 function1;
        if (this.f36088c.addAndGet(-2) != 0 || (function1 = (Function1) this.f36089d.get()) == null) {
            return;
        }
        function1.invoke(this);
    }

    public final boolean b() {
        AtomicInteger atomicInteger;
        int i10;
        do {
            atomicInteger = this.f36088c;
            i10 = atomicInteger.get();
            if ((i10 & 1) == 0) {
                return false;
            }
        } while (!atomicInteger.compareAndSet(i10, i10 + 2));
        return true;
    }
}
