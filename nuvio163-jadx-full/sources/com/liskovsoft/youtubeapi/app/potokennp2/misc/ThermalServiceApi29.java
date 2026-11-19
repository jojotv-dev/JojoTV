package com.liskovsoft.youtubeapi.app.potokennp2.misc;

import android.os.PowerManager;
import androidx.annotation.Keep;
import androidx.media3.container.NalUnitUtil;
import com.liskovsoft.sharedutils.mylogger.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
@Keep
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\bÃ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"}, d2 = {"Lcom/liskovsoft/youtubeapi/app/potokennp2/misc/ThermalServiceApi29;", "", "<init>", "()V", "isAvailable", "", "powerService", "Landroid/os/PowerManager;", "youtubeapi_release"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
final class ThermalServiceApi29 {
    public static final ThermalServiceApi29 INSTANCE = new ThermalServiceApi29();

    private ThermalServiceApi29() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final void isAvailable$lambda$0(int i10) {
    }

    public final boolean isAvailable(PowerManager powerService) {
        Intrinsics.checkNotNullParameter(powerService, "powerService");
        b bVar = new b();
        try {
            powerService.addThermalStatusListener(bVar);
            powerService.removeThermalStatusListener(bVar);
            return true;
        } catch (Exception e10) {
            Log.e("WebViewUtil", "Thermal service check failed", e10);
            return false;
        }
    }
}
