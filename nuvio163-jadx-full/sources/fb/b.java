package fb;

import android.net.Uri;
import com.liskovsoft.googleapi.drive3.DriveServiceInt;
import com.liskovsoft.youtubeapi.channelgroups.ChannelGroupServiceImpl;
import java.util.concurrent.Callable;

/* JADX INFO: loaded from: classes4.dex */
public final /* synthetic */ class b implements Callable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f16240b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ Uri f16241c;

    public /* synthetic */ b(Uri uri, int i10) {
        this.f16240b = i10;
        this.f16241c = uri;
    }

    @Override // java.util.concurrent.Callable
    public final Object call() {
        switch (this.f16240b) {
            case 0:
                return ChannelGroupServiceImpl.importGroupsObserve$lambda$0(this.f16241c);
            case 1:
                return DriveServiceInt.getFile(this.f16241c);
            case 2:
                return DriveServiceInt.getFolderList(this.f16241c);
            default:
                return DriveServiceInt.getFileList(this.f16241c);
        }
    }
}
