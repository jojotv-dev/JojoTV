package fe;

import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class k5 extends GZIPOutputStream {
    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public k5(OutputStream out) {
        super(out, 65536);
        Intrinsics.checkNotNullParameter(out, "out");
        ((GZIPOutputStream) this).def.setLevel(1);
    }

    public k5(BufferedOutputStream bufferedOutputStream) {
        super(bufferedOutputStream, 65536);
        ((GZIPOutputStream) this).def.setLevel(1);
    }
}
