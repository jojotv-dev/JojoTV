package androidx.core.app;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.core.graphics.drawable.IconCompat;
import io.ktor.http.ContentDisposition;
import java.util.Objects;

/* JADX INFO: loaded from: classes3.dex */
public final class s0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public CharSequence f2359a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public IconCompat f2360b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public String f2361c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public String f2362d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f2363e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public boolean f2364f;

    public static s0 a(Bundle bundle) {
        Bundle bundle2 = bundle.getBundle("icon");
        CharSequence charSequence = bundle.getCharSequence(ContentDisposition.Parameters.Name);
        IconCompat iconCompatA = bundle2 != null ? IconCompat.a(bundle2) : null;
        String string = bundle.getString("uri");
        String string2 = bundle.getString("key");
        boolean z2 = bundle.getBoolean("isBot");
        boolean z10 = bundle.getBoolean("isImportant");
        s0 s0Var = new s0();
        s0Var.f2359a = charSequence;
        s0Var.f2360b = iconCompatA;
        s0Var.f2361c = string;
        s0Var.f2362d = string2;
        s0Var.f2363e = z2;
        s0Var.f2364f = z10;
        return s0Var;
    }

    public final Bundle b() {
        Bundle bundle;
        Bundle bundle2 = new Bundle();
        bundle2.putCharSequence(ContentDisposition.Parameters.Name, this.f2359a);
        IconCompat iconCompat = this.f2360b;
        if (iconCompat != null) {
            bundle = new Bundle();
            switch (iconCompat.f2385a) {
                case -1:
                    bundle.putParcelable("obj", (Parcelable) iconCompat.f2386b);
                    break;
                case 0:
                default:
                    throw new IllegalArgumentException("Invalid icon");
                case 1:
                case 5:
                    bundle.putParcelable("obj", (Bitmap) iconCompat.f2386b);
                    break;
                case 2:
                case 4:
                case 6:
                    bundle.putString("obj", (String) iconCompat.f2386b);
                    break;
                case 3:
                    bundle.putByteArray("obj", (byte[]) iconCompat.f2386b);
                    break;
            }
            bundle.putInt("type", iconCompat.f2385a);
            bundle.putInt("int1", iconCompat.f2389e);
            bundle.putInt("int2", iconCompat.f2390f);
            bundle.putString("string1", iconCompat.f2394j);
            ColorStateList colorStateList = iconCompat.f2391g;
            if (colorStateList != null) {
                bundle.putParcelable("tint_list", colorStateList);
            }
            PorterDuff.Mode mode = iconCompat.f2392h;
            if (mode != IconCompat.k) {
                bundle.putString("tint_mode", mode.name());
            }
        } else {
            bundle = null;
        }
        bundle2.putBundle("icon", bundle);
        bundle2.putString("uri", this.f2361c);
        bundle2.putString("key", this.f2362d);
        bundle2.putBoolean("isBot", this.f2363e);
        bundle2.putBoolean("isImportant", this.f2364f);
        return bundle2;
    }

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof s0)) {
            return false;
        }
        s0 s0Var = (s0) obj;
        String str = this.f2362d;
        String str2 = s0Var.f2362d;
        if (str == null && str2 == null) {
            return Objects.equals(Objects.toString(this.f2359a), Objects.toString(s0Var.f2359a)) && Objects.equals(this.f2361c, s0Var.f2361c) && Boolean.valueOf(this.f2363e).equals(Boolean.valueOf(s0Var.f2363e)) && Boolean.valueOf(this.f2364f).equals(Boolean.valueOf(s0Var.f2364f));
        }
        return Objects.equals(str, str2);
    }

    public final int hashCode() {
        String str = this.f2362d;
        return str != null ? str.hashCode() : Objects.hash(this.f2359a, this.f2361c, Boolean.valueOf(this.f2363e), Boolean.valueOf(this.f2364f));
    }
}
