package com.fasterxml.jackson.core.sym;

/* JADX INFO: loaded from: classes3.dex */
public final class Name1 extends Name {
    private static final Name1 EMPTY = new Name1("", 0, 0);

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    private final int f7492q;

    public Name1(String str, int i10, int i11) {
        super(str, i10);
        this.f7492q = i11;
    }

    public static Name1 getEmptyName() {
        return EMPTY;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int i10, int i11, int i12) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int i10) {
        return i10 == this.f7492q;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int i10, int i11) {
        return i10 == this.f7492q && i11 == 0;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int[] iArr, int i10) {
        return i10 == 1 && iArr[0] == this.f7492q;
    }
}
