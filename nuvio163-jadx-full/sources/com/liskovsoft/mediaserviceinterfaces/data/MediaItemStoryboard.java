package com.liskovsoft.mediaserviceinterfaces.data;

/* JADX INFO: loaded from: classes4.dex */
public interface MediaItemStoryboard {

    public interface Size {
        int getColCount();

        int getDurationEachMS();

        int getHeight();

        int getRowCount();

        int getStartNum();

        int getWidth();
    }

    int getGroupDurationMS();

    Size getGroupSize();

    String getGroupUrl(int i10);
}
