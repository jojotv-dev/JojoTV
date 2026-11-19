package l8;

/* JADX INFO: loaded from: classes4.dex */
public final class c extends RuntimeException {
    public c(Exception exc, String str, Object... objArr) {
        super(String.format(str, objArr), exc);
    }

    public c(String str, Object... objArr) {
        super(String.format(str, objArr));
    }
}
