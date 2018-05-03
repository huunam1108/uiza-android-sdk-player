package testlibuiza.sample.uizavideo.slide;

import android.content.res.Resources;
import android.os.Bundle;

import testlibuiza.R;
import vn.loitp.core.base.BaseActivity;
import vn.loitp.core.utilities.LLog;
import vn.loitp.views.layout.draggablepanel.DraggableListener;
import vn.loitp.views.layout.draggablepanel.DraggablePanel;

public class TestUizaVideoIMActivityRlSlide extends BaseActivity {
    private DraggablePanel draggablePanel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        draggablePanel = (DraggablePanel) findViewById(R.id.draggable_panel);
        initializeDraggablePanel();

        draggablePanel.setDraggableListener(new DraggableListener() {
            @Override
            public void onMaximized() {
                LLog.d(TAG, "onMaximized");
            }

            @Override
            public void onMinimized() {
                LLog.d(TAG, "onMinimized");
            }

            @Override
            public void onClosedToLeft() {
                LLog.d(TAG, "onClosedToLeft");
            }

            @Override
            public void onClosedToRight() {
                LLog.d(TAG, "onClosedToRight");
            }
        });
    }

    @Override
    protected boolean setFullScreen() {
        return false;
    }

    @Override
    protected String setTag() {
        return getClass().getSimpleName();
    }

    @Override
    protected int setLayoutResourceId() {
        return R.layout.test_uiza_ima_video_activity_rl_slide;
    }

    private void initializeDraggablePanel() throws Resources.NotFoundException {
        FrmTop frmTop = new FrmTop();
        FrmBottom frmBottom = new FrmBottom();

        draggablePanel.setFragmentManager(getSupportFragmentManager());
        draggablePanel.setTopFragment(frmTop);
        draggablePanel.setBottomFragment(frmBottom);

        //draggablePanel.setXScaleFactor(xScaleFactor);
        //draggablePanel.setYScaleFactor(yScaleFactor);
        draggablePanel.setTopViewHeight(800);
        //draggablePanel.setTopFragmentMarginRight(topViewMarginRight);
        //draggablePanel.setTopFragmentMarginBottom(topViewMargnBottom);
        draggablePanel.setClickToMaximizeEnabled(false);
        draggablePanel.setClickToMinimizeEnabled(false);

        draggablePanel.initializeView();
    }
}
