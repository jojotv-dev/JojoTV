package j$.time.temporal;

import j$.time.LocalDate;

/* JADX INFO: loaded from: classes2.dex */
public interface Temporal extends TemporalAccessor {
    Temporal a(long j10, TemporalField temporalField);

    Temporal b(long j10, o oVar);

    long n(Temporal temporal, o oVar);

    /* JADX INFO: renamed from: e */
    default Temporal j(LocalDate localDate) {
        return localDate.f(this);
    }

    default Temporal c(long j10, o oVar) {
        return j10 == Long.MIN_VALUE ? b(Long.MAX_VALUE, oVar).b(1L, oVar) : b(-j10, oVar);
    }
}
