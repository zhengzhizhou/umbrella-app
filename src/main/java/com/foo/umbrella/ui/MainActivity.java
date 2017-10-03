package com.foo.umbrella.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.foo.umbrella.R;
import com.foo.umbrella.adapter.DailyViewAdapter;
import com.foo.umbrella.data.ApiServicesProvider;
import com.foo.umbrella.data.api.WeatherService;
import com.foo.umbrella.data.model.CurrentObservation;
import com.foo.umbrella.data.model.ForecastCondition;
import com.foo.umbrella.data.model.WeatherData;

import java.util.ArrayList;
import java.util.List;

import retrofit2.adapter.rxjava.Result;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity{
    WeatherService weatherService;
    String forcastString;
    TextView location, temp, desc;
    LinearLayout linearLayout;
    ImageView setting;
    GridLayoutManager gridLayoutManager_today, gridLayoutManager_tomorrow;
    ApiServicesProvider apiServicesProvider;
    RecyclerView recyclerView_today, recyclerView_tomorrow;
    DailyViewAdapter dailyViewAdapter_today, dailyViewAdapter_tomorrow;
    Toolbar toolbar;
    String units;
    SharedPreferences prefs;
    List<ForecastCondition> forecastConditions, forecastConditions_today, forecastConditions_tomorrow;
    int highest_today = Integer.MIN_VALUE, lowest_today = Integer.MAX_VALUE, highest_tomorrow = Integer.MIN_VALUE, lowest_tomorrow = Integer.MAX_VALUE;
    CurrentObservation currentObservation;
    int index_h_today = 0, index_l_today = 0, index_h_tomorrow = 0, index_l_tomorrow = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        prefs = getSharedPreferences("umbrella", MODE_PRIVATE);

        setting = (ImageView) findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
        forecastConditions_today = new ArrayList<>();
        forecastConditions_tomorrow = new ArrayList<>();
        linearLayout = (LinearLayout)findViewById(R.id.line1);
        location = (TextView)findViewById(R.id.location);
        temp = (TextView)findViewById(R.id.temp);
        desc = (TextView)findViewById(R.id.desc);

        apiServicesProvider = new ApiServicesProvider(getApplication());
        recyclerView_today = (RecyclerView)findViewById(R.id.recyclerView_today);
        recyclerView_tomorrow = (RecyclerView)findViewById(R.id.recyclerView_tomorrow);
        gridLayoutManager_today = new GridLayoutManager(getApplicationContext(), 4);
        gridLayoutManager_tomorrow = new GridLayoutManager(getApplicationContext(), 4);
        recyclerView_today.setLayoutManager(gridLayoutManager_today);
        recyclerView_tomorrow.setLayoutManager(gridLayoutManager_tomorrow);

    }

    private void checkForUnits() {
        if(prefs.contains("location")){
            units = getIntent().getStringExtra("units");
        }else{
            Intent intent = new Intent(MainActivity.this, SettingActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkForUnits();
    }

    @Override
    protected void onResume(){
        super.onResume();
        loadJson();
    }
    private void loadJson(){
        String zip = prefs.getString("location", "08902");
        Observable<Result<WeatherData>> observable = apiServicesProvider.getWeatherService().forecastForZipObservable(zip);
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Result<WeatherData>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.d("error", e.getMessage());
            }

            @Override
            public void onNext(Result<WeatherData> weatherDataResult) {
                forecastConditions = weatherDataResult.response().body().getForecast();
                currentObservation = weatherDataResult.response().body().getCurrentObservation();
                location.setText(currentObservation.getDisplayLocation().getFullName());
                desc.setText(currentObservation.getWeatherDescription());
                setForList(weatherDataResult);
                if(prefs.getString("units", "abc") == "Fahrenheit"){
                    temp.setText(currentObservation.getTempCelsius() + " C");
                }else{
                    temp.setText(currentObservation.getTempFahrenheit() + " F");
                }
                if((int)(Double.parseDouble(currentObservation.getTempCelsius()))>60){
                    linearLayout.setBackgroundColor(Color.parseColor("#FF9800"));
                }else{
                    linearLayout.setBackgroundColor(Color.parseColor("#03A9F4"));
                }
                boolean tomorrow = false;
                int today_hours = 0;
                int today = forecastConditions.get(0).getDateTime().getDayOfYear();
                for(int i = 0; i<forecastConditions.size(); i++){
                    if(today == forecastConditions.get(i).getDateTime().getDayOfYear()){
                        forecastConditions_today.add(forecastConditions.get(i));
                        if(Integer.parseInt(forecastConditions.get(i).getTempFahrenheit())>highest_today){
                            highest_today = Integer.parseInt(forecastConditions.get(i).getTempFahrenheit());
                            index_h_today = i;
                        }
                        if(Integer.parseInt(forecastConditions.get(i).getTempFahrenheit())<lowest_today){
                            lowest_today = Integer.parseInt(forecastConditions.get(i).getTempFahrenheit());
                            index_l_today = i;
                        }
                    }else if(forecastConditions.get(i).getDateTime().getDayOfYear() == today + 1){
                        if(tomorrow == false){
                            today_hours = i;
                        }
                        tomorrow = true;
                        forecastConditions_tomorrow.add(forecastConditions.get(i));
                        if(Integer.parseInt(forecastConditions.get(i).getTempFahrenheit())>highest_tomorrow){
                            highest_tomorrow = Integer.parseInt(forecastConditions.get(i).getTempFahrenheit());
                            index_h_tomorrow = i;
                        }
                        if(Integer.parseInt(forecastConditions.get(i).getTempFahrenheit())<lowest_tomorrow){
                            lowest_tomorrow = Integer.parseInt(forecastConditions.get(i).getTempFahrenheit());
                            index_l_tomorrow = i;
                        }
                    }
                }

                dailyViewAdapter_today = new DailyViewAdapter(getApplicationContext(), forecastConditions_today, index_h_today, index_l_today);
                dailyViewAdapter_tomorrow = new DailyViewAdapter(getApplicationContext(), forecastConditions_tomorrow, index_h_tomorrow-today_hours, index_l_tomorrow-today_hours);
                recyclerView_today.setAdapter(dailyViewAdapter_today);
                recyclerView_tomorrow.setAdapter(dailyViewAdapter_tomorrow);
            }
        });
    }

    private void setForList(Result<WeatherData> weatherDataResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.setting:
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);

        }
        return true;
    }

}
