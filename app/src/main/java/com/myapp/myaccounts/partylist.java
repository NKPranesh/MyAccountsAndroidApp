package com.myapp.myaccounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class partylist extends AppCompatActivity {
    private RecyclerView recyclerView;
    private myAdapterClass adapter;
    private List<partyRowInfo> ListItems;
    private EditText searchText;
    DatabaseReference purchaseData, saleData;
    String ipAddress;

    private void filter(String searchText) {
        List<partyRowInfo> filteredList = new ArrayList<>();
        searchText = searchText.trim();
        if (searchText.length() == 0) {
            filteredList.addAll(ListItems);
        } else {
            for (partyRowInfo item : ListItems) {
                if (item.name.toLowerCase().trim().contains(searchText.toLowerCase())) {
                    filteredList.add(item);
                }
            }
        }
//        System.out.println(filteredList.get(0).name);
        adapter.filterList(filteredList);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListItems = new ArrayList<>();
        setContentView(R.layout.activity_partylist);
        //Thread thread=new Thread(new FetchData());
        ipAddress = IPAddress.getLocalIpAddress().replace(".", "-");
        recyclerView = findViewById(R.id.recyclerViewIDPartyList);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final Bundle extras = getIntent().getExtras();
        searchText = findViewById(R.id.inputSearchID);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        //mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("called");
                filter(s.toString());
            }
        });

        purchaseData = FirebaseDatabase.getInstance().getReference().child(ipAddress).child("purchasePartyList");
        saleData = FirebaseDatabase.getInstance().getReference().child(ipAddress).child("salePartyList");
        int flag = extras.getInt("flag");
        if (flag == 0) {
            purchaseData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ListItems.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String[] temp = snap.getKey().split("---", 2);
                        partyRowInfo item = new partyRowInfo(temp[0], temp[1]);
                        ListItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    mProgressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
//            thread.start();

        } else {
            saleData.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ListItems.clear();
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        String[] temp = snap.getKey().split("---", 2);
                        partyRowInfo item = new partyRowInfo(temp[0], temp[1]);
                        ListItems.add(item);
                    }
                    adapter.notifyDataSetChanged();
                    mProgressDialog.dismiss();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        System.out.println("size______________________________" + ListItems.size());
        adapter = new myAdapterClass(this, ListItems);
        adapter.flag = flag;
        recyclerView.setAdapter(adapter);
    }
}
