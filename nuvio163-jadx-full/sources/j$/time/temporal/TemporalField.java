package j$.time.temporal;

import j$.time.format.c0;
import j$.time.format.d0;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public interface TemporalField {
    q A(TemporalAccessor temporalAccessor);

    q H();

    long O(TemporalAccessor temporalAccessor);

    Temporal U(Temporal temporal, long j10);

    boolean isDateBased();

    boolean v(TemporalAccessor temporalAccessor);

    default TemporalAccessor F(Map map, c0 c0Var, d0 d0Var) {
        return null;
    }
}
