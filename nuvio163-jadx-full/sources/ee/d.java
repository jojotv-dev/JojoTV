package ee;

import java.net.ServerSocket;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f15419a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final ServerSocket f15420b;

    public d(String url, ServerSocket socket) {
        Intrinsics.checkNotNullParameter(url, "url");
        Intrinsics.checkNotNullParameter(socket, "socket");
        this.f15419a = url;
        this.f15420b = socket;
    }
}
