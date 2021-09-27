package tw.tcnr.fridge;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class RANDOM_MEAL extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "tcnr07=>";
    private TextView t001;
    private Button b001;
    private int random_meal;
    private TextView tResult;
    private String answer;
//    private Uri FBuri;

    private DbHelper dbHper;
    private static final String DB_FILE = "Fridge.db";
    private static final String DB_TABLE_RandomMeal = "RandomMeal";
    private static final int DBversion = 1;
    private ArrayList<String> recSet;
    private ImageButton DBbtn;
    private Intent intent = new Intent();
    private ArrayList<String> exeRandom_ArrayList = new ArrayList<String>();
    private String tname="", tcontent="", tnote="";
    private String sqlctl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.random_meal);
        setupViewComponent();
        initDB();
        readSQL();
    }

    private void setupViewComponent() {
        enableStrictMode(this);//使用暫存堆疊，必須加入此方法
        initDB();
        readSQL();


        t001=(TextView)findViewById(R.id.RM_t001);
        tResult=(TextView)findViewById(R.id.RM_result);
        b001=(Button)findViewById(R.id.RM_b001);
        b001.setOnClickListener(this);

    }

    //------生命週期
    @Override
    public void onBackPressed() {
        initDB();
        readSQL();
//        super.onBackPressed();
    }
    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(TAG, "onStart()");
        initDB();
        readSQL();
    }
    @Override
    protected void onStop() {
        super.onStop();
        initDB();
        readSQL();
    }
    @Override
    protected void onPause() {
        super.onPause();
        initDB();
        readSQL();
    }
    @Override
    protected void onResume() {
        super.onResume();
        initDB();
        readSQL();
    }
    @Override
    protected void onRestart() {
        super.onRestart();
        initDB();
        readSQL();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        initDB();
        readSQL();
    }
    //------生命週期

    private void enableStrictMode(Context context) {
        //-------------抓取遠端資料庫設定執行續-------------------
        //----怕連上000時卡住，先把資料暫存，等主機OK再上傳
        StrictMode.setThreadPolicy(new
                StrictMode.
                        ThreadPolicy.Builder().
                detectDiskReads().
                detectDiskWrites().
                detectNetwork().
                penaltyLog().
                build());
        StrictMode.setVmPolicy(
                new
                        StrictMode.
                                VmPolicy.
                                Builder().
                        detectLeakedSqlLiteObjects().
                        penaltyLog().
                        penaltyDeath().
                        build());
    }

    private void initDB() {
        if (dbHper == null)
            dbHper = new DbHelper(this, DB_FILE, null, DBversion);
        dbmysql();
        recSet = dbHper.getRecSet_rm();
    }

    private void dbmysql() {
        sqlctl = "SELECT * FROM RandomMeal ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = Cook_DBConnector.executeQuery_ran(nameValuePairs);
            //==========================================
//            chk_httpstate();  //檢查 連結狀態
//==========================================
            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL 連結成功有資料
                int rowsAffected = dbHper.clearRec_rm();                 // 匯入前,刪除所有SQLite資料
                // 處理JASON 傳回來的每筆資料
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    ContentValues newRow = new ContentValues();
                    // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
                    Iterator itt = jsonData.keys();
                    while (itt.hasNext()) {
                        String key = itt.next().toString();
                        String value = jsonData.getString(key); // 取出欄位的值
                        if (value == null) {
                            continue;
                        } else if ("".equals(value.trim())) {
                            continue;
                        } else {
                            jsonData.put(key, value.trim());
                        }
                        // ------------------------------------------------------------------
                        newRow.put(key, value.toString()); // 動態找出有幾個欄位
                        // -------------------------------------------------------------------
                    }
                    // ---(2) 使用固定已知欄位---------------------------
                    // newRow.put("id", jsonData.getString("id").toString());
                    // newRow.put("name",
                    // jsonData.getString("name").toString());
                    // newRow.put("grp", jsonData.getString("grp").toString());
                    // newRow.put("address", jsonData.getString("address")
                    // -------------------加入SQLite---------------------------------------
                    long rowID = dbHper.insertRec_rm(newRow);
//                    Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();
                }
                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            recSet = dbHper.getRecSet_rm();  //重新載入SQLite
            // --------------------------------------------------------
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private void readSQL(){
        //===========取SQLite 資料=============
        recSet = dbHper.getRecSet_query_rm(tname, tcontent, tnote);
        List<Map<String, Object>> mList;
        mList = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < recSet.size(); i++) {
            Map<String, Object> item = new HashMap<String, Object>();
            String[] fld = recSet.get(i).split("#");

//            exeRandom_ArrayList.add(fld[0]);
            exeRandom_ArrayList.add(fld[1]);//只取[1]
//            exeRandom_ArrayList.add(fld[2]);
//            exeRandom_ArrayList.add(fld[3]);

        }

    }


    @Override
    public void onClick(View v) {
        random_meal = (int) (Math.random() * (exeRandom_ArrayList.size()));
        answer = exeRandom_ArrayList.get(random_meal);

        switch (v.getId()){
            case R.id.RM_b001:
                tResult.setText(answer);
//        tResult.setText(Integer.toString(exeRandom_ArrayList.size()));
//        tResult.setText(Integer.toString(random_meal));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.random_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item){
        Intent it = new Intent();

        switch (item.getItemId()) {
            case R.id.menu_edit:
                intent.setClass(RANDOM_MEAL.this, Random_setting.class);
                startActivity(intent);
                break;

            case R.id.menu_return:
                this.finish();
                break;

            case R.id.menu_logout:
                Toast.makeText(getApplicationContext(), "施工中", Toast.LENGTH_LONG).show();
                break;

//            case R.id.menu_member:
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.menu_member)
////                        .setMessage(getString(R.string.menu_member_message)+"\n"+"維尼、大神、佳佳、波波、柏榕、老大、培揚")
//                        .setCancelable(false)
////                        .setIcon(R.drawable.icon02)
//                        .setPositiveButton(R.string.menu_yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setNegativeButton(R.string.menu_no, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .show();
//                break;

//            case R.id.menu_fb:
//                FBuri = Uri.parse("https://www.facebook.com/kai.hao.9");
//                it = new Intent(Intent.ACTION_VIEW,FBuri);
//                startActivity(it);
//                break;

            //            case R.id.menu_notify:
//                new AlertDialog.Builder(this)
//                        .setTitle(R.string.menu_notify)
//                        .setMessage(getString(R.string.menu_message))
//                        .setCancelable(false)
////                        .setIcon(R.drawable.icon02)
//                        .setPositiveButton(R.string.menu_yes, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .setNegativeButton(R.string.menu_no, new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which) {
//
//                            }
//                        })
//                        .show();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }


}//***End


//        switch((int) random_meal){
//            case 1:
//                answer = getString(R.string.RM_resultTEST01) + "" + getString(R.string.RM_result);
//                break;
//            case 2:
//                answer = getString(R.string.RM_resultTEST02) + "" + getString(R.string.RM_result);
//                break;
//            case 3:
//                answer = getString(R.string.RM_resultTEST03) + "" + getString(R.string.RM_result);
//                break;
//            case 4:
//                answer = getString(R.string.RM_resultTEST04) + "" + getString(R.string.RM_result);
//                break;
//            case 5:
//                answer = getString(R.string.RM_resultTEST05) + "" + getString(R.string.RM_result);
//                break;
//            case 6:
//                answer = getString(R.string.RM_resultTEST06) + "" + getString(R.string.RM_result);
//                break;
//            case 7:
//                answer = getString(R.string.RM_resultTEST07) + "" + getString(R.string.RM_result);
//                break;
//            case 8:
//                answer = getString(R.string.RM_resultTEST08) + "" + getString(R.string.RM_result);
//                break;
//            case 9:
//                answer = getString(R.string.RM_resultTEST09) + "" + getString(R.string.RM_result);
//                break;
//        }

//        random_meal = Math.random();
//        if(random_meal < 0.334){
////            tResult.setText("麥當勞");
//            answer = getString(R.string.RM_resultTEST01) + "," + getString(R.string.RM_result);
//        }else{
//            if (random_meal < 0.667) {
//                answer = getString(R.string.RM_resultTEST02) + "," + getString(R.string.RM_result);
//            } else {
//                if(random_meal <= 0.999){
//                    answer = getString(R.string.RM_resultTEST03) + "," + getString(R.string.RM_result);
//                }else{
//                }
//            }
//
//            }