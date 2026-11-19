package com.uwetrottmann.tmdb2.entities;

import com.uwetrottmann.tmdb2.enumerations.ReleaseType;

/* JADX INFO: loaded from: classes4.dex */
public class DiscoverFilter {
    private final Integer[] items;
    private final Separator separator;

    public enum Separator {
        AND(","),
        OR("|");

        private final String symbol;

        Separator(String str) {
            this.symbol = str;
        }
    }

    public DiscoverFilter(Integer... numArr) {
        this(Separator.AND, numArr);
    }

    public final String toString() {
        Integer[] numArr;
        if (this.separator == null || (numArr = this.items) == null || numArr.length == 0) {
            return null;
        }
        StringBuilder sb2 = new StringBuilder();
        for (Integer num : this.items) {
            if (num != null) {
                if (sb2.length() > 0) {
                    sb2.append(this.separator.symbol);
                }
                sb2.append(num);
            }
        }
        return sb2.toString();
    }

    public DiscoverFilter(Separator separator, Integer... numArr) {
        this.separator = separator;
        this.items = numArr;
    }

    public DiscoverFilter(Separator separator, ReleaseType... releaseTypeArr) {
        this.separator = separator;
        this.items = new Integer[releaseTypeArr.length];
        for (int i10 = 0; i10 < releaseTypeArr.length; i10++) {
            ReleaseType releaseType = releaseTypeArr[i10];
            if (releaseType != null) {
                this.items[i10] = Integer.valueOf(releaseType.f11059id);
            }
        }
    }
}
