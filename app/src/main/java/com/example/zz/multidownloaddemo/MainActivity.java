package com.example.zz.multidownloaddemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.zz.multidownloaddemo.adapter.FileListAdapter;
import com.example.zz.multidownloaddemo.bean.FileInfo;
import com.example.zz.multidownloaddemo.services.DownLoadService;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：断点续传工具类
 * 创建人：zz
 * 创建时间：2017/7/18 16:24
 */
public class MainActivity extends Activity implements View.OnClickListener{
    private MyReceiver myReceiver;
    private ListView listView;
    private FileListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //   initWindow();
        initView();
        myReceiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter(DownLoadService.UPDATE_ACTION);
        intentFilter.addAction(DownLoadService.FINISH_ACTION);
        registerReceiver(myReceiver,intentFilter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(myReceiver);

    }

    /**
     * 初始化UI
     */
    private void initView() {
        listView = ((ListView) findViewById(R.id.listview_id));
        List<FileInfo> datas = new ArrayList<>();
        FileInfo fileInfo = new FileInfo(0,"https://qd.myapp.com/myapp/qqteam/AndroidQQ/mobileqq_android.apk","qq.apk",0,0);
        FileInfo fileInfo1 = new FileInfo(1,"http://dl.360safe.com/360zip_setup_3.2.0.2200.exe","zip.exe",0,0);
        FileInfo fileInfo2 = new FileInfo(2,"http://dl.ludashi.com/ludashi/ludashisetup.exe","鲁大师.exe",0,0);
        FileInfo fileInfo3 = new FileInfo(3,"http://dldir1.qq.com/qqfile/qq/TIM1.1.5/21175/TIM1.1.5.exe","TIM.exe",0,0);
        datas.add(fileInfo);
        datas.add(fileInfo1);
        datas.add(fileInfo2);
        datas.add(fileInfo3);
        adapter = new FileListAdapter(datas,getApplicationContext());
        listView.setAdapter(adapter);

    }

    @Override
    public void onClick(View view) {
    }


    class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(DownLoadService.UPDATE_ACTION.equals(intent.getAction())){
                int finished = intent.getIntExtra("finished", 0);
                int id = intent.getIntExtra("id", 0);
                adapter.updateProgess(id,finished);
            }else if(DownLoadService.FINISH_ACTION.equals(intent.getAction())){
                FileInfo fileInfo = (FileInfo) intent.getSerializableExtra("fileInfo");
                Toast.makeText(getApplicationContext(),fileInfo.getFileName(),Toast.LENGTH_SHORT).show();
            }
        }
    }
}
