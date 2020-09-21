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

import de.hdodenhof.circleimageview.CircleImageView;

public class FRAdapter extends RecyclerView.Adapter<FRAdapter.FRViewHolder> {
    private static OnClick onClick;
    private Context ctx;
    private List<UserModel> userModels;

    public FRAdapter(Context ctx, List<UserModel> userModels) {
        this.ctx = ctx;
        this.userModels = userModels;
    }

    @NonNull
    @Override
    public FRViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FRViewHolder ( LayoutInflater.from ( ctx ).inflate ( R.layout.ff_list_item, parent, false ) );
    }

    @Override
    public void onBindViewHolder(@NonNull FRViewHolder holder, int position) {
        String pp = userModels.get ( position ).getProfilePic ( );
        if ( pp != null ) {
            Picasso.with ( ctx ).load ( pp ).networkPolicy ( NetworkPolicy.OFFLINE ).into ( holder.imageView,
                    new Callback ( ) {
                        @Override
                        public void onSuccess() {
                            // Picasso.with ( ctx ).load ( pp ).error ( R.drawable.ic_baseline_account_circle_24 ).into ( holder.imageView );
                        }

                        @Override
                        public void onError() {
                            Picasso.with ( ctx ).load ( pp ).error ( R.drawable.ic_baseline_account_circle_24 ).into ( holder.imageView );
                        }
                    } );
        }
        holder.textView.setText ( userModels.get ( position ).getName ( ) );
    }

    @Override
    public int getItemCount() {
        return userModels.size ( );
    }

    public void setOnItemClickListener(OnClick onItemClickListener) {
        FRAdapter.onClick = onItemClickListener;
    }

    interface OnClick {
        public void listener(int position, View view);
    }

    public class FRViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView imageView;
        private TextView textView;

        public FRViewHolder(@NonNull View itemView) {
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
