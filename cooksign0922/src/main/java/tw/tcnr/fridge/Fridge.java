package tw.tcnr.fridge;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Fridge extends AppCompatActivity implements View.OnClickListener {

    private TextView count_t;
    private EditText b_id, b_name, b_date;


    private TextView tvTitle;
    private Button btNext, btPrev, btTop, btEnd;
    private ArrayList<String> recSet;
    private int index = 0;
    String msg = null;
    String TAG = "tncr23=";
    //--------------------------
    private float x1; // 觸控的 X 軸位置
    private float y1; // 觸控的 Y 軸位置
    private float x2;
    private float y2;
    int range = 50; // 兩點距離
    int ran = 60; // 兩點角度

    private Button btEdit, btDel;
    String tname, tdate;

    private Spinner mSpnName;
    int up_item = 0;
    //------------------------------
    protected static final int BUTTON_POSITIVE = -1;
    protected static final int BUTTON_NEGATIVE = -2;
    private Button btAdd, btAbandon, btquery, btcancel, btreport;

    //    private static ContentResolver mContRes;
    private String[] MYCOLUMN = new String[]{"id", "name", "date"};
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
    private static final String DB_FILE = "Fridge.db";
    private static final String DB_TABLE_fri = "fridge";
    private static final int DBversion = 1;
    //-----------------
    private String sqlctl;
    private String tid;
    private int rowsAffected;
    private int old_index;
    private String s_id;
    private String ser_msg;
    private int servermsgcolor;
    private TextView b_servermsg;
    // ----------定時更新--------------------------------
    private Long startTime;
    private Handler handler = new Handler();

    int autotime = 600;// 要幾秒的時間 更新匯入MySQL資料
    //------------------------------
    SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    private TextView nowtime;  //顯示更新時間及次數
    int update_time = 0;
    private String str;
    private Uri uri;
    private Intent its;
    private DatePicker b_dateP;
    // --------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        enableStrictMode(this); //使用暫存Thread 需要用此方法
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fridge);
        setupViewComponent();
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

        tvTitle = (TextView) findViewById(R.id.tvIdTitle);
        b_id = (EditText) findViewById(R.id.edid);
        b_name = (EditText) findViewById(R.id.edtName);
        b_date = (EditText) findViewById(R.id.edtDate);
        b_dateP = (DatePicker)findViewById(R.id.edtDateP);
        count_t = (TextView) findViewById(R.id.count_t);

        btNext = (Button) findViewById(R.id.btIdNext);
        btPrev = (Button) findViewById(R.id.btIdPrev);
        btTop = (Button) findViewById(R.id.btIdtop);
        btEnd = (Button) findViewById(R.id.btIdend);

        btEdit = (Button) findViewById(R.id.btldupdate);
        btDel = (Button) findViewById(R.id.btIdel);

        //-----------------------
        btAdd = (Button) findViewById(R.id.btnAdd);
        btAbandon = (Button) findViewById(R.id.btnabandon);
        btquery = (Button) findViewById(R.id.btnquery);
        btcancel = (Button) findViewById(R.id.btidcancel);
        btreport = (Button) findViewById(R.id.btnlist);

        blinear01 = (RelativeLayout) findViewById(R.id.relative01);
        blinear02 = (LinearLayout) findViewById(R.id.linear02);

        listView = (ListView) findViewById(R.id.listView);
        bsubTitle = (TextView) findViewById(R.id.subTitle);

        b_servermsg= (TextView)findViewById(R.id.tserver_msg);

        //---------設定layout 顯示---------------
        u_layout_def();
        //-----------------------------------------

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

        handler.postDelayed(updateTimer, 1000);  // 設定Delay的時間
        //===================================
        java.sql.Date curDate = new java.sql.Date(System.currentTimeMillis()); //  獲取當前時間
        str = formatter.format(curDate);
        nowtime.setText(getString(R.string.now_time) + str);
        // ----------------------------------
        initDB();
        showRec(index);
        u_setspinner();
        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Navy));
        tvTitle.setText("顯示資料： 共" + tcount + " 筆");
        //b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
        // -------------------------
        mSpnName.setOnItemSelectedListener(mSpnNameOnItemSelLis);

        //==========================================

    }
    //---------------------------------------------------------------------------------
    private Runnable updateTimer = new Runnable() {
        @Override
        public void run() {
            old_index = mSpnName.getSelectedItemPosition();
            Long spentTime = System.currentTimeMillis() - startTime;
            String hours = String.format("%02d", (spentTime / 1000) / 60 / 60);  // 計算目前已過分鐘數
            String minius = String.format("%02d", ((spentTime / 1000) / 60) % 60);  // 計算目前已過分鐘數
            String seconds = String.format("%02d", (spentTime / 1000) % 60);          // 計算目前已過秒數
            handler.postDelayed(this, autotime * 3000); // 真正延遲的時間
            // -------執行匯入MySQL
            dbmysql();
            recSet = dbHper.getRecSet_fri();  //重新載入SQLite
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

    private void u_setspinner() { //SPINNER更新 簡單重構
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

    private void u_layout_def() {
        btAdd.setVisibility(View.INVISIBLE);
        btAbandon.setVisibility(View.INVISIBLE);
        btquery.setVisibility(View.INVISIBLE);
        btEdit.setVisibility(View.VISIBLE);
        btDel.setVisibility(View.VISIBLE);

        blinear01.setVisibility(View.VISIBLE);
        blinear02.setVisibility(View.INVISIBLE);
        btreport.setVisibility(View.INVISIBLE);

        b_id.setEnabled(false);
        //-----------------------
    }


    private final Spinner.OnItemSelectedListener mSpnNameOnItemSelLis = new Spinner.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView parent, View view, int position,long id) {
            int iSelect = mSpnName.getSelectedItemPosition(); //找到按何項
            String[] fld = recSet.get(iSelect).split("#");
            String s = "資料：共" + recSet.size() + " 筆," + "你按下  " + String.valueOf(iSelect + 1) + "項"; //起始為0
            tvTitle.setText(s);
            //b_id .setTextColor(ContextCompat.getColor(parent.getContext(), R.color.Red));
            b_id .setText(fld[0]);
            b_name .setText(fld[1]);
            b_date .setText(fld[2]);
            //-------目前所選的item---
            index = iSelect;
            //-------------------------------
            // -----新增完清空白在此---------------
            if(btAdd.getVisibility() == View.VISIBLE){
                b_name.setHint("請繼續輸入");
                b_id.setText("");
                b_name.setText("");
                b_date.setText("");
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            b_id.setText("");
            b_name.setText("");
            b_date.setText("");
//            b_edid.setText("");
//            e001.setText("");
//            e002.setText("");
//            e003.setText("");
        }
    };

    private void initDB() {
        if (dbHper == null)
            dbHper = new DbHelper(this, DB_FILE, null, DBversion);
        dbmysql();
        recSet = dbHper.getRecSet_fri();
    }


    private void showRec(int index) {
        msg = "";
        if (recSet.size() != 0) {
            String stHead = "顯示資料：第 " + (index + 1) + " 筆 / 共 " + recSet.size() + " 筆";
            msg = getString(R.string.count_t) + recSet.size() + "筆";
            tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.Teal));
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Yellow));
            tvTitle.setText(stHead);
            String[] fld = recSet.get(index).split("#");
            //b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
            //b_id.setBackgroundColor(ContextCompat.getColor(this, R.color.Yellow));
            b_id.setText(fld[0]);
            b_name.setText(fld[1]);
            b_date.setText(fld[2]);
            mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
        } else {
            String stHead = "顯示資料：0 筆";
            msg = getString(R.string.count_t) + "0筆";
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Blue));
            tvTitle.setText(stHead);
            b_id.setText("");
            b_name.setText("");
            b_date.setText("");
        }
        count_t.setText(msg);
    }

    @Override
    public void onClick(View v) {
        int rowsAffected;
        Uri uri;
        String s_id;
        String whereClause;
        String[] selectionArgs;

        count_t.setText("共計:" + Integer.toString(dbHper.RecCount_fri()) + "筆");
        switch (v.getId()){
            case R.id.btIdtop:
                ctlFirst();
                break;
            case R.id.btIdNext:
                ctlNext();
                break;
            case R.id.btIdPrev:
                ctlPrev();
                break;
            case R.id.btIdend:
                ctlLast();
                break;
            //------------------------------------
            case R.id.btldupdate:
                //資料更新
                tid = b_id.getText().toString().trim();
                tname = b_name.getText().toString().trim();
                b_date.setText(b_dateP.getYear() + "/"+
                        (b_dateP.getMonth()+1) + "/"+
                        b_dateP.getDayOfMonth());
                tdate = b_date.getText().toString().trim();

                old_index=index;
                //String old_id = b_id.getText().toString();
                mysql_update(); // 執行MySQL更新
                dbmysql();
                //-------------------------------------
                recSet = dbHper.getRecSet_fri();
                u_setspinner();
//------------------------
//                用 old_id 搜尋 index
//--------------------------------
                index=old_index;   //這是陽春型
                showRec(index);
                msg = "第 " + (index + 1) + " 筆記錄  已修改 ! " ;
//                ctlLast();
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
                break;
            //----------------------------------
            case R.id.btIdel:
                // 刪除一筆
                Cook_MyAlertDialog aldDial = new Cook_MyAlertDialog(Fridge.this);
                aldDial.setTitle("刪除資料");
                aldDial.setMessage("資料刪除無法復原\n確定將資料刪除嗎?");
                aldDial.setIcon(android.R.drawable.ic_dialog_info);
                aldDial.setCancelable(false); //返回鍵關閉
                aldDial.setButton(BUTTON_POSITIVE, "確定刪除", aldBtListenerone);
                aldDial.setButton(BUTTON_NEGATIVE, "取消刪除", aldBtListenerone);
                aldDial.show();
                break;
            //----------------------------------
            case R.id.btnAdd: //按下新增鈕
                // 查詢name是否有有此筆資料
                tname = b_name.getText().toString().trim();
                b_date.setText(b_dateP.getYear() + "/"+
                        (b_dateP.getMonth()+1) + "/"+
                        b_dateP.getDayOfMonth());
                tdate = b_date.getText().toString().trim();

                if (tname.equals("") || tdate.equals("")  ) {
                    Toast.makeText(getApplicationContext(), "資料空白無法新增 !", Toast.LENGTH_SHORT).show();
                    return;
                }
                //------直接增加到MySQL------------
                mysql_insert();
                dbmysql();
                //----------------------------------------
                msg = null;
                // -------------------------
                ContentValues newRow = new ContentValues();
                newRow.put("name", tname);
                newRow.put("dates", tdate);
                //------------------------------
                long rowID = dbHper.insertRec_m_fri(newRow);
                if (rowID != -1) {
                    b_id.setHint("請繼續輸入");
                    b_name.setText("");
                    b_date.setText("");
                    msg = "新增記錄  成功 ! \n" + "目前資料表共有 " + dbHper.RecCount_fri() + " 筆記錄 !";
                    ctlLast();  //成功跳到最後一筆
                } else {
                    msg = "新增記錄  失敗 !";
                }
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                count_t.setText("共計:" + Integer.toString(dbHper.RecCount_fri()) + "筆");
                u_insert();
                dbmysql(); //重新匯入
                ctlLast();  //成功跳到最後一筆
                break;
            //------------------------------------
            case R.id.btnabandon: //按下放棄鈕
                mSpnName.setEnabled(true);
                Toast.makeText(getApplicationContext(), "*放棄新增*", Toast.LENGTH_SHORT).show();
                initDB();
                u_setspinner();
                u_layout_def();
                ctlLast();  //成功跳到最後一筆
                break;

            case R.id.btnquery: //按下查詢鈕
                tname = b_name.getText().toString().trim();
                b_date.setText(b_dateP.getYear() + "/"+
                        (b_dateP.getMonth()+1) + "/"+
                        b_dateP.getDayOfMonth());
                tdate = b_date.getText().toString().trim();
                msg = null;
                recSet = dbHper.getRecSet_query_fri(tname, tdate);
                Toast.makeText(getApplicationContext(), "顯示資料： 共 " + recSet.size() + " 筆", Toast.LENGTH_SHORT).show();
                u_setspinner();
                break;

            case R.id.btnlist: //按下列表鈕
                tname = b_name.getText().toString().trim();
                tdate = b_date.getText().toString().trim();
                msg = null;
                recSet = dbHper.getRecSet_query_fri(tname, tdate);
                Toast.makeText(getApplicationContext(), "顯示資料： 共 " + recSet.size() + " 筆", Toast.LENGTH_SHORT).show();
//                bsubTitle.setText("顯示資料： 共 " + recSet.size() + " 筆");
//===========取SQLite 資料=============
                List<Map<String, Object>> mList;
                mList = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < recSet.size(); i++) {
                    Map<String, Object> item = new HashMap<String, Object>();
                    String[] fld = recSet.get(i).split("#");
                    //item.put("imgView", R.drawable.userconfig);
                    item.put("txtView", "編號:" + fld[0] + "\n品名:" + fld[1] + "\n有效期限:" + fld[2] );
                    mList.add(item);
                }
//=========設定listview========
                blinear01.setVisibility(View.INVISIBLE);
                blinear02.setVisibility(View.VISIBLE);
                SimpleAdapter adapter = new SimpleAdapter(
                        this,
                        mList,
                        R.layout.fri_list_item,
                        new String[]{ "txtView"},
                        new int[]{ R.id.txtView}
                );
                listView.setAdapter(adapter);
                listView.setTextFilterEnabled(true);
                listView.setOnItemClickListener(listviewOnItemClkLis);
                break;
            //------------------------------------
            case R.id.btidcancel:
//                blinear01.setVisibility(View.VISIBLE);
//                blinear02.setVisibility(View.INVISIBLE);
                u_layout_def();
                u_setspinner();
//                setupViewComponent();
                break;
        }


    }

    private void mysql_update() {
        s_id = b_id.getText().toString().trim();
        tname = b_name.getText().toString().trim();
        tdate = b_date.getText().toString().trim();
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(s_id);
        nameValuePairs.add(tname);
        nameValuePairs.add(tdate);
        try {
            Thread.sleep(100); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = Cook_DBConnector.executeUpdate_fri( nameValuePairs);
//-----------------------------------------------
    }

    private void mysql_insert() {
        //        sqlctl = "SELECT * FROM fridge ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
//        nameValuePairs.add(sqlctl);
        nameValuePairs.add(tname);
        nameValuePairs.add(tdate);
        try {
            Thread.sleep(500); //  延遲Thread 睡眠0.5秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//-----------------------------------------------
        String result = Cook_DBConnector.executeInsert_fri(nameValuePairs);  //真正執行新增
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



    DialogInterface.OnClickListener aldBtListenerone = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE: //刪除資料
                    //                    int rowsAffected = dbHper.clearRec_fri();  //刪除全部資料
//                tid = b_id.getText().toString().trim();
                    old_index=index;
                    // ---------------------------
                    mysql_del();// 執行MySQL刪除
                    dbmysql();
                    // ---------------------------
                    index=old_index;
                    u_setspinner();
                    if (index == dbHper.RecCount_fri()) {
                        index--;
                    }
                    showRec(index);
//                    mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
//                }
                    msg = "資料已刪除" ;
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
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
        String result = Cook_DBConnector.executeDelet_fri(nameValuePairs);   //執行刪除
//-----------------------------------------------
    }

    private DialogInterface.OnClickListener aldBtListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:
                    int rowsAffected = dbHper.clearRec_fri();   //--- 刪除所有資料
                    msg = "資料表已空 !共刪除" + rowsAffected + " 筆";
                    break;
                case BUTTON_NEGATIVE:
                    msg = "放棄刪除所有資料 !";
                    break;
            }
            Toast.makeText(Fridge.this, msg, Toast.LENGTH_SHORT).show();
            index = 0;  //給index初始值
            setupViewComponent();
            showRec(index); //重構
        };


    };
    private void ctlFirst() {
        // 第一筆
        index = 0;
        showRec(index);
    }

    private void ctlPrev() {
        //上一筆
        index--;
        if (index<0)  index=recSet.size()-1;
        showRec(index);

    }

    private void ctlNext() {
        //下一筆
        index++;
        if (index >= recSet.size())     index=0;
        showRec(index);

    }

    private void ctlLast() {
        //最後一筆
        index = recSet.size()-1;
        showRec(index);

    }

    //--------------------------------------

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()){   //手機移動的方法
//            case MotionEvent.ACTION_DOWN:  //按下
//                x1 = event.getX(); //觸控按下的X軸位置
//                y1 = event.getY(); //觸控按下的Y軸位置
//                break;
//            case MotionEvent.ACTION_UP:
//                x2 = event.getX(); //觸控放開的X軸位置
//                y2 = event.getY(); //觸控放開的Y軸位置
//                // 判斷左右的方法，因為屏幕的左上角是：0，0 點右下角是max,max
//                // 並且移動距離需大於 > range
//                float xbar = Math.abs(x2 - x1);
//                float ybar = Math.abs(y2 - y1);
//                double z = Math.sqrt(xbar * xbar + ybar * ybar);
//                int angle = Math.round((float) (Math.asin(ybar / z) / Math.PI * 180));// 角度
//                if (x1 != 0 && y1 != 0) {
//                    if (x1 - x2 > range) { // 向左滑動
//                        ctlPrev();
//                    }
//                    if (x2 - x1 > range) { // 向右滑動
//                        ctlNext();
//                        // t001.setText("向右滑動\n" + "滑動參值x1=" + x1 + " x2=" + x2 + "
//                        // r=" + (x2 - x1)+"\n"+"ang="+angle);
//                    }
//                    if (y2 - y1 > range && angle > ran) { // 向下滑動
//                        // 往下角度需大於50
//                        // 最後一筆
//                        ctlLast();
//                    }
//                    if (y1 - y2 > range && angle > ran) { // 向上滑動
//                        // 往上角度需大於50
//                        ctlFirst();// 第一筆
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:  //拖曳
//
//                break;
//        }
//        return super.onTouchEvent(event);
//    }



//--------------------生命週期------------------------
@Override
protected void onDestroy() {
    super.onDestroy();
    handler.removeCallbacks(updateTimer);
}

    @Override
    protected void onPause() {
        super.onPause();
        if (dbHper != null) {
            dbHper.close();
            dbHper = null;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (dbHper == null)
            dbHper = new DbHelper(this, DB_FILE, null, DBversion);
//        setupViewComponent();//onCreate(null); // 重構

    }
    @Override
    protected void onStop() {
        super.onStop();
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    //--------------------------------------------

    private void u_insert() {
        btAdd.setVisibility(View.VISIBLE);
        btAbandon.setVisibility(View.VISIBLE);
        btEdit.setVisibility(View.INVISIBLE);
        btDel.setVisibility(View.INVISIBLE);
        b_id.setEnabled(false);
        b_id.setText("");
        b_name.setText("");
        b_date.setText("");
    }

    // 讀取MySQL 資料
    private void dbmysql() {
        sqlctl = "SELECT * FROM fridge ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = Cook_DBConnector.executeQuery_fri(nameValuePairs);
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
                int rowsAffected = dbHper.clearRec_fri();                 // 匯入前,刪除所有SQLite資料
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


                    // -------------------加入SQLite---------------------------------------
                    long rowID = dbHper.insertRec_m_fri(newRow);
                    //Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();
                }
                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            recSet = dbHper.getRecSet_fri();  //重新載入SQLite
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fridge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent it = new Intent();
        switch (item.getItemId()) {
            case R.id.m_add://新增
                u_insert();
                break;
//            case R.id.m_query://查詢
//                btAdd.setVisibility(View.INVISIBLE);
//                btAbandon.setVisibility(View.VISIBLE);
//                btEdit.setVisibility(View.INVISIBLE);
//                btDel.setVisibility(View.INVISIBLE);
//                btquery.setVisibility(View.VISIBLE);
//                b_id.setEnabled(false);
//                b_id.setText("");
//                b_name.setText("");
//                b_date.setText(" ");
//                b_id.setHint("(欄位未輸入時,表示皆可)");
//                break;

            case R.id.m_list://列表
                btAdd.setVisibility(View.INVISIBLE);
                btAbandon.setVisibility(View.VISIBLE);
                btEdit.setVisibility(View.INVISIBLE);
                btDel.setVisibility(View.INVISIBLE);
                btquery.setVisibility(View.INVISIBLE);
                btreport.setVisibility(View.VISIBLE);

                blinear01.setVisibility(View.VISIBLE);
                blinear02.setVisibility(View.INVISIBLE);
                b_id.setEnabled(false);
                b_id.setText("");
                b_name.setText("");
                b_date.setText("");
                b_id.setHint("請輸入");
                break;

            case R.id.m_mysql:
                // 匯入MySQL
                dbmysql();
                break;

            case R.id.menu_fb:
                uri = Uri.parse("https://www.facebook.com/kai.hao.9");
                its = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(its);
                break;

            case R.id.menu_notify:
                new AlertDialog.Builder(this)
                        .setTitle(R.string.menu_notify)
                        .setMessage(getString(R.string.menu_message))
                        .setCancelable(false)
                        //.setIcon(R.drawable.iu06)
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
                        .setMessage(getString(R.string.menu_member_message) + "\n" + "維尼、大神、佳佳、波波、柏榕、老大、培揚")
                        .setCancelable(false)
                        //.setIcon(R.drawable.iu06)
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

            case R.id.action_settings:
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    // ---------------------------------------------



}//-----------END