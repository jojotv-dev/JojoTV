package fe;

import android.util.JsonToken;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class h4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f16939a;

    static {
        int[] iArr = new int[JsonToken.values().length];
        try {
            iArr[JsonToken.STRING.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[JsonToken.NUMBER.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[JsonToken.BOOLEAN.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f16939a = iArr;
    }
}
