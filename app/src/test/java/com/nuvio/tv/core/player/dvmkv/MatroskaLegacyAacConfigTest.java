package com.nuvio.tv.core.player.dvmkv;

import static org.junit.Assert.assertArrayEquals;

import java.lang.reflect.Method;
import org.junit.Test;

public final class MatroskaLegacyAacConfigTest {
  @Test
  public void buildsHeAacConfigWithCoreThenOutputFrequency() throws Exception {
    Class<?> trackClass = Class.forName(
        "com.nuvio.tv.core.player.dvmkv.MatroskaExtractor$Track");
    Method method = trackClass.getDeclaredMethod(
        "maybeBuildLegacyHeAacConfig", int.class, int.class, int.class);
    method.setAccessible(true);

    byte[] config = (byte[]) method.invoke(null, 22050, 44100, 2);

    assertArrayEquals(new byte[] {(byte) 0x2B, (byte) 0x92, (byte) 0x08}, config);
  }
}
