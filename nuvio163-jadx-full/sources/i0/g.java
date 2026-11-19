package i0;

import android.os.Parcel;
import android.os.Parcelable;
import kotlin.jvm.JvmField;

/* JADX INFO: loaded from: classes.dex */
public final class g implements Parcelable {

    @JvmField
    public static final Parcelable.Creator<g> CREATOR = new f();

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f23603b;

    public g(int i10) {
        this.f23603b = i10;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public final boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof g) && this.f23603b == ((g) obj).f23603b;
    }

    public final int hashCode() {
        return Integer.hashCode(this.f23603b);
    }

    public final String toString() {
        return o2.a.p(new StringBuilder("DefaultLazyKey(index="), this.f23603b, ')');
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        parcel.writeInt(this.f23603b);
    }
}
