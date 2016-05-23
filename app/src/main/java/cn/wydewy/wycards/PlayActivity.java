package cn.wydewy.wycards;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import cn.wydewy.wycards.MainActivity;
import cn.wydewy.wycards.view.CardsGameView;

public class PlayActivity extends AppCompatActivity {
    CardsGameView myView;
    String messString;
    private int score;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                messString = msg.getData().getString("data");
                score = msg.getData().getInt("score");
                SharedPreferences preferences = getSharedPreferences("global",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("score", score);
                editor.commit();
                showDialog();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        // 隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 锁定横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        myView = new CardsGameView(this, handler);
        setContentView(myView);
    }

    public void showDialog() {
        new AlertDialog.Builder(this).setMessage(messString)
                .setPositiveButton("重新开始游戏", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        SharedPreferences preferences = getSharedPreferences("global",
                                Context.MODE_PRIVATE);
                        final SharedPreferences.Editor editor = preferences.edit();
                        final int jifen = preferences.getInt("jifen", 10);
                        if (jifen > 0) {
                            editor.putInt("jifen", jifen - 3);
                            editor.commit();
                            reGame();
                        } else {
                            startActivity(new Intent(PlayActivity.this, MainActivity.class));
                            Toast.makeText(PlayActivity.this, "您的积分不足哦~", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).setTitle("Enjoy it").create().show();
    }

    //重新开始游戏
    public void reGame() {
        myView = new CardsGameView(this, handler);
        setContentView(myView);
    }
}
