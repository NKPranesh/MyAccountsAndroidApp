package com.myapp.myaccounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class transactions extends AppCompatActivity {

    TextView title,heading;
    RecyclerView recyclerView;
    TransactionsAdapter adapter;
    Button showRecent;
    DatabaseReference transactionsData;
    Spinner monthSpinner,yearSpinner;
    FloatingActionButton fab;
    List<transactionRowInfo> ListItems,duplicateListItems;
    int count1=0,count2=0;
    String paymentStatus="-1";
    String dnt,ipAddress;
    Intent intent;
    private void filter(int month, int year)
    {
        List<transactionRowInfo> filteredList=new ArrayList<>();
        for(transactionRowInfo item: ListItems)
        {
            if((Integer.parseInt(item.Date.split("-")[1])==month) && (Integer.parseInt(item.Date.split("-")[2])==year))
            {
                filteredList.add(item);
            }
        }
        heading.setText("Transactions in "+monthSpinner.getSelectedItem().toString()+", "+yearSpinner.getSelectedItem().toString());
        adapter.filterList(filteredList);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //adapter=new TransactionsAdapter();
        ipAddress=IPAddress.getLocalIpAddress().replace(".","-");
        super.onCreate(savedInstanceState);
        final Bundle extras=getIntent().getExtras();
        setContentView(R.layout.activity_transactions);
        title=findViewById(R.id.titleID);
        heading=findViewById(R.id.HeadingID);
        title.setText(extras.getString("name"));
        ListItems=new ArrayList<transactionRowInfo>();
        recyclerView=findViewById(R.id.recyclerView);
        monthSpinner=findViewById(R.id.monthSpinnerID);
        showRecent=findViewById(R.id.showRecentTransactionsID);
        fab=findViewById(R.id.fabId);
        yearSpinner=findViewById(R.id.yearSpinnerID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //final  Bundle extras=getIntent().getExtras();
        showRecent.setVisibility(View.GONE);
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        // System.out.println("NAME-------------------------->"+extras.getString("name"));
        ListItems=new ArrayList<>();
        if(extras.getInt("flag")==0)
            transactionsData= FirebaseDatabase.getInstance().getReference().child(ipAddress).child("purchasePartyList").child(extras.getString("name")+"---"+extras.getString("gstNo")).child("Transactions");
        else
            transactionsData= FirebaseDatabase.getInstance().getReference().child(ipAddress).child("salePartyList").child(extras.getString("name")+"---"+extras.getString("gstNo")).child("Transactions");
        transactionsData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ListItems.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
//                    if(snap.getKey().equals("Payments"))
//                        continue;
                    String[] temp = snap.getKey().split("---", 2);
                    try{
                        dnt=snap.child("dnt").getValue().toString();}
                    catch (Exception e){

                    }
                    transactionRowInfo item = new transactionRowInfo(temp[0], temp[1],"-1",dnt);
                    paymentStatus=snap.child("paymentStatus").getValue().toString();
                    if(paymentStatus.isEmpty())
                        item.paymentStatus="-1";
                    else
                        item.paymentStatus=paymentStatus;
                    ListItems.add(item);
                }
                Collections.sort(ListItems, new Comparator<transactionRowInfo>() {
                    public int compare(transactionRowInfo o1, transactionRowInfo o2) {
                        return o2.getDnt().compareTo(o1.getDnt());
                    }
                });
                duplicateListItems=new ArrayList<>(ListItems);
                adapter.notifyDataSetChanged();
                mProgressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }});
//        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaeeeeeeeeeeeee");
//        System.out.println(ListItems.size());
        //   ListItems= (List<transactionRowInfo>) getIntent().getSerializableExtra("ListItems");
        System.out.println(ListItems.size());
        adapter=new TransactionsAdapter(this,ListItems);
        adapter.gstNo=extras.getString("gstNo");
        adapter.flag=extras.getInt("flag");
        adapter.name=extras.getString("name");
        recyclerView.setAdapter(adapter);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(count1==0)
                {
                    count1++;
                    return;
                }
                showRecent.setVisibility(View.VISIBLE);
                int month=monthSpinner.getSelectedItemPosition()+1;
                int year=yearSpinner.getSelectedItemPosition()+2016;
                filter(month,year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        yearSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(count2==0)
                {
                    count2++;
                    return;
                }
                showRecent.setVisibility(View.VISIBLE);
                int month=monthSpinner.getSelectedItemPosition()+1;
                int year=yearSpinner.getSelectedItemPosition()+2016;
                filter(month,year);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        showRecent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.fetchedDataList=duplicateListItems;
                adapter.notifyDataSetChanged();
                heading.setText("Recent Transactions");
                showRecent.setVisibility(View.GONE);
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(extras.getInt("flag")==0)
                {
                    intent=new Intent(transactions.this,newtransaction.class);
                    intent.putExtra("flag",0);
                }
                else
                {
                    intent=new Intent(transactions.this,newtransaction.class);
                    intent.putExtra("flag",1);
                }
                intent.putExtra("name",extras.getString("name"));
                intent.putExtra("gstNo",extras.getString("gstNo"));
                startActivity(intent);
            }
        });
    }
}
