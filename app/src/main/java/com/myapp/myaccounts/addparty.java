package com.myapp.myaccounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.textfield.TextInputLayout;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

class userInfo{
    String partyName,gstNumber,phoneNumber,address;
    public userInfo(){ }
    public userInfo(String partyName, String gstNumber, String phoneNumber, String address) {
        this.partyName = partyName;
        this.gstNumber = gstNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
    public String getPartyName() {
        return partyName;
    }

    public void setPartyName(String partyName) {
        this.partyName = partyName;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
public class addparty extends AppCompatActivity {
    FirebaseDatabase rootNode;
    DatabaseReference reference;
    TextInputLayout nameOfParty,gstNumber,phone,address;
    Button addButton;
    String key;
    int check=1;
    int check2=0;
    userInfo userInfo;
    List<String> deleteList;
    DatabaseReference purchaseData,saleData;
    String ipAddress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        final Bundle extras=getIntent().getExtras();
        setContentView(R.layout.activity_addparty);
        nameOfParty=findViewById(R.id.nameOfPartyID);
        gstNumber=findViewById(R.id.gstId);
        phone=findViewById(R.id.phoneID);
        address=findViewById(R.id.addressID);
        addButton=findViewById(R.id.addButtonId);
        ipAddress=IPAddress.getLocalIpAddress().replace(".","-");
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                rootNode=FirebaseDatabase.getInstance();
//                System.out.println("ERRROR");
                reference=FirebaseDatabase.getInstance().getReference();
                if(nameOfParty.getEditText().getText().toString().trim().length()==0||gstNumber.getEditText().getText().toString().trim().length()==0||phone.getEditText().getText().toString().trim().length()==0||address.getEditText().getText().toString().trim().length()==0){
                    Toast.makeText(addparty.this,"Please fill all the details or Enter - if empty",Toast.LENGTH_SHORT).show();
                }
                else{
                    String partyname=nameOfParty.getEditText().getText().toString().trim();
                    String GSTnumber=gstNumber.getEditText().getText().toString().trim();
                    String phoneNumber=phone.getEditText().getText().toString().trim();
                    String Address=address.getEditText().getText().toString().trim();
                    userInfo=new userInfo(partyname,GSTnumber,phoneNumber,Address);
                    key=partyname.concat("---").concat(GSTnumber);
                    purchaseData= FirebaseDatabase.getInstance().getReference().child(ipAddress).child("purchasePartyList");
                    saleData=FirebaseDatabase.getInstance().getReference().child(ipAddress).child("salePartyList");
                    final int flag=extras.getInt("flag");
                    if(flag==0){
                        purchaseData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (check != 99) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        if (key.equals(snap.getKey()) && check2 == 0) {
                                            check = -1;
                                            break;
                                        }
                                    }
                                    if (check == 1) {
                                        reference.child(ipAddress).child("purchasePartyList").child(key).setValue(userInfo);
                                        Toast.makeText(addparty.this, "Party Added Successfully", Toast.LENGTH_LONG).show();
                                        check2 = 1;
                                        check = 99;
                                        finish();
                                    } else {
                                        check = 1;
                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(addparty.this);
                                        alertDialog.setTitle("Replace");
                                        alertDialog.setMessage("There already exits a party with same Name and GST No. Do you want to replace it?\n Note: It will delete all its transactions");
                                        alertDialog.setCancelable(true);
                                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteList=new ArrayList<>();
                                                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(ipAddress).child("purchasePartyList").child(key).child("Transactions");
                                                databaseReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot snap:snapshot.getChildren()){
                                                            deleteList.add(snap.getKey());
                                                        }
                                                        StorageReference ref=FirebaseStorage.getInstance().getReference().child(ipAddress).child("purchasePartyList").child(key).child("Transactions");
                                                        for(String s:deleteList){
                                                            ref.child(s).child("billUpload").delete();
                                                            ref.child(s).child("wayBillUpload").delete();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {}
                                                });
                                                reference.child(ipAddress).child("purchasePartyList").child(key).setValue(userInfo);
                                                Toast.makeText(addparty.this, "Party Added Successfully", Toast.LENGTH_LONG).show();
                                                check = 99;
                                                finish();
                                            }
                                        });
                                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        AlertDialog dialog = alertDialog.create();
                                        try {
                                            dialog.show();
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
                    else
                    {
                        saleData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (check != 99) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        if (key.equals(snap.getKey()) && check2 == 0) {
                                            check = -1;
                                            break;
                                        }
                                    }
                                    if (check == 1) {
                                        reference.child(ipAddress).child("salePartyList").child(key).setValue(userInfo);
                                        Toast.makeText(addparty.this, "Party Added Successfully", Toast.LENGTH_LONG).show();
                                        check2 = 1;
                                        check = 99;
                                        finish();
                                    } else {
                                        check=1;
                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(addparty.this);
                                        alertDialog.setTitle("Replace");
                                        alertDialog.setMessage("There already exits a party with same Name and GST No. Do you want to replace it?\n Note: It will delete all its transactions");
                                        alertDialog.setCancelable(true);
                                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                deleteList=new ArrayList<>();
                                                DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child(ipAddress).child("salePartyList").child(key).child("Transactions");
                                                databaseReference.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        for(DataSnapshot snap:snapshot.getChildren()){
                                                            deleteList.add(snap.getKey());
                                                        }
                                                        StorageReference ref= FirebaseStorage.getInstance().getReference().child(ipAddress).child("salePartyList").child(key).child("Transactions");
                                                        for(String s:deleteList){
                                                            ref.child(s).child("billUpload").delete();
                                                            ref.child(s).child("wayBillUpload").delete();
                                                        }
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {}
                                                });
                                                reference.child(ipAddress).child("salePartyList").child(key).setValue(userInfo);
                                                Toast.makeText(addparty.this, "Party Added Successfully", Toast.LENGTH_LONG).show();
                                                check=99;
                                                finish();
                                            }
                                        });
                                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.cancel();
                                            }
                                        });
                                        AlertDialog dialog = alertDialog.create();
                                        try {
                                            dialog.show();
                                        } catch (Exception e) {
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }
//                    if(extras.getInt("flag")==0) {
//                        reference.child("purchasePartyList").child(key).setValue(userInfo);
//                    }
//                    else{
//                        reference.child("salePartyList").child(key).setValue(userInfo);
//                    }
                }
            }
        });

    }

}
