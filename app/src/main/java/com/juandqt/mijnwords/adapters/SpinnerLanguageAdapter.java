package com.juandqt.mijnwords.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.juandqt.mijnwords.R;
import com.squareup.picasso.Picasso;

/**
 * Created by juandaniel on 21/8/17.
 */

public class SpinnerLanguageAdapter extends ArrayAdapter {
    private String[] languages;
    private int[] images;
    private Context context;


    public SpinnerLanguageAdapter(@NonNull Context context, String[] languages, int[] images) {
        super(context, R.layout.sp_language,languages);
        this.context = context;
        this.languages = languages;
        this.images = images;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.sp_language, parent, false);
        TextView tvLanguage = (TextView) view.findViewById(R.id.tvCountry);
        ImageView ivFlag = (ImageView) view.findViewById(R.id.ivFlag);
        tvLanguage.setText(languages[position]);
        Picasso.with(context).load(images[position]).into(ivFlag);

        return view;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.sp_language, parent, false);
        TextView tvLanguage = (TextView) view.findViewById(R.id.tvCountry);
        ImageView ivFlag = (ImageView) view.findViewById(R.id.ivFlag);
        tvLanguage.setText(languages[position]);

        Picasso.with(context).load(images[position]).into(ivFlag);

        return view;
    }


}
