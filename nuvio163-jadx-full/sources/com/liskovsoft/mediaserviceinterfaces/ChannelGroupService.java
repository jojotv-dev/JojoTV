package com.liskovsoft.mediaserviceinterfaces;

import android.net.Uri;
import com.liskovsoft.mediaserviceinterfaces.data.ItemGroup;
import eh.c;
import java.io.File;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public interface ChannelGroupService {
    void addChannelGroup(ItemGroup itemGroup);

    ItemGroup.Item createChannel(String str, String str2, String str3);

    ItemGroup createChannelGroup(String str, String str2, List<ItemGroup.Item> list);

    void exportData(String str);

    ItemGroup findChannelGroupById(String str);

    ItemGroup findChannelGroupByTitle(String str);

    String[] findChannelIdsForGroup(String str);

    List<ItemGroup> getChannelGroups();

    c importGroupsObserve(Uri uri);

    c importGroupsObserve(File file);

    boolean isEmpty();

    void removeChannelGroup(ItemGroup itemGroup);

    void renameChannelGroup(ItemGroup itemGroup, String str);
}
