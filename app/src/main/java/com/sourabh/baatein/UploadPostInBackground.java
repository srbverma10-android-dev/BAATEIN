package com.sourabh.baatein;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.webkit.MimeTypeMap;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class UploadPostInBackground extends Service {

    private StorageReference storageReferenceToProfile = FirebaseStorage.getInstance().getReferenceFromUrl("gs://baatein-3bd82.appspot.com").child("POST");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getApplicationContext(),"Uploading", Toast.LENGTH_SHORT).show();

        final String myID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        final StorageReference imageRef = storageReferenceToProfile.child(myID).child(System.currentTimeMillis() + "." + getFileExtension(ShowBeforeUploadActivity.newImageUri));

        imageRef.putFile(ShowBeforeUploadActivity.newImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        final String downloadUrl = uri.toString();

                        final DatabaseReference databaseReferenceToPosts = FirebaseDatabase.getInstance().getReference("POSTS").push();
                        databaseReferenceToPosts.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {

                                HashMap<String , String> hashMap = new HashMap<>();
                                hashMap.put("image", downloadUrl);
                                hashMap.put("from", myID);
                                hashMap.put("uploadedAt", Long.toString(System.currentTimeMillis()));
                                hashMap.put("deleteAt", Long.toString(System.currentTimeMillis()+604800000));
                                hashMap.put("postId", snapshot.getRef().getKey());
                                hashMap.put("caption", ShowBeforeUploadActivity.caption.getText().toString());

                                databaseReferenceToPosts.setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        stopSelf();
                                        Toast.makeText(getApplicationContext(),"UPLOADED SUCCESSFULLY", Toast.LENGTH_SHORT).show();

                                    }
                                });

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Failed", Toast.LENGTH_SHORT).show();
            }
        });

        return START_STICKY;
    }

    private String getFileExtension(Uri uri){
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
