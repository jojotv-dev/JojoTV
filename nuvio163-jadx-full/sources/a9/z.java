package a9;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

/* JADX INFO: loaded from: classes4.dex */
public enum z implements k9.c {
    FILE_SHARE_READ(1),
    FILE_SHARE_WRITE(2),
    FILE_SHARE_DELETE(4);


    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final Set f726m = Collections.unmodifiableSet(EnumSet.allOf(z.class));

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f728b;

    z(long j10) {
        this.f728b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f728b;
    }
}
