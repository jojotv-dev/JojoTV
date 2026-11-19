package androidx.core.graphics.drawable;

import a.b;
import a4.a;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import androidx.versionedparcelable.CustomVersionedParcelable;
import io.ktor.sse.ServerSentEventKt;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes3.dex */
public class IconCompat extends CustomVersionedParcelable {
    public static final PorterDuff.Mode k = PorterDuff.Mode.SRC_IN;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f2385a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Object f2386b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public byte[] f2387c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public Parcelable f2388d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f2389e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public int f2390f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public ColorStateList f2391g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public PorterDuff.Mode f2392h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public String f2393i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public String f2394j;

    public IconCompat() {
        this.f2385a = -1;
        this.f2387c = null;
        this.f2388d = null;
        this.f2389e = 0;
        this.f2390f = 0;
        this.f2391g = null;
        this.f2392h = k;
        this.f2393i = null;
    }

    public static IconCompat a(Bundle bundle) {
        int i10 = bundle.getInt("type");
        IconCompat iconCompat = new IconCompat(i10);
        iconCompat.f2389e = bundle.getInt("int1");
        iconCompat.f2390f = bundle.getInt("int2");
        iconCompat.f2394j = bundle.getString("string1");
        if (bundle.containsKey("tint_list")) {
            iconCompat.f2391g = (ColorStateList) bundle.getParcelable("tint_list");
        }
        if (bundle.containsKey("tint_mode")) {
            iconCompat.f2392h = PorterDuff.Mode.valueOf(bundle.getString("tint_mode"));
        }
        switch (i10) {
            case -1:
            case 1:
            case 5:
                iconCompat.f2386b = bundle.getParcelable("obj");
                return iconCompat;
            case 0:
            default:
                Log.w("IconCompat", "Unknown type " + i10);
                return null;
            case 2:
            case 4:
            case 6:
                iconCompat.f2386b = bundle.getString("obj");
                return iconCompat;
            case 3:
                iconCompat.f2386b = bundle.getByteArray("obj");
                return iconCompat;
        }
    }

    public static IconCompat b(Icon icon) {
        icon.getClass();
        int I = b.I(icon);
        if (I == 2) {
            return e(null, b.G(icon), b.F(icon));
        }
        if (I == 4) {
            Uri uriJ = b.J(icon);
            uriJ.getClass();
            String string = uriJ.toString();
            string.getClass();
            IconCompat iconCompat = new IconCompat(4);
            iconCompat.f2386b = string;
            return iconCompat;
        }
        if (I != 6) {
            IconCompat iconCompat2 = new IconCompat(-1);
            iconCompat2.f2386b = icon;
            return iconCompat2;
        }
        Uri uriJ2 = b.J(icon);
        uriJ2.getClass();
        String string2 = uriJ2.toString();
        string2.getClass();
        IconCompat iconCompat3 = new IconCompat(6);
        iconCompat3.f2386b = string2;
        return iconCompat3;
    }

    public static Bitmap c(Bitmap bitmap, boolean z2) {
        int iMin = (int) (Math.min(bitmap.getWidth(), bitmap.getHeight()) * 0.6666667f);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(iMin, iMin, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        Paint paint = new Paint(3);
        float f10 = iMin;
        float f11 = 0.5f * f10;
        float f12 = 0.9166667f * f11;
        if (z2) {
            float f13 = 0.010416667f * f10;
            paint.setColor(0);
            paint.setShadowLayer(f13, 0.0f, f10 * 0.020833334f, 1023410176);
            canvas.drawCircle(f11, f11, f12, paint);
            paint.setShadowLayer(f13, 0.0f, 0.0f, 503316480);
            canvas.drawCircle(f11, f11, f12, paint);
            paint.clearShadowLayer();
        }
        paint.setColor(-16777216);
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
        Matrix matrix = new Matrix();
        matrix.setTranslate((-(bitmap.getWidth() - iMin)) / 2.0f, (-(bitmap.getHeight() - iMin)) / 2.0f);
        bitmapShader.setLocalMatrix(matrix);
        paint.setShader(bitmapShader);
        canvas.drawCircle(f11, f11, f12, paint);
        canvas.setBitmap(null);
        return bitmapCreateBitmap;
    }

    public static IconCompat d(int i10, Context context) {
        context.getClass();
        return e(context.getResources(), context.getPackageName(), i10);
    }

    public static IconCompat e(Resources resources, String str, int i10) {
        str.getClass();
        if (i10 == 0) {
            throw new IllegalArgumentException("Drawable resource ID must not be 0");
        }
        IconCompat iconCompat = new IconCompat(2);
        iconCompat.f2389e = i10;
        if (resources != null) {
            try {
                iconCompat.f2386b = resources.getResourceName(i10);
            } catch (Resources.NotFoundException unused) {
                throw new IllegalArgumentException("Icon resource cannot be found");
            }
        } else {
            iconCompat.f2386b = str;
        }
        iconCompat.f2394j = str;
        return iconCompat;
    }

    public final int f() {
        int i10 = this.f2385a;
        if (i10 == -1) {
            return b.F(this.f2386b);
        }
        if (i10 == 2) {
            return this.f2389e;
        }
        throw new IllegalStateException("called getResId() on " + this);
    }

    public final String g() {
        int i10 = this.f2385a;
        if (i10 == -1) {
            return b.G(this.f2386b);
        }
        if (i10 == 2) {
            String str = this.f2394j;
            return (str == null || TextUtils.isEmpty(str)) ? ((String) this.f2386b).split(ServerSentEventKt.COLON, -1)[0] : this.f2394j;
        }
        throw new IllegalStateException("called getResPackage() on " + this);
    }

    public final Uri h() {
        int i10 = this.f2385a;
        if (i10 == -1) {
            return b.J(this.f2386b);
        }
        if (i10 == 4 || i10 == 6) {
            return Uri.parse((String) this.f2386b);
        }
        throw new IllegalStateException("called getUri() on " + this);
    }

    public final Icon i(Context context) {
        Icon iconCreateWithBitmap;
        InputStream inputStreamOpenInputStream;
        int i10 = Build.VERSION.SDK_INT;
        switch (this.f2385a) {
            case -1:
                return (Icon) this.f2386b;
            case 0:
            default:
                throw new IllegalArgumentException("Unknown type");
            case 1:
                iconCreateWithBitmap = Icon.createWithBitmap((Bitmap) this.f2386b);
                break;
            case 2:
                iconCreateWithBitmap = Icon.createWithResource(g(), this.f2389e);
                break;
            case 3:
                iconCreateWithBitmap = Icon.createWithData((byte[]) this.f2386b, this.f2389e, this.f2390f);
                break;
            case 4:
                iconCreateWithBitmap = Icon.createWithContentUri((String) this.f2386b);
                break;
            case 5:
                iconCreateWithBitmap = i10 < 26 ? Icon.createWithBitmap(c((Bitmap) this.f2386b, false)) : a.f((Bitmap) this.f2386b);
                break;
            case 6:
                if (i10 >= 30) {
                    iconCreateWithBitmap = d4.a.a(h());
                } else {
                    if (context == null) {
                        throw new IllegalArgumentException("Context is required to resolve the file uri of the icon: " + h());
                    }
                    Uri uriH = h();
                    String scheme = uriH.getScheme();
                    if ("content".equals(scheme) || "file".equals(scheme)) {
                        try {
                            inputStreamOpenInputStream = context.getContentResolver().openInputStream(uriH);
                        } catch (Exception e10) {
                            Log.w("IconCompat", "Unable to load image from URI: " + uriH, e10);
                            inputStreamOpenInputStream = null;
                        }
                    } else {
                        try {
                            inputStreamOpenInputStream = new FileInputStream(new File((String) this.f2386b));
                        } catch (FileNotFoundException e11) {
                            Log.w("IconCompat", "Unable to load image from path: " + uriH, e11);
                            inputStreamOpenInputStream = null;
                        }
                    }
                    if (inputStreamOpenInputStream == null) {
                        throw new IllegalStateException("Cannot load adaptive icon from uri: " + h());
                    }
                    if (i10 < 26) {
                        iconCreateWithBitmap = Icon.createWithBitmap(c(BitmapFactory.decodeStream(inputStreamOpenInputStream), false));
                    } else {
                        iconCreateWithBitmap = a.f(BitmapFactory.decodeStream(inputStreamOpenInputStream));
                    }
                }
                break;
        }
        ColorStateList colorStateList = this.f2391g;
        if (colorStateList != null) {
            iconCreateWithBitmap.setTintList(colorStateList);
        }
        PorterDuff.Mode mode = this.f2392h;
        if (mode != k) {
            iconCreateWithBitmap.setTintMode(mode);
        }
        return iconCreateWithBitmap;
    }

    public final String toString() {
        String str;
        if (this.f2385a == -1) {
            return String.valueOf(this.f2386b);
        }
        StringBuilder sb2 = new StringBuilder("Icon(typ=");
        switch (this.f2385a) {
            case 1:
                str = "BITMAP";
                break;
            case 2:
                str = "RESOURCE";
                break;
            case 3:
                str = "DATA";
                break;
            case 4:
                str = "URI";
                break;
            case 5:
                str = "BITMAP_MASKABLE";
                break;
            case 6:
                str = "URI_MASKABLE";
                break;
            default:
                str = "UNKNOWN";
                break;
        }
        sb2.append(str);
        switch (this.f2385a) {
            case 1:
            case 5:
                sb2.append(" size=");
                sb2.append(((Bitmap) this.f2386b).getWidth());
                sb2.append("x");
                sb2.append(((Bitmap) this.f2386b).getHeight());
                break;
            case 2:
                sb2.append(" pkg=");
                sb2.append(this.f2394j);
                sb2.append(" id=");
                sb2.append(String.format("0x%08x", Integer.valueOf(f())));
                break;
            case 3:
                sb2.append(" len=");
                sb2.append(this.f2389e);
                if (this.f2390f != 0) {
                    sb2.append(" off=");
                    sb2.append(this.f2390f);
                }
                break;
            case 4:
            case 6:
                sb2.append(" uri=");
                sb2.append(this.f2386b);
                break;
        }
        if (this.f2391g != null) {
            sb2.append(" tint=");
            sb2.append(this.f2391g);
        }
        if (this.f2392h != k) {
            sb2.append(" mode=");
            sb2.append(this.f2392h);
        }
        sb2.append(")");
        return sb2.toString();
    }

    public IconCompat(int i10) {
        this.f2387c = null;
        this.f2388d = null;
        this.f2389e = 0;
        this.f2390f = 0;
        this.f2391g = null;
        this.f2392h = k;
        this.f2393i = null;
        this.f2385a = i10;
    }
}
