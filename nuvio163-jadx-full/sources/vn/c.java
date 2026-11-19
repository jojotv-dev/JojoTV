package vn;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;

/* JADX INFO: loaded from: classes5.dex */
public abstract class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final SharedPreferences f61118a;

    static {
        StrictMode.ThreadPolicy threadPolicyAllowThreadDiskWrites = StrictMode.allowThreadDiskWrites();
        try {
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(pa.a.f44383e);
            if (threadPolicyAllowThreadDiskWrites != null) {
                StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
            }
            f61118a = defaultSharedPreferences;
        } catch (Throwable th2) {
            if (threadPolicyAllowThreadDiskWrites != null) {
                try {
                    StrictMode.setThreadPolicy(threadPolicyAllowThreadDiskWrites);
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
            }
            throw th2;
        }
    }
}
