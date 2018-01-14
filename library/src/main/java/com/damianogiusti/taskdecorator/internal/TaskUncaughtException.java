package com.damianogiusti.taskdecorator.internal;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public class TaskUncaughtException extends RuntimeException {

  public TaskUncaughtException(Throwable cause) {
    super(cause);
  }
}
