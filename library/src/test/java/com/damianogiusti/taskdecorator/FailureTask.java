package com.damianogiusti.taskdecorator;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public class FailureTask extends AbstractTask<Void, String> {

  public static final Class<? extends Throwable> RESULT = IllegalStateException.class;

  @Override protected String run(Void param) throws Exception {
    throw new IllegalStateException();
  }
}
