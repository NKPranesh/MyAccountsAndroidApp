package com.myapp.myaccounts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
public class sale extends AppCompatActivity {
    Button addPartyButton,dateWiseReportButton,partyListButton;
    TextView addPartyTitle,dateWiseReportTitle,partyListTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);
        final Bundle extras=getIntent().getExtras();
        addPartyButton=findViewById(R.id.addCustomerButtonId);
        partyListButton=findViewById(R.id.partyListButtonId);
        addPartyTitle=findViewById(R.id.addPartyCustomerID);
        partyListTitle=findViewById(R.id.PartylistID);
        addPartyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(sale.this,addparty.class);
                intent.putExtra("flag",extras.getInt("flag"));
                startActivity(intent);
            }
        });
        addPartyTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(sale.this,addparty.class);
                intent.putExtra("flag",extras.getInt("flag"));
                startActivity(intent);
            }
        });
        partyListTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(sale.this,partylist.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });
        partyListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(sale.this,partylist.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });
    }
}
