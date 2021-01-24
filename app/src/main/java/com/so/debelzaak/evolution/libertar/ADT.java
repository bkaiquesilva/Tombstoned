package com.so.debelzaak.evolution.libertar;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ADT extends BaseAdapter {
    private Context mContext;

    int[] images= {R.drawable.emojq, R.drawable.emoju, R.drawable.emojr, R.drawable.emojt,
            R.drawable.emojs,
            R.drawable.emoja, R.drawable.emojb, R.drawable.emojc, R.drawable.emojd,
            R.drawable.emoje, R.drawable.emojf, R.drawable.emojg, R.drawable.emojh,
            R.drawable.emoji, R.drawable.emojj, R.drawable.emojk, R.drawable.emojl,
            R.drawable.emojm, R.drawable.emojn, R.drawable.emojo, R.drawable.emojp
    };
    public ADT(Context c){
        this.mContext=c;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int position) {
        return images[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView=new ImageView(mContext);
        imageView.setImageResource(images[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(64,64));

        return imageView;
    }
}
