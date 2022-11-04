package com.example.securenotes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class DashboardScreen extends AppCompatActivity {

    List<Note> mList = new ArrayList<>();
    NotesDatabase databaseClass;
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_screen);
        layout = findViewById(R.id.layout);

        FloatingActionButton addNoteBtn = findViewById(R.id.addnewnotebtn);
        addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardScreen.this,AddNoteActivity.class));
            }
        });

        databaseClass = new NotesDatabase(this);
        fetchAllNotesFromDatabase();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(getApplicationContext(),mList);
        recyclerView.setAdapter(myAdapter);

        ImageView deleteAllNotes = findViewById(R.id.deleteAll);
        deleteAllNotes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashboardScreen.this, "Click again to delete all notes!", Toast.LENGTH_SHORT).show();
                deleteAllNotes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteAllNotes();
                    }
                });
            }
        });

        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(recyclerView);
    }
    private void deleteAllNotes() {
        databaseClass.deleteAllNotes();
        recreate();
    }

    public void recreate(){
        fetchAllNotesFromDatabase();
        mList = new ArrayList<>();
        myAdapter = new MyAdapter(getApplicationContext(),mList);
        recyclerView.setAdapter(myAdapter);
    }

    private void fetchAllNotesFromDatabase() {
        Cursor cursor = databaseClass.getAllNotes();

        if (cursor.getCount()==0){

        }else{
            if (mList == null){
                mList = new ArrayList<>();
            }

            while (cursor.moveToNext()){
                mList.add(new Note(cursor.getString(0),cursor.getString(1),cursor.getString(2)));
            }
        }

    }

    ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Note item = myAdapter.getList().get(position);
            myAdapter.removeItem(viewHolder.getAdapterPosition());
            Snackbar snackbar=Snackbar.make(layout,"Item Deleted",Snackbar.LENGTH_LONG)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            myAdapter.restoreItem(item,position);
                            recyclerView.scrollToPosition(position);
                        }
                    }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                        @Override
                        public void onDismissed(Snackbar transientBottomBar, int event) {
                            super.onDismissed(transientBottomBar, event);

                            if (!(event ==DISMISS_EVENT_ACTION)){
                                databaseClass.deleteSingleItem(item.getId());
                            }
                        }
                    });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    };
}