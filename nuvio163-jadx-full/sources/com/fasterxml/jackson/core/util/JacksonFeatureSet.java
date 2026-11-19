package com.fasterxml.jackson.core.util;

import com.fasterxml.jackson.core.util.JacksonFeature;
import java.io.Serializable;

/* JADX INFO: loaded from: classes3.dex */
public final class JacksonFeatureSet<F extends JacksonFeature> implements Serializable {
    private static final long serialVersionUID = 1;
    protected int _enabled;

    public JacksonFeatureSet(int i10) {
        this._enabled = i10;
    }

    public static <F extends JacksonFeature> JacksonFeatureSet<F> fromBitmask(int i10) {
        return new JacksonFeatureSet<>(i10);
    }

    public static <F extends JacksonFeature> JacksonFeatureSet<F> fromDefaults(F[] fArr) {
        if (fArr.length > 31) {
            throw new IllegalArgumentException(String.format("Can not use type `%s` with JacksonFeatureSet: too many entries (%d > 31)", fArr[0].getClass().getName(), Integer.valueOf(fArr.length)));
        }
        int mask = 0;
        for (F f10 : fArr) {
            if (f10.enabledByDefault()) {
                mask |= f10.getMask();
            }
        }
        return new JacksonFeatureSet<>(mask);
    }

    public int asBitmask() {
        return this._enabled;
    }

    public boolean isEnabled(F f10) {
        return (f10.getMask() & this._enabled) != 0;
    }

    public JacksonFeatureSet<F> with(F f10) {
        int mask = f10.getMask() | this._enabled;
        return mask == this._enabled ? this : new JacksonFeatureSet<>(mask);
    }

    public JacksonFeatureSet<F> without(F f10) {
        int i10 = (~f10.getMask()) & this._enabled;
        return i10 == this._enabled ? this : new JacksonFeatureSet<>(i10);
    }
}
