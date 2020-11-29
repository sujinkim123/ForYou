package org.techtown.foryou;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {

    private EditText et_id, et_password, et_name, et_age;
    private Button btn_register;

    @Override
    protected void onCreate(Bundle savedInstanceState) { //액티비티 시작시 처음으로 실행되는 생명주기
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //아이디 값 찾아주기
        et_id = findViewById(R.id.et_id);
        et_password = findViewById(R.id.et_password);
        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);

        //회원가입 버튼 클릭 시 수행
        btn_register = findViewById(R.id.btn_register);
        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Edittext에 현재 입력되어있는 값을 get(가져온다)해준다.
                String userID = et_id.getText().toString();
                String userPassword = et_password.getText().toString();
                String userName = et_name.getText().toString();
                int userAge = Integer.parseInt(et_age.getText().toString());

                Log.d("로그", "btn_register onClick: ");
                Log.d("로그", " userID -> " + userID);
                Log.d("로그", " userPassword -> " + userPassword);
                Log.d("로그", " userName -> " + userName);
                Log.d("로그", " userAge -> " + userAge);


                Response.Listener<String> responseListener = new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            boolean success = jsonObject.getBoolean("success");
                            Log.d("로그", " response -> " + response);
                            if (success){ //회원등록에 성공한 경우
                                Toast.makeText(RegisterActivity.this,"회원등록에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(intent);
                                Log.d("로그","onResponse: 성공");
                            } else {//회원등록에 실패한 경우
                                Toast.makeText(RegisterActivity.this,"회원등록에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                Log.d("로그","onResponse: 실패");
                            }
                        } catch (JSONException e) {
                           System.out.println("JSONException =>"+ e);
                           e.printStackTrace();
                        }


                    }
                };
                //서버로 Volley를 이용해서 요청을 함
                RegisterRequest registerRequest = new RegisterRequest(userID, userPassword, userName, userAge, responseListener);
                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                queue.add(registerRequest);

            }
        });

    }
}