package n;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.ActionProvider;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import java.util.ArrayList;

/* JADX INFO: loaded from: classes3.dex */
public final class n implements e4.a {
    public o A;
    public MenuItem.OnActionExpandListener B;

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final int f39361a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final int f39362b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f39363c;

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public final int f39364d;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public CharSequence f39365e;

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public CharSequence f39366f;

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public Intent f39367g;

    /* JADX INFO: renamed from: h, reason: collision with root package name */
    public char f39368h;

    /* JADX INFO: renamed from: j, reason: collision with root package name */
    public char f39370j;

    /* JADX INFO: renamed from: l, reason: collision with root package name */
    public Drawable f39371l;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final l f39373n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public d0 f39374o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public MenuItem.OnMenuItemClickListener f39375p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public CharSequence f39376q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public CharSequence f39377r;

    /* JADX INFO: renamed from: y, reason: collision with root package name */
    public int f39384y;

    /* JADX INFO: renamed from: z, reason: collision with root package name */
    public View f39385z;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f39369i = 4096;
    public int k = 4096;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public int f39372m = 0;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public ColorStateList f39378s = null;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public PorterDuff.Mode f39379t = null;

    /* JADX INFO: renamed from: u, reason: collision with root package name */
    public boolean f39380u = false;

    /* JADX INFO: renamed from: v, reason: collision with root package name */
    public boolean f39381v = false;

    /* JADX INFO: renamed from: w, reason: collision with root package name */
    public boolean f39382w = false;

    /* JADX INFO: renamed from: x, reason: collision with root package name */
    public int f39383x = 16;
    public boolean C = false;

    public n(l lVar, int i10, int i11, int i12, int i13, CharSequence charSequence, int i14) {
        this.f39373n = lVar;
        this.f39361a = i11;
        this.f39362b = i10;
        this.f39363c = i12;
        this.f39364d = i13;
        this.f39365e = charSequence;
        this.f39384y = i14;
    }

    public static void c(String str, int i10, int i11, StringBuilder sb2) {
        if ((i10 & i11) == i11) {
            sb2.append(str);
        }
    }

    @Override // e4.a
    public final o a() {
        return this.A;
    }

    @Override // e4.a
    public final e4.a b(o oVar) {
        this.f39385z = null;
        this.A = oVar;
        this.f39373n.p(true);
        o oVar2 = this.A;
        if (oVar2 != null) {
            oVar2.f39386a = new a3.h(this, 14);
            oVar2.f39387b.setVisibilityListener(oVar2);
        }
        return this;
    }

    @Override // android.view.MenuItem
    public final boolean collapseActionView() {
        if ((this.f39384y & 8) == 0) {
            return false;
        }
        if (this.f39385z == null) {
            return true;
        }
        MenuItem.OnActionExpandListener onActionExpandListener = this.B;
        if (onActionExpandListener == null || onActionExpandListener.onMenuItemActionCollapse(this)) {
            return this.f39373n.d(this);
        }
        return false;
    }

    public final Drawable d(Drawable drawable) {
        if (drawable != null && this.f39382w && (this.f39380u || this.f39381v)) {
            drawable = drawable.mutate();
            if (this.f39380u) {
                drawable.setTintList(this.f39378s);
            }
            if (this.f39381v) {
                drawable.setTintMode(this.f39379t);
            }
            this.f39382w = false;
        }
        return drawable;
    }

    public final int e() {
        return this.f39364d;
    }

    @Override // android.view.MenuItem
    public final boolean expandActionView() {
        if (!f()) {
            return false;
        }
        MenuItem.OnActionExpandListener onActionExpandListener = this.B;
        if (onActionExpandListener == null || onActionExpandListener.onMenuItemActionExpand(this)) {
            return this.f39373n.f(this);
        }
        return false;
    }

    public final boolean f() {
        o oVar;
        if ((this.f39384y & 8) != 0) {
            if (this.f39385z == null && (oVar = this.A) != null) {
                this.f39385z = oVar.f39387b.onCreateActionView(this);
            }
            if (this.f39385z != null) {
                return true;
            }
        }
        return false;
    }

    public final boolean g() {
        MenuItem.OnMenuItemClickListener onMenuItemClickListener = this.f39375p;
        if (onMenuItemClickListener == null || !onMenuItemClickListener.onMenuItemClick(this)) {
            l lVar = this.f39373n;
            if (!lVar.e(lVar, this)) {
                Intent intent = this.f39367g;
                if (intent != null) {
                    try {
                        lVar.f39335a.startActivity(intent);
                        return true;
                    } catch (ActivityNotFoundException e10) {
                        Log.e("MenuItemImpl", "Can't find activity to handle intent; ignoring", e10);
                    }
                }
                o oVar = this.A;
                if (oVar == null || !oVar.f39387b.onPerformDefaultAction()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override // android.view.MenuItem
    public final ActionProvider getActionProvider() {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.getActionProvider()");
    }

    @Override // android.view.MenuItem
    public final View getActionView() {
        View view = this.f39385z;
        if (view != null) {
            return view;
        }
        o oVar = this.A;
        if (oVar == null) {
            return null;
        }
        View viewOnCreateActionView = oVar.f39387b.onCreateActionView(this);
        this.f39385z = viewOnCreateActionView;
        return viewOnCreateActionView;
    }

    @Override // e4.a, android.view.MenuItem
    public final int getAlphabeticModifiers() {
        return this.k;
    }

    @Override // android.view.MenuItem
    public final char getAlphabeticShortcut() {
        return this.f39370j;
    }

    @Override // e4.a, android.view.MenuItem
    public final CharSequence getContentDescription() {
        return this.f39376q;
    }

    @Override // android.view.MenuItem
    public final int getGroupId() {
        return this.f39362b;
    }

    @Override // android.view.MenuItem
    public final Drawable getIcon() {
        Drawable drawable = this.f39371l;
        if (drawable != null) {
            return d(drawable);
        }
        int i10 = this.f39372m;
        if (i10 == 0) {
            return null;
        }
        Drawable drawableV = w6.g.v(i10, this.f39373n.f39335a);
        this.f39372m = 0;
        this.f39371l = drawableV;
        return d(drawableV);
    }

    @Override // e4.a, android.view.MenuItem
    public final ColorStateList getIconTintList() {
        return this.f39378s;
    }

    @Override // e4.a, android.view.MenuItem
    public final PorterDuff.Mode getIconTintMode() {
        return this.f39379t;
    }

    @Override // android.view.MenuItem
    public final Intent getIntent() {
        return this.f39367g;
    }

    @Override // android.view.MenuItem
    public final int getItemId() {
        return this.f39361a;
    }

    @Override // android.view.MenuItem
    public final ContextMenu.ContextMenuInfo getMenuInfo() {
        return null;
    }

    @Override // e4.a, android.view.MenuItem
    public final int getNumericModifiers() {
        return this.f39369i;
    }

    @Override // android.view.MenuItem
    public final char getNumericShortcut() {
        return this.f39368h;
    }

    @Override // android.view.MenuItem
    public final int getOrder() {
        return this.f39363c;
    }

    @Override // android.view.MenuItem
    public final SubMenu getSubMenu() {
        return this.f39374o;
    }

    @Override // android.view.MenuItem
    public final CharSequence getTitle() {
        return this.f39365e;
    }

    @Override // android.view.MenuItem
    public final CharSequence getTitleCondensed() {
        CharSequence charSequence = this.f39366f;
        return charSequence != null ? charSequence : this.f39365e;
    }

    @Override // e4.a, android.view.MenuItem
    public final CharSequence getTooltipText() {
        return this.f39377r;
    }

    public final boolean h() {
        return (this.f39383x & 32) == 32;
    }

    @Override // android.view.MenuItem
    public final boolean hasSubMenu() {
        return this.f39374o != null;
    }

    public final boolean i() {
        return (this.f39383x & 4) != 0;
    }

    @Override // android.view.MenuItem
    public final boolean isActionViewExpanded() {
        return this.C;
    }

    @Override // android.view.MenuItem
    public final boolean isCheckable() {
        return (this.f39383x & 1) == 1;
    }

    @Override // android.view.MenuItem
    public final boolean isChecked() {
        return (this.f39383x & 2) == 2;
    }

    @Override // android.view.MenuItem
    public final boolean isEnabled() {
        return (this.f39383x & 16) != 0;
    }

    @Override // android.view.MenuItem
    public final boolean isVisible() {
        o oVar = this.A;
        if (oVar == null || !oVar.f39387b.overridesItemVisibility()) {
            return (this.f39383x & 8) == 0;
        }
        return (this.f39383x & 8) == 0 && this.A.f39387b.isVisible();
    }

    public final boolean j() {
        return (this.f39384y & 1) == 1;
    }

    public final boolean k() {
        return (this.f39384y & 2) == 2;
    }

    public final void l(boolean z2) {
        this.C = z2;
        this.f39373n.p(false);
    }

    public final void m(boolean z2) {
        int i10 = this.f39383x;
        int i11 = (z2 ? 2 : 0) | (i10 & (-3));
        this.f39383x = i11;
        if (i10 != i11) {
            this.f39373n.p(false);
        }
    }

    public final void n(boolean z2) {
        this.f39383x = (z2 ? 4 : 0) | (this.f39383x & (-5));
    }

    public final void o(boolean z2) {
        if (z2) {
            this.f39383x |= 32;
        } else {
            this.f39383x &= -33;
        }
    }

    public final void p(d0 d0Var) {
        this.f39374o = d0Var;
        d0Var.setHeaderTitle(this.f39365e);
    }

    public final boolean q(boolean z2) {
        int i10 = this.f39383x;
        int i11 = (z2 ? 0 : 8) | (i10 & (-9));
        this.f39383x = i11;
        return i10 != i11;
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionProvider(ActionProvider actionProvider) {
        throw new UnsupportedOperationException("This is not supported, use MenuItemCompat.setActionProvider()");
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionView(View view) {
        int i10;
        this.f39385z = view;
        this.A = null;
        if (view != null && view.getId() == -1 && (i10 = this.f39361a) > 0) {
            view.setId(i10);
        }
        l lVar = this.f39373n;
        lVar.k = true;
        lVar.p(true);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setAlphabeticShortcut(char c5) {
        if (this.f39370j == c5) {
            return this;
        }
        this.f39370j = Character.toLowerCase(c5);
        this.f39373n.p(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setCheckable(boolean z2) {
        int i10 = this.f39383x;
        int i11 = (z2 ? 1 : 0) | (i10 & (-2));
        this.f39383x = i11;
        if (i10 != i11) {
            this.f39373n.p(false);
        }
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setChecked(boolean z2) {
        if ((this.f39383x & 4) == 0) {
            m(z2);
            return this;
        }
        l lVar = this.f39373n;
        lVar.getClass();
        int groupId = getGroupId();
        ArrayList arrayList = lVar.f39340f;
        int size = arrayList.size();
        lVar.w();
        for (int i10 = 0; i10 < size; i10++) {
            n nVar = (n) arrayList.get(i10);
            if (nVar.getGroupId() == groupId && nVar.i() && nVar.isCheckable()) {
                nVar.m(nVar == this);
            }
        }
        lVar.v();
        return this;
    }

    @Override // android.view.MenuItem
    public final /* bridge */ /* synthetic */ MenuItem setContentDescription(CharSequence charSequence) {
        setContentDescription(charSequence);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setEnabled(boolean z2) {
        if (z2) {
            this.f39383x |= 16;
        } else {
            this.f39383x &= -17;
        }
        this.f39373n.p(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIcon(Drawable drawable) {
        this.f39372m = 0;
        this.f39371l = drawable;
        this.f39382w = true;
        this.f39373n.p(false);
        return this;
    }

    @Override // e4.a, android.view.MenuItem
    public final MenuItem setIconTintList(ColorStateList colorStateList) {
        this.f39378s = colorStateList;
        this.f39380u = true;
        this.f39382w = true;
        this.f39373n.p(false);
        return this;
    }

    @Override // e4.a, android.view.MenuItem
    public final MenuItem setIconTintMode(PorterDuff.Mode mode) {
        this.f39379t = mode;
        this.f39381v = true;
        this.f39382w = true;
        this.f39373n.p(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIntent(Intent intent) {
        this.f39367g = intent;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setNumericShortcut(char c5) {
        if (this.f39368h == c5) {
            return this;
        }
        this.f39368h = c5;
        this.f39373n.p(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setOnActionExpandListener(MenuItem.OnActionExpandListener onActionExpandListener) {
        this.B = onActionExpandListener;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setOnMenuItemClickListener(MenuItem.OnMenuItemClickListener onMenuItemClickListener) {
        this.f39375p = onMenuItemClickListener;
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setShortcut(char c5, char c10) {
        this.f39368h = c5;
        this.f39370j = Character.toLowerCase(c10);
        this.f39373n.p(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final void setShowAsAction(int i10) {
        int i11 = i10 & 3;
        if (i11 != 0 && i11 != 1 && i11 != 2) {
            throw new IllegalArgumentException("SHOW_AS_ACTION_ALWAYS, SHOW_AS_ACTION_IF_ROOM, and SHOW_AS_ACTION_NEVER are mutually exclusive.");
        }
        this.f39384y = i10;
        l lVar = this.f39373n;
        lVar.k = true;
        lVar.p(true);
    }

    @Override // android.view.MenuItem
    public final MenuItem setShowAsActionFlags(int i10) {
        setShowAsAction(i10);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitle(CharSequence charSequence) {
        this.f39365e = charSequence;
        this.f39373n.p(false);
        d0 d0Var = this.f39374o;
        if (d0Var != null) {
            d0Var.setHeaderTitle(charSequence);
        }
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitleCondensed(CharSequence charSequence) {
        this.f39366f = charSequence;
        this.f39373n.p(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final /* bridge */ /* synthetic */ MenuItem setTooltipText(CharSequence charSequence) {
        setTooltipText(charSequence);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setVisible(boolean z2) {
        if (q(z2)) {
            l lVar = this.f39373n;
            lVar.f39342h = true;
            lVar.p(true);
        }
        return this;
    }

    public final String toString() {
        CharSequence charSequence = this.f39365e;
        if (charSequence != null) {
            return charSequence.toString();
        }
        return null;
    }

    @Override // e4.a, android.view.MenuItem
    public final e4.a setContentDescription(CharSequence charSequence) {
        this.f39376q = charSequence;
        this.f39373n.p(false);
        return this;
    }

    @Override // e4.a, android.view.MenuItem
    public final e4.a setTooltipText(CharSequence charSequence) {
        this.f39377r = charSequence;
        this.f39373n.p(false);
        return this;
    }

    @Override // e4.a, android.view.MenuItem
    public final MenuItem setAlphabeticShortcut(char c5, int i10) {
        if (this.f39370j == c5 && this.k == i10) {
            return this;
        }
        this.f39370j = Character.toLowerCase(c5);
        this.k = KeyEvent.normalizeMetaState(i10);
        this.f39373n.p(false);
        return this;
    }

    @Override // e4.a, android.view.MenuItem
    public final MenuItem setNumericShortcut(char c5, int i10) {
        if (this.f39368h == c5 && this.f39369i == i10) {
            return this;
        }
        this.f39368h = c5;
        this.f39369i = KeyEvent.normalizeMetaState(i10);
        this.f39373n.p(false);
        return this;
    }

    @Override // e4.a, android.view.MenuItem
    public final MenuItem setShortcut(char c5, char c10, int i10, int i11) {
        this.f39368h = c5;
        this.f39369i = KeyEvent.normalizeMetaState(i10);
        this.f39370j = Character.toLowerCase(c10);
        this.k = KeyEvent.normalizeMetaState(i11);
        this.f39373n.p(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setIcon(int i10) {
        this.f39371l = null;
        this.f39372m = i10;
        this.f39382w = true;
        this.f39373n.p(false);
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setTitle(int i10) {
        setTitle(this.f39373n.f39335a.getString(i10));
        return this;
    }

    @Override // android.view.MenuItem
    public final MenuItem setActionView(int i10) {
        int i11;
        l lVar = this.f39373n;
        Context context = lVar.f39335a;
        View viewInflate = LayoutInflater.from(context).inflate(i10, (ViewGroup) new LinearLayout(context), false);
        this.f39385z = viewInflate;
        this.A = null;
        if (viewInflate != null && viewInflate.getId() == -1 && (i11 = this.f39361a) > 0) {
            viewInflate.setId(i11);
        }
        lVar.k = true;
        lVar.p(true);
        return this;
    }
}
