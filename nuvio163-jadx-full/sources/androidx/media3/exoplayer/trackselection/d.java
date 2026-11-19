package androidx.media3.exoplayer.trackselection;

import androidx.media3.common.Format;
import java.util.Comparator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class d implements Comparator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final /* synthetic */ int f3340b;

    public /* synthetic */ d(int i10) {
        this.f3340b = i10;
    }

    @Override // java.util.Comparator
    public final int compare(Object obj, Object obj2) {
        switch (this.f3340b) {
            case 0:
                return DefaultTrackSelector.VideoTrackInfo.compareSelections((List) obj, (List) obj2);
            case 1:
                return DefaultTrackSelector.AudioTrackInfo.compareSelections((List) obj, (List) obj2);
            case 2:
                return DefaultTrackSelector.TextTrackInfo.compareSelections((List) obj, (List) obj2);
            case 3:
                return BaseTrackSelection.lambda$new$0((Format) obj, (Format) obj2);
            default:
                return DefaultTrackSelector.lambda$static$0((Integer) obj, (Integer) obj2);
        }
    }
}
