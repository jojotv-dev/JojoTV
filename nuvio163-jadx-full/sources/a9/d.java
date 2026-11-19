package a9;

import androidx.media3.session.legacy.PlaybackStateCompat;

/* JADX INFO: loaded from: classes4.dex */
public enum d implements k9.c {
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_FILE_NAME(1),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_DIR_NAME(2),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_ATTRIBUTES(4),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_SIZE(8),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_LAST_WRITE(16),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_LAST_ACCESS(32),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_CREATION(64),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_EA(128),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_SECURITY(256),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_STREAM_NAME(512),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_STREAM_SIZE(1024),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NOTIFY_CHANGE_STREAM_WRITE(PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f626b;

    d(long j10) {
        this.f626b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f626b;
    }
}
