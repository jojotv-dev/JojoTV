package android.support.v4.media.session;

import android.annotation.SuppressLint;
import android.media.session.MediaSession;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
@SuppressLint({"BanParcelableUsage"})
public final class MediaSessionCompat$Token implements Parcelable {
    public static final Parcelable.Creator<MediaSessionCompat$Token> CREATOR = new g(2);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f1905b = new Object();

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final Object f1906c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public c f1907e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public c6.d f1908i;

    public MediaSessionCompat$Token(Object obj, c cVar) {
        this.f1906c = obj;
        this.f1907e = cVar;
    }

    public static MediaSessionCompat$Token a(Parcelable parcelable, c cVar) {
        if (parcelable == null) {
            return null;
        }
        if (parcelable instanceof MediaSession.Token) {
            return new MediaSessionCompat$Token(parcelable, cVar);
        }
        throw new IllegalArgumentException("token is not a valid MediaSession.Token object");
    }

    public final c b() {
        c cVar;
        synchronized (this.f1905b) {
            cVar = this.f1907e;
        }
        return cVar;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MediaSessionCompat$Token)) {
            return false;
        }
        Object obj2 = ((MediaSessionCompat$Token) obj).f1906c;
        Object obj3 = this.f1906c;
        if (obj3 == null) {
            return obj2 == null;
        }
        if (obj2 == null) {
            return false;
        }
        return obj3.equals(obj2);
    }

    public final int hashCode() {
        Object obj = this.f1906c;
        if (obj == null) {
            return 0;
        }
        return obj.hashCode();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeParcelable((Parcelable) this.f1906c, i10);
    }
}
