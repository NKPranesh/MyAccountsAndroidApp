package com.myapp.myaccounts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
class TransactionDetails{
    String invoiceNumber;
    String date;
    String amount;
    String amountCalculated;
    String gstValue;
    String PaymentStatus;
    String dnt;
    public TransactionDetails(){
    }
    public TransactionDetails(String invoiceNumber, String date, String amount, String gstValue, String paymentStatus, String dnt) {
        this.invoiceNumber = invoiceNumber;
        this.date = date;
        this.amount = amount;
        this.gstValue = gstValue;
        this.PaymentStatus = paymentStatus;
        this.amountCalculated=String.valueOf(Float.parseFloat(amount)+(Float.parseFloat(gstValue)/100)*Float.parseFloat(amount));
        this.dnt = dnt;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAmountCalculated() {
        return amountCalculated;
    }

    public void setAmountCalculated(String amountCalculated) {
        this.amountCalculated = amountCalculated;
    }

    public String getGstValue() {
        return gstValue;
    }

    public void setGstValue(String gstValue) {
        this.gstValue = gstValue;
    }

    public String getPaymentStatus() {
        return PaymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        PaymentStatus = paymentStatus;
    }

    public String getDnt() {
        return dnt;
    }

    public void setDnt(String dnt) {
        this.dnt = dnt;
    }

}


public class newtransaction extends AppCompatActivity {
    private FirebaseStorage storage;
    TransactionDetails details;
    private StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Uri billUpload;
    Spinner spin;
    private static int setImage=0;
    Uri imageUri;
    ImageView billuploadImg;
    TextInputLayout invoiceId;
    TextInputLayout date;
    TextInputLayout amountEditText;
    EditText gstValue;
    TextView amountCalculated,gstTextView;
    Button addButton;
    String percentage;
    String value;
    String key,ip;
    TextInputEditText insideBillWeight;
    float amount=0,totalBund=0;
    DatabaseReference purchaseData,saleData;
    int check=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final Bundle bundle=getIntent().getExtras();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newtransaction);
        spin = findViewById(R.id.spinnerId);
        spin.setPrompt("Dealer Type");
        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();
        invoiceId=findViewById(R.id.invoiceID);
        date=findViewById(R.id.dateID);
        ip=IPAddress.getLocalIpAddress().replace(".","-");
        amountEditText=findViewById(R.id.amountEditTextID);;
        gstValue=findViewById(R.id.gstValueId);
        amountCalculated=findViewById(R.id.totalAmountID);
        billuploadImg=findViewById(R.id.uploadBillId);
        addButton=findViewById(R.id.addButtonId);
        gstTextView=findViewById(R.id.gstID);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        insideBillWeight=findViewById(R.id.amountID);
        final String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        date.getEditText().setText(currentDate);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(check!=99) {
                    percentage = snapshot.child("GST").getValue().toString();
                    gstValue.setText(percentage);
                    FirebaseDatabase.getInstance().getReference().child("GST").setValue(gstValue.getText().toString());
                    System.out.println(percentage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    gstTextView.setVisibility(View.VISIBLE);
                    gstValue.setVisibility(View.VISIBLE);
                    gstValue.setText(percentage);
                }
                else{
                    gstValue.setText("0");
                    gstTextView.setVisibility(View.GONE);
                    gstValue.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gstValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String gst=gstValue.getText().toString();
                String amt=amountEditText.getEditText().getText().toString();
                if(amt.isEmpty()||amt.equals("")){
                    amountCalculated.setText("Total Amount: 0");
                }
                else {
                    if (gst.trim().equals("") || gst.isEmpty()) {
                        amount = Float.parseFloat(amt);
                        amountCalculated.setText("Total Amount: " + amount);
                    } else {
                        amount = Float.parseFloat(amt) + (Float.parseFloat(gst) / 100) * (Float.parseFloat(amt));
                        amountCalculated.setText("Total Amount: " + amount);
                    }
                }

            }
        });
        amountEditText.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String gst=gstValue.getText().toString();
                String amt=amountEditText.getEditText().getText().toString();
                if(amt.isEmpty()||amt.equals("")){
                    amountCalculated.setText("Total Amount: 0");
                }
                else {
                    if (gst.trim().equals("") || gst.isEmpty()) {
                        amount = Float.parseFloat(amt);
                        amountCalculated.setText("Total Amount: " + amount);
                    } else {
                        amount = Float.parseFloat(amt) + (Float.parseFloat(gst) / 100) * (Float.parseFloat(amt));
                        amountCalculated.setText("Total Amount: " + amount);
                    }
                }
            }
        });
        //(String invoiceNumber, String date, String whiteBundles, String greenBundles, String wAndGBundles, String ninetyBundles, String crushedBundles, String billWeight, String wayBridgeWeight,String ratePerKg)
//        TransactionDetails details=new TransactionDetails(invoiceId.getEditText().getText().toString(),date.getEditText().getText().toString(),whiteBundles.getText().toString(),greenBundles.getText().toString(),whiteAndGreenBundles.getText().toString(),ninetyBundles.getText().toString(),crushedBundles.getText().toString(),billWeight.getEditText().getText().toString(),wayBill.getEditText().getText().toString(),ratePerKg.getEditText().getText().toString());

        billuploadImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery,"Select Picture"),1);
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String gstMention = (String) spin.getSelectedItem();
                System.out.println("GST MENTION:" + gstMention);
                if(invoiceId.getEditText().getText().toString().trim().equals("")||invoiceId.getEditText().getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Invoice Number",Toast.LENGTH_LONG).show();
                }
                else if(amountEditText.getEditText().getText().toString().trim().equals("")||amountEditText.getEditText().getText().toString().trim().isEmpty()){
                    Toast.makeText(getApplicationContext(),"Enter Amount",Toast.LENGTH_LONG).show();
                }
                else if(!validateDate(date.getEditText().getText().toString())){
                    Toast.makeText(getApplicationContext(),"Enter Valid Date Format",Toast.LENGTH_LONG).show();
                }
                else{
                    System.out.println(bundle.getString("name") + " " + bundle.getString("gstNo") + "-------------------------------------------------------------------------------------------------------------------------");
                    if(amountEditText.getEditText().getText().toString().trim().equals("")){
                        amountEditText.getEditText().setText("0");
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd----HHmmss", Locale.getDefault());
                    final String currentDateandTime = sdf.format(new Date());
                    key=invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-");
                    if (gstValue.getText().toString().trim().equals("")) {
                        details = new TransactionDetails(invoiceId.getEditText().getText().toString(), date.getEditText().getText().toString(), amountEditText.getEditText().getText().toString(), "0","-1",currentDateandTime);
                    } else {
                        details = new TransactionDetails(invoiceId.getEditText().getText().toString(), date.getEditText().getText().toString(),amountEditText.getEditText().getText().toString(), gstValue.getText().toString(),"-1",currentDateandTime);
                        FirebaseDatabase.getInstance().getReference().child("GST").setValue(gstValue.getText().toString());}
                    details.date = details.date.replace("/", "-");

                    purchaseData= FirebaseDatabase.getInstance().getReference().child(ip).child("purchasePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo")).child("Transactions");
                    saleData=FirebaseDatabase.getInstance().getReference().child(ip).child("salePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo")).child("Transactions");
                    final int flag=bundle.getInt("flag");
                    if(flag==0){
                        purchaseData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(check!=99) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        if (key.equals(snap.getKey()) && !(currentDateandTime.equals(snap.child("dnt").getValue().toString()))) {
                                            check = -1;
                                            break;
                                        }
                                    }
                                    if (check == 1) {
                                        if (check != 99)
                                            databaseReference.child(ip).child("purchasePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo")).child("Transactions").child(invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-")).setValue(details);
                                        StorageReference riversRef = storageReference.child(ip).child("purchasePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo") + "/Transactions/" + invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-") + "/billUpload");
                                        try {
//
                                            Bitmap billUploadBitMap=((BitmapDrawable) billuploadImg.getDrawable()).getBitmap();
                                            if(!billUploadBitMap.sameAs(((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_menu_upload)).getBitmap())){
                                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                                billUploadBitMap.compress(Bitmap.CompressFormat.JPEG, 25, bytes);
                                                riversRef.putBytes(bytes.toByteArray());}
                                        } catch (Exception e) {
                                            System.out.println(e);
                                        }
                                        check = 99;
                                        Toast.makeText(getApplicationContext(), "Transaction added succesfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else if (check == -1) {
                                        check = 1;
                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(newtransaction.this);
                                        alertDialog.setTitle("Replace");
                                        alertDialog.setMessage("There already exits a party with same Invoice No and date. Do you want to replace it?\n Note: It will delete all its payment history");
                                        alertDialog.setCancelable(true);
                                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseStorage.getInstance().getReference().child(ip).child("purchasePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo")).child("Transactions").child(invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-")).child("billUpload").delete();
                                                databaseReference.child(ip).child("purchasePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo")).child("Transactions").child(invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-")).setValue(details);
                                                StorageReference riversRef = storageReference.child(ip).child("purchasePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo") + "/Transactions/" + invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-") + "/billUpload");
                                                try {
//
                                                    Bitmap billUploadBitMap=((BitmapDrawable) billuploadImg.getDrawable()).getBitmap();
                                                    if(!billUploadBitMap.sameAs(((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_menu_upload)).getBitmap())){
                                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                                        billUploadBitMap.compress(Bitmap.CompressFormat.JPEG, 25, bytes);
                                                        riversRef.putBytes(bytes.toByteArray());}
                                                } catch (Exception e) {
                                                    System.out.println(e);
                                                }
                                                check = 99;
                                                Toast.makeText(getApplicationContext(), "Transaction added succesfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                                return;
                                            }
                                        });
                                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                                Toast.makeText(getApplicationContext(), "Transaction added succesfully ", Toast.LENGTH_SHORT).show();
                                                dialog.cancel();
                                            }
                                        });
                                        if (!isFinishing()) {
                                            AlertDialog dialog = alertDialog.create();
                                            dialog.show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled (@NonNull DatabaseError error){

                            }
                        });
                    }

                    else
                    {
                        saleData.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(check!=99) {
                                    for (DataSnapshot snap : snapshot.getChildren()) {
                                        if (key.equals(snap.getKey()) && !(currentDateandTime.equals(snap.child("dnt").getValue().toString()))) {
                                            check = -1;
                                            break;
                                        }
                                    }
                                    if (check == 1) {
                                        if (check != 99)
                                            databaseReference.child(ip).child("salePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo")).child("Transactions").child(invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-")).setValue(details);
                                        StorageReference riversRef = storageReference.child(ip).child("salePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo") + "/Transactions/" + invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-") + "/billUpload");
                                        try {
//
                                            Bitmap billUploadBitMap=((BitmapDrawable) billuploadImg.getDrawable()).getBitmap();
                                            if(!billUploadBitMap.sameAs(((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_menu_upload)).getBitmap())){
                                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                                billUploadBitMap.compress(Bitmap.CompressFormat.JPEG, 25, bytes);
                                                riversRef.putBytes(bytes.toByteArray());}
                                        } catch (Exception e) {
                                            System.out.println(e);
                                        }
                                        check = 99;
                                        Toast.makeText(getApplicationContext(), "Transaction added succesfully", Toast.LENGTH_SHORT).show();
                                        finish();
                                    } else if (check == -1) {
                                        check = 1;
                                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(newtransaction.this);
                                        alertDialog.setTitle("Replace");
                                        alertDialog.setMessage("There already exits a party with same Invoice No and date. Do you want to replace it?\n Note: It will delete all its payment history");
                                        alertDialog.setCancelable(true);
                                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                FirebaseStorage.getInstance().getReference().child(ip).child("salePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo")).child("Transactions").child(invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-")).child("billUpload").delete();
                                                databaseReference.child(ip).child("salePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo")).child("Transactions").child(invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-")).setValue(details);
                                                StorageReference riversRef = storageReference.child(ip).child("salePartyList").child(bundle.getString("name") + "---" + bundle.getString("gstNo") + "/Transactions/" + invoiceId.getEditText().getText().toString() + "---" + date.getEditText().getText().toString().replace("/", "-") + "/billUpload");
                                                try {
//
                                                    Bitmap billUploadBitMap=((BitmapDrawable) billuploadImg.getDrawable()).getBitmap();
                                                    if(!billUploadBitMap.sameAs(((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_menu_upload)).getBitmap())){
                                                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                                        billUploadBitMap.compress(Bitmap.CompressFormat.JPEG, 25, bytes);
                                                        riversRef.putBytes(bytes.toByteArray());}
                                                } catch (Exception e) {
                                                    System.out.println(e);
                                                }
                                                check = 99;
                                                Toast.makeText(getApplicationContext(), "Transaction added succesfully", Toast.LENGTH_SHORT).show();
                                                finish();
                                                return;
                                            }
                                        });
                                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
//                                                Toast.makeText(getApplicationContext(), "Transaction added succesfully ", Toast.LENGTH_SHORT).show();
                                                dialog.cancel();
                                            }
                                        });
                                        if (!isFinishing()) {
                                            AlertDialog dialog = alertDialog.create();
                                            dialog.show();
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled (@NonNull DatabaseError error){

                            }
                        });
                    }
                }

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            imageUri=data.getData();
            try{
//                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                if(requestCode==1){
                    billUpload=imageUri;
                    billuploadImg.setImageURI(imageUri);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public boolean validateDate(String date){
        if(date.isEmpty()||date.trim().equals("")){
            return false;
        }
        String regex = "^[0-3]?[0-9]/[0-3]?[0-9]/(?:[0-9]{2})?[0-9]{2}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(date);
        if(matcher.matches()){
            return true;
        }
        return false;
    }

}
