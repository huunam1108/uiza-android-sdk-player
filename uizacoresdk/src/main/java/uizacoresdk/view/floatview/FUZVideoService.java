package uizacoresdk.view.floatview;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import uizacoresdk.R;
import uizacoresdk.util.UZData;
import uizacoresdk.util.UZUtil;
import uizacoresdk.view.ComunicateMng;
import vn.uiza.core.common.Constants;
import vn.uiza.core.utilities.LAnimationUtil;
import vn.uiza.core.utilities.LConnectivityUtil;
import vn.uiza.core.utilities.LLog;
import vn.uiza.core.utilities.LScreenUtil;
import vn.uiza.core.utilities.LUIUtil;

/**
 * Created by loitp on 12/12/2018.
 */

public class FUZVideoService extends Service implements FUZVideo.Callback {
    private final String TAG = "TAG" + getClass().getSimpleName();
    private final int CLICK_ACTION_THRESHHOLD = 200;
    private WindowManager mWindowManager;
    private View mFloatingView;
    private RelativeLayout rlControl;
    private RelativeLayout moveView;
    private ImageButton btExit;
    private ImageButton btFullScreen;
    private ImageButton btPlayPause;
    private TextView tvMsg;
    private FUZVideo fuzVideo;
    private WindowManager.LayoutParams params;
    private String linkPlay;
    private Gson gson = new Gson();
    private boolean isLivestream;
    private int screenWidth;
    private int screenHeight;
    private int statusBarHeight;

    public FUZVideoService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isInitCustomLinkplay = intent.getBooleanExtra(Constants.FLOAT_USER_USE_CUSTOM_LINK_PLAY, false);
        //LLog.d(TAG, "onStartCommand isInitCustomLinkplay " + isInitCustomLinkplay);
        if (isInitCustomLinkplay) {
        } else {
            if (UZData.getInstance().getData() == null) {
                LLog.e(TAG, "onStartCommand data == null");
                return super.onStartCommand(intent, flags, startId);
            }
        }
        if (intent.getExtras() != null) {
            linkPlay = intent.getStringExtra(Constants.FLOAT_LINK_PLAY);
            isLivestream = intent.getBooleanExtra(Constants.FLOAT_IS_LIVESTREAM, false);
            //LLog.d(TAG, "linkPlay " + linkPlay + ", isLivestream: " + isLivestream);
            setupVideo();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void findViews() {
        rlControl = (RelativeLayout) mFloatingView.findViewById(R.id.rl_control);
        moveView = (RelativeLayout) mFloatingView.findViewById(R.id.move_view);
        btExit = (ImageButton) mFloatingView.findViewById(R.id.bt_exit);
        btFullScreen = (ImageButton) mFloatingView.findViewById(R.id.bt_full_screen);
        btPlayPause = (ImageButton) mFloatingView.findViewById(R.id.bt_play_pause);
        tvMsg = (TextView) mFloatingView.findViewById(R.id.tv_msg);
        LUIUtil.setTextShadow(tvMsg);
        tvMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LAnimationUtil.play(v, Techniques.Pulse, new LAnimationUtil.Callback() {
                    @Override
                    public void onCancel() {
                    }

                    @Override
                    public void onEnd() {
                        setupVideo();
                    }

                    @Override
                    public void onRepeat() {
                    }

                    @Override
                    public void onStart() {
                    }
                });
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        screenWidth = LScreenUtil.getScreenWidth();
        screenHeight = LScreenUtil.getScreenHeight();
        statusBarHeight = LScreenUtil.getStatusBarHeight(getApplicationContext());
        //LLog.d(TAG, "statusBarHeight " + statusBarHeight);
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_uiza_video, null);
        findViews();
        //Add the view to the window.
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        //OPTION 1: floatview se neo vao 1 goc cua device
        /*params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);*/

        //OPTION 2: floatview se ko neo vao 1 goc cua device
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                LAYOUT_FLAG,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);

        setSizeMoveView(true, false);

        //Specify the view position
        //Initially view will be added to top-left corner
        //params.gravity = Gravity.TOP | Gravity.LEFT;
        //params.x = 0;
        //params.y = 0;

        //right-bottom corner
        /*params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = screenWidth - getMoveViewWidth();
        params.y = screenHeight - getMoveViewHeight();*/

        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = screenWidth - getMoveViewWidth();
        //params.y = screenHeight - getMoveViewHeight();
        params.y = screenHeight - getMoveViewHeight() - statusBarHeight;//dell hieu sao phai tru getBottomBarHeight thi moi dung position :(
        LLog.d(TAG, "first position: " + params.x + "-" + params.y);

        fuzVideo = (FUZVideo) mFloatingView.findViewById(R.id.uiza_video);
        //Add the view to the window
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mFloatingView, params);
        //Drag and move floating view using user's touch action.
        dragAndMove();

        btExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
            }
        });
        btFullScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set UI
                int screenHeight = screenWidth * 9 / 16;
                moveView.getLayoutParams().width = screenWidth;
                moveView.getLayoutParams().height = screenHeight;
                moveView.requestLayout();
                updateUISlide(0, 0);
                rlControl.setVisibility(View.GONE);

                //stop video
                fuzVideo.getPlayer().setPlayWhenReady(false);
                LUIUtil.showProgressBar(fuzVideo.getProgressBar());
                moveView.setOnTouchListener(null);//disabled move view

                //bắn cho FloatClickFullScreenReceiver
                UZUtil.setClickedPip(getApplicationContext(), true);
                Intent intent = new Intent();
                intent.putExtra(Constants.FLOAT_CLICKED_PACKAGE_NAME, getPackageName());
                intent.setAction(Constants.FLOAT_CLICKED_FULLSCREEN);
                intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
                sendBroadcast(intent);
            }
        });
        btPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fuzVideo == null) {
                    return;
                }
                boolean isToggleResume = fuzVideo.togglePauseResume();
                if (isToggleResume) {
                    btPlayPause.setImageResource(R.drawable.baseline_pause_circle_outline_white_48);
                } else {
                    btPlayPause.setImageResource(R.drawable.baseline_play_circle_outline_white_48);
                }
            }
        });
    }

    private CountDownTimer countDownTimer;

    private void slideToPosition(final int goToPosX, final int goToPosY) {
        final int currentPosX;
        final int currentPosY;
        if (params.x < 0) {
            currentPosX = 0;
        } else if (params.x > screenWidth) {
            currentPosX = screenWidth;
        } else {
            currentPosX = params.x;
        }
        if (params.y < 0) {
            currentPosY = 0;
        } else if (params.y > screenHeight) {
            currentPosY = screenHeight;
        } else {
            currentPosY = params.y;
        }
        LLog.d(TAG, "slideToPosition current Point: " + currentPosX + " x " + currentPosY);
        final int a = (int) Math.abs(goToPosX - currentPosX);
        final int b = (int) Math.abs(goToPosY - currentPosY);
        LLog.d(TAG, "slideToPosition " + goToPosX + " x " + goToPosY + " -> a x b: " + a + " x " + b);

        countDownTimer = new CountDownTimer(300, 3) {
            public void onTick(long t) {
                float step = (300 - t) / 3;
                int tmpX;
                int tmpY;
                if (currentPosX > goToPosX) {
                    tmpX = currentPosX - (int) (a * step / 100);
                    tmpY = currentPosY - (int) (b * step / 100);
                } else {
                    tmpX = currentPosX + (int) (a * step / 100);
                    tmpY = currentPosY + (int) (b * step / 100);
                }
                //LLog.d(TAG, "slideToLeft onTick step: " + step + ", " + tmpX + " x " + tmpY);
                updateUISlide(tmpX, tmpY);
            }

            public void onFinish() {
                //LLog.d(TAG, "slideToLeft onFinish " + goToPosX + " x " + goToPosY);
                updateUISlide(goToPosX, goToPosY);
            }
        }.start();
    }

    private void updateUISlide(int x, int y) {
        params.x = x;
        params.y = y;
        mWindowManager.updateViewLayout(mFloatingView, params);
    }

    private void dragAndMove() {
        moveView.setOnTouchListener(new View.OnTouchListener() {
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;
            private long lastTouchDown;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;

                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();

                        lastTouchDown = System.currentTimeMillis();
                        return true;
                    case MotionEvent.ACTION_UP:
                        //onClick event
                        clickRoot(lastTouchDown);
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        //LLog.d(TAG, "ACTION_MOVE " + (int) event.getRawX() + " - " + (int) event.getRawY() + "___" + (initialX + (int) (event.getRawX() - initialTouchX) + " - " + (initialY + (int) (event.getRawY() - initialTouchY))));
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY);

                        //Update the layout with new X & Y coordinate
                        mWindowManager.updateViewLayout(mFloatingView, params);
                        //LLog.d(TAG, "ACTION_MOVE " + getLocationOnScreen(mFloatingView));
                        getLocationOnScreen(mFloatingView);
                        return true;
                }
                return false;
            }
        });
    }

    private enum POS {TOP, LEFT, BOTTOM, RIGHT, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, CENTER, OUT_LEFT, OUT_RIGHT, OUT_TOP, OUT_BOTTOM}

    private POS pos;

    private void notiPos(POS tmpPos) {
        if (pos != tmpPos) {
            pos = tmpPos;
            //LLog.d(TAG, "notiPos: " + pos);
        }
    }

    private void getLocationOnScreen(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        int posLeft = location[0];
        int posTop = location[1];
        int posRight = posLeft + view.getWidth();
        int posBottom = posTop + view.getHeight();
        //LLog.d(TAG, "getLocationOnScreen " + posLeft + " - " + posTop + " - " + posRight + " - " + posBottom);
        if (posLeft == 0) {
            if (posTop == 0) {
                //LLog.d(TAG, "TOP_LEFT");
                notiPos(POS.TOP_LEFT);
            } else if (posBottom == screenHeight) {
                //LLog.d(TAG, "BOTTOM_LEFT");
                notiPos(POS.BOTTOM_LEFT);
            } else {
                //LLog.d(TAG, "LEFT");
                notiPos(POS.LEFT);
            }
        } else {
            if (posRight == screenWidth) {
                if (posTop == 0) {
                    //LLog.d(TAG, "TOP_RIGHT");
                    notiPos(POS.TOP_RIGHT);
                } else if (posBottom == screenHeight) {
                    //LLog.d(TAG, "BOTTOM_RIGHT");
                    notiPos(POS.BOTTOM_RIGHT);
                } else {
                    //LLog.d(TAG, "RIGHT");
                    notiPos(POS.RIGHT);
                }
            } else {
                if (posTop == 0) {
                    //LLog.d(TAG, "TOP");
                    notiPos(POS.TOP);
                } else if (posBottom == screenHeight) {
                    //LLog.d(TAG, "BOTTOM");
                    notiPos(POS.BOTTOM);
                } else {
                    //LLog.d(TAG, "CENTER");
                    notiPos(POS.CENTER);
                }
            }
        }
    }

    private void clickRoot(long lastTouchDown) {
        //works fine
        if (System.currentTimeMillis() - lastTouchDown < CLICK_ACTION_THRESHHOLD) {
            //click vao root se to hon, click lai de thu nho
            /*if (rlControl.getVisibility() == View.VISIBLE) {
                rlControl.setVisibility(View.GONE);
                setSizeMoveView(false, false);
            } else {
                rlControl.setVisibility(View.VISIBLE);
                setSizeMoveView(false, true);
            }*/
            btFullScreen.performClick();
        }
        if (pos == POS.CENTER && rlControl.getVisibility() == View.GONE) {
            int posX = params.x;
            int posY = params.y;
            int centerPosX = posX + getMoveViewWidth() / 2;
            int centerPosY = posY + getMoveViewHeight() / 2;
            LLog.d(TAG, "->" + posX + " x " + posY + " -> " + centerPosX + " x " + centerPosY);
            if (centerPosX < screenWidth / 2) {
                slideToPosition(0, posY);
            } else {
                slideToPosition(screenWidth - getMoveViewWidth(), posY);
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mFloatingView != null) {
            mWindowManager.removeView(mFloatingView);
        }
        if (fuzVideo != null) {
            fuzVideo.onDestroy();
        }
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        UZUtil.setClickedPip(getApplicationContext(), false);
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private long lastCurrentPosition;

    private void setListener() {
        //LLog.d(TAG, TAG + " addListener");
        if (fuzVideo == null || fuzVideo.getPlayer() == null) {
            return;
        }
        fuzVideo.getPlayer().addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                //LLog.e(TAG, "onPlayerError " + error.getMessage());
                lastCurrentPosition = fuzVideo.getPlayer().getCurrentPosition();
                //LLog.d(TAG, "onPlayerError lastCurrentPosition " + lastCurrentPosition);
                setupVideo();
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
            }

            @Override
            public void onSeekProcessed() {
            }
        });
    }

    @Override
    public void isInitResult(boolean isInitSuccess) {
        if (isInitSuccess && fuzVideo != null) {
            //LLog.d(TAG, "isInitResult seekTo lastCurrentPosition: " + lastCurrentPosition + ", isSendMsgToActivity: " + isSendMsgToActivity);
            editSizeOfMoveView();
            setListener();
            if (lastCurrentPosition > 0) {
                fuzVideo.getPlayer().seekTo(lastCurrentPosition);
            }
            if (!isSendMsgToActivity) {
                //LLog.d(TAG, "isPiPInitResult isSendMsgToActivity false -> send msg to UZVideo");
                //bắn cho UZVideo nhận
                ComunicateMng.MsgFromServiceIsInitSuccess msgFromServiceIsInitSuccess = new ComunicateMng.MsgFromServiceIsInitSuccess(null);
                msgFromServiceIsInitSuccess.setInitSuccess(isInitSuccess);
                ComunicateMng.postFromService(msgFromServiceIsInitSuccess);
                isSendMsgToActivity = true;
            }
        }
    }

    @Override
    public void onPlayerStateEnded() {
        //TODO Cần check xem nếu play pip ở playlist folder thì auto next, còn nếu là entity thì thôi
        /*if (UZData.getInstance().isPlayWithPlaylistFolder()) {
            LLog.d(TAG, "Dang play o che do playlist folder -> auto switch next data");
        } else {
            LLog.d(TAG, "Dang play o che do entity -> do nothing");
        }*/
        stopSelf();
    }

    private boolean isSendMsgToActivity;

    private void setupVideo() {
        if (linkPlay == null || linkPlay.isEmpty()) {
            //LLog.d(TAG, "setupVideo linkPlay == null || linkPlay.isEmpty()");
            return;
        }
        //LLog.d(TAG, "setupVideo linkPlay " + linkPlay + ", isLivestream: " + isLivestream);
        if (LConnectivityUtil.isConnected(this)) {
            fuzVideo.init(linkPlay, isLivestream, this);
            tvMsg.setVisibility(View.GONE);
        } else {
            tvMsg.setVisibility(View.VISIBLE);
        }
    }

    //click vo se larger, click lan nua de smaller
    private void setSizeMoveView(boolean isFirstSizeInit, boolean isLarger) {
        if (moveView == null) {
            return;
        }
        int w = 0;
        int h = 0;
        if (isFirstSizeInit) {
            w = screenWidth / 2;
            h = w * 9 / 16;
        } else {
            //works fine
            //OPTION 1: isLarger->mini player se to hon 1 chut
            //!isLarger->ve trang thai ban dau
            /*if (isLarger) {
                w = getMoveViewWidth() * 120 / 100;
                h = getMoveViewHeight() * 120 / 100;
            } else {
                int videoW = fuzVideo.getVideoW();
                int videoH = fuzVideo.getVideoH();
                w = screenWidth / 2;
                h = w * videoH / videoW;
            }*/
        }
        if (w != 0 && h != 0) {
            moveView.getLayoutParams().width = w;
            moveView.getLayoutParams().height = h;
            //LLog.d(TAG, "setSizeMoveView isFirstSizeInit:" + isFirstSizeInit + ",isLarger: " + isLarger + ", " + w + "x" + h);
            moveView.requestLayout();
        }
    }

    private int getMoveViewWidth() {
        if (moveView == null) {
            return 0;
        }
        return moveView.getLayoutParams().width;
    }

    private int getMoveViewHeight() {
        if (moveView == null) {
            return 0;
        }
        return moveView.getLayoutParams().height;
    }

    private int getVideoW() {
        if (fuzVideo == null) {
            return 0;
        }
        return fuzVideo.getVideoW();
    }

    private int getVideoH() {
        if (fuzVideo == null) {
            return 0;
        }
        return fuzVideo.getVideoH();
    }

    private void editSizeOfMoveView() {
        if (fuzVideo == null || moveView == null) {
            return;
        }
        int videoW = fuzVideo.getVideoW();
        int videoH = fuzVideo.getVideoH();
        int moveW = getMoveViewWidth();
        int moveH = moveW * videoH / videoW;
        //LLog.d(TAG, "editSizeOfMoveView " + videoW + "x" + videoH + " -> " + moveW + "x" + moveH);
        moveView.getLayoutParams().width = moveW;
        moveView.getLayoutParams().height = moveH;
        moveView.requestLayout();
    }

    //listen msg from UZVideo
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ComunicateMng.MsgFromActivity msg) {
        if (msg == null) {
            return;
        }
        if (msg instanceof ComunicateMng.MsgFromActivityPosition) {
            //Nhận được vị trí từ UZVideo, tiến hành seek tới vị trí này
            //LLog.d(TAG, "MsgFromActivityPosition position " + ((ComunicateMng.MsgFromActivityPosition) msg).getPosition());
            if (fuzVideo != null) {
                fuzVideo.seekTo(((ComunicateMng.MsgFromActivityPosition) msg).getPosition());
            }
        } else if (msg instanceof ComunicateMng.MsgFromActivityIsInitSuccess) {
            //lắng nghe UZVideo đã init success hay chưa
            //LLog.d(TAG, "MsgFromActivityIsInitSuccess isInitSuccess: " + ((ComunicateMng.MsgFromActivityIsInitSuccess) msg).isInitSuccess());
            if (fuzVideo != null) {
                //LLog.d(TAG, "getCurrentPosition: " + floatUizaIMAVideo.getCurrentPosition());
                //lấy vị trí của pip hiện tại để bắn cho UZVideo
                ComunicateMng.MsgFromServicePosition msgFromServicePosition = new ComunicateMng.MsgFromServicePosition(null);
                msgFromServicePosition.setPosition(fuzVideo.getCurrentPosition());
                ComunicateMng.postFromService(msgFromServicePosition);
                stopSelf();
            }
        }
    }
}