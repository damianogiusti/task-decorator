package com.damianogiusti.taskdecorator.internal;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
public interface UnaryFunction<T> {

  void invoke(T param) throws Exception;
}
