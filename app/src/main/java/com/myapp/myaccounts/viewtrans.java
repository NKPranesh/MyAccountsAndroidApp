package com.myapp.myaccounts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
class pay
{
    String date,paidAmount;

    public pay(String date, String paidAmount) {
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
class StorePaymentDetails
{
    String date,paidAmount,dnt;

    public StorePaymentDetails(String date, String paidAmount, String dnt) {
        this.date = date;
        this.paidAmount = paidAmount;
        this.dnt = dnt;
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

    public String getDnt() {
        return dnt;
    }

    public void setDnt(String dnt) {
        this.dnt = dnt;
    }
}
class ViewTransactionDetails {

    String invoiceNumber, date, amount, amountCalculated, gstValue;
}
public class viewtrans extends AppCompatActivity {
    ViewTransactionDetails viewDet=new ViewTransactionDetails();
    TransactionDetails details;
    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
    FloatingActionButton editButton;
    StorageReference storageReference;
    DatabaseReference transactionDetails;
    TextView invoiceNumber;
    TextView date;
    int flag=0;
    Uri billUpload,imageUri;
    TextView amountCalculated;
    TextView gstvalue;
    TextView amount;
    ImageView billImg;
    Button payButton;
    String dnt;
    String invoiceNo,openingDate;
    EditText editGstPerc,editAmount,editDate,editInvoice;
    int counter=0;
    float totalBund=0,totalAmt=0;
    String paymentStatus;
    String amtCalculated="0";
    ProgressDialog billUploadProgressDialog;
    DatabaseReference counterReference;
    List<StorePaymentDetails> ListItems;
    int payFlag=0;
    float totalPaidAmt=0;
    String ip;
    Float amountTemp=0.0f;
    String oldInvoiceNo,oldDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ip=IPAddress.getLocalIpAddress().replace(".","-");
        setContentView(R.layout.activity_viewtrans);
        final Bundle extras=getIntent().getExtras();
        editButton=findViewById(R.id.editTransactionButton);
        invoiceNumber=findViewById(R.id.viewInvoiceNoId);
        date=findViewById(R.id.viewDateId);
        amount=findViewById(R.id.viewAmountId);
        payButton=findViewById(R.id.paymentsButtonID);
        gstvalue=findViewById(R.id.viewGstPercentageId);
        amountCalculated=findViewById(R.id.viewTotalAmountID);
        billImg=findViewById(R.id.viewUploadBillImageId);
        editGstPerc=findViewById(R.id.editGstPercId);
        editAmount=findViewById(R.id.editAmountId);
        editDate=findViewById(R.id.editDateId);
        editInvoice=findViewById(R.id.editInvoiceNoId);
        invoiceNo=extras.getString("invoiceNo");
        openingDate=extras.getString("date");
        ListItems=new ArrayList<>();
        details=new TransactionDetails();
        final ProgressDialog mProgressDialog = new ProgressDialog(viewtrans.this);
        mProgressDialog.setMessage("Loading...");
        //mProgressDialog.setCancelable(false);
        mProgressDialog.show();
        transactionDetails= FirebaseDatabase.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("name")+"---"+extras.getString("gstNo")).child("Transactions").child(invoiceNo+"---"+openingDate);
//        transactionDetails= FirebaseDatabase.getInstance().getReference().child("purchasePartyList").child("sunnyleone---PHUB123").child("Transactions").child("anuvamshik---23-01-2021");
        final ArrayList<String> items=new ArrayList();
        transactionDetails.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(flag==1) {
                    mProgressDialog.dismiss();
                    return;
                }
                flag++;
                storageReference=FirebaseStorage.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("name")+"---"+extras.getString("gstNo")).child("Transactions").child(invoiceNo+"---"+openingDate).child("billUpload");
                billUploadProgressDialog = new ProgressDialog(viewtrans.this);
                billUploadProgressDialog.setMessage("Loading...");
                billUploadProgressDialog.show();
                storageReference.getBytes(1024*1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.length);
                        billImg.setImageBitmap(bitmap);
                        billUploadProgressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        billUploadProgressDialog.dismiss();
                    }
                });
                viewDet.invoiceNumber=snapshot.child("invoiceNumber").getValue().toString();
                viewDet.date=snapshot.child("date").getValue().toString();
                viewDet.amount=snapshot.child("amount").getValue().toString();
                viewDet.gstValue=snapshot.child("gstValue").getValue().toString();
                paymentStatus=snapshot.child("paymentStatus").getValue().toString();
                dnt=snapshot.child("dnt").getValue().toString();
                viewDet.amountCalculated=snapshot.child("amountCalculated").getValue().toString();
                invoiceNumber.setText("Invoice No: "+viewDet.invoiceNumber);
                date.setText("Date: "+viewDet.date);
                if(Float.parseFloat(paymentStatus)==1)
                    payButton.setText("Payment History");
                amount.setText("Amount: "+viewDet.amount);
                amountCalculated.setText("Total Amount: "+viewDet.amountCalculated);
                gstvalue.setText("GST(%): "+viewDet.gstValue);
                for (DataSnapshot snap : snapshot.getChildren())
                {
                    if(snap.getKey().equals("Payments")) {
                        payFlag = 1;
                        break;
                    }
                }
                if(payFlag==1)
                {
                    counterReference=FirebaseDatabase.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("name")+"---"+extras.getString("gstNo")).child("Transactions").child(invoiceNo+"---"+openingDate).child("Payments");
                    counterReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            totalPaidAmt=0;
                            for (DataSnapshot snap : snapshot.getChildren())
                            {
                                String dNt=snap.getKey();
                                String paidAmt=snap.child("paidAmount").getValue().toString();
                                totalPaidAmt+=Float.parseFloat(paidAmt);
                                String date=snap.child("date").getValue().toString();
                                ListItems.add(new StorePaymentDetails(date,paidAmt,dNt));
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
                mProgressDialog.dismiss();
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {
            }
        });
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counter != 1) {
                    date.setText("Date:");
                    invoiceNumber.setText("Invoice No:");
                    editDate.setVisibility(View.VISIBLE);
                    editDate.setText(viewDet.date.replace("-", "/"));
                    editInvoice.setVisibility(View.VISIBLE);
                    payButton.setVisibility(View.GONE);
                    editInvoice.setText(viewDet.invoiceNumber);
                    amount.setText("Amount: ");
                    gstvalue.setText("GST(%): ");
                    editGstPerc.setVisibility(View.VISIBLE);
                    editGstPerc.setText(viewDet.gstValue);
                    editAmount.setVisibility(View.VISIBLE);
                    editAmount.setText(viewDet.amount);

                    if (Float.parseFloat(paymentStatus) == 1)
                        payButton.setText(" Payment\nHistory ");
                    amountCalculated.setText("Total Amount: " + viewDet.amountCalculated);
                    editButton.setImageResource(R.drawable.save);
                    counter = 1;
                    editGstPerc.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String gst=editGstPerc.getText().toString();
                            String amt=editAmount.getText().toString();
                            if(amt.isEmpty()||amt.equals("")){
                                amountCalculated.setText("Total Amount: 0");
                            }
                            else {
                                if (gst.trim().equals("") || gst.isEmpty()) {
                                    amountTemp= Float.parseFloat(amt);
                                    amountCalculated.setText("Total Amount: " + amountTemp);
                                } else {
                                    amountTemp= Float.parseFloat(amt) + (Float.parseFloat(gst) / 100) * (Float.parseFloat(amt));
                                    amountCalculated.setText("Total Amount: " + amountTemp);
                                }
                            }

                        }
                    });
                    editAmount.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String gst=editGstPerc.getText().toString();
                            String amt=editAmount.getText().toString();
                            if(amt.isEmpty()||amt.equals("")){
                                amountCalculated.setText("Total Amount: 0");
                            }
                            else {
                                if (gst.trim().equals("") || gst.isEmpty()) {
                                    amountTemp= Float.parseFloat(amt);
                                    amountCalculated.setText("Total Amount: " + amountTemp);
                                } else {
                                    amountTemp= Float.parseFloat(amt) + (Float.parseFloat(gst) / 100) * (Float.parseFloat(amt));
                                    amountCalculated.setText("Total Amount: " + amountTemp);
                                }
                            }
                        }
                    });
                } else if (counter == 1) {
                    //here the code to be written for writing into database;
                    payButton.setVisibility(View.VISIBLE);
                    final ProgressDialog aProgressDialog = new ProgressDialog(viewtrans.this);
                    aProgressDialog.setMessage("Saving changes...");
                    //mProgressDialog.setCancelable(false);
                    aProgressDialog.show();
                    //mProgressDialog.setCancelable(false);
                    String newAmount = editAmount.getText().toString();
                    String newGstPerc = editGstPerc.getText().toString();
                    String newDate = editDate.getText().toString();
                    String newInvoiceNo = editInvoice.getText().toString();
                    if (newInvoiceNo.trim().equals("") || newInvoiceNo.trim().isEmpty()) {
                        Toast.makeText(viewtrans.this, "Enter Invoice Number", Toast.LENGTH_LONG).show();
                    } else if (!validateDate(newDate)) {
                        Toast.makeText(viewtrans.this, "Enter Valid Date", Toast.LENGTH_LONG).show();
                    } else if(editAmount.getText().toString().trim().equals("")||editAmount.getText().toString().trim().isEmpty()){
                        Toast.makeText(getApplicationContext(),"Enter Amount",Toast.LENGTH_LONG).show();
                    }
                    else {
                        amount.setVisibility(View.VISIBLE);
                        gstvalue.setVisibility(View.VISIBLE);
                        amountCalculated.setVisibility(View.VISIBLE);
                        date.setVisibility(View.VISIBLE);
                        invoiceNumber.setVisibility(View.VISIBLE);
                        editDate.setVisibility(View.INVISIBLE);
                        editInvoice.setVisibility(View.INVISIBLE);
                        editAmount.setVisibility(View.INVISIBLE);
                        editGstPerc.setVisibility(View.INVISIBLE);
                        if (Float.parseFloat(amountCalculated.getText().toString().split(": ")[1]) < totalPaidAmt) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(viewtrans.this);
                            alertDialog.setMessage("You have already paid "+totalPaidAmt+". Amount cannot be changed to " + amountCalculated.getText().toString().split(": ")[1] +". Changes cannot be made.");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    counter=0;
                                    editButton.performClick();
                                }
                            });
                            aProgressDialog.dismiss();
                            AlertDialog dialog = alertDialog.create();
                            dialog.show();
                        } else {

                            if (!newGstPerc.trim().equals("")) {
                                details.setGstValue(newGstPerc);
                                gstvalue.setText("GST(%): " + newGstPerc);
                                viewDet.gstValue = newGstPerc;
                            } else {
                                details.setGstValue("0");
                                viewDet.gstValue = "0";
                            }
                            if (!newAmount.trim().equals("")) {
                                details.setAmount(newAmount);
                                amount.setText("Amount: " + newAmount);
                                viewDet.amount = newAmount;
                            } else {
                                details.setAmount("0");
                                viewDet.amount = "0";
                            }
                            details.setDate(newDate);
                            date.setText("Date: " + newDate);
                            viewDet.date = newDate;
                            details.setInvoiceNumber(newInvoiceNo);
                            invoiceNumber.setText("Invoice No: " + newInvoiceNo);
                            viewDet.invoiceNumber = newInvoiceNo;
                            // amount.setText("Amount: "+viewDet.amountCalculated);
                            amtCalculated = String.valueOf(Float.parseFloat(details.amount)+(Float.parseFloat(details.gstValue)/100)*Float.parseFloat(details.amount));
                            details.setAmountCalculated(amtCalculated);
                            viewDet.amountCalculated = amtCalculated;
                            if(Float.parseFloat(amtCalculated)==totalPaidAmt)
                            {
                                details.setPaymentStatus("1");
                                paymentStatus="1";
                                payButton.setText(" Payment History ");
                            }
                            else
                            {
                                details.setPaymentStatus("-1");
                                paymentStatus="-1";
                                payButton.setText("Pay");
                            }
                            details.setDnt(dnt);
                            databaseReference.child(ip).child("purchasePartyList").child(extras.getString("name") + "---" + extras.getString("gstNo")).child("Transactions").child(invoiceNo + "---" + openingDate).removeValue();
                            databaseReference.child(ip).child("purchasePartyList").child(extras.getString("name") + "---" + extras.getString("gstNo")).child("Transactions").child(details.invoiceNumber + "---" + details.date.replace("/", "-")).setValue(details);
                            for(StorePaymentDetails a:ListItems)
                            {
                                databaseReference.child(ip).child("purchasePartyList").child(extras.getString("name") + "---" + extras.getString("gstNo")).child("Transactions").child(details.invoiceNumber + "---" + details.date.replace("/", "-")).child("Payments").child(a.dnt).setValue(new pay(a.date,a.paidAmount));
                            }
                            oldInvoiceNo=invoiceNo;
                            oldDate=openingDate;
                            invoiceNo = details.invoiceNumber;
                            openingDate = details.date.replace("/", "-");
                            editButton.setImageResource(android.R.drawable.ic_menu_edit);
                            counter = 0;
                            //here
                            StorageReference riversRef = FirebaseStorage.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("name") + "---" + extras.getString("gstNo")).child("Transactions").child(invoiceNo + "---" + openingDate).child("billUpload");
                            try {
                                StorageReference ref1 = FirebaseStorage.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("name") + "---" + extras.getString("gstNo")).child("Transactions").child(oldInvoiceNo + "---" + oldDate).child("billUpload");
                                ref1.delete();
                                Bitmap billUploadBitMap=((BitmapDrawable) billImg.getDrawable()).getBitmap();
                                if(!billUploadBitMap.sameAs(((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_menu_upload)).getBitmap())){
                                    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                    billUploadBitMap.compress(Bitmap.CompressFormat.JPEG, 25, bytes);
                                    riversRef.putBytes(bytes.toByteArray());}
                            } catch (Exception e) {
                                System.out.println(e);
                            } finally {
                                aProgressDialog.dismiss();
                            }
                        }
                    }
                }
            }
        });
        billImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter!=1){
                    final Bitmap bmap = ((BitmapDrawable)billImg.getDrawable()).getBitmap();
                    if(bmap.sameAs(((BitmapDrawable)getResources().getDrawable(android.R.drawable.ic_menu_upload)).getBitmap())){
                    }
                    else{
                        Intent fullScreenMode=new Intent(viewtrans.this,fullScreenMode.class);
                        fullScreenMode.putExtra("billtype","1");
                        fullScreenMode.putExtra("name",extras.getString("name"));
                        fullScreenMode.putExtra("gstNo",extras.getString("gstNo"));
                        fullScreenMode.putExtra("invoiceNo",invoiceNo);
                        fullScreenMode.putExtra("date",openingDate);
                        fullScreenMode.putExtra("listType","purchasePartyList");
                        startActivity(fullScreenMode);
                    }
                }
                else if(counter==1){
                    AlertDialog.Builder alertdialog=new AlertDialog.Builder(viewtrans.this);
                    alertdialog.setMessage("What do you want do?");
                    alertdialog.setCancelable(true);
                    alertdialog.setPositiveButton("Remove Image", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final ProgressDialog cProgressDialog = new ProgressDialog(viewtrans.this);
                            cProgressDialog.setMessage("Saving changes...");
                            //mProgressDialog.setCancelable(false);
                            cProgressDialog.show();
                            StorageReference riversRef=FirebaseStorage.getInstance().getReference().child(ip).child("purchasePartyList").child(extras.getString("name")+"---"+extras.getString("gstNo")).child("Transactions").child(invoiceNo+"---"+openingDate).child("billUpload");
                            riversRef.delete();
                            billUpload=null;
                            billImg.setImageResource(android.R.drawable.ic_menu_upload);
                            cProgressDialog.dismiss();
                        }
                    });
                    alertdialog.setNegativeButton("Upload Image", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent gallery=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            gallery.setType("image/*");
                            gallery.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(gallery,"Select Picture"),1);
                        }
                    });
                    AlertDialog dialog=alertdialog.create();
                    dialog.show();
                }
            }
        });
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(viewtrans.this,payments.class);
                intent.putExtra("flag",0);
                intent.putExtra("partyName",extras.getString("name"));
                intent.putExtra("GSTNo",extras.getString("gstNo"));
                intent.putExtra("InvoiceNo",invoiceNo);
                intent.putExtra("openingDate",openingDate);
                startActivity(intent);
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
                    billImg.setImageURI(imageUri);}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public boolean validateDate(String date){
        if(date.isEmpty()||date.trim().equals("")){
            System.out.println("BUT ITS RETURING FALSE");
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
