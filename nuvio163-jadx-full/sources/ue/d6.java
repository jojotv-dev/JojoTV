package ue;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class d6 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f58630a;

    static {
        int[] iArr = new int[androidx.lifecycle.o.values().length];
        try {
            iArr[androidx.lifecycle.o.ON_RESUME.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[androidx.lifecycle.o.ON_PAUSE.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[androidx.lifecycle.o.ON_STOP.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f58630a = iArr;
    }
}
