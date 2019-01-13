package com.technohack.mobilenumberverification_1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {


    private Spinner countryCodeSpinner;
    private EditText phoneNumber;
    private Button continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

       countryCodeSpinner=findViewById(R.id.main_spinnerId);
       phoneNumber=findViewById(R.id.mobileNumberId);
       continueBtn=findViewById(R.id.buttonContinueId);


       //for setting the value in the spinner
       countryCodeSpinner.setAdapter(new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_dropdown_item,CountryData.countryNames));

       continueBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               String countryCode=CountryData.countryCode[countryCodeSpinner.getSelectedItemPosition()];

               String number=phoneNumber.getText().toString().trim();

               if(number.isEmpty() || number.length()<10){

                   phoneNumber.setError("Number is required");
                   phoneNumber.requestFocus();
                   return;

               }


               String phone="+"+countryCode+number;

               Intent intent=new Intent(MainActivity.this,VerifyPhone.class);
               intent.putExtra("phone",phone);
               startActivity(intent);


           }
       });

    }
}
