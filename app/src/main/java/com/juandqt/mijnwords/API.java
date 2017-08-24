package com.juandqt.mijnwords;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

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
import com.juandqt.mijnwords.models.ModoVerbo;
import com.juandqt.mijnwords.models.Palabra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by juandaniel on 7/8/17.
 */

// https://stackoverflow.com/questions/3947641/android-equivalent-to-nsnotificationcenter
class API {

    static void getResultados(final Context context, final String id, final String palabra) {
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(context);
        final String url = new Common().getUrlURL(id);
        final String host = new Common().getHostURL();

        final StringRequest contentRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(final String responseHTML) {
                StringRequest stringRequest = new StringRequest(Request.Method.POST, host,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Intent intent = new Intent("SUCCESS");

                                HashMap<String, Palabra> params = new HashMap<>();

                                Palabra palabra = getData(response);

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
                        params.put("id", id);
                        params.put("palabra", palabra);
                        params.put("content", responseHTML);
                        params.put("lng_base", "ES");
                        params.put("lng_focus", Common.getSystemLanguage());
                        params.put("app_version", BuildConfig.VERSION_CODE + "");
                        return params;
                    }
                };

                queue.add(stringRequest);

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                handleNewtworkErrors(volleyError, context);
            }
        });


// Request a string response from the provided URL.
        // Add the request to the RequestQueue.
        queue.add(contentRequest);
    }

    private static void handleNewtworkErrors(VolleyError volleyError, Context context) {
        Intent intent = new Intent("ERROR");

        if (volleyError.networkResponse != null) {
            Toast.makeText(Common.context, "Actualizame[LEVANTAR VIEW] " + volleyError.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
        } else {
            String message = null;
            if (volleyError instanceof NetworkError) {
                message = "Cannot connect to Internet...Please check your connection!";
                intent.putExtra("RESP", 1);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            } else if (volleyError instanceof ServerError) {
                message = "The server could not be found. Please try again after some time!!";
                intent.putExtra("RESP", 2);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            } else if (volleyError instanceof AuthFailureError) {
                intent.putExtra("RESP", 0);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof ParseError) {
                intent.putExtra("RESP", 0);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                message = "Parsing error! Please try again after some time!!";
            } else if (volleyError instanceof NoConnectionError) {
                intent.putExtra("RESP", 0);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof TimeoutError) {
                intent.putExtra("RESP", 3);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                message = "Connection TimeOut! Please check your internet connection.";
            }

//                Log.e("RED", message);
        }

    }

    static Palabra getData(String json) {

        Log.e("RED", json);

        Palabra palabra = new Palabra();

        JSONObject verbosJSON = new JSONObject();
        Iterator<?> keys = null;
        ModoVerbo modoverbo = new ModoVerbo();

        // Cogemos el titulo
        try {

            // MARK: - Modo indicativo
            JSONObject jsonRoot = new JSONObject(json);

            // TODO: array de ejemoplos

            if (jsonRoot.isNull("ejemplo")) {
                Log.e("AD", "no ejemplos");
                palabra.setEjemplo(null);
            } else {
                JSONObject jsonEjemplo = jsonRoot.getJSONObject("ejemplo");
                Log.e("AD", "Bien, tenemos: " + jsonEjemplo.length());
                Ejemplo ejemplo = new Ejemplo();
                // Con 1 ejemplo, es un jsonObject
                if (jsonEjemplo.length() == 1) {
                    ejemplo.setEjemplosNl(null);
                    String extractEjemplo = jsonEjemplo.getString("base");
                    Log.e("AD", "Solo un ejemplo que contiene: " + extractEjemplo);
                    ArrayList<String> ejemplos = new ArrayList<>();
                    ejemplos.add(extractEjemplo);
                    ejemplo.setEjemplosEs(ejemplos);
                    palabra.setEjemplo(ejemplo);

                    Log.e("AD", ejemplos.toString());

                    // Con 2, es un jsonArray
                } else if (jsonEjemplo.length() == 2) {
                    JSONArray jsonEjemplosEs = jsonEjemplo.getJSONArray("base");
                    JSONArray jsonEjemplosNl = jsonEjemplo.getJSONArray("focus");
                    Log.e("AD", "Mas de: " + jsonEjemplosEs.length());

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


                // Con dos listas de ejemplos
            }

            JSONObject modoIndicativoJSON = jsonRoot.getJSONObject("modo_indicativo");

            // Presente
            verbosJSON = modoIndicativoJSON.getJSONObject("presente");
            ArrayList<String> miPresente = new ArrayList<>();
            keys = verbosJSON.keys();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                miPresente.add(item);
            }
            modoverbo.setTiempo("KK");
//            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.presente));
            modoverbo.setPresente(miPresente);

            // Preterito_imperfecto
            verbosJSON = modoIndicativoJSON.getJSONObject("preterito_imperfecto");
            keys = verbosJSON.keys();
            ArrayList<String> miPimperfecto = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                miPimperfecto.add(item);
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.preterito_imperfecto));
            modoverbo.setPreteritoImperfecto(miPimperfecto);

            // Preterito indefinido
            verbosJSON = modoIndicativoJSON.getJSONObject("preterito_indefinido");

            keys = verbosJSON.keys();
            ArrayList<String> miPIndefinido = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                miPIndefinido.add(item);
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.preterito_indefinido));
            modoverbo.setPreteritoIndefinido(miPIndefinido);

            // Futuro
            verbosJSON = modoIndicativoJSON.getJSONObject("futuro");
            keys = verbosJSON.keys();
            ArrayList<String> miFuturo = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                miFuturo.add(item);
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.futuro));
            modoverbo.setFuturo(miFuturo);

            palabra.setModoIndicativo(modoverbo);


            // MARK: - Modo subjuntivo

            modoverbo = new ModoVerbo();
            keys = null;

            JSONObject modoSubjuntivo = jsonRoot.getJSONObject("modo_subjuntivo");
            verbosJSON = modoSubjuntivo.getJSONObject("presente");

            keys = verbosJSON.keys();
            ArrayList<String> msPresente = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                msPresente.add(item);
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.presente));
            modoverbo.setPresente(msPresente);

            verbosJSON = modoSubjuntivo.getJSONObject("preterito_imperfecto");

            keys = verbosJSON.keys();
            ArrayList<String> msPimperfecto = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                msPimperfecto.add(item);
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.preterito_imperfecto));
            modoverbo.setPreteritoImperfecto(msPimperfecto);

            verbosJSON = modoSubjuntivo.getJSONObject("futuro");

            keys = verbosJSON.keys();
            ArrayList<String> msFuturo = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                msFuturo.add(item);
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.futuro));
            modoverbo.setFuturo(msFuturo);

            palabra.setModoSubjuntivo(modoverbo);


            // MARK: - Modo Condicional

            modoverbo = new ModoVerbo();

            JSONObject modoCondicional = jsonRoot.getJSONObject("modo_condicional");
            verbosJSON = modoCondicional.getJSONObject("condicional");

            keys = verbosJSON.keys();
            ArrayList<String> mcCondicional = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                mcCondicional.add(item);
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.condicional));
            modoverbo.setCondicional(mcCondicional);

            palabra.setModoCondicional(modoverbo);

            // MARK: - Modo Imperativo

            modoverbo = new ModoVerbo();

            JSONObject modoImperativo = jsonRoot.getJSONObject("modo_imperativo");
            verbosJSON = modoImperativo.getJSONObject("afirmativo");

            keys = verbosJSON.keys();
            ArrayList<String> miAfirmativo = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                miAfirmativo.add(item);
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.afirmativo));
            modoverbo.setAfirmativo(miAfirmativo);

            verbosJSON = modoImperativo.getJSONObject("negativo");

            keys = verbosJSON.keys();
            ArrayList<String> miNegativo = new ArrayList<>();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                String item = verbosJSON.getString(key);
                miNegativo.add(item);
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.negativo));
            modoverbo.setNegativo(miNegativo);

            palabra.setModoImperativo(modoverbo);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.e("RED", "Test MS: " + palabra.toString());

        return palabra;
    }

    public static void postSuggestion(final String verb, final String baseExample, final String newExample, final String focusLanguage) {
        final String url = new Common().getUrlSuggestion();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Common.getContext(), Common.context.getResources().getString(R.string.suggest_sent), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Common.getContext(), Common.context.getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("verb", verb);
                params.put("base_example", baseExample);
                params.put("focus_language", focusLanguage);
                params.put("new_example", newExample);
                return params;
            }
        };

        Volley.newRequestQueue(Common.context).add(stringRequest);
    }

    public static void reportEjemplo(final String verb, final String baseExample, final String translatedExample, final String focusLanguage) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new Common().getUrlHostReport(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Common.context, Common.context.getResources().getString(R.string.report_sent), Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Common.getContext(), Common.context.getResources().getString(R.string.try_later), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("verb", verb);
                params.put("base_example", baseExample);
                params.put("focus_language", focusLanguage);
                params.put("translated_example", translatedExample);
                return params;
            }
        };

        Volley.newRequestQueue(Common.context).add(stringRequest);
    }

    public static void checkIfUpdated(final Context context) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, new Common().getUpdateUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(Common.context, "Inicia todo actualizado", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(Common.context, "Actualizame " + error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
                handleNewtworkErrors(error, context);

//
//                if (error.networkResponse != null) {
//                    Toast.makeText(Common.context, "Actualizame " + error.networkResponse.statusCode, Toast.LENGTH_SHORT).show();
//                } else {
//                    handleNewtworkErrors(error, context);
//                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("app_version", BuildConfig.VERSION_CODE + "");
                return params;
            }
        };
        Volley.newRequestQueue(Common.context).add(stringRequest);
    }

}
