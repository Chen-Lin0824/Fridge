package tw.tcnr.fridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Homepic extends AppCompatActivity implements Animation.AnimationListener{

    private ImageView imageView;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepic);
        context = this;
        imageView = findViewById(R.id.hpstartpic);

        //imageView 設定動畫元件(透明度調整)
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_alpha_in01);
        animation.setFillEnabled(true);
        animation.setFillAfter(true);
        animation.setAnimationListener(this);
        imageView.setAnimation(animation);
    }
    @Override
    public void onAnimationStart(Animation animation) {}

    @Override
    public void onAnimationEnd(Animation animation) {
//        startActivity(new Intent(context, Googlelogin.class));
        startActivity(new Intent(context, HomePage.class));
        finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {}

}//----------END