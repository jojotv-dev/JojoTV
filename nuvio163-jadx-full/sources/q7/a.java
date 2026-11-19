package q7;

/* JADX INFO: loaded from: classes3.dex */
public abstract class a implements i {
    public abstract boolean a(char c5);

    @Override // q7.i
    public final boolean apply(Object obj) {
        return a(((Character) obj).charValue());
    }
}
