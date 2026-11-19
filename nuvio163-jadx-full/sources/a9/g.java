package a9;

import androidx.media3.session.legacy.PlaybackStateCompat;

/* JADX INFO: loaded from: classes4.dex */
public enum g implements k9.c {
    FILE_DIRECTORY_FILE(1),
    FILE_WRITE_THROUGH(2),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_SEQUENTIAL_ONLY(4),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NO_INTERMEDIATE_BUFFERING(8),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_SYNCHRONOUS_IO_ALERT(16),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_SYNCHRONOUS_IO_NONALERT(32),
    FILE_NON_DIRECTORY_FILE(64),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_COMPLETE_IF_OPLOCKED(256),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NO_EA_KNOWLEDGE(512),
    FILE_RANDOM_ACCESS(PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_DELETE_ON_CLOSE(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_OPEN_BY_FILE_ID(PlaybackStateCompat.ACTION_PLAY_FROM_URI),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_OPEN_FOR_BACKUP_INTENT(16384),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_NO_COMPRESSION(PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_OPEN_REMOTE_INSTANCE(1024),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_OPEN_REQUIRING_OPLOCK(PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_DISALLOW_EXCLUSIVE(PlaybackStateCompat.ACTION_PREPARE_FROM_URI),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_RESERVE_OPFILTER(1048576),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_OPEN_REPARSE_POINT(PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_OPEN_NO_RECALL(PlaybackStateCompat.ACTION_SET_PLAYBACK_SPEED),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_OPEN_FOR_FREE_SPACE_QUERY(8388608);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f646b;

    g(long j10) {
        this.f646b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f646b;
    }
}
