package kotlin.reflect.jvm.internal.impl.types;

/* JADX INFO: loaded from: classes3.dex */
public final class TypeParameterErasureOptions {
    private final boolean intersectUpperBounds;
    private final boolean leaveNonTypeParameterTypes;

    public TypeParameterErasureOptions(boolean z2, boolean z10) {
        this.leaveNonTypeParameterTypes = z2;
        this.intersectUpperBounds = z10;
    }

    public final boolean getIntersectUpperBounds() {
        return this.intersectUpperBounds;
    }

    public final boolean getLeaveNonTypeParameterTypes() {
        return this.leaveNonTypeParameterTypes;
    }
}
