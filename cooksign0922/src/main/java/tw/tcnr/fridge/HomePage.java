package tw.tcnr.fridge;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

public class HomePage extends AppCompatActivity implements View.OnClickListener {

    private ImageButton fridgeBtn,timeBtn,listBtn,cookBtn,carBtn,randomBtn;
    private Intent intent = new Intent();
    private LinearLayout r_layout;
    private RelativeLayout r_layout_out02;
    private ImageView hpstartpic;
    private Uri uri;
    private Intent it;
    private Button googlelogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fridge_hp);
        setupViewComponent();
    }

    private void setupViewComponent() {

        fridgeBtn=(ImageButton)findViewById(R.id.fridge_hp_btn);
        timeBtn=(ImageButton)findViewById(R.id.time_hp_btn);
        listBtn=(ImageButton)findViewById(R.id.list_hp_btn);
        cookBtn=(ImageButton)findViewById(R.id.cook_hp_btn);
        carBtn=(ImageButton)findViewById(R.id.car_hp_btn);
        randomBtn=(ImageButton)findViewById(R.id.random_hp_btn);
        googlelogin=(Button)findViewById(R.id.hp_login);
        cookBtn.setOnClickListener(this);
        carBtn.setOnClickListener(this);
        timeBtn.setOnClickListener(this);
        fridgeBtn.setOnClickListener(this);
        listBtn.setOnClickListener(this);
        randomBtn.setOnClickListener(this);
        googlelogin.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fridge_hp_btn:

                intent.putExtra("class_title",getString(R.string.fridge_name));
                intent.setClass(HomePage.this, Fridge.class);
                startActivity(intent);
                break;

            case R.id.time_hp_btn:

                intent.putExtra("class_title",getString(R.string.expired_name));
                intent.setClass(HomePage.this, Expired.class);
                startActivity(intent);
                break;

            case R.id.cook_hp_btn:

                intent.putExtra("class_title",getString(R.string.cook_name));
                intent.setClass(HomePage.this, Cooklist.class);
                startActivity(intent);

                break;

            case R.id.list_hp_btn:
                intent.putExtra("class_title",getString(R.string.shop_name));
                intent.setClass(HomePage.this, ShoplistHpMain.class);
                startActivity(intent);
                break;

            case R.id.car_hp_btn:

                intent.putExtra("class_title",getString(R.string.trash_name));
                intent.setClass(HomePage.this, Garsign01.class);
                startActivity(intent);

                break;

            case R.id.random_hp_btn:

                intent.putExtra("class_title",getString(R.string.random_name));
                intent.setClass(HomePage.this, RANDOM_MEAL.class);
                startActivity(intent);

                break;

            case R.id.hp_login:

                intent.setClass(HomePage.this, Googlelogin.class);
                startActivity(intent);

                break;


        }


    }

    //------生命週期
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //-----------------------Menu選單---------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.menu_fb:
                uri = Uri.parse("https://www.facebook.com/kai.hao.9");
                it = new Intent(Intent.ACTION_VIEW,uri);
                startActivity(it);
                break;

            case R.id.menu_notify:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_notify)
                        .setMessage(getString(R.string.menu_message))
                        .setCancelable(false)
                        .setIcon(R.drawable.icon)
                        .setPositiveButton(R.string.menu_ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .setNegativeButton(R.string.menu_no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                break;
            case R.id.menu_member:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_member)
                        .setMessage(getString(R.string.menu_member_message)+"\n"+"聖寰、晨霖、柏昇、濰藜、柏榕")
                        .setCancelable(false)
                        .setIcon(R.drawable.icon)
                        .setPositiveButton("確認", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
//                        .setNegativeButton(R.string.menu_no, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
                        .show();

                break;
            case R.id.menu_logout:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}//------------END