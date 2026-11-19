package androidx.media3.common.text;

import android.os.Bundle;
import q7.f;

/* JADX INFO: loaded from: classes.dex */
public final /* synthetic */ class a implements f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f2793a;

    public /* synthetic */ a(int i10) {
        this.f2793a = i10;
    }

    @Override // q7.f
    public final Object apply(Object obj) {
        switch (this.f2793a) {
            case 0:
                return CueGroup.lambda$static$0((Cue) obj);
            case 1:
                return Cue.fromBundle((Bundle) obj);
            default:
                return ((Cue) obj).toSerializableBundle();
        }
    }
}
