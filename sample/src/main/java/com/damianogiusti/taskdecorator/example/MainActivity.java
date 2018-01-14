package com.damianogiusti.taskdecorator.example;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.damianogiusti.taskdecorator.TaskBag;

import timber.log.Timber;

public class MainActivity extends AppCompatActivity {

  private TaskBag taskBag = new TaskBag();

  @Override protected void attachBaseContext(Context newBase) {
    super.attachBaseContext(newBase);
    Timber.plant(new Timber.DebugTree());
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  @Override protected void onStart() {
    super.onStart();

    new HelloWorldTask()
            .onSuccess(param -> Toast.makeText(getApplicationContext(), param, Toast.LENGTH_LONG).show())
            .onError(Timber::e)
            .onCompleted(() -> {
              throw new IllegalStateException("Crash bitch!");
            })
            .andThen(new HowAreYouTask()
                    .onSuccess(param -> Toast.makeText(getApplicationContext(), param, Toast.LENGTH_LONG).show())
                    .onError(Timber::e)
                    .onCompleted(() -> Timber.d("Completed!")))
            .execute()
            .addTo(taskBag);
  }

  @Override protected void onStop() {
    super.onStop();
    taskBag.clear();
  }
}
