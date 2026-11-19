package androidx.core.app;

import android.app.Notification;
import android.app.Person;

/* JADX INFO: loaded from: classes3.dex */
public abstract class c0 {
    public static Notification.MessagingStyle a(Person person) {
        return new Notification.MessagingStyle(person);
    }

    public static Notification.MessagingStyle b(Notification.MessagingStyle messagingStyle, boolean z2) {
        return messagingStyle.setGroupConversation(z2);
    }
}
