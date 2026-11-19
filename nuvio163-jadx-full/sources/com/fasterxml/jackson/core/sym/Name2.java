package com.fasterxml.jackson.core.sym;

/* JADX INFO: loaded from: classes3.dex */
public final class Name2 extends Name {

    /* JADX INFO: renamed from: q1, reason: collision with root package name */
    private final int f7493q1;

    /* JADX INFO: renamed from: q2, reason: collision with root package name */
    private final int f7494q2;

    public Name2(String str, int i10, int i11, int i12) {
        super(str, i10);
        this.f7493q1 = i11;
        this.f7494q2 = i12;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int i10) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int i10, int i11, int i12) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int i10, int i11) {
        return i10 == this.f7493q1 && i11 == this.f7494q2;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int[] iArr, int i10) {
        return i10 == 2 && iArr[0] == this.f7493q1 && iArr[1] == this.f7494q2;
    }
}
