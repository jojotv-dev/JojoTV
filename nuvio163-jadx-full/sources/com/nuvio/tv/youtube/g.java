package com.nuvio.tv.youtube;

import java.net.ServerSocket;
import java.net.Socket;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class g implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f10432b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Object f10433c;

    public /* synthetic */ g(Object obj, int i10) {
        this.f10432b = i10;
        this.f10433c = obj;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f10432b) {
            case 0:
                YouTubeAccountViewModel.refreshAccountInfo$lambda$0((YouTubeAccountViewModel) this.f10433c);
                break;
            case 1:
                YouTubeMpdServer.ensureStarted$lambda$2((ServerSocket) this.f10433c);
                break;
            default:
                YouTubeMpdServer.ensureStarted$lambda$2$1((Socket) this.f10433c);
                break;
        }
    }
}
