package re;

import android.util.JsonToken;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class b0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f52815a;

    static {
        int[] iArr = new int[u1.values().length];
        try {
            iArr[1] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            u1 u1Var = u1.f53280b;
            iArr[2] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            u1 u1Var2 = u1.f53280b;
            iArr[0] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        int[] iArr2 = new int[JsonToken.values().length];
        try {
            iArr2[JsonToken.BEGIN_OBJECT.ordinal()] = 1;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[JsonToken.BEGIN_ARRAY.ordinal()] = 2;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr2[JsonToken.STRING.ordinal()] = 3;
        } catch (NoSuchFieldError unused6) {
        }
        try {
            iArr2[JsonToken.NUMBER.ordinal()] = 4;
        } catch (NoSuchFieldError unused7) {
        }
        try {
            iArr2[JsonToken.BOOLEAN.ordinal()] = 5;
        } catch (NoSuchFieldError unused8) {
        }
        f52815a = iArr2;
    }
}
