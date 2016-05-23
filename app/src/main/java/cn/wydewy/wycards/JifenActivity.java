package cn.wydewy.wycards;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import butterknife.Bind;
import butterknife.ButterKnife;

public class JifenActivity extends SwipBack4AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_jifen)
    TextView tvJifen;
    @Bind(R.id.tv_tip_get_jifen)
    TextView tvTipGetJifen;
    @Bind(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jifen);
        ButterKnife.bind(this);
        toolbar.setTitle(getString(R.string.jifen));
        setSupportActionBar(toolbar);
        initAd();
        Handler handler = new Handler();
        handler.postDelayed(runnable, 1500);//开启广告

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });
        tvTipGetJifen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences preferences = getSharedPreferences("global",
                Context.MODE_PRIVATE);
        int jifen = preferences.getInt("jifen", 10);
        tvJifen.setText("当前积分：" + jifen);
    }

    private InterstitialAd mInterstitialAd;

    /**
     * init ad
     */

    private void initAd() {
        mInterstitialAd = new InterstitialAd(this);
        // Defined in res/values/strings.xml
        mInterstitialAd.setAdUnitId(getString(R.string.ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
            }

            @Override
            public void onAdLeftApplication() {
                super.onAdLeftApplication();
                SharedPreferences preferences = getSharedPreferences("global",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                int jifen = preferences.getInt("jifen", 100);
                jifen += 3;
                editor.putInt("jifen", jifen);
                editor.commit();
            }
        });
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
        AdRequest adRequest = new AdRequest.Builder().build();
        mInterstitialAd.loadAd(adRequest);

    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (mInterstitialAd != null) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
//                Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            // Create the InterstitialAd and set the adUnitId.
            showInterstitial();
        }
    };
}
