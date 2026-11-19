package androidx.core.app;

import android.app.Notification;
import android.graphics.drawable.Icon;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
public abstract class u {
    public static Parcelable a(Icon icon) {
        return icon;
    }

    public static void b(Notification.Builder builder, Icon icon) {
        builder.setLargeIcon(icon);
    }
}
