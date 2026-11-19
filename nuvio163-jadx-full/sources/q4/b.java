package q4;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.recyclerview.widget.y0;

/* JADX INFO: loaded from: classes3.dex */
public abstract class b implements Parcelable {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Parcelable f45644b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public static final a f45643c = new a();
    public static final Parcelable.Creator<b> CREATOR = new y0(2);

    public b() {
        this.f45644b = null;
    }

    public final Parcelable a() {
        return this.f45644b;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i10) {
        parcel.writeParcelable(this.f45644b, i10);
    }

    public b(Parcelable parcelable) {
        if (parcelable != null) {
            this.f45644b = parcelable == f45643c ? null : parcelable;
            return;
        }
        throw new IllegalArgumentException("superState must not be null");
    }

    public b(Parcel parcel, ClassLoader classLoader) {
        Parcelable parcelable = parcel.readParcelable(classLoader);
        this.f45644b = parcelable == null ? f45643c : parcelable;
    }
}
