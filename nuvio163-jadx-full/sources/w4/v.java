package w4;

import android.os.Build;
import android.text.Editable;
import android.text.SpanWatcher;
import android.text.Spannable;
import android.text.TextWatcher;
import java.util.concurrent.atomic.AtomicInteger;

/* JADX INFO: loaded from: classes3.dex */
public final class v implements TextWatcher, SpanWatcher {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final Object f62023b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final AtomicInteger f62024c = new AtomicInteger(0);

    public v(Object obj) {
        this.f62023b = obj;
    }

    public final void a() {
        this.f62024c.incrementAndGet();
    }

    @Override // android.text.TextWatcher
    public final void afterTextChanged(Editable editable) {
        ((TextWatcher) this.f62023b).afterTextChanged(editable);
    }

    public final void b() {
        this.f62024c.decrementAndGet();
    }

    @Override // android.text.TextWatcher
    public final void beforeTextChanged(CharSequence charSequence, int i10, int i11, int i12) {
        ((TextWatcher) this.f62023b).beforeTextChanged(charSequence, i10, i11, i12);
    }

    @Override // android.text.SpanWatcher
    public final void onSpanAdded(Spannable spannable, Object obj, int i10, int i11) {
        if (this.f62024c.get() <= 0 || !(obj instanceof y)) {
            ((SpanWatcher) this.f62023b).onSpanAdded(spannable, obj, i10, i11);
        }
    }

    /* JADX WARN: Code duplicated, block: B:14:0x001c A[PHI: r11
  0x001c: PHI (r11v1 int) = (r11v0 int), (r11v3 int) binds: [B:8:0x0011, B:12:0x0017] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // android.text.SpanWatcher
    public final void onSpanChanged(Spannable spannable, Object obj, int i10, int i11, int i12, int i13) {
        int i14;
        int i15;
        if (this.f62024c.get() <= 0 || !(obj instanceof y)) {
            if (Build.VERSION.SDK_INT >= 28) {
                i14 = i10;
                i15 = i12;
            } else {
                if (i10 > i11) {
                    i10 = 0;
                }
                if (i12 > i13) {
                    i14 = i10;
                    i15 = 0;
                } else {
                    i14 = i10;
                    i15 = i12;
                }
            }
            ((SpanWatcher) this.f62023b).onSpanChanged(spannable, obj, i14, i11, i15, i13);
        }
    }

    @Override // android.text.SpanWatcher
    public final void onSpanRemoved(Spannable spannable, Object obj, int i10, int i11) {
        if (this.f62024c.get() <= 0 || !(obj instanceof y)) {
            ((SpanWatcher) this.f62023b).onSpanRemoved(spannable, obj, i10, i11);
        }
    }

    @Override // android.text.TextWatcher
    public final void onTextChanged(CharSequence charSequence, int i10, int i11, int i12) {
        ((TextWatcher) this.f62023b).onTextChanged(charSequence, i10, i11, i12);
    }
}
