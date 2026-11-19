package ao;

import android.content.Context;
import java.util.LinkedList;
import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes5.dex */
public abstract class d extends zn.t {

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public final Context f4146l;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public String f4150p;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final LinkedList f4147m = new LinkedList();

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final LinkedList f4148n = new LinkedList();

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public final int f4155u = 20;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public boolean f4151q = true;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public boolean f4152r = true;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public boolean f4153s = false;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public final int f4154t = 1;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final boolean f4149o = true;

    static {
        Pattern.compile("^[0-9\\.]*$");
    }

    public d(Context context) {
        this.f4146l = context.getApplicationContext();
    }
}
