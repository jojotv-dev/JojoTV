package com.liskovsoft.mediaserviceinterfaces.oauth;

import eh.c;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface SignInService {
    List<Account> getAccounts();

    c getAccountsObserve();

    Account getSelectedAccount();

    boolean isSigned();

    c isSignedObserve();

    void removeAccount(Account account);

    void selectAccount(Account account);

    void setOnChange(Runnable runnable);

    c signInObserve();

    void signOut();

    c signOutObserve();
}
