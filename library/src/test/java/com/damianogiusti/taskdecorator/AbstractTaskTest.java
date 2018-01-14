package com.damianogiusti.taskdecorator;

import com.damianogiusti.taskdecorator.internal.Function;
import com.damianogiusti.taskdecorator.internal.UnaryFunction;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Damiano Giusti on 14/01/18.
 */
@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class AbstractTaskTest {

  @Mock private BaseAsyncTask mockAsyncTask;
  @Mock private AsyncTaskFactory mockFactory;

  private AbstractTask<Void, String> successfulTask;
  private AbstractTask<Void, String> failureTask;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    successfulTask = new SuccessfulTask();
    failureTask = new FailureTask();
  }

  @Test
  public void tasksSetUpCorrectly() throws Exception {
    assertNotNull(successfulTask);
    assertNotNull(failureTask);
    assertTrue(successfulTask instanceof SuccessfulTask);
    assertTrue(failureTask instanceof FailureTask);
  }

  @Test
  public void whenExecutedThenExecuteAsyncTask() throws Exception {
    mockAsyncTaskFactory(successfulTask);
    mockAsyncTaskFactory(failureTask);

    successfulTask.execute();
    failureTask.execute();
    //noinspection unchecked
    verify(mockAsyncTask, times(2)).execute(eq(null));
  }

  @Test
  public void whenCalledThenSetCallbacks() throws Exception {
    UnaryFunction<String> success = newUnaryFunction();
    UnaryFunction<Throwable> failure = newUnaryFunction();
    Function started = newFunction();
    Function completed = newFunction();

    successfulTask.onSuccess(success);
    successfulTask.onError(failure);
    successfulTask.onStarted(started);
    successfulTask.onCompleted(completed);

    assertEquals(success, successfulTask.onSuccess);
    assertEquals(failure, successfulTask.onError);
    assertEquals(started, successfulTask.onStarted);
    assertEquals(completed, successfulTask.onCompleted);
  }

  @Test
  public void whenExecutedWhenInvokeOnStarted() throws Exception {
    Function startedBlock = mock(Function.class);
    successfulTask
            .onStarted(startedBlock)
            .execute();
    verify(startedBlock, times(1)).invoke();
  }

  @Test
  public void whenSuccessThenInvokeCallback() throws Exception {
    successfulTask
            .onSuccess(new UnaryFunction<String>() {
              @Override public void invoke(String param) throws Exception {
                assertEquals(SuccessfulTask.RESULT, param);
              }
            })
            .execute();
  }

  @Test
  public void whenSuccessThenInvokeOnCompleted() throws Exception {
    Function completedBlock = mock(Function.class);
    successfulTask
            .onCompleted(completedBlock)
            .onSuccess(AbstractTaskTest.<String>newUnaryFunction())
            .execute();
    verify(completedBlock, times(1)).invoke();
  }

  @Test
  public void whenFailureThenInvokeCallback() throws Exception {
    failureTask
            .onError(new UnaryFunction<Throwable>() {
              @Override public void invoke(Throwable param) throws Exception {
                assertTrue(FailureTask.RESULT == param.getClass());
              }
            })
            .execute();
  }

  @Test
  public void whenFailureThenDoNotInvokeOnSuccess() throws Exception {
    UnaryFunction<String> successBlock = mock(UnaryFunction.class);
    failureTask
            .onSuccess(successBlock)
            .onError(AbstractTaskTest.<Throwable>newUnaryFunction())
            .execute();
    verify(successBlock, never()).invoke(anyString());
  }

  @Test
  public void whenFailureThenDoNotInvokeOnCompleted() throws Exception {
    Function completedBlock = mock(Function.class);
    failureTask
            .onCompleted(completedBlock)
            .onError(AbstractTaskTest.<Throwable>newUnaryFunction())
            .execute();
    verify(completedBlock, never()).invoke();
  }

  @Test(expected = RuntimeException.class)
  public void whenFailureWithNoCallbackThenThrowException() throws Exception {
    failureTask.execute();
  }

  ///////////////////////////////////////////////////////////////////////////
  // Private methods
  ///////////////////////////////////////////////////////////////////////////

  private static <T> UnaryFunction<T> newUnaryFunction() {
    return new UnaryFunction<T>() {
      @Override public void invoke(T param) throws Exception {
      }
    };
  }

  private static Function newFunction() {
    return new Function() {
      @Override public void invoke() throws Exception {
      }
    };
  }

  private void mockAsyncTaskFactory(AbstractTask<Void, String> task) throws Exception {
    Field asyncTaskFactoryField = getFactoryField();
    when(mockFactory.createAsyncTask(ArgumentMatchers.<AbstractTask<Object, Object>>any()))
            .thenReturn(mockAsyncTask);
    asyncTaskFactoryField.set(task, mockFactory);
  }

  private Field getFactoryField() throws Exception {
    Field asyncTaskFactoryField = AbstractTask.class.getDeclaredField("asyncTaskFactory");
    asyncTaskFactoryField.setAccessible(true);
    return asyncTaskFactoryField;
  }
}