package android.support.v4.media.session;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
@SuppressLint({"BanParcelableUsage"})
public final class PlaybackStateCompat implements Parcelable {
    public static final Parcelable.Creator<PlaybackStateCompat> CREATOR = new g(4);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f1914b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final long f1915c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final long f1916e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final float f1917i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final long f1918m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final int f1919n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final CharSequence f1920o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public final long f1921p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public final ArrayList f1922q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public final long f1923r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public final Bundle f1924s;

    public static final class CustomAction implements Parcelable {
        public static final Parcelable.Creator<CustomAction> CREATOR = new i();

        /* JADX INFO: renamed from: b, reason: collision with root package name */
        public final String f1925b;

        /* JADX INFO: renamed from: c, reason: collision with root package name */
        public final CharSequence f1926c;

        /* JADX INFO: renamed from: e, reason: collision with root package name */
        public final int f1927e;

        /* JADX INFO: renamed from: i, reason: collision with root package name */
        public final Bundle f1928i;

        public CustomAction(Parcel parcel) {
            this.f1925b = parcel.readString();
            this.f1926c = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.f1927e = parcel.readInt();
            this.f1928i = parcel.readBundle(h.class.getClassLoader());
        }

        @Override // android.os.Parcelable
        public final int describeContents() {
            return 0;
        }

        public final String toString() {
            return "Action:mName='" + ((Object) this.f1926c) + ", mIcon=" + this.f1927e + ", mExtras=" + this.f1928i;
        }

        @Override // android.os.Parcelable
        public final void writeToParcel(Parcel parcel, int i10) {
            parcel.writeString(this.f1925b);
            TextUtils.writeToParcel(this.f1926c, parcel, i10);
            parcel.writeInt(this.f1927e);
            parcel.writeBundle(this.f1928i);
        }
    }

    public PlaybackStateCompat(Parcel parcel) {
        this.f1914b = parcel.readInt();
        this.f1915c = parcel.readLong();
        this.f1917i = parcel.readFloat();
        this.f1921p = parcel.readLong();
        this.f1916e = parcel.readLong();
        this.f1918m = parcel.readLong();
        this.f1920o = (CharSequence) TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.f1922q = parcel.createTypedArrayList(CustomAction.CREATOR);
        this.f1923r = parcel.readLong();
        this.f1924s = parcel.readBundle(h.class.getClassLoader());
        this.f1919n = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("PlaybackState {state=");
        sb2.append(this.f1914b);
        sb2.append(", position=");
        sb2.append(this.f1915c);
        sb2.append(", buffered position=");
        sb2.append(this.f1916e);
        sb2.append(", speed=");
        sb2.append(this.f1917i);
        sb2.append(", updated=");
        sb2.append(this.f1921p);
        sb2.append(", actions=");
        sb2.append(this.f1918m);
        sb2.append(", error code=");
        sb2.append(this.f1919n);
        sb2.append(", error message=");
        sb2.append(this.f1920o);
        sb2.append(", custom actions=");
        sb2.append(this.f1922q);
        sb2.append(", active item id=");
        return a0.i.h(this.f1923r, "}", sb2);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeInt(this.f1914b);
        parcel.writeLong(this.f1915c);
        parcel.writeFloat(this.f1917i);
        parcel.writeLong(this.f1921p);
        parcel.writeLong(this.f1916e);
        parcel.writeLong(this.f1918m);
        TextUtils.writeToParcel(this.f1920o, parcel, i10);
        parcel.writeTypedList(this.f1922q);
        parcel.writeLong(this.f1923r);
        parcel.writeBundle(this.f1924s);
        parcel.writeInt(this.f1919n);
    }
}
