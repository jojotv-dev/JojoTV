package j$.time.temporal;

import androidx.media3.common.C;

/* JADX INFO: loaded from: classes2.dex */
public enum ChronoField implements TemporalField {
    NANO_OF_SECOND("NanoOfSecond", q.f(0, 999999999)),
    NANO_OF_DAY("NanoOfDay", q.f(0, 86399999999999L)),
    MICRO_OF_SECOND("MicroOfSecond", q.f(0, 999999)),
    MICRO_OF_DAY("MicroOfDay", q.f(0, 86399999999L)),
    MILLI_OF_SECOND("MilliOfSecond", q.f(0, 999)),
    MILLI_OF_DAY("MilliOfDay", q.f(0, 86399999)),
    SECOND_OF_MINUTE("SecondOfMinute", q.f(0, 59), 0),
    SECOND_OF_DAY("SecondOfDay", q.f(0, 86399)),
    MINUTE_OF_HOUR("MinuteOfHour", q.f(0, 59), 0),
    MINUTE_OF_DAY("MinuteOfDay", q.f(0, 1439)),
    HOUR_OF_AMPM("HourOfAmPm", q.f(0, 11)),
    CLOCK_HOUR_OF_AMPM("ClockHourOfAmPm", q.f(1, 12)),
    HOUR_OF_DAY("HourOfDay", q.f(0, 23), 0),
    CLOCK_HOUR_OF_DAY("ClockHourOfDay", q.f(1, 24)),
    AMPM_OF_DAY("AmPmOfDay", q.f(0, 1), 0),
    DAY_OF_WEEK("DayOfWeek", q.f(1, 7), 0),
    ALIGNED_DAY_OF_WEEK_IN_MONTH("AlignedDayOfWeekInMonth", q.f(1, 7)),
    ALIGNED_DAY_OF_WEEK_IN_YEAR("AlignedDayOfWeekInYear", q.f(1, 7)),
    DAY_OF_MONTH("DayOfMonth", q.g(1, 28, 31), 0),
    DAY_OF_YEAR("DayOfYear", q.g(1, 365, 366)),
    EPOCH_DAY("EpochDay", q.f(-365243219162L, 365241780471L)),
    ALIGNED_WEEK_OF_MONTH("AlignedWeekOfMonth", q.g(1, 4, 5)),
    ALIGNED_WEEK_OF_YEAR("AlignedWeekOfYear", q.f(1, 53)),
    MONTH_OF_YEAR("MonthOfYear", q.f(1, 12), 0),
    PROLEPTIC_MONTH("ProlepticMonth", q.f(-11999999988L, 11999999999L)),
    YEAR_OF_ERA("YearOfEra", q.g(1, 999999999, C.NANOS_PER_SECOND)),
    YEAR("Year", q.f(-999999999, 999999999), 0),
    ERA("Era", q.f(0, 1), 0),
    INSTANT_SECONDS("InstantSeconds", q.f(Long.MIN_VALUE, Long.MAX_VALUE)),
    OFFSET_SECONDS("OffsetSeconds", q.f(-64800, 64800));


    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f26667a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final q f26668b;

    static {
        ChronoUnit chronoUnit = ChronoUnit.NANOS;
    }

    ChronoField(String str, q qVar) {
        this.f26667a = str;
        this.f26668b = qVar;
    }

    ChronoField(String str, q qVar, int i10) {
        this.f26667a = str;
        this.f26668b = qVar;
    }

    @Override // j$.time.temporal.TemporalField
    public final q H() {
        return this.f26668b;
    }

    public final void W(long j10) {
        this.f26668b.b(j10, this);
    }

    @Override // j$.time.temporal.TemporalField
    public final boolean isDateBased() {
        return ordinal() >= DAY_OF_WEEK.ordinal() && ordinal() <= ERA.ordinal();
    }

    public final boolean X() {
        return ordinal() < DAY_OF_WEEK.ordinal();
    }

    @Override // j$.time.temporal.TemporalField
    public final boolean v(TemporalAccessor temporalAccessor) {
        return temporalAccessor.i(this);
    }

    @Override // j$.time.temporal.TemporalField
    public final q A(TemporalAccessor temporalAccessor) {
        return temporalAccessor.l(this);
    }

    @Override // j$.time.temporal.TemporalField
    public final long O(TemporalAccessor temporalAccessor) {
        return temporalAccessor.k(this);
    }

    @Override // j$.time.temporal.TemporalField
    public final Temporal U(Temporal temporal, long j10) {
        return temporal.a(j10, this);
    }

    @Override // java.lang.Enum
    public final String toString() {
        return this.f26667a;
    }
}
