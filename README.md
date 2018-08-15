# MultiImageSelector
感谢原作者大神带我上路，在原基础上兼容android 7.0以上拍照 ，新增是否选取照片功能


#### 改进点
- 兼容7.0以上拍照
- 新增是否主动选择照片的功能
- 动态设置照片水印

#### 为何要改进
 - 7.0以后android 在拍照时返回的“uri”这块做了很大的改进
 - 有这样的奇葩需求：
 1.上传多张照片，具体多少张不确定，用户根据当时场景自己随意定。
 2.部分用户要求可以从“相册”里主动去选照片，部分用户要求不能从“相册”里选照片。（用户来自不同市区，开发时共用同一套模板，对于不同用户稍加修改项目模板，打算写一个开关出来，于是就诞生了是否主动选择照片的功能）
 3.加水印防止用户提前拍好照片，后期使用。

#### 效果预览
![image.png](https://upload-images.jianshu.io/upload_images/5915124-26183ba4b4d7dd46.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
#### 进入项目
- gradle 配置
```
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
   
    compile 'com.zhy:base-rvadapter:3.0.3' //万能适配器
    compile 'com.zhy:base-adapter:3.0.3'  //万能适配器
    compile 'com.github.bumptech.glide:glide:3.7.0'
    implementation 'com.github.stf-android:MultiImageSelector:1.0.1'  // 图片选择器
```
- Manifest 配置
```
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.CAMERA" />
          
         <!--兼容7.0以上拍照-->
         <!-- authorities：是该项目的包名+provider
        <!- grantUriPermissions：必须是true，表示授予 URI 临时访问权限-->
        <!--exported：必须是false-->
        <!--resource：中的@xml/file_paths是我们接下来要在资源文件目录下添加的文件 -->
        <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

```
- file_paths.xml文件
在app下res目录下新建xml文件夹，然后再xml中新建 file_paths.xml
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!--path：需要临时授权访问的路径（.代表在相机调用时候访问的是所有路径，而文件写入时访问的路径是Android/data/com.ijuyin.prints.news/）-->
    <!--name： 是你为设置的这个访问路径起的名字-->
    <paths>
        <external-path
            name="camera_photos"
            path="." />
        <external-path
            name="files_root"
            path="Android/data/com.ijuyin.prints.news/" />
        <external-path
            name="external_storage_root"
            path="." />
    </paths>
</resources>
```
- MultiImageSelector 的调用
```

private void startCamera() {
        MultiImageSelector mModeType = null;
        if (mMode) {// 拍单张还是多张
            mModeType = getMultiImageSelectorOrigin().multi();// 多选
        } else {
            mModeType = getMultiImageSelectorOrigin().single(); // 单选
        }

        String trim = waterMrakEdit.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            mModeType.start(this, mCoder); // 开始拍照
        } else {
            WaterMarkBean waterMarkBean = mModeType.getWaterMarkBean(); // 设置水印的属性
            waterMarkBean.setTextSize(28);
            waterMarkBean.setColor("#d4237a");
            waterMarkBean.setAntiAlias(true);
            waterMarkBean.setAlpha(180);
            waterMarkBean.setRotate(-30);
            waterMarkBean.setMark(trim);
            mModeType.setWaterMarkStyle(waterMarkBean).start(this, mCoder); // 开始拍照
        }
    }

private MultiImageSelector getMultiImageSelectorOrigin() {
      return MultiImageSelector.create()
                .showCamera(mShowCamera) // 是否显示相机. 默认为显示
                .count(mPhotoNumSp) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                .selectPhoto(mSelectorPhoto) // 是否主动从相册中选择照片
                .setWaterMarkPrivacy(mWaterMarkVis)  // 是否添加水印
                .origin(listPhotoPath);//返回照片集合的路径
    }

   
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mCoder) {
            if (resultCode == RESULT_OK) { // 照片的路径集合返回
                listPhotoPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                setGridViewData();
            }
        }
    }
```
 #### 说明：
- 关于已经显出的照片的增加，移除，更换的问题
遵循是否主动在相册中选择照片的规则下

1.主动选择
 单选模式：拍照进行替换照片，不能移除。
 多选模式：拍照增加替换或者点击某张照片进行增加，移除。
2.不主动选择（默认选中）
单选模式：只能拍照更换照片（默认选中该照片）。
多选模式：拍照（默认选中最近几次拍的照片），可长按某张照片移除它。
除拍照外其他操作最后点击“完成”即可。
- 添加 水印且不设置水印内容时，显示当前拍照时间
