package q2;

/* JADX INFO: loaded from: classes.dex */
public abstract class h0 {
    public static String a(int i10) {
        if (i10 == 1) {
            return "Touch";
        }
        if (i10 == 2) {
            return "Mouse";
        }
        if (i10 != 3) {
            return i10 != 4 ? "Unknown" : "Eraser";
        }
        return "Stylus";
    }
}
