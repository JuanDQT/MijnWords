/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.juandqt.mijnwords;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.juandqt.mijnwords.models.Palabra;

/**
 * A fragment representing a single step in a wizard. The fragment shows a dummy title indicating
 * the page number, along with some dummy text.
 */
public class ScreenSlidePageFragment extends android.support.v4.app.Fragment implements View.OnClickListener {
    /**
     * The argument key for the page number this fragment represents.
     */
    public static final String ARG_PAGE = "page";
    public static Palabra mPalabra = null;

    private RelativeLayout rlRoot;

    private String[] modos = new String[]{
            Common.getContext().getString(R.string.modo_indicativo),
            Common.getContext().getString(R.string.modo_subjuntivo),
            Common.getContext().getString(R.string.modo_condicional),
            Common.getContext().getString(R.string.modo_imperativo),
    };
    private int defaultSize;
    private static final int MAX_HEIGHT = 800;
    private static final int DURATION_TRANSITION = 600;
    private static final int TIEMPOS_MODO_INDICATIVO = 4;
    private static final int TIEMPOS_MODO_SUBJUNTIVO = 3;
    private static final int TIEMPOS_MODO_CONDICIONAL = 1;
    private static final int TIEMPOS_MODO_IMPERATIVO = 2;
    /**
     * The fragment's page number, which is set to the argument value for {@link #ARG_PAGE}.
     */
    private int mPageNumber;

    /**
     * Factory method for this fragment class. Constructs a new fragment for the given page number.
     */
    public static ScreenSlidePageFragment create(int pageNumber) {

        ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ScreenSlidePageFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt(ARG_PAGE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("SIZE", "ACT: " + mPalabra.getEjemplo());
        // Inflate the layout containing a title and body text.
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        // Set the title view to show the page number.

        rlRoot = (RelativeLayout) rootView.findViewById(R.id.rlRoot);
        RelativeLayout rlModo = (RelativeLayout) rlRoot.getChildAt(0);
        ConstraintLayout clTiempo = (ConstraintLayout) rlModo.getChildAt(1);

        defaultSize = clTiempo.getLayoutParams().height;

        for (int i = 0; i < rlRoot.getChildCount(); i++) {

            Log.e("SIZE", "Modos: " + i);
            RelativeLayout rlModos = (RelativeLayout) rlRoot.getChildAt(i);
            rlModos.setVisibility(View.GONE);

            // 5?

            for (int j = 0; j < rlModos.getChildCount(); j++) {

                if (rlModos.getChildAt(j) instanceof TextView) {
                    TextView title = (TextView) rlModos.getChildAt(j);
                    title.setText(modos[mPageNumber]);
                }

                if (rlModos.getChildAt(j) instanceof ConstraintLayout) {
                    ConstraintLayout clVerbos = (ConstraintLayout) rlModos.getChildAt(j);
                    TextView tvTiempo = (TextView) clVerbos.getChildAt(0);
                    tvTiempo.setOnClickListener(this);

                }
            }

            updateVerbosContent(rlModos, i);


        }

        rlRoot.getChildAt(mPageNumber).setVisibility(View.VISIBLE);

        return rootView;
    }

    // Animamos el escalado de la vista
    @Override
    public void onClick(View v) {

        if (((ConstraintLayout) v.getParent()).getMeasuredHeight() == defaultSize) {
            expand(((ConstraintLayout) v.getParent()), DURATION_TRANSITION, MAX_HEIGHT);
        } else {
            collapse(((ConstraintLayout) v.getParent()), DURATION_TRANSITION, defaultSize);
        }
    }

    public void updateVerbosContent(RelativeLayout relativeLayout, int index) {

        switch (index) {
            case 0:
                // Modo indicativo
                // In loop, los 4 tiemposw
                for(int j = 0; j < TIEMPOS_MODO_INDICATIVO; j++) {
                    ConstraintLayout clVerbos = (ConstraintLayout) relativeLayout.getChildAt(j+1);
                    for (int i = 0; i < mPalabra.getModoIndicativo().getPresente().size(); i++) {
                        // +1 porque la vista en el layout esta una posicion mas abajo
                        TextView tvVerbo = (TextView) clVerbos.getChildAt(i + 1);
                        tvVerbo.setText(mPalabra.getModoIndicativo().getAllTimes().get(j).get(i));
                    }
                }
                break;
            case 1:
                // In loop, los 3 tiempos
                for(int j = 0; j < TIEMPOS_MODO_SUBJUNTIVO; j++) {
                    ConstraintLayout clVerbos = (ConstraintLayout) relativeLayout.getChildAt(j+1);
                    for (int i = 0; i < mPalabra.getModoSubjuntivo().getPresente().size(); i++) {
                        // +1 porque la vista en el layout esta una posicion mas abajo
                        TextView tvVerbo = (TextView) clVerbos.getChildAt(i + 1);
                        tvVerbo.setText(mPalabra.getModoSubjuntivo().getAllTimes().get(j).get(i));
                    }
                }
                break;
            case 2:
                // In loop, los 3 tiempos
                for(int j = 0; j < TIEMPOS_MODO_CONDICIONAL; j++) {
                    ConstraintLayout clVerbos = (ConstraintLayout) relativeLayout.getChildAt(j+1);
                    for (int i = 0; i < mPalabra.getModoCondicional().getCondicional().size(); i++) {
                        // +1 porque la vista en el layout esta una posicion mas abajo
                        TextView tvVerbo = (TextView) clVerbos.getChildAt(i + 1);
                        tvVerbo.setText(mPalabra.getModoCondicional().getAllTimes().get(j).get(i));
                    }
                }
                break;
            case 3:
                // In loop, los 2 tiempos
                for(int j = 0; j < TIEMPOS_MODO_IMPERATIVO; j++) {
                    ConstraintLayout clVerbos = (ConstraintLayout) relativeLayout.getChildAt(j+1);
                    for (int i = 0; i < mPalabra.getModoImperativo().getAfirmativo().size(); i++) {
                        Log.e("NEW", "Holi " + i);
                        // +1 porque la vista en el layout esta una posicion mas abajo
                        TextView tvVerbo = (TextView) clVerbos.getChildAt(i + 1);
                        tvVerbo.setText(mPalabra.getModoImperativo().getAllTimes().get(j).get(i));
                    }
                }
                break;
        }
    }

    // Test
    public static void expand(final View v, int duration, int targetHeight) {

        int prevHeight = v.getHeight();

        v.setVisibility(View.VISIBLE);
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });

        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }

    public static void collapse(final View v, int duration, int targetHeight) {
        int prevHeight = v.getHeight();
        ValueAnimator valueAnimator = ValueAnimator.ofInt(prevHeight, targetHeight);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                v.getLayoutParams().height = (int) animation.getAnimatedValue();
                v.requestLayout();
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        valueAnimator.setDuration(duration);
        valueAnimator.start();
    }


}
