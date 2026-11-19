package n;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.SparseArray;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.core.app.i0;
import com.eclipsesource.v8.Platform;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import k4.u0;
import ld.x5;

/* JADX INFO: loaded from: classes.dex */
public class l implements Menu {

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public static final int[] f39334y = {1, 4, 5, 3, 2, 0};

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final Context f39335a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Resources f39336b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public boolean f39337c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final boolean f39338d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public j f39339e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public final ArrayList f39340f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public final ArrayList f39341g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public boolean f39342h;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final ArrayList f39343i;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public final ArrayList f39344j;
    public boolean k;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public CharSequence f39346m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public Drawable f39347n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public View f39348o;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public n f39355v;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    public boolean f39357x;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public int f39345l = 0;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public boolean f39349p = false;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public boolean f39350q = false;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public boolean f39351r = false;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public boolean f39352s = false;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public final ArrayList f39353t = new ArrayList();

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public final CopyOnWriteArrayList f39354u = new CopyOnWriteArrayList();

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    public boolean f39356w = false;

    public l(Context context) {
        boolean zB;
        boolean z2 = false;
        this.f39335a = context;
        Resources resources = context.getResources();
        this.f39336b = resources;
        this.f39340f = new ArrayList();
        this.f39341g = new ArrayList();
        this.f39342h = true;
        this.f39343i = new ArrayList();
        this.f39344j = new ArrayList();
        this.k = true;
        if (resources.getConfiguration().keyboard != 1) {
            ViewConfiguration viewConfiguration = ViewConfiguration.get(context);
            Method method = u0.f27849a;
            if (Build.VERSION.SDK_INT >= 28) {
                zB = i0.B(viewConfiguration);
            } else {
                Resources resources2 = context.getResources();
                int identifier = resources2.getIdentifier("config_showMenuShortcutsWhenKeyboardPresent", "bool", Platform.ANDROID);
                zB = identifier != 0 && resources2.getBoolean(identifier);
            }
            if (zB) {
                z2 = true;
            }
        }
        this.f39338d = z2;
    }

    public final n a(int i10, int i11, int i12, CharSequence charSequence) {
        int i13;
        int i14 = ((-65536) & i12) >> 16;
        if (i14 < 0 || i14 >= 6) {
            throw new IllegalArgumentException("order does not contain a valid category.");
        }
        int i15 = (f39334y[i14] << 16) | (65535 & i12);
        n nVar = new n(this, i10, i11, i12, i15, charSequence, this.f39345l);
        ArrayList arrayList = this.f39340f;
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            if (((n) arrayList.get(size)).e() <= i15) {
                i13 = size + 1;
                arrayList.add(i13, nVar);
                p(true);
                return nVar;
            }
        }
        i13 = 0;
        arrayList.add(i13, nVar);
        p(true);
        return nVar;
    }

    @Override // android.view.Menu
    public final MenuItem add(CharSequence charSequence) {
        return a(0, 0, 0, charSequence);
    }

    @Override // android.view.Menu
    public final int addIntentOptions(int i10, int i11, int i12, ComponentName componentName, Intent[] intentArr, Intent intent, int i13, MenuItem[] menuItemArr) {
        int i14;
        PackageManager packageManager = this.f39335a.getPackageManager();
        List<ResolveInfo> listQueryIntentActivityOptions = packageManager.queryIntentActivityOptions(componentName, intentArr, intent, 0);
        int size = listQueryIntentActivityOptions != null ? listQueryIntentActivityOptions.size() : 0;
        if ((i13 & 1) == 0) {
            removeGroup(i10);
        }
        for (int i15 = 0; i15 < size; i15++) {
            ResolveInfo resolveInfo = listQueryIntentActivityOptions.get(i15);
            int i16 = resolveInfo.specificIndex;
            Intent intent2 = new Intent(i16 < 0 ? intent : intentArr[i16]);
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            intent2.setComponent(new ComponentName(activityInfo.applicationInfo.packageName, activityInfo.name));
            n nVarA = a(i10, i11, i12, resolveInfo.loadLabel(packageManager));
            nVarA.setIcon(resolveInfo.loadIcon(packageManager));
            nVarA.setIntent(intent2);
            if (menuItemArr != null && (i14 = resolveInfo.specificIndex) >= 0) {
                menuItemArr[i14] = nVarA;
            }
        }
        return size;
    }

    @Override // android.view.Menu
    public final SubMenu addSubMenu(CharSequence charSequence) {
        return addSubMenu(0, 0, 0, charSequence);
    }

    public final void b(x xVar, Context context) {
        this.f39354u.add(new WeakReference(xVar));
        xVar.g(context, this);
        this.k = true;
    }

    public final void c(boolean z2) {
        if (this.f39352s) {
            return;
        }
        this.f39352s = true;
        CopyOnWriteArrayList<WeakReference> copyOnWriteArrayList = this.f39354u;
        for (WeakReference weakReference : copyOnWriteArrayList) {
            x xVar = (x) weakReference.get();
            if (xVar == null) {
                copyOnWriteArrayList.remove(weakReference);
            } else {
                xVar.a(this, z2);
            }
        }
        this.f39352s = false;
    }

    @Override // android.view.Menu
    public final void clear() {
        n nVar = this.f39355v;
        if (nVar != null) {
            d(nVar);
        }
        this.f39340f.clear();
        p(true);
    }

    public final void clearHeader() {
        this.f39347n = null;
        this.f39346m = null;
        this.f39348o = null;
        p(false);
    }

    @Override // android.view.Menu
    public final void close() {
        c(true);
    }

    public boolean d(n nVar) {
        CopyOnWriteArrayList<WeakReference> copyOnWriteArrayList = this.f39354u;
        boolean zF = false;
        if (!copyOnWriteArrayList.isEmpty() && this.f39355v == nVar) {
            w();
            for (WeakReference weakReference : copyOnWriteArrayList) {
                x xVar = (x) weakReference.get();
                if (xVar != null) {
                    zF = xVar.f(nVar);
                    if (zF) {
                        break;
                    }
                } else {
                    copyOnWriteArrayList.remove(weakReference);
                }
            }
            v();
            if (zF) {
                this.f39355v = null;
            }
        }
        return zF;
    }

    public boolean e(l lVar, n nVar) {
        j jVar = this.f39339e;
        return jVar != null && jVar.o(lVar, nVar);
    }

    public boolean f(n nVar) {
        CopyOnWriteArrayList<WeakReference> copyOnWriteArrayList = this.f39354u;
        boolean zE = false;
        if (copyOnWriteArrayList.isEmpty()) {
            return false;
        }
        w();
        for (WeakReference weakReference : copyOnWriteArrayList) {
            x xVar = (x) weakReference.get();
            if (xVar != null) {
                zE = xVar.e(nVar);
                if (zE) {
                    break;
                }
            } else {
                copyOnWriteArrayList.remove(weakReference);
            }
        }
        v();
        if (zE) {
            this.f39355v = nVar;
        }
        return zE;
    }

    @Override // android.view.Menu
    public final MenuItem findItem(int i10) {
        MenuItem menuItemFindItem;
        ArrayList arrayList = this.f39340f;
        int size = arrayList.size();
        for (int i11 = 0; i11 < size; i11++) {
            n nVar = (n) arrayList.get(i11);
            if (nVar.getItemId() == i10) {
                return nVar;
            }
            if (nVar.hasSubMenu() && (menuItemFindItem = ((l) nVar.getSubMenu()).findItem(i10)) != null) {
                return menuItemFindItem;
            }
        }
        return null;
    }

    public final n g(int i10, KeyEvent keyEvent) {
        ArrayList arrayList = this.f39353t;
        arrayList.clear();
        h(arrayList, i10, keyEvent);
        if (arrayList.isEmpty()) {
            return null;
        }
        int metaState = keyEvent.getMetaState();
        KeyCharacterMap.KeyData keyData = new KeyCharacterMap.KeyData();
        keyEvent.getKeyData(keyData);
        int size = arrayList.size();
        if (size == 1) {
            return (n) arrayList.get(0);
        }
        boolean zN = n();
        for (int i11 = 0; i11 < size; i11++) {
            n nVar = (n) arrayList.get(i11);
            char alphabeticShortcut = zN ? nVar.getAlphabeticShortcut() : nVar.getNumericShortcut();
            char[] cArr = keyData.meta;
            if ((alphabeticShortcut == cArr[0] && (metaState & 2) == 0) || ((alphabeticShortcut == cArr[2] && (metaState & 2) != 0) || (zN && alphabeticShortcut == '\b' && i10 == 67))) {
                return nVar;
            }
        }
        return null;
    }

    @Override // android.view.Menu
    public final MenuItem getItem(int i10) {
        return (MenuItem) this.f39340f.get(i10);
    }

    public final void h(List list, int i10, KeyEvent keyEvent) {
        boolean zN = n();
        int modifiers = keyEvent.getModifiers();
        KeyCharacterMap.KeyData keyData = new KeyCharacterMap.KeyData();
        if (keyEvent.getKeyData(keyData) || i10 == 67) {
            ArrayList arrayList = this.f39340f;
            int size = arrayList.size();
            for (int i11 = 0; i11 < size; i11++) {
                n nVar = (n) arrayList.get(i11);
                if (nVar.hasSubMenu()) {
                    ((l) nVar.getSubMenu()).h(list, i10, keyEvent);
                }
                char alphabeticShortcut = zN ? nVar.getAlphabeticShortcut() : nVar.getNumericShortcut();
                if ((modifiers & 69647) == ((zN ? nVar.getAlphabeticModifiers() : nVar.getNumericModifiers()) & 69647) && alphabeticShortcut != 0) {
                    char[] cArr = keyData.meta;
                    if ((alphabeticShortcut == cArr[0] || alphabeticShortcut == cArr[2] || (zN && alphabeticShortcut == '\b' && i10 == 67)) && nVar.isEnabled()) {
                        list.add(nVar);
                    }
                }
            }
        }
    }

    @Override // android.view.Menu
    public final boolean hasVisibleItems() {
        if (this.f39357x) {
            return true;
        }
        ArrayList arrayList = this.f39340f;
        int size = arrayList.size();
        for (int i10 = 0; i10 < size; i10++) {
            if (((n) arrayList.get(i10)).isVisible()) {
                return true;
            }
        }
        return false;
    }

    public final void i() {
        ArrayList arrayListL = l();
        if (this.k) {
            CopyOnWriteArrayList<WeakReference> copyOnWriteArrayList = this.f39354u;
            boolean zH = false;
            for (WeakReference weakReference : copyOnWriteArrayList) {
                x xVar = (x) weakReference.get();
                if (xVar == null) {
                    copyOnWriteArrayList.remove(weakReference);
                } else {
                    zH |= xVar.h();
                }
            }
            ArrayList arrayList = this.f39343i;
            ArrayList arrayList2 = this.f39344j;
            if (zH) {
                arrayList.clear();
                arrayList2.clear();
                int size = arrayListL.size();
                for (int i10 = 0; i10 < size; i10++) {
                    n nVar = (n) arrayListL.get(i10);
                    if (nVar.h()) {
                        arrayList.add(nVar);
                    } else {
                        arrayList2.add(nVar);
                    }
                }
            } else {
                arrayList.clear();
                arrayList2.clear();
                arrayList2.addAll(l());
            }
            this.k = false;
        }
    }

    @Override // android.view.Menu
    public final boolean isShortcutKey(int i10, KeyEvent keyEvent) {
        return g(i10, keyEvent) != null;
    }

    public String j() {
        return "android:menu:actionviewstates";
    }

    public final ArrayList l() {
        boolean z2 = this.f39342h;
        ArrayList arrayList = this.f39341g;
        if (!z2) {
            return arrayList;
        }
        arrayList.clear();
        ArrayList arrayList2 = this.f39340f;
        int size = arrayList2.size();
        for (int i10 = 0; i10 < size; i10++) {
            n nVar = (n) arrayList2.get(i10);
            if (nVar.isVisible()) {
                arrayList.add(nVar);
            }
        }
        this.f39342h = false;
        this.k = true;
        return arrayList;
    }

    public boolean m() {
        return this.f39356w;
    }

    public boolean n() {
        return this.f39337c;
    }

    public boolean o() {
        return this.f39338d;
    }

    public final void p(boolean z2) {
        if (this.f39349p) {
            this.f39350q = true;
            if (z2) {
                this.f39351r = true;
                return;
            }
            return;
        }
        if (z2) {
            this.f39342h = true;
            this.k = true;
        }
        CopyOnWriteArrayList<WeakReference> copyOnWriteArrayList = this.f39354u;
        if (copyOnWriteArrayList.isEmpty()) {
            return;
        }
        w();
        for (WeakReference weakReference : copyOnWriteArrayList) {
            x xVar = (x) weakReference.get();
            if (xVar == null) {
                copyOnWriteArrayList.remove(weakReference);
            } else {
                xVar.d();
            }
        }
        v();
    }

    @Override // android.view.Menu
    public final boolean performIdentifierAction(int i10, int i11) {
        return q(findItem(i10), null, i11);
    }

    @Override // android.view.Menu
    public final boolean performShortcut(int i10, KeyEvent keyEvent, int i11) {
        n nVarG = g(i10, keyEvent);
        boolean zQ = nVarG != null ? q(nVarG, null, i11) : false;
        if ((i11 & 2) != 0) {
            c(true);
        }
        return zQ;
    }

    public final boolean q(MenuItem menuItem, x xVar, int i10) {
        n nVar = (n) menuItem;
        boolean zC = false;
        if (nVar == null || !nVar.isEnabled()) {
            return false;
        }
        boolean zG = nVar.g();
        o oVarA = nVar.a();
        boolean z2 = oVarA != null && oVarA.a();
        if (nVar.f()) {
            boolean zExpandActionView = nVar.expandActionView() | zG;
            if (zExpandActionView) {
                c(true);
            }
            return zExpandActionView;
        }
        if (!nVar.hasSubMenu() && !z2) {
            if ((i10 & 1) == 0) {
                c(true);
            }
            return zG;
        }
        if ((i10 & 4) == 0) {
            c(false);
        }
        if (!nVar.hasSubMenu()) {
            nVar.p(new d0(this.f39335a, this, nVar));
        }
        d0 d0Var = (d0) nVar.getSubMenu();
        if (z2) {
            oVarA.b(d0Var);
        }
        CopyOnWriteArrayList<WeakReference> copyOnWriteArrayList = this.f39354u;
        if (!copyOnWriteArrayList.isEmpty()) {
            zC = xVar != null ? xVar.c(d0Var) : false;
            for (WeakReference weakReference : copyOnWriteArrayList) {
                x xVar2 = (x) weakReference.get();
                if (xVar2 == null) {
                    copyOnWriteArrayList.remove(weakReference);
                } else if (!zC) {
                    zC = xVar2.c(d0Var);
                }
            }
        }
        boolean z10 = zG | zC;
        if (!z10) {
            c(true);
        }
        return z10;
    }

    public final void r(x xVar) {
        CopyOnWriteArrayList<WeakReference> copyOnWriteArrayList = this.f39354u;
        for (WeakReference weakReference : copyOnWriteArrayList) {
            x xVar2 = (x) weakReference.get();
            if (xVar2 == null || xVar2 == xVar) {
                copyOnWriteArrayList.remove(weakReference);
            }
        }
    }

    @Override // android.view.Menu
    public final void removeGroup(int i10) {
        ArrayList arrayList = this.f39340f;
        int size = arrayList.size();
        int i11 = 0;
        int i12 = 0;
        while (true) {
            if (i12 >= size) {
                i12 = -1;
                break;
            } else if (((n) arrayList.get(i12)).getGroupId() == i10) {
                break;
            } else {
                i12++;
            }
        }
        if (i12 >= 0) {
            int size2 = arrayList.size() - i12;
            while (true) {
                int i13 = i11 + 1;
                if (i11 >= size2 || ((n) arrayList.get(i12)).getGroupId() != i10) {
                    break;
                }
                if (i12 >= 0 && i12 < arrayList.size()) {
                    arrayList.remove(i12);
                }
                i11 = i13;
            }
            p(true);
        }
    }

    @Override // android.view.Menu
    public final void removeItem(int i10) {
        ArrayList arrayList = this.f39340f;
        int size = arrayList.size();
        int i11 = 0;
        while (true) {
            if (i11 >= size) {
                i11 = -1;
                break;
            } else if (((n) arrayList.get(i11)).getItemId() == i10) {
                break;
            } else {
                i11++;
            }
        }
        if (i11 < 0 || i11 >= arrayList.size()) {
            return;
        }
        arrayList.remove(i11);
        p(true);
    }

    public final void s(Bundle bundle) {
        MenuItem menuItemFindItem;
        if (bundle == null) {
            return;
        }
        SparseArray<Parcelable> sparseParcelableArray = bundle.getSparseParcelableArray(j());
        int size = this.f39340f.size();
        for (int i10 = 0; i10 < size; i10++) {
            MenuItem item = getItem(i10);
            View actionView = item.getActionView();
            if (actionView != null && actionView.getId() != -1) {
                actionView.restoreHierarchyState(sparseParcelableArray);
            }
            if (item.hasSubMenu()) {
                ((d0) item.getSubMenu()).s(bundle);
            }
        }
        int i11 = bundle.getInt("android:menu:expandedactionview");
        if (i11 <= 0 || (menuItemFindItem = findItem(i11)) == null) {
            return;
        }
        menuItemFindItem.expandActionView();
    }

    @Override // android.view.Menu
    public final void setGroupCheckable(int i10, boolean z2, boolean z10) {
        ArrayList arrayList = this.f39340f;
        int size = arrayList.size();
        for (int i11 = 0; i11 < size; i11++) {
            n nVar = (n) arrayList.get(i11);
            if (nVar.getGroupId() == i10) {
                nVar.n(z10);
                nVar.setCheckable(z2);
            }
        }
    }

    @Override // android.view.Menu
    public void setGroupDividerEnabled(boolean z2) {
        this.f39356w = z2;
    }

    @Override // android.view.Menu
    public final void setGroupEnabled(int i10, boolean z2) {
        ArrayList arrayList = this.f39340f;
        int size = arrayList.size();
        for (int i11 = 0; i11 < size; i11++) {
            n nVar = (n) arrayList.get(i11);
            if (nVar.getGroupId() == i10) {
                nVar.setEnabled(z2);
            }
        }
    }

    @Override // android.view.Menu
    public final void setGroupVisible(int i10, boolean z2) {
        ArrayList arrayList = this.f39340f;
        int size = arrayList.size();
        boolean z10 = false;
        for (int i11 = 0; i11 < size; i11++) {
            n nVar = (n) arrayList.get(i11);
            if (nVar.getGroupId() == i10 && nVar.q(z2)) {
                z10 = true;
            }
        }
        if (z10) {
            p(true);
        }
    }

    @Override // android.view.Menu
    public void setQwertyMode(boolean z2) {
        this.f39337c = z2;
        p(false);
    }

    @Override // android.view.Menu
    public final int size() {
        return this.f39340f.size();
    }

    public final void t(Bundle bundle) {
        int size = this.f39340f.size();
        SparseArray<? extends Parcelable> sparseArray = null;
        for (int i10 = 0; i10 < size; i10++) {
            MenuItem item = getItem(i10);
            View actionView = item.getActionView();
            if (actionView != null && actionView.getId() != -1) {
                if (sparseArray == null) {
                    sparseArray = new SparseArray<>();
                }
                actionView.saveHierarchyState(sparseArray);
                if (item.isActionViewExpanded()) {
                    bundle.putInt("android:menu:expandedactionview", item.getItemId());
                }
            }
            if (item.hasSubMenu()) {
                ((d0) item.getSubMenu()).t(bundle);
            }
        }
        if (sparseArray != null) {
            bundle.putSparseParcelableArray(j(), sparseArray);
        }
    }

    public final void u(int i10, CharSequence charSequence, int i11, Drawable drawable, View view) {
        if (view != null) {
            this.f39348o = view;
            this.f39346m = null;
            this.f39347n = null;
        } else {
            if (i10 > 0) {
                this.f39346m = this.f39336b.getText(i10);
            } else if (charSequence != null) {
                this.f39346m = charSequence;
            }
            if (i11 > 0) {
                this.f39347n = x5.u(i11, this.f39335a);
            } else if (drawable != null) {
                this.f39347n = drawable;
            }
            this.f39348o = null;
        }
        p(false);
    }

    public final void v() {
        this.f39349p = false;
        if (this.f39350q) {
            this.f39350q = false;
            p(this.f39351r);
        }
    }

    public final void w() {
        if (this.f39349p) {
            return;
        }
        this.f39349p = true;
        this.f39350q = false;
        this.f39351r = false;
    }

    @Override // android.view.Menu
    public final MenuItem add(int i10) {
        return a(0, 0, 0, this.f39336b.getString(i10));
    }

    @Override // android.view.Menu
    public final SubMenu addSubMenu(int i10) {
        return addSubMenu(0, 0, 0, this.f39336b.getString(i10));
    }

    @Override // android.view.Menu
    public final MenuItem add(int i10, int i11, int i12, CharSequence charSequence) {
        return a(i10, i11, i12, charSequence);
    }

    @Override // android.view.Menu
    public final SubMenu addSubMenu(int i10, int i11, int i12, CharSequence charSequence) {
        n nVarA = a(i10, i11, i12, charSequence);
        d0 d0Var = new d0(this.f39335a, this, nVarA);
        nVarA.p(d0Var);
        return d0Var;
    }

    @Override // android.view.Menu
    public final MenuItem add(int i10, int i11, int i12, int i13) {
        return a(i10, i11, i12, this.f39336b.getString(i13));
    }

    @Override // android.view.Menu
    public final SubMenu addSubMenu(int i10, int i11, int i12, int i13) {
        return addSubMenu(i10, i11, i12, this.f39336b.getString(i13));
    }

    public l k() {
        return this;
    }
}
