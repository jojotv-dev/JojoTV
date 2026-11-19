package ne;

import java.util.List;
import kotlin.jvm.internal.Intrinsics;

/* JADX INFO: loaded from: classes4.dex */
public final class w1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final List f41766a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f41767b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final String f41768c;

    public w1(List games, String str, String hash) {
        Intrinsics.checkNotNullParameter(games, "games");
        Intrinsics.checkNotNullParameter(hash, "hash");
        this.f41766a = games;
        this.f41767b = str;
        this.f41768c = hash;
    }
}
