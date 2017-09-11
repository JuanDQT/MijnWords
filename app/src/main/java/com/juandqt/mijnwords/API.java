package com.juandqt.mijnwords;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
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

    // MARK: - Descargamos los datos de la pagina y se lo enviamos al servidor


    static void getResultados(final Context context, final String id, final String palabra) {
        // Instantiate the RequestQueue.
        final RequestQueue queue = Volley.newRequestQueue(context);
        final String host = new Common().getHostURL();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, host, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Intent intent = new Intent("SUCCESS");
                HashMap<String, Palabra> params = new HashMap<>();

                Palabra palabra = getData(response);

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
                params.put("id", id);
                params.put("os", "android");
                params.put("palabra", palabra);
                params.put("lng_focus", Common.getSystemLanguage());
                params.put("lng_base", "ES");
                params.put("app_version", BuildConfig.VERSION_CODE + "");
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

    // Devolvemos el objeto Palabra como resultado al movil.
    static Palabra getData(String json) {

        Palabra palabra = new Palabra();

        JSONObject verbosJSON = new JSONObject();
        Iterator<?> keys = null;
        ModoVerbo modoverbo = new ModoVerbo();

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

            JSONObject modoIndicativoJSON = jsonRoot.getJSONObject("modo_indicativo");

            // Presente
            ArrayList<String> miPresente = new ArrayList<>();
            for (int i = 0; i < modoIndicativoJSON.getJSONArray("presente").length(); i++) {
                miPresente.add(modoIndicativoJSON.getJSONArray("presente").getString(i));
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.presente));
            modoverbo.setPresente(miPresente);

            // Preterito_imperfecto
            ArrayList<String> miPimperfecto = new ArrayList<>();
            for (int i = 0; i < modoIndicativoJSON.getJSONArray("preterito_imperfecto").length(); i++) {
                miPimperfecto.add(modoIndicativoJSON.getJSONArray("preterito_imperfecto").getString(i));
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.preterito_imperfecto));
            modoverbo.setPreteritoImperfecto(miPimperfecto);

            // Preterito indefinido

            ArrayList<String> miPIndefinido = new ArrayList<>();
            for (int i = 0; i < modoIndicativoJSON.getJSONArray("preterito_indefinido").length(); i++) {
                miPIndefinido.add(modoIndicativoJSON.getJSONArray("preterito_indefinido").getString(i));
            }

            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.preterito_indefinido));
            modoverbo.setPreteritoIndefinido(miPIndefinido);

            // Futuro
            ArrayList<String> miFuturo = new ArrayList<>();
            for (int i = 0; i < modoIndicativoJSON.getJSONArray("futuro").length(); i++) {
                miFuturo.add(modoIndicativoJSON.getJSONArray("futuro").getString(i));
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.futuro));
            modoverbo.setFuturo(miFuturo);

            palabra.setModoIndicativo(modoverbo);

            // MARK: - Modo subjuntivo

            modoverbo = new ModoVerbo();

            JSONObject modoSubjuntivo = jsonRoot.getJSONObject("modo_subjuntivo");
            ArrayList<String> msPresente = new ArrayList<>();
            for (int i = 0; i < modoSubjuntivo.getJSONArray("presente").length(); i++) {
                msPresente.add(modoSubjuntivo.getJSONArray("presente").getString(i));
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.presente));
            modoverbo.setPresente(msPresente);

            ArrayList<String> msPimperfecto = new ArrayList<>();
            for (int i = 0; i < modoSubjuntivo.getJSONArray("preterito_imperfecto").length(); i++) {
                msPimperfecto.add(modoSubjuntivo.getJSONArray("preterito_imperfecto").getString(i));
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.preterito_imperfecto));
            modoverbo.setPreteritoImperfecto(msPimperfecto);

            ArrayList<String> msFuturo = new ArrayList<>();
            for (int i = 0; i < modoSubjuntivo.getJSONArray("futuro").length(); i++) {
                msFuturo.add(modoSubjuntivo.getJSONArray("futuro").getString(i));
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.futuro));
            modoverbo.setFuturo(msFuturo);

            palabra.setModoSubjuntivo(modoverbo);


            // MARK: - Modo Condicional

            modoverbo = new ModoVerbo();

            JSONObject modoCondicional = jsonRoot.getJSONObject("modo_condicional");

            ArrayList<String> mcCondicional = new ArrayList<>();
            for (int i = 0; i < modoCondicional.getJSONArray("condicional").length(); i++) {
                mcCondicional.add(modoCondicional.getJSONArray("condicional").getString(i));
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.condicional));
            modoverbo.setCondicional(mcCondicional);

            palabra.setModoCondicional(modoverbo);

            // MARK: - Modo Imperativo

            modoverbo = new ModoVerbo();

            JSONObject modoImperativo = jsonRoot.getJSONObject("modo_imperativo");

            ArrayList<String> miAfirmativo = new ArrayList<>();
            for (int i = 0; i < modoImperativo.getJSONArray("afirmativo").length(); i++) {
                miAfirmativo.add(modoImperativo.getJSONArray("afirmativo").getString(i));
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.afirmativo));
            modoverbo.setAfirmativo(miAfirmativo);

            ArrayList<String> miNegativo = new ArrayList<>();
            for (int i = 0; i < modoImperativo.getJSONArray("negativo").length(); i++) {
                miNegativo.add(modoImperativo.getJSONArray("negativo").getString(i));
            }
            modoverbo.setTiempo(Common.getContext().getResources().getString(R.string.negativo));
            modoverbo.setNegativo(miNegativo);

            palabra.setModoImperativo(modoverbo);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return palabra;
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


}
