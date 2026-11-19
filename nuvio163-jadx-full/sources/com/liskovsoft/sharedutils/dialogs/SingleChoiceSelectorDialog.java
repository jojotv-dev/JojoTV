package com.liskovsoft.sharedutils.dialogs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import com.liskovsoft.sharedutils.R;

/* JADX INFO: loaded from: classes4.dex */
public class SingleChoiceSelectorDialog extends GenericSelectorDialog {
    private final GenericSelectorDialog.SingleDialogSource mDialogSource;

    public SingleChoiceSelectorDialog(Context context, GenericSelectorDialog.SingleDialogSource singleDialogSource, int i10) {
        super(context, singleDialogSource, i10);
        this.mDialogSource = singleDialogSource;
    }

    public static void create(Context context, GenericSelectorDialog.SingleDialogSource singleDialogSource, int i10) {
        new SingleChoiceSelectorDialog(context, singleDialogSource, i10).run();
    }

    @Override // com.liskovsoft.sharedutils.dialogs.GenericSelectorDialog
    public CheckedTextView createDialogItem(LayoutInflater layoutInflater, ViewGroup viewGroup, GenericSelectorDialog.DialogSourceBase.DialogItem dialogItem) {
        return (CheckedTextView) layoutInflater.inflate(R.layout.dialog_check_item_single, viewGroup, false);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        GenericSelectorDialog.DialogSourceBase.DialogItem dialogItem = (GenericSelectorDialog.DialogSourceBase.DialogItem) view.getTag();
        CheckedTextView checkedTextView = (CheckedTextView) view;
        checkedTextView.setChecked(true);
        if (dialogItem.getChecked() != checkedTextView.isChecked()) {
            dialogItem.setChecked(checkedTextView.isChecked());
            updateViews(getRoot());
        }
    }
}
