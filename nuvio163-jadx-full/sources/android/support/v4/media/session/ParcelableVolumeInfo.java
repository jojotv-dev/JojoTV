package android.support.v4.media.session;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
@SuppressLint({"BanParcelableUsage"})
public class ParcelableVolumeInfo implements Parcelable {
    public static final Parcelable.Creator<ParcelableVolumeInfo> CREATOR = new g(3);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f1909b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f1910c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f1911e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f1912i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public int f1913m;

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeInt(this.f1909b);
        parcel.writeInt(this.f1911e);
        parcel.writeInt(this.f1912i);
        parcel.writeInt(this.f1913m);
        parcel.writeInt(this.f1910c);
    }
}
