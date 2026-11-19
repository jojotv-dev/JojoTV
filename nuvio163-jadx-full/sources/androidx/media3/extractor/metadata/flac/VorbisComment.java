package androidx.media3.extractor.metadata.flac;

import androidx.media3.common.MediaMetadata;
import androidx.media3.common.Metadata;
import androidx.media3.common.util.UnstableApi;
import kf.ge;
import l3.c;
import w.h0;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
@Deprecated
public class VorbisComment implements Metadata.Entry {
    public final String key;
    public final String value;

    public VorbisComment(String str, String str2) {
        this.key = ge.s0(str);
        this.value = str2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj != null && getClass() == obj.getClass()) {
            VorbisComment vorbisComment = (VorbisComment) obj;
            if (this.key.equals(vorbisComment.key) && this.value.equals(vorbisComment.value)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return this.value.hashCode() + h0.c(527, 31, this.key);
    }

    @Override // androidx.media3.common.Metadata.Entry
    public void populateMediaMetadata(MediaMetadata.Builder builder) {
        String str = this.key;
        str.getClass();
        switch (str) {
            case "TOTALTRACKS":
                Integer numX = c.X(this.value);
                if (numX != null) {
                    builder.setTotalTrackCount(numX);
                    break;
                }
                break;
            case "TOTALDISCS":
                Integer numX2 = c.X(this.value);
                if (numX2 != null) {
                    builder.setTotalDiscCount(numX2);
                    break;
                }
                break;
            case "TRACKNUMBER":
                Integer numX3 = c.X(this.value);
                if (numX3 != null) {
                    builder.setTrackNumber(numX3);
                    break;
                }
                break;
            case "ALBUM":
                builder.setAlbumTitle(this.value);
                break;
            case "GENRE":
                builder.setGenre(this.value);
                break;
            case "TITLE":
                builder.setTitle(this.value);
                break;
            case "DESCRIPTION":
                builder.setDescription(this.value);
                break;
            case "DISCNUMBER":
                Integer numX4 = c.X(this.value);
                if (numX4 != null) {
                    builder.setDiscNumber(numX4);
                    break;
                }
                break;
            case "ALBUMARTIST":
                builder.setAlbumArtist(this.value);
                break;
            case "ARTIST":
                builder.setArtist(this.value);
                break;
        }
    }

    public String toString() {
        return "VC: " + this.key + "=" + this.value;
    }
}
