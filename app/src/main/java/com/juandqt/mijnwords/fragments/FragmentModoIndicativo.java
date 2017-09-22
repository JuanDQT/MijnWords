package com.juandqt.mijnwords.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juandqt.mijnwords.Common;
import com.juandqt.mijnwords.R;
import com.juandqt.mijnwords.models.ModoVerbo;

import java.util.Locale;

/**
 * Created by juandaniel on 17/8/17.
 */

@SuppressLint("ValidFragment")
public class FragmentModoIndicativo extends Fragment implements View.OnClickListener {

    private View view;

    private ConstraintLayout constraintLayout;
    private int maxHeight = 0;
    private static final int DURATION_TRANSITION = 600;
    private ModoVerbo modoVerbo;
    private TextToSpeech textToSpeech;

    @SuppressLint("ValidFragment")
    public FragmentModoIndicativo(ModoVerbo modoVerbo) {
        this.modoVerbo = modoVerbo;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = LayoutInflater.from(getActivity()).inflate(R.layout.fragent_modo_indicativo, null);

        RelativeLayout rlModo = (RelativeLayout) view.findViewById(R.id.RlModoIndicativo);

        constraintLayout = (ConstraintLayout) view.findViewById(R.id.a1);

        ViewTreeObserver vto = constraintLayout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                constraintLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                maxHeight = constraintLayout.getMeasuredHeight();

            }
        });

        for (int i = 0, index = 0; i < rlModo.getChildCount(); i++) {
            // Primer resultado + onClick
            if (rlModo.getChildAt(i) instanceof ConstraintLayout) {
                ConstraintLayout clTiempos = (ConstraintLayout) rlModo.getChildAt(i); // 1,2,3,
                TextView tvTiempo = (TextView) clTiempos.getChildAt(0);
                tvTiempo.setOnClickListener(this);

                for (int j = 0; j < modoVerbo.getPresente().size(); j++) {
                    final TextView tvVerbo = (TextView) clTiempos.getChildAt(j + 1);
                    tvVerbo.setText(modoVerbo.getAllTimes().get(index).get(j));

                    tvVerbo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                                @Override
                                public void onInit(int status) {

                                    if (status == TextToSpeech.SUCCESS) {
                                        Locale locSpanish = new Locale(Common.getBaseVerblanguage());
                                        int result = textToSpeech.setLanguage(locSpanish);
                                        // tts.setPitch(5); // set pitch level
                                        // tts.setSpeechRate(2); // set speech speed rate
                                        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                            Log.e("TTS", "Language is not supported");
                                        } else {
                                            textToSpeech.speak(tvVerbo.getText().toString(), TextToSpeech.QUEUE_FLUSH, null, null);
                                        }


                                    }
                                }
                            });
                        }
                    });
                }
                index++;
            }
        }

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