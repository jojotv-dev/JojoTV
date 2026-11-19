package io.github.jan.supabase.storage;

import android.content.Context;
import androidx.media3.container.NalUnitUtil;
import kotlin.Metadata;

/* JADX INFO: loaded from: classes5.dex */
@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u001a\b\u0010\u0002\u001a\u00020\u0001H\u0000\"\u0010\u0010\u0000\u001a\u0004\u0018\u00010\u0001X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0003"}, d2 = {"appContext", "Landroid/content/Context;", "applicationContext", "storage-kt_release"}, k = 2, mv = {2, 2, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final class ContextKt {
    private static Context appContext;

    public static final Context applicationContext() {
        Context context = appContext;
        if (context != null) {
            return context;
        }
        throw new IllegalStateException("Application context not initialized");
    }
}
