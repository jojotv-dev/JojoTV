package re;

import java.util.HashMap;
import java.util.HashSet;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class p {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final HashSet f53182a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final HashMap f53183b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final HashSet f53184c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final HashSet f53185d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final HashMap f53186e;

    public p(HashSet keys, HashMap keysByName, HashSet keysWithoutLogo, HashSet catchupKeys, HashMap keysById) {
        Intrinsics.checkNotNullParameter(keys, "keys");
        Intrinsics.checkNotNullParameter(keysByName, "keysByName");
        Intrinsics.checkNotNullParameter(keysWithoutLogo, "keysWithoutLogo");
        Intrinsics.checkNotNullParameter(catchupKeys, "catchupKeys");
        Intrinsics.checkNotNullParameter(keysById, "keysById");
        this.f53182a = keys;
        this.f53183b = keysByName;
        this.f53184c = keysWithoutLogo;
        this.f53185d = catchupKeys;
        this.f53186e = keysById;
    }
}
