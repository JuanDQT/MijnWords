package com.juandqt.mijnwords.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.juandqt.mijnwords.API;
import com.juandqt.mijnwords.Common;
import com.juandqt.mijnwords.HomeActivity;
import com.juandqt.mijnwords.R;
import com.juandqt.mijnwords.models.PalabraSearch;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import me.thanel.swipeactionview.SwipeActionView;
import me.thanel.swipeactionview.SwipeGestureListener;

/**
 * Created by juandaniel on 21/9/17.
 */

public class HistoricAdapter extends RecyclerView.Adapter<HistoricAdapter.PalabraViewHolder> {
    private int localView = R.layout.item_historic;
    private ArrayList<PalabraSearch> list;
    private Context context;

    public HistoricAdapter(ArrayList<PalabraSearch> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public PalabraViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(localView, parent, false);
        return new PalabraViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PalabraViewHolder holder, final int position) {
        holder.tvVerbo.setText(list.get(position).getName());
        int flagResource = context.getResources().getIdentifier(Common.allLanguages.get(list.get(position).getLanguageCode().toUpperCase()).toString(), "drawable", context.getPackageName());
        Picasso.with(context).load(flagResource).into(holder.ivFlag);
        SwipeActionView swipeView = (SwipeActionView) holder.itemView.findViewById(R.id.savItem);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvVerbo = (TextView) v.findViewById(R.id.tvVerb);

                String codePalabra = API.getPalabraCodeLanguageByString(tvVerbo.getText().toString().toLowerCase());
                ((HomeActivity) context).selectedWord(tvVerbo.getText().toString(), codePalabra);
            }
        });

        swipeView.setSwipeGestureListener(new SwipeGestureListener() {
            @Override
            public boolean onSwipedLeft(@NotNull SwipeActionView swipeActionView) {
                ((HomeActivity)context).removeVerbHistory(position, list.get(position).getName().toLowerCase());
                return true;
            }

            public boolean onSwipedRight(@NotNull SwipeActionView swipeActionView) {
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class PalabraViewHolder extends RecyclerView.ViewHolder {
        private TextView tvVerbo;
        private ImageView ivFlag;

        public PalabraViewHolder(View itemView) {
            super(itemView);
            tvVerbo = (TextView) itemView.findViewById(R.id.tvVerb);
            ivFlag = (ImageView) itemView.findViewById(R.id.ivSwipeFlag);
        }
    }


}