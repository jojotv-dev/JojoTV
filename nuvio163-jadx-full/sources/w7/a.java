package w7;

import java.lang.reflect.Field;

/* JADX INFO: loaded from: classes.dex */
public final enum a extends h {
    public a() {
        super("IDENTITY", 0);
    }

    @Override // w7.h
    public final String b(Field field) {
        return field.getName();
    }
}
