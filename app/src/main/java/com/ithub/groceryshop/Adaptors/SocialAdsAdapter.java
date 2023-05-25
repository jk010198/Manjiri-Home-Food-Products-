package com.ithub.groceryshop.Adaptors;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ithub.groceryshop.Models.SocialAdsModel;
import com.ithub.groceryshop.R;

import java.util.List;


public class SocialAdsAdapter extends ArrayAdapter<SocialAdsModel> {

    private Activity context;
    public static List<SocialAdsModel> adsList;
    public static String name, add;

    public SocialAdsAdapter(Activity context, List<SocialAdsModel> adslist) {
        super(context, R.layout.row_list_social_ads, adslist);
        this.context = context;
        this.adsList = adslist;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();

        View listview = inflater.inflate(R.layout.row_list_social_ads, null, true);

        TextView tv_company_name = listview.findViewById(R.id.tv_ads_title);
        TextView tv_company_contact = listview.findViewById(R.id.tv_ads_contactnumber);
        TextView tv_company_alternetcontact = listview.findViewById(R.id.tv_ads_alternet_contactnumber);

        ImageView imageView = listview.findViewById(R.id.iv_ads_img);

        SocialAdsModel sam = adsList.get(position);

        tv_company_name.setText(sam.getBusiness_name());
        tv_company_contact.setText(sam.getBusiness_contact());
        tv_company_alternetcontact.setText(sam.getAlternet_contact());

        if (sam.getImage().equals("")){
            Glide
                    .with(context)
                    .load(R.drawable.advertisement_img)
                    .into(imageView);
        } else{
            Glide
                    .with(context)
                    .load(sam.getImage())
                    .into(imageView);
        }

        return listview;
    }
}