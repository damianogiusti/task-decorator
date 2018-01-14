package com.damianogiusti.taskdecorator.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.damianogiusti.taskdecorator.TaskBag;

public class MainActivity extends AppCompatActivity {

  private TaskBag taskBag = new TaskBag();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override protected void onStart() {
    super.onStart();

    new HelloWorldTask()
            .onSuccess(param -> Toast.makeText(getApplicationContext(), param, Toast.LENGTH_LONG).show())
            .onCompleted(() -> Toast.makeText(getApplicationContext(), "Completed!", Toast.LENGTH_SHORT).show())
            .andThen(new HowAreYouTask()
                    .onSuccess(param -> Toast.makeText(getApplicationContext(), param, Toast.LENGTH_LONG).show()))
            .execute()
            .addTo(taskBag);
  }

  @Override protected void onStop() {
    super.onStop();
    taskBag.clear();
  }
}
