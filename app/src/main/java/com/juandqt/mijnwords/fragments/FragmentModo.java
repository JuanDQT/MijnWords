package com.juandqt.mijnwords.fragments;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Dimension;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.juandqt.mijnwords.Common;
import com.juandqt.mijnwords.R;
import com.juandqt.mijnwords.models.Modo;

import java.util.Locale;

/**
 * Created by juandaniel on 17/8/17.
 */

@SuppressLint("ValidFragment")
public class FragmentModo extends Fragment implements View.OnClickListener {

    private ScrollView view;

    private static final int DURATION_TRANSITION = 650;
    private Modo modo;
    private TextToSpeech textToSpeech;
    private static final int MAX_HEIGHT_TEXTVIEW = 75;
    private static final int MAX_MARGIN_TOP_TEXTVIEW = 20;
    private static final int MAX_MARGIN_LEFT_RIGHT_TEXTVIEW = 75;
    private static final int FONT_SIZE_TEXTVIEW = 18;
    private static int maxScrollHeight = 0;
    private static final int MIN_SCROLL_HEIGHT = 100;

    @SuppressLint("ValidFragment")
    public FragmentModo(Modo modo) {
        this.modo = modo;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = (ScrollView) LayoutInflater.from(getActivity()).inflate(R.layout.fragent_modo, null);

        RelativeLayout rlModo = (RelativeLayout) view.getChildAt(0);
        TextView tvModo = (TextView) rlModo.getChildAt(0);
        tvModo.setText(modo.getTitle());

        int maxHeight = 0;
        // debug
        final TextView textView = new TextView(getContext());
        textView.setTextSize(Dimension.SP, FONT_SIZE_TEXTVIEW);
        int sizeDebug = textView.getLineHeight();
        //
        maxScrollHeight = MIN_SCROLL_HEIGHT + (modo.getPersons().size() * sizeDebug) + (modo.getPersons().size() * MAX_MARGIN_TOP_TEXTVIEW) + MAX_MARGIN_TOP_TEXTVIEW;

        for (int i = 0; i < modo.getAllVerbs().size(); i++) {

            ConstraintLayout clRow = new ConstraintLayout(getContext());
            ConstraintSet constraintSetTime = new ConstraintSet();
            clRow.setId(View.generateViewId());
            clRow.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            maxHeight = (i == 0) ? maxScrollHeight : MIN_SCROLL_HEIGHT;

            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, maxHeight);
            relativeParams.addRule(RelativeLayout.BELOW, rlModo.getChildAt(rlModo.getChildCount() - 1).getId());
            relativeParams.setMargins(0, i == 0 ? 0 : 5, 0, 0);
            clRow.setLayoutParams(relativeParams);
            rlModo.addView(clRow);

            // Consultamos cuantas filas de verbos tiene: hay de 5 o 6 o mas...
            TextView tvTiempo = new TextView(getContext());
            tvTiempo.setId(View.generateViewId());
            clRow.addView(tvTiempo);

            tvTiempo.setText(modo.getAllVerbs().get(i).getTiempo());
            tvTiempo.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            tvTiempo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvTiempo.setTextSize(Dimension.SP, 20);
            tvTiempo.setTextColor(Color.WHITE);
            tvTiempo.setGravity(Gravity.CENTER);
            tvTiempo.setOnClickListener(this);

            constraintSetTime.constrainHeight(tvTiempo.getId(), MIN_SCROLL_HEIGHT);
            constraintSetTime.connect(tvTiempo.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
            constraintSetTime.connect(tvTiempo.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
            constraintSetTime.connect(tvTiempo.getId(), ConstraintSet.TOP, clRow.getId(), ConstraintSet.TOP, 0);

            // Creamos los TV 12, 6 filas de 2
            // Iteramos los verbos y cogemos las persons

            // FIXME: Error con ingles!
            for (int k = 0; k < modo.getAllVerbs().get(i).getVerbs().size(); k++) {

                Log.e("PIP", modo.getAllVerbs().get(i).getVerbs().toString());
                TextView tvPerson = new TextView(getContext());
                tvPerson.setId(View.generateViewId());
                tvPerson.setText(modo.getPersons().get(k));
                tvPerson.setTextColor(Color.WHITE);
                tvPerson.setTextSize(Dimension.SP, FONT_SIZE_TEXTVIEW);

                tvPerson.setGravity(View.TEXT_ALIGNMENT_CENTER);
                clRow.addView(tvPerson);

                constraintSetTime.constrainWidth(tvPerson.getId(), ConstraintSet.WRAP_CONTENT);
                constraintSetTime.constrainHeight(tvPerson.getId(), sizeDebug);
                constraintSetTime.connect(tvPerson.getId(), ConstraintSet.TOP, clRow.getChildAt(k).getId(), ConstraintSet.BOTTOM, MAX_MARGIN_TOP_TEXTVIEW);
                constraintSetTime.connect(tvPerson.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
                constraintSetTime.setMargin(tvPerson.getId(), ConstraintSet.START, MAX_MARGIN_LEFT_RIGHT_TEXTVIEW);

            }

            for (int k = 0; k < modo.getAllVerbs().get(i).getVerbs().size(); k++) {

                // TextView verbo
                final TextView tvVerb = new TextView(getContext());
                tvVerb.setId(View.generateViewId());
                tvVerb.setText(modo.getAllVerbs().get(i).getVerbs().get(k));
                tvVerb.setTextColor(Color.WHITE);
                tvVerb.setTextSize(Dimension.SP, FONT_SIZE_TEXTVIEW);
                tvVerb.setGravity(View.TEXT_ALIGNMENT_CENTER);
                clRow.addView(tvVerb);

                constraintSetTime.constrainWidth(tvVerb.getId(), ConstraintSet.WRAP_CONTENT);
                constraintSetTime.constrainHeight(tvVerb.getId(), MAX_HEIGHT_TEXTVIEW);
                constraintSetTime.connect(tvVerb.getId(), ConstraintSet.TOP, clRow.getChildAt(k).getId(), ConstraintSet.BOTTOM, MAX_MARGIN_TOP_TEXTVIEW);
                constraintSetTime.connect(tvVerb.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
                constraintSetTime.setMargin(tvVerb.getId(), ConstraintSet.END, MAX_MARGIN_LEFT_RIGHT_TEXTVIEW);

                tvVerb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                            @Override
                            public void onInit(int status) {

                                if (status == TextToSpeech.SUCCESS) {
                                    Locale locSpanish = new Locale(Common.getBaseLanguage());
                                    int result = textToSpeech.setLanguage(locSpanish);
                                    // tts.setPitch(5); // set pitch level
                                    // tts.setSpeechRate(2); // set speech speed rate
                                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                        Log.e("TTS", "Language is not supported");
                                    } else {

                                        if (!textToSpeech.isSpeaking()){
                                            textToSpeech.speak(tvVerb.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
                                        }
                                    }


                                }
                            }
                        });

                    }
                });
            }

            clRow.setConstraintSet(constraintSetTime);
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        if (((ConstraintLayout) v.getParent()).getMeasuredHeight() > MIN_SCROLL_HEIGHT) {
            Common.collapse(((ConstraintLayout) v.getParent()), DURATION_TRANSITION, MIN_SCROLL_HEIGHT);
        } else {
            Common.expand(((ConstraintLayout) v.getParent()), DURATION_TRANSITION, maxScrollHeight);
        }
    }

}