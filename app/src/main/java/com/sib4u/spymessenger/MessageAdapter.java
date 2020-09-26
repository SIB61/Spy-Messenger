package com.sib4u.spymessenger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public int MY_MESSAGE = 1;
    public int FRIEND_MESSAGE = 2;
    private MessageModel messageModel = new MessageModel ( );
    private Context ctx;
    private List<MessageModel> messageModels;

    public MessageAdapter(Context ctx, List<MessageModel> messageModels) {
        this.ctx = ctx;
        this.messageModels = messageModels;
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
        String message = (String) messageModels.get ( position ).getMessage ( );
        String DecryptedMessage = "failed";

        Timestamp timestamp = (Timestamp) messageModels.get ( position ).getTimestamp ( );

        try {
            DecryptedMessage = new RSAAlgo ( ).Decrypt ( message, new SharedPref ( ctx ).getPrivateKey ( ) );
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | InvalidKeySpecException | BadPaddingException e) {
            e.printStackTrace ( );
        }


        if ( getItemViewType ( position ) == MY_MESSAGE ) {
            MyMessageViewHolder holder1 = (MyMessageViewHolder) holder;
            holder1.messageTextView.setText ( DecryptedMessage );
            if ( timestamp != null ) {
                @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat ( "h:mm a" ).format ( timestamp.toDate ( ) );
                holder1.timeTextView.setText ( time );
            }
        } else {
            FriendMessageViewHolder holder1 = (FriendMessageViewHolder) holder;
            holder1.messageTextView.setText ( DecryptedMessage );
            if ( timestamp != null ) {
                @SuppressLint("SimpleDateFormat") String time = new SimpleDateFormat ( "h:mm a" ).format ( timestamp.toDate ( ) );
                holder1.timeTextView.setText ( time );
            }
        }
    }

    @Override
    public int getItemCount() {
        return messageModels.size ( );
    }

    @Override
    public int getItemViewType(int position) {
        return messageModels.get ( position ).getFrom ( ).equals ( FirebaseAuth.getInstance ( ).getCurrentUser ( ).getUid ( ) ) ? MY_MESSAGE : FRIEND_MESSAGE;
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
