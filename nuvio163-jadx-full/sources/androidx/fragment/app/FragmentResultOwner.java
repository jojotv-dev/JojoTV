package androidx.fragment.app;

import android.os.Bundle;
import androidx.lifecycle.x;

/* JADX INFO: loaded from: classes.dex */
public interface FragmentResultOwner {
    void clearFragmentResult(String str);

    void clearFragmentResultListener(String str);

    void setFragmentResult(String str, Bundle bundle);

    void setFragmentResultListener(String str, x xVar, FragmentResultListener fragmentResultListener);
}
