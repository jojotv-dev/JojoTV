package fe;

import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class z6 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f17924a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f17925b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f17926c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final String f17927d;

    public z6(String key, String label, String url, String from) {
        Intrinsics.checkNotNullParameter(key, "key");
        Intrinsics.checkNotNullParameter(label, "label");
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(from, "from");
        this.f17924a = key;
        this.f17925b = label;
        this.f17926c = url;
        this.f17927d = from;
    }
}
