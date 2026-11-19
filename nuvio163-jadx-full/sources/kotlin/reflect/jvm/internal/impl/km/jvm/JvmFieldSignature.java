package kotlin.reflect.jvm.internal.impl.km.jvm;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public final class JvmFieldSignature extends JvmMemberSignature {
    private final String descriptor;
    private final String name;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public JvmFieldSignature(String name, String descriptor) {
        super(null);
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(descriptor, "descriptor");
        this.name = name;
        this.descriptor = descriptor;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof JvmFieldSignature)) {
            return false;
        }
        JvmFieldSignature jvmFieldSignature = (JvmFieldSignature) obj;
        return Intrinsics.areEqual(this.name, jvmFieldSignature.name) && Intrinsics.areEqual(this.descriptor, jvmFieldSignature.descriptor);
    }

    @Override // kotlin.reflect.jvm.internal.impl.km.jvm.JvmMemberSignature
    public String getDescriptor() {
        return this.descriptor;
    }

    @Override // kotlin.reflect.jvm.internal.impl.km.jvm.JvmMemberSignature
    public String getName() {
        return this.name;
    }

    public int hashCode() {
        return this.descriptor.hashCode() + (this.name.hashCode() * 31);
    }

    @Override // kotlin.reflect.jvm.internal.impl.km.jvm.JvmMemberSignature
    public String toString() {
        return getName() + ':' + getDescriptor();
    }
}
