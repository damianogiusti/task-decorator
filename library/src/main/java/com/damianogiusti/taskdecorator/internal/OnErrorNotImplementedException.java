package com.damianogiusti.taskdecorator.internal;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public class OnErrorNotImplementedException extends RuntimeException {

  public OnErrorNotImplementedException() {
    super("OnError not implemented!");
  }
}