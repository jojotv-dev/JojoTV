package com.nuvio.tv.domain.model;

import androidx.media3.container.NalUnitUtil;
import androidx.media3.extractor.text.ttml.TtmlNode;
import bd.e0;
import dc.d1;
import dc.u;
import io.ktor.http.ContentDisposition;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import w.h0;

/* JADX INFO: loaded from: classes.dex */
@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b#\b\u0087\b\u0018\u00002\u00020\u0001Bc\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u000e\u0010\u000fJ\t\u0010\u001d\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001f\u001a\u00020\u0005HÆ\u0003J\t\u0010 \u001a\u00020\bHÆ\u0003J\t\u0010!\u001a\u00020\bHÆ\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\u0005HÆ\u0003Jk\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0005HÆ\u0001J\u0014\u0010'\u001a\u00020\b2\b\u0010(\u001a\u0004\u0018\u00010\u0001HÖ\u0083\u0004J\n\u0010)\u001a\u00020\u0003HÖ\u0081\u0004J\n\u0010*\u001a\u00020\u0005HÖ\u0081\u0004R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0013\u0010\n\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0013R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0013R\u0013\u0010\f\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0013R\u0013\u0010\r\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0013R\u0011\u0010\u001c\u001a\u00020\b8F¢\u0006\u0006\u001a\u0004\b\u001c\u0010\u0016¨\u0006+"}, d2 = {"Lcom/nuvio/tv/domain/model/UserProfile;", "", TtmlNode.ATTR_ID, "", ContentDisposition.Parameters.Name, "", "avatarColorHex", "usesPrimaryAddons", "", "usesPrimaryPlugins", "avatarId", "avatarUrl", "profileBackgroundId", "profileBackgroundUrl", "<init>", "(ILjava/lang/String;Ljava/lang/String;ZZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getId", "()I", "getName", "()Ljava/lang/String;", "getAvatarColorHex", "getUsesPrimaryAddons", "()Z", "getUsesPrimaryPlugins", "getAvatarId", "getAvatarUrl", "getProfileBackgroundId", "getProfileBackgroundUrl", "isPrimary", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "other", "hashCode", "toString", "app_fullRelease"}, k = 1, mv = {2, 3, 0}, xi = NalUnitUtil.H265_NAL_UNIT_TYPE_UNSPECIFIED)
public final /* data */ class UserProfile {
    public static final int $stable = 0;
    private final String avatarColorHex;
    private final String avatarId;
    private final String avatarUrl;
    private final int id;
    private final String name;
    private final String profileBackgroundId;
    private final String profileBackgroundUrl;
    private final boolean usesPrimaryAddons;
    private final boolean usesPrimaryPlugins;

    public UserProfile(int i10, String name, String avatarColorHex, boolean z2, boolean z10, String str, String str2, String str3, String str4) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(avatarColorHex, "avatarColorHex");
        this.id = i10;
        this.name = name;
        this.avatarColorHex = avatarColorHex;
        this.usesPrimaryAddons = z2;
        this.usesPrimaryPlugins = z10;
        this.avatarId = str;
        this.avatarUrl = str2;
        this.profileBackgroundId = str3;
        this.profileBackgroundUrl = str4;
    }

    public static /* synthetic */ UserProfile copy$default(UserProfile userProfile, int i10, String str, String str2, boolean z2, boolean z10, String str3, String str4, String str5, String str6, int i11, Object obj) {
        if ((i11 & 1) != 0) {
            i10 = userProfile.id;
        }
        if ((i11 & 2) != 0) {
            str = userProfile.name;
        }
        if ((i11 & 4) != 0) {
            str2 = userProfile.avatarColorHex;
        }
        if ((i11 & 8) != 0) {
            z2 = userProfile.usesPrimaryAddons;
        }
        if ((i11 & 16) != 0) {
            z10 = userProfile.usesPrimaryPlugins;
        }
        if ((i11 & 32) != 0) {
            str3 = userProfile.avatarId;
        }
        if ((i11 & 64) != 0) {
            str4 = userProfile.avatarUrl;
        }
        if ((i11 & 128) != 0) {
            str5 = userProfile.profileBackgroundId;
        }
        if ((i11 & 256) != 0) {
            str6 = userProfile.profileBackgroundUrl;
        }
        String str7 = str5;
        String str8 = str6;
        String str9 = str3;
        String str10 = str4;
        boolean z11 = z10;
        String str11 = str2;
        return userProfile.copy(i10, str, str11, z2, z11, str9, str10, str7, str8);
    }

    /* JADX INFO: renamed from: component1, reason: from getter */
    public final int getId() {
        return this.id;
    }

    /* JADX INFO: renamed from: component2, reason: from getter */
    public final String getName() {
        return this.name;
    }

    /* JADX INFO: renamed from: component3, reason: from getter */
    public final String getAvatarColorHex() {
        return this.avatarColorHex;
    }

    /* JADX INFO: renamed from: component4, reason: from getter */
    public final boolean getUsesPrimaryAddons() {
        return this.usesPrimaryAddons;
    }

    /* JADX INFO: renamed from: component5, reason: from getter */
    public final boolean getUsesPrimaryPlugins() {
        return this.usesPrimaryPlugins;
    }

    /* JADX INFO: renamed from: component6, reason: from getter */
    public final String getAvatarId() {
        return this.avatarId;
    }

    /* JADX INFO: renamed from: component7, reason: from getter */
    public final String getAvatarUrl() {
        return this.avatarUrl;
    }

    /* JADX INFO: renamed from: component8, reason: from getter */
    public final String getProfileBackgroundId() {
        return this.profileBackgroundId;
    }

    /* JADX INFO: renamed from: component9, reason: from getter */
    public final String getProfileBackgroundUrl() {
        return this.profileBackgroundUrl;
    }

    public final UserProfile copy(int id2, String name, String avatarColorHex, boolean usesPrimaryAddons, boolean usesPrimaryPlugins, String avatarId, String avatarUrl, String profileBackgroundId, String profileBackgroundUrl) {
        Intrinsics.checkNotNullParameter(name, "name");
        Intrinsics.checkNotNullParameter(avatarColorHex, "avatarColorHex");
        return new UserProfile(id2, name, avatarColorHex, usesPrimaryAddons, usesPrimaryPlugins, avatarId, avatarUrl, profileBackgroundId, profileBackgroundUrl);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserProfile)) {
            return false;
        }
        UserProfile userProfile = (UserProfile) other;
        return this.id == userProfile.id && Intrinsics.areEqual(this.name, userProfile.name) && Intrinsics.areEqual(this.avatarColorHex, userProfile.avatarColorHex) && this.usesPrimaryAddons == userProfile.usesPrimaryAddons && this.usesPrimaryPlugins == userProfile.usesPrimaryPlugins && Intrinsics.areEqual(this.avatarId, userProfile.avatarId) && Intrinsics.areEqual(this.avatarUrl, userProfile.avatarUrl) && Intrinsics.areEqual(this.profileBackgroundId, userProfile.profileBackgroundId) && Intrinsics.areEqual(this.profileBackgroundUrl, userProfile.profileBackgroundUrl);
    }

    public final String getAvatarColorHex() {
        return this.avatarColorHex;
    }

    public final String getAvatarId() {
        return this.avatarId;
    }

    public final String getAvatarUrl() {
        return this.avatarUrl;
    }

    public final int getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public final String getProfileBackgroundId() {
        return this.profileBackgroundId;
    }

    public final String getProfileBackgroundUrl() {
        return this.profileBackgroundUrl;
    }

    public final boolean getUsesPrimaryAddons() {
        return this.usesPrimaryAddons;
    }

    public final boolean getUsesPrimaryPlugins() {
        return this.usesPrimaryPlugins;
    }

    public int hashCode() {
        int iD = o2.a.d(o2.a.d(h0.c(h0.c(Integer.hashCode(this.id) * 31, 31, this.name), 31, this.avatarColorHex), 31, this.usesPrimaryAddons), 31, this.usesPrimaryPlugins);
        String str = this.avatarId;
        int iHashCode = (iD + (str == null ? 0 : str.hashCode())) * 31;
        String str2 = this.avatarUrl;
        int iHashCode2 = (iHashCode + (str2 == null ? 0 : str2.hashCode())) * 31;
        String str3 = this.profileBackgroundId;
        int iHashCode3 = (iHashCode2 + (str3 == null ? 0 : str3.hashCode())) * 31;
        String str4 = this.profileBackgroundUrl;
        return iHashCode3 + (str4 != null ? str4.hashCode() : 0);
    }

    public final boolean isPrimary() {
        return this.id == 1;
    }

    public String toString() {
        int i10 = this.id;
        String str = this.name;
        String str2 = this.avatarColorHex;
        boolean z2 = this.usesPrimaryAddons;
        boolean z10 = this.usesPrimaryPlugins;
        String str3 = this.avatarId;
        String str4 = this.avatarUrl;
        String str5 = this.profileBackgroundId;
        String str6 = this.profileBackgroundUrl;
        StringBuilder sbL = d1.l(i10, "UserProfile(id=", ", name=", str, ", avatarColorHex=");
        d1.A(str2, ", usesPrimaryAddons=", ", usesPrimaryPlugins=", sbL, z2);
        u.w(", avatarId=", str3, ", avatarUrl=", sbL, z10);
        e0.x(sbL, str4, ", profileBackgroundId=", str5, ", profileBackgroundUrl=");
        return o2.a.q(sbL, str6, ")");
    }

    public /* synthetic */ UserProfile(int i10, String str, String str2, boolean z2, boolean z10, String str3, String str4, String str5, String str6, int i11, DefaultConstructorMarker defaultConstructorMarker) {
        this(i10, str, str2, (i11 & 8) != 0 ? false : z2, (i11 & 16) != 0 ? false : z10, (i11 & 32) != 0 ? null : str3, (i11 & 64) != 0 ? null : str4, (i11 & 128) != 0 ? null : str5, (i11 & 256) != 0 ? null : str6);
    }
}
