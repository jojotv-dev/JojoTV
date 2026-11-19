package b4;

import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;

/* JADX INFO: loaded from: classes3.dex */
public final class j {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final ColorStateList f4326a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Configuration f4327b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f4328c;

    public j(ColorStateList colorStateList, Configuration configuration, Resources.Theme theme) {
        this.f4326a = colorStateList;
        this.f4327b = configuration;
        this.f4328c = theme == null ? 0 : theme.hashCode();
    }
}
