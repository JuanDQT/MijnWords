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
import android.widget.AdapterView;
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

import java.util.ArrayList;

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

    private String[] codeLanguges = Common.allLanguages.keySet().toArray(new String[Common.allLanguages.size()]);
    private Integer[] flagLanguges = Common.allLanguages.values().toArray(new Integer[Common.allLanguages.size()]);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mContext = this.getApplicationContext();
        mBuscar = (Button) findViewById(R.id.btnBuscar);
        mInput = (EditText) findViewById(R.id.etBuscar);
        ivInfo = (ImageView) findViewById(R.id.ivInfo);
        ivlanguage = (ImageView) findViewById(R.id.ivLanguage);
        ivBaseLanguage = (ImageView) findViewById(R.id.ivBaseLanguage);
        ivExampleLanguage = (ImageView) findViewById(R.id.ivExampleLanguage);
        btnHistoric = (Button) findViewById(R.id.btnHistoric);

        Picasso.with(this).load(Common.allLanguages.get(Common.getExampleLanguage())).into(ivExampleLanguage);
        Picasso.with(this).load(Common.allLanguages.get(Common.getBaseLanguage())).into(ivBaseLanguage);

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
                final View vLanguages = LayoutInflater.from(HomeActivity.this).inflate(R.layout.ad_languages, null);
                final Spinner spBaseLanguage = (Spinner) vLanguages.findViewById(R.id.spBaseLanguage);
                final Spinner spExampleLanguage = (Spinner) vLanguages.findViewById(R.id.spExamplesLanguage);

                final SpinnerLanguageAdapter spAdapterBaseLanguage = new SpinnerLanguageAdapter(HomeActivity.this, codeLanguges, flagLanguges);
                spBaseLanguage.setAdapter(spAdapterBaseLanguage);
                int currentPositionSpinner = spAdapterBaseLanguage.getPosition(Common.getBaseLanguage());
                spBaseLanguage.setSelection(currentPositionSpinner);

                setSpinnerExampleConfig(vLanguages, Common.getBaseLanguage());

                spBaseLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        setSpinnerExampleConfig(vLanguages, codeLanguges[position]);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // Nothing
                    }
                });

                adbLanguages.setView(vLanguages);
                adbLanguages.setTitle(getResources().getString(R.string.config_languages));
                adbLanguages.setPositiveButton(getResources().getString(R.string.save), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences("SP", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("BL", spBaseLanguage.getSelectedItem().toString());
                        editor.putString("LN", spExampleLanguage.getSelectedItem().toString());
                        editor.apply();
                        Picasso.with(HomeActivity.this).load(Common.allLanguages.get(Common.getBaseLanguage())).into(ivBaseLanguage);
                        Picasso.with(HomeActivity.this).load(Common.allLanguages.get(Common.getExampleLanguage())).into(ivExampleLanguage);
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
                String palabra = mInput.getText().toString().trim();
                // Check if the input is not empty
                if (palabra.length() > 0) {
                    Intent intent = new Intent(HomeActivity.this, DetailsActivity.class);
                    intent.putExtra("word", Character.toUpperCase(palabra.charAt(0)) + palabra.substring(1).toLowerCase());
                    startActivity(intent);
                    finish();
                    return;

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

                    builder.setTitle(getResources().getString(R.string.select_word_from_favs));
                    builder.setView(view);

                    alertDialog = builder.create();
                    alertDialog.show();
                } else {
                    Toast.makeText(mContext, getResources().getString(R.string.no_verbs_saved), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void selectedWord(String palabra, String codePalabra) {
        SharedPreferences.Editor editor = getSharedPreferences("SP", Context.MODE_PRIVATE).edit();
        editor.putString("BL", codePalabra.toUpperCase());

        // Si la palabra origen y ejemplo tienen el mismo code, cambiamos el code del ejemplo
        if (Common.getExampleLanguage().toUpperCase().equals(codePalabra.toUpperCase())) {
            String newCodeFocusLanguage = getCodeCountries(Common.getExampleLanguage())[0];
            editor.putString("LN", newCodeFocusLanguage.toUpperCase());
            Picasso.with(this).load(Common.allLanguages.get(codePalabra.toUpperCase())).into(ivBaseLanguage);
            Picasso.with(this).load(Common.allLanguages.get(newCodeFocusLanguage.toUpperCase())).into(ivExampleLanguage);
        }
        editor.apply();

        alertDialog.hide();
        mInput.setText(palabra);
        mBuscar.performClick();
    }

    public void removeVerbHistory(int position, String word) {
        API.deleteWordFromFav(word);
        this.list.remove(position);
        this.adapter.notifyDataSetChanged();
        if (this.list.size() == 0) {
            this.alertDialog.hide();
        }
    }

    public void setSpinnerExampleConfig(View vLanguage, String baseLanguage) {
        final Spinner spExampleLanguage = (Spinner) vLanguage.findViewById(R.id.spExamplesLanguage);
        SpinnerLanguageAdapter spAdapterExampleLanguage = new SpinnerLanguageAdapter(HomeActivity.this, getCodeCountries(baseLanguage), getFlagsCountries(baseLanguage));
        spExampleLanguage.setAdapter(spAdapterExampleLanguage);
        int currentPositionSpinner = spAdapterExampleLanguage.getPosition(baseLanguage);
        spExampleLanguage.setSelection(currentPositionSpinner);
    }

    public String[] getCodeCountries(String baseLanguage) {

        String[] selectedCodeLanguages = new String[codeLanguges.length - 1];

        for (int i = 0, index = 0; i < codeLanguges.length; i++) {
            if (!codeLanguges[i].equals(baseLanguage)) {
                selectedCodeLanguages[index] = codeLanguges[i];
                index++;
            }
        }
        return selectedCodeLanguages;
    }

    public Integer[] getFlagsCountries(String baseLanguage) {

        int currentFlag = Common.allLanguages.get(baseLanguage);
        Integer[] selectedFlagLanguages = new Integer[flagLanguges.length - 1];

        for (int i = 0, index = 0; i < flagLanguges.length; i++) {
            if (flagLanguges[i] != currentFlag) {
                selectedFlagLanguages[index] = flagLanguges[i];
                index++;
            }
        }
        return selectedFlagLanguages;
    }
}