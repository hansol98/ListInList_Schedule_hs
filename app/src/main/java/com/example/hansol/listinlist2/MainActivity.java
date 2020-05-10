package com.example.hansol.listinlist2;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements OnItemClick {

    //역 이름을 받아서 지역코드랑 시군구코드 받기 위한 배열(현재 3개 지역만 넣어놔서 배열크기가 3임)
    String[] arr_line = null;
    String[] _name = new String[3];           //txt에서 받은 역이름
    String[] _areaCode = new String[3];       //txt에서 받은 지역코드
    String[] _sigunguCode = new String[3];    //txt에서 받은 시군구코드
    String[] _x = new String[3];              //txt에서 받은 x좌표
    String[] _y = new String[3];              //txt에서 받은 y좌표
    String[] _benefitURL = new String[3];     //txt에서 받은 혜택url
    String st_name, areaCode, sigunguCode, benefitURL;            //전달받은 역의 지역코드, 시군구코드, 혜택URL
    Double x, y;                                      //전달받은 역의 x,y 좌표

    String name_1[] = new String[20];  //returnResult를 줄바꿈 단위로 쪼개서 넣은 배열/ name_1[0]에는 한 관광지의 이름,url,contentId,위치가 다 들어가 있다.
    String name_2[] = new String[5];  //name_1를 "  " 단위로 쪼개서 넣은 배열/ [0]= contentID/ [1]=mapx/ [2]에= mapy/ [3]= img_Url/ [4]= name이 들어가 있다.

    //xml 파싱한 값을 분류해서 쪼개 넣음
    String name[] = new String[20];        //관광지 이름
    String img_Url[] = new String[20];     //이미지 URL
    String contentid[] = new String[20];   //관광지ID
    String mapx[] = new String[20];        //X좌표
    String mapy[] = new String[120];        //Y좌표
    int length = name_1.length;

    String returnResult, url;
    String Url_front = "https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory&fname=";

    List<Recycler_item> items = new ArrayList<>();

    RecyclerView recyclerView;
//    AppBarLayout appBarLayout;
    //    MapView mapView;
//    MapPOIItem marker;
    ViewGroup mapViewContainer;
    Button reset_btn;
    TextView page3_1_x_region, benefit;
    ImageView benefit_url;

    private DbOpenHelper mDbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        mDbOpenHelper.create();

        //리사이클러뷰 구현 부분
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview_db);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        //관광 api 연결 부분
        SearchTask task = new SearchTask();
        try {
            String RESULT = task.execute().get();
            Log.i("전달 받은 값", RESULT);


            //사진링크, 타이틀(관광명), 분야뭔지 분리
            name_1 = RESULT.split("\n");
            Log.i("여기다 문정아ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ",Integer.toString(length));
            for (int i = 0; i < length; i++) {
                name_2 = name_1[i].split("  ");

                //img_Url이 없는 경우도 있기 때문에, 길이=5=있음/ 길이=4=없음
                if (name_2.length == 5) {
                    contentid[i] = name_2[0];
                    img_Url[i] = name_2[1];
                    mapx[i] = name_2[2];
                    mapy[i] = name_2[3];
                    name[i] = name_2[4];
                } else {
                    contentid[i] = name_2[0];
                    img_Url[i] = null;
                    mapx[i] = name_2[1];
                    mapy[i] = name_2[2];
                    name[i] = name_2[3];
                }
            }

            Recycler_item[] item = new Recycler_item[length];
            for (int i = 0; i < length; i++) {
                item[i] = new Recycler_item(Url_front + img_Url[i], name[i], contentid[i]);
            }
            for (int i = 0; i < length; i++) {
                items.add(item[i]);
            }
            recyclerView.setAdapter(new MainAdapter(getApplicationContext(), items, this));
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onClick(double x, double y, String name) {

    }

    @Override
    public void make_db(String countId, String name) {
        mDbOpenHelper.open();
        mDbOpenHelper.insertColumn(countId, name);
        mDbOpenHelper.close();
    }

    @Override
    public void make_dialog() {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("관심관광지 추가 성공");
        builder.setMessage("관심관광지 목록을 확인하시겠습니까?");
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //관심관광지 페이지로 감
                Intent intent = new Intent(MainActivity.this, Heart_page.class);
                //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        builder.show();
    }

    //이 클래스는 어댑터와 서로 주고받으며 쓰는 클래스임
    public static class Recycler_item {
        String image;
        String title;
        String contentviewID;

        String getImage() {
            return this.image;
        }

        String getTitle() {
            return this.title;
        }

        String getContentviewID() {
            return this.contentviewID;
        }


        Recycler_item(String image, String title, String contentviewID) {
            this.image = image;
            this.title = title;
            this.contentviewID = contentviewID;
        }
    }

    //관광api 연결
    class SearchTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            //초기화 단계에서 사용
//            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("시작", "시작");

//            //시군구코드가 0 일 때와 0이 아닐때를 구분해서 url을 넣어준다.
//            if(sigunguCode.equals("0")){
//                url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=" +
//                        "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
//                        "&pageNo=1&numOfRows=20&MobileApp=AppTest&MobileOS=ETC&arrange=A&contentTypeId=12&" +
//                        "sigunguCode=" +
//                        "&areaCode=" + areaCode +
//                        "&listYN=Y";
//            } else {
//                url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=" +
//                        "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
//                        "&pageNo=1&numOfRows=20&MobileApp=AppTest&MobileOS=ETC&arrange=A&contentTypeId=12&" +
//                        "sigunguCode=" + sigunguCode +
//                        "&areaCode=" + areaCode +
//                        "&listYN=Y";
//            }
            url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=" +
                    "7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
                    "&pageNo=1&numOfRows=20&MobileApp=AppTest&MobileOS=ETC&arrange=A&contentTypeId=12&" +
                    "sigunguCode=" +
                    "&areaCode=1" +
                    "&listYN=Y";
//
//            String url = "https://api.visitkorea.or.kr/openapi/service/rest/KorService/areaBasedList?serviceKey=7LT0Q7XeCAuzBmGUO7LmOnrkDGK2s7GZIJQdvdZ30lf7FmnTle%2BQoOqRKpjcohP14rouIrtag9KOoCZe%2BXuNxg%3D%3D" +
//                    "&pageNo=1&numOfRows=10&MobileApp=AppTest&MobileOS=ETC&arrange=A&contentTypeId=12&sigunguCode=&areaCode=1&listYN=Y";

            URL xmlUrl;
            returnResult = "";
            String re = "";

            try {
                boolean title = false;
                boolean firstimage = false;
                boolean item = false;
                boolean contentid = false;
                boolean mapx = false;
                boolean mapy = false;

                xmlUrl = new URL(url);
                Log.d("url", url);
                xmlUrl.openConnection().getInputStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(xmlUrl.openStream(), "utf-8");

                int eventType = parser.getEventType();
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    switch (eventType) {
                        case XmlPullParser.START_DOCUMENT:
                            break;

                        case XmlPullParser.START_TAG: {
                            if (parser.getName().equals("item")) {
                                item = true;
                            }
                            if (parser.getName().equals("contentid")) {
                                contentid = true;
                                Log.d("태그 시작", "태그 시작2");
                            }
                            if (parser.getName().equals("mapx")) {
                                mapx = true;
                            }
                            if (parser.getName().equals("mapy")) {
                                mapy = true;
                            }
                            if (parser.getName().equals("firstimage")) {
                                firstimage = true;
                                Log.d("태그 시작", "태그 시작3");
                            }
                            if (parser.getName().equals("title")) {
                                title = true;
                                Log.d("태그 시작", "태그 시작4");
                            }
                            break;
                        }

                        case XmlPullParser.TEXT: {
                            if (contentid) {
                                returnResult += parser.getText() + "  ";
                                contentid = false;
                            }
                            if (mapx) {
                                returnResult += parser.getText() + "  ";
                                mapx = false;
                            }
                            if (mapy) {
                                returnResult += parser.getText() + "  ";
                                mapy = false;
                            }
                            if (firstimage) {
                                returnResult += parser.getText() + "  ";
                                firstimage = false;
                            }
                            if (title) {
                                returnResult += parser.getText() + "\n";
                                Log.d("태그 받음", "태그받음4");
                                title = false;
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                break;
                            }
                        case XmlPullParser.END_DOCUMENT:
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("err", "erro");
            }
            return returnResult;
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}
