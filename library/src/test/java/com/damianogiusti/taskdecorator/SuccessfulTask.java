package com.damianogiusti.taskdecorator;

import com.damianogiusti.taskdecorator.internal.UnaryFunction;

import static org.junit.Assert.assertEquals;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public class SuccessfulTask extends AbstractTask<Void, String> {

  public static final String RESULT = "Hello, I'm a concrete task";

  @Override protected String run(Void param) throws Exception {
    return RESULT;
  }

  static class OnSuccessCallback implements UnaryFunction<String> {

    @Override public void invoke(String param) throws Exception {
      assertEquals(RESULT, param);
    }
  }
}
