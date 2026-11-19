package u8;

import androidx.media3.session.legacy.PlaybackStateCompat;
import k9.c;

/* JADX INFO: loaded from: classes4.dex */
public enum a implements c {
    FILE_READ_DATA(1),
    FILE_WRITE_DATA(2),
    FILE_APPEND_DATA(4),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_EXECUTE(32),
    FILE_LIST_DIRECTORY(1),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ADD_FILE(2),
    FILE_ADD_SUBDIRECTORY(4),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_TRAVERSE(32),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_DELETE_CHILD(64),
    FILE_READ_ATTRIBUTES(128),
    FILE_WRITE_ATTRIBUTES(256),
    FILE_READ_EA(8),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_WRITE_EA(16),
    DELETE(PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH),
    /* JADX INFO: Fake field, exist only in values array */
    READ_CONTROL(PlaybackStateCompat.ACTION_PREPARE_FROM_URI),
    /* JADX INFO: Fake field, exist only in values array */
    GENERIC_EXECUTE(PlaybackStateCompat.ACTION_SET_REPEAT_MODE),
    /* JADX INFO: Fake field, exist only in values array */
    GENERIC_ALL(PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED),
    /* JADX INFO: Fake field, exist only in values array */
    GENERIC_EXECUTE(1048576),
    /* JADX INFO: Fake field, exist only in values array */
    GENERIC_ALL(16777216),
    /* JADX INFO: Fake field, exist only in values array */
    GENERIC_EXECUTE(33554432),
    /* JADX INFO: Fake field, exist only in values array */
    GENERIC_ALL(268435456),
    /* JADX INFO: Fake field, exist only in values array */
    GENERIC_EXECUTE(536870912),
    GENERIC_WRITE(1073741824),
    /* JADX INFO: Fake field, exist only in values array */
    GENERIC_READ(2147483648L),
    /* JADX INFO: Fake field, exist only in values array */
    ADS_RIGHT_DS_CONTROL_ACCESS(256),
    /* JADX INFO: Fake field, exist only in values array */
    ADS_RIGHT_DS_CREATE_CHILD(1),
    /* JADX INFO: Fake field, exist only in values array */
    ADS_RIGHT_DS_DELETE_CHILD(2),
    /* JADX INFO: Fake field, exist only in values array */
    ADS_RIGHT_DS_READ_PROP(16),
    /* JADX INFO: Fake field, exist only in values array */
    ADS_RIGHT_DS_WRITE_PROP(32),
    /* JADX INFO: Fake field, exist only in values array */
    ADS_RIGHT_DS_SELF(8);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f58301b;

    a(long j10) {
        this.f58301b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f58301b;
    }
}
