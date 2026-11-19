package io.sentry;

import androidx.media3.exoplayer.upstream.CmcdConfiguration;
import com.liskovsoft.youtubeapi.videoinfo.models.formats.VideoFormat;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes.dex */
public final class b7 implements d2 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Date f25370b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public Date f25371c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final AtomicInteger f25372e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final String f25373i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final String f25374m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public Boolean f25375n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public a7 f25376o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public Long f25377p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public Double f25378q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public final String f25379r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public String f25380s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public final String f25381t;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public final String f25382u;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public String f25383v;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    public final io.sentry.util.a f25384w = new io.sentry.util.a();

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    public ConcurrentHashMap f25385x;

    public b7(a7 a7Var, Date date, Date date2, int i10, String str, String str2, Boolean bool, Long l7, Double d10, String str3, String str4, String str5, String str6, String str7) {
        this.f25376o = a7Var;
        this.f25370b = date;
        this.f25371c = date2;
        this.f25372e = new AtomicInteger(i10);
        this.f25373i = str;
        this.f25374m = str2;
        this.f25375n = bool;
        this.f25377p = l7;
        this.f25378q = d10;
        this.f25379r = str3;
        this.f25380s = str4;
        this.f25381t = str5;
        this.f25382u = str6;
        this.f25383v = str7;
    }

    /* JADX INFO: renamed from: a, reason: merged with bridge method [inline-methods] */
    public final b7 clone() {
        return new b7(this.f25376o, this.f25370b, this.f25371c, this.f25372e.get(), this.f25373i, this.f25374m, this.f25375n, this.f25377p, this.f25378q, this.f25379r, this.f25380s, this.f25381t, this.f25382u, this.f25383v);
    }

    public final void b(Date date) {
        io.sentry.util.a aVar = this.f25384w;
        aVar.g();
        try {
            this.f25375n = null;
            if (this.f25376o == a7.Ok) {
                this.f25376o = a7.Exited;
            }
            if (date != null) {
                this.f25371c = date;
            } else {
                this.f25371c = new Date();
            }
            Date date2 = this.f25371c;
            if (date2 != null) {
                this.f25378q = Double.valueOf(Math.abs(date2.getTime() - this.f25370b.getTime()) / 1000.0d);
                long time = this.f25371c.getTime();
                if (time < 0) {
                    time = Math.abs(time);
                }
                this.f25377p = Long.valueOf(time);
            }
            aVar.close();
        } catch (Throwable th2) {
            try {
                aVar.close();
            } catch (Throwable th3) {
                th2.addSuppressed(th3);
            }
            throw th2;
        }
    }

    public final boolean c(a7 a7Var, String str, boolean z2, String str2) {
        boolean z10;
        io.sentry.util.a aVar = this.f25384w;
        aVar.g();
        boolean z11 = true;
        if (a7Var != null) {
            try {
                this.f25376o = a7Var;
                z10 = true;
            } catch (Throwable th2) {
                try {
                    aVar.close();
                } catch (Throwable th3) {
                    th2.addSuppressed(th3);
                }
                throw th2;
            }
        } else {
            z10 = false;
        }
        if (str != null) {
            this.f25380s = str;
            z10 = true;
        }
        if (z2) {
            this.f25372e.addAndGet(1);
            z10 = true;
        }
        if (str2 != null) {
            this.f25383v = str2;
        } else {
            z11 = z10;
        }
        if (z11) {
            this.f25375n = null;
            Date date = new Date();
            this.f25371c = date;
            long time = date.getTime();
            if (time < 0) {
                time = Math.abs(time);
            }
            this.f25377p = Long.valueOf(time);
        }
        aVar.close();
        return z11;
    }

    @Override // io.sentry.d2
    public final void serialize(i3 i3Var, ILogger iLogger) throws IOException {
        v0.m mVar = (v0.m) i3Var;
        mVar.u();
        String str = this.f25374m;
        if (str != null) {
            mVar.F(CmcdConfiguration.KEY_SESSION_ID);
            mVar.W(str);
        }
        String str2 = this.f25373i;
        if (str2 != null) {
            mVar.F("did");
            mVar.W(str2);
        }
        if (this.f25375n != null) {
            mVar.F(VideoFormat.INIT);
            mVar.U(this.f25375n);
        }
        mVar.F("started");
        mVar.T(iLogger, this.f25370b);
        mVar.F("status");
        mVar.T(iLogger, this.f25376o.name().toLowerCase(Locale.ROOT));
        if (this.f25377p != null) {
            mVar.F("seq");
            mVar.V(this.f25377p);
        }
        mVar.F("errors");
        mVar.S(this.f25372e.intValue());
        if (this.f25378q != null) {
            mVar.F("duration");
            mVar.V(this.f25378q);
        }
        if (this.f25371c != null) {
            mVar.F("timestamp");
            mVar.T(iLogger, this.f25371c);
        }
        if (this.f25383v != null) {
            mVar.F("abnormal_mechanism");
            mVar.T(iLogger, this.f25383v);
        }
        mVar.F("attrs");
        mVar.u();
        mVar.F("release");
        mVar.T(iLogger, this.f25382u);
        String str3 = this.f25381t;
        if (str3 != null) {
            mVar.F("environment");
            mVar.T(iLogger, str3);
        }
        String str4 = this.f25379r;
        if (str4 != null) {
            mVar.F("ip_address");
            mVar.T(iLogger, str4);
        }
        if (this.f25380s != null) {
            mVar.F("user_agent");
            mVar.T(iLogger, this.f25380s);
        }
        mVar.w();
        ConcurrentHashMap concurrentHashMap = this.f25385x;
        if (concurrentHashMap != null) {
            for (String str5 : concurrentHashMap.keySet()) {
                h1.f.y(this.f25385x, str5, mVar, str5, iLogger);
            }
        }
        mVar.w();
    }
}
