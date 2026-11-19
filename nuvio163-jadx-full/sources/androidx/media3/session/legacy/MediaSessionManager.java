package androidx.media3.session.legacy;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import androidx.media3.common.util.UnstableApi;
import io.ktor.sse.ServerSentEventKt;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class MediaSessionManager {
    private static volatile MediaSessionManager sessionManager;
    MediaSessionManagerImpl impl;
    static final String TAG = "MediaSessionManager";
    static final boolean DEBUG = Log.isLoggable(TAG, 3);
    private static final Object lock = new Object();

    public static class MediaSessionManagerImpl {
        private static final boolean DEBUG = MediaSessionManager.DEBUG;
        private static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
        private static final String PERMISSION_MEDIA_CONTENT_CONTROL = "android.permission.MEDIA_CONTENT_CONTROL";
        private static final String PERMISSION_STATUS_BAR_SERVICE = "android.permission.STATUS_BAR_SERVICE";
        private static final String TAG = "MediaSessionManager";
        ContentResolver contentResolver;
        Context context;

        public MediaSessionManagerImpl(Context context) {
            this.context = context;
            this.contentResolver = context.getContentResolver();
        }

        private boolean hasMediaControlPermission(RemoteUserInfoImpl remoteUserInfoImpl) {
            return this.context.checkPermission(PERMISSION_MEDIA_CONTENT_CONTROL, remoteUserInfoImpl.getPid(), remoteUserInfoImpl.getUid()) == 0;
        }

        private boolean isPermissionGranted(RemoteUserInfoImpl remoteUserInfoImpl, String str) {
            if (remoteUserInfoImpl.getPid() < 0) {
                return this.context.getPackageManager().checkPermission(str, remoteUserInfoImpl.getPackageName()) == 0;
            }
            return this.context.checkPermission(str, remoteUserInfoImpl.getPid(), remoteUserInfoImpl.getUid()) == 0;
        }

        public boolean isEnabledNotificationListener(RemoteUserInfoImpl remoteUserInfoImpl) {
            String string = Settings.Secure.getString(this.contentResolver, ENABLED_NOTIFICATION_LISTENERS);
            if (string != null) {
                for (String str : string.split(ServerSentEventKt.COLON)) {
                    ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(str);
                    if (componentNameUnflattenFromString != null && componentNameUnflattenFromString.getPackageName().equals(remoteUserInfoImpl.getPackageName())) {
                        return true;
                    }
                }
            }
            return false;
        }

        public boolean isTrustedForMediaControl(RemoteUserInfoImpl remoteUserInfoImpl) {
            if (hasMediaControlPermission(remoteUserInfoImpl)) {
                return true;
            }
            try {
                if (this.context.getPackageManager().getApplicationInfo(remoteUserInfoImpl.getPackageName(), 0) == null) {
                    return false;
                }
                return isPermissionGranted(remoteUserInfoImpl, PERMISSION_STATUS_BAR_SERVICE) || isPermissionGranted(remoteUserInfoImpl, PERMISSION_MEDIA_CONTENT_CONTROL) || remoteUserInfoImpl.getUid() == 1000 || isEnabledNotificationListener(remoteUserInfoImpl);
            } catch (PackageManager.NameNotFoundException unused) {
                if (DEBUG) {
                    Log.d(TAG, "Package " + remoteUserInfoImpl.getPackageName() + " doesn't exist");
                }
                return false;
            }
        }
    }

    /* JADX INFO: loaded from: classes3.dex */
    public interface RemoteUserInfoImpl {
        String getPackageName();

        int getPid();

        int getUid();
    }

    public static final class RemoteUserInfoImplApi28 extends RemoteUserInfoImplBase {
        public RemoteUserInfoImplApi28(String str, int i10, int i11) {
            super(str, i10, i11);
        }

        public static String getPackageName(android.media.session.MediaSessionManager.RemoteUserInfo remoteUserInfo) {
            return remoteUserInfo.getPackageName();
        }

        public RemoteUserInfoImplApi28(android.media.session.MediaSessionManager.RemoteUserInfo remoteUserInfo) {
            super(remoteUserInfo.getPackageName(), remoteUserInfo.getPid(), remoteUserInfo.getUid());
        }
    }

    public static class RemoteUserInfoImplBase implements RemoteUserInfoImpl {
        private final String packageName;
        private final int pid;
        private final int uid;

        public RemoteUserInfoImplBase(String str, int i10, int i11) {
            this.packageName = str;
            this.pid = i10;
            this.uid = i11;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof RemoteUserInfoImplBase)) {
                return false;
            }
            RemoteUserInfoImplBase remoteUserInfoImplBase = (RemoteUserInfoImplBase) obj;
            if (this.pid < 0 || remoteUserInfoImplBase.pid < 0) {
                return TextUtils.equals(this.packageName, remoteUserInfoImplBase.packageName) && this.uid == remoteUserInfoImplBase.uid;
            }
            return TextUtils.equals(this.packageName, remoteUserInfoImplBase.packageName) && this.pid == remoteUserInfoImplBase.pid && this.uid == remoteUserInfoImplBase.uid;
        }

        @Override // androidx.media3.session.legacy.MediaSessionManager.RemoteUserInfoImpl
        public String getPackageName() {
            return this.packageName;
        }

        @Override // androidx.media3.session.legacy.MediaSessionManager.RemoteUserInfoImpl
        public int getPid() {
            return this.pid;
        }

        @Override // androidx.media3.session.legacy.MediaSessionManager.RemoteUserInfoImpl
        public int getUid() {
            return this.uid;
        }

        public int hashCode() {
            return Objects.hash(this.packageName, Integer.valueOf(this.uid));
        }
    }

    private MediaSessionManager(Context context) {
        this.impl = new MediaSessionManagerImpl(context);
    }

    public static MediaSessionManager getSessionManager(Context context) {
        MediaSessionManager mediaSessionManager;
        synchronized (lock) {
            try {
                if (sessionManager == null) {
                    sessionManager = new MediaSessionManager(context.getApplicationContext());
                }
                mediaSessionManager = sessionManager;
            } catch (Throwable th2) {
                throw th2;
            }
        }
        return mediaSessionManager;
    }

    public boolean isTrustedForMediaControl(RemoteUserInfo remoteUserInfo) {
        return this.impl.isTrustedForMediaControl(remoteUserInfo.impl);
    }

    public static final class RemoteUserInfo {
        public static final String LEGACY_CONTROLLER = "android.media.session.MediaController";
        public static final int UNKNOWN_PID = -1;
        public static final int UNKNOWN_UID = -1;
        RemoteUserInfoImpl impl;

        public RemoteUserInfo(String str, int i10, int i11) {
            if (str == null) {
                throw new NullPointerException("package shouldn't be null");
            }
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("packageName should be nonempty");
            }
            if (Build.VERSION.SDK_INT >= 28) {
                this.impl = new RemoteUserInfoImplApi28(str, i10, i11);
            } else {
                this.impl = new RemoteUserInfoImplBase(str, i10, i11);
            }
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof RemoteUserInfo) {
                return this.impl.equals(((RemoteUserInfo) obj).impl);
            }
            return false;
        }

        public String getPackageName() {
            return this.impl.getPackageName();
        }

        public int getPid() {
            return this.impl.getPid();
        }

        public int getUid() {
            return this.impl.getUid();
        }

        public int hashCode() {
            return this.impl.hashCode();
        }

        public RemoteUserInfo(android.media.session.MediaSessionManager.RemoteUserInfo remoteUserInfo) {
            String packageName = RemoteUserInfoImplApi28.getPackageName(remoteUserInfo);
            if (packageName != null) {
                if (!TextUtils.isEmpty(packageName)) {
                    this.impl = new RemoteUserInfoImplApi28(remoteUserInfo);
                    return;
                }
                throw new IllegalArgumentException("packageName should be nonempty");
            }
            throw new NullPointerException("package shouldn't be null");
        }
    }
}
