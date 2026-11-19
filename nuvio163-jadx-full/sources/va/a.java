package va;

import com.liskovsoft.googleapi.service.YouTubeDataService;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class a implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f60717b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ String[] f60718c;

    public /* synthetic */ a(String[] strArr, int i10) {
        this.f60717b = i10;
        this.f60718c = strArr;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f60717b) {
            case 0:
                return YouTubeDataService.getPlaylistMetadata$lambda$0(this.f60718c);
            case 1:
                return YouTubeDataService.getChannelMetadata$lambda$0(this.f60718c);
            default:
                return YouTubeDataService.getVideoMetadata$lambda$0(this.f60718c);
        }
    }
}
