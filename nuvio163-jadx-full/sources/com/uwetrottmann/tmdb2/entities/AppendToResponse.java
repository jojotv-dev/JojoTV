package com.uwetrottmann.tmdb2.entities;

import com.uwetrottmann.tmdb2.enumerations.AppendToResponseItem;

/* JADX INFO: loaded from: classes4.dex */
public class AppendToResponse {
    private final AppendToResponseItem[] items;

    public AppendToResponse(AppendToResponseItem... appendToResponseItemArr) {
        this.items = appendToResponseItemArr;
    }

    public final String toString() {
        AppendToResponseItem[] appendToResponseItemArr = this.items;
        if (appendToResponseItemArr == null || appendToResponseItemArr.length <= 0) {
            return null;
        }
        StringBuilder sb2 = new StringBuilder();
        int i10 = 0;
        while (true) {
            AppendToResponseItem[] appendToResponseItemArr2 = this.items;
            if (i10 >= appendToResponseItemArr2.length) {
                return sb2.toString();
            }
            sb2.append(appendToResponseItemArr2[i10]);
            if (i10 < this.items.length - 1) {
                sb2.append(',');
            }
            i10++;
        }
    }
}
