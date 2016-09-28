package com.oxbix.spanlogistics;

import android.app.Activity;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.oxbix.spanlogistics.adapter.SharePlatformAdapter;
import com.oxbix.spanlogistics.bean.PlatformVO;
import com.oxbix.spanlogistics.recevied.SmsReceiver;
import com.oxbix.spanlogistics.util.ShareHelper;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;

import java.util.List;


public class LauncherActivity extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private RelativeLayout mBottomLayout;
    private GridView mGridView;
    private SharePlatformAdapter sharePlatformAdapter;
    private List<PlatformVO> mData;
    private ImageView mCanImg;
    private Activity that;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        that = LauncherActivity.this;
        setContentView(R.layout.activity_main);
        findView();
        initEvent();

        registSmsReceive();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver();
    }

    SmsReceiver mSmsReceive;
    /**
     * 注册短信监听广播
     */
    private void registSmsReceive() {
        mSmsReceive = new SmsReceiver();
        IntentFilter intentFilter = new IntentFilter(SmsReceiver.SMS_SEND_ACTIOIN);
//        intentFilter.addAction(SmsReceiver.SMS_SEND_ACTIOIN);       //发送状态
//        intentFilter.addAction(SmsReceiver.SMS_DELIVERED_ACTION);   //接收状态
        intentFilter.setPriority(1000);
        registerReceiver(mSmsReceive, intentFilter);
    }

    /**
     * 取消注册短信监听广播
     */
    private void unregisterReceiver() {
        if (mSmsReceive != null) {
            unregisterReceiver(mSmsReceive);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData();
    }

    public void findView() {
        mBottomLayout = (RelativeLayout) that.findViewById(R.id.id_bottom_layout);
        mGridView = (GridView) that.findViewById(R.id.id_gridview);
        mCanImg = (ImageView) that.findViewById(R.id.id_cancel);
    }

    public void initEvent() {
        mGridView.setOnItemClickListener(this);
        mCanImg.setOnClickListener(this);
    }

    public void loadData() {
        mData = ShareHelper.getShareResource(that);
        sharePlatformAdapter = new SharePlatformAdapter(that, mData);
        mGridView.setAdapter(sharePlatformAdapter);
        ShareHelper.setListViewHeightBasedOnChildren(mGridView, 3);
        sharePlatformAdapter.notifyDataSetChanged();
        mBottomLayout.getLayoutParams().height = mGridView.getLayoutParams().height + mCanImg.getLayoutParams().height + 60;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(that.getApplicationContext(), "item " + position + " clicked" + "share by " + mData.get(position).getDescription(), Toast.LENGTH_SHORT).show();
        ShareAction shareAction = ShareHelper.getShareAction(that, mData.get(position).getDescription(), new shareCallBack());
        shareAction.share();
//        Intent intent = new Intent(this, EditInfliterTestActivity.class);
//        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        that.finish();
    }

    public class shareCallBack implements UMShareListener {

        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(getApplicationContext(), share_media.toString() + "分享成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {
            Toast.makeText(getApplicationContext(), share_media.toString() + "分享失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {
            Toast.makeText(getApplicationContext(), share_media.toString() + "分享取消", Toast.LENGTH_SHORT).show();
        }
    }
}
