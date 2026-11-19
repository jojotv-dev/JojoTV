package android.support.v4.media;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.media.MediaDescription;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.recyclerview.widget.l1;
import androidx.recyclerview.widget.m1;
import androidx.recyclerview.widget.w;
import androidx.versionedparcelable.ParcelImpl;
import g.l;
import kotlin.jvm.internal.Intrinsics;
import moe.shizuku.api.BinderContainer;
import o.p0;
import org.chromium.base.UnguessableToken;

/* JADX INFO: loaded from: classes.dex */
public final class g implements Parcelable.Creator {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final /* synthetic */ int f1898a;

    @Override // android.os.Parcelable.Creator
    public final Object createFromParcel(Parcel parcel) {
        Bundle bundle;
        switch (this.f1898a) {
            case 0:
                return new MediaBrowserCompat$MediaItem(parcel);
            case 1:
                Object objCreateFromParcel = MediaDescription.CREATOR.createFromParcel(parcel);
                if (objCreateFromParcel == null) {
                    return null;
                }
                MediaDescription mediaDescription = (MediaDescription) objCreateFromParcel;
                String strG = i.g(mediaDescription);
                CharSequence charSequenceI = i.i(mediaDescription);
                CharSequence charSequenceH = i.h(mediaDescription);
                CharSequence charSequenceC = i.c(mediaDescription);
                Bitmap bitmapE = i.e(mediaDescription);
                Uri uriF = i.f(mediaDescription);
                Bundle bundleD = i.d(mediaDescription);
                if (bundleD != null) {
                    bundleD = android.support.v4.media.session.h.b(bundleD);
                }
                Uri uriA = bundleD != null ? (Uri) bundleD.getParcelable(androidx.media3.session.legacy.MediaDescriptionCompat.DESCRIPTION_KEY_MEDIA_URI) : null;
                if (uriA == null) {
                    bundle = bundleD;
                } else if (bundleD.containsKey(androidx.media3.session.legacy.MediaDescriptionCompat.DESCRIPTION_KEY_NULL_BUNDLE_FLAG) && bundleD.size() == 2) {
                    bundle = null;
                } else {
                    bundleD.remove(androidx.media3.session.legacy.MediaDescriptionCompat.DESCRIPTION_KEY_MEDIA_URI);
                    bundleD.remove(androidx.media3.session.legacy.MediaDescriptionCompat.DESCRIPTION_KEY_NULL_BUNDLE_FLAG);
                    bundle = bundleD;
                }
                if (uriA == null) {
                    uriA = j.a(mediaDescription);
                }
                MediaDescriptionCompat mediaDescriptionCompat = new MediaDescriptionCompat(strG, charSequenceI, charSequenceH, charSequenceC, bitmapE, uriF, bundle, uriA);
                mediaDescriptionCompat.f1883q = mediaDescription;
                return mediaDescriptionCompat;
            case 2:
                return new MediaMetadataCompat(parcel);
            case 3:
                return new RatingCompat(parcel.readInt(), parcel.readFloat());
            case 4:
                return new android.support.v4.os.e(parcel);
            case 5:
                w wVar = new w();
                wVar.f4124b = parcel.readInt();
                wVar.f4125c = parcel.readInt();
                wVar.f4126e = parcel.readInt() == 1;
                return wVar;
            case 6:
                l1 l1Var = new l1();
                l1Var.f3980b = parcel.readInt();
                l1Var.f3981c = parcel.readInt();
                l1Var.f3983i = parcel.readInt() == 1;
                int i10 = parcel.readInt();
                if (i10 > 0) {
                    int[] iArr = new int[i10];
                    l1Var.f3982e = iArr;
                    parcel.readIntArray(iArr);
                }
                return l1Var;
            case 7:
                m1 m1Var = new m1();
                m1Var.f3992b = parcel.readInt();
                m1Var.f3993c = parcel.readInt();
                int i11 = parcel.readInt();
                m1Var.f3994e = i11;
                if (i11 > 0) {
                    int[] iArr2 = new int[i11];
                    m1Var.f3995i = iArr2;
                    parcel.readIntArray(iArr2);
                }
                int i12 = parcel.readInt();
                m1Var.f3996m = i12;
                if (i12 > 0) {
                    int[] iArr3 = new int[i12];
                    m1Var.f3997n = iArr3;
                    parcel.readIntArray(iArr3);
                }
                m1Var.f3999p = parcel.readInt() == 1;
                m1Var.f4000q = parcel.readInt() == 1;
                m1Var.f4001r = parcel.readInt() == 1;
                m1Var.f3998o = parcel.readArrayList(l1.class.getClassLoader());
                return m1Var;
            case 8:
                return new ParcelImpl(parcel);
            case 9:
                Intrinsics.checkNotNullParameter(parcel, "parcel");
                Intrinsics.checkNotNullParameter(parcel, "parcel");
                return new g.a(parcel.readInt(), parcel.readInt() == 0 ? null : (Intent) Intent.CREATOR.createFromParcel(parcel));
            case 10:
                Intrinsics.checkNotNullParameter(parcel, "inParcel");
                Intrinsics.checkNotNullParameter(parcel, "parcel");
                Parcelable parcelable = parcel.readParcelable(IntentSender.class.getClassLoader());
                Intrinsics.checkNotNull(parcelable);
                return new l((IntentSender) parcelable, (Intent) parcel.readParcelable(Intent.class.getClassLoader()), parcel.readInt(), parcel.readInt());
            case 11:
                p0 p0Var = new p0(parcel);
                p0Var.f43280b = parcel.readByte() != 0;
                return p0Var;
            case 12:
                o4.h hVar = new o4.h(parcel);
                hVar.f43482b = parcel.readInt();
                return hVar;
            case 13:
                BinderContainer binderContainer = new BinderContainer();
                binderContainer.f39258b = parcel.readStrongBinder();
                return binderContainer;
            default:
                long j10 = parcel.readLong();
                long j11 = parcel.readLong();
                if (j10 == 0 || j11 == 0) {
                    return null;
                }
                return new UnguessableToken(j10, j11);
        }
    }

    @Override // android.os.Parcelable.Creator
    public final Object[] newArray(int i10) {
        switch (this.f1898a) {
            case 0:
                return new MediaBrowserCompat$MediaItem[i10];
            case 1:
                return new MediaDescriptionCompat[i10];
            case 2:
                return new MediaMetadataCompat[i10];
            case 3:
                return new RatingCompat[i10];
            case 4:
                return new android.support.v4.os.e[i10];
            case 5:
                return new w[i10];
            case 6:
                return new l1[i10];
            case 7:
                return new m1[i10];
            case 8:
                return new ParcelImpl[i10];
            case 9:
                return new g.a[i10];
            case 10:
                return new l[i10];
            case 11:
                return new p0[i10];
            case 12:
                return new o4.h[i10];
            case 13:
                return new BinderContainer[i10];
            default:
                return new UnguessableToken[i10];
        }
    }
}
