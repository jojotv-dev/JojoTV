package android.support.v4.media;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
@SuppressLint({"BanParcelableUsage"})
public class MediaBrowserCompat$MediaItem implements Parcelable {
    public static final Parcelable.Creator<MediaBrowserCompat$MediaItem> CREATOR = new g(0);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f1873b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final MediaDescriptionCompat f1874c;

    public MediaBrowserCompat$MediaItem(Parcel parcel) {
        this.f1873b = parcel.readInt();
        this.f1874c = MediaDescriptionCompat.CREATOR.createFromParcel(parcel);
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public final String toString() {
        return "MediaItem{mFlags=" + this.f1873b + ", mDescription=" + this.f1874c + '}';
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeInt(this.f1873b);
        this.f1874c.writeToParcel(parcel, i10);
    }
}
