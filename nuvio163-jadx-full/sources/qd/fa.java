package qd;

import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Enum visitor error
jadx.core.utils.exceptions.JadxRuntimeException: Can't remove SSA var: r0v1 qd.fa[], still in use, count: 1, list:
  (r0v1 qd.fa[]) from 0x001e: INVOKE (r0v1 qd.fa[]) STATIC call: kotlin.enums.EnumEntriesKt.enumEntries(java.lang.Enum[]):kotlin.enums.EnumEntries A[MD:<E extends java.lang.Enum<E>>:(E extends java.lang.Enum<E>[]):kotlin.enums.EnumEntries<E extends java.lang.Enum<E>> (m), WRAPPED] (LINE:31)
	at jadx.core.utils.InsnRemover.removeSsaVar(InsnRemover.java:164)
	at jadx.core.utils.InsnRemover.unbindResult(InsnRemover.java:129)
	at jadx.core.utils.InsnRemover.lambda$unbindInsns$1(InsnRemover.java:101)
	at java.base/java.util.ArrayList.forEach(ArrayList.java:1604)
	at jadx.core.utils.InsnRemover.unbindInsns(InsnRemover.java:100)
	at jadx.core.utils.InsnRemover.removeAllAndUnbind(InsnRemover.java:257)
	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:187)
	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:102)
 */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX INFO: loaded from: classes4.dex */
public final class fa {
    MOVIE("movie"),
    SHOW("show");


    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f46477m;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f46478b;

    static {
        f46477m = EnumEntriesKt.enumEntries(faVarArr);
    }

    public fa(String str) {
        super(str, i);
        this.f46478b = str;
    }

    public static fa valueOf(String str) {
        return (fa) Enum.valueOf(fa.class, str);
    }

    public static fa[] values() {
        return (fa[]) f46476i.clone();
    }

    public final String a() {
        return this.f46478b;
    }
}
