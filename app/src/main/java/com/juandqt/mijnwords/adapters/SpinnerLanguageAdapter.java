package com.juandqt.mijnwords.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by juandaniel on 21/8/17.
 */

public class SpinnerLanguageAdapter extends ArrayAdapter<String> {

    private String[] languages;
    private int[] images;
    private Context context;


    public SpinnerLanguageAdapter(@NonNull Context context, @LayoutRes int resource) {
        super(context, resource);
        this.context = context;
        this.languages = new String[]{"EN", "NL"};
//        this.images = new int[]{context.getResources().getDrawable(R.drawable.es_lang), context.getResources().getDrawable(R.drawable.nl_lang)};
        System.out.println(android.R.string.cancel);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        View view = LayoutInflater.from(context).inflate(R.layout.sp_languages, parent);
//        TextView tvLanguage = (TextView) view.findViewById(R.id.tvLanguage);
//        ImageView ivFlag = (ImageView) view.findViewById(R.id.ivFlag);
//        tvLanguage.setText(languages[position]);
//        Picasso.with(context).load(images[position]).into(ivFlag);

        return null;
    }
}
