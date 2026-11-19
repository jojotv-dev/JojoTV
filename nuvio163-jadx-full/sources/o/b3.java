package o;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
public final class b3 extends q4.b {
    public static final Parcelable.Creator<b3> CREATOR = new androidx.recyclerview.widget.y0(1);

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f43103e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public boolean f43104i;

    public b3(Parcel parcel, ClassLoader classLoader) {
        super(parcel, classLoader);
        this.f43103e = parcel.readInt();
        this.f43104i = parcel.readInt() != 0;
    }

    @Override // q4.b, android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        super.writeToParcel(parcel, i10);
        parcel.writeInt(this.f43103e);
        parcel.writeInt(this.f43104i ? 1 : 0);
    }

    public b3(Parcelable parcelable) {
        super(parcelable);
    }
}
