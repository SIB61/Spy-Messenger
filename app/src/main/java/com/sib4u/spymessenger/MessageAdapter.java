package com.sib4u.spymessenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public int MY_MESSAGE = 1;
    public int FRIEND_MESSAGE = 2;
    private MessageModel messageModel = new MessageModel ( );
    private Context ctx;
    private List<Map<String, Object>> messageMaps;

    public MessageAdapter(Context ctx, List<Map<String, Object>> messageMaps) {
        this.ctx = ctx;
        this.messageMaps = messageMaps;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if ( viewType == MY_MESSAGE ) {
            return new MyMessageViewHolder ( LayoutInflater.from ( ctx ).inflate ( R.layout.my_text, parent, false ) );
        } else if ( viewType == FRIEND_MESSAGE ) {
            return new FriendMessageViewHolder ( LayoutInflater.from ( ctx ).inflate ( R.layout.friend_text, parent, false ) );
        } else {
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String message = (String) messageMaps.get ( position ).get ( "message" );
        String DecryptedMessage = "failed";
        Date timestamp = (Date) messageMaps.get ( position ).get ( "timestamp" );

        try {
            DecryptedMessage = new RSAAlgo ( ).Decrypt ( message, new SharedPref ( ctx ).getPrivateKey ( ) );
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidKeySpecException | BadPaddingException e) {
            e.printStackTrace ( );
        }


        if ( getItemViewType ( position ) == MY_MESSAGE ) {
            MyMessageViewHolder holder1 = (MyMessageViewHolder) holder;
            holder1.messageTextView.setText ( DecryptedMessage );


            if ( timestamp != null ) {
                @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat ( "h:mm a" ).format ( timestamp );
                holder1.timeTextView.setText ( time );
            }
        } else {
            FriendMessageViewHolder holder1 = (FriendMessageViewHolder) holder;
            holder1.messageTextView.setText ( DecryptedMessage );
            if ( timestamp != null ) {
                @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat ( "h:mm a" ).format ( timestamp );
                holder1.timeTextView.setText ( time );
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageMaps.size ( );
    }

    @Override
    public int getItemViewType(int position) {
        long i = (long) messageMaps.get ( position ).get ( "type" );
        if ( i == 1 ) {
            return MY_MESSAGE;
        } else {
            return FRIEND_MESSAGE;
        }
    }

    public class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timeTextView;

        public MyMessageViewHolder(@NonNull View itemView) {
            super ( itemView );
            messageTextView = itemView.findViewById ( R.id.MyMessage );
            timeTextView = itemView.findViewById ( R.id.MyTime );
        }
    }

    public class FriendMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageTextView, timeTextView;

        public FriendMessageViewHolder(@NonNull View itemView) {
            super ( itemView );
            messageTextView = itemView.findViewById ( R.id.FriendMessage );
            timeTextView = itemView.findViewById ( R.id.FriendTime );
        }
    }

}
