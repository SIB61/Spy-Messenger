package com.sib4u.spymessenger;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class FFAdapter extends RecyclerView.Adapter<FFAdapter.FFViewHolder> {
    private static OnClick onClick;
    private Context ctx;
    private List<Map<String, Object>> maps;

    public FFAdapter(Context ctx, List<Map<String, Object>> maps) {
        this.ctx = ctx;
        this.maps = maps;
    }

    @NonNull
    @Override
    public FFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FFViewHolder ( LayoutInflater.from ( ctx ).inflate ( R.layout.ff_list_item, parent, false ) );
    }

    @Override
    public void onBindViewHolder(@NonNull FFViewHolder holder, int position) {
        String pp = (String) maps.get ( position ).get ( "profilePic" );
        if ( pp != null ) {
            Picasso.get ( ).load ( pp ).networkPolicy ( NetworkPolicy.OFFLINE ).
                    placeholder ( R.drawable.ic_default_image ).into ( holder.imageView,
                    new Callback ( ) {
                        @Override
                        public void onSuccess() {
                            // Picasso.with ( ctx ).load ( pp ).error ( R.drawable.ic_baseline_account_circle_24 ).into ( holder.imageView );
                        }

                        @Override
                        public void onError(Exception e) {
                            Picasso.get ( ).load ( pp ).
                                    placeholder ( R.drawable.ic_default_image )
                                    .error ( R.drawable.ic_default_image ).into ( holder.imageView );

                        }

                    } );
        }
        holder.textView.setText ( (CharSequence) maps.get ( position ).get ( "name" ) );

    }

    @Override
    public int getItemCount() {
        return maps.size ( );
    }

    public void setOnItemClickListener(OnClick onItemClickListener) {
        FFAdapter.onClick = onItemClickListener;
    }

    interface OnClick {
        public void listener(int position, View view);
    }

    public class FFViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView textView;

        public FFViewHolder(@NonNull View itemView) {
            super ( itemView );
            imageView = itemView.findViewById ( R.id.FFImage );
            textView = itemView.findViewById ( R.id.FFName );
            itemView.setOnClickListener ( new View.OnClickListener ( ) {
                @Override
                public void onClick(View view) {
                    onClick.listener ( getAdapterPosition ( ), view );
                }
            } );

        }
    }
}
