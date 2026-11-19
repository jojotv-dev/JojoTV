package jcifs;

/* JADX INFO: loaded from: classes5.dex */
public interface Credentials {
    String getUserDomain();

    boolean isAnonymous();

    boolean isGuest();

    <T extends Credentials> T unwrap(Class<T> cls);
}
