package com.brm.uz.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.brm.uz.R;
import com.brm.uz.models.AccountPOJO;
import com.brm.uz.view.FirebaseChatInterface;
import com.brm.uz.viewHolder.UsersViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUsersFragment extends Fragment implements FirebaseChatInterface {

    public View view;
    public RecyclerView recyclerView;
    public DatabaseReference reference;
    public EditText mSearchField;
    public ImageButton mSearchButton;
    public String searchText, current_user, managerString;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.activity_chat_users, container, false);
        recyclerView = view.findViewById(R.id.chat_users_recycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        current_user = user.getUid();
        reference = FirebaseDatabase.getInstance().getReference().child("Account");
        mSearchButton = view.findViewById(R.id.chat_users_search_btn);
        mSearchField = view.findViewById(R.id.chat_users_edit_text);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = mSearchField.getText().toString();
                firebaseSearch();
            }
        });
        firebaseDownload();
        return view;
    }

    public void firebaseSearch(){
        Query firebaseQuery = reference.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff");

        FirebaseRecyclerAdapter<AccountPOJO, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AccountPOJO, UsersViewHolder>(
                AccountPOJO.class,
                R.layout.item_view_chat,
                UsersViewHolder.class,
                firebaseQuery

        ) {
            @Override
            protected void populateViewHolder(UsersViewHolder usersViewHolder, final AccountPOJO accountPOJO, final int i) {
                usersViewHolder.setDetails(accountPOJO.getName(),  accountPOJO.getRegion());
                usersViewHolder.setImage(accountPOJO.getImage(), getActivity());

                final String user_id = getRef(i).getKey();
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public void firebaseDownload(){
        DatabaseReference manager = FirebaseDatabase.getInstance().getReference().child("Account").child(current_user);
        manager.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                managerString = dataSnapshot.child("manager").getValue().toString();
                Query firebaseQuery = reference.orderByChild("manager").startAt(managerString).endAt(managerString + "\uf8ff");
                FirebaseRecyclerAdapter<AccountPOJO, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<AccountPOJO, UsersViewHolder>(
                        AccountPOJO.class,
                        R.layout.item_view_chat,
                        UsersViewHolder.class,
                        firebaseQuery

                ) {
                    @Override
                    protected void populateViewHolder(UsersViewHolder usersViewHolder, final AccountPOJO accountPOJO, final int i) {
                        usersViewHolder.setDetails(accountPOJO.getName(),   accountPOJO.getRegion());
                        usersViewHolder.setImage(accountPOJO.getImage(), getActivity());

                        final String user_id = getRef(i).getKey();
                    }
                };
                recyclerView.setAdapter(firebaseRecyclerAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
