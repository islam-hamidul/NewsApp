package com.example.newsappdemo;

import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.newsappdemo.adapter.Adapter;
import com.example.newsappdemo.broadcast.MyReceiver;
import com.example.newsappdemo.model.Article;
import com.example.newsappdemo.model.News;
import com.example.newsappdemo.network.ApiClient;
import com.example.newsappdemo.network.ApiInterface;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, MyReceiver.ConnectivityReceiverListener {
    public static final String API_KEY = "e5e3a444364b42f0829f035765ce82e9";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Article> articles = new ArrayList<>();
    private List<Article> dbArticle;
    private Adapter adapter;
    private String TAG = MainActivity.class.getSimpleName();
    // private TextView topHeadline;
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean isConnected;
    MyReceiver myReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);
        myReceiver = new MyReceiver();
        checkConnection();

    }

    @Override
    public void onRefresh() {
        // LoadJson();
    }


    public void LoadOnlineData() {

        // errorLayout.setVisibility(View.GONE);
        swipeRefreshLayout.setRefreshing(true);

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        String country = Utils.getCountry();
        String language = Utils.getLanguage();

        Call<News> call;

        /*if (keyword.length() > 0 ){
            call = apiInterface.getNewsSearch(keyword, language, "publishedAt", API_KEY);
        } else {
            call = apiInterface.getNews(country,"business", API_KEY);
        }*/

        call = apiInterface.getNews(country, "business", API_KEY);

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticle() != null) {

                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticle();
                    adapter = new Adapter(articles, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                    initListener();

                    SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(articles);
                    editor.putString("task list", json);
                    editor.apply();
                   /* Bitmap bm = BitmapFactory.decodeFile(response.body().getArticle().get(0).getUrlToImage());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object
                    byte[] b = baos.toByteArray();*/
                    // Log.d("art",new Gson().toJson(articles));

                    // SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                   /* sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    Gson gson = new Gson();
                    String json = gson.toJson(articles);
                    editor.putString("save_articles", json);
                    editor.apply();*/
                    // Toast.makeText(MainActivity.this, "Saved Array List to Shared preferences. ", Toast.LENGTH_SHORT).show();
                    // initListener();

                    // topHeadline.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);


                } else {

                    // topHeadline.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);

                    String errorCode;
                    switch (response.code()) {
                        case 404:
                            errorCode = "404 not found";
                            break;
                        case 500:
                            errorCode = "500 server broken";
                            break;
                        default:
                            errorCode = "unknown error";
                            break;
                    }

                    /*showErrorMessage(
                            R.drawable.no_result,
                            "No Result",
                            "Please Try Again!\n"+
                                    errorCode);*/

                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                // topHeadline.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                /*showErrorMessage(
                        R.drawable.oops,
                        "Oops..",
                        "Network failure, Please Try Again\n"+
                                t.toString());*/
            }
        });


    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(articles);
        editor.putString("task list", json);
        editor.apply();
        Log.d("art", articles.toString());
    }

    private void loadOfflineData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        // Log.d("test2", json);
        Type type = new TypeToken<ArrayList<Article>>() {
        }.getType();
         /*dbArticle = gson.fromJson(json, type);
        dbArticle = new ArrayList<>();*/
        dbArticle = new ArrayList<>();
        try {
            if (json != null) {
                JSONArray jsonArray = new JSONArray(json);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    // String description = object.getString("description");
                    // String Source = object.getString("source");
                    String title = object.getString("title");
                    String author = "";
                    if (!object.isNull("author")) {
                        author = object.getString("author");
                    } else {
                        author = "No Author";
                    }
                    // Log.d("description", jsonArray.length()+"");
                    Article article = new Article();
                    article.setTitle(title);
                    // rticle.setDescription(description);
                    // article.setSource(,Source.toString());
                    article.setAuthor(author);
                    dbArticle.add(article);
                }
            }
            // JSONObject object=jsonArray.getJSONObject(0);
            Log.d("kk", new Gson().toJson(dbArticle));
            adapter = new Adapter(dbArticle, MainActivity.this);
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            /*if (dbArticle == null) {
                dbArticle = new ArrayList<>();
                Log.d("test", dbArticle.toString());
            }*/
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void checkConnection() {
        isConnected = MyReceiver.isConnected();
        if (isConnected) {
            LoadOnlineData();

            // Toast.makeText(this, "Online", Toast.LENGTH_SHORT).show();

        } else {
            loadOfflineData();
            // Toast.makeText(this, "Ooops! you are offline", Toast.LENGTH_SHORT).show();

        }
    }

    private void initListener() {
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, NewsDwtailsActivity.class);

                Article article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("img", article.getUrlToImage());
                startActivity(intent);


            }
        });
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        checkConnection();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myReceiver, filter);
        AppController.getInstance().setConnectivityListener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(myReceiver);
    }

}