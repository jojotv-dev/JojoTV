package xp;

import okhttp3.Request;

/* JADX INFO: loaded from: classes3.dex */
public interface f<T> extends Cloneable {
    void H(i iVar);

    void cancel();

    /* JADX INFO: renamed from: clone */
    f mo2053clone();

    u0 execute();

    boolean isCanceled();

    Request request();
}
