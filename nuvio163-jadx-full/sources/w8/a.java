package w8;

import androidx.media3.session.legacy.PlaybackStateCompat;
import k9.c;

/* JADX INFO: loaded from: classes4.dex */
public enum a implements c {
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_ARCHIVE(32),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_COMPRESSED(PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH),
    FILE_ATTRIBUTE_DIRECTORY(16),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_ENCRYPTED(16384),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_HIDDEN(2),
    FILE_ATTRIBUTE_NORMAL(128),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_NOT_CONTENT_INDEXED(PlaybackStateCompat.ACTION_PLAY_FROM_URI),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_OFFLINE(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_READONLY(1),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_REPARSE_POINT(1024),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_SPARSE_FILE(512),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_SYSTEM(4),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_TEMPORARY(256),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_INTEGRITY_STREAM(PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ATTRIBUTE_NO_SCRUB_DATA(PlaybackStateCompat.ACTION_PREPARE_FROM_URI);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f62161b;

    a(long j10) {
        this.f62161b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f62161b;
    }
}
