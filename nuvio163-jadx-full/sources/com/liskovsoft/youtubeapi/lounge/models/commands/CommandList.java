package com.liskovsoft.youtubeapi.lounge.models.commands;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.List;

/* JADX INFO: loaded from: classes4.dex */
public class CommandList {

    @JsonPath({"$[*]"})
    private List<CommandItem> mCommands;

    public List<CommandItem> getCommands() {
        return this.mCommands;
    }

    public String getParam(String str) {
        List<CommandItem> list = this.mCommands;
        if (list == null) {
            return null;
        }
        for (CommandItem commandItem : list) {
            if (str.equals(commandItem.getType())) {
                return commandItem.getParams().get(0);
            }
        }
        return null;
    }
}
