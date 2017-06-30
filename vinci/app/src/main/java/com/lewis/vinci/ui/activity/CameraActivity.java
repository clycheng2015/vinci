package com.lewis.vinci.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.lewis.lib_vinci.utils.CameraUtils;
import com.lewis.lib_vinci.utils.FileUtils;
import com.lewis.lib_vinci.utils.LogUtil;
import com.lewis.vinci.R;
import com.lewis.vinci.common.base.NetworkBaseActivity;
import com.lewis.vinci.common.imageloader.ImageLoader;
import com.lewis.vinci.permission.PermissionListener;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    private File fileName;
    private String pathAlbum;
    public int mWidth;
    public int mHeight;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_camera;
    }

    @Override
    protected void afterCreate(Bundle savedInstanceState) {
        FileUtils.init();
        mWidth = iv_photo.getWidth();
        mHeight = iv_photo.getHeight();
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
                requestPermissions(new String[]{Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, new PermissionListener() {
                    @Override
                    public void onGranted() {
                        CameraUtils.albumChoose(CameraActivity.this);
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {
                        //提示用户去设置页面打开权限
                    }
                });
                break;
        }
    }

    private void openCamera() {
        File tempFileName = FileUtils.createPhotoName();
        if (null != tempFileName) {
            fileName = tempFileName;
            LogUtil.d("CameraActivity", fileName.getAbsolutePath());
            CameraUtils.takePhoto(this, fileName);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CameraUtils.CODE_TAKE_PHOTO:
                    Log.e("TAG", "---------" +
                            FileProvider.getUriForFile(this, "com.lewis.vinci.fileprovider", fileName));
                    //不进行图片剪裁直接显示
                    //ImageLoader.loadImageWithUri(CameraActivity.this, iv_photo, CameraUtils.takePhotoUri);
//                    if (null != CameraUtils.takePhotoUri) {
//                        ImageLoader.loadImageWithUri(CameraActivity.this, iv_photo, CameraUtils.takePhotoUri);
//                        //在手机相册中显示刚拍摄的图片(没有效果，还是无法存到相册)
//                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//                        Uri contentUri = Uri.fromFile(fileName);
//                        mediaScanIntent.setData(contentUri);
//                        sendBroadcast(mediaScanIntent);
//                    }
                    //进行图片剪裁再显示
                    CameraUtils.takePhotoZoom(CameraActivity.this, fileName);
                    break;
                case CameraUtils.CODE_TAKE_PHOTO_ZOOM:
                    if (null != CameraUtils.takePhotoUri) {
                        ImageLoader.loadImageWithUri(CameraActivity.this, iv_photo, CameraUtils.takePhotoUri);
                    }
                    break;
                case CameraUtils.CODE_ALBUM_CHOOSE:
                    //方式一：从相册打开图片，不剪裁直接显示
                    if (data == null) return;
                    Uri uri = data.getData();
//                    int sdkVersion = Integer.valueOf(Build.VERSION.SDK);
//                    if (sdkVersion >= 19) {
//                        pathAlbum = CameraUtils.getPath_above19(CameraActivity.this, uri);
//                    } else {
//                        pathAlbum = CameraUtils.getFilePath_below19(CameraActivity.this, uri);
//                    }
//                    LogUtil.d(pathAlbum);
//                    iv_photo.setImageBitmap(CameraUtils.getSmallBitmap(pathAlbum, mWidth, mHeight));
                    //ImageLoader.loadImageWithUri(CameraActivity.this, iv_photo, Uri.fromFile(new File(pathAlbum)));
                    break;
                case CameraUtils.CODE_ALBUM_CHOOSE_ZOOM:
                    ImageLoader.loadImageWithUri(CameraActivity.this, iv_photo, Uri.fromFile(new File(pathAlbum)));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
