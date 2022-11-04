package com.example.securenotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

public class UpdateNoteActivity extends AppCompatActivity {
    EditText et_title;
    EditText et_description;
    ImageView btn_updateNote;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);
        et_title = findViewById(R.id.titleInput);
        et_description = findViewById(R.id.descriptionInput);
        btn_updateNote = findViewById(R.id.updateBtn);

        Intent i = getIntent();
        et_title.setText(i.getStringExtra("title"));
        et_description.setText(i.getStringExtra("description"));
        String id = i.getStringExtra("id");

        btn_updateNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(et_title.getText().toString()) && !TextUtils.isEmpty(et_description.getText().toString())) {
                    NotesDatabase db = new NotesDatabase(UpdateNoteActivity.this);
                    db.updateNote(id, et_title.getText().toString(), et_description.getText().toString());
                    startActivity(new Intent(UpdateNoteActivity.this, DashboardScreen.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                } else {
                    Toast.makeText(UpdateNoteActivity.this, "Fields are Empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}