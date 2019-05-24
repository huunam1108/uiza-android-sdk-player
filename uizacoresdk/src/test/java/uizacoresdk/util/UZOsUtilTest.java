package uizacoresdk.util;

import android.content.Context;
import org.junit.Test;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
public class UZOsUtilTest {
    @Mock
    Context context;

    @Test
    public void getDeviceId() {
        assertEquals("", UZOsUtil.getDeviceId(context));
    }

    @Test
    public void getViewerOsArchitecture() {
        assertEquals(32, UZOsUtil.getViewerOsArchitecture());
    }
}
