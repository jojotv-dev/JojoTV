package a9;

import androidx.media3.session.legacy.PlaybackStateCompat;

/* JADX INFO: loaded from: classes4.dex */
public enum b0 implements k9.c {
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_MANUAL_CACHING(0),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_AUTO_CACHING(16),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_VDO_CACHING(32),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_NO_CACHING(48),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_DFS(1),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_DFS_ROOT(2),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_RESTRICT_EXCLUSIVE_OPENS(256),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_FORCE_SHARED_DELETE(512),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_ALLOW_NAMESPACE_CACHING(1024),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_ACCESS_BASED_DIRECTORY_ENUM(PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_FORCE_LEVELII_OPLOCK(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_ENABLE_HASH_V1(PlaybackStateCompat.ACTION_PLAY_FROM_URI),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_ENABLE_HASH_V2(16384),
    SMB2_SHAREFLAG_ENCRYPT_DATA(PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID),
    /* JADX INFO: Fake field, exist only in values array */
    SMB2_SHAREFLAG_IDENTITY_REMOTING(PlaybackStateCompat.ACTION_SET_REPEAT_MODE);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f614b;

    b0(long j10) {
        this.f614b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f614b;
    }
}
