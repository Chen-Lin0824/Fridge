package tw.tcnr.fridge;

import static android.content.ContentValues.TAG;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Expired extends AppCompatActivity implements View.OnClickListener {
    private TextView count_t;
    private EditText exp_id, exp_name, exp_date;


    private TextView tvTitle;
    private Button btNext, btPrev, btTop, btEnd;
    private ArrayList<String> recSet;
    private int index = 0;
    String msg = null;
    //--------------------------


    private Button btEdit, btDel;
    String tname, tgrp, taddr;

    private Spinner mSpnName;
    int up_item = 0;
    //------------------------------
    protected static final int BUTTON_POSITIVE = -1;
    protected static final int BUTTON_NEGATIVE = -2;
    private Button btAdd, btAbandon, btquery, btcancel, btreport;

    //    private static ContentResolver mContRes;
    private String[] MYCOLUMN = new String[]{"id", "name", "dates"};
    int tcount;

    // ------------------
    public static String myselection = "";
    public static String myorder = "id ASC"; // 排序欄位
    public static String myargs[] = new String[]{};

    private LinearLayout blinear02;
    private ListView listView;
    private TextView bsubTitle;
    private RelativeLayout blinear01;
    //===============
    private DbHelper dbHper;
    private static final String DB_FILE = "fridge.db"; //資料庫名稱
    private static final String DB_TABLE = "fridge";
    private static final int DBversion = 1;
    //-----------------
    private String sqlctl;
    private String tid;
    private String s_id;
    private String ser_msg;
    private int servermsgcolor;
    private TextView b_servermsg;
    //--------------------------
    // ----------定時更新--------------------------------
    private Long startTime;
    private Handler handler = new Handler();

//    int autotime =120;// 要幾秒的時間 更新匯入MySQL資料
    //------------------------------
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
    private TextView nowtime;  //顯示更新時間及次數
    int update_time = 0;
    private String str;



    private Uri uri;

    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableStrictMode(this);//使用Thead 暫存時宣告
        setContentView(R.layout.expsign01);
//        setupViewComponent();
    }

    private void enableStrictMode(Context context) {
        //-------------抓取遠端資料庫設定執行續------------------------------
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

        // 設定class標題
        Intent intent = this.getIntent();
        String mode_title = intent.getStringExtra("class_title");
        this.setTitle(mode_title);


        tvTitle = (TextView) findViewById(R.id.tvIdTitle); //綠框資料內容
        exp_id = (EditText) findViewById(R.id.edid); //id顯示欄位
        exp_name = (EditText) findViewById(R.id.edtName);//顯示姓名
        exp_date = (EditText) findViewById(R.id.edtGrp);//顯示群組

        b_servermsg=(TextView)findViewById(R.id.tserver_msg);
        btNext = (Button) findViewById(R.id.btIdNext);//下一筆
        btPrev = (Button) findViewById(R.id.btIdPrev);//前一筆
        btTop = (Button) findViewById(R.id.btIdtop);//首筆
        btEnd = (Button) findViewById(R.id.btIdend);//尾筆

        btEdit = (Button) findViewById(R.id.btnupdate);//更新
        btDel = (Button) findViewById(R.id.btIdDel);//刪除

        //-----------------------
        btAdd = (Button) findViewById(R.id.btnAdd);//新增
        btAbandon = (Button) findViewById(R.id.btnabandon);//取消執行
        btquery = (Button) findViewById(R.id.btnquery);//查詢
        btcancel = (Button) findViewById(R.id.btidcancel);// list item 列表清單裡的取消按鈕
        btreport = (Button) findViewById(R.id.btnlist);//列表

        blinear01 = (RelativeLayout) findViewById(R.id.relative01);
        blinear02 = (LinearLayout) findViewById(R.id.linear02);

        listView = (ListView) findViewById(R.id.listView);// list item 列表清單
        bsubTitle = (TextView) findViewById(R.id.subTitle);// list item 列表清單標題
        //---------設定layout 顯示---------------
        u_layout_def();
        //-----------------------------------------按鈕的監聽

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

        b_servermsg = (TextView) findViewById(R.id.servermsg);
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.Aqua));

        mSpnName = (Spinner) this.findViewById(R.id.spnName);

        //===================================
        nowtime = (TextView) findViewById(R.id.now_time);
        startTime = System.currentTimeMillis();        // 取得目前時間

        handler.postDelayed(updateTimer, 1000);  // 設定Delay的時間
        //===================================
        java.sql.Date curDate = new java.sql.Date(System.currentTimeMillis()); //  獲取當前時間
        str = formatter.format(curDate);
        nowtime.setText(getString(R.string.exp_nowtime) + str);
        // ----------------------------------------

        initDB();
        showRec(index);//顯示紀錄
        u_setspinner();
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));//首頁標題裡面字的顏色
        tvTitle.setText("顯示資料： 共" + tcount + " 筆");
        exp_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
        // -------------------------
        mSpnName.setOnItemSelectedListener(mSpnNameOnItemSelLis);//下拉監聽
//-------------------------------------------------------------------------


    }
    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            old_index = mSpnName.getSelectedItemPosition();
            Long spentTime = System.currentTimeMillis() - startTime;
            String hours = String.format("%02d", (spentTime / 1000) / 60 / 60);  // 計算目前已過分鐘數
            String minius = String.format("%02d", ((spentTime / 1000) / 60) % 60);  // 計算目前已過分鐘數
            String seconds = String.format("%02d", (spentTime / 1000) % 60);          // 計算目前已過秒數
//            handler.postDelayed(this, autotime * 1000); // 真正延遲的時間
            // -------執行匯入MySQL
            dbmysql();
            recSet = dbHper.getRecSetexp();  //重新載入SQLite
            u_setspinner();  //重新設定spinner內容
            index = old_index;
            showRec(index); //重設spainner 小窗顯示及細目內容
            //-------------------------------------------------------------------------------
            ++update_time;
//            nowtime.setText(getString(R.string.now_time) + "(每" + autotime + "秒)" + str + "->"
//                    + hours + ":" + minius + ":" + seconds
//                    + " (" + (update_time) + "次)");
//            //------ 宣告鈴聲 ---------------------------
//            ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100); // 100=max
//            toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 500);
//            toneG.release();
//            // --------------------------------------------------------
        }
    };



  private void u_setspinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        for (int i = 0; i < recSet.size(); i++) {
            String[] fld = recSet.get(i).split("#");
            adapter.add(fld[1] + " " + fld[2]);
        }
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//將數組新增到下拉選單
        mSpnName.setAdapter(adapter);

        mSpnName.setOnItemSelectedListener(mSpnNameOnItemSelLis);//下拉監聽
        //        mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆

    }

    private void initDB() { //進入資料庫
        if (dbHper == null)
            dbHper = new DbHelper(this, DB_FILE, null, DBversion);
        recSet = dbHper.getRecSetexp();//取得all data
    }

    private View.OnClickListener btn01On = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//        intent.putExtra("class_title87", getString(R.string.exp_clean));//呼叫其他
//            intent.setClass(Expired.this, Garsign01.class);//用m0607呼叫後面的
//            startActivity(intent);
        }
    };

      //刪除item



    //---------柏昇的4大主要功能監聽-------
//    private View.OnClickListener MainBtnOn =new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            switch (v.getId()){
//                case R.id.fri_b001:
//                    intent.putExtra("class_title",getString(R.string.app_name));
//                    intent.setClass(Expired.this,Frisign01.class );
//                    break;
//                case R.id.fri_b002:
//                    intent.putExtra("class_title",getString(R.string.expired_name));
//                    intent.setClass(Expired.this,Expired.class );
//                    break;
//                case R.id.fri_b003:
//                    intent.putExtra("class_title",getString(R.string.shop_name));
//                    intent.setClass(Expired.this,Shop.class );
//                    break;
//                case R.id.fri_b004:
//                    intent.putExtra("class_title",getString(R.string.cook_name));
//                    intent.setClass(Expired.this,Cooklist.class );
//                    break;
//            }
//            startActivity(intent);
//        }
//    };



    private void u_layout_def() {

        btAdd.setVisibility(View.INVISIBLE); //新增
        btAbandon.setVisibility(View.INVISIBLE);
        btquery.setVisibility(View.INVISIBLE);
        btEdit.setVisibility(View.INVISIBLE); //更新
        btDel.setVisibility(View.VISIBLE);

        blinear01.setVisibility(View.VISIBLE);
        blinear02.setVisibility(View.INVISIBLE);
        btreport.setVisibility(View.VISIBLE);
        exp_date.setEnabled(false);
        exp_id.setEnabled(false);//id不能輸入
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
            exp_id.setTextColor(ContextCompat.getColor(parent.getContext(), R.color.Red));
            exp_id.setText(fld[0]);
            exp_name.setText(fld[1]);
            exp_date.setText(fld[2]);

            //-------目前所選的item---
            index = iSelect;
            // -----新增完清空白在此---------------
            if(btAdd.getVisibility() == View.VISIBLE){
                exp_name.setHint("");
                exp_id.setHint("");
                exp_id.setText("");
                exp_name.setText("");
                exp_date.setText("");

            }
            //--------------------------------
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            // TODO Auto-generated method stub
            exp_id.setText("");
            exp_name.setText("");
            exp_date.setText("");

        }
    };

    @Override
    public void onClick(View v) {
        int rowsAffected;
        Uri uri;
        String s_id;
        String whereClause;
        String[] selectionArgs;
        Log.d(TAG, "a=" + v.getId());
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
                tid = exp_id.getText().toString().trim();
                tname = exp_name.getText().toString().trim();
                tgrp = exp_date.getText().toString().trim();
                old_index=index;
                mysql_update(); // 執行MySQL更新
                dbmysql();
                //-------------------------------------
                recSet = dbHper.getRecSetexp();
                u_setspinner();
                index=old_index;
                showRec(index);
                msg = "第 " + (index + 1) + " 筆記錄  已修改 ! " ;
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                ctlLast();
                break;

                ////                old_index=index;
//                String old_id = exp_id.getText().toString();
//                mysql_update(); // 執行MySQL更新
//                dbmysql();
//                //-------------------------------------
////             recSet = dbHper.getRecSetexp();  //這是陽春型
//                u_setspinner();
//
////                用 old_id 搜尋 index
//              recSet = dbHper.getRecSet_queryexp_id(old_id);



////--------------------------------
//                index=old_index;   //這是陽春型
//
//                showRec(index);
//                msg = "第 " + (index + 1) + " 筆記錄  已修改 ! " ;
//                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
//                ctlLast();


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

                // 查詢name是否有有此筆資料
                tname = exp_name.getText().toString().trim();
                tgrp = exp_date.getText().toString().trim();



                //-------直接增加到MySQL--------------
                mysql_insert();
                dbmysql();
                //----------------------------------------
                msg = null;
                // -------------------------
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 hh:mm:ss");
                Date date =new Date(System.currentTimeMillis());
                String time=simpleDateFormat.format(date);

                ContentValues newRow = new ContentValues();
                newRow.put("name", tname);
                newRow.put("dates", time);

                //------------------------------

                long rowID = dbHper.insertRecexp(newRow);
                if (rowID != -1) {
                    exp_id.setHint("");
                    exp_name.setText("");
                    exp_date.setText("");
                    msg = "新增記錄  成功 ! \n" + "目前資料表共有 " + dbHper.RecCountexp() + " 筆記錄 !";
                    ctlLast();  //成功跳到最後一筆
                } else {
                    msg = "新增記錄  失敗 !";
                }
//                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

//                setupViewComponent();
                dbmysql();
                ctlLast();  //成功跳到最後一筆
                u_insert();


                break;

            case R.id.btnabandon: //按下放棄鈕
                mSpnName.setEnabled(true);
//                Toast.makeText(getApplicationContext(), "*放棄新增*", Toast.LENGTH_SHORT).show();
                initDB();
                u_setspinner();
                u_layout_def();
                ctlLast();  //成功跳到最後一筆
                break;

            case R.id.btnquery: //按下查詢鈕
                tname = exp_name.getText().toString().trim();
                tgrp = exp_date.getText().toString().trim();
                exp_name.setText("");
                exp_date.setText("");
                msg = null;
                recSet = dbHper.getRecSet_queryexp(tname, tgrp);
//                Toast.makeText(getApplicationContext(), "顯示資料： 共 " + recSet.size() + " 筆", Toast.LENGTH_SHORT).show();
                u_setspinner();
                break;
            case R.id.btnlist: //按下列表鈕

                exp_id.setEnabled(false);
                exp_id.setText("");
                exp_name.setText("");
                exp_date.setText("");
                exp_id.setHint("");

                tname = exp_name.getText().toString().trim();
                tgrp = exp_date.getText().toString().trim();
                msg = null;
                recSet = dbHper.getRecSet_queryexp(tname, tgrp);
//                Toast.makeText(getApplicationContext(), "顯示資料： 共 " + recSet.size() + " 筆", Toast.LENGTH_SHORT).show();
//                bsubTitle.setText("顯示資料： 共 " + recSet.size() + " 筆");
//===========取SQLite 資料=============
                List<Map<String, Object>> mList;
                mList = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < recSet.size(); i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    String[] fld = recSet.get(i).split("#");
                    item.put("imgView", R.drawable.trashcan_btn);
                    item.put("txtView", "\n過期物品:" + fld[1] + "\n過期時間:" + fld[2]);
                    mList.add(item);
                }
//=========設定listview========
                blinear01.setVisibility(View.INVISIBLE);
                blinear02.setVisibility(View.VISIBLE);
                SimpleAdapter adapter = new SimpleAdapter(
                        this,
                        mList,
                        R.layout.exp_list_item,
                        new String[]{"imgView", "txtView"},
                        new int[]{R.id.imgView, R.id.txtView}
                );
                listView.setAdapter(adapter);
                listView.setTextFilterEnabled(true);
//                listView.setOnItemClickListener(listviewOnItemClkLis);
//                listView.setOnItemLongClickListener(gg);
                break;
            //------------------------------------

            case R.id.btidcancel:
//                blinear01.setVisibility(View.VISIBLE);
//                blinear02.setVisibility(View.INVISIBLE);
                u_layout_def();
                u_setspinner();
//                setupViewComponent();
                break;



            //--------------------------
            // ----------
            //-------------

        }
    }


    private void mysql_update() {
        s_id = exp_id.getText().toString().trim();
        tname = exp_name.getText().toString().trim();
        tgrp = exp_date.getText().toString().trim();
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(s_id);
        nameValuePairs.add(tname);
        nameValuePairs.add(tgrp);

        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = Cook_DBConnector.executeUpdate( nameValuePairs);
//-----------------------------------------------


    }


    private void mysql_insert() {
        //        sqlctl = "SELECT * FROM member ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        nameValuePairs.add(tname);
        nameValuePairs.add(tgrp);

        try {
            Thread.sleep(500); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = Cook_DBConnector.executeInsertexp(nameValuePairs);  //真正執行新增
//-----------------------------------------------
    }

//    private AdapterView.OnItemLongClickListener gg =new AdapterView.OnItemLongClickListener() {
//        @Override
//        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//            MyAlertDialogexp myAltDlg = new MyAlertDialogexp(Expired.this);
//            myAltDlg.getWindow().setBackgroundDrawableResource(R.color.Yellow);
//            myAltDlg.setTitle("刪除資料");
//            myAltDlg.setMessage("資料刪除無法復原\n確定刪除這筆資料嗎?");
//            myAltDlg.setCancelable(false);
//            myAltDlg.setIcon(android.R.drawable.ic_delete);
//            myAltDlg.setButton(DialogInterface.BUTTON_POSITIVE, "確定刪除", aldBtListener);
//            myAltDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "取消刪除", aldBtListener);
//            dbmysql();
//            myAltDlg.show();
//
//            return true;
//        }
//    };


//    private ListView.OnItemClickListener listviewOnItemClkLis = new ListView.OnItemClickListener() {
//        @Override
//        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            String s = "你按下第 " + Integer.toString(position + 1) + "筆"
//                    + ((TextView) view.findViewById(R.id.txtView))
//                    .getText()
//                    .toString();
//            bsubTitle.setText(s);
//        }
//    };
    private int old_index;
    // ---------------------------------------------
    private DialogInterface.OnClickListener aldBtListener = new DialogInterface.OnClickListener() { //清空資料的dialog

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
                    //                    int rowsAffected = dbHper.clearRecexp();  //刪除全部資料
//                tid = exp_id.getText().toString().trim();
                    old_index=index;
                    // ---------------------------
                    mysql_del();// 執行MySQL刪除

                    dbmysql();
                    // ---------------------------
                    index=old_index;
                    u_setspinner();
                    if (index == dbHper.RecCountexp()) {
                        index--;
                    }
                    showRec(index);
//                    mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
//                }
//                    msg = "資料已刪除" ;
//                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    break;
                case BUTTON_NEGATIVE:
//                    msg = "放棄刪除所有資料 !";
//                    Toast.makeText(Expired.this, msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    private void mysql_del() {
        //---------
        s_id = exp_id.getText().toString().trim();
        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        nameValuePairs.add(s_id);
        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = Cook_DBConnector.executeDelet(nameValuePairs);   //執行刪除
//-----------------------------------------------
    }


    private void showRec(int index) {
        msg = "";
        if (recSet.size() != 0) {
            String stHead = "顯示資料：第 " + (index + 1) + " 筆 / 共 " + recSet.size() + " 筆";
            msg = getString(R.string.exp_count_t) + recSet.size() + "筆";
            tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.Teal));
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Yellow));
            tvTitle.setText(stHead);

            String[] fld = recSet.get(index).split("#");
            exp_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
            exp_id.setBackgroundColor(ContextCompat.getColor(this, R.color.Yellow));
            exp_id.setText(fld[0]);
            exp_name.setText(fld[1]);
            exp_date.setText(fld[2]);
            mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
        } else {
            String stHead = "顯示資料：0 筆";
            msg = getString(R.string.exp_count_t) + "0筆";
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Blue));
            tvTitle.setText(stHead);
            exp_id.setText("");
            exp_name.setText("");
            exp_date.setText("");
        }


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

    //---------------------------------------------------
    @Override
    protected void onPause() {
        super.onPause();
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

    @Override
    protected void onResume() {
        super.onResume();
        if (dbHper == null)
            dbHper = new DbHelper(this, DB_FILE, null, DBversion);
        dbmysql();
        setupViewComponent();//onCreate(null); // 重構

    }




    private void u_insert() {
        btAdd.setVisibility(View.VISIBLE);
        btAbandon.setVisibility(View.VISIBLE);
        btEdit.setVisibility(View.INVISIBLE);
        btDel.setVisibility(View.INVISIBLE);
//        b_edid.setEnabled(false);
//        b_edid.setText("");
//        e001.setText("");
//        e002.setText("");
//        e003.setText("");

    }
    // 讀取MySQL 資料
    private void dbmysql() {
        sqlctl = "SELECT * FROM fridge ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = Cook_DBConnector.executeQueryexp(nameValuePairs);
            /**************************************************************************
             * SQL 結果有多筆資料時使用JSONArray
             * 只有一筆資料時直接建立JSONObject物件 JSONObject
             * jsonData = new JSONObject(result);
             **************************************************************************/
            //==========================================
//            chk_httpstate();  //檢查 連結狀態
//==========================================
            JSONArray jsonArray = new JSONArray(result);
            // -------------------------------------------------------
            if (jsonArray.length() > 0) { // MySQL 連結成功有資料
                int rowsAffected = dbHper.clearRecexp(); // 匯入前,刪除所有SQLite資料(因為SQLite是暫存的資料，所以在匯入真正資料庫時要清空暫存資料，不然ID等資料會重複
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
                    long rowID = dbHper.insertRecexp(newRow);
//                    Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();
                }
                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            recSet = dbHper.getRecSetexp();  //重新載入SQLite
            u_setspinner();
            // --------------------------------------------------------
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

//    private void chk_httpstate() {
//        //**************************************************
////*       檢查連線狀況
////**************************************************
//        //存取類別成員 DBConnector01.httpstate 判定是否回應 200(連線要求成功)
//        if (tw.tcnr09.expired.DBConnector.httpstate == 200) {
//            ser_msg = "伺服器匯入資料(code:" + tw.tcnr09.expired.DBConnector.httpstate + ") ";
//            servermsgcolor = ContextCompat.getColor(this, R.color.Blue);
////                Toast.makeText(getBaseContext(), "由伺服器匯入資料 ",
////                        Toast.LENGTH_SHORT).show();
//        } else {
//            int checkcode = tw.tcnr09.expired.DBConnector.httpstate / 100;
//            switch (checkcode) {
//                case 1:
//                    ser_msg = "資訊回應(code:" + tw.tcnr09.expired.DBConnector.httpstate + ") ";
//                    break;
//                case 2:
//                    ser_msg = "已經完成由伺服器會入資料(code:" + tw.tcnr09.expired.DBConnector.httpstate + ") ";
//                    break;
//                case 3:
//                    ser_msg = "伺服器重定向訊息，請稍後在試(code:" + tw.tcnr09.expired.DBConnector.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
//                    break;
//                case 4:
//                    ser_msg = "用戶端錯誤回應，請稍後在試(code:" + tw.tcnr09.expired.DBConnector.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
//                    break;
//                case 5:
//                    ser_msg = "伺服器error responses，請稍後在試(code:" + tw.tcnr09.expired.DBConnector.httpstate + ") ";
//                    servermsgcolor = ContextCompat.getColor(this, R.color.Red);
//                    break;
//            }
////                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
//        }
//        if (tw.tcnr09.expired.DBConnector.httpstate == 0) {
//            ser_msg = "遠端資料庫異常(code:" + DBConnector.httpstate + ") ";
//            servermsgcolor = ContextCompat.getColor(this, R.color.Red);
//        }
//        b_servermsg.setText(ser_msg);
//        b_servermsg.setTextColor(servermsgcolor);
//
//        //-------------------------------------------------------------------
//    }
    //------MENU-------




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainexp, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it = new Intent();
        switch (item.getItemId()) {
            case R.id.m_add://新增
                exp_id.setText("");
                exp_name.setText("");
                exp_date.setText("");
                exp_name.setHint("請輸入");
                exp_date.setHint("");

                u_insert();
                break;
            case R.id.m_query://查詢
                btAdd.setVisibility(View.INVISIBLE);
                btAbandon.setVisibility(View.VISIBLE);
                btEdit.setVisibility(View.INVISIBLE);
                btDel.setVisibility(View.INVISIBLE);
                btquery.setVisibility(View.VISIBLE);

                exp_id.setEnabled(false);
                exp_id.setText("");
                exp_name.setText("");
                exp_date.setText("");
                exp_id.setHint("");
                break;


//
//            case R.id.m_mysql:
//                // 匯入MySQL
//                dbmysql();
//                break;
//
//

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
                        .setIcon(R.drawable.circle)
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
                        .setMessage(getString(R.string.menu_member_message)+"\n"+"維尼、大神、佳佳、波波、柏榕、老大、培揚")
                        .setCancelable(false)
                        .setIcon(R.drawable.circle)
                        .setPositiveButton("確定", new DialogInterface.OnClickListener() {
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

            case R.id.menu_return:
                this.finish();
                break;

            case R.id.menu_logout:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
