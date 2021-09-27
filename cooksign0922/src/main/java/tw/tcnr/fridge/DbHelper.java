package tw.tcnr.fridge;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

////----------------------------------------------------------
//建構式參數說明：
//context   可以操作資料庫的內容本文，一般可直接傳入Activity物件。
//name  要操作資料庫名稱，如果資料庫不存在，會自動被建立出來並呼叫onCreate()方法。
//factory  用來做深入查詢用，入門時用不到。
//version  版本號碼。
////-----------------------
public class DbHelper extends SQLiteOpenHelper {

    String TAG = "tcnr06=>";
    public String sCreateTableCommand;    // 資料庫名稱
    private static final String DB_FILE = "Fridge.db";//----------->共用資料庫名稱
//    DB_FILE ="cook_friends.db"

    //================個人資料表TABLE名稱(資料庫物件，固定的欄位變數)================
    private static final String DB_TABLE_Cook= "recipe";    // 維尼TABLE
    private static final String DB_TABLE_login= "login";    // loginTABLE
    private static final String DB_TABLE_Shoplist01= "shoplist01";
    private static final String DB_TABLE_Shoplist02= "shoplist02";  // 晨霖TABLE
    //    private static final String DB_TABLE_XXX = "XXXXX";    // 柏昇TABLE
    private static final String DB_TABLE_RandomMeal = "RandomMeal";   // 柏榕TABLE
    private static final String DB_TABLEexp = "fridge";    // 培揚+柏昇 共用TABLE


    private static SQLiteDatabase database;//資料庫物件，固定的欄位變數(共用，勿碰)

    //================個人資料表TABLE名稱================================

    //------------------------------
    // 資料庫版本，資料結構改變的時候要更改這個數字，通常是加一
    public static final int VERSION = 1;    // 資料表名稱
    //------------------------------

//================建立資料表的語法================

    //---------維尼的語法-----------
    private static final String crTBsql_cook = "CREATE TABLE " + DB_TABLE_Cook + " ( "
            + "id INTEGER PRIMARY KEY," + "title TEXT NOT NULL," + "recipe_text TEXT);";


    //---------login的語法-----------
    private static final String crTBsql_login = "CREATE TABLE " + DB_TABLE_login + " ( "
            + "id INTEGER PRIMARY KEY," + "user_id TEXT NOT NULL);";
    //---------晨霖的語法-----------
    private static final String crTBsql_Shoplist01 = "CREATE TABLE " + DB_TABLE_Shoplist01 + " ( "
            + "id INTEGER PRIMARY KEY," + "shop_title TEXT NOT NULL," + "shoplist_contain TEXT);";

    private static final String crTBsql_Shoplist02 = "CREATE TABLE " + DB_TABLE_Shoplist02 + " ( "
            + "id INTEGER PRIMARY KEY," + "titleID INTEGER NOT NULL," + "shoplist_item TEXT);";
    //---------柏昇的語法-----------
//    private static final String XXXTBsql = "CREATE TABLE " + XXX_DB_TABLE + " ( "
//            + "id INTEGER PRIMARY KEY," + "title TEXT NOT NULL," + "recipe_text TEXT);";
    //---------柏榕的語法-----------
    private static final String TBsql_RandomMeal = "CREATE TABLE " + DB_TABLE_RandomMeal + " ( "
            + "id INTEGER PRIMARY KEY," + "name TEXT NOT NULL," + "content TEXT,"
            + "note TEXT);";
    //---------培揚+柏昇的語法-----------
    private static final String crTBsqlexp = "CREATE TABLE " + DB_TABLEexp + " ( "
            + "id INTEGER PRIMARY KEY," + "name TEXT NOT NULL," + "dates TEXT);";

    //================建立資料表的語法================


    //----------------------------------------------
    // 需要資料庫的元件呼叫這個方法，這個方法在一般的應用都不需要修改
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new DbHelper(context, DB_FILE, null, VERSION).getWritableDatabase();
        }
        return database;
    }
    //------------------ SQLiteDatabase 共用，勿動-----------------

    public DbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
//        super(context, name, factory, version);
        super(context, "Fridge.db", null, 1);
        sCreateTableCommand = "";
    }
    //------------------ DbHelper 共用，勿動-----------------

    // ===========執行 新增 DB TABLE 命令(個人有個人的方法，請勿動到他人語法)===========
    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(crTBsql_cook);//維尼的
        db.execSQL(crTBsql_login);//user的
        db.execSQL(crTBsql_Shoplist01);
        db.execSQL(crTBsql_Shoplist02);//晨霖的
        //        db.execSQL(XXXTBsql);//柏昇的
        db.execSQL(TBsql_RandomMeal);//柏榕的
        db.execSQL(crTBsqlexp);//培揚+柏昇的
    }
    // ===========執行 新增 DB TABLE 命令(個人有個人的方法，請勿動到他人語法)===========

    //------------------ 共用，勿動-----------------
    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }
    //------------------ 共用，勿動-----------------

    // ===========執行 新增 DB TABLE 更新命令(個人有個人的方法，請勿動到他人語法)===========
    //----若有日後要新增，須把版本改掉，VERSION = n，之後程式會再重新檢查
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade()");//共用勿動

        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_Cook);//維尼
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_login);//user
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_Shoplist01);
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_Shoplist02);//晨霖
//        db.execSQL("DROP TABLE IF EXISTS " + XXX_DB_TABLE);//柏昇
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_RandomMeal);//柏榕
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLEexp);//培揚+柏昇

        onCreate(db);//共用勿動
    }

    //===========現在開始為維尼的區塊，勿動===========
    //===========維尼的新增資料===========
    public long insertRec(String b_title, String b_recipe_text) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("title", b_title);
        rec.put("recipe_text", b_recipe_text);

        long rowID = db.insert(DB_TABLE_Cook, null, rec);
        db.close();
        return rowID;
    }

    //===========維尼的新增資料===========
    //    ContentValues values
    public long insertRec_Cookm(ContentValues newRow) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_Cook, null, newRow);
        db.close();
        return rowID;
    }

    public int RecCount_Cook() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Cook;
        Cursor recSet = db.rawQuery(sql, null);
        int count = recSet.getCount();
        recSet.close();
        return count;
    }

    public ArrayList<String> getRecSet_Cook() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Cook;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
//-------------------------

    public int clearRec_Cook() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Cook;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = db.delete(DB_TABLE_Cook, "1", null);
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }


    public int updateRec_Cook(String b_id, String b_title, String b_recipe_text) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Cook;
        Cursor recSet = db.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
            ContentValues rec = new ContentValues();
//			rec.put("id", b_id);
            rec.put("title", b_title);
            rec.put("recipe_text", b_recipe_text);


            String whereClause = "id='" + b_id + "'";

            int rowsAffected = db.update(DB_TABLE_Cook, rec, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public int deleteRec_Cook(String b_id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Cook;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
            String whereClause = "id='" + b_id + "'";
            int rowsAffected = db.delete(DB_TABLE_Cook, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public ArrayList<String> getRecSet_query_Cook(String ttitle, String trecipe_text) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + DB_TABLE_Cook +
                " WHERE title LIKE ? AND recipe_text LIKE ? ORDER BY id ASC";
        String[] args = new String[]{"%%",
                "%%"};

        Cursor recSet = db.rawQuery(sql, args);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }


    //===========維尼的區塊結束，勿動===========


    //===========現在開始為user的區塊，勿動===========
    //    ContentValues values
    public long insertRec_login(ContentValues newRow) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_login, null, newRow);
        db.close();
        return rowID;
    }

    public int RecCount_login() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_login;
        Cursor recSet = db.rawQuery(sql, null);
        int count = recSet.getCount();
        recSet.close();
        return count;
    }

    public ArrayList<String> getRecSet_login() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_login;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
//-------------------------

    public int clearRec_login() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_login;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = db.delete(DB_TABLE_login, "1", null);
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }


//    public int updateRec_login(String b_id, String b_title, String b_recipe_text) {
//        SQLiteDatabase db = getWritableDatabase();
//        String sql = "SELECT * FROM " + DB_TABLE_login;
//        Cursor recSet = db.rawQuery(sql, null);
//
//        if (recSet.getCount() != 0) {
//            ContentValues rec = new ContentValues();
////			rec.put("id", b_id);
//            rec.put("title", b_title);
//            rec.put("recipe_text", b_recipe_text);
//
//
//            String whereClause = "id='" + b_id + "'";
//
//            int rowsAffected = db.update(DB_TABLE_login, rec, whereClause, null);
//            recSet.close();
//            db.close();
//            return rowsAffected;
//        } else {
//            recSet.close();
//            db.close();
//            return -1;
//        }
//    }

//    public int deleteRec_login(String b_id) {
//        SQLiteDatabase db = getWritableDatabase();
//        String sql = "SELECT * FROM " + DB_TABLE_login;
//        Cursor recSet = db.rawQuery(sql, null);
//        if (recSet.getCount() != 0) {
//            String whereClause = "id='" + b_id + "'";
//            int rowsAffected = db.delete(DB_TABLE_login, whereClause, null);
//            recSet.close();
//            db.close();
//            return rowsAffected;
//        } else {
//            recSet.close();
//            db.close();
//            return -1;
//        }
//    }
//
//    public ArrayList<String> getRecSet_query_login(String ttitle, String trecipe_text) {
//        SQLiteDatabase db = getReadableDatabase();
//
//        String sql = "SELECT * FROM " + DB_TABLE_login +
//                " WHERE title LIKE ? AND recipe_text LIKE ? ORDER BY id ASC";
//        String[] args = new String[]{"%%",
//                "%%"};
//
//        Cursor recSet = db.rawQuery(sql, args);
//        ArrayList<String> recAry = new ArrayList<String>();
//        //----------------------------
//        int columnCount = recSet.getColumnCount();
//        while (recSet.moveToNext()) {
//            String fldSet = "";
//            for (int i = 0; i < columnCount; i++)
//                fldSet += recSet.getString(i) + "#";
//            recAry.add(fldSet);
//        }
//        //------------------------
//        recSet.close();
//        db.close();
//        return recAry;
//    }
    //===========user的區塊結束，勿動===========

    //===========現在開始為Shoplist01的區塊，勿動===========
    //===========Shoplist01的新增資料===========
    public long insertRec_Shoplist01(String b_title, String b_recipe_text) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("shoplist_item", b_title);
        rec.put("shoplist_contain", b_recipe_text);

        long rowID = db.insert(DB_TABLE_Shoplist01, null, rec);
        db.close();
        return rowID;
    }

    //===========維尼的新增資料===========
    //    ContentValues values
    public long insertRec_Shoplist01(ContentValues newRow) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_Shoplist01, null, newRow);
        db.close();
        return rowID;
    }

    public int RecCount_Shoplist01() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist01;
        Cursor recSet = db.rawQuery(sql, null);
        int count = recSet.getCount();
        recSet.close();
        return count;
    }

    public ArrayList<String> getRecSet_Shoplist01() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist01;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        recSet.moveToFirst();
        String fldSet = "";

        if (recSet.getCount() != 0) { // 判斷資料如果 不是0比 才執行抓資料
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#"; // 欄位跟欄位 用 # 做區隔
            recAry.add(fldSet);
        }

        while (recSet.moveToNext()) {
            fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
//-------------------------

    public int clearRec_Shoplist01() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist01;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//   String whereClause = "id < 0";
            int rowsAffected = db.delete(DB_TABLE_Shoplist01, "1", null);
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }


    public int updateRec_Shoplist01(String b_id, String b_title, String b_recipe_text) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist01;
        Cursor recSet = db.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
            ContentValues rec = new ContentValues();
//   rec.put("id", b_id);
            rec.put("shop_title", b_title);
            rec.put("shoplist_contain", b_recipe_text);


            String whereClause = "id='" + b_id + "'";

            int rowsAffected = db.update(DB_TABLE_Shoplist01, rec, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public int deleteRec_Shoplist01(String b_id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist01;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
            String whereClause = "id='" + b_id + "'";
            int rowsAffected = db.delete(DB_TABLE_Shoplist01, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public ArrayList<String> getRecSet_query_Shoplist01(String ttitle, String trecipe_text) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + DB_TABLE_Shoplist01 +
                " WHERE shop_title LIKE ? AND shoplist_contain LIKE ? ORDER BY id ASC";
        String[] args = new String[]{"%%",
                "%%"};

        Cursor recSet = db.rawQuery(sql, args);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }


    //===========Shoplist01的區塊結束，勿動===========

    //===========現在開始為Shoplist02的區塊，勿動===========
    public long insertRec_Shoplist02_m(String b_title,String b_recipe_text) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("titleID", b_title);
        rec.put("shoplist_item", b_recipe_text);


        long rowID = db.insert(DB_TABLE_Shoplist02, null, rec);
        db.close();
        return rowID;
    }

    //===========維尼的新增資料===========
    //    ContentValues values
    public long insertRec_Shoplist02(ContentValues newRow_s02) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_Shoplist02, null, newRow_s02);
        db.close();
        return rowID;
    }

    public int RecCount_Shoplist02() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist02;
        Cursor recSet = db.rawQuery(sql, null);
        int count = recSet.getCount();
        recSet.close();
        return count;
    }

    public ArrayList<String> getRecSet_Shoplist02() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist02;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        recSet.moveToFirst();
        String fldSet = "";

        if (recSet.getCount() != 0) { // 判斷資料如果 不是0比 才執行抓資料
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#"; // 欄位跟欄位 用 # 做區隔
            recAry.add(fldSet);
        }

        while (recSet.moveToNext()) {
            fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
//-------------------------

    public int clearRec_Shoplist02() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist02;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//   String whereClause = "id < 0";
            int rowsAffected = db.delete(DB_TABLE_Shoplist02, "1", null);
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }


    public int updateRec_Shoplist02(String b_id, String b_title,String b_recipe_text) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist02;
        Cursor recSet = db.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
            ContentValues rec = new ContentValues();
//   rec.put("id", b_id);
            rec.put("titleID", b_title);
            rec.put("shoplist_item", b_recipe_text);


            String whereClause = "id='" + b_id + "'";

            int rowsAffected = db.update(DB_TABLE_Shoplist02, rec, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public int deleteRec_Shoplist02(String b_id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_Shoplist02;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
            String whereClause = "id='" + b_id + "'";
            int rowsAffected = db.delete(DB_TABLE_Shoplist02, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public ArrayList<String> getRecSet_query_Shoplist02(String get_titleID) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + DB_TABLE_Shoplist02 +
                " WHERE titleID LIKE ?  ORDER BY id ASC";
        String[] args = new String[]{"%" + get_titleID + "%"};

        Cursor recSet = db.rawQuery(sql, args);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
    //===========Shoplist02的區塊結束，勿動===========


    //===========現在開始為柏昇的區塊，勿動===========
    //    ContentValues values
    public long insertRec_m_fri(ContentValues rec) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLEexp, null, rec);
        db.close();
        return rowID;
    }

    public int RecCount_fri() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLEexp;
        Cursor recSet = db.rawQuery(sql, null);
        int count = recSet.getCount();
        recSet.close();
        return count;
    }

    public ArrayList<String> getRecSet_fri() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLEexp;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
    //
    public int clearRec_fri() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLEexp;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = db.delete(DB_TABLEexp, "1", null);
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }



    public ArrayList<String> getRecSet_query_fri(String tname, String tdate) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + DB_TABLEexp +
                " WHERE name LIKE ? AND dates LIKE ? ORDER BY id ASC";
        String[] args = new String[]{"%" + tname.toString() + "%",
                "%" + tdate.toString() + "%"};

        Cursor recSet = db.rawQuery(sql, args);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
    //===========柏昇的區塊結束，勿動===========

    //===========現在開始為柏榕的區塊，勿動===========
    public long insertRec_ran(String b_name, String b_content, String b_note) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues rec = new ContentValues();
        rec.put("name", b_name);
        rec.put("content", b_content);
        rec.put("note", b_note);
        long rowID = db.insert(DB_TABLE_RandomMeal, null, rec);
        db.close();
        return rowID;
    }

    //    ContentValues values
    public long insertRec_rm(ContentValues rec) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLE_RandomMeal, null, rec);
        db.close();
        return rowID;
    }

    public int RecCount_rm() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_RandomMeal;
        Cursor recSet = db.rawQuery(sql, null);
        int count = recSet.getCount();
        recSet.close();
        return count;
    }

    public ArrayList<String> getRecSet_rm() {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_RandomMeal;
        Cursor recSet = db.rawQuery(sql, null);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        recSet.moveToFirst();
        String fldSet = "";

        if (recSet.getCount() != 0) { // 判斷資料如果 不是0比 才執行抓資料
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#"; // 欄位跟欄位 用 # 做區隔
            recAry.add(fldSet);
        }

        while (recSet.moveToNext()) {
            fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
//

    public int clearRec_rm() {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_RandomMeal;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = db.delete(DB_TABLE_RandomMeal, "1", null);
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }


    public int updateRec_rm(String b_id, String b_name, String b_content, String b_note) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_RandomMeal;
        Cursor recSet = db.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
            ContentValues rec = new ContentValues();
//			rec.put("id", b_id);
            rec.put("name", b_name);
            rec.put("content", b_content);
            rec.put("note", b_note);

            String whereClause = "id='" + b_id + "'";

            int rowsAffected = db.update(DB_TABLE_RandomMeal, rec, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public int deleteRec_rm(String b_id) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLE_RandomMeal;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
            String whereClause = "id='" + b_id + "'";
            int rowsAffected = db.delete(DB_TABLE_RandomMeal, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public ArrayList<String> getRecSet_query_rm(String tname, String tcontent, String tnote) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + DB_TABLE_RandomMeal +
                " WHERE name LIKE ? AND content LIKE ? AND note LIKE ? ORDER BY id ASC";
        String[] args = new String[]{"%" + tname.toString() + "%",
                "%" + tcontent.toString() + "%",
                "%" + tnote.toString() + "%"};

        Cursor recSet = db.rawQuery(sql, args);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        recSet.moveToFirst();
        String fldSet = "";

        if (recSet.getCount() != 0) { // 判斷資料如果 不是0比 才執行抓資料
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#"; // 欄位跟欄位 用 # 做區隔
            recAry.add(fldSet);
        }
        while (recSet.moveToNext()) {
             fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }

    //===========柏榕的區塊結束，勿動===========

    //===========現在開始為培揚的區塊，勿動===========
    public long insertRecex(String exp_name) {
        SQLiteDatabase db = getWritableDatabase();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date =new Date(System.currentTimeMillis());
        String time=simpleDateFormat.format(date);
        ContentValues rec = new ContentValues();
        rec.put("name", exp_name);
        rec.put("dates", time);
        long rowID = db.insert(DB_TABLEexp, null, rec);
        db.close();
        return rowID;
    }

    //    ContentValues values
    public long insertRecexp(ContentValues rec) {
        SQLiteDatabase db = getWritableDatabase();
        long rowID = db.insert(DB_TABLEexp, null, rec);
        db.close();
        return rowID;
    }

    public int RecCountexp() {
        SQLiteDatabase db = getWritableDatabase(); //讀寫的方法打開資料庫
        String sql = "SELECT * FROM " + DB_TABLEexp; //選擇member table
        Cursor recSet = db.rawQuery(sql, null);
        int count = recSet.getCount();
        recSet.close();
        db.close();
        return count;
    }

    public ArrayList<String> getRecSetexp() { //撈取資料
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT * FROM " + DB_TABLEexp;
        Cursor recSet = db.rawQuery(sql, null); //運行提供的SQL
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount(); //返回總列數
        while (recSet.moveToNext()) { //移動到下一行
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
//

    public int clearRecexp() { //清除資料
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLEexp;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
//			String whereClause = "id < 0";
            int rowsAffected = db.delete(DB_TABLEexp, "1", null);
            // From the documentation of SQLiteDatabase delete method:
            // To remove all rows and get a count pass "1" as the whereClause.
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }


    public int updateRecexp(String exp_id, String exp_name, String exp_date) { //更新資料
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLEexp;
        Cursor recSet = db.rawQuery(sql, null);

        if (recSet.getCount() != 0) {
            ContentValues rec = new ContentValues();
//			rec.put("id", b_id);
            rec.put("name", exp_name);
            rec.put("dates", exp_date);

            String whereClause = "id='" + exp_id + "'";

            int rowsAffected = db.update(DB_TABLEexp, rec, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public int deleteRecexp(String exp_id) { //刪除資料
        SQLiteDatabase db = getWritableDatabase();
        String sql = "SELECT * FROM " + DB_TABLEexp;
        Cursor recSet = db.rawQuery(sql, null);
        if (recSet.getCount() != 0) {
            String whereClause = "id='" + exp_id + "'";
            int rowsAffected = db.delete(DB_TABLEexp, whereClause, null);
            recSet.close();
            db.close();
            return rowsAffected;
        } else {
            recSet.close();
            db.close();
            return -1;
        }
    }

    public ArrayList<String> getRecSet_queryexp(String tname, String tgrp) {
        SQLiteDatabase db = getReadableDatabase();

        String sql = "SELECT * FROM " + DB_TABLEexp +
                " WHERE name LIKE ? AND dates LIKE ? ORDER BY id ASC";
        String[] args = new String[]{"%" + tname.toString() + "%",
                "%" + tgrp.toString() + "%"};

        Cursor recSet = db.rawQuery(sql, args);
        ArrayList<String> recAry = new ArrayList<String>();
        //----------------------------
        int columnCount = recSet.getColumnCount();
        while (recSet.moveToNext()) {
            String fldSet = "";
            for (int i = 0; i < columnCount; i++)
                fldSet += recSet.getString(i) + "#";
            recAry.add(fldSet);
        }
        //------------------------
        recSet.close();
        db.close();
        return recAry;
    }
    //===========培揚的區塊結束，勿動===========



}//-----全部程式碼END-----