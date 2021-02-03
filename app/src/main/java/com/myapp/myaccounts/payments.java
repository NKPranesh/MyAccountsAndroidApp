package com.myapp.myaccounts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class paymentInfo
{
    String date,paidAmount,dnt;

    public paymentInfo(String date, String paidAmount) {
        this.date = date;
        this.paidAmount = paidAmount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }
}
public class payments extends AppCompatActivity {
    TextView partyName;
    TextView payTitle;
    TextView gstNo,phone,address;
    TextView invoiceNo,openingDate,totalAmount,amountPaid,balanceAmount;
    EditText amount,date;
    Button pay;
    RecyclerView recyclerView;
    PaymentsAdapter adapter;
    List<paymentRowInfo> ListItems;
    paymentRowInfo head;
    Float paidAmt=0.0f,balAmt=0.0f;
    DatabaseReference reference,counterReference;
    DatabaseReference transactionDetails;
    int check=1;
    String ip;
    int payStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments);
        partyName=findViewById(R.id.partyNameID);
        payTitle=findViewById(R.id.payID);
        gstNo=findViewById(R.id.gstNOID);
        phone=findViewById(R.id.phoneID);
        address=findViewById(R.id.addressID);
        invoiceNo=findViewById(R.id.invoiceNumberID);
        openingDate=findViewById(R.id.openingDateID);
        totalAmount=findViewById(R.id.totalAmountID);
        amountPaid=findViewById(R.id.amountPaidID);
        balanceAmount=findViewById(R.id.balanceamountID);
        amount=findViewById(R.id.enterAmountID);
        date=findViewById(R.id.dateInputID);
        pay=findViewById(R.id.payButton);
        ip=IPAddress.getLocalIpAddress().replace(".","-");
        recyclerView=findViewById(R.id.recyclerID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final  Bundle extras=getIntent().getExtras();
        partyName.setText("Name: "+extras.getString("partyName"));
        gstNo.setText("GST NO.: "+extras.getString("GSTNo"));
//        phone.setText("Phone: "+extras.getString("phone"));
//        address.setText("Address: "+extras.getString("address"));
        invoiceNo.setText("Invoice No.: "+extras.getString("InvoiceNo"));
        openingDate.setText("Opening Date: "+extras.getString("openingDate"));
//        totalAmount.setText("Total Amount: "+extras.getString("totalAmount"));
        ListItems=new ArrayList<>();
        head=new paymentRowInfo("Date","Paid Amt","Bal. Amt","9999999");
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Loading...");
        mProgressDialog.show();
        if(extras.getInt("flag")==0)
            transactionDetails = FirebaseDatabase.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("partyName")+"---"+extras.getString("GSTNo"));
        else
            transactionDetails = FirebaseDatabase.getInstance().getReference().child(ip).child("salePartyList").child(extras.getString("partyName")+"---"+extras.getString("GSTNo"));
        transactionDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                mProgressDialog.setMessage("Loading...");
//                mProgressDialog.show();
                if (check != 99) {
                    phone.setText("Phone: " + snapshot.child("phoneNumber").getValue().toString());
                    address.setText("Address: " + snapshot.child("address").getValue().toString());
                    try {
                        totalAmount.setText("Total Amount: " + String.format("%.2f", Float.parseFloat(snapshot.child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("amountCalculated").getValue().toString())));
                        balAmt = Float.parseFloat(snapshot.child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("amountCalculated").getValue().toString());
                    } catch (Exception e) {

                    }
                    amountPaid.setText("Amount Paid: " + String.format("%.2f", paidAmt));
                    if (extras.getInt("flag") == 0)
                        counterReference = FirebaseDatabase.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("partyName") + "---" + extras.getString("GSTNo")).child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("Payments");
                    else
                        counterReference = FirebaseDatabase.getInstance().getReference().child(ip).child("salePartyList").child(extras.getString("partyName") + "---" + extras.getString("GSTNo")).child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("Payments");
                    counterReference.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (check != 99) {
                                ListItems.clear();
                                ListItems.add(head);
                                paidAmt = 0.0f;
                                for (DataSnapshot snap : snapshot.getChildren()) {
                                    paidAmt = paidAmt + Float.parseFloat(snap.child("paidAmount").getValue().toString());
                                    paymentRowInfo item = new paymentRowInfo(snap.child("date").getValue().toString(), snap.child("paidAmount").getValue().toString(), Float.toString(balAmt - paidAmt), snap.getKey());
                                    ListItems.add(item);
                                }
                                Collections.sort(ListItems, new Comparator<paymentRowInfo>() {
                                    public int compare(paymentRowInfo o1, paymentRowInfo o2) {
                                        return o2.getDnt().compareTo(o1.getDnt());
                                    }
                                });
                                amountPaid.setText("Amount Paid: " + String.format("%.2f", Float.parseFloat(paidAmt.toString())));
                                balanceAmount.setText("Balance Amount: " + String.format("%.2f", balAmt - paidAmt));
                                System.out.println((balAmt - paidAmt) == 0);
                                if (FirebaseDatabase.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("partyName") + "---" + extras.getString("GSTNo")).child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("invoiceNumber").getKey().toString().equals("invoiceNumber")) {
                                    if (Float.parseFloat(balanceAmount.getText().toString().split(": ")[1]) == 0) {

                                        if (extras.getInt("flag") == 0) {
                                            payStatus = 1;
                                            FirebaseDatabase.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("partyName") + "---" + extras.getString("GSTNo")).child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("paymentStatus").setValue("1");
                                        } else {
                                            payStatus = 1;
                                            FirebaseDatabase.getInstance().getReference().child(ip).child("salePartyList").child(extras.getString("partyName") + "---" + extras.getString("GSTNo")).child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("paymentStatus").setValue("1");
                                        }
                                    }
                                    else {
                                        if (extras.getInt("flag") == 0) {
                                            payStatus = -1;
                                            FirebaseDatabase.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("partyName") + "---" + extras.getString("GSTNo")).child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("paymentStatus").setValue("-1");
                                        } else {
                                            payStatus = -1;
                                            FirebaseDatabase.getInstance().getReference().child(ip).child("salePartyList").child(extras.getString("partyName") + "---" + extras.getString("GSTNo")).child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("paymentStatus").setValue("-1");
                                        }

                                    }
                                }
                                if (payStatus == 1) {
                                    payTitle.setVisibility(View.GONE);
                                    amount.setVisibility(View.GONE);
                                    date.setVisibility(View.GONE);
                                    pay.setVisibility(View.GONE);
                                    balanceAmount.setTypeface(null, Typeface.BOLD);
                                } else {
                                    payTitle.setVisibility(View.VISIBLE);
                                    amount.setVisibility(View.VISIBLE);
                                    date.setVisibility(View.VISIBLE);
                                    pay.setVisibility(View.VISIBLE);
                                    balanceAmount.setTypeface(null, Typeface.NORMAL);
                                }
                                adapter.notifyDataSetChanged();
//                        nProgressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    mProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        balanceAmount.setText("Balance Amount: "+balAmt);
        final String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        date.setText(currentDate);
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paymentInfo value = new paymentInfo(date.getText().toString(), amount.getText().toString());
                if (!(value.paidAmount.equals(null) || (value.paidAmount.isEmpty()))) {
                    if (!(Float.parseFloat(value.paidAmount) == 0)) {
                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(payments.this);
                        alertDialog.setTitle("Confirm");
                        alertDialog.setMessage("Do you want to make this payment?");
                        alertDialog.setCancelable(true);
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                paymentInfo value = new paymentInfo(date.getText().toString(), amount.getText().toString());
                                reference = FirebaseDatabase.getInstance().getReference();
                                SimpleDateFormat sdf = new SimpleDateFormat("----HHmmss", Locale.getDefault());
                                String currentDateandTime = sdf.format(new Date());
                                currentDateandTime = value.date.split("-")[2] + value.date.split("-")[1] + value.date.split("-")[0] + currentDateandTime;
                                if (Float.parseFloat(value.paidAmount) + paidAmt > balAmt)
                                    Toast.makeText(payments.this, "Payment Amount Exceeding...", Toast.LENGTH_LONG).show();
                                    //  reference.child("purchasePartyList").child(extras.getString("partyName")+"---"+extras.getString("GSTNo")).child("Transactions").child("Payments").child(currentDateandTime).setValue(value);
                                else {
                                    if(extras.getInt("flag")==0)
                                        reference.child(ip).child("purchasePartyList").child(extras.getString("partyName") + "---" + extras.getString("GSTNo")).child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("Payments").child(currentDateandTime).setValue(value);
                                    else
                                        reference.child(ip).child("salePartyList").child(extras.getString("partyName") + "---" + extras.getString("GSTNo")).child("Transactions").child(extras.getString("InvoiceNo") + "---" + extras.getString("openingDate")).child("Payments").child(currentDateandTime).setValue(value);
                                    Toast.makeText(payments.this, "Paid successfully", Toast.LENGTH_SHORT).show();
                                }
                                // balanceAmount.setText("Balance Amount"+Float.toString(balAmt-paidAmt));
                            }
                        });
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog dialog = alertDialog.create();
                        dialog.show();
                    }
                    else
                        Toast.makeText(payments.this,"Enter valid amount",Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Toast.makeText(payments.this,"Enter amount",Toast.LENGTH_SHORT).show();
                }
            }
        });
//        final ProgressDialog nProgressDialog = new ProgressDialog(this);
//        nProgressDialog.setMessage("Loading...");
//        nProgressDialog.show();
        adapter=new PaymentsAdapter(this,ListItems);
        adapter.name=extras.getString("partyName");
        adapter.gstNo=extras.getString("GSTNo");
        adapter.flag=extras.getInt("flag");
        adapter.invoicendate=extras.getString("InvoiceNo")+"---"+extras.getString("openingDate");
        recyclerView.setAdapter(adapter);
    }
    @Override
    public void onBackPressed() {
        // your code.
        check=99;
        finish();
    }
}