package com.myapp.myaccounts;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
public class MainActivity extends AppCompatActivity {
    Button purchase,sale;
    TextView purchaseTitle,saleTitle;
    ImageView logout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        purchase=findViewById(R.id.purchaseButtonid);
        sale=findViewById(R.id.saleButtonId);
        purchaseTitle=findViewById(R.id.purchaseTitleID);
        saleTitle=findViewById(R.id.saleTitleID);
        logout=findViewById(R.id.logoutID);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences=getSharedPreferences("checkbox",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putString("remember","false");
                editor.apply();
                Intent intent=new Intent(MainActivity.this,verifyPhone.class);
                startActivity(intent);
                finish();
            }
        });
        purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,purchase.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
        purchaseTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,purchase.class);
                intent.putExtra("flag",0);
                startActivity(intent);
            }
        });
        sale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,sale.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });
        saleTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,sale.class);
                intent.putExtra("flag",1);
                startActivity(intent);
            }
        });
    }

}
