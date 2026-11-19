package org.bouncycastle.crypto.digests;

import kotlin.jvm.internal.ByteCompanionObject;

/* JADX INFO: loaded from: classes5.dex */
public abstract class d implements ek.o, rn.e {

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public static final long[] f43985p = {4794697086780616226L, 8158064640168781261L, -5349999486874862801L, -1606136188198331460L, 4131703408338449720L, 6480981068601479193L, -7908458776815382629L, -6116909921290321640L, -2880145864133508542L, 1334009975649890238L, 2608012711638119052L, 6128411473006802146L, 8268148722764581231L, -9160688886553864527L, -7215885187991268811L, -4495734319001033068L, -1973867731355612462L, -1171420211273849373L, 1135362057144423861L, 2597628984639134821L, 3308224258029322869L, 5365058923640841347L, 6679025012923562964L, 8573033837759648693L, -7476448914759557205L, -6327057829258317296L, -5763719355590565569L, -4658551843659510044L, -4116276920077217854L, -3051310485924567259L, 489312712824947311L, 1452737877330783856L, 2861767655752347644L, 3322285676063803686L, 5560940570517711597L, 5996557281743188959L, 7280758554555802590L, 8532644243296465576L, -9096487096722542874L, -7894198246740708037L, -6719396339535248540L, -6333637450476146687L, -4446306890439682159L, -4076793802049405392L, -3345356375505022440L, -2983346525034927856L, -860691631967231958L, 1182934255886127544L, 1847814050463011016L, 2177327727835720531L, 2830643537854262169L, 3796741975233480872L, 4115178125766777443L, 5681478168544905931L, 6601373596472566643L, 7507060721942968483L, 8399075790359081724L, 8693463985226723168L, -8878714635349349518L, -8302665154208450068L, -8016688836872298968L, -6606660893046293015L, -4685533653050689259L, -4147400797238176981L, -3880063495543823972L, -3348786107499101689L, -1523767162380948706L, -757361751448694408L, 500013540394364858L, 748580250866718886L, 1242879168328830382L, 1977374033974150939L, 2944078676154940804L, 3659926193048069267L, 4368137639120453308L, 4836135668995329356L, 5532061633213252278L, 6448918945643986474L, 6902733635092675308L, 7801388544844847127L};

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ek.f f43986a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final byte[] f43987b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f43988c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public long f43989d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public long f43990e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public long f43991f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public long f43992g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public long f43993h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public long f43994i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public long f43995j;
    public long k;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public long f43996l;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public long f43997m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final long[] f43998n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public int f43999o;

    public d(ek.f fVar) {
        this.f43987b = new byte[8];
        this.f43998n = new long[80];
        this.f43986a = fVar;
        this.f43988c = 0;
        reset();
    }

    public static long a(long j10, long j11, long j12) {
        return ((~j10) & j12) ^ (j11 & j10);
    }

    public static long b(long j10, long j11, long j12) {
        return ((j10 & j12) ^ (j10 & j11)) ^ (j11 & j12);
    }

    public static long c(long j10) {
        return ((j10 >>> 39) | (j10 << 25)) ^ (((j10 << 36) | (j10 >>> 28)) ^ ((j10 << 30) | (j10 >>> 34)));
    }

    public static long d(long j10) {
        return ((j10 >>> 41) | (j10 << 23)) ^ (((j10 << 50) | (j10 >>> 14)) ^ ((j10 << 46) | (j10 >>> 18)));
    }

    public final void e(d dVar) {
        byte[] bArr = dVar.f43987b;
        System.arraycopy(bArr, 0, this.f43987b, 0, bArr.length);
        this.f43988c = dVar.f43988c;
        this.f43989d = dVar.f43989d;
        this.f43990e = dVar.f43990e;
        this.f43991f = dVar.f43991f;
        this.f43992g = dVar.f43992g;
        this.f43993h = dVar.f43993h;
        this.f43994i = dVar.f43994i;
        this.f43995j = dVar.f43995j;
        this.k = dVar.k;
        this.f43996l = dVar.f43996l;
        this.f43997m = dVar.f43997m;
        long[] jArr = dVar.f43998n;
        System.arraycopy(jArr, 0, this.f43998n, 0, jArr.length);
        this.f43999o = dVar.f43999o;
    }

    public final void f() {
        long j10 = this.f43989d;
        if (j10 > 2305843009213693951L) {
            this.f43990e += j10 >>> 61;
            this.f43989d = j10 & 2305843009213693951L;
        }
        long j11 = this.f43989d << 3;
        long j12 = this.f43990e;
        byte b5 = ByteCompanionObject.MIN_VALUE;
        while (true) {
            update(b5);
            if (this.f43988c == 0) {
                break;
            } else {
                b5 = 0;
            }
        }
        if (this.f43999o > 14) {
            g();
        }
        long[] jArr = this.f43998n;
        jArr[14] = j12;
        jArr[15] = j11;
        g();
    }

    public final void g() {
        long[] jArr;
        long j10 = this.f43989d;
        if (j10 > 2305843009213693951L) {
            this.f43990e += j10 >>> 61;
            this.f43989d = j10 & 2305843009213693951L;
        }
        int i10 = 16;
        while (true) {
            jArr = this.f43998n;
            if (i10 > 79) {
                break;
            }
            long j11 = jArr[i10 - 2];
            long j12 = ((j11 >>> 6) ^ (((j11 << 45) | (j11 >>> 19)) ^ ((j11 << 3) | (j11 >>> 61)))) + jArr[i10 - 7];
            long j13 = jArr[i10 - 15];
            jArr[i10] = j12 + ((((j13 >>> 8) | (j13 << 56)) ^ ((j13 << 63) | (j13 >>> 1))) ^ (j13 >>> 7)) + jArr[i10 - 16];
            i10++;
        }
        long j14 = this.f43991f;
        long j15 = this.f43992g;
        long j16 = this.f43993h;
        long j17 = this.f43994i;
        long j18 = this.f43995j;
        long j19 = this.k;
        long j20 = this.f43996l;
        long j21 = j14;
        long j22 = j17;
        long j23 = j15;
        long j24 = j18;
        long j25 = j16;
        long j26 = j19;
        long j27 = this.f43997m;
        int i11 = 0;
        int i12 = 0;
        while (i11 < 10) {
            long jD = d(j24);
            long jA = a(j24, j26, j20);
            long j28 = j24;
            long j29 = j20;
            long j30 = j26;
            long[] jArr2 = f43985p;
            int i13 = i12 + 1;
            long j31 = jD + jA + jArr2[i12] + jArr[i12] + j27;
            long j32 = j22 + j31;
            long j33 = j23;
            long j34 = j25;
            long jC = c(j21) + b(j21, j33, j34) + j31;
            long jD2 = d(j32) + a(j32, j28, j30) + jArr2[i13];
            int i14 = i12 + 2;
            long j35 = jD2 + jArr[i13] + j29;
            long j36 = j34 + j35;
            long j37 = j21;
            long jC2 = c(jC) + b(jC, j37, j33) + j35;
            int i15 = i12 + 3;
            long jD3 = d(j36) + a(j36, j32, j28) + jArr2[i14] + jArr[i14] + j30;
            long j38 = j33 + jD3;
            long jC3 = c(jC2) + b(jC2, jC, j37) + jD3;
            int i16 = i12 + 4;
            long jD4 = d(j38) + a(j38, j36, j32) + jArr2[i15] + jArr[i15] + j28;
            long j39 = j37 + jD4;
            long jC4 = c(jC3) + b(jC3, jC2, jC) + jD4;
            int i17 = i12 + 5;
            long jD5 = d(j39) + a(j39, j38, j36) + jArr2[i16] + jArr[i16] + j32;
            long j40 = jC + jD5;
            long jC5 = c(jC4) + b(jC4, jC3, jC2) + jD5;
            int i18 = i12 + 6;
            long jD6 = d(j40) + a(j40, j39, j38) + jArr2[i17] + jArr[i17] + j36;
            long j41 = jC2 + jD6;
            long jC6 = c(jC5) + b(jC5, jC4, jC3) + jD6;
            int i19 = i12 + 7;
            long jD7 = d(j41) + a(j41, j40, j39) + jArr2[i18] + jArr[i18] + j38;
            long j42 = jC3 + jD7;
            long jC7 = c(jC6) + b(jC6, jC5, jC4) + jD7;
            i12 += 8;
            long jD8 = d(j42) + a(j42, j41, j40) + jArr2[i19] + jArr[i19] + j39;
            long j43 = jC4 + jD8;
            long jC8 = jD8 + c(jC7) + b(jC7, jC6, jC5);
            i11++;
            j26 = j42;
            j25 = jC6;
            j24 = j43;
            j27 = j40;
            j23 = jC7;
            j22 = jC5;
            j20 = j41;
            j21 = jC8;
        }
        this.f43991f += j21;
        this.f43992g += j23;
        this.f43993h += j25;
        this.f43994i += j22;
        this.f43995j += j24;
        this.k += j26;
        this.f43996l += j20;
        this.f43997m += j27;
        this.f43999o = 0;
        for (int i20 = 0; i20 < 16; i20++) {
            jArr[i20] = 0;
        }
    }

    @Override // ek.o
    public final int getByteLength() {
        return 128;
    }

    @Override // ek.n
    public void reset() {
        this.f43989d = 0L;
        this.f43990e = 0L;
        int i10 = 0;
        this.f43988c = 0;
        int i11 = 0;
        while (true) {
            byte[] bArr = this.f43987b;
            if (i11 >= bArr.length) {
                break;
            }
            bArr[i11] = 0;
            i11++;
        }
        this.f43999o = 0;
        while (true) {
            long[] jArr = this.f43998n;
            if (i10 == jArr.length) {
                return;
            }
            jArr[i10] = 0;
            i10++;
        }
    }

    @Override // ek.n
    public final void update(byte b5) {
        int i10 = this.f43988c;
        int i11 = i10 + 1;
        this.f43988c = i11;
        byte[] bArr = this.f43987b;
        bArr[i10] = b5;
        if (i11 == bArr.length) {
            this.f43998n[this.f43999o] = (((long) rn.d.a(4, bArr)) & 4294967295L) | ((((long) rn.d.a(0, bArr)) & 4294967295L) << 32);
            int i12 = this.f43999o + 1;
            this.f43999o = i12;
            if (i12 == 16) {
                g();
            }
            this.f43988c = 0;
        }
        this.f43989d++;
    }

    public d(d dVar) {
        this.f43987b = new byte[8];
        this.f43998n = new long[80];
        this.f43986a = dVar.f43986a;
        e(dVar);
    }

    @Override // ek.n
    public final void update(byte[] bArr, int i10, int i11) {
        while (this.f43988c != 0 && i11 > 0) {
            update(bArr[i10]);
            i10++;
            i11--;
        }
        while (true) {
            byte[] bArr2 = this.f43987b;
            if (i11 < bArr2.length) {
                break;
            }
            this.f43998n[this.f43999o] = (((long) rn.d.a(i10 + 4, bArr)) & 4294967295L) | ((((long) rn.d.a(i10, bArr)) & 4294967295L) << 32);
            int i12 = this.f43999o + 1;
            this.f43999o = i12;
            if (i12 == 16) {
                g();
            }
            i10 += bArr2.length;
            i11 -= bArr2.length;
            this.f43989d += (long) bArr2.length;
        }
        while (i11 > 0) {
            update(bArr[i10]);
            i10++;
            i11--;
        }
    }
}
