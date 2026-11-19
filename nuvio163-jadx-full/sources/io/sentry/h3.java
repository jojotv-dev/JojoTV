package io.sentry;

import java.io.Closeable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public interface h3 extends Closeable {
    Integer C();

    Long D();

    String G();

    HashMap I(ILogger iLogger, s1 s1Var);

    Float L();

    Object M();

    ArrayList S(ILogger iLogger, s1 s1Var);

    void beginArray();

    void beginObject();

    void endArray();

    void endObject();

    boolean hasNext();

    void i(ILogger iLogger, AbstractMap abstractMap, String str);

    TimeZone m(ILogger iLogger);

    double nextDouble();

    float nextFloat();

    int nextInt();

    long nextLong();

    String nextName();

    String nextString();

    Double o();

    Date p(ILogger iLogger);

    io.sentry.vendor.gson.stream.b peek();

    Boolean q();

    void setLenient(boolean z2);

    void skipValue();

    Object w(ILogger iLogger, s1 s1Var);
}
