package app.originality.com.originality.ui.fragment;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

import app.originality.com.originality.R;
import app.originality.com.originality.config.Contants;
import app.originality.com.originality.util.view.ImageCycleView;
import app.originality.com.originality.video.VideoPlayerActivity;

public class MusicListFragment extends BaseFragment implements View.OnClickListener {

    private ImageCycleView mImageCycleView;
    private ArrayList<String> mImgResources;

    @Override
    protected int setView() {
        return R.layout.fragment_music_list;
    }

    @Override
    protected void findViews() {
        mImageCycleView = (ImageCycleView) this.findViewById(R.id.id_imagecycleview);
    }

    @Override
    protected void init() {
        mImgResources = new ArrayList<String>();
        for (int i = 0; i < 8; i++) {
            mImgResources.add(Contants.imageUrls[i]);
        }
        mImageCycleView.setImageResources(mImgResources, new OnImageCycleListenner());
    }


    @Override
    protected void initEvent() {
    }


    @Override
    protected void loadData() {
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_music_img:
//                Intent intent = new Intent(mAct, MusicListActivity.class);
//                startActivity(intent);
                Intent intent = new Intent(mAct, VideoPlayerActivity.class);
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

        }
    }

}