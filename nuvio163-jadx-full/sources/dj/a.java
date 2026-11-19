package dj;

import javax.el.ExpressionFactory;

/* JADX INFO: loaded from: classes5.dex */
public abstract class a {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final ExpressionFactory f13333a = a();

    public static final ExpressionFactory a() {
        try {
            return ExpressionFactory.newInstance();
        } catch (RuntimeException unused) {
            return null;
        }
    }
}
