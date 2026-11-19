package w8;

import k9.c;

/* JADX INFO: loaded from: classes4.dex */
public enum b implements c {
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_ADDED(1),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_REMOVED(2),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_MODIFIED(3),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_RENAMED_OLD_NAME(4),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_RENAMED_NEW_NAME(5),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_ADDED_STREAM(6),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_REMOVED_STREAM(7),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_MODIFIED_STREAM(8),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_REMOVED_BY_DELETE(9),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_ID_NOT_TUNNELLED(10),
    /* JADX INFO: Fake field, exist only in values array */
    FILE_ACTION_TUNNELLED_ID_COLLISION(11);


    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final long f62163b;

    b(long j10) {
        this.f62163b = j10;
    }

    @Override // k9.c
    public final long getValue() {
        return this.f62163b;
    }
}
