package x1;

import android.view.autofill.AutofillValue;

/* JADX INFO: loaded from: classes3.dex */
public final class g implements r {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final AutofillValue f62992a;

    public g(AutofillValue autofillValue) {
        this.f62992a = autofillValue;
    }

    public final AutofillValue a() {
        return this.f62992a;
    }

    public final Boolean b() {
        if (this.f62992a.isToggle()) {
            return Boolean.valueOf(this.f62992a.getToggleValue());
        }
        return null;
    }

    public final CharSequence c() {
        if (this.f62992a.isText()) {
            return this.f62992a.getTextValue();
        }
        return null;
    }
}
