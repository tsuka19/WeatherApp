package com.example.weatherapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.os.HandlerCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0,0,0,"松山");
        menu.add(0,1,0,"札幌");
        menu.add(0,2,0,"津");
        menu.add(0,3,0,"東京");
        menu.add(0,4,0,"沖縄");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int region = item.getItemId();
        Executors.newSingleThreadExecutor().execute(() -> {
            URL url;
            try {
                if (region == 0) {


                    url = new URL("https://weather.tsukumijima.net/api/forecast/city/380010");
                }
                else if (region==2){
                    url = new URL("https://weather.tsukumijima.net/api/forecast/city/240010");
                }
                else if (region==3) {
                    url = new URL("https://weather.tsukumijima.net/api/forecast/city/130010");
                }
                else if (region==4) {
                    url = new URL("https://weather.tsukumijima.net/api/forecast/city/471010");
                }
                else {
                    url = new URL("https://weather.tsukumijima.net/api/forecast/city/016010");
                }


                HttpURLConnection con = (HttpURLConnection) url.openConnection();

                con.setRequestMethod("GET");

                InputStreamReader isr = new InputStreamReader(con.getInputStream(),
                        StandardCharsets.UTF_8);

                BufferedReader br = new BufferedReader(isr);

                String line;
                StringBuilder builder = new StringBuilder();

                while ((line = br.readLine()) != null) {
                    builder.append(line);
//                        builder.append("\n"); これでも改行できる
                    //builder.append(System.getProperty("line.separator"));//これで改行できる
                }
                HandlerCompat.createAsync(getMainLooper()).post(
                        new Runnable() {
                            @Override
                            public void run() {
                                TextView textView = findViewById(R.id.textView);
                                textView.setText(builder.toString());
                                TextView textView2 = findViewById(R.id.textView2);
                                textView2.setText(builder.toString());
                                TextView textView3 = findViewById(R.id.textView3);
                                textView3.setText(builder.toString());
                                TextView textView4 = findViewById(R.id.textView4);
                                textView4.setText(builder.toString());
                                TextView textView5 = findViewById(R.id.textView5);
                                textView5.setText(builder.toString());
                                textView3.setMovementMethod(new ScrollingMovementMethod());
                                ImageView imageView = findViewById(R.id.imageView);


                                try {
                                    JSONObject matuyama = new JSONObject(builder.toString());

                                    String title = matuyama.getString("title");
                                    textView.setText(title);

                                    JSONArray array = matuyama.getJSONArray("forecasts");
                                    JSONObject tenki = array.getJSONObject(0);
                                    String telop = tenki.getString("telop");
                                    textView2.setText(telop);

                                    JSONObject gaiyou = matuyama.getJSONObject("description");
                                    String bodyText = gaiyou.getString("bodyText");
                                    textView3.setText(bodyText);


                                    JSONObject temperature = tenki.getJSONObject("temperature");
                                    JSONObject max = temperature.getJSONObject("max");
                                    String celsius = max.getString("celsius");
                                    if (celsius.equals("null")) {
                                        textView5.setText("最高気温 : " + "--");
                                    } else {
                                        textView5.setText("最高気温 : " + celsius);
                                    }


                                    JSONObject min = temperature.getJSONObject("min");
                                    String celsius2 = min.getString("celsius");
                                    if (celsius2.equals("null")) {
                                        textView4.setText("最低気温 : " + "--");
                                    } else {
                                        textView4.setText("最低気温 : " + celsius2);
                                    }

                                    JSONObject image = tenki.getJSONObject("image");
                                    String imageurl = image.getString("url");

                                    Executors.newSingleThreadExecutor().execute(() -> {
                                        try {
                                            URL url2 = new URL(imageurl);
                                            HttpURLConnection con = (HttpURLConnection) url2.openConnection();
                                            con.setRequestMethod("GET");

                                            SVG svg = SVG.getFromInputStream(con.getInputStream());
                                            HandlerCompat.createAsync(getMainLooper()).post(
                                                    new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                                            imageView.setImageDrawable(drawable);
                                                        }});
                                        }
                                        catch ( MalformedURLException e){
                                            e.printStackTrace();
                                        } catch (ProtocolException e) {
                                            throw new RuntimeException(e);
                                        } catch (SVGParseException e) {
                                            throw new RuntimeException(e);
                                        } catch (IOException e) {
                                            throw new RuntimeException(e);
                                        }


                                    });


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return true;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                try{
                    URL url = new URL("https://weather.tsukumijima.net/api/forecast/city/380010");

                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setRequestMethod("GET");

                    InputStreamReader isr = new InputStreamReader(con.getInputStream(),
                            StandardCharsets.UTF_8);

                    BufferedReader br = new BufferedReader(isr);

                    String line ;
                    StringBuilder builder = new StringBuilder();

                    while((line=br.readLine()) != null) {
                        builder.append(line);
//                        builder.append("\n"); これでも改行できる
                        //builder.append(System.getProperty("line.separator"));//これで改行できる
                    }
                    HandlerCompat.createAsync(getMainLooper()).post(
                            new Runnable() {
                                @Override
                                public void run() {
                                    TextView textView = findViewById(R.id.textView);
                                    textView.setText(builder.toString());
                                    TextView textView2 = findViewById(R.id.textView2);
                                    textView2.setText(builder.toString());
                                    TextView textView3 = findViewById(R.id.textView3);
                                    textView3.setText(builder.toString());
                                    TextView textView4 = findViewById(R.id.textView4);
                                    textView4.setText(builder.toString());
                                    TextView textView5 = findViewById(R.id.textView5);
                                    textView5.setText(builder.toString());
                                    textView3.setMovementMethod(new ScrollingMovementMethod());
                                    ImageView imageView = findViewById(R.id.imageView);
                                    try{
                                        JSONObject matuyama = new JSONObject(builder.toString());

                                        String title = matuyama.getString("title");
                                        textView.setText(title);

                                        JSONArray array = matuyama.getJSONArray("forecasts");
                                        JSONObject tenki = array.getJSONObject(0);
                                        String telop = tenki.getString("telop");
                                        textView2.setText(telop);

                                        JSONObject gaiyou = matuyama.getJSONObject("description");
                                        String bodyText = gaiyou.getString("bodyText");
                                        textView3.setText(bodyText);


                                        JSONObject temperature = tenki.getJSONObject("temperature");
                                        JSONObject max = temperature.getJSONObject("max");
                                        String celsius = max.getString("celsius");
                                        if (celsius.equals("null")){
                                            textView5.setText("最高気温 : "+"--");
                                        }
                                        else {
                                            textView5.setText("最高気温 : " + celsius);
                                        }
                                      

                                        JSONObject min = temperature.getJSONObject("min");
                                        String celsius2 = min.getString("celsius");
                                        if (celsius2.equals("null")){
                                            textView4.setText("最低気温 : "+"--");
                                        }
                                        else {
                                            textView4.setText("最低気温 : " + celsius2);
                                        }
                                        JSONObject image = tenki.getJSONObject("image");
                                        String imageurl = image.getString("url");

                                        Executors.newSingleThreadExecutor().execute(() -> {
                                            try {
                                                URL url2 = new URL(imageurl);
                                                HttpURLConnection con = (HttpURLConnection) url2.openConnection();
                                                con.setRequestMethod("GET");

                                                SVG svg = SVG.getFromInputStream(con.getInputStream());
                                                HandlerCompat.createAsync(getMainLooper()).post(
                                                        new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                Drawable drawable = new PictureDrawable(svg.renderToPicture());
                                                                imageView.setImageDrawable(drawable);
                                                            }
                                                        });
                                            } catch (ProtocolException e) {
                                                throw new RuntimeException(e);
                                            } catch (MalformedURLException e) {
                                                throw new RuntimeException(e);
                                            } catch (IOException e) {
                                                throw new RuntimeException(e);
                                            } catch (SVGParseException e) {
                                                throw new RuntimeException(e);
                                            }
                                        });


                                        }
                                    catch (JSONException e){
                                        e.printStackTrace();
                                    }
                                }

                            }
                    );
                }
                catch (IOException e){
                    e.printStackTrace();
                }
            }
        });



    }
}