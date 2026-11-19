package androidx.appcompat.widget;

import a.b;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import com.jojo.tv.R;
import o.a;
import o.p2;

/* JADX INFO: loaded from: classes.dex */
public class ActionBarContainer extends FrameLayout {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public boolean f1978b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public View f1979c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public View f1980e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public Drawable f1981i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public Drawable f1982m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public Drawable f1983n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final boolean f1984o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public boolean f1985p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public final int f1986q;

    public ActionBarContainer(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setBackground(new a(this));
        TypedArray typedArrayObtainStyledAttributes = context.obtainStyledAttributes(attributeSet, i.a.f23515a);
        boolean z2 = false;
        this.f1981i = typedArrayObtainStyledAttributes.getDrawable(0);
        this.f1982m = typedArrayObtainStyledAttributes.getDrawable(2);
        this.f1986q = typedArrayObtainStyledAttributes.getDimensionPixelSize(13, -1);
        if (getId() == R.id.split_action_bar) {
            this.f1984o = true;
            this.f1983n = typedArrayObtainStyledAttributes.getDrawable(1);
        }
        typedArrayObtainStyledAttributes.recycle();
        if (!this.f1984o ? !(this.f1981i != null || this.f1982m != null) : this.f1983n == null) {
            z2 = true;
        }
        setWillNotDraw(z2);
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.f1981i;
        if (drawable != null && drawable.isStateful()) {
            this.f1981i.setState(getDrawableState());
        }
        Drawable drawable2 = this.f1982m;
        if (drawable2 != null && drawable2.isStateful()) {
            this.f1982m.setState(getDrawableState());
        }
        Drawable drawable3 = this.f1983n;
        if (drawable3 == null || !drawable3.isStateful()) {
            return;
        }
        this.f1983n.setState(getDrawableState());
    }

    public View getTabContainer() {
        return null;
    }

    @Override // android.view.ViewGroup, android.view.View
    public final void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.f1981i;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
        Drawable drawable2 = this.f1982m;
        if (drawable2 != null) {
            drawable2.jumpToCurrentState();
        }
        Drawable drawable3 = this.f1983n;
        if (drawable3 != null) {
            drawable3.jumpToCurrentState();
        }
    }

    @Override // android.view.View
    public final void onFinishInflate() {
        super.onFinishInflate();
        this.f1979c = findViewById(R.id.action_bar);
        this.f1980e = findViewById(R.id.action_context_bar);
    }

    @Override // android.view.View
    public final boolean onHoverEvent(MotionEvent motionEvent) {
        super.onHoverEvent(motionEvent);
        return true;
    }

    @Override // android.view.ViewGroup
    public final boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return this.f1978b || super.onInterceptTouchEvent(motionEvent);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public final void onLayout(boolean z2, int i10, int i11, int i12, int i13) {
        super.onLayout(z2, i10, i11, i12, i13);
        boolean z10 = true;
        if (this.f1984o) {
            Drawable drawable = this.f1983n;
            if (drawable != null) {
                drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            } else {
                z10 = false;
            }
        } else {
            if (this.f1981i == null) {
                z10 = false;
            } else if (this.f1979c.getVisibility() == 0) {
                this.f1981i.setBounds(this.f1979c.getLeft(), this.f1979c.getTop(), this.f1979c.getRight(), this.f1979c.getBottom());
            } else {
                View view = this.f1980e;
                if (view == null || view.getVisibility() != 0) {
                    this.f1981i.setBounds(0, 0, 0, 0);
                } else {
                    this.f1981i.setBounds(this.f1980e.getLeft(), this.f1980e.getTop(), this.f1980e.getRight(), this.f1980e.getBottom());
                }
            }
            this.f1985p = false;
        }
        if (z10) {
            invalidate();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    public final void onMeasure(int i10, int i11) {
        int i12;
        if (this.f1979c == null && View.MeasureSpec.getMode(i11) == Integer.MIN_VALUE && (i12 = this.f1986q) >= 0) {
            i11 = View.MeasureSpec.makeMeasureSpec(Math.min(i12, View.MeasureSpec.getSize(i11)), Integer.MIN_VALUE);
        }
        super.onMeasure(i10, i11);
        if (this.f1979c == null) {
            return;
        }
        View.MeasureSpec.getMode(i11);
    }

    @Override // android.view.View
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        super.onTouchEvent(motionEvent);
        return true;
    }

    public void setPrimaryBackground(Drawable drawable) {
        Drawable drawable2 = this.f1981i;
        if (drawable2 != null) {
            drawable2.setCallback(null);
            unscheduleDrawable(this.f1981i);
        }
        this.f1981i = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            View view = this.f1979c;
            if (view != null) {
                this.f1981i.setBounds(view.getLeft(), this.f1979c.getTop(), this.f1979c.getRight(), this.f1979c.getBottom());
            }
        }
        boolean z2 = false;
        if (!this.f1984o ? !(this.f1981i != null || this.f1982m != null) : this.f1983n == null) {
            z2 = true;
        }
        setWillNotDraw(z2);
        invalidate();
        b.L(this);
    }

    public void setSplitBackground(Drawable drawable) {
        Drawable drawable2;
        Drawable drawable3 = this.f1983n;
        if (drawable3 != null) {
            drawable3.setCallback(null);
            unscheduleDrawable(this.f1983n);
        }
        this.f1983n = drawable;
        boolean z2 = this.f1984o;
        boolean z10 = false;
        if (drawable != null) {
            drawable.setCallback(this);
            if (z2 && (drawable2 = this.f1983n) != null) {
                drawable2.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            }
        }
        if (!z2 ? !(this.f1981i != null || this.f1982m != null) : this.f1983n == null) {
            z10 = true;
        }
        setWillNotDraw(z10);
        invalidate();
        b.L(this);
    }

    public void setStackedBackground(Drawable drawable) {
        Drawable drawable2 = this.f1982m;
        if (drawable2 != null) {
            drawable2.setCallback(null);
            unscheduleDrawable(this.f1982m);
        }
        this.f1982m = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            if (this.f1985p && this.f1982m != null) {
                throw null;
            }
        }
        boolean z2 = false;
        if (!this.f1984o ? !(this.f1981i != null || this.f1982m != null) : this.f1983n == null) {
            z2 = true;
        }
        setWillNotDraw(z2);
        invalidate();
        b.L(this);
    }

    public void setTransitioning(boolean z2) {
        this.f1978b = z2;
        setDescendantFocusability(z2 ? 393216 : 262144);
    }

    @Override // android.view.View
    public void setVisibility(int i10) {
        super.setVisibility(i10);
        boolean z2 = i10 == 0;
        Drawable drawable = this.f1981i;
        if (drawable != null) {
            drawable.setVisible(z2, false);
        }
        Drawable drawable2 = this.f1982m;
        if (drawable2 != null) {
            drawable2.setVisible(z2, false);
        }
        Drawable drawable3 = this.f1983n;
        if (drawable3 != null) {
            drawable3.setVisible(z2, false);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public final ActionMode startActionModeForChild(View view, ActionMode.Callback callback) {
        return null;
    }

    @Override // android.view.View
    public final boolean verifyDrawable(Drawable drawable) {
        Drawable drawable2 = this.f1981i;
        boolean z2 = this.f1984o;
        if (drawable == drawable2 && !z2) {
            return true;
        }
        if (drawable == this.f1982m && this.f1985p) {
            return true;
        }
        return (drawable == this.f1983n && z2) || super.verifyDrawable(drawable);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public final ActionMode startActionModeForChild(View view, ActionMode.Callback callback, int i10) {
        if (i10 != 0) {
            return super.startActionModeForChild(view, callback, i10);
        }
        return null;
    }

    public void setTabContainer(p2 p2Var) {
    }
}
