package mf;

import com.jojo.tv.R;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

/* JADX WARN: Enum visitor error
jadx.core.utils.exceptions.JadxRuntimeException: Can't remove SSA var: r0v1 mf.b[], still in use, count: 1, list:
  (r0v1 mf.b[]) from 0x0055: INVOKE (r0v1 mf.b[]) STATIC call: kotlin.enums.EnumEntriesKt.enumEntries(java.lang.Enum[]):kotlin.enums.EnumEntries A[MD:<E extends java.lang.Enum<E>>:(E extends java.lang.Enum<E>[]):kotlin.enums.EnumEntries<E extends java.lang.Enum<E>> (m), WRAPPED] (LINE:86)
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
/* JADX INFO: loaded from: classes.dex */
public final class b {
    ORIGINAL(R.string.player_aspect_fit),
    /* JADX INFO: Fake field, exist only in values array */
    FULL_SCREEN(R.string.player_aspect_crop),
    /* JADX INFO: Fake field, exist only in values array */
    STRETCH(R.string.player_aspect_stretch),
    /* JADX INFO: Fake field, exist only in values array */
    SLIGHT_ZOOM(R.string.player_aspect_mode_slight_zoom),
    /* JADX INFO: Fake field, exist only in values array */
    CINEMA_ZOOM(R.string.player_aspect_mode_cinema_zoom),
    /* JADX INFO: Fake field, exist only in values array */
    VERTICAL_STRETCH(R.string.player_aspect_fit_height),
    /* JADX INFO: Fake field, exist only in values array */
    HORIZONTAL_STRETCH(R.string.player_aspect_fit_width);


    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final /* synthetic */ EnumEntries f35726i;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f35727b;

    static {
        f35726i = EnumEntriesKt.enumEntries(bVarArr);
    }

    public b(int i10) {
        super(str, i);
        this.f35727b = i10;
    }

    public static b valueOf(String str) {
        return (b) Enum.valueOf(b.class, str);
    }

    public static b[] values() {
        return (b[]) f35725e.clone();
    }
}
