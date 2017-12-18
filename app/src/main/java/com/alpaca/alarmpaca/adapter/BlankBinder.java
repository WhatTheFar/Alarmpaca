package com.alpaca.alarmpaca.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ahamed.multiviewadapter.BaseViewHolder;
import com.ahamed.multiviewadapter.ItemBinder;
import com.alpaca.alarmpaca.R;

import java.util.ArrayList;
import java.util.List;


public class BlankBinder extends ItemBinder<BlankBinder.BlankItem, BlankBinder.ViewHolder> {

    @Override
    public ViewHolder create(LayoutInflater layoutInflater, ViewGroup parent) {
        return new ViewHolder(layoutInflater.inflate(R.layout.item_simple_blank, parent, false));
    }

    @Override
    public void bind(ViewHolder holder, BlankItem item) {
    }

    @Override
    public boolean canBindData(Object item) {
        return item instanceof BlankItem;
    }

    @Override
    public int getSpanSize(int maxSpanCount) {
        return maxSpanCount;
    }

    @SuppressWarnings("deprecation")
    static class ViewHolder extends BaseViewHolder<BlankItem> {

        ViewHolder(View itemView) {
            super(itemView);

        }
    }

    public static class BlankItem {

        public static List<BlankItem> getOneBlankItemList() {
            List<BlankBinder.BlankItem> blankItemList = new ArrayList<>();
            blankItemList.add(new BlankBinder.BlankItem());
            return blankItemList;
        }

    }
}

