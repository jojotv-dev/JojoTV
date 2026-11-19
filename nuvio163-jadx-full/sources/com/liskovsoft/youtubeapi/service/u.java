package com.liskovsoft.youtubeapi.service;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class u implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f7797b;

    public /* synthetic */ u(int i10) {
        this.f7797b = i10;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f7797b) {
            case 0:
                return YouTubeNotificationsService.getNotificationItemsObserve$lambda$0();
            case 1:
                return InetAddress.getLocalHost();
            case 2:
                return null;
            case 3:
                return new ArrayList();
            default:
                return io.sentry.android.core.internal.util.f.f24785c.a();
        }
    }
}
