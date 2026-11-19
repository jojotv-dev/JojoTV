package org.chromium.net.impl;

import android.content.Context;
import java.util.Arrays;
import org.chromium.base.annotations.UsedByReflection;
import zn.e;

/* JADX INFO: loaded from: classes5.dex */
public class NativeCronetProvider extends e {
    @UsedByReflection
    public NativeCronetProvider(Context context) {
        super(context);
    }

    public final boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof NativeCronetProvider) {
            return this.f66850a.equals(((NativeCronetProvider) obj).f66850a);
        }
        return false;
    }

    public final int hashCode() {
        return Arrays.hashCode(new Object[]{NativeCronetProvider.class, this.f66850a});
    }
}
