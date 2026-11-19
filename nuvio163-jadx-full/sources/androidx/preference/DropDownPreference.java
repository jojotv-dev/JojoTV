package androidx.preference;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ArrayAdapter;
import com.jojo.tv.R;

/* JADX INFO: loaded from: classes3.dex */
public class DropDownPreference extends ListPreference {

    /* JADX INFO: renamed from: r, reason: collision with root package name */
    public final ArrayAdapter f3792r;

    public DropDownPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet, R.attr.dropdownPreferenceStyle);
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item);
        this.f3792r = arrayAdapter;
        arrayAdapter.clear();
        CharSequence[] charSequenceArr = this.f3793p;
        if (charSequenceArr != null) {
            for (CharSequence charSequence : charSequenceArr) {
                arrayAdapter.add(charSequence.toString());
            }
        }
    }

    @Override // androidx.preference.Preference
    public final void b() {
        ArrayAdapter arrayAdapter = this.f3792r;
        if (arrayAdapter != null) {
            arrayAdapter.notifyDataSetChanged();
        }
    }
}
