package o;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import com.jojo.tv.R;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/* JADX INFO: loaded from: classes3.dex */
public class s1 extends ListView {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Rect f43305b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public int f43306c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f43307e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public int f43308i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public int f43309m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public int f43310n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public q1 f43311o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public boolean f43312p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public final boolean f43313q;

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public boolean f43314r;

    /* JADX INFO: renamed from: s, reason: collision with root package name */
    public o4.d f43315s;

    /* JADX INFO: renamed from: t, reason: collision with root package name */
    public androidx.recyclerview.widget.k f43316t;

    public s1(Context context, boolean z2) {
        super(context, null, R.attr.dropDownListViewStyle);
        this.f43305b = new Rect();
        this.f43306c = 0;
        this.f43307e = 0;
        this.f43308i = 0;
        this.f43309m = 0;
        this.f43313q = z2;
        setCacheColorHint(0);
    }

    public final int a(int i10, int i11) {
        int listPaddingTop = getListPaddingTop();
        int listPaddingBottom = getListPaddingBottom();
        int dividerHeight = getDividerHeight();
        Drawable divider = getDivider();
        ListAdapter adapter = getAdapter();
        if (adapter == null) {
            return listPaddingTop + listPaddingBottom;
        }
        int measuredHeight = listPaddingTop + listPaddingBottom;
        if (dividerHeight <= 0 || divider == null) {
            dividerHeight = 0;
        }
        int count = adapter.getCount();
        int i12 = 0;
        View view = null;
        for (int i13 = 0; i13 < count; i13++) {
            int itemViewType = adapter.getItemViewType(i13);
            if (itemViewType != i12) {
                view = null;
                i12 = itemViewType;
            }
            view = adapter.getView(i13, view, this);
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams == null) {
                layoutParams = generateDefaultLayoutParams();
                view.setLayoutParams(layoutParams);
            }
            int i14 = layoutParams.height;
            view.measure(i10, i14 > 0 ? View.MeasureSpec.makeMeasureSpec(i14, 1073741824) : View.MeasureSpec.makeMeasureSpec(0, 0));
            view.forceLayout();
            if (i13 > 0) {
                measuredHeight += dividerHeight;
            }
            measuredHeight += view.getMeasuredHeight();
            if (measuredHeight >= i11) {
                return i11;
            }
        }
        return measuredHeight;
    }

    /* JADX WARN: Code duplicated, block: B:82:0x014c  */
    /* JADX WARN: Code duplicated, block: B:84:0x0162  */
    /* JADX WARN: Code duplicated, block: B:86:0x0167  */
    /* JADX WARN: Code duplicated, block: B:88:0x016b  */
    /* JADX WARN: Code duplicated, block: B:90:0x017d  */
    /* JADX WARN: Code duplicated, block: B:92:0x0181  */
    /* JADX WARN: Code duplicated, block: B:94:0x0185  */
    /* JADX WARN: Code duplicated, block: B:9:0x0015  */
    public final boolean b(MotionEvent motionEvent, int i10) {
        boolean z2;
        boolean zA;
        View childAt;
        View childAt2;
        o4.d dVar;
        int actionMasked = motionEvent.getActionMasked();
        boolean z10 = false;
        if (actionMasked != 1) {
            if (actionMasked == 2) {
                z2 = true;
            } else if (actionMasked != 3) {
                z2 = true;
            } else {
                z2 = false;
            }
            if (z2 || z10) {
                this.f43314r = false;
                setPressed(false);
                drawableStateChanged();
                childAt2 = getChildAt(this.f43310n - getFirstVisiblePosition());
                if (childAt2 != null) {
                    childAt2.setPressed(false);
                }
            }
            if (z2) {
                if (this.f43315s == null) {
                    this.f43315s = new o4.d(this);
                }
                o4.d dVar2 = this.f43315s;
                boolean z11 = dVar2.f43480x;
                dVar2.f43480x = true;
                dVar2.onTouch(this, motionEvent);
            } else {
                dVar = this.f43315s;
                if (dVar != null) {
                    if (dVar.f43480x) {
                        dVar.d();
                    }
                    dVar.f43480x = false;
                }
            }
            return z2;
        }
        z2 = false;
        int iFindPointerIndex = motionEvent.findPointerIndex(i10);
        if (iFindPointerIndex < 0) {
            z2 = false;
        } else {
            int x10 = (int) motionEvent.getX(iFindPointerIndex);
            int y10 = (int) motionEvent.getY(iFindPointerIndex);
            int iPointToPosition = pointToPosition(x10, y10);
            if (iPointToPosition == -1) {
                z10 = true;
            } else {
                View childAt3 = getChildAt(iPointToPosition - getFirstVisiblePosition());
                float f10 = x10;
                float f11 = y10;
                this.f43314r = true;
                int i11 = Build.VERSION.SDK_INT;
                n1.a(this, f10, f11);
                if (!isPressed()) {
                    setPressed(true);
                }
                layoutChildren();
                int i12 = this.f43310n;
                if (i12 != -1 && (childAt = getChildAt(i12 - getFirstVisiblePosition())) != null && childAt != childAt3 && childAt.isPressed()) {
                    childAt.setPressed(false);
                }
                this.f43310n = iPointToPosition;
                n1.a(childAt3, f10 - childAt3.getLeft(), f11 - childAt3.getTop());
                if (!childAt3.isPressed()) {
                    childAt3.setPressed(true);
                }
                Drawable selector = getSelector();
                boolean z12 = (selector == null || iPointToPosition == -1) ? false : true;
                if (z12) {
                    selector.setVisible(false, false);
                }
                int left = childAt3.getLeft();
                int top = childAt3.getTop();
                int right = childAt3.getRight();
                int bottom = childAt3.getBottom();
                Rect rect = this.f43305b;
                rect.set(left, top, right, bottom);
                rect.left -= this.f43306c;
                rect.top -= this.f43307e;
                rect.right += this.f43308i;
                rect.bottom += this.f43309m;
                if (i11 >= 33) {
                    zA = p1.a(this);
                } else {
                    Field field = r1.f43299a;
                    if (field != null) {
                        try {
                            zA = field.getBoolean(this);
                        } catch (IllegalAccessException e10) {
                            e10.printStackTrace();
                            zA = false;
                        }
                    } else {
                        zA = false;
                    }
                }
                if (childAt3.isEnabled() != zA) {
                    boolean z13 = !zA;
                    if (Build.VERSION.SDK_INT >= 33) {
                        p1.b(this, z13);
                    } else {
                        Field field2 = r1.f43299a;
                        if (field2 != null) {
                            try {
                                field2.set(this, Boolean.valueOf(z13));
                            } catch (IllegalAccessException e11) {
                                e11.printStackTrace();
                            }
                        }
                    }
                    if (iPointToPosition != -1) {
                        refreshDrawableState();
                    }
                }
                if (z12) {
                    float fExactCenterX = rect.exactCenterX();
                    float fExactCenterY = rect.exactCenterY();
                    selector.setVisible(getVisibility() == 0, false);
                    selector.setHotspot(fExactCenterX, fExactCenterY);
                }
                Drawable selector2 = getSelector();
                if (selector2 != null && iPointToPosition != -1) {
                    selector2.setHotspot(f10, f11);
                }
                q1 q1Var = this.f43311o;
                if (q1Var != null) {
                    q1Var.f43285c = false;
                }
                refreshDrawableState();
                if (actionMasked == 1) {
                    performItemClick(childAt3, iPointToPosition, getItemIdAtPosition(iPointToPosition));
                }
                z2 = true;
                z10 = false;
            }
        }
        if (z2) {
            this.f43314r = false;
            setPressed(false);
            drawableStateChanged();
            childAt2 = getChildAt(this.f43310n - getFirstVisiblePosition());
            if (childAt2 != null) {
                childAt2.setPressed(false);
            }
        } else {
            this.f43314r = false;
            setPressed(false);
            drawableStateChanged();
            childAt2 = getChildAt(this.f43310n - getFirstVisiblePosition());
            if (childAt2 != null) {
                childAt2.setPressed(false);
            }
        }
        if (z2) {
            if (this.f43315s == null) {
                this.f43315s = new o4.d(this);
            }
            o4.d dVar3 = this.f43315s;
            boolean z14 = dVar3.f43480x;
            dVar3.f43480x = true;
            dVar3.onTouch(this, motionEvent);
        } else {
            dVar = this.f43315s;
            if (dVar != null) {
                if (dVar.f43480x) {
                    dVar.d();
                }
                dVar.f43480x = false;
            }
        }
        return z2;
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.view.ViewGroup, android.view.View
    public final void dispatchDraw(Canvas canvas) {
        Drawable selector;
        Rect rect = this.f43305b;
        if (!rect.isEmpty() && (selector = getSelector()) != null) {
            selector.setBounds(rect);
            selector.draw(canvas);
        }
        super.dispatchDraw(canvas);
    }

    @Override // android.widget.AbsListView, android.view.ViewGroup, android.view.View
    public final void drawableStateChanged() {
        if (this.f43316t != null) {
            return;
        }
        super.drawableStateChanged();
        q1 q1Var = this.f43311o;
        if (q1Var != null) {
            q1Var.f43285c = true;
        }
        Drawable selector = getSelector();
        if (selector != null && this.f43314r && isPressed()) {
            selector.setState(getDrawableState());
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public final boolean hasFocus() {
        return this.f43313q || super.hasFocus();
    }

    @Override // android.view.View
    public final boolean hasWindowFocus() {
        return this.f43313q || super.hasWindowFocus();
    }

    @Override // android.view.View
    public final boolean isFocused() {
        return this.f43313q || super.isFocused();
    }

    @Override // android.view.View
    public final boolean isInTouchMode() {
        return (this.f43313q && this.f43312p) || super.isInTouchMode();
    }

    @Override // android.widget.ListView, android.widget.AbsListView, android.widget.AdapterView, android.view.ViewGroup, android.view.View
    public final void onDetachedFromWindow() {
        this.f43316t = null;
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        int i10 = Build.VERSION.SDK_INT;
        if (i10 < 26) {
            return super.onHoverEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 10 && this.f43316t == null) {
            androidx.recyclerview.widget.k kVar = new androidx.recyclerview.widget.k(this, 12);
            this.f43316t = kVar;
            post(kVar);
        }
        boolean zOnHoverEvent = super.onHoverEvent(motionEvent);
        if (actionMasked != 9 && actionMasked != 7) {
            setSelection(-1);
            return zOnHoverEvent;
        }
        int iPointToPosition = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
        if (iPointToPosition != -1 && iPointToPosition != getSelectedItemPosition()) {
            View childAt = getChildAt(iPointToPosition - getFirstVisiblePosition());
            if (childAt.isEnabled()) {
                requestFocus();
                if (i10 < 30 || !o1.f43267d) {
                    setSelectionFromTop(iPointToPosition, childAt.getTop() - getTop());
                } else {
                    try {
                        o1.f43264a.invoke(this, Integer.valueOf(iPointToPosition), childAt, Boolean.FALSE, -1, -1);
                        o1.f43265b.invoke(this, Integer.valueOf(iPointToPosition));
                        o1.f43266c.invoke(this, Integer.valueOf(iPointToPosition));
                    } catch (IllegalAccessException e10) {
                        e10.printStackTrace();
                    } catch (InvocationTargetException e11) {
                        e11.printStackTrace();
                    }
                }
            }
            Drawable selector = getSelector();
            if (selector != null && this.f43314r && isPressed()) {
                selector.setState(getDrawableState());
            }
        }
        return zOnHoverEvent;
    }

    @Override // android.widget.AbsListView, android.view.View
    public final boolean onTouchEvent(MotionEvent motionEvent) {
        if (motionEvent.getAction() == 0) {
            this.f43310n = pointToPosition((int) motionEvent.getX(), (int) motionEvent.getY());
        }
        androidx.recyclerview.widget.k kVar = this.f43316t;
        if (kVar != null) {
            s1 s1Var = (s1) kVar.f3975c;
            s1Var.f43316t = null;
            s1Var.removeCallbacks(kVar);
        }
        return super.onTouchEvent(motionEvent);
    }

    public void setListSelectionHidden(boolean z2) {
        this.f43312p = z2;
    }

    @Override // android.widget.AbsListView
    public void setSelector(Drawable drawable) {
        q1 q1Var = null;
        if (drawable != null) {
            q1 q1Var2 = new q1();
            Drawable drawable2 = q1Var2.f43284b;
            if (drawable2 != null) {
                drawable2.setCallback(null);
            }
            q1Var2.f43284b = drawable;
            drawable.setCallback(q1Var2);
            q1Var2.f43285c = true;
            q1Var = q1Var2;
        }
        this.f43311o = q1Var;
        super.setSelector(q1Var);
        Rect rect = new Rect();
        if (drawable != null) {
            drawable.getPadding(rect);
        }
        this.f43306c = rect.left;
        this.f43307e = rect.top;
        this.f43308i = rect.right;
        this.f43309m = rect.bottom;
    }
}
