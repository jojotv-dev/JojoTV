package com.liskovsoft.mediaserviceinterfaces.data;

import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface ItemGroup {

    public interface Item {
        String getBadge();

        String getChannelId();

        String getIconUrl();

        int getLikeCount();

        CharSequence getSubtitle();

        String getTitle();

        String getVideoId();
    }

    void add(Item item);

    void addAll(List<Item> list);

    boolean contains(String str);

    Item findItem(String str);

    String getBadge();

    String getIconUrl();

    String getId();

    List<Item> getItems();

    String getTitle();

    boolean isEmpty();

    void remove(String str);
}
