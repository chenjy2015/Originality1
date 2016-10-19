package app.originality.com.originality.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;

import com.blankj.utilcode.utils.FileUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.originality.com.originality.R;
import app.originality.com.originality.config.Contants;
import app.originality.com.originality.util.view.ImageCycleView;
import app.originality.com.originality.video.VideoPlayerActivity;

public class VideoListActivity extends Activity implements View.OnClickListener{

    private ImageCycleView mImageCycleView;
    public  ArrayList<String> mImgResources;
    public static List<File> mVideFiles;     //视频文件

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setView();
        findViews();
        init();
    }

    protected int setView() {
        return R.layout.activity_video_list;
    }

    protected void findViews() {
        mImageCycleView = (ImageCycleView) this.findViewById(R.id.id_imagecycleview);
    }

    protected void init() {

    }


    protected void loadData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                File file = Environment.getExternalStorageDirectory();
                if (file != null) {
                    searchFile(file);
                    mHandler.sendEmptyMessage(0);
                }
            }
        }).start();
    }


    public Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mVideFiles != null || mVideFiles.size() > 0) {
                mImgResources = new ArrayList<String>();
                for (int i = 0; i < mVideFiles.size(); i++) {
                    mImgResources.add(Contants.imageUrls[i]);
                }
                mImageCycleView.setImageResources(mImgResources, new OnImageCycleListenner());
            }
        }
    };

    private static File videoFile = null;

    /**
     * 递归算法 查找出包含RMVB的视频文件
     *
     * @param file
     */
    public void searchFile(File file) {
//        if (file.isDirectory()) {
//            File[] files = file.listFiles();
//            if(files != null){
//                for (File f : files) {
//                    searchFile(f);
//                }
//            }
//        } else if (file.getName().endsWith(".mp4")) {
//            videoFile = file;
//            return;
//        }
        mVideFiles = FileUtils.listFilesInDirWithFilter(file.getPath(), ".mp4");
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_music_img:
                Intent intent = new Intent(VideoListActivity.this, MusicListActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * 轮播图回调
     */
    public class OnImageCycleListenner implements ImageCycleView.ImageCycleViewListener {

        //展示图片
        @Override
        public void displayImage(String imageURL, ImageView imageView) {
            ImageLoader.getInstance().displayImage(imageURL, imageView);
        }

        //图片点击
        @Override
        public void onImageClick(int position, View imageView) {
            Intent intent = new Intent(VideoListActivity.this, VideoPlayerActivity.class);
            intent.putExtra("index", position);
            startActivity(intent);
        }
    }
}
