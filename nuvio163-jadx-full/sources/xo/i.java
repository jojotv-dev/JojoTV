package xo;

import up.y;

/* JADX INFO: loaded from: classes5.dex */
public final class i {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f64105a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f64106b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f64107c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public int f64108d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int[] f64109e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f64110f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public int f64111g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public e f64112h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public i f64113i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public k8.c f64114j;
    public i k;

    public final i a() {
        e eVar = this.f64112h;
        return eVar == null ? this : eVar.f64078a;
    }

    public final void b(int i10, int i11) {
        if (this.f64109e == null) {
            this.f64109e = new int[6];
        }
        int i12 = this.f64108d;
        int[] iArr = this.f64109e;
        if (i12 >= iArr.length) {
            int[] iArr2 = new int[iArr.length + 6];
            System.arraycopy(iArr, 0, iArr2, 0, iArr.length);
            this.f64109e = iArr2;
        }
        int[] iArr3 = this.f64109e;
        int i13 = this.f64108d;
        int i14 = i13 + 1;
        this.f64108d = i14;
        iArr3[i13] = i10;
        this.f64108d = i13 + 2;
        iArr3[i14] = i11;
    }

    public final void c(y yVar, int i10, boolean z2) {
        if ((this.f64105a & 2) != 0) {
            if (z2) {
                yVar.i(this.f64107c - i10);
                return;
            } else {
                yVar.k(this.f64107c - i10);
                return;
            }
        }
        if (z2) {
            b((-1) - i10, yVar.f60314c);
            yVar.i(-1);
        } else {
            b(i10, yVar.f60314c);
            yVar.k(-1);
        }
    }

    public final boolean d(int i10, byte[] bArr) {
        this.f64105a |= 2;
        this.f64107c = i10;
        int i11 = 0;
        boolean z2 = false;
        while (i11 < this.f64108d) {
            int[] iArr = this.f64109e;
            int i12 = i11 + 1;
            int i13 = iArr[i11];
            i11 += 2;
            int i14 = iArr[i12];
            if (i13 >= 0) {
                int i15 = i10 - i13;
                if (i15 < -32768 || i15 > 32767) {
                    int i16 = i14 - 1;
                    int i17 = bArr[i16] & 255;
                    if (i17 <= 168) {
                        bArr[i16] = (byte) (i17 + 49);
                    } else {
                        bArr[i16] = (byte) (i17 + 20);
                    }
                    z2 = true;
                }
                bArr[i14] = (byte) (i15 >>> 8);
                bArr[i14 + 1] = (byte) i15;
            } else {
                int i18 = i13 + i10 + 1;
                bArr[i14] = (byte) (i18 >>> 24);
                bArr[i14 + 1] = (byte) (i18 >>> 16);
                bArr[i14 + 2] = (byte) (i18 >>> 8);
                bArr[i14 + 3] = (byte) i18;
            }
        }
        return z2;
    }

    /* JADX WARN: Code duplicated, block: B:35:0x0084  */
    /* JADX WARN: Code duplicated, block: B:39:0x0092  */
    /* JADX WARN: Code duplicated, block: B:41:0x009a  */
    /* JADX WARN: Code duplicated, block: B:52:0x009d A[SYNTHETIC] */
    /*  JADX ERROR: JadxOverflowException in pass: LoopRegionVisitor
        jadx.core.utils.exceptions.JadxOverflowException: LoopRegionVisitor.assignOnlyInLoop endless recursion
        	at jadx.core.utils.ErrorsCounter.addError(ErrorsCounter.java:59)
        	at jadx.core.utils.ErrorsCounter.error(ErrorsCounter.java:31)
        	at jadx.core.dex.attributes.nodes.NotificationAttrNode.addError(NotificationAttrNode.java:19)
        */
    public final void e(xo.i r8, long r9, int r11) {
        /*
            r7 = this;
            r0 = r7
        L1:
            if (r0 == 0) goto La2
            xo.i r1 = r0.k
            r2 = 0
            r0.k = r2
            if (r8 == 0) goto L50
            int r2 = r0.f64105a
            r3 = r2 & 2048(0x800, float:2.87E-42)
            if (r3 == 0) goto L11
            goto L63
        L11:
            r2 = r2 | 2048(0x800, float:2.87E-42)
            r0.f64105a = r2
            r3 = r2 & 256(0x100, float:3.59E-43)
            if (r3 == 0) goto L80
            r2 = r2 & 1024(0x400, float:1.435E-42)
            if (r2 == 0) goto L37
            int r2 = r8.f64105a
            r2 = r2 & 1024(0x400, float:1.435E-42)
            if (r2 != 0) goto L24
            goto L37
        L24:
            r2 = 0
        L25:
            int[] r3 = r0.f64109e
            int r4 = r3.length
            if (r2 >= r4) goto L37
            r3 = r3[r2]
            int[] r4 = r8.f64109e
            r4 = r4[r2]
            r3 = r3 & r4
            if (r3 == 0) goto L34
            goto L80
        L34:
            int r2 = r2 + 1
            goto L25
        L37:
            k8.c r2 = new k8.c
            r3 = 5
            r2.<init>(r3)
            int r3 = r0.f64110f
            r2.f27980b = r3
            k8.c r3 = r8.f64114j
            java.lang.Object r3 = r3.f27981c
            xo.i r3 = (xo.i) r3
            r2.f27981c = r3
            k8.c r3 = r0.f64114j
            r2.f27982d = r3
            r0.f64114j = r2
            goto L80
        L50:
            int r2 = r0.f64105a
            r3 = r2 & 1024(0x400, float:1.435E-42)
            r4 = 32
            if (r3 == 0) goto L65
            int[] r3 = r0.f64109e
            long r5 = r9 >>> r4
            int r5 = (int) r5
            r3 = r3[r5]
            int r5 = (int) r9
            r3 = r3 & r5
            if (r3 == 0) goto L65
        L63:
            r0 = r1
            goto L1
        L65:
            r3 = r2 & 1024(0x400, float:1.435E-42)
            if (r3 != 0) goto L75
            r2 = r2 | 1024(0x400, float:1.435E-42)
            r0.f64105a = r2
            int r2 = r11 / 32
            int r2 = r2 + 1
            int[] r2 = new int[r2]
            r0.f64109e = r2
        L75:
            int[] r2 = r0.f64109e
            long r3 = r9 >>> r4
            int r3 = (int) r3
            r4 = r2[r3]
            int r5 = (int) r9
            r4 = r4 | r5
            r2[r3] = r4
        L80:
            k8.c r2 = r0.f64114j
        L82:
            if (r2 == 0) goto L63
            int r3 = r0.f64105a
            r3 = r3 & 128(0x80, float:1.8E-43)
            if (r3 == 0) goto L92
            k8.c r3 = r0.f64114j
            java.lang.Object r3 = r3.f27982d
            k8.c r3 = (k8.c) r3
            if (r2 == r3) goto L9d
        L92:
            java.lang.Object r3 = r2.f27981c
            xo.i r3 = (xo.i) r3
            xo.i r4 = r3.k
            if (r4 != 0) goto L9d
            r3.k = r1
            r1 = r3
        L9d:
            java.lang.Object r2 = r2.f27982d
            k8.c r2 = (k8.c) r2
            goto L82
        La2:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: xo.i.e(xo.i, long, int):void");
    }

    public final String toString() {
        StringBuffer stringBuffer = new StringBuffer("L");
        stringBuffer.append(System.identityHashCode(this));
        return stringBuffer.toString();
    }
}
