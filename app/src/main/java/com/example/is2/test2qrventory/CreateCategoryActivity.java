package com.example.is2.test2qrventory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.is2.test2qrventory.connection.CategoryAccess;
import com.example.is2.test2qrventory.connection.DomainAccess;
import com.example.is2.test2qrventory.connection.VolleyResponseListener;
import com.example.is2.test2qrventory.model.Category;
import com.example.is2.test2qrventory.model.Domain;
import com.example.is2.test2qrventory.model.User;
import com.github.clans.fab.FloatingActionButton;

import java.io.IOException;

public class CreateCategoryActivity extends AppCompatActivity implements VolleyResponseListener {

    private int PICK_IMAGE_REQUEST = 1;
    private Button loadImage;
    private FloatingActionButton fab_save;
    private EditText textEditName;
    private EditText textEditDescription;
    private Bitmap image_upload = null;
    private Toast toast;

    private User user = null;
    private long domain_id = 0;
    private int category_parent_id = 0;
    private Category category_to_save = null;
    private Category category_response = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_category);

        textEditName = (EditText) findViewById(R.id.name_category);
        textEditDescription = (EditText) findViewById(R.id.description_category);

        fab_save = (FloatingActionButton) findViewById(R.id.fab_save_category);
        fab_save.setOnClickListener(onSaveCategoryHandler);

        user = getIntent().getParcelableExtra("user");

        domain_id = getIntent().getLongExtra("domain_id", 0);
        category_parent_id = getIntent().getIntExtra("category_parent_id", 0);

        loadImage = (Button) findViewById(R.id.buttonLoadImage_category);
        loadImage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onButtonClickLoadImage();
            }
        });
    }

    View.OnClickListener onSaveCategoryHandler = new View.OnClickListener() {
        public void onClick(View v) {
            saveCategory();
        }
    };

    private void saveCategory() {
        // save into database
        String name = textEditName.getText().toString();
        String description = textEditDescription.getText().toString();

        category_to_save = new Category();
        category_to_save.setName(name);
        category_to_save.setDescription(description);

        if(image_upload != null) {
            category_to_save.setImageURL(image_upload.toString());
        }

        if(domain_id != 0 && category_parent_id != 0) {
            CategoryAccess categoryAccess = new CategoryAccess(user.getApiKey());
            categoryAccess.addCategory(this, category_to_save, domain_id, category_parent_id);
        }
    }

    public void onButtonClickLoadImage()  {
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                image_upload = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));

                //ImageView imageView = (ImageView) findViewById(R.id.imageView);
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String message) {

    }

    @Override
    public void onResponse(Object response) {
        String message = "Failed to Save";
        if(response != null) {
            category_response = (Category) response;
            message = "Saved";
        }
        ShowToastMessage(message);
    }

    private void ShowToastMessage(String message) {
        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /*@Override
    public void onBackPressed()
    {

        super.onBackPressed();
    }*/
}
