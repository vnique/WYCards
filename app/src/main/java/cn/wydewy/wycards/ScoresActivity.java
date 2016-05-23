package cn.wydewy.wycards;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ScoresActivity extends SwipBack4AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_score)
    TextView tvScore;

    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scores);
        ButterKnife.bind(this);
        toolbar.setTitle(getString(R.string.scores));
        setSupportActionBar(toolbar);

        SharedPreferences preferences = getSharedPreferences("global",
                Context.MODE_PRIVATE);
        int pre = preferences.getInt("pre", 0);
        int add = preferences.getInt("score", 0);
        score = pre + add;

        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("pre", score);
        editor.commit();

        tvScore.setText("总分：" + score);
    }


}
