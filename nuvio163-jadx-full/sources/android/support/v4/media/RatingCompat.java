package android.support.v4.media;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
@SuppressLint({"BanParcelableUsage"})
public final class RatingCompat implements Parcelable {
    public static final Parcelable.Creator<RatingCompat> CREATOR = new g(3);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f1885b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final float f1886c;

    public RatingCompat(int i10, float f10) {
        this.f1885b = i10;
        this.f1886c = f10;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return this.f1885b;
    }

    public final String toString() {
        StringBuilder sb2 = new StringBuilder("Rating:style=");
        sb2.append(this.f1885b);
        sb2.append(" rating=");
        float f10 = this.f1886c;
        sb2.append(f10 < 0.0f ? "unrated" : String.valueOf(f10));
        return sb2.toString();
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeInt(this.f1885b);
        parcel.writeFloat(this.f1886c);
    }
}
