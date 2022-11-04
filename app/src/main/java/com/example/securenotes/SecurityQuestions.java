package com.example.securenotes;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SecurityQuestions extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    MainActivity m = new MainActivity();
    QuestionsDatabase myDbQ;
    UserPassDatabase myDbUP;
    EditText ans,resetU,resetP;
    Button btnSubmit;
    TextView queTextView;
    String que;
    Cursor cursor;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_questions);

        myDbQ = new QuestionsDatabase(SecurityQuestions.this);
        myDbUP = new UserPassDatabase(this);
        cursor = myDbQ.getAllData();
        Spinner spinner = findViewById(R.id.spinner);
        ans = findViewById(R.id.editTextAnswer);
        queTextView = findViewById(R.id.textViewQuestion);
        btnSubmit = findViewById(R.id.buttonSubmit);
        resetP = findViewById(R.id.editTextPasswordReset);
        resetU = findViewById(R.id.editTextUsernameReset);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.questions, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        if(cursor.getCount()==0){
            Toast.makeText(SecurityQuestions.this, "Answer any one of the security question", Toast.LENGTH_SHORT).show();
            spinner.setVisibility(View.VISIBLE);
            queTextView.setVisibility(View.INVISIBLE);
            resetP.setVisibility(View.INVISIBLE);
            resetU.setVisibility(View.INVISIBLE);
        }

        else{
            spinner.setVisibility(View.INVISIBLE);
            queTextView.setVisibility(View.VISIBLE);
            resetU.setVisibility(View.INVISIBLE);
            resetP.setVisibility(View.INVISIBLE);
            cursor.moveToNext();
            queTextView.setText(m.decrypt(cursor.getString(0)));
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if(cursor.getCount()==0) {
                    String answer = ans.getText().toString();
                    myDbQ.insertData(m.encrypt(que), m.encrypt(answer));
                    startActivity(new Intent(SecurityQuestions.this, MainActivity.class));
                    return;
                }
                else{
                    String answer = ans.getText().toString();

                    if(answer.equals(m.decrypt(cursor.getString(1)))){
                        queTextView.setVisibility(View.INVISIBLE);
                        resetP.setVisibility(View.VISIBLE);
                        resetU.setVisibility(View.VISIBLE);
                        ans.setVisibility(View.INVISIBLE);
                        btnSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myDbUP.deleteRecord();
                                myDbUP.insertData(m.encrypt(resetU.getText().toString()),m.encrypt(resetP.getText().toString()));
                                Toast.makeText(SecurityQuestions.this, "Credentials Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SecurityQuestions.this,MainActivity.class));
                                return;
                            }
                        });
                    }
                    else
                        Toast.makeText(SecurityQuestions.this, "Wrong answer!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        que = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}