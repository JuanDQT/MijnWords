package com.juandqt.mijnwords;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    // Default views
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

    // Views
    private TextView tvEjemploEs;
    private TextView tvEjemploNl;
    private ImageButton ibBack;
    private ImageView ivBaseVerb;
    private ImageView ivExampleVerb;
    private ImageButton ibSave;

    private int indexVerb;

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
        tvWord = (TextView) findViewById(R.id.tvWord);
        tvEjemploEs = (TextView) findViewById(R.id.tvEjemploEs);
        tvEjemploNl = (TextView) findViewById(R.id.tvEjemploNl);

        clHeaderContent = (ConstraintLayout) findViewById(R.id.clContent);
        vpContent = (ViewPager) findViewById(R.id.vpContent);
        clEjemplos = (ConstraintLayout) findViewById(R.id.clEjemplos);
        clEjemploEs = (ConstraintLayout) findViewById(R.id.clEjemploEs);
        clEjemploNl = (ConstraintLayout) findViewById(R.id.clEjemploNl);
        ibBack = (ImageButton) findViewById(R.id.ibBack);
        ivBaseVerb = (ImageView) findViewById(R.id.ivBaseVerb);
        ivExampleVerb = (ImageView) findViewById(R.id.ivExampleVerb);
        ibSave = (ImageButton) findViewById(R.id.ibSave);

        Picasso.with(this).load(Common.allLanguages.get(Common.getSystemLanguage())).into(ivExampleVerb);

        LocalBroadcastManager.getInstance(this).registerReceiver(success, new IntentFilter("SUCCESS"));
        LocalBroadcastManager.getInstance(this).registerReceiver(errors, new IntentFilter("ERROR"));
        LocalBroadcastManager.getInstance(this).registerReceiver(update, new IntentFilter("UPDATE"));

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

        ibSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (API.checkIfWordIsInFav(palabraId)) {
                    Picasso.with(DetailsActivity.this).load(android.R.drawable.btn_star_big_off).into(ibSave);
                    API.deleteWordFromFav(palabraId);
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.deleted_from_favs), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(DetailsActivity.this, getResources().getString(R.string.saved_in_fav), Toast.LENGTH_SHORT).show();
                    Picasso.with(DetailsActivity.this).load(android.R.drawable.btn_star_big_on).into(ibSave);
                    API.saveWordToFav(palabraId, palabra, Common.getBaseVerblanguage());
                }
            }
        });

    }

    // Recibidor
    private BroadcastReceiver success = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, final Intent intent) {

            // Checkeamos si existe
            if (API.checkIfWordIsInFav(palabraId)) {
                Log.e("RM", "existe!");
                Picasso.with(DetailsActivity.this).load(android.R.drawable.btn_star_big_on).into(ibSave);
            } else {
                Log.e("RM", "no existe");
                Picasso.with(DetailsActivity.this).load(android.R.drawable.btn_star_big_off).into(ibSave);
            }

            pbLoading.setVisibility(View.GONE);
            clHeaderContent.setVisibility(View.VISIBLE);
            vpContent.setVisibility(View.VISIBLE);

            HashMap<String, Palabra> params = (HashMap<String, Palabra>) intent.getSerializableExtra("map");
            tvWord.setText(palabra.toUpperCase());

            final Palabra palabra = params.get("palabra");

            VerbsPageAdapter verbsPageAdapter = new VerbsPageAdapter(getSupportFragmentManager(), new Fragment[]{new FragmentModoIndicativo(palabra.getModoIndicativo()), new FragmentModoSubjuntivo(palabra.getModoSubjuntivo()), new FragmentModoCondicional(palabra.getModoCondicional()), new FragmentModoImperativo(palabra.getModoImperativo())});
            vpContent.setAdapter(verbsPageAdapter);

            if (palabra.getEjemplo() == null) {
//                Toast.makeText(context, "No hay ejemplos", Toast.LENGTH_SHORT).show();
                //No hay ejemplos
            } else {

                // Un ejemplo en espanol
                clEjemplos.setVisibility(View.VISIBLE);
                tvEjemploEs.setText(palabra.getEjemplo().getEjemplosEs().get(0));
                tvEjemploNl.setText(palabra.getEjemplo().getEjemplosNl().get(0));

                // Varios ejemplos en espanol

                clEjemplos.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        indexVerb = indexVerb + 1 == palabra.getEjemplo().getEjemplosEs().size() ? 0 : indexVerb + 1;
                        tvEjemploEs.setText(palabra.getEjemplo().getEjemplosEs().get(indexVerb));
                        tvEjemploNl.setText(palabra.getEjemplo().getEjemplosNl().get(indexVerb));

                    }
                });


                // https://www.android-arsenal.com/details/1/5828
//                Sequent.origin(clEjemploEs).start();

            }

        }


    };

    // Ops! actualiza tu app!
    private BroadcastReceiver update = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startActivity(new Intent(DetailsActivity.this, UpdateAppActivity.class));
            finish();
        }
    };

    // Clasificacion de errores
    private BroadcastReceiver errors = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            int typeError = intent.getIntExtra("RESP", 0);

            switch (typeError) {
                case 0:
                    Picasso.with(context).load(R.drawable.unknow_error).into(ivError);
                    tvError.setText(getResources().getString(R.string.unknow_error));
                    break;
                case 1:
                    Picasso.with(context).load(R.drawable.no_network).into(ivError);
                    tvError.setText(getResources().getString(R.string.no_internet_access));
                    break;
                case 2:
                    // Solo cuando el te deniega el acceso
                    Picasso.with(context).load(R.drawable.server_error).into(ivError);
                    tvError.setText(getResources().getString(R.string.server_off));
                    break;
                case 3:
                    // Tu internet es lento o no se puede conectar con el servidor
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
