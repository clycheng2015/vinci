package com.lewis.vinci.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lewis.lib_vinci.utils.CameraUtils;
import com.lewis.lib_vinci.utils.FileUtils;
import com.lewis.vinci.R;
import com.lewis.vinci.common.base.NetworkBaseActivity;
import com.lewis.vinci.common.imageloader.ImageLoader;
import com.lewis.vinci.permission.PermissionListener;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.lewis.lib_vinci.utils.CameraUtils.CODE_TAKE_PHOTO;

/**
 * Created by ZHT on 2017/6/2.
 * 相机，相册调用Activity
 */

public class CameraActivity extends NetworkBaseActivity {

    @BindView(R.id.iv_photo)
    ImageView iv_photo;
    @BindView(R.id.bt_camera)
    Button bt_camera;
    @BindView(R.id.bt_album)
    Button bt_album;
    private String mFilePath;
    private String mFileName;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        FileUtils.init();
        mFilePath = FileUtils.getFileDir() + File.separator;
    }


    @OnClick({R.id.bt_camera, R.id.bt_album})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_camera:
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        openCamera();
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {
                        //提示用户去设置页面打开权限
                    }
                });

                break;

            case R.id.bt_album:
                CameraUtils.albumChoose(CameraActivity.this);
                break;
        }
    }

    private void openCamera() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = new File(mFilePath);
            if (!path.exists()) {
                path.mkdirs();
            }
            mFileName = System.currentTimeMillis() + ".jpg";
            File file = new File(path, mFileName);
            if (file.exists()) {
                file.delete();
            }
            FileUtils.startActionCapture(this, file, CODE_TAKE_PHOTO);
        } else {
            Log.e("main", "sdcard not exists");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CameraUtils.CODE_TAKE_PHOTO:
                    FileUtils.takePhotoZoom(CameraActivity.this);
                    break;
                case CameraUtils.CODE_TAKE_PHOTO_ZOOM:
                    if (null != CameraUtils.takePhotoUri) {
                        ImageLoader.loadImageWithUri(CameraActivity.this, iv_photo, CameraUtils.takePhotoUri);
                    }
                    break;
                case CameraUtils.CODE_ALBUM_CHOOSE:
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4以上系统使用这个方法处理图片
                        CameraUtils.handleImageOnKitKat(CameraActivity.this, data);
                    } else {
                        //4.4一下系统使用这个方法处理图片
                        CameraUtils.handleImageBeforeKitKat(CameraActivity.this, data);
                    }
                    break;
                case CameraUtils.CODE_ALBUM_CHOOSE_ZOOM:
                    if (null != CameraUtils.albumPhotonUri) {
                        ImageLoader.loadImageWithUri(CameraActivity.this, iv_photo, CameraUtils.albumPhotonUri);
                    }
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
