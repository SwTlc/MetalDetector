package com.example.metaldetector;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openGraph(View view) {
        Intent intent = new Intent(this,Graph.class);
        startActivity(intent);
    }
    // graph.class = nome activity 1

   public void openAxes(View view) {
      Intent intent = new Intent(this,Axes.class);
      startActivity(intent);
   }
    // Axes.class = nome activity 2

}

