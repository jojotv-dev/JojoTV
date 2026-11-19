package c8;

import java.sql.Timestamp;
import java.util.Date;
import z7.g;

/* JADX INFO: loaded from: classes.dex */
public final class e extends g {

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final /* synthetic */ int f6590c;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ e(Class cls, int i10) {
        super(cls);
        this.f6590c = i10;
    }

    @Override // z7.g
    public final Date a(Date date) {
        switch (this.f6590c) {
            case 0:
                return new java.sql.Date(date.getTime());
            default:
                return new Timestamp(date.getTime());
        }
    }
}
