package com.cpsc.photos;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.ArrayList;

import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.bean.WaterMarkBean;

public class MainActivity extends AppCompatActivity {

    private Spinner cameraVisSp, waterMrakVisSp, photoSp, photoNumSp;
    private EditText waterMrakEdit;
    private TextView openCameraTv;
    private GridView gridView;
    private int mPhotoNumSp = 1;
    private ArrayList<String> listPhotoPath;
    private Boolean mShowCamera = true;
    private Boolean mMode = true; //多张照片 ,最多9张 ,false 单张
    private Boolean mSelectorPhoto = true;
    private Boolean mWaterMarkVis = false;
    private final int mCoder = 1;
    private final int mCoders = 2;
    private final String positionStr = "position";
    private SharedPreferences shared;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initSharedPre();
        initListener();
    }


    private void initListener() {

        //是否从相册里主动选照片
        setPhotoSp(photoSp, 0, new onSpinnerListener() {
            @Override
            public void result(int position) {
                if (position == 0)
                    mSelectorPhoto = true;
                else
                    mSelectorPhoto = false;


                setPhotoSp(photoNumSp, 1, new onSpinnerListener() {
                    @Override
                    public void result(int position) {

                        if (position == 0)
                            mMode = false;
                        else
                            mMode = true;

                        mPhotoNumSp = position + 1;


                    }
                });
            }
        });


        // 是否显示相机
        setPhotoSp(cameraVisSp, 2, new onSpinnerListener() {
            @Override
            public void result(int position) {
                if (position == 1)
                    mShowCamera = false;
                else
                    mShowCamera = true;

            }
        });

        setPhotoSp(waterMrakVisSp, 3, new onSpinnerListener() {
            @Override
            public void result(int position) {
                if (position == 0) {
                    mWaterMarkVis = false;
                    waterMrakEdit.setVisibility(View.GONE);
                } else {
                    mWaterMarkVis = true;
                    waterMrakEdit.setVisibility(View.VISIBLE);
                }
            }
        });

        // 拍照
        openCameraTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testPermisson();
            }
        });

        setGridViewData();

    }


    private void setGridViewData() {

        if (listPhotoPath == null) {
            return;
        }

        gridView.setAdapter(new CommonAdapter<String>(this, R.layout.gridview_adapter, listPhotoPath) {
            @Override
            protected void convert(ViewHolder viewHolder, String item, int position) {
                Glide.with(MainActivity.this)
                        .load(item)
                        .error(R.mipmap.ic_launcher)
                        .into((ImageView) viewHolder.getView(R.id.item_gridviewimg));
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String imgPath = (String) parent.getAdapter().getItem(position);
                startActivity(new Intent(MainActivity.this, PhotoBigActivity.class).putExtra("imgPath", imgPath));
            }
        });

    }

    /**
     * @author stf
     * @time 2018-07-11 15:52
     * @remark 开始拍照
     */
    private void startCamera() {

        MultiImageSelector mModeType = null;
        if (mMode) {// 拍单张还是多张
            mModeType = getMultiImageSelectorOrigin().multi();// 开始拍照
        } else {
            mModeType = getMultiImageSelectorOrigin().single();
        }

        String trim = waterMrakEdit.getText().toString().trim();
        if (TextUtils.isEmpty(trim)) {
            mModeType.start(this, mCoder);
        } else {
            WaterMarkBean waterMarkBean = mModeType.getWaterMarkBean(); // 设置水印的属性
            waterMarkBean.setTextSize(28);
            waterMarkBean.setColor("#d4237a");
            waterMarkBean.setAntiAlias(true);
            waterMarkBean.setAlpha(180);
            waterMarkBean.setRotate(-30);
            waterMarkBean.setMark(trim);
            mModeType.setWaterMarkStyle(waterMarkBean).start(this, mCoder);
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

    private void setPhotoSp(Spinner spinner, final int flag, final onSpinnerListener listener) {
        String[] items = getData(flag);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);

        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (flag == 1) {
                    if (shared == null) {
                        shared = getSharedPreferences("SHARED", MODE_PRIVATE);
                    }
                    shared.edit().putInt(positionStr, position).commit();
                }
                listener.result(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (flag != 1) {
            return;
        }

        if (shared == null) {
            shared = getSharedPreferences("SHARED", MODE_PRIVATE);
        }
        int anInt = shared.getInt(positionStr, 0);
        spinner.setSelection(anInt);
    }

    private String[] getData(int flag) {
        String[] items = new String[0];
        if (flag == 0) {
            items = new String[]{"是", "否"};
        } else if (flag == 1) {
            items = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        } else if (flag == 2) {
            items = new String[]{"是", "否"};
        } else if (flag == 3) {
            items = new String[]{"否", "是"};
        } else {
            items = new String[]{"未添加数据"};
        }
        return items;
    }


    private void initView() {
        photoSp = findViewById(R.id.select_photo_spinner);
        photoNumSp = findViewById(R.id.select_photonum_spinner);
        cameraVisSp = findViewById(R.id.select_camera_spinner);
        waterMrakVisSp = findViewById(R.id.select_camera_vis_spinner);
        openCameraTv = findViewById(R.id.select_start_tv);
        gridView = findViewById(R.id.select_photo_gridView);
        waterMrakEdit = findViewById(R.id.select_camera_markedit);
    }

    private void initSharedPre() {
        shared = getSharedPreferences("SHARED", MODE_PRIVATE);
    }

    public interface onSpinnerListener {
        void result(int position);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == mCoder) {
            if (resultCode == RESULT_OK) {
                listPhotoPath = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                setGridViewData();
            }
        }
    }

    private void testPermisson() {
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, mCoder);
        } else {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, 2);
            } else {
                startCamera();
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case mCoder:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    testPermisson();
                } else {
                    Toast.makeText(MainActivity.this, "暂无权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case mCoders:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    testPermisson();
                } else {
                    Toast.makeText(MainActivity.this, "暂无权限", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }

    }


}
