package com.damianogiusti.taskdecorator.example;

import com.damianogiusti.taskdecorator.SimpleTask;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public class HowAreYouTask extends SimpleTask<String> {

  @Override protected String run(Void param) throws Exception {
    return "How are you?";
  }
}
