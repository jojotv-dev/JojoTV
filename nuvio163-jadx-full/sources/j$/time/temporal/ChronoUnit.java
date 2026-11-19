package j$.time.temporal;

import j$.time.Duration;

/* JADX INFO: loaded from: classes2.dex */
public enum ChronoUnit implements o {
    NANOS("Nanos", Duration.F(1)),
    MICROS("Micros", Duration.F(1000)),
    MILLIS("Millis", Duration.F(1000000)),
    SECONDS("Seconds", Duration.ofSeconds(1)),
    MINUTES("Minutes", Duration.ofSeconds(60)),
    HOURS("Hours", Duration.ofSeconds(3600)),
    HALF_DAYS("HalfDays", Duration.ofSeconds(43200)),
    DAYS("Days", Duration.ofSeconds(86400)),
    WEEKS("Weeks", Duration.ofSeconds(604800)),
    MONTHS("Months", Duration.ofSeconds(2629746)),
    YEARS("Years", Duration.ofSeconds(31556952)),
    DECADES("Decades", Duration.ofSeconds(315569520)),
    CENTURIES("Centuries", Duration.ofSeconds(3155695200L)),
    MILLENNIA("Millennia", Duration.ofSeconds(31556952000L)),
    ERAS("Eras", Duration.ofSeconds(31556952000000000L)),
    FOREVER("Forever", Duration.ofSeconds(Long.MAX_VALUE, 999999999));


    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f26670a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Duration f26671b;

    ChronoUnit(String str, Duration duration) {
        this.f26670a = str;
        this.f26671b = duration;
    }

    public Duration getDuration() {
        return this.f26671b;
    }

    @Override // j$.time.temporal.o
    public final Temporal v(Temporal temporal, long j10) {
        return temporal.b(j10, this);
    }

    @Override // j$.time.temporal.o
    public long between(Temporal temporal, Temporal temporal2) {
        return temporal.n(temporal2, this);
    }

    @Override // java.lang.Enum
    public final String toString() {
        return this.f26670a;
    }
}
