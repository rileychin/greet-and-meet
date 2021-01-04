package com.example.greetmeet_v1;

import android.content.*;


public class SessionManager {
    //Initialize Variable
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public SessionManager(Context context){
        sharedPreferences = context.getSharedPreferences("AppKey",0);
        editor = sharedPreferences.edit();
        editor.apply();
    }

    //set login method
    public void setLogin(boolean login){
        editor.putBoolean("KEY_LOGIN",login);
        editor.commit();
    }

    //Create get login method
    public boolean getLogin(){
        return sharedPreferences.getBoolean("KEY_LOGIN",false);
    }

    //Create set username
    public void setUsername(String username){
        editor.putString("KEY_USERNAME",username);
        editor.commit();
    }

    //Create get username
    public String getUsername(){
        return sharedPreferences.getString("KEY_USERNAME", "");
    }

}