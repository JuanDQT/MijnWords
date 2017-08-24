package com.juandqt.mijnwords;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.juandqt.mijnwords.fragments.FragmentModoCondicional;
import com.juandqt.mijnwords.fragments.FragmentModoImperativo;
import com.juandqt.mijnwords.fragments.FragmentModoIndicativo;
import com.juandqt.mijnwords.fragments.FragmentModoSubjuntivo;
import com.juandqt.mijnwords.models.Palabra;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class DetailsActivity extends AppCompatActivity {

    private ProgressBar pbLoading;
    private LinearLayout llError;
    private ImageView ivError;
    private TextView tvError;
    private TextView tvWord;
    private Button btnError;
    private String palabraId;
    private String palabra;

    // Layouts
    private ConstraintLayout clHeaderContent;
    private ViewPager vpContent;
    private ConstraintLayout clEjemplos;
    private ConstraintLayout clEjemploEs;
    private ConstraintLayout clEjemploNl;

    private TextView tvEjemploEs;
    private TextView tvEjemploNl;
    private ImageButton ibRefresh;
    private int indexVerb;
    private ImageButton ibBack;
    private ImageButton ibToggle;
    private ImageView ivBaseVerb;
    private ImageView ivExampleVerb;

    private static final int ANADIR_EJEMPLO = 0;
    private static final int REPORTAR_EJEMPLO = 1;
    private static int ESTADO_TOGGLE = 0;

    /**
     * The pager adapter, which provides the pages to the view pager widget.
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        pbLoading = (ProgressBar) findViewById(R.id.pgLoading);
        llError = (LinearLayout) findViewById(R.id.error_layout);
        ivError = (ImageView) findViewById(R.id.ivError);
        tvError = (TextView) findViewById(R.id.tvError);
        btnError = (Button) findViewById(R.id.btnError);
        ibRefresh = (ImageButton) findViewById(R.id.ibRefresh);
        tvWord = (TextView) findViewById(R.id.tvWord);
        tvEjemploEs = (TextView) findViewById(R.id.tvEjemploEs);
        tvEjemploNl = (TextView) findViewById(R.id.tvEjemploNl);

        clHeaderContent = (ConstraintLayout) findViewById(R.id.clContent);
        vpContent = (ViewPager) findViewById(R.id.vpContent);
        clEjemplos = (ConstraintLayout) findViewById(R.id.clEjemplos);
        clEjemploEs = (ConstraintLayout) findViewById(R.id.clEjemploEs);
        clEjemploNl = (ConstraintLayout) findViewById(R.id.clEjemploNl);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ibToggle = (ImageButton) findViewById(R.id.ibToggle);
        ivBaseVerb = (ImageView) findViewById(R.id.ivBaseVerb);
        ivExampleVerb = (ImageView) findViewById(R.id.ivExampleVerb);

        Picasso.with(this).load(Common.allLanguages.get(Common.getSystemLanguage())).into(ivExampleVerb);

        LocalBroadcastManager.getInstance(this).registerReceiver(success, new IntentFilter("SUCCESS"));
        LocalBroadcastManager.getInstance(this).registerReceiver(errors, new IntentFilter("ERROR"));

        palabraId = getIntent().getStringExtra("id");
        palabra = getIntent().getStringExtra("word");

        // Go call request
        API.getResultados(Common.getContext(), palabraId, palabra.trim().toLowerCase());

        ibBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vpContent.setAdapter(null);
                startActivity(new Intent(DetailsActivity.this, HomeActivity.class));
                finish();
            }
        });

    }

    // Recibidor
    private BroadcastReceiver success = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {
            pbLoading.setVisibility(View.GONE);
            clHeaderContent.setVisibility(View.VISIBLE);
            vpContent.setVisibility(View.VISIBLE);


            HashMap<String, Palabra> params = (HashMap<String, Palabra>) intent.getSerializableExtra("map");
            tvWord.setText(palabra);

            final Palabra palabra = params.get("palabra");


            VerbsPageAdapter verbsPageAdapter = new VerbsPageAdapter(getSupportFragmentManager(), new Fragment[]{new FragmentModoIndicativo(palabra.getModoIndicativo()), new FragmentModoSubjuntivo(palabra.getModoSubjuntivo()), new FragmentModoCondicional(palabra.getModoCondicional()), new FragmentModoImperativo(palabra.getModoImperativo())});
            vpContent.setAdapter(verbsPageAdapter);

            if (palabra.getEjemplo() == null) {
//                Toast.makeText(context, "No hay ejemplos", Toast.LENGTH_SHORT).show();
                //No hay ejemplos
            } else {
                // Un ejemplo en espanol
                clEjemplos.setVisibility(View.VISIBLE);
                clEjemploEs.setVisibility(View.VISIBLE);
                if (palabra.getEjemplo().getEjemplosNl() == null) {
                    clEjemploNl.setVisibility(View.GONE);
                    ibRefresh.setVisibility(View.GONE);
                    Picasso.with(DetailsActivity.this).load(android.R.drawable.ic_menu_add).into(ibToggle);
                    // Solo un ejemplo en espanol
                    tvEjemploEs.setText(palabra.getEjemplo().getEjemplosEs().get(0));
                    ESTADO_TOGGLE = ANADIR_EJEMPLO;

                } else {
                    ibRefresh.setVisibility(View.VISIBLE);
                    clEjemploNl.setVisibility(View.VISIBLE);
                    tvEjemploEs.setText(palabra.getEjemplo().getEjemplosEs().get(0));
                    tvEjemploNl.setText(palabra.getEjemplo().getEjemplosNl().get(0));
                    Picasso.with(DetailsActivity.this).load(android.R.drawable.ic_dialog_alert).into(ibToggle);
                    ESTADO_TOGGLE = REPORTAR_EJEMPLO;

                    // Varios ejemplos en espanol

                    ibRefresh.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Log.e("AD", "Indice: " + indexVerb);
                            indexVerb = indexVerb + 1 == palabra.getEjemplo().getEjemplosEs().size() ? 0 : indexVerb + 1;
                            tvEjemploEs.setText(palabra.getEjemplo().getEjemplosEs().get(indexVerb));
                            tvEjemploNl.setText(palabra.getEjemplo().getEjemplosNl().get(indexVerb));
//                            Sequent.origin(clEjemploEs).start();
//                            Sequent.origin(clEjemploNl).start();
                        }
                    });

//                    Sequent.origin(clEjemploNl).start();
                }

                // https://www.android-arsenal.com/details/1/5828
//                Sequent.origin(clEjemploEs).start();
            }

            ibToggle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(DetailsActivity.this);
                    View cargarView = null;

                    switch (ESTADO_TOGGLE) {
                        case ANADIR_EJEMPLO:
                            cargarView = LayoutInflater.from(DetailsActivity.this).inflate(R.layout.ad_anadir, null);
                            final EditText etTranslate = (EditText) cargarView.findViewById(R.id.etTranslate);
                            etTranslate.setHint(Common.getSystemLanguage());
                            adBuilder.setPositiveButton(getResources().getString(R.string.suggest), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(DetailsActivity.this, "Enviado", Toast.LENGTH_SHORT).show();
                                    if (etTranslate.getText().toString().trim().length() > 0) {
                                        API.postSuggestion(tvWord.getText().toString(), tvEjemploEs.getText().toString(), etTranslate.getText().toString(),Common.getSystemLanguage());
                                    }
                                }
                            });

                            adBuilder.setTitle(getResources().getString(R.string.suggest_translation));
                            adBuilder.setMessage(tvEjemploEs.getText().toString());
                            adBuilder.setView(cargarView);

                            break;
                        case REPORTAR_EJEMPLO:
                            adBuilder.setTitle(DetailsActivity.this.getResources().getString(R.string.wrong_translation));
                            adBuilder.setMessage(DetailsActivity.this.getResources().getString(R.string.wrong_translation_description));
                            adBuilder.setPositiveButton(DetailsActivity.this.getResources().getString(R.string.report), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // TODO: reportar
                                    API.reportEjemplo(tvWord.getText().toString(), tvEjemploEs.getText().toString(), tvEjemploNl.getText().toString(), Common.getSystemLanguage() );
                                }
                            });

                            break;
                    }
                    adBuilder.setNegativeButton(DetailsActivity.this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog adRelease = adBuilder.create();
                    adRelease.show();

                }
            });

        }


    };

    private BroadcastReceiver errors = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int typeError = intent.getIntExtra("RESP", 0);

            switch (typeError) {
                case 0:
                    Log.e("RED", "Error descomocido :P");
                    Picasso.with(context).load(R.drawable.unknow_error).into(ivError);
                    tvError.setText(getResources().getString(R.string.unknow_error));
                    break;
                case 1:
                    Log.e("RED", "No hay conexion a internet");
                    Picasso.with(context).load(R.drawable.no_network).into(ivError);
                    tvError.setText(getResources().getString(R.string.no_internet_access));
                    break;
                case 2:
                    // Solo cuando el te deniega el acceso
                    Log.e("RED", "El servidor parece off");
                    Picasso.with(context).load(R.drawable.server_error).into(ivError);
                    tvError.setText(getResources().getString(R.string.server_off));
                    break;
                case 3:
                    // Tu internet es lento o no se puede conectar con el servidor
                    Log.e("RED", "Conexion a internet lenta");
                    Picasso.with(context).load(R.drawable.slow_internet).into(ivError);
                    tvError.setText(getResources().getString(R.string.slow_internet));
                    break;
            }
            pbLoading.setVisibility(View.GONE);
            llError.setVisibility(View.VISIBLE);
            tvWord.setText(getResources().getString(R.string.title_error));
            btnError.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    llError.setVisibility(View.GONE);
                    pbLoading.setVisibility(View.VISIBLE);
                    API.getResultados(Common.getContext(), palabraId, palabra.trim().toLowerCase());
                }
            });

        }
    };

    @Override
    public void onBackPressed() {
        vpContent.setAdapter(null);
        startActivity(new Intent(DetailsActivity.this, HomeActivity.class));
        finish();
    }

    class VerbsPageAdapter extends FragmentPagerAdapter {

        private Fragment[] pages;

        public VerbsPageAdapter(FragmentManager fm, Fragment[] pages) {
            super(fm);
            Log.e("FR", "frgments creados");
            this.pages = pages;
        }

        @Override
        public Fragment getItem(int position) {
            return pages[position];
        }

        @Override
        public int getCount() {
            return pages.length;
        }
    }


}
