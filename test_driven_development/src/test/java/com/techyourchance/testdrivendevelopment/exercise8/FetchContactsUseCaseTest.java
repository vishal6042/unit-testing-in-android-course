package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.example11.cart.CartItem;
import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import junit.framework.TestCase;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.util.Types;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest extends TestCase {

    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String FULL_PHONE_NUMBER = "fullPhoneNumber";
    public static final String IMAGE_URL = "image-url";
    public static final int AGE = 20;
    public static final String FILTER_TERM = "filterTerm";
    FetchContactsUseCase SUT;
    @Mock
    GetContactsHttpEndpoint mGetContactsHttpEndpointMock;
    @Mock
    FetchContactsUseCase.Listener mListenerMock1;
    @Mock
    FetchContactsUseCase.Listener mListenerMock2;
    @Captor
    private ArgumentCaptor<List<Contact>> mAcList;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchContactsUseCase(mGetContactsHttpEndpointMock);
        success();
    }

    @Test
    public void testCorrectFilterMethodPassedtoEndPoint() {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.getContacts(FILTER_TERM);

        Mockito.verify(mGetContactsHttpEndpointMock).getContacts(ac.capture(), ArgumentMatchers.any(GetContactsHttpEndpoint.Callback.class));
        Assert.assertThat(ac.getValue(), CoreMatchers.is(FILTER_TERM));


    }

    @Test
    public void fetchContacts_success_observersNotifiedWithCorrectData() throws Exception {
        // Arrange
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.getContacts(FILTER_TERM);
        // Assert
        verify(mListenerMock1).onContactFetched(mAcList.capture());
        verify(mListenerMock2).onContactFetched(mAcList.capture());
        List<List<Contact>> captures = mAcList.getAllValues();
        List<Contact> capture1 = captures.get(0);
        List<Contact> capture2 = captures.get(1);
        assertThat(capture1, is(getContactItems()));
        assertThat(capture2, is(getContactItems()));
    }


    @Test
    public void fetchContacts_success_unsubscribedObserversNotNotified() throws Exception {
        // Arrange
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.unregisterListener(mListenerMock2);
        SUT.getContacts(FILTER_TERM);
        // Assert
        verify(mListenerMock1).onContactFetched(any(List.class));
        verifyNoMoreInteractions(mListenerMock2);
    }

    @Test
    public void fetchContacts_generalError_observersNotifiedOfFailure() throws Exception {
        // Arrange
        generaError();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.getContacts(FILTER_TERM);
        // Assert
        verify(mListenerMock1).onFetchContactFailed();
        verify(mListenerMock2).onFetchContactFailed();
    }

    @Test
    public void fetchContacts_networkError_observersNotifiedOfFailure() throws Exception {
        // Arrange
        networkError();
        // Act
        SUT.registerListener(mListenerMock1);
        SUT.registerListener(mListenerMock2);
        SUT.getContacts(FILTER_TERM);
        // Assert
        verify(mListenerMock1).onFetchContactFailed();
        verify(mListenerMock2).onFetchContactFailed();
    }

    private void success() {

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object args[] = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsSucceeded(getListOfContacts());
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(ArgumentMatchers.anyString(), ArgumentMatchers.any(GetContactsHttpEndpoint.Callback.class));
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(ArgumentMatchers.anyString(), ArgumentMatchers.any(GetContactsHttpEndpoint.Callback.class));
    }

    private void generaError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                GetContactsHttpEndpoint.Callback callback = (GetContactsHttpEndpoint.Callback) args[1];
                callback.onGetContactsFailed(GetContactsHttpEndpoint.FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(mGetContactsHttpEndpointMock).getContacts(ArgumentMatchers.anyString(), ArgumentMatchers.any(GetContactsHttpEndpoint.Callback.class));
    }


    private List<ContactSchema> getListOfContacts() {
        List<ContactSchema> list = new ArrayList<>();
        list.add(new ContactSchema(
                ID,
                FULL_NAME,
                FULL_PHONE_NUMBER,
                IMAGE_URL,
                AGE
        ));
        return list;
    }

    private List<Contact> getContactItems() {
        List<Contact> list = new ArrayList<>();
        list.add(new Contact(
                ID,
                FULL_NAME,
                IMAGE_URL
        ));
        return list;
    }
}