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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

public class UploadProfileInBackground extends Service {
    private StorageReference storageReferenceToProfile = FirebaseStorage.getInstance().getReferenceFromUrl("gs://baatein-3bd82.appspot.com").child("ProfileImages");

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(getApplicationContext(),"Uploading", Toast.LENGTH_SHORT).show();

        final String myID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

        final StorageReference imageRef = storageReferenceToProfile.child(myID).child(myID + "." + getFileExtension(EditProfileActivity.newImageUri));

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User").child(myID);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()){

                    String pathToDelete = snapshot.child("imageUrl").getValue().toString();

                    StorageReference deleteImageRef = FirebaseStorage.getInstance().getReferenceFromUrl(pathToDelete);
                    deleteImageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            imageRef.putFile(EditProfileActivity.newImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String downloadUrl = uri.toString();

                                            final DatabaseReference databaseReferenceToProfile = FirebaseDatabase.getInstance().getReference("User").child(myID);
                                            databaseReferenceToProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if (snapshot.exists()){

                                                        databaseReferenceToProfile.child("imageUrl").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                Toast.makeText(getApplicationContext(),"Upload Successful", Toast.LENGTH_SHORT).show();
                                                                stopSelf();
                                                            }
                                                        });

                                                    } else {
                                                        Toast.makeText(getApplicationContext(),"in else snapshow does not exist", Toast.LENGTH_SHORT).show();
                                                    }

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


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            imageRef.putFile(EditProfileActivity.newImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            final String downloadUrl = uri.toString();

                                            final DatabaseReference databaseReferenceToProfile = FirebaseDatabase.getInstance().getReference("User").child(myID);
                                            databaseReferenceToProfile.addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                                    if (snapshot.exists()){

                                                        databaseReferenceToProfile.child("imageUrl").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                Toast.makeText(getApplicationContext(),"Upload Successful", Toast.LENGTH_SHORT).show();
                                                                stopSelf();
                                                            }
                                                        });

                                                    } else {
                                                        Toast.makeText(getApplicationContext(),"in else snapshow does not exist", Toast.LENGTH_SHORT).show();
                                                    }

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

                        }
                    });

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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

        Toast.makeText(getApplicationContext(),"UPLOADING BACKGROUND SERVICE IS DESTROYED...",Toast.LENGTH_SHORT).show();

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
