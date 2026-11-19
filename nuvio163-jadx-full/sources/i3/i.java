package i3;

import java.text.CharacterIterator;
import kotlin.jvm.internal.CharCompanionObject;

/* JADX INFO: loaded from: classes.dex */
public final class i implements CharacterIterator {

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final CharSequence f23727b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final int f23728c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public int f23729e = 0;

    public i(CharSequence charSequence, int i10) {
        this.f23727b = charSequence;
        this.f23728c = i10;
    }

    @Override // java.text.CharacterIterator
    public final Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException unused) {
            throw new InternalError();
        }
    }

    @Override // java.text.CharacterIterator
    public final char current() {
        int i10 = this.f23729e;
        return i10 == this.f23728c ? CharCompanionObject.MAX_VALUE : this.f23727b.charAt(i10);
    }

    @Override // java.text.CharacterIterator
    public final char first() {
        this.f23729e = 0;
        return current();
    }

    @Override // java.text.CharacterIterator
    public final int getBeginIndex() {
        return 0;
    }

    @Override // java.text.CharacterIterator
    public final int getEndIndex() {
        return this.f23728c;
    }

    @Override // java.text.CharacterIterator
    public final int getIndex() {
        return this.f23729e;
    }

    @Override // java.text.CharacterIterator
    public final char last() {
        int i10 = this.f23728c;
        if (i10 == 0) {
            this.f23729e = i10;
            return CharCompanionObject.MAX_VALUE;
        }
        int i11 = i10 - 1;
        this.f23729e = i11;
        return this.f23727b.charAt(i11);
    }

    @Override // java.text.CharacterIterator
    public final char next() {
        int i10 = this.f23729e + 1;
        this.f23729e = i10;
        int i11 = this.f23728c;
        if (i10 < i11) {
            return this.f23727b.charAt(i10);
        }
        this.f23729e = i11;
        return CharCompanionObject.MAX_VALUE;
    }

    @Override // java.text.CharacterIterator
    public final char previous() {
        int i10 = this.f23729e;
        if (i10 <= 0) {
            return CharCompanionObject.MAX_VALUE;
        }
        int i11 = i10 - 1;
        this.f23729e = i11;
        return this.f23727b.charAt(i11);
    }

    @Override // java.text.CharacterIterator
    public final char setIndex(int i10) {
        if (i10 > this.f23728c || i10 < 0) {
            throw new IllegalArgumentException("invalid position");
        }
        this.f23729e = i10;
        return current();
    }
}
