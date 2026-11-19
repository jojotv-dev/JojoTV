package com.liskovsoft.sharedutils.dialogs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.liskovsoft.sharedutils.R;
import java.util.ArrayList;
import java.util.List;
import p000j.g;
import p000j.h;

/* JADX INFO: loaded from: classes4.dex */
public abstract class GenericSelectorDialog implements View.OnClickListener {
    protected static final int MULTI_CHOICE = 1;
    protected static final int SINGLE_CHOICE = 0;
    private final Context mActivity;
    protected h mAlertDialog;
    private ArrayList<CheckedTextView> mDialogItems;
    private final DialogSourceBase mDialogSource;
    private final int mThemeResId;

    public interface CombinedDialogSource extends DialogSourceBase {
    }

    public interface DialogSourceBase {

        public static class DialogItem {
            private boolean mChecked;
            private final String mTitle;

            public DialogItem(String str, boolean z2) {
                this.mTitle = str;
                this.mChecked = z2;
            }

            public static DialogItem create(String str, boolean z2) {
                return new DialogItem(str, z2);
            }

            public boolean doRender() {
                return true;
            }

            public boolean getChecked() {
                return this.mChecked;
            }

            public String getTitle() {
                return this.mTitle;
            }

            public void setChecked(boolean z2) {
                this.mChecked = z2;
            }
        }

        public static class MultiDialogItem extends DialogItem {
            public MultiDialogItem(String str, boolean z2) {
                super(str, z2);
            }
        }

        public static class SingleDialogItem extends DialogItem {
            public SingleDialogItem(String str, boolean z2) {
                super(str, z2);
            }
        }

        List<DialogItem> getItems();

        String getTitle();

        void onDismiss();
    }

    public interface MultiDialogSource extends DialogSourceBase {
    }

    public interface SingleDialogSource extends DialogSourceBase {
    }

    public GenericSelectorDialog(Context context, DialogSourceBase dialogSourceBase, int i10) {
        this.mActivity = context;
        this.mDialogSource = dialogSourceBase;
        this.mThemeResId = i10;
    }

    @SuppressLint({"InflateParams"})
    private View buildView(Context context) {
        LayoutInflater layoutInflaterFrom = LayoutInflater.from(context);
        View viewInflate = layoutInflaterFrom.inflate(R.layout.generic_selection_dialog, (ViewGroup) null);
        ViewGroup viewGroup = (ViewGroup) viewInflate.findViewById(R.id.root);
        TypedArray typedArrayObtainStyledAttributes = context.getTheme().obtainStyledAttributes(new int[]{android.R.attr.selectableItemBackground});
        int resourceId = typedArrayObtainStyledAttributes.getResourceId(0, 0);
        typedArrayObtainStyledAttributes.recycle();
        this.mDialogItems = new ArrayList<>();
        for (DialogSourceBase.DialogItem dialogItem : this.mDialogSource.getItems()) {
            CheckedTextView checkedTextViewCreateDialogItem = createDialogItem(layoutInflaterFrom, viewGroup, dialogItem);
            checkedTextViewCreateDialogItem.setBackgroundResource(resourceId);
            checkedTextViewCreateDialogItem.setText(dialogItem.getTitle());
            checkedTextViewCreateDialogItem.setFocusable(true);
            checkedTextViewCreateDialogItem.setTag(dialogItem);
            checkedTextViewCreateDialogItem.setTextSize(0, this.mActivity.getResources().getDimension(R.dimen.dialog_text_size));
            checkedTextViewCreateDialogItem.setOnClickListener(this);
            this.mDialogItems.add(checkedTextViewCreateDialogItem);
            viewGroup.addView(checkedTextViewCreateDialogItem);
        }
        return viewInflate;
    }

    private View createCustomTitle(Context context) {
        String title = this.mDialogSource.getTitle();
        View viewInflate = LayoutInflater.from(context).inflate(R.layout.dialog_custom_title, (ViewGroup) null);
        ((TextView) viewInflate.findViewById(R.id.title)).setText(title);
        return viewInflate;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setUpDismissListener$0(DialogInterface dialogInterface) {
        DialogSourceBase dialogSourceBase = this.mDialogSource;
        if (dialogSourceBase != null) {
            dialogSourceBase.onDismiss();
        }
    }

    private void setUpDismissListener() {
        h hVar = this.mAlertDialog;
        if (hVar != null) {
            hVar.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: com.liskovsoft.sharedutils.dialogs.a
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    this.f7645b.lambda$setUpDismissListener$0(dialogInterface);
                }
            });
        }
    }

    private void showDialog() {
        g gVar = new g(this.mActivity, this.mThemeResId);
        gVar.f26834a.f26772e = createCustomTitle(gVar.getContext());
        h hVarCreate = gVar.setView(buildView(gVar.getContext())).create();
        this.mAlertDialog = hVarCreate;
        hVarCreate.show();
        updateViews(getRoot(), true);
        setUpDismissListener();
    }

    public abstract CheckedTextView createDialogItem(LayoutInflater layoutInflater, ViewGroup viewGroup, DialogSourceBase.DialogItem dialogItem);

    public int getItemType(DialogSourceBase.DialogItem dialogItem) {
        if (dialogItem instanceof DialogSourceBase.SingleDialogItem) {
            return 0;
        }
        if (dialogItem instanceof DialogSourceBase.MultiDialogItem) {
            return 1;
        }
        throw new IllegalStateException("Incorrect DialogItem supplied");
    }

    public View getRoot() {
        return this.mAlertDialog.findViewById(R.id.root);
    }

    public void run() {
        showDialog();
    }

    public void updateViews(View view) {
        updateViews(view, false);
    }

    /* JADX WARN: Code duplicated, block: B:9:0x0015  */
    private void updateViews(View view, boolean z2) {
        boolean z10;
        List<DialogSourceBase.DialogItem> items = this.mDialogSource.getItems();
        if (z2) {
            DialogSourceBase dialogSourceBase = this.mDialogSource;
            if ((dialogSourceBase instanceof CombinedDialogSource) || (dialogSourceBase instanceof SingleDialogSource)) {
                z10 = true;
            } else {
                z10 = false;
            }
        } else {
            z10 = false;
        }
        for (DialogSourceBase.DialogItem dialogItem : items) {
            CheckedTextView checkedTextView = (CheckedTextView) view.findViewWithTag(dialogItem);
            if (checkedTextView == null) {
                throw new IllegalStateException("Please, don't create 'DialogItems' in the 'getItems' method. For such purposed use constructor.");
            }
            checkedTextView.setChecked(dialogItem.getChecked());
            checkedTextView.setVisibility(dialogItem.doRender() ? 0 : 8);
            if (z10 && dialogItem.getChecked() && dialogItem.doRender()) {
                checkedTextView.requestFocus();
            }
        }
    }
}
