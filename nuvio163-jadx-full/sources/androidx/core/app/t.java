package androidx.core.app;

import android.app.Notification;

/* JADX INFO: loaded from: classes3.dex */
public abstract class t {
    public static Notification.Builder a(Notification.Builder builder, String str) {
        return builder.addPerson(str);
    }

    public static Notification.Builder b(Notification.Builder builder, String str) {
        return builder.setCategory(str);
    }
}
