package tw.tcnr.fridge;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class Copen01 extends ListActivity {

    private DbHelper dbHper;
    //private DBConnector dbConnector;
    private static final String DB_FILE = "cook_friends.db";
    private static final String DB_TABLE = "recipe";
    private static final int DBversion = 1;
    private TextView tvTitle;

    private EditText b_id, b_title, b_recipe_text;
    String t_title, t_recipe_text;
    String ttitle, trecipe_text;
    String msg = null;

    private List<Map<String, Object>> mList;
    private ArrayList<String> recSet;
    private RelativeLayout blinear01;
    private LinearLayout blinear02;
    String TAG = "tcnr06=>";
    private String sqlctl;
    private int index = 0;

    protected static final int BUTTON_POSITIVE = -1;
    protected static final int BUTTON_NEGATIVE = -2;
    private String s_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recipe_list);
        setupViewComponent();
        initDB();
    }

    private void setupViewComponent() {

//        initDB();
//        tvTitle = (TextView) findViewById(R.id.tvIdTitle);
//        tvTitle.setTextColor(ContextCompat.getColor(this, R.color.White));
//        tvTitle.setBackgroundResource(R.color.FridgeDark);
//        tvTitle.setText("顯示資料： 共 " + recSet.size() + " 筆");
//
////        b_title = (EditText) findViewById(R.id.et_title);
////        b_recipe_text = (EditText) findViewById(R.id.et_content);
////
////        t_title = b_title.getText().toString().trim();
////        t_recipe_text = b_recipe_text.getText().toString().trim();
////        msg = null;
////        recSet = dbHper.getRecSet_query(t_title, t_recipe_text);
////        Toast.makeText(getApplicationContext(), "顯示資料： 共 " + recSet.size() + " 筆", Toast.LENGTH_SHORT).show();
//
////        blinear01 = (RelativeLayout) findViewById(R.id.rel3);
////        blinear02 = (LinearLayout) findViewById(R.id.linear02);
////        recSet = dbHper.getRecSet_query(t_title,t_recipe_text);
//        //recSet = dbHper.getRecSet_query();//連sqlite
//        recSet = dbHper.getRecSet_query(ttitle,trecipe_text);//連sqlite
//        //Toast.makeText(getApplicationContext(), "顯示資料： 共 " + recSet.size() + " 筆", Toast.LENGTH_SHORT).show();
//
//        //===========取SQLite 資料=============
//        List<Map<String, Object>> mList;
//        mList = new ArrayList<Map<String, Object>>();
//        for (int i = 0; i < recSet.size(); i++) {
//            Map<String, Object> item = new HashMap<String, Object>();
//            String[] fld = recSet.get(i).split("#");
//            item.put("imgView", R.drawable.userconfig);//要用url
//            item.put("txtView", fld[1] + "\n"
//                    +" 點擊食譜查看更多 "+fld[0]);
//            mList.add(item);
//        }
//
//        //=========設定listview========
//
//        SimpleAdapter adapter = new SimpleAdapter(this,
//                mList, R.layout.list_item,
//                new String[]{"imgView","txtView"},//代表layout中的欄位
//                new int[]{R.id.imgView, R.id.txtView} );
//        setListAdapter(adapter);
//
//        //----------------------------------
//        ListView listview = getListView();
//        listview.setTextFilterEnabled(true);
//        listview.setOnItemClickListener(listviewOnItemClkLis);
    }
    private void initDB() {
        if(dbHper==null){
            dbHper = new DbHelper(this, DB_FILE, null, DBversion);
            recSet=dbHper.getRecSet_Cook();
        }
    }
    // 讀取MySQL 資料
    private void dbmysql() {
        sqlctl = "SELECT * FROM recipe ORDER BY id ASC";
        ArrayList<String> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(sqlctl);
        try {
            String result = Cook_DBConnector.executeQuery_Cook(nameValuePairs);
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
                    long rowID = dbHper.insertRec_Cookm(newRow);
                    //Toast.makeText(getApplicationContext(), "共匯入 " + Integer.toString(jsonArray.length()) + " 筆資料", Toast.LENGTH_SHORT).show();
                }
                // ---------------------------
            } else {
                Toast.makeText(getApplicationContext(), "主機資料庫無資料", Toast.LENGTH_LONG).show();
            }
            recSet = dbHper.getRecSet_Cook();  //重新載入SQLite
            //u_setspinner();
            // --------------------------------------------------------
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }

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

    private void showRec(int index) {

        msg = "";
        if (recSet.size() != 0) {
            String stHead = "顯示資料：第 " + (index + 1) + " 筆 / 共 " + recSet.size() + " 筆";
            msg = getString(R.string.count_t) + recSet.size() + "筆";
            tvTitle.setBackgroundColor(ContextCompat.getColor(this, R.color.Teal));
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Yellow));
            tvTitle.setText(stHead);

            String[] fld = recSet.get(index).split("#");
            b_id.setTextColor(ContextCompat.getColor(this, R.color.Red));
            b_id.setBackgroundColor(ContextCompat.getColor(this, R.color.Yellow));
            b_id.setText(fld[0]);
            b_title.setText(fld[1]);
            b_recipe_text.setText(fld[2]);

            //mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
        } else {
            String stHead = "顯示資料：0 筆";
            msg = getString(R.string.count_t) + "0筆";
            tvTitle.setTextColor(ContextCompat.getColor(this, R.color.Blue));
            tvTitle.setText(stHead);
            b_id.setText("");
            b_title.setText("");
            b_recipe_text.setText("");

        }

        //count_t.setText(msg);


    }



    private ListView.OnItemClickListener listviewOnItemClkLis = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            String s = "你按下第 "  + Integer.toString(position +1)   + "筆\n"
//                    + ((TextView) view.findViewById(R.id.txtView)).getText()   .toString();
            //tvTitle.setText(s);

            String s = "你按下第 "  + Integer.toString(position +1)   + "筆";
            Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

            // 刪除資料 --使用對話盒
            Cook_MyAlertDialog myAltDlg = new Cook_MyAlertDialog(Copen01.this);
            myAltDlg.getWindow().setBackgroundDrawableResource(R.color.Yellow);
            myAltDlg.setTitle("刪除資料");
            myAltDlg.setMessage("資料刪除無法復原\n確定將第"+ Integer.toString(position +1)   +"筆資料刪除嗎?");
            myAltDlg.setCancelable(false);
            myAltDlg.setIcon(android.R.drawable.ic_delete);
            myAltDlg.setButton(DialogInterface.BUTTON_POSITIVE, "確定刪除", aldBtListener);
            myAltDlg.setButton(DialogInterface.BUTTON_NEGATIVE, "取消刪除", aldBtListener);
            myAltDlg.show();
        }


    };


    private DialogInterface.OnClickListener aldBtListener = new DialogInterface.OnClickListener() {

        private int old_index;

        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case BUTTON_POSITIVE:

                    old_index=index;
                    // ---------------------------
                    mysql_del();// 執行MySQL刪除
                    dbmysql();
                    // ---------------------------
                    index=old_index;
                    //u_setspinner();
                    if (index == dbHper.RecCount_Cook()) {
                        index--;
                    }
                    showRec(index);
//                    mSpnName.setSelection(index, true); //spinner 小窗跳到第幾筆
//                }
                    msg = "資料已刪除！" ;
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                    break;

                case BUTTON_NEGATIVE:
                    msg = "放棄刪除資料！";
                    Toast.makeText(Copen01.this, msg, Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };






//--------------------生命週期------------------------
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
    }
    @Override
    protected void onStop() {
        super.onStop();
    }


    //--------------------------------------------
}//------------END