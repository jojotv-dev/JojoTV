package androidx.datastore.preferences.protobuf;

/* JADX WARN: Enum visitor error
jadx.core.utils.exceptions.JadxRuntimeException: Init of enum field 'EF2' uses external variables
	at jadx.core.dex.visitors.EnumVisitor.createEnumFieldByConstructor(EnumVisitor.java:485)
	at jadx.core.dex.visitors.EnumVisitor.processEnumFieldByRegister(EnumVisitor.java:422)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromFilledArray(EnumVisitor.java:351)
	at jadx.core.dex.visitors.EnumVisitor.extractEnumFieldsFromInsn(EnumVisitor.java:284)
	at jadx.core.dex.visitors.EnumVisitor.convertToEnum(EnumVisitor.java:153)
	at jadx.core.dex.visitors.EnumVisitor.visit(EnumVisitor.java:102)
 */
/* JADX WARN: Failed to restore enum class, 'enum' modifier and super class removed */
/* JADX INFO: loaded from: classes.dex */
public class c2 {

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public static final y1 f2441e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public static final z1 f2442i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public static final a2 f2443m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public static final /* synthetic */ c2[] f2444n;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final d2 f2445b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f2446c;

    /* JADX INFO: Fake field, exist only in values array */
    c2 EF0;

    /* JADX INFO: Fake field, exist only in values array */
    c2 EF1;

    /* JADX INFO: Fake field, exist only in values array */
    c2 EF2;

    static {
        c2 c2Var = new c2("DOUBLE", 0, d2.DOUBLE, 1);
        c2 c2Var2 = new c2("FLOAT", 1, d2.FLOAT, 5);
        d2 d2Var = d2.LONG;
        c2 c2Var3 = new c2("INT64", 2, d2Var, 0);
        c2 c2Var4 = new c2("UINT64", 3, d2Var, 0);
        d2 d2Var2 = d2.INT;
        c2 c2Var5 = new c2("INT32", 4, d2Var2, 0);
        c2 c2Var6 = new c2("FIXED64", 5, d2Var, 1);
        c2 c2Var7 = new c2("FIXED32", 6, d2Var2, 5);
        c2 c2Var8 = new c2("BOOL", 7, d2.BOOLEAN, 0);
        y1 y1Var = new y1("STRING", 8, d2.STRING, 2);
        f2441e = y1Var;
        d2 d2Var3 = d2.MESSAGE;
        z1 z1Var = new z1("GROUP", 9, d2Var3, 3);
        f2442i = z1Var;
        a2 a2Var = new a2("MESSAGE", 10, d2Var3, 2);
        f2443m = a2Var;
        f2444n = new c2[]{c2Var, c2Var2, c2Var3, c2Var4, c2Var5, c2Var6, c2Var7, c2Var8, y1Var, z1Var, a2Var, new b2("BYTES", 11, d2.BYTE_STRING, 2), new c2("UINT32", 12, d2Var2, 0), new c2("ENUM", 13, d2.ENUM, 0), new c2("SFIXED32", 14, d2Var2, 5), new c2("SFIXED64", 15, d2Var, 1), new c2("SINT32", 16, d2Var2, 0), new c2("SINT64", 17, d2Var, 0)};
    }

    public c2(String str, int i10, d2 d2Var, int i11) {
        super(str, i10);
        this.f2445b = d2Var;
        this.f2446c = i11;
    }

    public static c2 valueOf(String str) {
        return (c2) Enum.valueOf(c2.class, str);
    }

    public static c2[] values() {
        return (c2[]) f2444n.clone();
    }
}
