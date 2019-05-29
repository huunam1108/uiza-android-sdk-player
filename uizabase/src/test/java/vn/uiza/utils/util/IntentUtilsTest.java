package vn.uiza.utils.util;


import android.content.Context;
import android.os.Bundle;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.io.File;

import static vn.uiza.utils.util.IntentUtilsTest.TestConfig.PATH_FILE;
import static vn.uiza.utils.util.IntentUtilsTest.TestConfig.PATH_TEMP;

@RunWith(RobolectricTestRunner.class)
public class IntentUtilsTest {
    @Mock
    Bundle bundle;

    @Mock
    Context mContext;

    @Mock
    File file;

    static class TestConfig {

        static final String FILE_SEP = File.separator;
        static final String TEST_PATH;

        static {
            String projectPath = System.getProperty("user.dir");
            TEST_PATH = projectPath + FILE_SEP + "src" + FILE_SEP + "test" + FILE_SEP + "res" + FILE_SEP;
        }

        static final String PATH_TEMP = TEST_PATH + "temp" + FILE_SEP;
        static final String PATH_FILE = TEST_PATH + "file" + FILE_SEP;
    }


    @Test
    public void getInstallAppIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getInstallAppIntent(PATH_TEMP, "authority"));
    }

    @Test
    public void getUninstallAppIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getUninstallAppIntent("packageName"));
    }

    @Test
    public void getLaunchAppIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getInstallAppIntent(PATH_TEMP, "packageName"));
    }

    @Test
    public void getAppDetailsSettingsIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getAppDetailsSettingsIntent("packageName"));
    }

    @Test
    public void getShareTextIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getShareTextIntent("content"));
    }

    @Test
    public void getShareImageIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getShareImageIntent("content", PATH_FILE));
    }

    @Test
    public void getShareImageUriIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getShareImageIntent("content", PATH_FILE));
    }

    @Test
    public void getDialIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getDialIntent("0123456789"));
    }

    @Test
    public void getShutdownIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getShutdownIntent());
    }

    @Test
    public void getComponentIntent_test_success() {
        Assert.assertNotNull(IntentUtils.getComponentIntent("packageName", "className", bundle));
        Assert.assertNotNull(IntentUtils.getComponentIntent("packageName", "className"));
    }
}
