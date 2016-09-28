package app.originality.com.originality.util.media;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.SurfaceHolder;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import app.originality.com.originality.application.OApplication;
import app.originality.com.originality.bean.MusicSourceVO;
import app.originality.com.originality.util.AndroidSystemHelper;
import app.originality.com.originality.util.LogOut;

/**
 * @author cjy
 * @version V1.0
 * @company Chenjy_Studio
 * @Description 多媒体播放辅助类
 * @date 2016/9/3 11:50
 */
public class MediaHelper extends ActivityMethod {

    public ArrayList<MusicSourceVO> mMusicSourceData;

    public Gson mGson;

    private MediaPlayer mMediaPlayer;

    private static MediaHelper mInstance;

    private static MediaPlayerProgressListenner mMediaPlayerProgressListenner;   //当前多媒体播放进度值

    private static Timer mTimer;

    private static Context mContext;

    private MediaHelper() {
        init();
        initMediaPlayer();
    }

    public synchronized static MediaHelper getInstance() {
        if (mInstance == null) {
            mInstance = new MediaHelper();
        }
        return mInstance;
    }

    public void init() {
        mContext = OApplication.mContext;
        mGson = new Gson();
    }

    /**
     * 设置多媒体播放进度回调
     *
     * @param mediaPlayerProgressListenner
     */
    public void setMediaPlayerProgressListenner(MediaPlayerProgressListenner mediaPlayerProgressListenner) {
        mMediaPlayerProgressListenner = mediaPlayerProgressListenner;
        mMediaPlayerProgressListenner.onPlayerStart(getDuration());
        if (mediaPlayerProgressListenner != null) {
            initMediaPlayerChange();
        }
    }

    /**
     * 初始化媒体播放类
     * 判断：
     * 1.如果上下文对象为空
     * 2.媒体播放器服务类不为空且已经在运行中
     * 则不进行二次初始化操作
     */
    public void initMediaPlayer() {
        if (mContext == null || mMediaPlayer != null || AndroidSystemHelper.isServiceWorked(mContext, MediaPlayerService.class.getName())) {
            return;
        }
        mMediaPlayer = new MediaPlayer();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    /**
     * 播放本地音乐文件
     *
     * @param path
     */
    private void playerMusicByLocation(String path) {
        try {
            if (mMediaPlayer == null) {
                return;
            }
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
            mMediaPlayer.setDataSource(path);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 播放本地asset音乐文件
     *
     * @param fileName asset中的文件名称
     */
    private void playerMusicByAsset(String fileName) {
        try {
            if (mMediaPlayer == null) {
                return;
            }
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.pause();
            mMediaPlayer.seekTo(0);
            AssetManager am = mContext.getAssets();//获得该应用的AssetManager
            AssetFileDescriptor afd = am.openFd(fileName);
            mMediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放本地asset音乐文件
     *
     * @param sourceId raw中文件id
     */
    private void playerMusicByRaw(String sourceId) {
        try {
            mMediaPlayer = MediaPlayer.create(mContext, Integer.parseInt(sourceId));
            mMediaPlayer.setLooping(false);
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            mMediaPlayer = new MediaPlayer();
        } catch (Exception e) {
            e.printStackTrace();
            mMediaPlayer = new MediaPlayer();
        }
    }


    /**
     * 播放网络资源音乐
     *
     * @param url
     */
    private void playerMusicByInterner(String url) {
        Uri uri = Uri.parse(url);
        try {
            if (mMediaPlayer == null) {
                return;
            }
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mContext, uri);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            LogOut.printStackTrace(e);
            onDestroy();
        } catch (Exception e) {
            LogOut.printStackTrace(e);
            onDestroy();
        }
    }


    /**
     * 播放网络资源视频
     *
     * @param url
     */
    private void playerVideoByInterner(String url, SurfaceHolder mSurfaceHolder) {
        Uri uri = Uri.parse(url);
        try {
            if (mMediaPlayer == null) {
                return;
            }
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音乐流的类型
            mMediaPlayer.setDisplay(mSurfaceHolder);//设置video影片以surfaceviewholder播放
            mMediaPlayer.setDataSource(mContext, uri);
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            LogOut.printStackTrace(e);
            onDestroy();
        } catch (Exception e) {
            LogOut.printStackTrace(e);
            onDestroy();
        }
    }


    /**
     * 播放本地资源视频
     *
     * @param filePath
     */
    private void playerVideoBySDCard(String filePath, SurfaceHolder mSurfaceHolder) {
        try {
            if (mMediaPlayer == null) {
                return;
            }
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                mMediaPlayer = new MediaPlayer();
            }
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);//设置音乐流的类型
            mMediaPlayer.setDisplay(mSurfaceHolder);//设置video影片以surfaceviewholder播放
            mMediaPlayer.setDataSource(filePath);
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            LogOut.printStackTrace(e);
            onDestroy();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            LogOut.printStackTrace(e);
            onDestroy();
        }
    }

    public void player(MediaType mediaType, String url, MediaPlayerProgressListenner mediaPlayerProgressListenner) {
        //媒体类型 ； 音乐，资源来源：互联网
        if (mediaType == MediaType.MUSIC_INTERNER) {
            playerMusicByInterner(url);
            //媒体类型 ； 音乐，资源来源：本地
        } else if (mediaType == MediaType.MUSIC_LOCATION) {
            playerMusicByLocation(url);
            //媒体类型： 音频，资源来源：本地 asset
        } else if (mediaType == MediaType.MUSIC_ASSET) {
            playerMusicByAsset(url);
        } else if (mediaType == MediaType.MUSIC_RAW) {
            playerMusicByRaw(Integer.parseInt(url) + "");
            //媒体类型 ； 视频，资源来源：互联网
        } else if (mediaType == MediaType.VIDEO_INTERNER) {

            //媒体类型 ； 视频，资源来源：本地
        } else if (mediaType == MediaType.VIDEO_LOCATION) {

        }
        if (mediaPlayerProgressListenner != null) {
            setMediaPlayerProgressListenner(mediaPlayerProgressListenner);
        }
    }

    /**
     * 视频播放
     *
     * @param mediaType
     * @param url
     * @param surfaceHolder                     //视频展示依附View Holder
     * @param mediaPlayerProgressListenner      //播放进度监听
     */
    public void playerVideo(MediaType mediaType, String url, SurfaceHolder surfaceHolder, MediaPlayerProgressListenner mediaPlayerProgressListenner) {
        //媒体类型 ； 视频，资源来源：互联网
        if (mediaType == MediaType.VIDEO_INTERNER) {
            playerVideoByInterner(url, surfaceHolder);
            //媒体类型 ； 视频，资源来源：本地
        } else if (mediaType == MediaType.VIDEO_LOCATION) {
            playerVideoBySDCard(url, surfaceHolder);
        }
        if (mediaPlayerProgressListenner != null) {
            setMediaPlayerProgressListenner(mediaPlayerProgressListenner);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (mMediaPlayer == null) {
            return;
        }
        try {
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRestart() {
        super.onRestart();
        if (mMediaPlayer == null) {
            return;
        }
        try {
            mMediaPlayer.reset();
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMediaPlayerProgressListenner != null) {
            mMediaPlayerProgressListenner.onPlayerPause();
        }
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mMediaPlayerProgressListenner != null) {
            mMediaPlayerProgressListenner.onPlayerStop();
        }
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
            mMediaPlayerProgressListenner.onPlayerStop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        if(mMediaPlayerProgressListenner != null){
            mMediaPlayerProgressListenner.onPlayerStop();
        }
        if (mMediaPlayer != null) {
            try {
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.stop();
                }
            } catch (IllegalStateException e) {
                LogOut.printStackTrace(e);
            }
            mMediaPlayer.release();
        }
    }




    /**
     * 获取当前播放进度值
     *
     * @return
     */
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * 获取 文件一共播放长度值
     *
     * @return
     */
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }

    /**
     * 移动播放进度
     * @param position
     */
    public void seekTo(int position) {
        if (mMediaPlayer == null) {
            return;
        }
        try {
            if (mMediaPlayer.isPlaying()) {
                mMediaPlayer.pause();
            }
            mMediaPlayer.seekTo(position);
//            mMediaPlayer.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    public void initMediaPlayerChange() {
        //----------定时器记录播放进度--------- //
        mTimer = new Timer();
        TimerTask mTimerTask = new TimerTask() {
            @Override
            public void run() {
                if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
                    mHandler.sendEmptyMessage(0);
                }
            }
        };
        mTimer.schedule(mTimerTask, 0, 10);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mMediaPlayerProgressListenner.onPlayerChange(getCurrentPosition());
        }
    };
}