package com.everonet.demo.miniprograms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.everonet.demo.miniprograms.R;
import com.everonet.demo.miniprograms.model.MiniAppRespone;

import java.util.List;


public class MiniListRecyclerViewAdapter extends RecyclerView.Adapter<MiniListRecyclerViewAdapter.MiniListRVHolder> {

    private LayoutInflater mLayoutInflater;
    private Context mContext;
    List<MiniAppRespone.ResultEntity> miniAppList;

    private OnItemClickListener onItemClickListener;

    public MiniListRecyclerViewAdapter(Context context, List<MiniAppRespone.ResultEntity> orderList) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.miniAppList = orderList;
    }

    @Override
    public MiniListRVHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new MiniListRVHolder(mLayoutInflater.inflate(R.layout.item_mini_app, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(MiniListRVHolder holder, int position) {
        MiniAppRespone.ResultEntity miniAppInfo = miniAppList.get(position);

        if (miniAppInfo != null) {
//            Glide.with(mContext).load(miniAppInfo.getIcon_uri()).centerCrop().into(holder.appIcon);
            glideImage(miniAppInfo.getIcon_uri(), holder.appIcon);
            holder.appName.setText(miniAppInfo.getDisplay_name());
        }
    }

    @Override
    public int getItemCount() {
        return miniAppList == null ? 0 : miniAppList.size();
    }

    class MiniListRVHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView appName;
        ImageView appIcon;
        LinearLayout llItem;

        public MiniListRVHolder(View itemView) {
            super(itemView);
            appName = (TextView) itemView.findViewById(R.id.tx_mini_name);
            appIcon = (ImageView) itemView.findViewById(R.id.iv_mini_logo);

            llItem = (LinearLayout) itemView.findViewById(R.id.ll_item);
            llItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.ll_item:
                    if (null != onItemClickListener) {
                        onItemClickListener.onItemClicked(view, getAdapterPosition());
                    }
                    break;
            }
        }
    }

    public void addAll(List<MiniAppRespone.ResultEntity> list) {
        miniAppList.clear();
        miniAppList.addAll(list);
        notifyItemRangeChanged(0, list.size());
    }

    public void addMore(List<MiniAppRespone.ResultEntity> list) {
        miniAppList.addAll(list);
        notifyItemRangeChanged(getItemCount() - 1, list.size());

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClicked(View v, int position);
    }

    public void glideImage(String url, ImageView iv) {
        RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_image_loading);
        Glide.with(mContext)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .apply(options)
                .into(iv);
    }
}
