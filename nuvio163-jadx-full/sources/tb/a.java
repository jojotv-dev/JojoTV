package tb;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/* JADX INFO: loaded from: classes4.dex */
public final class a extends Handler {
    @Override // android.os.Handler
    public final void handleMessage(Message message) {
        Bundle data = message.getData();
        if (data != null) {
            if (data.getInt("PARTS_COUNT", 0) > 20) {
                try {
                    Thread.sleep(5L);
                } catch (InterruptedException e10) {
                    e10.printStackTrace();
                }
            }
            String string = data.getString("VALUE");
            String string2 = data.getString("TAG");
            if (string == null || string2 == null) {
                return;
            }
            Log.v(string2, string);
        }
    }
}
