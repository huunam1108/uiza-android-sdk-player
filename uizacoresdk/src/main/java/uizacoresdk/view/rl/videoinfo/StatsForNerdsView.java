package uizacoresdk.view.rl.videoinfo;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;
import uizacoresdk.R;

public class StatsForNerdsView extends RelativeLayout {
    private TextView textEntityId, textBufferHealth, textNetworkActivity, textVolumeNormalized,
            textCurrentOptimalRes, textViewPortFrame, textConnectionSpeed, textHost, textVersion,
            textDeviceInfo;

    public StatsForNerdsView(Context context) {
        super(context);
        init();
    }

    public StatsForNerdsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public StatsForNerdsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public StatsForNerdsView(Context context, AttributeSet attrs, int defStyleAttr,
            int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.layout_stats_for_nerds, this);
        textEntityId = findViewById(R.id.text_entity_id);
        textBufferHealth = findViewById(R.id.text_buffer_health);
        textNetworkActivity = findViewById(R.id.text_network_activity);
        textVolumeNormalized = findViewById(R.id.text_volume_normalized);
        textCurrentOptimalRes = findViewById(R.id.text_current_optimal_res);
        textViewPortFrame = findViewById(R.id.text_viewport_frame);
        textConnectionSpeed = findViewById(R.id.text_connection_speed);
        textHost = findViewById(R.id.text_host);
        textVersion = findViewById(R.id.text_versions);
        textDeviceInfo = findViewById(R.id.text_device_info);
    }

    public void setEntityInfo(String entityId) {
        textEntityId.setText(entityId);
    }

    public void setTextBufferHealth(String value) {
        textBufferHealth.setText(value);
    }

    public void setTextNetworkActivity(String value) {
        textNetworkActivity.setText(value);
    }

    public void setTextVolumeNormalized(String value) {
        textVolumeNormalized.setText(value);
    }

    public void setTextCurrentOptimalRes(String value) {
        textCurrentOptimalRes.setText(value);
    }

    public void setTextViewPortFrame(String value) {
        textViewPortFrame.setText(value);
    }

    public void setTextConnectionSpeed(String value) {
        textConnectionSpeed.setText(value);
    }

    public void setTextHost(String value) {
        textHost.setText(value);
    }

    public void setTextVersion(String value) {
        textVersion.setText(value);
    }

    public void setTextDeviceInfo(String value) {
        textDeviceInfo.setText(value);
    }
}
