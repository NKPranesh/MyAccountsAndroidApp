package com.myapp.myaccounts;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.io.Serializable;
import java.util.List;
class transactionRowInfo implements Serializable
{
    public String invoiceNo,Date,paymentStatus,dnt;

    public transactionRowInfo(String transactionNo, String date,String paymentStatus,String dnt) {
        this.invoiceNo = transactionNo;
        Date = date;
        this.paymentStatus=paymentStatus;
        this.dnt=dnt;
    }

    public String getDnt() {
        return dnt;
    }

    public void setDnt(String dnt) {
        this.dnt = dnt;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getTransactionNo() {
        return invoiceNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.invoiceNo = transactionNo;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }
}
public class TransactionsAdapter extends RecyclerView.Adapter<TransactionsAdapter.ViewHolder> {
    String name="",gstNo="",ipAddress;
    Context context;
    List<transactionRowInfo> fetchedDataList;
    int flag;
    Intent intent;
    public TransactionsAdapter(){}
    public TransactionsAdapter(Context context, List<transactionRowInfo> fetchedDataList) {
        this.context = context;
        this.fetchedDataList = fetchedDataList;
    }

    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_row,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        ipAddress=IPAddress.getLocalIpAddress().replace(".","-");
        holder.transactionNumber.setText(fetchedDataList.get(position).invoiceNo);
        //System.out.println(fetchedDataList.size());
        if(!fetchedDataList.get(position).Date.equals(""))
        {
            holder.date.setText("Date: "+fetchedDataList.get(position).Date);
            holder.payButton.setVisibility(View.VISIBLE);
        }
        else {
            holder.date.setText("");
            holder.payButton.setVisibility(View.GONE);
        }
        if (fetchedDataList.get(position).paymentStatus.equals("1"))
        {
            holder.payButton.setVisibility(View.GONE);
        }
        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popupMenu=new PopupMenu(context,v);
                popupMenu.getMenuInflater().inflate(R.menu.popip_menu,popupMenu.getMenu());
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
                        alertDialog.setTitle("Delete");
                        alertDialog.setMessage("Do you want to delete the transaction? This will delete all its record.");
                        alertDialog.setCancelable(true);
                        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String data=holder.transactionNumber.getText()+"---"+holder.date.getText().toString().split(": ")[1];
                                System.out.println(name+"---"+gstNo+"  "+data);
                                if(flag==0)
                                {
                                    FirebaseDatabase.getInstance().getReference().child(ipAddress).child("purchasePartyList").child(name+"---"+gstNo).child("Transactions").child(data).removeValue();
                                    FirebaseStorage.getInstance().getReference().child(ipAddress).child("purchasePartyList").child(name+"---"+gstNo).child("Transactions").child(data).child("billUpload").delete();
                                    FirebaseStorage.getInstance().getReference().child(ipAddress).child("purchasePartyList").child(name+"---"+gstNo).child("Transactions").child(data).child("wayBillUpload").delete();

                                }
                                else
                                {
                                    FirebaseDatabase.getInstance().getReference().child(ipAddress).child("salePartyList").child(name+"---"+gstNo).child("Transactions").child(data).removeValue();
                                    FirebaseStorage.getInstance().getReference().child(ipAddress).child("salePartyList").child(name+"---"+gstNo).child("Transactions").child(data).child("billUpload").delete();
                                    FirebaseStorage.getInstance().getReference().child(ipAddress).child("salePartyList").child(name+"---"+gstNo).child("Transactions").child(data).child("wayBillUpload").delete();
                                }
                                Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_LONG).show();
                                fetchedDataList.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position,fetchedDataList.size());
                            }
                        });
                        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        AlertDialog dialog=alertDialog.create();
                        dialog.show();
                        return true;
                    }
                });
                return true;
            }
        });
        holder.payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context,payments.class);
                intent.putExtra("flag",flag);
                intent.putExtra("partyName",name);
                intent.putExtra("GSTNo",gstNo);
                intent.putExtra("InvoiceNo",fetchedDataList.get(position).invoiceNo);
                intent.putExtra("openingDate",fetchedDataList.get(position).Date);
                context.startActivity(intent);
            }
        });
    }

    public int getItemCount() {
        return fetchedDataList.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView transactionNumber,date;
        public CardView cardView;
        public Button payButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionNumber=itemView.findViewById(R.id.transactionNoID);
            date=itemView.findViewById(R.id.transactiondateID);
            payButton=itemView.findViewById(R.id.paymentsButtonID);
            cardView=itemView.findViewById(R.id.cardID);
            cardView.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            if(flag==0)
                intent=new Intent(context,viewtrans.class);
            else
                intent=new Intent(context,viewTransactionSale.class);
            intent.putExtra("name",name);
            intent.putExtra("gstNo",gstNo);
            intent.putExtra("invoiceNo",transactionNumber.getText().toString());
            intent.putExtra("date",date.getText().toString().split(": ")[1]);
            context.startActivity(intent);
        }
    }
    public void filterList(List<transactionRowInfo> filteredList){
        fetchedDataList=filteredList;
        if(fetchedDataList.size()==0){
            fetchedDataList.clear();
            transactionRowInfo empty=new transactionRowInfo("No Transaction found","","1","999999");
            fetchedDataList.add(empty);
        }

        notifyDataSetChanged();
    }
}
