package o;

import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Property;
import android.view.View;
import androidx.appcompat.widget.SwitchCompat;

/* JADX INFO: loaded from: classes3.dex */
public final class r2 extends Property {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f43300a;

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    public /* synthetic */ r2(int i10, String str, Class cls) {
        super(cls, str);
        this.f43300a = i10;
    }

    @Override // android.util.Property
    public final Object get(Object obj) {
        switch (this.f43300a) {
            case 0:
                return Float.valueOf(((SwitchCompat) obj).H);
            case 1:
                return null;
            case 2:
                return null;
            case 3:
                return null;
            case 4:
                return null;
            case 5:
                return null;
            case 6:
                return Float.valueOf(x5.a0.f63428a.e((View) obj));
            default:
                return ((View) obj).getClipBounds();
        }
    }

    @Override // android.util.Property
    public final void set(Object obj, Object obj2) {
        switch (this.f43300a) {
            case 0:
                ((SwitchCompat) obj).setThumbPosition(((Float) obj2).floatValue());
                break;
            case 1:
                x5.d dVar = (x5.d) obj;
                PointF pointF = (PointF) obj2;
                dVar.getClass();
                dVar.f63436a = Math.round(pointF.x);
                int iRound = Math.round(pointF.y);
                dVar.f63437b = iRound;
                int i10 = dVar.f63441f + 1;
                dVar.f63441f = i10;
                if (i10 == dVar.f63442g) {
                    x5.a0.a(dVar.f63440e, dVar.f63436a, iRound, dVar.f63438c, dVar.f63439d);
                    dVar.f63441f = 0;
                    dVar.f63442g = 0;
                }
                break;
            case 2:
                x5.d dVar2 = (x5.d) obj;
                PointF pointF2 = (PointF) obj2;
                dVar2.getClass();
                dVar2.f63438c = Math.round(pointF2.x);
                int iRound2 = Math.round(pointF2.y);
                dVar2.f63439d = iRound2;
                int i11 = dVar2.f63442g + 1;
                dVar2.f63442g = i11;
                if (dVar2.f63441f == i11) {
                    x5.a0.a(dVar2.f63440e, dVar2.f63436a, dVar2.f63437b, dVar2.f63438c, iRound2);
                    dVar2.f63441f = 0;
                    dVar2.f63442g = 0;
                }
                break;
            case 3:
                View view = (View) obj;
                PointF pointF3 = (PointF) obj2;
                x5.a0.a(view, view.getLeft(), view.getTop(), Math.round(pointF3.x), Math.round(pointF3.y));
                break;
            case 4:
                View view2 = (View) obj;
                PointF pointF4 = (PointF) obj2;
                x5.a0.a(view2, Math.round(pointF4.x), Math.round(pointF4.y), view2.getRight(), view2.getBottom());
                break;
            case 5:
                View view3 = (View) obj;
                PointF pointF5 = (PointF) obj2;
                int iRound3 = Math.round(pointF5.x);
                int iRound4 = Math.round(pointF5.y);
                x5.a0.a(view3, iRound3, iRound4, view3.getWidth() + iRound3, view3.getHeight() + iRound4);
                break;
            case 6:
                float fFloatValue = ((Float) obj2).floatValue();
                x5.a0.f63428a.j((View) obj, fFloatValue);
                break;
            default:
                ((View) obj).setClipBounds((Rect) obj2);
                break;
        }
    }
}
