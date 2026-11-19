package android.support.v4.media;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.media.MediaDescription;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes3.dex */
@SuppressLint({"BanParcelableUsage"})
public final class MediaDescriptionCompat implements Parcelable {
    public static final Parcelable.Creator<MediaDescriptionCompat> CREATOR = new g(1);

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final String f1875b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public final CharSequence f1876c;

    /* JADX INFO: renamed from: e, reason: collision with root package name */
    public final CharSequence f1877e;

    /* JADX INFO: renamed from: i, reason: collision with root package name */
    public final CharSequence f1878i;

    /* JADX INFO: renamed from: m, reason: collision with root package name */
    public final Bitmap f1879m;

    /* JADX INFO: renamed from: n, reason: collision with root package name */
    public final Uri f1880n;

    /* JADX INFO: renamed from: o, reason: collision with root package name */
    public final Bundle f1881o;

    /* JADX INFO: renamed from: p, reason: collision with root package name */
    public final Uri f1882p;

    /* JADX INFO: renamed from: q, reason: collision with root package name */
    public MediaDescription f1883q;

    public MediaDescriptionCompat(String str, CharSequence charSequence, CharSequence charSequence2, CharSequence charSequence3, Bitmap bitmap, Uri uri, Bundle bundle, Uri uri2) {
        this.f1875b = str;
        this.f1876c = charSequence;
        this.f1877e = charSequence2;
        this.f1878i = charSequence3;
        this.f1879m = bitmap;
        this.f1880n = uri;
        this.f1881o = bundle;
        this.f1882p = uri2;
    }

    @Override // android.os.Parcelable
    public final int describeContents() {
        return 0;
    }

    public final String toString() {
        return ((Object) this.f1876c) + ", " + ((Object) this.f1877e) + ", " + ((Object) this.f1878i);
    }

    @Override // android.os.Parcelable
    public final void writeToParcel(Parcel parcel, int i10) {
        MediaDescription mediaDescriptionA = this.f1883q;
        if (mediaDescriptionA == null) {
            MediaDescription.Builder builderB = i.b();
            i.n(builderB, this.f1875b);
            i.p(builderB, this.f1876c);
            i.o(builderB, this.f1877e);
            i.j(builderB, this.f1878i);
            i.l(builderB, this.f1879m);
            i.m(builderB, this.f1880n);
            i.k(builderB, this.f1881o);
            j.b(builderB, this.f1882p);
            mediaDescriptionA = i.a(builderB);
            this.f1883q = mediaDescriptionA;
        }
        mediaDescriptionA.writeToParcel(parcel, i10);
    }
}
