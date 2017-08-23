package com.juandqt.mijnwords;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.juandqt.mijnwords.adapters.SpinnerLanguageAdapter;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.Scanner;

public class HomeActivity extends AppCompatActivity {

    private EditText mInput;
    private Button mBuscar;
    private Context mContext;
    private JSONObject jsonFile;
    private ImageView ivInfo;
    private ImageView ivlanguage;
    private ImageView ivBaseLanguage;
    private ImageView ivExampleLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Leemos

        final InputStream inputStream = getResources().openRawResource(R.raw.palabras); // getting XML

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        final String jsonString = s.hasNext() ? s.next() : "";

        mContext = this.getApplicationContext();
        mBuscar = (Button) findViewById(R.id.buscar);
        mInput = (EditText) findViewById(R.id.etBuscar);
        ivInfo = (ImageView) findViewById(R.id.ivInfo);
        ivlanguage = (ImageView) findViewById(R.id.ivLanguage);
        ivExampleLanguage = (ImageView) findViewById(R.id.ivExampleLanguage);

        Picasso.with(this).load(Common.allLanguages.get(Common.getSystemLanguage())).into(ivExampleLanguage);

        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                    Log.i("NEw","Enter pressed");
                    mBuscar.callOnClick();
                }
                return false;
            }
        });

        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder adWindows = new AlertDialog.Builder(HomeActivity.this);
                View viewPopUp = LayoutInflater.from(HomeActivity.this).inflate(R.layout.ad_credits, null);
                adWindows.setView(viewPopUp);

                AlertDialog adPopup = adWindows.create();
                adPopup.show();
            }
        });


        ivlanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                AlertDialog.Builder adbLanguages = new AlertDialog.Builder(HomeActivity.this);
                View vLanguages = LayoutInflater.from(HomeActivity.this).inflate(R.layout.ad_languages, null);
                final Spinner spBaseLanguage = (Spinner) vLanguages.findViewById(R.id.spBaseLanguage);
                final Spinner spExampleLanguage = (Spinner) vLanguages.findViewById(R.id.spExamplesLanguage);

                final SpinnerLanguageAdapter spAdapterBaseLanguage = new SpinnerLanguageAdapter(HomeActivity.this, new String[]{"ES"},new int[]{R.drawable.es_lang});
                spBaseLanguage.setAdapter(spAdapterBaseLanguage);

                spBaseLanguage.setEnabled(false);
                Toast.makeText(HomeActivity.this, "Idioma: " + Common.getSystemLanguage(), Toast.LENGTH_SHORT).show();

                SpinnerLanguageAdapter spAdapterExampleLanguage = new SpinnerLanguageAdapter(HomeActivity.this, new String[]{"EN", "NL"},new int[]{R.drawable.en_lang, R.drawable.nl_lang});
                spExampleLanguage.setAdapter(spAdapterExampleLanguage);
                spExampleLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        TextView tvLanguage = (TextView) view.findViewById(R.id.tvCountry);
                        Toast.makeText(HomeActivity.this, "Seleccionado: " +  tvLanguage.getText().toString()  , Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                adbLanguages.setView(vLanguages);
                adbLanguages.setTitle(getResources().getString(R.string.config_languages));
                adbLanguages.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO guardar en Preferene shared<resources>
                        SharedPreferences sharedPreferences = getSharedPreferences("SP", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("LN", spExampleLanguage.getSelectedItem().toString());
                        editor.commit();
                        Picasso.with(HomeActivity.this).load(Common.allLanguages.get(Common.getSystemLanguage())).into(ivExampleLanguage);
                    }
                });
                adbLanguages.setNegativeButton(getResources().getString(R.string.cancel), null);
                AlertDialog adView = adbLanguages.create();
                adView.show();

            }
        });

        //
        mBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtenemos la palabra del fichero JSON
                String palabra = mInput.getText().toString();

                // Check if the input is not empty
                if (palabra.trim().length() > 0) {

                    String id = getIdByPalabra(jsonString, palabra);

                    // Check if the response of the key exist
                    if (id.length() > 0) {
                        // Go next Activity with ID word
                        Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
                        intent.putExtra("id", id);
                        intent.putExtra("word", Character.toUpperCase(palabra.charAt(0)) + palabra.substring(1).toLowerCase());
                        startActivity(intent);
                        finish();
                        return;

                    } else {
                        Toast.makeText(mContext, "Esa palabra no esta en el diccionario", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public String getIdByPalabra(String json, String palabra) {

        String id = "";

        try {
            jsonFile = new JSONObject(json);
        } catch (JSONException e) {
            Log.e("ERROR", "JSON mal formado");
            e.printStackTrace();
        }

        // Iterar sobre las letras

        if (palabra.charAt(0) >= 'a' && palabra.charAt(0) <= 'z' || palabra.charAt(0) >= 'A' && palabra.charAt(0) <= 'Z') {

            try {

                String firstLetra = palabra.toUpperCase().charAt(0) + "";

                // Si es numero o ~234.. etc, de esta linea pasa a la excepcion
                JSONArray letraJSON = jsonFile.getJSONArray(firstLetra);

                for (int i = 0; i < letraJSON.length(); i++) {
                    JSONObject row = letraJSON.getJSONObject(i);
                    String key = row.keys().next();

                    if (row.getString(key).equals(palabra.toLowerCase())) {
                        id = key;
                    }
                }

            } catch (Exception e) {
                Log.e("JSON", "q fas nen");
                e.printStackTrace();
            }

        }


        return id;
    }
}
