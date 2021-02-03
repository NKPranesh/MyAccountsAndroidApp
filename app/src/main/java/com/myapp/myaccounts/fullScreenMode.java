package com.myapp.myaccounts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
public class fullScreenMode extends AppCompatActivity {
    StorageReference refBillUpload,refWayBillUpload;
    ImageView fullImageViewId;
    FloatingActionButton downloadButton;
    String ip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_mode);
        fullImageViewId = findViewById(R.id.fullImageViewID);
        downloadButton=findViewById(R.id.downloadButtonId);
        final Bundle extras=getIntent().getExtras();
        String name=extras.getString("name");
        String gstNo=extras.getString("gstNo");
        String invoiceNo=extras.getString("invoiceNo");
        String date=extras.getString("date");
        String billType=extras.getString("billtype");
        ip=IPAddress.getLocalIpAddress().replace(".","-");
        if(billType.equals("1")){
            refBillUpload= FirebaseStorage.getInstance().getReference().child(ip).child(extras.getString("listType")).child(name+"---"+gstNo).child("Transactions").child(invoiceNo+"---"+date).child("billUpload");
            refBillUpload.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    fullImageViewId.setImageBitmap(bitmap);
                }
            });
            downloadButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    refBillUpload.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
//                            downloadFile(getApplicationContext(),"billUpload",".jpg",uri.toString());
                            Context context = getApplicationContext();
                            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//                            Uri uri=Uri.parse(url);
                            DownloadManager.Request request = new DownloadManager.Request(uri);
                            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                            request.setDestinationInExternalFilesDir(context, "DIRECTORY_DOWNLOAD", "billUpload" + ".jpg");
                            downloadManager.enqueue(request);
                        }
                    });
                }
            });
        }

    }
}
