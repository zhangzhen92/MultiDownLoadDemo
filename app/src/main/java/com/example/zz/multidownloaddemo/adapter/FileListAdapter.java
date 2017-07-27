package com.example.zz.multidownloaddemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.zz.multidownloaddemo.R;
import com.example.zz.multidownloaddemo.bean.FileInfo;
import com.example.zz.multidownloaddemo.services.DownLoadService;

import java.util.List;

/**
 * 类描述：文件下载适配器
 * 创建人：zz
 * 创建时间： 2017/7/24 11:54
 */


public class FileListAdapter extends BaseAdapter{
    private List<FileInfo> datas;
    private Context context;

    public FileListAdapter(List<FileInfo> datas, Context context) {
        this.datas = datas;
        this.context = context;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(convertView == null){
           convertView = LayoutInflater.from(context).inflate(R.layout.list_item,null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final FileInfo fileInfo = datas.get(position);
        if(!TextUtils.isEmpty(fileInfo.getFileName())){
            viewHolder.tvFileName.setText(fileInfo.getFileName());
        }
        viewHolder.progressBar.setMax(100);
        viewHolder.progressBar.setProgress(fileInfo.getFinished());
        viewHolder.btStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DownLoadService.class);
                intent.putExtra("fileInfo",fileInfo);
                intent.setAction(DownLoadService.START_ACTION);
                context.startService(intent);
            }
        });

        viewHolder.btStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DownLoadService.class);
                intent.putExtra("fileInfo",fileInfo);
                intent.setAction(DownLoadService.STOP_ACTION);
                context.startService(intent);
            }
        });

        return convertView;
    }


    public void updateProgess(int id,int progress){
        FileInfo fileInfo = datas.get(id);
        fileInfo.setFinished(progress);
        notifyDataSetChanged();
    }


    class ViewHolder{
        private TextView tvFileName;
        private Button btStart;
        private Button btStop;
        private ProgressBar progressBar;
         public ViewHolder(View convertView){
          tvFileName = ((TextView) convertView.findViewById(R.id.tv_filename));
          btStart = ((Button) convertView.findViewById(R.id.button_start));
            btStop = ((Button) convertView.findViewById(R.id.button_stop));
             progressBar = ((ProgressBar) convertView.findViewById(R.id.pb_progress));
         }
    }
}
