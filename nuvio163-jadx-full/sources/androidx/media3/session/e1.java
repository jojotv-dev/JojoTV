package androidx.media3.session;

import androidx.media3.session.legacy.MediaSessionManager;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes3.dex */
public final /* synthetic */ class e1 implements Runnable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3500b = 0;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f3501c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final /* synthetic */ Object f3502e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final /* synthetic */ Object f3503i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final /* synthetic */ Object f3504m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final /* synthetic */ Object f3505n;

    public /* synthetic */ e1(MediaControllerImplLegacy mediaControllerImplLegacy, AtomicInteger atomicInteger, List list, ArrayList arrayList, int i10) {
        this.f3502e = mediaControllerImplLegacy;
        this.f3503i = atomicInteger;
        this.f3504m = list;
        this.f3505n = arrayList;
        this.f3501c = i10;
    }

    @Override // java.lang.Runnable
    public final void run() {
        switch (this.f3500b) {
            case 0:
                ((MediaControllerImplLegacy) this.f3502e).lambda$addQueueItems$4((AtomicInteger) this.f3503i, (List) this.f3504m, (ArrayList) this.f3505n, this.f3501c);
                break;
            default:
                ((MediaSessionLegacyStub) this.f3502e).lambda$dispatchSessionTaskWithSessionCommandInternal$22((SessionCommand) this.f3503i, this.f3501c, (MediaSessionManager.RemoteUserInfo) this.f3504m, (MediaSessionLegacyStub.SessionTask) this.f3505n);
                break;
        }
    }

    public /* synthetic */ e1(MediaSessionLegacyStub mediaSessionLegacyStub, SessionCommand sessionCommand, int i10, MediaSessionManager.RemoteUserInfo remoteUserInfo, MediaSessionLegacyStub.SessionTask sessionTask) {
        this.f3502e = mediaSessionLegacyStub;
        this.f3503i = sessionCommand;
        this.f3501c = i10;
        this.f3504m = remoteUserInfo;
        this.f3505n = sessionTask;
    }
}
