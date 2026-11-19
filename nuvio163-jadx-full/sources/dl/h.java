package dl;

import androidx.media3.extractor.ts.TsExtractor;

/* JADX INFO: loaded from: classes5.dex */
public abstract class h {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final c6.a[] f13387e = new c6.a[0];

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final b f13388a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final c6.a f13389b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final c6.a f13390c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final c6.a[] f13391d;

    /* JADX WARN: Illegal instructions before constructor call */
    public h(b bVar, c6.a aVar, c6.a aVar2) {
        c6.a[] aVarArr;
        int i10 = bVar == null ? 0 : bVar.f13370f;
        if (i10 == 0 || i10 == 5) {
            aVarArr = f13387e;
        } else {
            c6.a aVarE = bVar.e(a.f13363b);
            if (i10 == 1 || i10 == 2) {
                aVarArr = new c6.a[]{aVarE};
            } else if (i10 == 3) {
                aVarArr = new c6.a[]{aVarE, aVarE, aVarE};
            } else if (i10 != 4) {
                if (i10 != 6) {
                    throw new IllegalArgumentException("unknown coordinate system");
                }
                aVarArr = new c6.a[]{aVarE};
            } else {
                aVarArr = new c6.a[]{aVarE, bVar.f13366b};
            }
        }
        this(bVar, aVar, aVar2, aVarArr);
    }

    /* JADX WARN: Code duplicated, block: B:72:0x00f6  */
    /* JADX WARN: Code duplicated, block: B:81:0x0113 A[RETURN] */
    public final boolean a(h hVar) {
        h hVarE;
        if (hVar != null) {
            b bVar = hVar.f13388a;
            b bVar2 = this.f13388a;
            boolean z2 = bVar2 == null;
            boolean z10 = bVar == null;
            boolean zD = d();
            boolean zD2 = hVar.d();
            if (zD || zD2) {
                if (zD && zD2 && (z2 || z10 || bVar2.d(bVar))) {
                    return true;
                }
            } else if (z2 && z10) {
                hVarE = this;
                if (!hVarE.f13389b.equals(hVar.f13389b)) {
                }
            } else if (z2) {
                hVar = hVar.e();
                hVarE = this;
                if (!hVarE.f13389b.equals(hVar.f13389b) && hVarE.b().equals(hVar.b())) {
                    return true;
                }
            } else {
                if (z10) {
                    hVarE = e();
                } else if (bVar2.d(bVar)) {
                    h[] hVarArr = new h[2];
                    hVarArr[0] = this;
                    hVarArr[1] = bVar2.i(hVar);
                    for (int i10 = 0; i10 < 2; i10++) {
                        h hVar2 = hVarArr[i10];
                        if (hVar2 != null && bVar2 != hVar2.f13388a) {
                            throw new IllegalArgumentException("'points' entries must be null or on this curve");
                        }
                    }
                    int i11 = bVar2.f13370f;
                    if (i11 != 0 && i11 != 5) {
                        c6.a[] aVarArr = new c6.a[2];
                        int[] iArr = new int[2];
                        int i12 = 0;
                        for (int i13 = 0; i13 < 2; i13++) {
                            h hVar3 = hVarArr[i13];
                            if (hVar3 != null) {
                                b bVar3 = hVar3.f13388a;
                                int i14 = bVar3 == null ? 0 : bVar3.f13370f;
                                if (i14 != 0 && i14 != 5 && !hVar3.d() && !hVar3.f13391d[0].E()) {
                                    aVarArr[i12] = hVar3.c();
                                    iArr[i12] = i13;
                                    i12++;
                                }
                            }
                        }
                        if (i12 != 0) {
                            c6.a[] aVarArr2 = new c6.a[i12];
                            aVarArr2[0] = aVarArr[0];
                            int i15 = 0;
                            while (true) {
                                int i16 = i15 + 1;
                                if (i16 >= i12) {
                                    break;
                                }
                                aVarArr2[i16] = aVarArr2[i15].O(aVarArr[i16]);
                                i15 = i16;
                            }
                            c6.a aVarB = aVarArr2[i15].B();
                            while (i15 > 0) {
                                int i17 = i15 - 1;
                                c6.a aVar = aVarArr[i15];
                                aVarArr[i15] = aVarArr2[i17].O(aVarB);
                                aVarB = aVarB.O(aVar);
                                i15 = i17;
                            }
                            aVarArr[0] = aVarB;
                            for (int i18 = 0; i18 < i12; i18++) {
                                int i19 = iArr[i18];
                                hVarArr[i19] = hVarArr[i19].f(aVarArr[i18]);
                            }
                        }
                    }
                    hVarE = hVarArr[0];
                    hVar = hVarArr[1];
                }
                if (!hVarE.f13389b.equals(hVar.f13389b)) {
                }
            }
        }
        return false;
    }

    public c6.a b() {
        return this.f13390c;
    }

    public c6.a c() {
        c6.a[] aVarArr = this.f13391d;
        if (aVarArr.length <= 0) {
            return null;
        }
        return aVarArr[0];
    }

    public final boolean d() {
        if (this.f13389b == null || this.f13390c == null) {
            return true;
        }
        c6.a[] aVarArr = this.f13391d;
        return aVarArr.length > 0 && aVarArr[0].H();
    }

    public final h e() {
        if (!d()) {
            b bVar = this.f13388a;
            int i10 = bVar == null ? 0 : bVar.f13370f;
            if (i10 != 0 && i10 != 5) {
                c6.a aVarC = c();
                if (!aVarC.E()) {
                    if (bVar == null) {
                        throw new IllegalStateException("Detached points must be in affine coordinates");
                    }
                    c6.a aVarJ = bVar.j(ek.j.b());
                    return f(aVarC.O(aVarJ).B().O(aVarJ));
                }
            }
        }
        return this;
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof h) {
            return a((h) obj);
        }
        return false;
    }

    public final h f(c6.a aVar) {
        c6.a aVarO;
        b bVar = this.f13388a;
        int i10 = bVar == null ? 0 : bVar.f13370f;
        c6.a aVar2 = this.f13390c;
        c6.a aVar3 = this.f13389b;
        if (i10 == 1) {
            aVarO = aVar3.O(aVar);
        } else if (i10 == 2 || i10 == 3 || i10 == 4) {
            c6.a aVarF0 = aVar.f0();
            aVar = aVarF0.O(aVar);
            aVarO = aVar3.O(aVarF0);
        } else {
            if (i10 != 6) {
                throw new IllegalStateException("not a projective coordinate system");
            }
            aVarO = aVar3.O(aVar);
        }
        return bVar.c(aVarO, aVar2.O(aVar));
    }

    public final int hashCode() {
        b bVar = this.f13388a;
        int i10 = bVar == null ? 0 : ~bVar.hashCode();
        if (d()) {
            return i10;
        }
        h hVarE = e();
        return (i10 ^ (hVarE.f13389b.hashCode() * 17)) ^ (hVarE.b().hashCode() * TsExtractor.TS_STREAM_TYPE_AIT);
    }

    public final String toString() {
        if (d()) {
            return "INF";
        }
        StringBuilder sb2 = new StringBuilder("(");
        sb2.append(this.f13389b);
        sb2.append(',');
        sb2.append(this.f13390c);
        int i10 = 0;
        while (true) {
            c6.a[] aVarArr = this.f13391d;
            if (i10 >= aVarArr.length) {
                sb2.append(')');
                return sb2.toString();
            }
            sb2.append(',');
            sb2.append(aVarArr[i10]);
            i10++;
        }
    }

    public h(b bVar, c6.a aVar, c6.a aVar2, c6.a[] aVarArr) {
        this.f13388a = bVar;
        this.f13389b = aVar;
        this.f13390c = aVar2;
        this.f13391d = aVarArr;
    }
}
