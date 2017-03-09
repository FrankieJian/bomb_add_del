package com.example.g572_528r.as0307_bomb;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * Created by yls on 2017/3/7.
 */

public class addPerson extends AppCompatActivity {
    private static final int GET_HEAD_IMG = 1001;
    private static final int CROP_HEAD = 1002;
    private Button btnSave;
    private Button btnUpdate;
    private EditText edtName;
    private EditText edtAge;
    private EditText edtAddress;
    private ImageView headImg;
    private Person mPerson;
    private Bitmap bmp;
    private  String path;
    private Uri imgUri;
    private String url;
    private  String perId;
    private String imgId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);

        initViews();
        isUpdate();
    }

    public void isUpdate() {
        Intent intent = getIntent();
        if(intent == null) {
            return;
        }else {
            edtName.setText(intent.getStringExtra("name"));
            edtAge.setText(String.valueOf(intent.getIntExtra("age", 0)));
            edtAddress.setText(intent.getStringExtra("address"));
            perId = intent.getStringExtra("perId");
            imgId = intent.getStringExtra("imgId");
            if(intent.getStringExtra("url") == null){
                headImg.setImageResource(R.mipmap.ic_launcher);
            }else {
                Glide.with(addPerson.this).load(intent.getStringExtra("url")).into(headImg);
            }
        }
    }

    private void initViews() {
        mPerson = new Person();
        btnSave = (Button) findViewById(R.id.btn_save);
        edtName = (EditText) findViewById(R.id.edt_name);
        edtAge = (EditText) findViewById(R.id.edt_age);
        edtAddress = (EditText) findViewById(R.id.edt_address);
        btnUpdate = (Button) findViewById(R.id.btn_update);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });

        headImg = (ImageView) findViewById(R.id.headImg);

        headImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImg();
                // 上传图片因为这里是异步的uploadImg();还没得到图片就已经 p.setPic(url);所以放到
                //按钮的点击事件中执行，保证图片先得到，按钮点击事件执行才开始上传。
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadImg();
            }
        });
    }

    private void update() {
        getContent();
        ImageBean imageBean = new ImageBean();
        imageBean.setUrl(url);
        imageBean.update(imgId, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e == null) {
                    Toast.makeText(addPerson.this, "更新成功!!!", Toast.LENGTH_SHORT).show();
                    mPerson.update(perId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e == null){
                                Toast.makeText(addPerson.this, "更新成功+1!!!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(addPerson.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(addPerson.this, "更新失败+1!!!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(addPerson.this, "更新失败!!!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void downloadImg() {
        if (path == null) {
            Toast.makeText(this, "你还没有选择头像！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        final BmobFile bmobFile = new BmobFile(new File(path));
        bmobFile.upload(new UploadFileListener() {
            @Override
            public void done(BmobException e) {
                if (e == null){
                    //图片在服务器上的路径
                    url = bmobFile.getFileUrl();
                    Toast.makeText(addPerson.this, "上传图片成功！！！", Toast.LENGTH_SHORT).show();
                    ImageBean imageBean = new ImageBean();
                    imageBean.setUrl(url);
                    imageBean.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null){
                                Toast.makeText(addPerson.this, "成功！！", Toast.LENGTH_SHORT).show();
                                //为了解决线程问题，先让person表上传到服务器，再提交图片到服务器
                                getContent();
                                savePerson();
                            }else {
                                Toast.makeText(addPerson.this, "失败！！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(addPerson.this, "上传图片失败！！！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, GET_HEAD_IMG);
    }

    private void getContent() {
        if (edtName.getText().toString().isEmpty() || edtAge.getText().toString().isEmpty() || edtAddress.getText().toString().isEmpty()) {
            Toast.makeText(this, "不能为空！！！", Toast.LENGTH_SHORT).show();
            return;
        }
        String name = edtName.getText().toString().trim();
        int age = Integer.valueOf(edtAge.getText().toString().trim());
        String address = edtAddress.getText().toString().trim();

        mPerson.setAddress(address);
        mPerson.setAge(age);
        mPerson.setName(name);
    }

    private void savePerson() {
        mPerson.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if(e == null) {
                    Toast.makeText(addPerson.this, "添加成功！！！", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(addPerson.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(addPerson.this, "添加失败！！！", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_HEAD_IMG) {

            if (data != null) {
                imgUri = data.getData();
            }

            //获取图片路径
            String proj[]  = {MediaStore.Images.Media.DATA};
            Cursor cursor = managedQuery(imgUri,proj,null,null,null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            path = cursor.getString(column_index);
            Toast.makeText(this, "" + path, Toast.LENGTH_SHORT).show();
            //裁剪图片
            Intent intent = new Intent();
            intent.setAction("com.android.camera.action.CROP");
            intent.setDataAndType(imgUri, "image/*");
            intent.putExtra("crop", true);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("return-data", true);

            startActivityForResult(intent, CROP_HEAD);

        }

        if (requestCode == CROP_HEAD) {
            if (data == null) {
                return;
            }
            Bundle bundle = data.getExtras();
            bmp = bundle.getParcelable("data");
            headImg.setImageBitmap(bmp);
        }
    }
}
