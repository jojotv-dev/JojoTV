package o;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RatingBar;
import com.jojo.tv.R;

/* JADX INFO: loaded from: classes3.dex */
public final class e0 extends RatingBar {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final c0 f43132b;

    public e0(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R.attr.ratingBarStyle);
        t2.a(getContext(), this);
        c0 c0Var = new c0(this);
        this.f43132b = c0Var;
        c0Var.a(attributeSet, R.attr.ratingBarStyle);
    }

    @Override // android.widget.RatingBar, android.widget.AbsSeekBar, android.widget.ProgressBar, android.view.View
    public final synchronized void onMeasure(int i10, int i11) {
        super.onMeasure(i10, i11);
        Bitmap bitmap = this.f43132b.f43109b;
        if (bitmap != null) {
            setMeasuredDimension(View.resolveSizeAndState(bitmap.getWidth() * getNumStars(), i10, 0), getMeasuredHeight());
        }
    }
}
