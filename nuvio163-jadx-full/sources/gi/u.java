package gi;

import bd.e0;
import io.ktor.util.date.GMTDateParser;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes3.dex */
public abstract class u {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final char[] f20227a = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', GMTDateParser.DAY_OF_MONTH, 'e', 'f'};

    public static final void a(long j10, long j11, long j12) {
        if (j11 >= 0 && j12 <= j10) {
            if (j11 > j12) {
                throw new IllegalArgumentException(e0.j(o2.a.x(j11, "startIndex (", ") > endIndex ("), j12, ')'));
            }
            return;
        }
        StringBuilder sbX = o2.a.x(j11, "startIndex (", ") and endIndex (");
        sbX.append(j12);
        sbX.append(") are not within the range [0..size(");
        sbX.append(j10);
        sbX.append("))");
        throw new IndexOutOfBoundsException(sbX.toString());
    }

    public static final void b(long j10, long j11, long j12) {
        if (j11 < 0 || j11 > j10 || j10 - j11 < j12 || j12 < 0) {
            StringBuilder sbX = o2.a.x(j11, "offset (", ") and byteCount (");
            sbX.append(j12);
            sbX.append(") are not within the range [0..size(");
            sbX.append(j10);
            sbX.append("))");
            throw new IllegalArgumentException(sbX.toString());
        }
    }

    public static final int c(l lVar, byte b5, int i10, int i11) {
        Intrinsics.checkNotNullParameter(lVar, "<this>");
        if (i10 < 0 || i10 >= lVar.b()) {
            throw new IllegalArgumentException(String.valueOf(i10).toString());
        }
        if (i10 > i11 || i11 > lVar.b()) {
            throw new IllegalArgumentException(String.valueOf(i11).toString());
        }
        int i12 = lVar.f20211b;
        byte[] bArr = lVar.f20210a;
        while (i10 < i11) {
            if (bArr[i12 + i10] == b5) {
                return i10;
            }
            i10++;
        }
        return -1;
    }

    public static final boolean d(l lVar) {
        Intrinsics.checkNotNullParameter(lVar, "<this>");
        return lVar.b() == 0;
    }
}
