package com.technohack.mobilenumberverification_1;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {

    private String verificationId;

    private Button signBtn;
    private EditText smsCode;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onStart() {
        super.onStart();

        //if userAlready LoggedIn
        if(firebaseAuth.getCurrentUser()!=null)
        {
            Intent intent=new Intent(VerifyPhone.this,Profile.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);


        firebaseAuth=FirebaseAuth.getInstance();

        smsCode=findViewById(R.id.editTextCode);
        signBtn=findViewById(R.id.buttonSignIn);
        progressBar=findViewById(R.id.progressbar);

         //For autoVerification SmsCode
        String phoneNumber=getIntent().getExtras().getString("phone");
        sendVerificationCode(phoneNumber);


        //when user smsCode is not detected automatically and user need to enter it manually

        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String code=smsCode.getText().toString().trim();

                if(code.isEmpty()){

                    smsCode.setError("Enter Code...");
                    smsCode.requestFocus();
                    return;

                }

                //to verify the sms code
                verifyCode(code);

            }
        });



    }

    //to verify the smsCode
    public void verifyCode(String code){

        PhoneAuthCredential credential=PhoneAuthProvider.getCredential(verificationId,code);

        //if smsCode is automatically verified then call the signIn method to allow the user to signIn successfully
        signInWithCredential(credential);

    }

    //for signIn the user using credential

    private void signInWithCredential(PhoneAuthCredential credential) {

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(task.isSuccessful()){

                            Intent intent=new Intent(VerifyPhone.this,Profile.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();


                        }else{

                            Toast.makeText(VerifyPhone.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                        }
                    }
                });


    }

    public void sendVerificationCode(String number){

        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,
                60,
                TimeUnit.SECONDS,
                TaskExecutors.MAIN_THREAD,

                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    //This method called when verification number is sent
                    @Override
                    public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        super.onCodeSent(s, forceResendingToken);

                        //s contains the verificationId
                        verificationId=s;

                    }

                    //when auto verification compeleted
                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {

                        //this will get the sms code for verification
                        String code=phoneAuthCredential.getSmsCode();
                        //after getting the sms code we need to verify is with phone credential

                       if(code!=null){

                           verifyCode(code);

                       }

                    }
                     //is there any error occurred in between verification then this method will called
                    @Override
                    public void onVerificationFailed(FirebaseException e) {

                        Toast.makeText(VerifyPhone.this,"Failed to SignIn",Toast.LENGTH_LONG).show();

                    }

                }

        );

    }






}
