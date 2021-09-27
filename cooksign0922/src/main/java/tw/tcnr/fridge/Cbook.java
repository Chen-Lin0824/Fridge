package tw.tcnr.fridge;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Cbook extends AppCompatActivity {

    private Button btn_cancel;
    private Intent intent = new Intent();
    private TextView cbookname;
    private ImageView cbookimg;
    private TextView cbooktext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cookbook);
        setupViewComponent();
    }

    private void setupViewComponent() {

        //Intent intent = this.getIntent();
        Bundle bundle = getIntent().getExtras();

        cbookname=(TextView)findViewById(R.id.cbook_name);
        cbookname.setText(bundle.getString("Name_Test"));

        cbookimg=(ImageView)findViewById(R.id.cbook_img);
        cbookimg.setImageResource(bundle.getInt("Img"));

        cbooktext=(TextView)findViewById(R.id.cbook_t001);
        cbooktext.setText(bundle.getString("Text"));

        //cbookimg.setImageResource(intent.getIntExtra("ID",));
        //cbookimg = (ImageView) getIntent().getSerializableExtra("CbookImgObj");





    }


}//END