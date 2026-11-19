package eh;

import java.util.NoSuchElementException;

/* JADX INFO: loaded from: classes5.dex */
public abstract class c {
    public final Object a() {
        mh.b bVar = new mh.b(1);
        b(bVar);
        if (bVar.getCount() != 0) {
            try {
                bVar.await();
            } catch (InterruptedException e10) {
                bVar.dispose();
                throw rh.c.a(e10);
            }
        }
        Throwable th2 = bVar.f39168c;
        if (th2 != null) {
            throw rh.c.a(th2);
        }
        Object obj = bVar.f39167b;
        if (obj != null) {
            return obj;
        }
        throw new NoSuchElementException();
    }

    public final void b(f fVar) {
        try {
            c(fVar);
        } catch (NullPointerException e10) {
            throw e10;
        } catch (Throwable th2) {
            ra.a.X(th2);
            bo.a.H(th2);
            NullPointerException nullPointerException = new NullPointerException("Actually not, but can't throw other exceptions due to RS");
            nullPointerException.initCause(th2);
            throw nullPointerException;
        }
    }

    public abstract void c(f fVar);
}
