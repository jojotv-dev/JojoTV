package com.liskovsoft.sharedutils.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import com.liskovsoft.sharedutils.R;

/* JADX INFO: loaded from: classes4.dex */
public class MultiChoiceSelectorDialog extends GenericSelectorDialog {
    private final GenericSelectorDialog.MultiDialogSource mDialogSource;

    public MultiChoiceSelectorDialog(Context context, GenericSelectorDialog.MultiDialogSource multiDialogSource, int i10) {
        super(context, multiDialogSource, i10);
        this.mDialogSource = multiDialogSource;
    }

    public static void create(Context context, GenericSelectorDialog.MultiDialogSource multiDialogSource, int i10) {
        new MultiChoiceSelectorDialog(context, multiDialogSource, i10).run();
    }

    @Override // com.liskovsoft.sharedutils.dialogs.GenericSelectorDialog
    public CheckedTextView createDialogItem(LayoutInflater layoutInflater, ViewGroup viewGroup, GenericSelectorDialog.DialogSourceBase.DialogItem dialogItem) {
        return (CheckedTextView) layoutInflater.inflate(R.layout.dialog_check_item_multi, viewGroup, false);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        GenericSelectorDialog.DialogSourceBase.DialogItem dialogItem = (GenericSelectorDialog.DialogSourceBase.DialogItem) view.getTag();
        CheckedTextView checkedTextView = (CheckedTextView) view;
        checkedTextView.setChecked(!checkedTextView.isChecked());
        if (dialogItem.getChecked() != checkedTextView.isChecked()) {
            dialogItem.setChecked(checkedTextView.isChecked());
            updateViews(getRoot());
        }
    }
}
