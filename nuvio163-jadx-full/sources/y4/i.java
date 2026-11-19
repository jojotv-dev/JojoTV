package y4;

import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.widget.EditText;
import w4.k;

/* JADX INFO: loaded from: classes3.dex */
public final class i implements TextWatcher {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final EditText f64632b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public h f64633c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public boolean f64634e = true;

    public i(EditText editText) {
        this.f64632b = editText;
    }

    public static void a(EditText editText, int i10) {
        int length;
        if (i10 == 1 && editText != null && editText.isAttachedToWindow()) {
            Editable editableText = editText.getEditableText();
            int selectionStart = Selection.getSelectionStart(editableText);
            int selectionEnd = Selection.getSelectionEnd(editableText);
            k kVarA = k.a();
            if (editableText == null) {
                length = 0;
            } else {
                kVarA.getClass();
                length = editableText.length();
            }
            kVarA.g(0, length, 0, editableText);
            if (selectionStart >= 0 && selectionEnd >= 0) {
                Selection.setSelection(editableText, selectionStart, selectionEnd);
            } else if (selectionStart >= 0) {
                Selection.setSelection(editableText, selectionStart);
            } else if (selectionEnd >= 0) {
                Selection.setSelection(editableText, selectionEnd);
            }
        }
    }

    @Override // android.text.TextWatcher
    public final void onTextChanged(CharSequence charSequence, int i10, int i11, int i12) throws Throwable {
        EditText editText = this.f64632b;
        if (!editText.isInEditMode() && this.f64634e && k.d() && i11 <= i12 && (charSequence instanceof Spannable)) {
            int iC = k.a().c();
            if (iC != 0) {
                if (iC == 1) {
                    k.a().g(i10, i12 + i10, 0, (Spannable) charSequence);
                    return;
                } else if (iC != 3) {
                    return;
                }
            }
            k kVarA = k.a();
            if (this.f64633c == null) {
                this.f64633c = new h(editText);
            }
            kVarA.h(this.f64633c);
        }
    }

    @Override // android.text.TextWatcher
    public final void afterTextChanged(Editable editable) {
    }

    @Override // android.text.TextWatcher
    public final void beforeTextChanged(CharSequence charSequence, int i10, int i11, int i12) {
    }
}
