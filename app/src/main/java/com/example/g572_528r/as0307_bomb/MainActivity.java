package com.example.g572_528r.as0307_bomb;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class MainActivity extends AppCompatActivity {
    private Button btn_insert;
    private RecyclerView mRecyclerView;
    private List<Person> mPersonList = new ArrayList<>();
    private MyAdapter mMyAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        QueryAllPerson();
    }

    private void initViews() {
        Bmob.initialize(this, "17edadaf31b491c9074b7a27c30ecc86");
        btn_insert = (Button) findViewById(R.id.insert_btn);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycle);
        btn_insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, addPerson.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void QueryAllPerson() {
        BmobQuery<Person> personBmobQuery = new BmobQuery<Person>();
        personBmobQuery.findObjects(new FindListener<Person>() {
            @Override
            public void done(List<Person> list, BmobException e) {
                if(e == null){
                    mPersonList = list;

                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this);
                    mMyAdapter = new MyAdapter(mPersonList);
                    mRecyclerView.setLayoutManager(linearLayoutManager);
                    mRecyclerView.setAdapter(mMyAdapter);
                    Toast.makeText(MainActivity.this, "点击头像修改数据", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private List<Person> mPersons;
        private List<ImageBean> mImageBeen = new ArrayList<>();

        public MyAdapter(List<Person> mPersons) {
            this.mPersons = mPersons;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView headImg;
            private TextView tv_name;
            private TextView tv_age;
            private TextView tv_address;
            private ImageView delImg;

            public ViewHolder(View itemView) {
                super(itemView);
                headImg = (ImageView) itemView.findViewById(R.id.img_head);
                tv_name = (TextView) itemView.findViewById(R.id.tv_name);
                tv_age = (TextView) itemView.findViewById(R.id.tv_age);
                tv_address = (TextView) itemView.findViewById(R.id.tv_address);
                delImg = (ImageView) itemView.findViewById(R.id.img_del);
            }
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.person_main, parent, false);
            ViewHolder holder = new ViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(final MyAdapter.ViewHolder holder, final int position) {
            final Person p = mPersons.get(position);
            holder.tv_name.setText(p.getName());
            holder.tv_age.setText(String.valueOf(p.getAge()));
            holder.tv_address.setText(p.getAddress());

            holder.headImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = holder.getAdapterPosition();
                    Intent intent = new Intent(MainActivity.this, addPerson.class);
                    intent.putExtra("name", p.getName());
                    intent.putExtra("age", p.getAge());
                    intent.putExtra("address", p.getAddress());
                    intent.putExtra("url", mImageBeen.get(index).getUrl());
                    intent.putExtra("perId", p.getObjectId());
                    intent.putExtra("imgId", mImageBeen.get(index).getObjectId());
                    startActivity(intent);
                }
            });

            final BmobQuery<ImageBean> imageBeanBmobQuery = new BmobQuery<>();
            imageBeanBmobQuery.findObjects(new FindListener<ImageBean>() {
                @Override
                public void done(List<ImageBean> list, BmobException e) {
                    if(e == null){
                        mImageBeen = list;
                        Glide.with(MainActivity.this).load(mImageBeen.get(position).getUrl()).into(holder.headImg);
                    }else {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            holder.delImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int index = holder.getAdapterPosition();
                    String perId = p.getObjectId();
                    String imgId = mImageBeen.get(position).getObjectId();

                    //删除图片
                    mImageBeen.get(index).delete(new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            p.delete(new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if (e == null) {
                                        Toast.makeText(MainActivity.this, "删除成功！！！", Toast.LENGTH_SHORT).show();
                                        QueryAllPerson();
                                    }else {
                                        Toast.makeText(MainActivity.this, "删除失败！！！", Toast.LENGTH_SHORT).show();
                                    }
                               }
                            });
                            Toast.makeText(MainActivity.this, "删除成功+1！！！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }

        @Override
        public int getItemCount() {
            return mPersons.size();
        }
    }
}
