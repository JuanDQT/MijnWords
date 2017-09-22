package com.juandqt.mijnwords.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juandqt.mijnwords.HomeActivity;
import com.juandqt.mijnwords.R;
import com.juandqt.mijnwords.models.PalabraSearch;

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
    public void onBindViewHolder(PalabraViewHolder holder, final int position) {
        holder.tvVerbo.setText(list.get(position).getName());
        holder.tvIdWord.setText(list.get(position).getId() + "");

        SwipeActionView swipeView = (SwipeActionView) holder.itemView.findViewById(R.id.savItem);
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvVerbo = (TextView) v.findViewById(R.id.tvVerb);
                ((HomeActivity)context).selectedWord(tvVerbo.getText().toString());
            }
        });

        swipeView.setSwipeGestureListener(new SwipeGestureListener() {
            @Override
            public boolean onSwipedLeft(@NotNull SwipeActionView swipeActionView) {
                TextView tvIdWord = (TextView) swipeActionView.findViewById(R.id.tvIdWord);
                ((HomeActivity)context).removeVerbHistory(position, tvIdWord.getText().toString());
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
        private TextView tvIdWord;
        public PalabraViewHolder(View itemView) {
            super(itemView);
            tvVerbo = (TextView) itemView.findViewById(R.id.tvVerb);
            tvIdWord = (TextView) itemView.findViewById(R.id.tvIdWord);
        }
    }


}