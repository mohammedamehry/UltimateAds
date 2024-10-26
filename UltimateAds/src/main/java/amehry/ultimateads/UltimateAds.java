package amehry.ultimateads;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.telephony.TelephonyManager;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import amehry.ultimateads.interfaces.CountryListener;
import amehry.ultimateads.interfaces.DataFetchListener;
import amehry.ultimateads.models.UniversaladsResponse;

public class UltimateAds {

    //FIXME :  Error Codes :
    // 1 : user country is blocked 400
    // 2 : country code is null 300;
    // 3 : error in fetching data 100;
    // 3 : internet error 50;


    public static String countryCode = "NULL";
    public static UniversaladsResponse response;
    public static boolean isCustomAdsInitialized = false;
    public static void getUserLocation(Context activity, CountryListener listener) {
        TelephonyManager tm = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
        countryCode = tm.getNetworkCountryIso();
        if (countryCode != null && !countryCode.isEmpty()) {
            countryCode = countryCode.toUpperCase();
            listener.onCountryReceived(response.getBlockedCountries().contains(countryCode));
        } else {
            ExecutorService executorService = Executors.newSingleThreadExecutor();
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(getCountryApi());
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                            StringBuilder stringBuilder = new StringBuilder();
                            String line;
                            while ((line = bufferedReader.readLine()) != null) {
                                stringBuilder.append(line).append("\n");
                            }
                            bufferedReader.close();
                            JSONObject json = new JSONObject(stringBuilder.toString());
                            countryCode = json.getString("countryCode").toUpperCase();
                        } finally {
                            urlConnection.disconnect();
                        }
                    } catch (Exception e) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCountryError(e.getMessage());
                            }
                        });
                        countryCode = "NULL";
                    }
                    String finalCountryCode = countryCode;
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCountryReceived(response.getBlockedCountries().contains(finalCountryCode));
                        }
                    });
                }
            });
        }
    }
    public static String getCountryApi() {
        Random random = new Random();
        int randomInt = random.nextInt(2);
        if (randomInt == 1) {
            return "http://ip-api.com/json/";
        }
        return "https://freeipapi.com/api/json";
    }




    public static void fetchData(Context context, String UniversalLink, DataFetchListener fetchListener){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(com.android.volley.Request.Method.GET, UniversalLink, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<UniversaladsResponse> jsonAdapter = moshi.adapter(UniversaladsResponse.class);
                try {
                    response = jsonAdapter.fromJson(s);
                    getUserLocation(context, new CountryListener() {
                        @Override
                        public void onCountryReceived(boolean blocked) {
                            if (blocked) {
                                fetchListener.onDataFetchError("400");
                            } else {
                                fetchListener.onDataFetched();
                                isCustomAdsInitialized = true;

                            }
                        }

                        @Override
                        public void onCountryError(String error) {
                            fetchListener.onDataFetchError("300");
                        }
                    });

                } catch (Exception e) {
                    fetchListener.onDataFetchError("100");

                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(com.android.volley.VolleyError volleyError) {
                fetchListener.onDataFetchError("50");
            }
        });
        stringRequest.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(5000, com.android.volley.DefaultRetryPolicy.DEFAULT_MAX_RETRIES, com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }



    public static void OpenAmazon(Context context, String packageName) {
        try {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse("amzn://apps/android?p=" + packageName));
            context.startActivity(intent);
        } catch (Exception var3) {
            context.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("http://www.amazon.com/gp/mas/dl/android?p=" + packageName)));
        }

    }
}
