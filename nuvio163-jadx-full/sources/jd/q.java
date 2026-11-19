package jd;

import jcifs.smb.SmbException;
import kotlin.jvm.functions.Function1;

/* JADX INFO: loaded from: classes4.dex */
public final class q implements Function1 {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final q f27327b = new q();

    @Override // kotlin.jvm.functions.Function1
    public final Object invoke(Object obj) {
        return Boolean.valueOf(obj instanceof SmbException);
    }
}
