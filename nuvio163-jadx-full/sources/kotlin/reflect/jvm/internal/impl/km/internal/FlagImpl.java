package kotlin.reflect.jvm.internal.impl.km.internal;

import kotlin.jvm.internal.Intrinsics;
import kotlin.reflect.jvm.internal.impl.metadata.deserialization.Flags;

/* JADX INFO: loaded from: classes3.dex */
public final class FlagImpl {
    private final int bitWidth;
    private final int offset;
    private final int value;

    public FlagImpl(int i10, int i11, int i12) {
        this.offset = i10;
        this.bitWidth = i11;
        this.value = i12;
    }

    public final int getBitWidth$kotlin_metadata() {
        return this.bitWidth;
    }

    public final int getOffset$kotlin_metadata() {
        return this.offset;
    }

    public final int getValue$kotlin_metadata() {
        return this.value;
    }

    public final boolean invoke(int i10) {
        return ((i10 >>> this.offset) & ((1 << this.bitWidth) - 1)) == this.value;
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public FlagImpl(Flags.FlagField<?> field, int i10) {
        this(field.offset, field.bitWidth, i10);
        Intrinsics.checkNotNullParameter(field, "field");
    }

    /* JADX WARN: 'this' call moved to the top of the method (can break code semantics) */
    public FlagImpl(Flags.BooleanFlagField field) {
        this(field, 1);
        Intrinsics.checkNotNullParameter(field, "field");
    }
}
