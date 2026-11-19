package org.jsoup.internal;

import java.io.IOException;
import org.jsoup.SerializationException;

/* JADX INFO: loaded from: classes5.dex */
public abstract class QuietAppendable {

    public static final class BaseAppendable extends QuietAppendable {

        /* JADX INFO: renamed from: a, reason: collision with root package name */
        private final Appendable f44225a;

        @FunctionalInterface
        public interface Action {
            void append();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$append$0(CharSequence charSequence) throws IOException {
            this.f44225a.append(charSequence);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$append$1(char c5) throws IOException {
            this.f44225a.append(c5);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$append$2(char[] cArr, int i10, int i11) throws IOException {
            this.f44225a.append(new String(cArr, i10, i11));
        }

        private BaseAppendable quiet(Action action) {
            try {
                action.append();
                return this;
            } catch (IOException e10) {
                throw new SerializationException(e10);
            }
        }

        private BaseAppendable(Appendable appendable) {
            this.f44225a = appendable;
        }

        @Override // org.jsoup.internal.QuietAppendable
        public BaseAppendable append(final CharSequence charSequence) {
            return quiet(new Action() { // from class: org.jsoup.internal.d
                @Override // org.jsoup.internal.QuietAppendable.BaseAppendable.Action
                public final void append() throws IOException {
                    this.f44236a.lambda$append$0(charSequence);
                }
            });
        }

        @Override // org.jsoup.internal.QuietAppendable
        public BaseAppendable append(final char c5) {
            return quiet(new Action() { // from class: org.jsoup.internal.b
                @Override // org.jsoup.internal.QuietAppendable.BaseAppendable.Action
                public final void append() throws IOException {
                    this.f44230a.lambda$append$1(c5);
                }
            });
        }

        @Override // org.jsoup.internal.QuietAppendable
        public QuietAppendable append(final char[] cArr, final int i10, final int i11) {
            return quiet(new Action() { // from class: org.jsoup.internal.c
                @Override // org.jsoup.internal.QuietAppendable.BaseAppendable.Action
                public final void append() throws IOException {
                    this.f44232a.lambda$append$2(cArr, i10, i11);
                }
            });
        }
    }

    public static final class StringBuilderAppendable extends QuietAppendable {

        /* JADX INFO: renamed from: sb, reason: collision with root package name */
        private final StringBuilder f44226sb;

        public String toString() {
            return this.f44226sb.toString();
        }

        private StringBuilderAppendable(StringBuilder sb2) {
            this.f44226sb = sb2;
        }

        @Override // org.jsoup.internal.QuietAppendable
        public StringBuilderAppendable append(CharSequence charSequence) {
            this.f44226sb.append(charSequence);
            return this;
        }

        @Override // org.jsoup.internal.QuietAppendable
        public StringBuilderAppendable append(char c5) {
            this.f44226sb.append(c5);
            return this;
        }

        @Override // org.jsoup.internal.QuietAppendable
        public QuietAppendable append(char[] cArr, int i10, int i11) {
            this.f44226sb.append(cArr, i10, i11);
            return this;
        }
    }

    public static QuietAppendable wrap(Appendable appendable) {
        return appendable instanceof StringBuilder ? new StringBuilderAppendable((StringBuilder) appendable) : new BaseAppendable(appendable);
    }

    public abstract QuietAppendable append(char c5);

    public abstract QuietAppendable append(CharSequence charSequence);

    public abstract QuietAppendable append(char[] cArr, int i10, int i11);
}
