package vn.uiza.utils.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;

@RunWith(RobolectricTestRunner.class)
public class EncodeUtilsTest {
    @Test
    public void urlEncode_urlDecode() {
        String urlEncodeString = "%E5%93%88%E5%93%88%E5%93%88";
        assertEquals(urlEncodeString, EncodeUtils.urlEncode("哈哈哈"));
        assertEquals(urlEncodeString, EncodeUtils.urlEncode("哈哈哈", "UTF-8"));

        assertEquals("哈哈哈", EncodeUtils.urlDecode(urlEncodeString));
        assertEquals("哈哈哈", EncodeUtils.urlDecode(urlEncodeString, "UTF-8"));
    }

    @Test
    public void base64Decode_base64Encode() {
        assertTrue(
                Arrays.equals(
                        "HMT".getBytes(),
                        EncodeUtils.base64Decode(EncodeUtils.base64Encode("HMT"))
                )
        );
        assertTrue(
                Arrays.equals(
                        "HMT".getBytes(),
                        EncodeUtils.base64Decode(EncodeUtils.base64Encode2String("HMT".getBytes()))
                )
        );
        assertEquals(
                "SE1U",
                EncodeUtils.base64Encode2String("HMT".getBytes())
        );
        assertTrue(
                Arrays.equals(
                        "SE1U".getBytes(),
                        EncodeUtils.base64Encode("HMT".getBytes())
                )
        );
    }

    @Test
    public void htmlEncode_htmlDecode() {
        String html = "<html>" +
                "<head>" +
                "<title>Title</title>" +
                "</head>" +
                "<body>" +
                "<p>body Body</p>" +
                "<p>title Body</p>" +
                "</body>" +
                "</html>";
        String encodeHtml = "&lt;html&gt;&lt;head&gt;&lt;title&gt;Title&lt;/title&gt;&lt;/head&gt;&lt;body&gt;&lt;p&gt;body Body&lt;/p&gt;&lt;p&gt;title Body&lt;/p&gt;&lt;/body&gt;&lt;/html&gt;";

        assertEquals(encodeHtml, EncodeUtils.htmlEncode(html));
    }
}
