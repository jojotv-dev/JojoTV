package i9;

import androidx.media3.session.legacy.PlaybackStateCompat;

/* JADX INFO: loaded from: classes4.dex */
public enum e implements k9.c {
    NTLMSSP_NEGOTIATE_56(2147483648L),
    NTLMSSP_NEGOTIATE_KEY_EXCH(1073741824),
    NTLMSSP_NEGOTIATE_128(536870912),
    NTLMSSP_NEGOTIATE_VERSION(33554432),
    NTLMSSP_NEGOTIATE_TARGET_INFO(8388608),
    /* JADX INFO: Fake field, exist only in values array */
    NTLMSSP_REQUEST_NON_NT_SESSION_KEY(PlaybackStateCompat.ACTION_SET_PLAYBACK_SPEED),
    /* JADX INFO: Fake field, exist only in values array */
    NTLMSSP_NEGOTIATE_IDENTIFY(1048576),
    NTLMSSP_NEGOTIATE_EXTENDED_SESSIONSECURITY(PlaybackStateCompat.ACTION_SET_SHUFFLE_MODE_ENABLED),
    /* JADX INFO: Fake field, exist only in values array */
    NTLMSSP_TARGET_TYPE_SERVER(PlaybackStateCompat.ACTION_PREPARE_FROM_URI),
    /* JADX INFO: Fake field, exist only in values array */
    NTLMSSP_TARGET_TYPE_DOMAIN(PlaybackStateCompat.ACTION_PREPARE_FROM_SEARCH),
    NTLMSSP_NEGOTIATE_ALWAYS_SIGN(PlaybackStateCompat.ACTION_PREPARE_FROM_MEDIA_ID),
    NTLMSSP_NEGOTIATE_OEM_WORKSTATION_SUPPLIED(PlaybackStateCompat.ACTION_PLAY_FROM_URI),
    NTLMSSP_NEGOTIATE_OEM_DOMAIN_SUPPLIED(PlaybackStateCompat.ACTION_SKIP_TO_QUEUE_ITEM),
    NTLMSSP_NEGOTIATE_ANONYMOUS(PlaybackStateCompat.ACTION_PLAY_FROM_SEARCH),
    NTLMSSP_NEGOTIATE_NTLM(512),
    NTLMSSP_NEGOTIATE_LM_KEY(128),
    NTLMSSP_NEGOTIATE_DATAGRAM(64),
    NTLMSSP_NEGOTIATE_SEAL(32),
    NTLMSSP_NEGOTIATE_SIGN(16),
    NTLMSSP_REQUEST_TARGET(4),
    /* JADX INFO: Fake field, exist only in values array */
    NTLM_NEGOTIATE_OEM(2),
    NTLMSSP_NEGOTIATE_UNICODE(1);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f23875b;

    e(long j10) {
        this.f23875b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f23875b;
    }
}
