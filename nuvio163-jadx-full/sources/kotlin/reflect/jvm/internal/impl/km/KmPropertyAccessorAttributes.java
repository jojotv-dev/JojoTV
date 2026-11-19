package kotlin.reflect.jvm.internal.impl.km;

import java.util.ArrayList;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public final class KmPropertyAccessorAttributes {
    private final List<KmAnnotation> annotations;
    private int flags;

    public KmPropertyAccessorAttributes(int i10) {
        this.flags = i10;
        this.annotations = new ArrayList(0);
    }

    public final List<KmAnnotation> getAnnotations() {
        return this.annotations;
    }

    public final int getFlags$kotlin_metadata() {
        return this.flags;
    }

    public final void setFlags$kotlin_metadata(int i10) {
        this.flags = i10;
    }

    public KmPropertyAccessorAttributes() {
        this(0);
    }
}
