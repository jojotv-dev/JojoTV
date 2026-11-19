package org.jsoup.helper;

import java.net.Authenticator;
import java.net.PasswordAuthentication;

/* JADX INFO: loaded from: classes5.dex */
class AuthenticationHandler extends Authenticator {
    static final int MaxAttempts = 3;
    static AuthShim handler;
    int attemptCount = 0;
    RequestAuthenticator auth;

    public interface AuthShim {
        void enable(RequestAuthenticator requestAuthenticator, Object obj);

        AuthenticationHandler get(AuthenticationHandler authenticationHandler);

        void remove();
    }

    public static class GlobalHandler implements AuthShim {
        static ThreadLocal<AuthenticationHandler> authenticators = new ThreadLocal<>();

        static {
            Authenticator.setDefault(new AuthenticationHandler());
        }

        @Override // org.jsoup.helper.AuthenticationHandler.AuthShim
        public void enable(RequestAuthenticator requestAuthenticator, Object obj) {
            authenticators.set(new AuthenticationHandler(requestAuthenticator));
        }

        @Override // org.jsoup.helper.AuthenticationHandler.AuthShim
        public AuthenticationHandler get(AuthenticationHandler authenticationHandler) {
            return authenticators.get();
        }

        @Override // org.jsoup.helper.AuthenticationHandler.AuthShim
        public void remove() {
            authenticators.remove();
        }
    }

    static {
        try {
            handler = (AuthShim) Class.forName("org.jsoup.helper.RequestAuthHandler").getConstructor(null).newInstance(null);
        } catch (ClassNotFoundException unused) {
            handler = new GlobalHandler();
        } catch (Exception e10) {
            throw new IllegalStateException(e10);
        }
    }

    public AuthenticationHandler() {
    }

    @Override // java.net.Authenticator
    public final PasswordAuthentication getPasswordAuthentication() {
        AuthenticationHandler authenticationHandler = handler.get(this);
        if (authenticationHandler == null) {
            return null;
        }
        int i10 = authenticationHandler.attemptCount + 1;
        authenticationHandler.attemptCount = i10;
        if (i10 > 3 || authenticationHandler.auth == null) {
            return null;
        }
        return authenticationHandler.auth.authenticate(new RequestAuthenticator.Context(getRequestingURL(), getRequestorType(), getRequestingPrompt()));
    }

    public AuthenticationHandler(RequestAuthenticator requestAuthenticator) {
        this.auth = requestAuthenticator;
    }
}
