package uizacoresdk.view.rl.video;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.audio.AudioAttributes;
import com.google.android.exoplayer2.decoder.DecoderCounters;

import com.google.android.exoplayer2.metadata.Metadata;
import com.google.android.exoplayer2.source.MediaSourceEventListener;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import java.io.IOException;
import java.util.Locale;

import vn.uiza.core.common.Constants;

final class UZPlayerHelper {
    private static final String PLAYER_STATE_FORMAT = "playWhenReady:%s playbackState:%s window:%s";
    private static final String BUFFERING = "buffering";
    private static final String ENDED = "ended";
    private static final String IDLE = "idle";
    private static final String READY = "ready";
    private static final String UNKNOWN = "unknown";
    private SimpleExoPlayer player;

    UZPlayerHelper(SimpleExoPlayer player) {
        this.player = player;
    }

    boolean isPlayerValid() {
        return player != null;
    }

    void setPlayWhenReady(boolean ready) {
        if (isPlayerValid()) {
            player.setPlayWhenReady(ready);
        }
    }

    void release() {
        if (isPlayerValid()) {
            player.release();
            player = null;
        }
    }

    float getVolume() {
        if (isPlayerValid()) {
            return player.getVolume();
        }
        return Constants.NOT_FOUND;
    }

    void setVolume(float value) {
        if (isPlayerValid()) {
            player.setVolume(value);
        }
    }

    boolean seekTo(long positionMs) {
        if (isPlayerValid()) {
            player.seekTo(positionMs);
            return true;
        }
        return false;
    }

    void seekToDefaultPosition() {
        if (isPlayerValid()) {
            player.seekToDefaultPosition();
        }
    }

    void seekToForward(long forward) {
        if (!isPlayerValid()) {
            return;
        }
        if (player.getCurrentPosition() + forward > player.getDuration()) {
            player.seekTo(player.getDuration());
        } else {
            player.seekTo(player.getCurrentPosition() + forward);
        }
    }

    void seekToBackward(long backward) {
        if (!isPlayerValid()) {
            return;
        }
        if (player.getCurrentPosition() - backward > 0) {
            player.seekTo(player.getCurrentPosition() - backward);
        } else {
            player.seekTo(0);
        }
    }

    long getCurrentPosition() {
        if (!isPlayerValid()) {
            return 0;
        }
        return player.getCurrentPosition();
    }

    long getDuration() {
        if (!isPlayerValid()) {
            return 0;
        }
        return player.getDuration();
    }

    boolean isVOD() {
        if (!isPlayerValid()) {
            return false;
        }
        return !player.isCurrentWindowDynamic();
    }

    boolean isLIVE() {
        if (!isPlayerValid()) {
            return false;
        }
        return player.isCurrentWindowDynamic();
    }

    /**
     * Returns a string containing player state debugging information.
     */
    String getPlayerStateString() {
        if (!isPlayerValid()) {
            return null;
        }
        String playbackStateString;
        switch (player.getPlaybackState()) {
            case Player.STATE_BUFFERING:
                playbackStateString = BUFFERING;
                break;
            case Player.STATE_ENDED:
                playbackStateString = ENDED;
                break;
            case Player.STATE_IDLE:
                playbackStateString = IDLE;
                break;
            case Player.STATE_READY:
                playbackStateString = READY;
                break;
            default:
                playbackStateString = UNKNOWN;
                break;
        }
        return String.format(PLAYER_STATE_FORMAT, player.getPlayWhenReady(), playbackStateString, player.getCurrentWindowIndex());
    }

    /**
     * Returns a string containing video debugging information.
     */
    String getVideoString() {
        if (!isPlayerValid()) {
            return null;
        }
        Format format = player.getVideoFormat();
        if (format == null) {
            return "";
        }
        return "\n" + format.sampleMimeType + "(id:" + format.id + " r:" + format.width + "x"
                + format.height + getPixelAspectRatioString(format.pixelWidthHeightRatio)
                + getDecoderCountersBufferCountString(player.getVideoDecoderCounters()) + ")";
    }

    String getDecoderCountersBufferCountString(DecoderCounters counters) {
        if (counters == null) {
            return "";
        }
        counters.ensureUpdated();
        return " sib:" + counters.skippedInputBufferCount
                + " sb:" + counters.skippedOutputBufferCount
                + " rb:" + counters.renderedOutputBufferCount
                + " db:" + counters.droppedBufferCount
                + " mcdb:" + counters.maxConsecutiveDroppedBufferCount
                + " dk:" + counters.droppedToKeyframeCount;
    }

    String getPixelAspectRatioString(float pixelAspectRatio) {
        return pixelAspectRatio == Format.NO_VALUE || pixelAspectRatio == 1f ? "" : (" par:" + String.format(Locale.US, "%.02f", pixelAspectRatio));
    }

    int getVideoProfileW() {
        if (!isPlayerValid()) {
            return Constants.UNKNOW;
        }
        Format format = player.getVideoFormat();
        if (format == null) {
            return Constants.UNKNOW;
        }
        return format.width;
    }

    int getVideoProfileH() {
        if (!isPlayerValid()) {
            return Constants.UNKNOW;
        }
        Format format = player.getVideoFormat();
        if (format == null) {
            return Constants.UNKNOW;
        }
        return format.height;
    }

    /**
     * Returns a string containing audio debugging information.
     */
    String getAudioString() {
        if (player == null) {
            return null;
        }
        Format format = player.getAudioFormat();
        if (format == null) {
            return "";
        }
        return "\n" + format.sampleMimeType
                + "(id:" + format.id
                + " hz:" + format.sampleRate
                + " ch:" + format.channelCount
                + getDecoderCountersBufferCountString(player.getAudioDecoderCounters()) + ")";
    }

    long getContentPosition() {
        if (!isPlayerValid()) {
            return 0;
        }
        return player.getContentPosition();
    }

    SimpleExoPlayer getPlayer() {
        return player;
    }

    void addListener() {
        if (!isPlayerValid()) return;
        player.addAnalyticsListener(new AnalyticsListener() {
            @Override
            public void onPlayerStateChanged(EventTime eventTime, boolean playWhenReady,
                    int playbackState) {
                Log.e("x", "xxxx onPlayerStateChanged");
            }

            @Override
            public void onTimelineChanged(EventTime eventTime, int reason) {
                Log.e("x", "xxxx onTimelineChanged");
            }

            @Override
            public void onPositionDiscontinuity(EventTime eventTime, int reason) {
                Log.e("x", "xxxx onPositionDiscontinuity");
            }

            @Override
            public void onSeekStarted(EventTime eventTime) {
                Log.e("x", "xxxx onSeekStarted");
            }

            @Override
            public void onSeekProcessed(EventTime eventTime) {
                Log.e("x", "xxxx onSeekProcessed");
            }

            @Override
            public void onPlaybackParametersChanged(EventTime eventTime,
                    PlaybackParameters playbackParameters) {
                Log.e("x", "xxxx onPlaybackParametersChanged");
            }

            @Override
            public void onRepeatModeChanged(EventTime eventTime, int repeatMode) {
                Log.e("x", "xxxx onRepeatModeChanged");
            }

            @Override
            public void onShuffleModeChanged(EventTime eventTime, boolean shuffleModeEnabled) {
                Log.e("x", "xxxx onShuffleModeChanged");
            }

            @Override
            public void onLoadingChanged(EventTime eventTime, boolean isLoading) {
                Log.e("x", "xxxx onLoadingChanged");
            }

            @Override
            public void onPlayerError(EventTime eventTime, ExoPlaybackException error) {
                Log.e("x", "xxxx onPlayerError");
            }

            @Override
            public void onTracksChanged(EventTime eventTime, TrackGroupArray trackGroups,
                    TrackSelectionArray trackSelections) {
                Log.e("x", "xxxx onTracksChanged");
            }

            @Override
            public void onLoadStarted(EventTime eventTime,
                    MediaSourceEventListener.LoadEventInfo loadEventInfo,
                    MediaSourceEventListener.MediaLoadData mediaLoadData) {
                Log.e("x", "xxxx onLoadStarted");
            }

            @Override
            public void onLoadCompleted(EventTime eventTime,
                    MediaSourceEventListener.LoadEventInfo loadEventInfo,
                    MediaSourceEventListener.MediaLoadData mediaLoadData) {
                Log.e("x", "xxxx onLoadCompleted");
            }

            @Override
            public void onLoadCanceled(EventTime eventTime,
                    MediaSourceEventListener.LoadEventInfo loadEventInfo,
                    MediaSourceEventListener.MediaLoadData mediaLoadData) {
                Log.e("x", "xxxx onLoadCanceled");
            }

            @Override
            public void onLoadError(EventTime eventTime,
                    MediaSourceEventListener.LoadEventInfo loadEventInfo,
                    MediaSourceEventListener.MediaLoadData mediaLoadData, IOException error,
                    boolean wasCanceled) {
                Log.e("x", "xxxx onLoadError");
            }

            @Override
            public void onDownstreamFormatChanged(EventTime eventTime,
                    MediaSourceEventListener.MediaLoadData mediaLoadData) {
                Log.e("x", "xxxx onDownstreamFormatChanged");
            }

            @Override
            public void onUpstreamDiscarded(EventTime eventTime,
                    MediaSourceEventListener.MediaLoadData mediaLoadData) {
                Log.e("x", "xxxx onUpstreamDiscarded");
            }

            @Override
            public void onMediaPeriodCreated(EventTime eventTime) {
                Log.e("x", "xxxx onMediaPeriodCreated");
            }

            @Override
            public void onMediaPeriodReleased(EventTime eventTime) {
                Log.e("x", "xxxx onMediaPeriodReleased");
            }

            @Override
            public void onReadingStarted(EventTime eventTime) {
                Log.e("x", "xxxx onReadingStarted");
            }

            @Override
            public void onBandwidthEstimate(EventTime eventTime, int totalLoadTimeMs,
                    long totalBytesLoaded, long bitrateEstimate) {
                Log.e("x", "xxxx onBandwidthEstimate");
            }

            @Override
            public void onSurfaceSizeChanged(EventTime eventTime, int width, int height) {
                Log.e("x", "xxxx onSurfaceSizeChanged");
            }

            @Override
            public void onMetadata(EventTime eventTime, Metadata metadata) {
                Log.e("x", "xxxx onMetadata");
            }

            @Override
            public void onDecoderEnabled(EventTime eventTime, int trackType,
                    DecoderCounters decoderCounters) {
                Log.e("x", "xxxx onDecoderEnabled");
            }

            @Override
            public void onDecoderInitialized(EventTime eventTime, int trackType, String decoderName,
                    long initializationDurationMs) {
                Log.e("x", "xxxx onDecoderInitialized");
            }

            @Override
            public void onDecoderInputFormatChanged(EventTime eventTime, int trackType,
                    Format format) {
                Log.e("x", "xxxx onDecoderInputFormatChanged");
            }

            @Override
            public void onDecoderDisabled(EventTime eventTime, int trackType,
                    DecoderCounters decoderCounters) {
                Log.e("x", "xxxx onDecoderDisabled");
            }

            @Override
            public void onAudioSessionId(EventTime eventTime, int audioSessionId) {
                Log.e("x", "xxxx onAudioSessionId");
            }

            @Override
            public void onAudioAttributesChanged(EventTime eventTime,
                    AudioAttributes audioAttributes) {
                Log.e("x", "xxxx onAudioAttributesChanged");
            }

            @Override
            public void onVolumeChanged(EventTime eventTime, float volume) {
                Log.e("x", "xxxx onVolumeChanged");
            }

            @Override
            public void onAudioUnderrun(EventTime eventTime, int bufferSize, long bufferSizeMs,
                    long elapsedSinceLastFeedMs) {
                Log.e("x", "xxxx onAudioUnderrun");
            }

            @Override
            public void onDroppedVideoFrames(EventTime eventTime, int droppedFrames,
                    long elapsedMs) {
                Log.e("x", "xxxx onDroppedVideoFrames");
            }

            @Override
            public void onVideoSizeChanged(EventTime eventTime, int width, int height,
                    int unappliedRotationDegrees, float pixelWidthHeightRatio) {
                Log.e("x", "xxxx onVideoSizeChanged");
            }

            @Override
            public void onRenderedFirstFrame(EventTime eventTime, @Nullable Surface surface) {
                Log.e("x", "xxxx onRenderedFirstFrame");
            }

            @Override
            public void onDrmSessionAcquired(EventTime eventTime) {
                Log.e("x", "xxxx onDrmSessionAcquired");
            }

            @Override
            public void onDrmKeysLoaded(EventTime eventTime) {
                Log.e("x", "xxxx onDrmKeysLoaded");
            }

            @Override
            public void onDrmSessionManagerError(EventTime eventTime, Exception error) {
                Log.e("x", "xxxx onDrmSessionManagerError");
            }

            @Override
            public void onDrmKeysRestored(EventTime eventTime) {
                Log.e("x", "xxxx onDrmKeysRestored");
            }

            @Override
            public void onDrmKeysRemoved(EventTime eventTime) {
                Log.e("x", "xxxx onDrmKeysRemoved");
            }

            @Override
            public void onDrmSessionReleased(EventTime eventTime) {
                Log.e("x", "xxxx onDrmSessionReleased");
            }
        });
    }
}
