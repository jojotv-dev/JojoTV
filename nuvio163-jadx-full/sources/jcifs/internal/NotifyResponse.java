package jcifs.internal;

import java.util.List;
import jcifs.FileNotifyInformation;

/* JADX INFO: loaded from: classes5.dex */
public interface NotifyResponse extends CommonServerMessageBlockResponse {
    List<FileNotifyInformation> getNotifyInformation();
}
