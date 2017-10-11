package com.juandqt.mijnwords;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.juandqt.mijnwords.models.Ejemplo;
import com.juandqt.mijnwords.models.Modo;
import com.juandqt.mijnwords.models.PalabraSearch;
import com.juandqt.mijnwords.models.Verbo;
import com.juandqt.mijnwords.models.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

/**
 * Created by juandaniel on 7/8/17.
 */

// https://stackoverflow.com/questions/3947641/android-equivalent-to-nsnotificationcenter
class API {

    // MARK: - Descargamos los datos de la pagina y se lo enviamos al servidor


    static void getResultados(final Context context, final String palabra) {
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(context);
        final String host = new Common().getHostURL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, host, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent intent = new Intent("SUCCESS");
                HashMap<String, Word> params = new HashMap<>();
                Log.e("PIP", response);
                Word palabra = getData(response);

                if (palabra == null) {
                    LocalBroadcastManager.getInstance(Common.context).sendBroadcast(new Intent("UPDATE"));
                    return;
                }

                params.put("palabra", palabra);
                intent.putExtra("map", params);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handleNewtworkErrors(volleyError, context);
            }

        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("palabra", palabra);
                params.put("lng_focus", Common.getExampleLanguage());
                params.put("lng_base", Common.getBaseLanguage().toLowerCase());
                params.put("app_version", "4");
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    // Errores de conexión, server off, no responde..
    private static void handleNewtworkErrors(VolleyError volleyError, Context context) {
        Intent intent = new Intent("ERROR");

        if (volleyError instanceof NetworkError) {
            intent.putExtra("RESP", 1);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } else if (volleyError instanceof ServerError) {
            intent.putExtra("RESP", 2);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } else if (volleyError instanceof AuthFailureError) {
            intent.putExtra("RESP", 0);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } else if (volleyError instanceof ParseError) {
            intent.putExtra("RESP", 0);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } else if (volleyError instanceof NoConnectionError) {
            intent.putExtra("RESP", 0);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        } else if (volleyError instanceof TimeoutError) {
            intent.putExtra("RESP", 3);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

    }

    // Devolvemos el objeto como resultado al movil.
    static Word getData(String json) {

        Word palabra = new Word();

        // Cogemos el titulo
        try {

            // MARK: - Modo indicativo
            JSONObject jsonRoot = new JSONObject(json);

            // Checking app updates
            if (!jsonRoot.getBoolean("updated")) {

                return null;
            }

            if (jsonRoot.isNull("ejemplo")) {
                palabra.setEjemplo(null);
            } else {
                JSONObject jsonEjemplo = jsonRoot.getJSONObject("ejemplo");
                Ejemplo ejemplo = new Ejemplo();

                // Con 2, es un jsonArray
                JSONArray jsonEjemplosEs = jsonEjemplo.getJSONArray("base");
                JSONArray jsonEjemplosNl = jsonEjemplo.getJSONArray("focus");

                ArrayList<String> ejemplosEs = new ArrayList<>();
                ArrayList<String> ejemplosNl = new ArrayList<>();

                for (int i = 0; i < jsonEjemplosEs.length(); i++) {
                    ejemplosEs.add(jsonEjemplosEs.getString(i));
                    ejemplosNl.add(jsonEjemplosNl.getString(i));
                }

                ejemplo.setEjemplosEs(ejemplosEs);
                ejemplo.setEjemplosNl(ejemplosNl);
                palabra.setEjemplo(ejemplo);

            }

            // Buscamos todas sus modos:

            JSONArray jsonModos = jsonRoot.getJSONArray("modos");

            // Recogemos todos los verbos del modo
            for (int i = 0; i < jsonModos.length(); i++) {
                JSONArray verbContainer = jsonModos.getJSONObject(i).getJSONArray("verbs");
                // Iteramos la array que contiene los verbos(2,4)

                Modo modo = new Modo();
                // Tiem;os
                String title = jsonModos.getJSONObject(i).getString("title");
                modo.setTitle(title);
                String times = jsonModos.getJSONObject(i).getString("times");
                String timesArray[] = times.split(",");
                String persons = jsonModos.getJSONObject(i).getString("persons");
                modo.setPersons(Arrays.asList(persons.split(",")));

                for (int j = 0; j < verbContainer.length(); j++) {
                    JSONArray arrayVerb = verbContainer.getJSONArray(j);
                    Verbo verbo = new Verbo();
                    verbo.setTiempo(timesArray[j]);// JSON get tiempo position
                    //Iteramos cada uno de los verbos y los guardamos
                    for (int k = 0; k < arrayVerb.length(); k++) {
                        String verboResult = (String) arrayVerb.get(k);
                        verbo.addVerb(verboResult);
                    }
                    modo.addVerbo(verbo);
                }
                palabra.addModo(modo);
            }

        } catch (JSONException e) {
            e.printStackTrace();
            e.printStackTrace();
        }

        return palabra;
    }

    static boolean checkIfWordIsInFav(String word) {
        try (Realm realm = Realm.getDefaultInstance()) {
            PalabraSearch palabraSearch = realm.where(PalabraSearch.class).equalTo("name", word).findFirst();
            return palabraSearch != null;
        }
    }

    static void saveWordToFav(String palabra, String baseCodeLanguge) {
        try (Realm realm = Realm.getDefaultInstance()) {
            realm.beginTransaction();
            PalabraSearch palabraSearch = realm.createObject(PalabraSearch.class);
            palabraSearch.setName(palabra);
            palabraSearch.setLanguageCode(baseCodeLanguge);
            realm.commitTransaction();
        }
    }

    static void deleteWordFromFav(String word) {
        try (Realm realm = Realm.getDefaultInstance()) {
            PalabraSearch palabraSearch = realm.where(PalabraSearch.class).equalTo("name", word).findFirst();
            realm.beginTransaction();
            // TODO: check?
            palabraSearch.deleteFromRealm();
            realm.commitTransaction();
        }
    }

    static ArrayList<PalabraSearch> getAllWordsFromHistoric(Realm realm) {
        return new ArrayList<>(realm.where(PalabraSearch.class).findAll());
    }
}
