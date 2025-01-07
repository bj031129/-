package com.example.mail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.app.R;
import com.example.mail.entity.Mail;
import com.example.mail.api.SharedPreferencesUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;


    public class ReceiverActivity extends AppCompatActivity implements AdapterView.OnItemClickListener{
        private List<Mail> mailList = null;
        private String  userAddress;
        ListView lv;
        Handler handler;
        Pop3Helper pop3Helper=new Pop3Helper();
        private String ip;
        private TextView btReturn;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_receiver);

            SharedPreferencesUtil util = SharedPreferencesUtil.getInstance(com.example.mail.ReceiverActivity.this);
            userAddress = util.readString("username");

            MyApplication application = (MyApplication) this.getApplicationContext();
            ip = application.getNumber();

            //找到控件
            btReturn = findViewById(R.id.returnbtn);

            handler = new Handler(Looper.getMainLooper()) {
                @SuppressLint("HandlerLeak")
                @Override
                public void handleMessage(Message msg) {
                    if (msg.what == 1) {
                        System.out.println("handle:\n");
                        System.out.println("msg:"+msg);
                        // 动态更新数据UI界面
                        String str = msg.getData().getString("body") + "";//获取值时相应的类型要对应，传入为String类型用getString；Int类型用getInt。
                        System.out.println(str);
                        try {
                            Gson gson=new Gson();
                            mailList = (List<Mail>) gson.fromJson(str, new TypeToken<List<Mail>>(){}.getType());
                            Log.d("Adapter", "Mail list size: " + mailList.size());

                            if(mailList.size()!=0) {
                                MyListDataAdapter adapter = new MyListDataAdapter();
                                lv.setAdapter(adapter);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            initData();
            freshData();
            lv = findViewById(R.id.rlv_rec);
            lv.setOnItemClickListener(this::onItemClick);
            MyListDataAdapter adapter = new MyListDataAdapter();
            lv.setAdapter(adapter);

            btReturn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

    private void initData() {
        mailList = new ArrayList<>();
    }

    private void freshData() {
        String ok = "获取成功";
        String err = "网络错误";
        String empty = "邮箱为空";
        MyApplication application = (MyApplication) this.getApplicationContext();
        new Thread(new Runnable() {
            @Override
            public void run() {

                // 获取邮件列表

                List<Mail> receivedMails = pop3Helper.getMailList();
                List<Mail> preMails = new ArrayList<>();
                preMails= application.getMailList();
                Log.d("ReceiverActivity", "Pre mails: " + preMails);
                if(receivedMails.size()>preMails.size())
                {
                    for (int i=preMails.size();i<receivedMails.size();i++) {
                           Mail mail=receivedMails.get(i);
                           mail.setMid(i);
                           System.out.println(mail);
                           application.addMail(mail);
                    }
                }
                receivedMails.clear();
                for(Mail mail:preMails){
                    if(mail.getDeleted()==null||mail.getDeleted()!=true){

                        receivedMails.add(mail);
                    }
                }

                Log.d("ReceiverActivity", "Received mails: " + receivedMails);

                // 处理邮件列表
                if (receivedMails != null && !receivedMails.isEmpty()) {
                    Message msg = new Message();
                    msg.what = 1;  // 给信使做标记
                    Bundle bundle = new Bundle();
                    bundle.putString("body", new Gson().toJson(receivedMails));
                    msg.setData(bundle);
                    handler.sendMessage(msg);  // handler传递参数
                } else {
                    Looper.prepare();
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), empty, Toast.LENGTH_SHORT).show();
                    });
                    Looper.loop();
                }

            }
        }).start();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()) {
            case R.id.rlv_rec:
                Mail curMail = (Mail) adapterView.getItemAtPosition(i);
                Intent intent = new Intent(ReceiverActivity.this, DetailsActivity.class);
                intent.putExtra("content", curMail.getBody());
                intent.putExtra("from", curMail.getSenderEmail());
                intent.putExtra("to", curMail.getReceiverEmail());
                intent.putExtra("subject", curMail.getSubject());
                intent.putExtra("date", curMail.getSendTime());
                intent.putExtra("mode", 3);
                startActivity(intent);
                break;
        }
    }

    class MyListDataAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mailList.size();
        }

        @Override
        public Object getItem(int i) {
            return mailList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            MyListViewHolder viewHolder;

            if (view == null) {
                view = View.inflate(ReceiverActivity.this, R.layout.item_receiver_email, null);
                viewHolder = new MyListViewHolder();
                viewHolder.sender_name = view.findViewById(R.id.sender_name);
                viewHolder.subject = view.findViewById(R.id.subject);
                viewHolder.content = view.findViewById(R.id.content);
                viewHolder.receiverDate = view.findViewById(R.id.receiverDate);
                viewHolder.deleteButton = view.findViewById(R.id.deleteButton);  // 获取删除按钮
                view.setTag(viewHolder);
            } else {
                viewHolder = (MyListViewHolder) view.getTag();
            }

            Mail mail = mailList.get(i);
            viewHolder.sender_name.setText(mail.getSenderEmail());
            viewHolder.subject.setText(mail.getSubject());
            viewHolder.content.setText(mail.getBody());
            viewHolder.receiverDate.setText(mail.getReceiverEmail());

            // 设置删除按钮的点击事件
            viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteMail(i);  // 调用删除邮件的方法
                }
            });




            return view;
        }

        class MyListViewHolder {
            TextView sender_name, subject, receiverDate, content;
            Button deleteButton;  // 删除按钮
        }
    }
    private void deleteMail(int i) {

        Mail mail = mailList.get(i);
        mailList.remove(i);  // 从列表中移除邮件
        int mid = mail.getMid();
        mail.setDeleted(true);  // 标记邮件为删除
        System.out.println(i);
        System.out.println(mail);
        MyApplication application = (MyApplication) getApplicationContext();
        List<Mail> appMailList = application.getMailList();
        appMailList.set(mid, mail);  // 更新 MyApplication 中的邮件状态
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean sendSuccessful = false;
                try {
                    sendSuccessful = pop3Helper.deleteMail(mid);
                    if (sendSuccessful) {
                        Looper.prepare();
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "邮件已删除", Toast.LENGTH_SHORT).show();
                        });
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        runOnUiThread(() -> {
                            Toast.makeText(getApplicationContext(), "邮件删除失败", Toast.LENGTH_SHORT).show();
                        });
                        Looper.loop();
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

        MyListDataAdapter adapter = (MyListDataAdapter) lv.getAdapter();
        adapter.notifyDataSetChanged();  // 刷新 ListView
        Toast.makeText(getApplicationContext(), "邮件已删除", Toast.LENGTH_SHORT).show();  // 显示删除成功的提示
    }

}
