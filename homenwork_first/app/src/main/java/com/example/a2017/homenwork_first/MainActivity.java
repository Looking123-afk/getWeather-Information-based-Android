package com.example.a2017.homenwork_first;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    //更新UI
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 处理消息时需要知道是成功的消息还是失败的消息
           TextView resultText = (TextView) findViewById(R.id.resultText);
            switch (msg.what){
                case 0:
                        resultText.setText(msg.obj.toString());
                    break;
            }
        }

};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
      

       //按钮触发事件
    public void button_click_2(View view){
        Thread t = new Thread(){
            @Override
            public void run() {
                HttpURLConnection connection = null;
                try {
                    EditText ed = (EditText)findViewById(R.id.cityName);
                    String city = java.net.URLEncoder.encode(ed.getText().toString(), "utf-8");
                    //String city = java.net.URLEncoder.encode("广州", "utf-8");
                    URL url = new URL("http://wthrcdn.etouch.cn/weather_mini?city="+city);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(5000);
                    connection.setReadTimeout(5000);
                    InputStream in = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    Log.i("TAG", response.toString());

                         String res = new String(response);
                        try {
                            JSONObject jsonObject = new JSONObject(res);
                                JSONObject root = jsonObject.getJSONObject("data");

                                String log = "";
                                log = log+"城市："+root.getString("city")+"\n"+"PM2.5："+root.getString("aqi")+"\n"+"\n";
                                JSONObject yes = (JSONObject)root.getJSONObject("yesterday");
                                log = log + yes.getString("date") +"\t"+yes.getString("high")+ "~"+
                                    yes.getString("low")+"\t"+
                                    yes.getString("type")+"\t"+
                                    yes.getString("fx")+"\n"+ "\n"+"\n";
                                for (int i =0;i<4;i++){
                                    JSONObject arr = (JSONObject) root.getJSONArray("forecast").get(i);
                                    log = log +
                                            arr.getString("date")+"\t"+
                                            arr.getString("high")+ "~"+
                                            arr.getString("low")+"\t"+
                                            arr.getString("type")+"\t"+
                                            arr.getString("fengxiang")+"\n"+ "\n"+"\n";
                                }
                                Message message = new Message();
                                message.what = 0;
                                message.obj = log;
                                handler.sendMessage(message);
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            };
        t.start();
    }
}





