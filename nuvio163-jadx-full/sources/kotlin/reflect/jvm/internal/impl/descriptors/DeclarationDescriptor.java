package kotlin.reflect.jvm.internal.impl.descriptors;

import kotlin.reflect.jvm.internal.impl.descriptors.annotations.Annotated;

/* JADX INFO: loaded from: classes.dex */
public interface DeclarationDescriptor extends Named, Annotated {
    <R, D> R accept(DeclarationDescriptorVisitor<R, D> declarationDescriptorVisitor, D d10);

    DeclarationDescriptor getContainingDeclaration();

    DeclarationDescriptor getOriginal();
}
