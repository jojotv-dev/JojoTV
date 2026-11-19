package com.fasterxml.jackson.core.sym;

/* JADX INFO: loaded from: classes3.dex */
public final class Name3 extends Name {

    /* JADX INFO: renamed from: q1, reason: collision with root package name */
    private final int f7495q1;

    /* JADX INFO: renamed from: q2, reason: collision with root package name */
    private final int f7496q2;

    /* JADX INFO: renamed from: q3, reason: collision with root package name */
    private final int f7497q3;

    public Name3(String str, int i10, int i11, int i12, int i13) {
        super(str, i10);
        this.f7495q1 = i11;
        this.f7496q2 = i12;
        this.f7497q3 = i13;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int i10) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int i10, int i11) {
        return false;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int i10, int i11, int i12) {
        return this.f7495q1 == i10 && this.f7496q2 == i11 && this.f7497q3 == i12;
    }

    @Override // com.fasterxml.jackson.core.sym.Name
    public boolean equals(int[] iArr, int i10) {
        return i10 == 3 && iArr[0] == this.f7495q1 && iArr[1] == this.f7496q2 && iArr[2] == this.f7497q3;
    }
}
