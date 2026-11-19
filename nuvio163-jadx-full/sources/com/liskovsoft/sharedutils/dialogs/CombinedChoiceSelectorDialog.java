package com.liskovsoft.sharedutils.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import com.liskovsoft.sharedutils.R;

/* JADX INFO: loaded from: classes4.dex */
public class CombinedChoiceSelectorDialog extends GenericSelectorDialog {
    private final GenericSelectorDialog.CombinedDialogSource mDialogSource;

    public CombinedChoiceSelectorDialog(Context context, GenericSelectorDialog.CombinedDialogSource combinedDialogSource, int i10) {
        super(context, combinedDialogSource, i10);
        this.mDialogSource = combinedDialogSource;
    }

    public static void create(Context context, GenericSelectorDialog.CombinedDialogSource combinedDialogSource, int i10) {
        new CombinedChoiceSelectorDialog(context, combinedDialogSource, i10).run();
    }

    @Override // com.liskovsoft.sharedutils.dialogs.GenericSelectorDialog
    public CheckedTextView createDialogItem(LayoutInflater layoutInflater, ViewGroup viewGroup, GenericSelectorDialog.DialogSourceBase.DialogItem dialogItem) {
        int itemType = getItemType(dialogItem);
        if (itemType == 0) {
            return (CheckedTextView) layoutInflater.inflate(R.layout.dialog_check_item_single, viewGroup, false);
        }
        if (itemType == 1) {
            return (CheckedTextView) layoutInflater.inflate(R.layout.dialog_check_item_multi, viewGroup, false);
        }
        throw new IllegalStateException("Incorrect DialogItem supplied");
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        GenericSelectorDialog.DialogSourceBase.DialogItem dialogItem = (GenericSelectorDialog.DialogSourceBase.DialogItem) view.getTag();
        CheckedTextView checkedTextView = (CheckedTextView) view;
        int itemType = getItemType(dialogItem);
        if (itemType == 0) {
            checkedTextView.setChecked(true);
        } else if (itemType == 1) {
            checkedTextView.setChecked(!checkedTextView.isChecked());
        }
        if (dialogItem.getChecked() != checkedTextView.isChecked()) {
            dialogItem.setChecked(checkedTextView.isChecked());
            updateViews(getRoot());
        }
    }
}
