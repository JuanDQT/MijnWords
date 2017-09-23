package com.juandqt.mijnwords.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.juandqt.mijnwords.Common;
import com.juandqt.mijnwords.R;
import com.juandqt.mijnwords.models.Modo;

/**
 * Created by juandaniel on 17/8/17.
 */

@SuppressLint("ValidFragment")
public class FragmentModo extends Fragment implements View.OnClickListener {

    private ScrollView view;

    private ConstraintLayout constraintLayout;
    private int maxHeight = 0;
    private static final int DURATION_TRANSITION = 600;
    private Modo modo;
    private TextToSpeech textToSpeech;

    @SuppressLint("ValidFragment")
    public FragmentModo(Modo modo) {
        this.modo = modo;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ScrollView) LayoutInflater.from(getActivity()).inflate(R.layout.fragent_modo_indicativo, null);

        RelativeLayout rlModo = (RelativeLayout) view.getChildAt(0);
        rlModo.setBackgroundColor(Color.BLACK);
        TextView tvModo = (TextView) rlModo.getChildAt(0);
        tvModo.setText(modo.getTitle());

        int[] colores = {Color.CYAN, Color.GREEN, Color.BLUE, Color.YELLOW};


        int numeroRows = 0;
        int maxHeight = 0;
        for (int i = 0; i < modo.getAllVerbs().size(); i++) {
            numeroRows++;

            ConstraintLayout clRow = new ConstraintLayout(getContext());
            clRow.setId(View.generateViewId());
            clRow.setBackgroundColor(colores[i]);

            maxHeight = (i == 0) ? 420 : 110;

            RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, maxHeight);
            params4.addRule(RelativeLayout.BELOW, rlModo.getChildAt(rlModo.getChildCount() - 1).getId());
            Log.e("NVS", "THIS: " + clRow.getId() + ", Below: " + rlModo.getChildAt(rlModo.getChildCount() - 1).getId());
            clRow.setLayoutParams(params4);

            rlModo.addView(clRow);

            // Consultamos cuantas filas de verbos tiene: hay de 5 o 6 o mas...
            for (int j = 0; j < modo.getAllVerbs().size(); j++) {

                Log.e("SAPE", i + "_" + j);
                TextView tvTiempo = new TextView(getContext());
                clRow.addView(tvTiempo);
                tvTiempo.setText(modo.getAllVerbs().get(i).getTiempo());
                tvTiempo.setBackgroundColor(Color.WHITE);
                tvTiempo.setId(View.generateViewId());

                ConstraintSet set = new ConstraintSet();
//                set.connect(tvTiempo.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, 0);
//                set.connect(tvTiempo.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, 120);
                set.constrainHeight(tvTiempo.getId(), 150);
                // TODO: revisar el ultimo parametro, no es margin!
                set.connect(tvTiempo.getId(),ConstraintSet.LEFT,clRow.getId(),ConstraintSet.LEFT);
//                set.centerVertically(tvTiempo.getId(), clRow.getId());
                set.applyTo(clRow);
                clRow.setConstraintSet(set);

                break;
            }

        }

        Log.e("NVS", "hijos: " + rlModo.getChildCount());
/*
        Toast.makeText(getActivity(), "ejecutamos: " + numeroRows, Toast.LENGTH_SHORT).show();

        ConstraintLayout clRow = new ConstraintLayout(getContext());
        clRow.setId(View.generateViewId());
        clRow.setBackgroundColor(Color.BLACK);

        RelativeLayout.LayoutParams params4 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,100);
        params4.addRule(RelativeLayout.BELOW, tvModo.getId());
        clRow.setLayoutParams(params4);

        rlModo.addView(clRow);
*/


//        constraintLayout = (ConstraintLayout) view.findViewById(R.id.a1);
//        ViewTreeObserver vto = constraintLayout.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                constraintLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                maxHeight = constraintLayout.getMeasuredHeight();
//
//            }
//        });

//        for (int i = 0, index = 0; i < rlModo.getChildCount(); i++) {
//            // Primer resultado + onClick
//            if (rlModo.getChildAt(i) instanceof ConstraintLayout) {
//                ConstraintLayout clTiempos = (ConstraintLayout) rlModo.getChildAt(i); // 1,2,3,
//                TextView tvTiempo = (TextView) clTiempos.getChildAt(0);
//                tvTiempo.setOnClickListener(this);
//
//                for (int j = 0; j < modoVerbo.getPresente().size(); j++) {
//                    final TextView tvVerbo = (TextView) clTiempos.getChildAt(j + 1);
//                    tvVerbo.setText(modoVerbo.getAllTimes().get(index).get(j));
//
//                    tvVerbo.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
//                                @Override
//                                public void onInit(int status) {
//
//                                    if (status == TextToSpeech.SUCCESS) {
//                                        Locale locSpanish = new Locale(Common.getBaseVerblanguage());
//                                        int result = textToSpeech.setLanguage(locSpanish);
//                                        // tts.setPitch(5); // set pitch level
//                                        // tts.setSpeechRate(2); // set speech speed rate
//                                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
//                                            Log.e("TTS", "Language is not supported");
//                                        } else {
//                                            textToSpeech.speak(tvVerbo.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
//                                        }
//
//
//                                    }
//                                }
//                            });
//                        }
//                    });
//                }
//                index++;
//            }
//        }

        return view;
    }

    @Override
    public void onClick(View v) {

        if (((ConstraintLayout) v.getParent()).getMeasuredHeight() == maxHeight) {
            Common.collapse(((ConstraintLayout) v.getParent()), DURATION_TRANSITION, v.getMeasuredHeight());
        } else {
            Common.expand(((ConstraintLayout) v.getParent()), DURATION_TRANSITION, maxHeight);
        }
    }

}