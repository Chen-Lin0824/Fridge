package tw.tcnr.fridge;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CeditMain extends AppCompatActivity implements View.OnClickListener {
    String TAG = "tcnr06=>";
    private TextView count_t;
    private EditText b_id;

    private TextView tvTitle;
    private Button btNext, btPrev, btTop, btEnd;
    private ArrayList<String> recSet;
    private int index = 0;
    String msg = null;
    //--------------------------
    private float x1; // 觸控的 X 軸位置
    private float y1; // 觸控的 Y 軸位置
    private float x2;
    private float y2;
    int range = 50; // 兩點距離
    int ran = 60; // 兩點角度

    private Button btEdit, btDel;
    String ttitle, trecipe_text, taddr;

    private Spinner mSpnName;
    int up_item = 0;
    //------------------------------
    protected static final int BUTTON_POSITIVE = -1;
    protected static final int BUTTON_NEGATIVE = -2;
    private Button btAdd, btAbandon, btquery, btcancel, btreport;

    private String[] MYCOLUMN = new String[]{"id", "name", "grp", "address"};
    int tcount;

    // ------------------
    private RelativeLayout b_Relbutton;
    private RelativeLayout brelative01;
    private LinearLayout blinear02;
    private ListView listView;
    private TextView bsubTitle;
    //===============
    private DbHelper dbHper;
    private static final String DB_FILE = "Fridge.db";
    private static final String DB_TABLE_Cook = "recipe";
    private static final int DBversion = 1;
    //-----------------
    private String sqlctl;
    private String tid;
    private String s_id;
    private String taddress;
    private int old_index = 0;

    private MenuItem b_m_add, b_m_query, b_m_batch, b_m_list, b_m_mysql, b_m_edit_start, b_m_edit_stop, b_m_return;
    private Menu menu;
    private boolean touch_flag;

    private TextView b_servermsg;
    private String ser_msg;
    private int servermsgcolor;

    // ----------定時更新--------------------------------
    private Long startTime;
    private Handler handler = new Handler();

    int autotime = 3;// 要幾秒的時間 更新匯入MySQL資料
    //------------------------------
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private TextView nowtime;  //顯示更新時間及次數
    int update_time = 0;
    private String str;
    // --------------------------------------------------------
    private boolean runAuto_flag = false;  //Runable updateTime 狀態
    private TextView b_editon;
    private String stHead;
    private boolean edittype_flag = false;  //編輯狀態
    private String showip; //顯示手機ip
    private EditText b_title,b_recipe_text;

    // --------------------------------------------------------
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        enableStrictMode(this);//使用暫存堆疊，必須加入此方法
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cedit_main);
        //--------取得目前時間
        startTime = System.currentTimeMillis();
        // -----------------
        setupViewComponent();
    }

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


    private void setupViewComponent() {
        tvTitle = (TextView) findViewById(R.id.tvIdTitle);
        b_id = (EditText) findViewById(R.id.edid);
        b_title = (EditText) findViewById(R.id.et_title);
        b_recipe_text = (EditText) findViewById(R.id.et_content);

        count_t = (TextView) findViewById(R.id.count_t);

        btNext = (Button) findViewById(R.id.btIdNext);
        btPrev = (Button) findViewById(R.id.btIdPrev);
        btTop = (Button) findViewById(R.id.btIdtop);
        btEnd = (Button) findViewById(R.id.btIdend);

        btEdit = (Button) findViewById(R.id.btnupdate);
        btDel = (Button) findViewById(R.id.btIdDel);

        //-----------------------
        btAdd = (Button) findViewById(R.id.btnAdd);
        btAbandon = (Button) findViewById(R.id.btnabandon);
        btquery = (Button) findViewById(R.id.btnquery);
        btcancel = (Button) findViewById(R.id.btidcancel);
        btreport = (Button) findViewById(R.id.btnlist);

        brelative01 = (RelativeLayout) findViewById(R.id.chp_relative01);
        blinear02 = (LinearLayout) findViewById(R.id.chp_rel_ceditlinear01);

        listView = (ListView) findViewById(R.id.listView);

        b_Relbutton = (RelativeLayout) findViewById(R.id.chp_Relbutton);
        listView = (ListView) findViewById(R.id.listView);
        bsubTitle = (TextView) findViewById(R.id.subTitle);
        b_editon = (TextView) findViewById(R.id.edit_on);

        b_servermsg = (TextView) findViewById(R.id.servermsg);
        tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.Teal));
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Yellow));
        //-----------------
        mSpnName = (Spinner) this.findViewById(R.id.spnName);
        nowtime = (TextView) findViewById(R.id.now_time);
        //---------設定layout 顯示---------------
        u_layout_def();
        //-----------------------
        btNext.setOnClickListener(this);
        btPrev.setOnClickListener(this);
        btTop.setOnClickListener(this);
        btEnd.setOnClickListener(this);

        btEdit.setOnClickListener(this);
        btDel.setOnClickListener(this);

        btAdd.setOnClickListener(this);
        btAbandon.setOnClickListener(this);
        btquery.setOnClickListener(this);
        btcancel.setOnClickListener(this);
        btreport.setOnClickListener(this);

        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        //-----------------
        mSpnName = (Spinner) this.findViewById(R.id.spnName);
        //===================================
        nowtime = (TextView) findViewById(R.id.now_time);
        startTime = System.currentTimeMillis();        // 取得目前時間
//        ************************************
        if (runAuto_flag == false) {
            handler.postDelayed(updateTimer, 500);  // 設定Delay的時間
            runAuto_flag = true;
        }
        // ************************************
        //===================================
        java.sql.Date curDate = new java.sql.Date(System.currentTimeMillis()); //  獲取當前時間
        str = formatter.format(curDate);
        nowtime.setText(getString(R.string.now_time) + str);
        // ----------------------------------------
        initDB();

        if(recSet==null || recSet.size()<1){
            //無資料的時候
            Toast.makeText(this, "無資料顯示", Toast.LENGTH_SHORT).show();

        }else {
            //有資料的時候
            int a=0;
            showRec(index);
            u_setspinner();
            stHead = "顯示資料：第" + (index + 1) + " / " + tcount + " 筆";
            tvTitle.setText(stHead);
            b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
            // -------------------------
            mSpnName.setOnItemSelectedListener(mSpnNameOnItemSelLis);
        }



    }

    private Runnable updateTimer=new Runnable() {
        @Override
        public void run() {
            old_index = mSpnName.getSelectedItemPosition();
            Long spentTime = System.currentTimeMillis() - startTime;
            String hours = String.format("%02d", (spentTime / 1000) / 60 / 60);  // 計算目前已過分鐘數
            String minius = String.format("%02d", ((spentTime / 1000) / 60) % 60);  // 計算目前已過分鐘數
            String seconds = String.format("%02d", (spentTime / 1000) % 60);          // 計算目前已過秒數
            handler.postDelayed(this, autotime * 1000); // 真正延遲的時間
            // -------執行匯入MySQL
            dbmysql();

            recSet = dbHper.getRecSet_Cook();  //重新載入SQLite
//            int a=0;
            u_setspinner();  //重新設定spinner內容
            index = old_index;
            showRec(index); //重設spainner 小窗顯示及細目內容
            //-------------------------------------------------------------------------------
            ++update_time;
            nowtime.setText(getString(R.string.now_time) + "(每" + autotime + "秒)" + str + "->"
                    + hours + ":" + minius + ":" + seconds
                    + " (" + (update_time) + "次)");
            //------ 宣告鈴聲 ---------------------------
//            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100); // 100=max
//            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 500);
//            toneG.release();
            // --------------------------------------------------------

        }
    };


    private void initDB() {
        if (dbHper == null)
            dbHper = new DbHelper(this, DB_FILE, null, DBversion);
        //-----****此處要加 才不會造成 SQLite 無資料 閃退****------
        dbmysql();
        //-----------
        recSet = dbHper.getRecSet_Cook();
    }

    private void u_layout_def() {
        btAdd.setVisibility(View.INVISIBLE);
        btAbandon.setVisibility(View.INVISIBLE);
        btquery.setVisibility(View.INVISIBLE);
        btEdit.setVisibility(View.VISIBLE);
        btDel.setVisibility(View.VISIBLE);

        brelative01.setVisibility(View.VISIBLE);
        blinear02.setVisibility(View.INVISIBLE);
        btreport.setVisibility(View.INVISIBLE);

        b_id.setEnabled(false);
        //-----------------------
        b_Relbutton.setVisibility(View.INVISIBLE);
        b_editon.setVisibility(View.INVISIBLE);
        //-----------------------

    }

    private Spinner.OnItemSelectedListener mSpnNameOnItemSelLis = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView parent, View view, int position,
                                   long id) {
            int iSelect = mSpnName.getSelectedItemPosition(); //找到按何項
            String[] fld = recSet.get(iSelect).split("#");
            String s = "資料：共" + recSet.size() + " 筆," + "你按下  " + String.valueOf(iSelect + 1) + "項"; //起始為0
            tvTitle.setText(s);
            b_id.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.Red));
            b_id.setText(fld[0]);
            b_title.setText(fld[1]);
            b_recipe_text.setText(fld[2]);
            //-------目前所選的item---
            index = iSelect;
            // -----新增完清空白在此---------------
            if(btAdd.getVisibility() == View.VISIBLE){
                b_id.setHint("請繼續輸入");
                b_title.setHint("為你的食譜取一個好名字吧！");
                b_recipe_text.setHint("盡情發揮你的創意吧！");
                b_id.setText("");
                b_title.setText("");
                b_recipe_text.setText("");
                //-------使用者IP取得
                showip=NetworkDetect();
                //b_address.setText(showip);
            }
            //--------------------------------
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            b_id.setText("");
            b_title.setText("");
            b_recipe_text.setText("");

        }
    };

    @Override
    public void onClick(View v) {
        int rowsAffected;
        Uri uri;
        String s_id;
        String whereClause;
        String[] selectionArgs;
        switch (v.getId()) {
            case R.id.btIdNext:
                ctlNext();
                break;
            case R.id.btIdPrev:
                ctlPrev();
                break;
            case R.id.btIdtop:
                ctlFirst();
                break;
            case R.id.btIdend:
                ctlLast();
                break;

            //------------------------------------
            case R.id.btnupdate:
                // 資料更新



                tid = b_id.getText().toString().trim();
                ttitle = b_title.getText().toString().trim();
                trecipe_text = b_recipe_text.getText().toString().trim();


                    //-------------------------------------
                old_index=index;
                mysql_update(); // 執行MySQL更新
                dbmysql();
                //-------------------------------------
                recSet = dbHper.getRecSet_Cook();
                u_setspinner();
                index=old_index;
                showRec(index);
                msg = "第 " + (index + 1) + " 筆記錄  已修改 ! " ;
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

                break;

            //------------------------------------
            case R.id.btIdDel:
                // 刪除資料 --使用對話盒
                Cook_MyAlertDialog myAltDlg = new Cook_MyAlertDialog(this);
                myAltDlg.getWindow().setBackgroundDrawableResource(R.color.Yellow);
                myAltDlg.setTitle("刪除資料");
                myAltDlg.setMessage("資料刪除無法復原\n確定將所有資料刪除嗎?");
                myAltDlg.setCancelable(false);
                myAltDlg.setIcon(android.R.drawable.ic_delete);
                myAltDlg.setButton(DialogInterface.BUTTON_POSITIVE, "確定刪除", aldBtListener);
                myAltDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "取消刪除", aldBtListener);
                myAltDlg.show();
                break;
            //-----------------------
            case R.id.btnAdd: //按下新增鈕
                // 查詢name是否有有此筆資料
                ttitle = b_title.getText().toString().trim();
                trecipe_text = b_recipe_text.getText().toString().trim();


                if (ttitle.equals("") || trecipe_text.equals("")  ) {
                    Toast.makeText(getApplicationContext(), "資料空白是無法新增的 !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //------直接增加到MySQL------------
                mysql_insert();
                dbmysql();
                //----------------------------------------
                msg = null;
                // -------------------------
                ContentValues newRow = new ContentValues();
                newRow.put("title", ttitle);
                newRow.put("recipe_text", trecipe_text);

                //------------------------------

                long rowID = dbHper.insertRec_Cookm(newRow);
                if (rowID != -1) {
                    b_id.setHint("請繼續輸入");
                    b_title.setText("繼續為你的食譜取個好名字吧！！");
                    b_recipe_text.setText("繼續發揮你的創意料理吧！！");
                    msg = "新增食譜  成功 ! \n" + "目前資料表共有 " + dbHper.RecCount_Cook() + " 筆食譜 !";
//                    ctlLast();  //成功跳到最後一筆
                } else {
                    msg = "新增食譜  失敗 !";
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                count_t.setText("共計:" + Integer.toString(dbHper.RecCount_Cook()) + "筆");
//                setupViewComponent();
                u_insert();

                dbmysql(); //重新匯入
                ctlLast();  //成功跳到最後一筆
                break;

            //------------------------------------
            case R.id.btnabandon: //按下放棄鈕

                u_menu_edit_main(); //返回編輯狀態
//                mSpnName.setEnabled(true);
//                Toast.makeText(getApplicationContext(), "*放棄新增*", Toast.LENGTH_SHORT).show();
//                initDB();
//                u_setspinner();
//                u_layout_def();
//                ctlLast();  //成功跳到最後一筆


                break;

            //------------------------------------
            case R.id.btnquery: //按下查詢鈕
                ttitle = b_title.getText().toString().trim();
                trecipe_text = b_recipe_text.getText().toString().trim();
                msg = null;
//                recSet = dbHper.getRecSet_query_Cook();
                recSet = dbHper.getRecSet_query_Cook(ttitle, trecipe_text);//ttitle, trecipe_text
                Toast.makeText(getApplicationContext(), "顯示資料： 共 " + recSet.size() + " 筆", Toast.LENGTH_SHORT).show();
                u_setspinner();
                break;
            //------------------------------------
            case R.id.btnlist: //按下列表鈕
                ttitle = b_title.getText().toString().trim();
                trecipe_text = b_recipe_text.getText().toString().trim();

                msg = null;
                recSet = dbHper.getRecSet_query_Cook(ttitle, trecipe_text);//ttitle, trecipe_text
                Toast.makeText(getApplicationContext(), "顯示資料： 共 " + recSet.size() + " 筆", Toast.LENGTH_SHORT).show();
//                bsubTitle.setText("顯示資料： 共 " + recSet.size() + " 筆");
//===========取SQLite 資料=============
                List<Map<String, Object>> mList;
                mList = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < recSet.size(); i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    String[] fld = recSet.get(i).split("#");
                    item.put("imgView", R.drawable.userconfig);
                    item.put("txtView", "id:" + fld[0] + "\n食譜名稱:" + fld[1] + "\n內容:" + fld[2]);
                    mList.add(item);
                }
//=========設定listview========
                brelative01.setVisibility(View.INVISIBLE);
                blinear02.setVisibility(View.VISIBLE);
                SimpleAdapter adapter = new SimpleAdapter(
                        this,
                        mList,
                        R.layout.recipe_list_item,
                        new String[]{"imgView", "txtView"},
                        new int[]{R.id.imgView, R.id.txtView}
                );
                listView.setAdapter(adapter);
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(listviewOnItemClkLis);
                break;
            //------------------------------------
            case R.id.btidcancel://關閉列表
                old_index=mSpnName.getSelectedItemPosition();
                dbmysql();
                recSet=dbHper.getRecSet_Cook();//重新載入SQLite
                u_setspinner();//重新設定spinner內容
//-------------------------
                u_menu_main();
                edittype_flag=false;
                index=old_index;
                showRec(index);//重設定spinner小窗顯示及項目內容
                //********************************************
                if (runAuto_flag == false ) {
                    handler.post(updateTimer);  //開啟執行續
                    runAuto_flag = true;
                }
                //********************************************
                break;
        }
    }//------onClick END

    private void mysql_update() {
        s_id = b_id.getText().toString().trim();
        ttitle = b_title.getText().toString().trim();
        trecipe_text = b_recipe_text.getText().toString().trim();

        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(s_id);
        nameValuePairs.add(ttitle);
        nameValuePairs.add(trecipe_text);

        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = Cook_DBConnector.executeUpdate_Cook( nameValuePairs);
//-----------------------------------------------
    }

    private void mysql_insert() {
//        sqlctl = "SELECT * FROM member ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        nameValuePairs.add(ttitle);
        nameValuePairs.add(trecipe_text);

        //--------寫死的參數
//        nameValuePairs.add("456456456");
//        nameValuePairs.add("454566");
//        nameValuePairs.add("777777");
        try {
            Thread.sleep(500); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = Cook_DBConnector.executeInsert_Cook(nameValuePairs);  //真正執行新增
//-----------------------------------------------

    }

    private ListView.OnItemClickListener listviewOnItemClkLis = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String s = "你按下第 " + Integer.toString(position + 1) + "筆"
                    + ((TextView) view.findViewById(R.id.txtView))
                    .getText()
                    .toString();
            bsubTitle.setText(s);
        }
    };
    // ---------------------------------------------
    private DialogInterface.OnClickListener aldBtListener = new DialogInterface.OnClickListener() {

        private int old_index;

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
//                    int rowsAffected = dbHper.clearRec();  //刪除全部資料
//                tid = b_id.getText().toString().trim();
                    old_index=index;
                    // ---------------------------
                    mysql_del();// 執行MySQL刪除
                    dbmysql();
                    // ---------------------------
                    index=old_index;
                    u_setspinner();
                    if (index == dbHper.RecCount_Cook()) {
                        index--;
                    }
                    showRec(index);
//                    mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
//                }
                    msg = "資料已刪除" ;
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    break;

                case BUTTON_NEGATIVE:
                    msg = "放棄刪除所有資料 !";
                    Toast.makeText(CeditMain.this, msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void mysql_del() {
        //---------
        s_id = b_id.getText().toString().trim();
        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        nameValuePairs.add(s_id);
        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = Cook_DBConnector.executeDelet_Cook(nameValuePairs);   //執行刪除
//-----------------------------------------------
    }

    private void u_setspinner() {//重構

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        for (int i = 0; i < recSet.size(); i++) {
            String[] fld = recSet.get(i).split("#");
            adapter.add(fld[0] + " " + fld[1] + " " + fld[2]);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpnName.setAdapter(adapter);

        mSpnName.setOnItemSelectedListener(mSpnNameOnItemSelLis);
        //        mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆

    }

    private void showRec(int index) {

        msg = "";
        if (recSet.size() != 0) {
            String stHead = "顯示資料：第 " + (index + 1) + " 筆 / 共 " + recSet.size() + " 筆";
            msg = getString(R.string.count_t) + recSet.size() + "筆";
            tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.Teal));
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Yellow));
            tvTitle.setText(stHead);
//            int a=0;

            String[] fld = recSet.get(index).split("#");
//            int b=0;
            b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
            b_id.setBackgroundColor(ContextCompat.getColor(this, R.color.Yellow));
            b_id.setText(fld[0]);
            b_title.setText(fld[1]);
            b_recipe_text.setText(fld[2]);
//            int a1=0;
            mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
//            int a2=0;
        } else {
            String stHead = "顯示資料：0 筆";
            msg = getString(R.string.count_t) + "0筆";
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Blue));
            tvTitle.setText(stHead);
            b_id.setText("");
            b_title.setText("");
            b_recipe_text.setText("");

        }

        count_t.setText(msg);


    }

    //------------------------------------------------
    private void ctlFirst() {
        // 第一筆
        index = 0;
        showRec(index);
    }

    private void ctlPrev() {
        // 上一筆
        index--;
        if (index < 0)
            index = recSet.size() - 1;
        showRec(index);
    }

    private void ctlNext() {
        // 下一筆
        index++;
        if (index >= recSet.size())
            index = 0;
        showRec(index);
    }


    private void ctlLast() {
        // 最後一筆
        index = recSet.size() - 1;
        showRec(index);
    }

    // ---------------------------------------------
    public boolean onKeyDown(int keyCode, KeyEvent event) { //禁用返回鍵
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        }
        return false;
    }

    //---------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
        if (runAuto_flag == true) {
            handler.removeCallbacks(updateTimer); //關閉執行續
            runAuto_flag = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //********************************************
        if (runAuto_flag == false && edittype_flag == false) {
            handler.post(updateTimer);  //開啟執行續
            runAuto_flag = true;
        }
        //********************************************

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        handler.removeCallbacks(updateTimer);
    }



    private void u_insert() {
        btAdd.setVisibility(View.VISIBLE);
        btAbandon.setVisibility(View.VISIBLE);
        btEdit.setVisibility(View.INVISIBLE);
        btDel.setVisibility(View.INVISIBLE);
        b_id.setEnabled(false);

        mSpnName.setEnabled(false);
        b_id.setHint("請繼續輸入");
        b_title.setHint("請為你的食譜想一個名字吧！");
        b_recipe_text.setHint("發揮無限創意、紀錄美味的一刻吧！");
        b_id.setText("");
        b_title.setText("");
        b_recipe_text.setText("");


        //-------使用者IP取得
        showip=NetworkDetect();
        //b_address.setText(showip);

    }

    private String NetworkDetect() {//取得手機IP

        ConnectivityManager CM = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        WifiManager wm = (WifiManager) getApplicationContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        String IPaddress = Finduserip.NetwordDetect(CM, wm);
        return IPaddress;
    }

    // 讀取MySQL 資料
    private void dbmysql() {
        sqlctl = "SELECT * FROM recipe ORDER BY id ASC";
        //sqlctl = "SELECT * FROM recipe WHERE"
        //sqlctl = "SELECT * FROM recipe";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = Cook_DBConnector.executeQuery_Cook(nameValuePairs);//匯入mysql的資料
            //==========================================
            chk_httpstate();  //檢查 連結狀態
            //==========================================
            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            JSONArray jsonArray = new JSONArray(result);
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL 連結成功有資料
                int rowsAffected = dbHper.clearRec_Cook(); // 匯入前,刪除所有SQLite資料(因為SQLite是暫存的資料，所以在匯入真正資料庫時要清空暫存資料，不然ID等資料會重複
                // 處理JASON 傳回來的每筆資料
                //Toast.makeText(getApplicationContext(), "匯入資料庫成功", Toast.LENGTH_LONG).show();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonData = jsonArray.getJSONObject(i);
                    ContentValues newRow = new ContentValues();
                    // --(1) 自動取的欄位 --取出 jsonObject 每個欄位("key","value")-----------------------
//                    Iterator itt = jsonData.keys();
//                    while (itt.hasNext()) {
//                        String key = itt.next().toString();
//                        String value = jsonData.getString(key); // 取出欄位的值
//                        if (value == null) {
//                            continue;
//                        } else if ("".equals(value.trim())) {
//                            continue;
//                        } else {
//                            jsonData.put(key, value.trim());
//                        }
//                        // ------------------------------------------------------------------
//                        newRow.put(key, value.toString()); // 動態找出有幾個欄位
//                        // -------------------------------------------------------------------
//                    }
                    // ---(2) 使用固定已知欄位---------------------------
                     newRow.put("id", jsonData.getString("id").toString());
                     newRow.put("title",
                     jsonData.getString("title").toString());
                     newRow.put("recipe_text", jsonData.getString("recipe_text").toString());

                    // -------------------加入SQLite---------------------------------------
                    long rowID = dbHper.insertRec_Cookm(newRow);
                    //Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();
                }
                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            recSet = dbHper.getRecSet_Cook();  //重新載入SQLite
            u_setspinner();
            // --------------------------------------------------------
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

    private void chk_httpstate() {
//**************************************************
//*       檢查連線狀況
//**************************************************
        //存取類別成員 DBConnector01.httpstate 判定是否回應 200(連線要求成功)
        if (Cook_DBConnector.httpstate == 200) {
            ser_msg = "伺服器匯入資料(code:" + Cook_DBConnector.httpstate + ") ";
            servermsgcolor = ContextCompat.getColor(this, R.color.Navy);
//                Toast.makeText(getBaseContext(), "由伺服器匯入資料 ",
//                        Toast.LENGTH_SHORT).show();
        } else {
            int checkcode = Cook_DBConnector.httpstate / 100;
            switch (checkcode) {
                case 1:
                    ser_msg = "資訊回應(code:" + Cook_DBConnector.httpstate + ") ";
                    break;
                case 2:
                    ser_msg = "已經完成由伺服器會入資料(code:" + Cook_DBConnector.httpstate + ") ";
                    break;
                case 3:
                    ser_msg = "伺服器重定向訊息，請稍後在試(code:" + Cook_DBConnector.httpstate + ") ";
                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 4:
                    ser_msg = "用戶端錯誤回應，請稍後在試(code:" + Cook_DBConnector.httpstate + ") ";
                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
                case 5:
                    ser_msg = "伺服器error responses，請稍後在試(code:" + Cook_DBConnector.httpstate + ") ";
                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
                    break;
            }
//                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
        }
        if (Cook_DBConnector.httpstate == 0) {
            ser_msg = "遠端資料庫異常(code:" + Cook_DBConnector.httpstate + ") ";
        }
        b_servermsg.setText(ser_msg);
        b_servermsg.setTextColor(servermsgcolor);

        //-------------------------------------------------------------------
    }
    //------MENU-------

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.cedit_main, menu);
            this.menu = menu;
//
            b_m_add = menu.findItem(R.id.m_add);
            b_m_query = menu.findItem(R.id.m_query);
            b_m_list = menu.findItem(R.id.m_list);
            b_m_edit_start = menu.findItem(R.id.m_edit_start);
            b_m_edit_stop = menu.findItem(R.id.m_edit_stop);
            b_m_return = menu.findItem(R.id.m_return);
//        ========================
            u_menu_main();
            return true;
        }

    private void u_menu_main() {
        brelative01.setVisibility(View.VISIBLE);
        blinear02.setVisibility(View.INVISIBLE);
//        b_Relbutton.setVisibility(View.VISIBLE);
        menu.setGroupVisible(R.id.m_group1, true);
        menu.setGroupVisible(R.id.m_group2, false);
        menu.setGroupVisible(R.id.m_group3, false);
        b_Relbutton.setVisibility(View.INVISIBLE);
        mSpnName.setVisibility(View.VISIBLE);
        b_editon.setVisibility(View.INVISIBLE);
        btEdit.setVisibility(View.INVISIBLE);
        btDel.setVisibility(View.INVISIBLE);
        //--------------------------

    }
    private void u_menu_edit_main() {
//*************************************
        if (runAuto_flag == true) {
            handler.removeCallbacks(updateTimer); //關閉執行續
            runAuto_flag = false;
        }
//***************************************
        menu.setGroupVisible(R.id.m_group1, false);
        menu.setGroupVisible(R.id.m_group2, true);
        menu.setGroupVisible(R.id.m_group3, false);
        //--------------------------
        b_Relbutton.setVisibility(View.VISIBLE);
        b_editon.setVisibility(View.VISIBLE);
        btAdd.setVisibility(View.INVISIBLE);
        btAbandon.setVisibility(View.INVISIBLE);
        btquery.setVisibility(View.INVISIBLE);
        btreport.setVisibility(View.INVISIBLE);
        btEdit.setVisibility(View.VISIBLE);
        btDel.setVisibility(View.VISIBLE);
        mSpnName.setVisibility(View.VISIBLE);
        mSpnName.setEnabled(true);

        u_button_ontouch();
        touch_flag = true;  //開啟ontuchevent
        index = mSpnName.getSelectedItemPosition(); // 找到按何項
        //        mSpnName.setEnabled(false);
        showRec(index); //重設spainner 小窗顯示及細目內容
    }

    private void u_button_ontouch() {
        btTop.setVisibility(View.VISIBLE);
        btNext.setVisibility(View.VISIBLE);
        btPrev.setVisibility(View.VISIBLE);
        btEnd.setVisibility(View.VISIBLE);
    }

    private void u_menu_return() {
        menu.setGroupVisible(R.id.m_group1, false);
        menu.setGroupVisible(R.id.m_group2, false);
        menu.setGroupVisible(R.id.m_group3, true);
    }

    private void stop_edit() {
        mSpnName.setEnabled(true);
        old_index = mSpnName.getSelectedItemPosition();
        u_menu_main();
        edittype_flag = false;
        // -------執行匯入MySQL
        dbmysql();
        recSet = dbHper.getRecSet_Cook();  //重新載入SQLite
        u_setspinner();  //重新設定spinner內容
        index = old_index;
        showRec(index); //重設spainner 小窗顯示及細目內容
        //********************************************
        if (runAuto_flag == false && edittype_flag == false) {
            handler.post(updateTimer);  //開啟執行續
            runAuto_flag = true;
        }
        //********************************************
    }

    @Override
        public boolean onOptionsItemSelected (MenuItem item){
            Intent it = new Intent();
            switch (item.getItemId()) {
                case R.id.m_add://新增
                    u_insert();
                    break;
                case R.id.m_query://查詢
                    btAdd.setVisibility(View.INVISIBLE);
                    btAbandon.setVisibility(View.VISIBLE);
                    btEdit.setVisibility(View.INVISIBLE);
                    btDel.setVisibility(View.INVISIBLE);
                    btquery.setVisibility(View.VISIBLE);
                    b_id.setEnabled(false);
                    b_id.setText("");
                    b_title.setText("");
                    b_recipe_text.setText(" ");

                    b_id.setHint("(欄位未輸入時,表示皆可)");
                    break;

                case R.id.m_list://列表
                    btAdd.setVisibility(View.INVISIBLE);
                    btAbandon.setVisibility(View.VISIBLE);
                    btEdit.setVisibility(View.INVISIBLE);
                    btDel.setVisibility(View.INVISIBLE);
                    btquery.setVisibility(View.INVISIBLE);
                    btreport.setVisibility(View.VISIBLE);

                    brelative01.setVisibility(View.VISIBLE);
                    blinear02.setVisibility(View.INVISIBLE);
                    b_id.setEnabled(false);
                    b_id.setText("");
                    b_title.setText("");
                    b_recipe_text.setText("");

                    b_id.setHint("請輸入");
                    break;


                case R.id.m_return:
                    btAbandon.performClick(); //觸發放棄按鈕
                    break;

                case R.id.action_settings:
                    this.finish();
                    // finish()：結束當前 Activity，不會立即釋放內存。遵循 android 內存管理機制。
                    // exit()：結束當前組件如 Activity，並立即釋放當前 Activity 所占資源。
                    // killProcess()：結束當前組件如 Activity，並立即釋放當前Activity  所占資源。
                    // restartPackage()：結束整個 App，包括 service 等其它 Activity 組件。
//                btAbandon.performClick();
                    break;

                case R.id.m_edit_start:  //啟用編輯
                    u_menu_edit_main();
                    edittype_flag = true;
                    break;

                case R.id.m_edit_stop: //關閉編輯
                    stop_edit(); //返回起始狀態
                    break;

//                case R.id.action_settings:
//                    this.finish();
//                    break;
            }

            return super.onOptionsItemSelected(item);
        }




    // ---------------------------------------------

}//-----END