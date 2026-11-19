package m;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.view.LayoutInflater;
import com.jojo.tv.R;

/* JADX INFO: loaded from: classes.dex */
public final class c extends ContextWrapper {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public int f34170a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public Resources.Theme f34171b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public LayoutInflater f34172c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public Resources f34173d;

    public c(Context context, int i10) {
        super(context);
        this.f34170a = i10;
    }

    public final void a() {
        if (this.f34171b == null) {
            this.f34171b = getResources().newTheme();
            Resources.Theme theme = getBaseContext().getTheme();
            if (theme != null) {
                this.f34171b.setTo(theme);
            }
        }
        this.f34171b.applyStyle(this.f34170a, true);
    }

    @Override // android.content.ContextWrapper
    public final void attachBaseContext(Context context) {
        super.attachBaseContext(context);
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final AssetManager getAssets() {
        return getResources().getAssets();
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final Resources getResources() {
        if (this.f34173d == null) {
            this.f34173d = super.getResources();
        }
        return this.f34173d;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final Object getSystemService(String str) {
        if (!"layout_inflater".equals(str)) {
            return getBaseContext().getSystemService(str);
        }
        if (this.f34172c == null) {
            this.f34172c = LayoutInflater.from(getBaseContext()).cloneInContext(this);
        }
        return this.f34172c;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final Resources.Theme getTheme() {
        Resources.Theme theme = this.f34171b;
        if (theme != null) {
            return theme;
        }
        if (this.f34170a == 0) {
            this.f34170a = R.style.Theme_AppCompat_Light;
        }
        a();
        return this.f34171b;
    }

    @Override // android.content.ContextWrapper, android.content.Context
    public final void setTheme(int i10) {
        if (this.f34170a != i10) {
            this.f34170a = i10;
            a();
        }
    }
}
