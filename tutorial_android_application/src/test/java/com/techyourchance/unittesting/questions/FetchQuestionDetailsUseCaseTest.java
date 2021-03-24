package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.StackoverflowApi;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import junit.framework.TestCase;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest extends TestCase {

    FetchQuestionDetailsUseCase SUT;

    @Mock
    TimeProvider mTimeProviderTD;
    EndpointTd mEndpointTD;

    @Mock
    FetchQuestionDetailsUseCase.Listener mListener1;
    @Mock
    FetchQuestionDetailsUseCase.Listener mListener2;

    @Before
    public void setUp() throws Exception {
        mEndpointTD = new EndpointTd();
        SUT = new FetchQuestionDetailsUseCase(mEndpointTD, mTimeProviderTD);
    }

    @Test
    public void test_fetchQuestionDetailsAndNotify_success_listenerwithCorrectData() {
        ArgumentCaptor<QuestionDetails> argumentCaptor = ArgumentCaptor.forClass(QuestionDetails.class);

        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);

        SUT.fetchQuestionDetailsAndNotify("id");

        Mockito.verify(mListener1).onQuestionDetailsFetched(argumentCaptor.capture());
        Mockito.verify(mListener2).onQuestionDetailsFetched(argumentCaptor.capture());

        QuestionDetails questionDetails = argumentCaptor.getValue();
        Assert.assertThat(questionDetails, CoreMatchers.is(getQuestionDetails()));
    }

    @Test
    public void test_fetchQuestionDetailsAndNotify_failure_listenerWithCorrectData() {

        failure();
        SUT.registerListener(mListener1);
        SUT.registerListener(mListener2);

        SUT.fetchQuestionDetailsAndNotify("id");

        Mockito.verify(mListener1).onQuestionDetailsFetchFailed();
        Mockito.verify(mListener2).onQuestionDetailsFetchFailed();

    }

    private void failure() {
        mEndpointTD.mFailure = true;
    }

    private QuestionDetails getQuestionDetails() {
        return new QuestionDetails("id", "title", "body");
    }

    private static class EndpointTd extends FetchQuestionDetailsEndpoint {

        boolean mFailure;

        public EndpointTd() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(String questionId, Listener listener) {
            if (mFailure) {
                listener.onQuestionDetailsFetchFailed();
            } else {
                listener.onQuestionDetailsFetched(new QuestionSchema("title", questionId, "body"));
            }
        }
    }
}