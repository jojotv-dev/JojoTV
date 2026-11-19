package androidx.recyclerview.widget;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public final class m1 implements Parcelable {
    public static final Parcelable.Creator<m1> CREATOR = new android.support.v4.media.g(7);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public int f3992b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f3993c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f3994e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int[] f3995i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public int f3996m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public int[] f3997n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public ArrayList f3998o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public boolean f3999p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public boolean f4000q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public boolean f4001r;

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeInt(this.f3992b);
        parcel.writeInt(this.f3993c);
        parcel.writeInt(this.f3994e);
        if (this.f3994e > 0) {
            parcel.writeIntArray(this.f3995i);
        }
        parcel.writeInt(this.f3996m);
        if (this.f3996m > 0) {
            parcel.writeIntArray(this.f3997n);
        }
        parcel.writeInt(this.f3999p ? 1 : 0);
        parcel.writeInt(this.f4000q ? 1 : 0);
        parcel.writeInt(this.f4001r ? 1 : 0);
        parcel.writeList(this.f3998o);
    }
}
