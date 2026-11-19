package com.liskovsoft.mediaserviceinterfaces;

import com.liskovsoft.mediaserviceinterfaces.oauth.Account;
import eh.c;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface SignInService {

    public interface OnAccountChange {
        void onAccountChanged(Account account);
    }

    void addOnAccountChange(OnAccountChange onAccountChange);

    List<Account> getAccounts();

    Account getSelectedAccount();

    boolean isSigned();

    String printDebugInfo();

    void refreshAccounts();

    void removeAccount(Account account);

    void selectAccount(Account account);

    c signInObserve();
}
