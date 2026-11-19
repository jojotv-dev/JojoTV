package androidx.media3.extractor.text;

import androidx.media3.common.text.Cue;
import androidx.media3.common.util.UnstableApi;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public interface Subtitle {
    List<Cue> getCues(long j10);

    long getEventTime(int i10);

    int getEventTimeCount();

    int getNextEventTimeIndex(long j10);
}
