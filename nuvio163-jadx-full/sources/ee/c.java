package ee;

import java.net.ServerSocket;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f15417a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ServerSocket f15418b;

    public c(String url, ServerSocket socket) {
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(socket, "socket");
        this.f15417a = url;
        this.f15418b = socket;
    }
}
