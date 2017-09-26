package com.juandqt.mijnwords;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.juandqt.mijnwords.adapters.HistoricAdapter;
import com.juandqt.mijnwords.adapters.SpinnerLanguageAdapter;
import com.juandqt.mijnwords.models.PalabraSearch;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import io.realm.Realm;

public class HomeActivity extends AppCompatActivity {

    // Views
    private EditText mInput;
    private Button mBuscar;
    private Context mContext;
    private JSONObject jsonFile;
    private ImageView ivInfo;
    private ImageView ivlanguage;
    private ImageView ivBaseLanguage;
    private ImageView ivExampleLanguage;
    private Button btnHistoric;
    private AlertDialog alertDialog;

    //https://github.com/Tunous/SwipeActionView#license
    private ArrayList<PalabraSearch> list;
    private HistoricAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Leemos
        final InputStream inputStream = getResources().openRawResource(R.raw.palabras); // getting JSON

        Scanner s = new Scanner(inputStream).useDelimiter("\\A");
        final String jsonString = s.hasNext() ? s.next() : "";

        mContext = this.getApplicationContext();
        mBuscar = (Button) findViewById(R.id.btnBuscar);
        mInput = (EditText) findViewById(R.id.etBuscar);
        ivInfo = (ImageView) findViewById(R.id.ivInfo);
        ivlanguage = (ImageView) findViewById(R.id.ivLanguage);
        ivExampleLanguage = (ImageView) findViewById(R.id.ivExampleLanguage);
        btnHistoric = (Button) findViewById(R.id.btnHistoric);

        Picasso.with(this).load(Common.allLanguages.get(Common.getSystemLanguage())).into(ivExampleLanguage);

        mInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
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

                final SpinnerLanguageAdapter spAdapterBaseLanguage = new SpinnerLanguageAdapter(HomeActivity.this, new String[]{"ES"}, new int[]{R.drawable.es_lang});
                spBaseLanguage.setAdapter(spAdapterBaseLanguage);

                spBaseLanguage.setEnabled(false);

                SpinnerLanguageAdapter spAdapterExampleLanguage = new SpinnerLanguageAdapter(HomeActivity.this, new String[]{"EN", "NL"}, new int[]{R.drawable.en_lang, R.drawable.nl_lang});
                spExampleLanguage.setAdapter(spAdapterExampleLanguage);
                int currentPositionSpinner = spAdapterExampleLanguage.getPosition(Common.getSystemLanguage());
                spExampleLanguage.setSelection(currentPositionSpinner);

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

        // Go try search word
        mBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Obtenemos la palabra del fichero JSON
                String palabra = mInput.getText().toString().trim();

                // Check if the input is not empty
                if (palabra.length() > 0) {

                    String id = Common.getIdByPalabra(jsonString, palabra);

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

        btnHistoric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Realm realm = Realm.getDefaultInstance();
                list = API.getAllWordsFromHistoric(realm);

                if (list.size() != 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.ad_historic, null);
                    RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.rvHistoric);
                    recyclerView.setLayoutManager(new LinearLayoutManager(HomeActivity.this, LinearLayout.VERTICAL, false));

                    adapter = new HistoricAdapter(list, HomeActivity.this);
                    recyclerView.setAdapter(adapter);

                    builder.setTitle(getResources().getString(R.string.select_word_from_favs    ));
                    builder.setView(view);

                    alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.no_verbs_saved), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void selectedWord(String palabra) {
        alertDialog.hide();
        mInput.setText(palabra);
        mBuscar.performClick();
    }

    public void removeVerbHistory(int position, String idPalabra) {
        API.deleteWordFromFav(idPalabra);
        this.list.remove(position);
        this.adapter.notifyDataSetChanged();
        if (this.list.size() == 0) {
            this.alertDialog.hide();
        }
    }

}
