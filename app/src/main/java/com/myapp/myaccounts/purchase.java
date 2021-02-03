package com.myapp.myaccounts;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class purchase extends AppCompatActivity {
    Button addPartyButton,dateWiseReportButton,partyListButton;
    TextView addPartyTitle,dateWiseReportTitle,partyListTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle extras=getIntent().getExtras();
        setContentView(R.layout.activity_main);
        addPartyButton=findViewById(R.id.addCustomerButtonId);
        partyListButton=findViewById(R.id.partyListButtonId);
        addPartyTitle=findViewById(R.id.addPartyCustomerID);
        partyListTitle=findViewById(R.id.PartylistID);

        addPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(purchase.this,addparty.class);
//                intent.putExtra("flag",extras.getInt("flag"));
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
        addPartyTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(purchase.this,addparty.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
        partyListTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(purchase.this,partylist.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
        partyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(purchase.this,partylist.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
    }
}
