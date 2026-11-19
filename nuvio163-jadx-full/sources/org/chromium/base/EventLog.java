package org.chromium.base;

import org.chromium.base.annotations.CalledByNative;

/* JADX INFO: loaded from: classes5.dex */
public class EventLog {
    @CalledByNative
    public static void writeEvent(int i10, int i11) {
        android.util.EventLog.writeEvent(i10, i11);
    }
}
