package k4;

import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import com.jojo.tv.R;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.WeakHashMap;

/* JADX INFO: loaded from: classes3.dex */
public final class s0 {

    /* JADX INFO: renamed from: d, reason: collision with root package name */
    public static final ArrayList f27827d = new ArrayList();

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public WeakHashMap f27828a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public SparseArray f27829b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public WeakReference f27830c;

    public static s0 a(View view) {
        s0 s0Var = (s0) view.getTag(R.id.tag_unhandled_key_event_manager);
        if (s0Var != null) {
            return s0Var;
        }
        s0 s0Var2 = new s0();
        s0Var2.f27828a = null;
        s0Var2.f27829b = null;
        s0Var2.f27830c = null;
        view.setTag(R.id.tag_unhandled_key_event_manager, s0Var2);
        return s0Var2;
    }

    public final boolean b(View view, KeyEvent keyEvent) {
        if (keyEvent.getAction() == 0) {
            WeakHashMap weakHashMap = this.f27828a;
            if (weakHashMap != null) {
                weakHashMap.clear();
            }
            ArrayList arrayList = f27827d;
            if (!arrayList.isEmpty()) {
                synchronized (arrayList) {
                    try {
                        if (this.f27828a == null) {
                            this.f27828a = new WeakHashMap();
                        }
                        for (int size = arrayList.size() - 1; size >= 0; size--) {
                            ArrayList arrayList2 = f27827d;
                            View view2 = (View) ((WeakReference) arrayList2.get(size)).get();
                            if (view2 == null) {
                                arrayList2.remove(size);
                            } else {
                                this.f27828a.put(view2, Boolean.TRUE);
                                for (ViewParent parent = view2.getParent(); parent instanceof View; parent = parent.getParent()) {
                                    this.f27828a.put((View) parent, Boolean.TRUE);
                                }
                            }
                        }
                    } catch (Throwable th2) {
                        throw th2;
                    }
                }
            }
        }
        View viewC = c(view);
        if (keyEvent.getAction() == 0) {
            int keyCode = keyEvent.getKeyCode();
            if (viewC != null && !KeyEvent.isModifierKey(keyCode)) {
                if (this.f27829b == null) {
                    this.f27829b = new SparseArray();
                }
                this.f27829b.put(keyCode, new WeakReference(viewC));
            }
        }
        return viewC != null;
    }

    public final View c(View view) {
        int size;
        WeakHashMap weakHashMap = this.f27828a;
        if (weakHashMap == null || !weakHashMap.containsKey(view)) {
            return null;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int childCount = viewGroup.getChildCount() - 1; childCount >= 0; childCount--) {
                View viewC = c(viewGroup.getChildAt(childCount));
                if (viewC != null) {
                    return viewC;
                }
            }
        }
        ArrayList arrayList = (ArrayList) view.getTag(R.id.tag_unhandled_key_listeners);
        if (arrayList == null || (size = arrayList.size() - 1) < 0) {
            return null;
        }
        arrayList.get(size).getClass();
        throw new ClassCastException();
    }

    public final boolean d(KeyEvent keyEvent) {
        WeakReference weakReference;
        ArrayList arrayList;
        int size;
        int iIndexOfKey;
        WeakReference weakReference2 = this.f27830c;
        if (weakReference2 != null && weakReference2.get() == keyEvent) {
            return false;
        }
        this.f27830c = new WeakReference(keyEvent);
        if (this.f27829b == null) {
            this.f27829b = new SparseArray();
        }
        SparseArray sparseArray = this.f27829b;
        if (keyEvent.getAction() != 1 || (iIndexOfKey = sparseArray.indexOfKey(keyEvent.getKeyCode())) < 0) {
            weakReference = null;
        } else {
            weakReference = (WeakReference) sparseArray.valueAt(iIndexOfKey);
            sparseArray.removeAt(iIndexOfKey);
        }
        if (weakReference == null) {
            weakReference = (WeakReference) sparseArray.get(keyEvent.getKeyCode());
        }
        if (weakReference == null) {
            return false;
        }
        View view = (View) weakReference.get();
        if (view == null || !view.isAttachedToWindow() || (arrayList = (ArrayList) view.getTag(R.id.tag_unhandled_key_listeners)) == null || (size = arrayList.size() - 1) < 0) {
            return true;
        }
        arrayList.get(size).getClass();
        throw new ClassCastException();
    }
}
