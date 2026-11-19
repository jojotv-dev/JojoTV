package w7;

import java.io.IOException;
import java.io.StringWriter;
import z7.j1;
import z7.v0;

/* JADX INFO: loaded from: classes.dex */
public abstract class q {
    public boolean a() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public double b() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public float c() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public int d() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public final p e() {
        if (this instanceof p) {
            return (p) this;
        }
        throw new IllegalStateException("Not a JSON Array: " + this);
    }

    public final t f() {
        if (this instanceof t) {
            return (t) this;
        }
        throw new IllegalStateException("Not a JSON Object: " + this);
    }

    public final v h() {
        if (this instanceof v) {
            return (v) this;
        }
        throw new IllegalStateException("Not a JSON Primitive: " + this);
    }

    public long i() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public String j() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public final String toString() {
        try {
            StringWriter stringWriter = new StringWriter();
            d8.b bVar = new d8.b(stringWriter);
            bVar.f11881p = 1;
            j1.f65756z.getClass();
            v0.d(bVar, this);
            return stringWriter.toString();
        } catch (IOException e10) {
            throw new AssertionError(e10);
        }
    }
}
