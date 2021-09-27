package tw.tcnr.fridge;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

public class Cooklist extends AppCompatActivity implements ViewSwitcher.ViewFactory,AdapterView.OnItemClickListener {

    private GridView gridview02,gridview03;

    // 圖庫的圖片資源索引
    private Integer[]thumbImgArr={
            R.drawable.cimg01_0,R.drawable.cimg02_0,R.drawable.cimg03_0,R.drawable.cimg04_0,
            R.drawable.cimg05_0,R.drawable.cimg06_0,R.drawable.cimg07_0,R.drawable.cimg08_0,
            R.drawable.cimg09_0,R.drawable.cimg10_0,R.drawable.cimg11_0,R.drawable.cimg12_0,
    };

    private String[]Text_Arr={"豆腐鮮蝦煲\n1. 首先加鮮蝦去殼、泥腸後與醃料混合醃製約15分鐘後備用。\n2. 將蝦殼用油炒香後加入400ml的水熬煮成蝦湯後備用。\n3. 將雞蛋豆腐下鍋煎至表面金黃後起鍋備用。\n4. 同一油鍋，爆香蔥白末、薑末、蒜末。\n5. 接著加入步驟2&3的蝦高湯與豆腐煨煮約10分鐘；接著加入醃好的鮮蝦與米酒。\n6. 起鍋前加入醬油、鹽、糖、白胡椒調味，最後用芡水勾芡、灑上蔥花就完成了喔。",
            "羊肉炒麵\n1.取一碗加入羊肉、沙茶醬、米酒、白胡椒、蒜泥、玉米粉略醃。\n2.取一鍋下豬油，鍋熱後下羊肉略炒置七分熟。\n3.備一鍋滾水下油麵，煮至麵浮起脹大。\n取原本的炒鍋加入沙茶醬、蒜頭、辣椒、羊肉、薑絲、空心菜梗、油麵、少許水。\n最後加入空心菜葉、依照個人口味加入白胡椒粉、鹽巴、米酒即可完成。",
            "黑胡椒牛柳\n1.將牛肉與醃料均勻混合後備用。\n2.起油鍋，將牛肉下鍋炒至約5分熟後起鍋備用。\n3.同一油鍋，將蒜末與洋蔥下鍋爆香。\n4.接著將牛肉與黑胡椒醬倒入鍋中拌炒。\n最後將紅、黃椒與經蔥下鍋稍微拌炒後就完成了。",
            "滑蛋蝦仁\n1.剝好蝦殼去蝦腸，在蝦背化一刀，如此不僅好入味，也容易扒住蛋汁，蝦仁加入鹽適量、白胡椒適量、米酒少許醃3-5分鐘\n2.熱鍋加少許食用油，將醃好的蝦仁爆炒至8-9分熟\n3.將炒好的蝦仁倒入濾網中，濾掉多的油水\n4.4個蛋汁中加入鹽適量、黑胡椒適量、鮮雞粉、太白粉1大匙(加水混勻)、蔥末混合，將瀝乾的蝦仁與蛋汁混合液一起攪拌一下\n5.鍋中加1大匙橄欖油+1匙奶油熱鍋\n6.鍋熱後將蝦仁蛋液倒入鍋中不要翻動蝦仁蛋液須停止5-10秒，蛋液有一些凝固後\n7.中火的火侯，時間也要快速，鍋鏟由外向內滑動翻炒一下後，停留一下不動(關火)等蛋液扒住蝦仁\n8.金黃香滑的蝦仁滑蛋就完成",
            "鍋燒意麵\n準備食材：\n" +
                    "香菇切細\n" +
                    "小白菜切段\n" +
                    "虱目魚切小塊\n" +
                    "肉絲醃香菇醬油\n" +
                    "打蛋\n-----------\n"+
                    "1.雞高湯加水煮滾，加入香菇、肉絲中火煮5分鐘，沸騰後加入虱目魚片，調味鹽、白胡椒粉、些許香菇醬油\n2.拆除意麵包裝袋，分別放在要食用的碗中或是大一點的鍋子。\n3.在高湯鍋加入切好的小白菜，用大湯勺在鍋內製造一個漩渦，然後順著漩渦倒入蛋液，加入柴魚片，然後關火。\n4.將煮好的食材及高湯加入要食用的碗或是大鍋中，悶2分鐘，讓麵條完全吸收湯汁的鮮味，即可享食。",
            "絲瓜蛤蠣\n1.蛤蜊吐沙後洗淨瀝乾：自來水中加鹽（鹽量要足，大約是海水的鹹度），將蛤蜊放入，蓋上鍋蓋，約2小時，蛤蜊吐沙完成。將蛤蜊洗淨瀝乾水分備用。\n2.絲瓜削皮、切塊（約1~2cm厚片）；薑切成薑絲。\n3.取出平底鍋，倒入少許油，放入絲瓜塊，開中火，拌炒約30秒。\n4.加少許水，蓋上鍋蓋燜煮絲瓜。\n5.絲瓜變軟（代表煮熟了）時，放入吐完沙的蛤蜊，蓋上鍋蓋燜煮。",
            "蔥爆牛肉\n1.首先將牛肉絲與醃料均勻混合備用。\n2.起油鍋先將牛肉絲下鍋炒製5分熟後起鍋備用。\n3.另起油鍋，將薑絲、蒜、蔥白下鍋爆香。\n4.再來將步驟2的牛肉絲與醬油、蠔油倒入鍋中拌炒。\n5.最後將蔥綠、辣椒下鍋拌炒後，從鍋邊嗆入米酒就完成了喔。",
            "金針排骨湯\n1.乾金針以適量的冷水現泡軟，約泡10分鐘，之後再清洗乾淨。\n2.起鍋，加適量的水，放入排骨汆燙去血水雜質，完成再清洗乾淨。\n3.老薑切成片狀。\n4.再起鍋，放入老薑片、排骨，再加水，以大火煮滾後轉小火，熬煮約30-40分鐘（依個人對排骨接受的軟嫩度而定）。",
            "薑絲炒大腸\n1.將豬大腸以麵粉洗淨黏液，冷水下鍋，下薑片少許、蔥，煮約20分鐘，取出洗淨後，切小塊備用。\n2.薑切絲，蒜頭與辣椒切片，酸菜洗淨去除鹹味切絲備用。\n3.下油、薑絲、酸菜心略炒煸香。\n4.下蒜頭、辣椒、大腸、水、黃豆醬、糖、米酒調味。\n5.起鍋前下醋、香油，最後略勾芡即可。",
            "鳳梨蝦球\n1.白蝦剝殼蝦背劃一刀並且去腸泥\n2.酥炸粉加入適量水調和，不要太稀，白蝦裹粉後小火炸\n3.炸好起鍋瀝油，盤子鋪上鳳梨片\n4.鳳梨片舖好再鋪上白蝦，淋上美奶滋，完成",
            "提拉米蘇\n1.先將白蘭地倒入濃縮咖啡中混合成咖啡酒備用\n2.將鮮奶油分次加入砂糖，打發到有紋路的狀態\n3.將馬斯卡彭起司倒入鮮奶油中拌勻成馬斯卡彭起司糊備用\n4.將手指餅乾沾上咖啡酒放入容器中\n5.鋪上一半的馬斯卡彭起司糊\n6.重複一次「步驟4」，放上吸附咖啡酒的手指餅乾\n7.鋪上剩下的馬斯卡彭起司糊\n8.灑上無糖可可粉，放到冰箱靜置一個晚上就能品嚐囉！",
            "雞蛋布丁\n【食材】\n" +
                    "\n" +
                    "手工布丁5杯份量(每杯大約85 ml)\n" +
                    "\n" +
                    "鮮奶 280ml、雞蛋2顆、鮮奶油30 ml、砂糖 30g (布丁液用)、黑糖 5小匙 (放在布丁底部用)\n" +
                    "\n" +
                    "【做法】\n" +
                    "1.將1小匙黑糖放入布丁杯中。\n" +
                    "\n" +
                    "2.將鮮奶、砂糖、鮮奶油、雞蛋放到大碗中攪拌均勻。(如擔心砂糖無法完全融化，可以稍微將鮮奶微波加溫，先讓鮮奶與砂糖混合攪拌)\n" +
                    "\n" +
                    "3.將步驟2攪拌均勻的布丁液過濾(將不要的蛋筋濾除，否則布丁蒸好會稍微醜醜的)\n" +
                    "\n" +
                    "4.將濾好的布丁液緩慢倒入布丁杯中，蓋上鋁箔紙防止蒸布丁的時水滴進入杯中影響美觀。\n" +
                    "\n" +
                    "5.電鍋倒入2碗水，將布丁放入，準備一隻竹筷子，將電鍋蓋留一個筷子的縫隙，蒸20~30分鐘即可取出冷卻。"};

    private String[]Name_Arr={"豆腐鮮蝦煲","羊肉炒麵","黑胡椒牛柳","滑蛋蝦仁","鍋燒意麵","絲瓜蛤蠣","蔥爆牛肉","金針排骨湯","薑絲炒大腸","鳳梨蝦球","提拉米蘇","雞蛋布丁"};

    private Integer[]cbookArr={R.drawable.cimg01_0,R.drawable.cimg02_0,R.drawable.cimg03_0,R.drawable.cimg04_0,R.drawable.cimg05_0,R.drawable.cimg06_0
            ,R.drawable.cimg07_0,R.drawable.cimg08_0,R.drawable.cimg09_0,R.drawable.cimg10_0,R.drawable.cimg11_0,R.drawable.cimg12_0};


    private RecyclerView r001;
    private TextView ui_loading;
    private Button clookbtn01,clookbtn03;
    private Intent intent = new Intent();
    private Button cedit_btn;
    private Uri uri;
    private Intent it;
    private Button testBtn;
    //private TextView idtest;
    //private MediaPlayer startmusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cooklist);
        setupViewComponent();
    }

    private void setupViewComponent() {

        // 設定class標題
        Intent intent = this.getIntent();
        String mode_title = intent.getStringExtra("class_title");
        this.setTitle(mode_title);

        //gridview03=(GridView)findViewById(R.id.ch_g003);
        gridview02=(GridView)findViewById(R.id.ch_g002);

        //idtest=(TextView)findViewById(R.id.textView);測試用
        //testBtn=(Button)findViewById(R.id.testbutton);
        //testBtn.setOnClickListener(testBtnOn);


        //將縮圖填入GridView
//        setGridView03();
//        gridview03.setAdapter(new GridAdapter(this,thumbImgArr));
//        gridview03.setOnItemClickListener((AdapterView.OnItemClickListener)this);

        setGridView02();
        gridview02.setAdapter(new GridAdapter(this,thumbImgArr));
        gridview02.setOnItemClickListener((AdapterView.OnItemClickListener)this);

        //-----開啟片頭音樂---------
        //startmusic = MediaPlayer.create(getApplication(), R.raw.startmusic);
        //startmusic.start();

        //設定新增食譜BTN監聽
        cedit_btn=(Button)findViewById(R.id.ch_b101);
        cedit_btn.setOnClickListener(cedit_btnOn);

        //-----設定查看更多btn監聽
//        clookbtn01=(Button)findViewById(R.id.clook_b001);
//        clookbtn01.setOnClickListener(clookbtn01On);
        clookbtn03=(Button)findViewById(R.id.clook_b003);
        clookbtn03.setOnClickListener(clookbtn03On);


    }//setupViewComponent(END)

//    private View.OnClickListener testBtnOn = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            intent.setClass(Cooklist.this, CHPage.class);
//
//            //  執行指定的class
//            startActivity(intent);
//
//        }
//    };

    private View.OnClickListener clookbtn03On = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            intent.setClass(Cooklist.this, Copen03.class);

            //  執行指定的class
            startActivity(intent);



            //查看更多BTN連結

//            Uri url=Uri.parse("https://tcnr2021a02.000webhostapp.com/web/20210701-food.html");
//            Intent i=new Intent(Intent.ACTION_VIEW,url);
//            startActivity(i);

        }
    };

    private View.OnClickListener cedit_btnOn = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            intent.setClass(Cooklist.this, CHPage.class);

            //  執行指定的class
            startActivity(intent);

        }
    };





    private void setGridView02() {
        int size = thumbImgArr.length; //找出需放幾張圖
        int length = 85; //縮圖的寬度

        //----------------------
        DisplayMetrics dm = new DisplayMetrics(); //找出使用者手機的寬高
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
//        float w=dm.widthPixels;
//        float h=dm.heightPixels;
        int gridviewWidth = (int) (size * (length + 3) * density*1.1); //整個水平選單的寬度
        int itemWidth = (int) (length * density*1.1); //每個縮圖佔的寬度
//String aa="等一下";
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        gridview02.setLayoutParams(params);
        gridview02.setColumnWidth(itemWidth);
        gridview02.setHorizontalSpacing(2); // 間距
        //gridview.setStretchMode(GridView.NO_STRETCH); //
        //gridview.setNumColumns(size); //

        //gridview.setAdapter(new GridAdapter(this, thumbImgArr));
    }

    private void setGridView03() {
        int size = thumbImgArr.length; //找出需放幾張圖
        int length = 85; //縮圖的寬度

        //----------------------
        DisplayMetrics dm = new DisplayMetrics(); //找出使用者手機的寬高
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
//        float w=dm.widthPixels;
//        float h=dm.heightPixels;
        int gridviewWidth = (int) (size * (length + 3) * density*1.1); //整個水平選單的寬度
        int itemWidth = (int) (length * density*1.1); //每個縮圖佔的寬度
//String aa="等一下";
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(gridviewWidth, LinearLayout.LayoutParams.WRAP_CONTENT);

        gridview03.setLayoutParams(params);
        gridview03.setColumnWidth(itemWidth);
        gridview03.setHorizontalSpacing(1); // 間距

    }



    @Override
    public View makeView() {
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        //點擊食譜的作用
        //idtest.setText(Name_Arr[position]);
        intent.setClass(Cooklist.this, Cbook.class);
        //Index為陣列的第幾個值(位置)
        Bundle bundle = new Bundle();//很像陣列的東西，可以打字串
        bundle.putString("Name_Test", Name_Arr[position]);
        bundle.putInt("Img", cbookArr[position]);
        bundle.putString("Text", Text_Arr[position]);
        intent.putExtras(bundle);
        startActivity(intent);

    }

    //---------禁按返回鍵------------
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
                        .setMessage(getString(R.string.menu_member_message)+"\n"+"維尼、大神、佳佳、波波、柏榕、老大、培揚")
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

}//------END