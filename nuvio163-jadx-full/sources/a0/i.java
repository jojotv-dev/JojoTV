package a0;

import android.util.Log;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import java.util.HashMap;
import kotlin.ResultKt;
import kotlin.jvm.internal.Ref;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class i {
    public static void A(String str, String str2, String str3) {
        Log.w(str3, str + str2);
    }

    public static void B(StringBuilder sb2, String str, String str2) {
        sb2.append(str);
        Log.w(str2, sb2.toString());
    }

    public static StringBuilder C(String str, String str2, String str3, String str4, String str5) {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(str2);
        sb2.append(str3);
        sb2.append(str4);
        sb2.append(str5);
        return sb2;
    }

    public static void D(String str, String str2, String str3) {
        androidx.media3.common.util.Log.w(str3, str + str2);
    }

    public static int a(int i10, int i11, int i12) {
        return androidx.datastore.preferences.protobuf.n.r(i10) + i11 + i12;
    }

    public static int b(int i10, int i11, int i12, int i13) {
        return androidx.datastore.preferences.protobuf.n.s(i10) + i11 + i12 + i13;
    }

    public static int c(d2.c1 c1Var, int i10, int i11) {
        return (c1Var.hashCode() + i10) * i11;
    }

    public static String d(int i10, String str) {
        return i10 + str;
    }

    public static String e(int i10, String str, String str2) {
        return str + str2 + i10;
    }

    public static String f(int i10, String str, StringBuilder sb2) {
        sb2.append(str);
        sb2.append(i10);
        return sb2.toString();
    }

    public static String g(long j10, String str) {
        return j10 + str;
    }

    public static String h(long j10, String str, StringBuilder sb2) {
        sb2.append(j10);
        sb2.append(str);
        return sb2.toString();
    }

    public static String i(RecyclerView recyclerView, StringBuilder sb2) {
        sb2.append(recyclerView.y());
        return sb2.toString();
    }

    public static String j(Class cls, String str) {
        return str + cls;
    }

    public static String k(String str, int i10) {
        return str + i10;
    }

    public static String l(String str, Fragment fragment, String str2) {
        return str + fragment + str2;
    }

    public static String m(String str, Class cls, String str2) {
        return str + cls + str2;
    }

    public static String n(String str, String str2, String str3) {
        return str + str2 + str3;
    }

    public static String o(String str, String str2, String str3, String str4) {
        return str + str2 + str3 + str4;
    }

    public static String p(String str, String str2, String str3, String str4, String str5) {
        return str + str2 + str3 + str4 + str5;
    }

    public static String q(StringBuilder sb2, String str, String str2, String str3, String str4) {
        sb2.append(str);
        sb2.append(str2);
        sb2.append(str3);
        sb2.append(str4);
        return sb2.toString();
    }

    public static StringBuilder r(int i10, String str, String str2, String str3, String str4) {
        StringBuilder sb2 = new StringBuilder(str);
        sb2.append(str2);
        sb2.append(str3);
        sb2.append(i10);
        sb2.append(str4);
        return sb2;
    }

    public static StringBuilder s(String str, String str2) {
        StringBuilder sb2 = new StringBuilder();
        sb2.append(str);
        sb2.append(str2);
        return sb2;
    }

    public static StringBuilder t(String str, String str2, String str3, String str4, String str5) {
        StringBuilder sb2 = new StringBuilder(str);
        sb2.append(str2);
        sb2.append(str3);
        sb2.append(str4);
        sb2.append(str5);
        return sb2;
    }

    public static Ref.ObjectRef u(Object obj) {
        ResultKt.throwOnFailure(obj);
        return new Ref.ObjectRef();
    }

    public static void v(int i10, int i11, String str, String str2, StringBuilder sb2) {
        sb2.append(i10);
        sb2.append(str);
        sb2.append(i11);
        sb2.append(str2);
    }

    public static void w(int i10, String str, String str2) {
        androidx.media3.common.util.Log.w(str2, str + i10);
    }

    public static void x(int i10, String str, String str2, String str3, StringBuilder sb2) {
        sb2.append(str);
        sb2.append(str2);
        sb2.append(i10);
        sb2.append(str3);
    }

    public static void y(int i10, HashMap map, String str, int i11, String str2) {
        map.put(str, Integer.valueOf(i10));
        map.put(str2, Integer.valueOf(i11));
    }

    public static void z(long j10, String str, String str2, StringBuilder sb2) {
        sb2.append(str);
        sb2.append(j10);
        sb2.append(str2);
    }
}
