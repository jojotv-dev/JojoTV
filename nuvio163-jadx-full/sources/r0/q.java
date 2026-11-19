package r0;

import android.app.ActivityOptions;
import android.app.PendingIntent;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public abstract class q {
    public static void a(PendingIntent pendingIntent) {
        try {
            pendingIntent.send(ActivityOptions.makeBasic().setPendingIntentBackgroundActivityStartMode(1).toBundle());
        } catch (PendingIntent.CanceledException e10) {
            Log.e("TextClassification", "error sending pendingIntent: " + pendingIntent + " error: " + e10);
        }
    }
}
