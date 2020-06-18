package com.example.uberclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    public void onClick(View view) {

        if(edtUserType.getText().toString().equals("Driver") || edtUserType.getText().toString().equals("Passenger")) {

            if (ParseUser.getCurrentUser() == null) {

                ParseAnonymousUtils.logIn(new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {

                        if (user != null && e == null) {
                            FancyToast.makeText(MainActivity.this,"We have an anonymous user",Toast.LENGTH_SHORT,FancyToast.INFO,false).show();

                            user.put("as",edtUserType.getText().toString());

                            user.saveInBackground();

                        }

                    }
                });

            }

        }

    }

    enum State {
        SIGNUP,LOGIN;
    }

    private State state;

    private EditText edtUsername, edtPassword, edtUserType;
    private Button btnSignUp, btnOneTimeLogin;
    private RadioButton btnRadioPassenger, btnRadioDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ParseInstallation.getCurrentInstallation().saveInBackground();
        if (ParseUser.getCurrentUser() != null) {

            //Transition
            ParseUser.logOut();

        }

        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtUserType = findViewById(R.id.edtUserType);

        btnRadioPassenger = findViewById(R.id.btnRadioPassenger);
        btnRadioDriver = findViewById(R.id.btnRadioDriver);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnOneTimeLogin = findViewById(R.id.btnOneTimeLogin);

        btnOneTimeLogin.setOnClickListener(this);

        state = State.SIGNUP;


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(state == State.SIGNUP) {

                    if(btnRadioDriver.isChecked() == false && btnRadioPassenger.isChecked() == false) {

                        Toast.makeText(MainActivity.this,"Are you a driver or a passenger?",Toast.LENGTH_LONG).show();
                        return;

                    }

                    ParseUser appUser = new ParseUser();
                    appUser.setUsername(edtUsername.getText().toString());
                    appUser.setPassword(edtPassword.getText().toString());
                    if(btnRadioDriver.isChecked()) {
                        appUser.put("as","Driver");

                    } else if (btnRadioPassenger.isChecked()) {
                        appUser.put("as","Passenger");

                    }

                    appUser.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(ParseException e) {

                            if (e == null) {

                                FancyToast.makeText(MainActivity.this,"Signed Up!",Toast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();

                            }

                        }
                    });

                } else if (state == State.LOGIN) {

                    if(btnRadioDriver.isChecked() == false && btnRadioPassenger.isChecked() == false) {

                        Toast.makeText(MainActivity.this,"Are you a driver or a passenger?",Toast.LENGTH_LONG).show();
                        return;

                    }

                    ParseUser.logInInBackground(edtUsername.getText().toString(), edtPassword.getText().toString(), new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {

                            if(user != null && e == null) {

                                FancyToast.makeText(MainActivity.this,"User Logged in!",Toast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();

                            }

                        }
                    });

                }

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.my_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {

            case R.id.login_item:

                if(state == State.SIGNUP) {

                    state=State.LOGIN;
                    item.setTitle("Sign Up");
                    btnSignUp.setText("Log In");

                } else if (state==State.LOGIN) {

                    state=State.SIGNUP;
                    item.setTitle("Log In");
                    btnSignUp.setText("Sign Up");

                }

                break;

        }

        return super.onOptionsItemSelected(item);
    }
}