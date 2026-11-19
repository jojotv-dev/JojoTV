package up;

import j$.util.DesugarTimeZone;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/* JADX INFO: loaded from: classes5.dex */
public abstract class g0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final DecimalFormat f60133a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final DecimalFormat f60134b;

    static {
        DecimalFormat decimalFormat = new DecimalFormat();
        f60133a = decimalFormat;
        decimalFormat.setMinimumIntegerDigits(2);
        DecimalFormat decimalFormat2 = new DecimalFormat();
        f60134b = decimalFormat2;
        decimalFormat2.setMinimumIntegerDigits(4);
        decimalFormat2.setGroupingUsed(false);
    }

    public static String a(Date date) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(DesugarTimeZone.getTimeZone("UTC"));
        StringBuffer stringBuffer = new StringBuffer();
        gregorianCalendar.setTime(date);
        stringBuffer.append(f60134b.format(gregorianCalendar.get(1)));
        long j10 = gregorianCalendar.get(2) + 1;
        DecimalFormat decimalFormat = f60133a;
        stringBuffer.append(decimalFormat.format(j10));
        stringBuffer.append(decimalFormat.format(gregorianCalendar.get(5)));
        stringBuffer.append(decimalFormat.format(gregorianCalendar.get(11)));
        stringBuffer.append(decimalFormat.format(gregorianCalendar.get(12)));
        stringBuffer.append(decimalFormat.format(gregorianCalendar.get(13)));
        return stringBuffer.toString();
    }
}
