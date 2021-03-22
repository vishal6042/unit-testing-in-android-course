package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;


public class UpdateUsernameUseCaseSyncTest {

    public static final String USERID = "userId";
    public static final String USERNAME = "username";


    UpdateUsernameUseCaseSync SUT;
    UpdateUsernameHttpEndpointSync mUpdateUsernameHttpEndpointSyncMock;
    UsersCache mUsersCacheMock;
    EventBusPoster mEventBusPosterMock;

    @Before
    public void setUp() throws Exception {
        mUpdateUsernameHttpEndpointSyncMock = Mockito.mock(UpdateUsernameHttpEndpointSync.class);
        mUsersCacheMock = Mockito.mock(UsersCache.class);
        mEventBusPosterMock = Mockito.mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(mUpdateUsernameHttpEndpointSyncMock,
                mUsersCacheMock, mEventBusPosterMock);
        success();
    }

    @Test
    public void updateUsername_success_userIdAndUsernamePassedToEndpoint() throws Exception {
        SUT.updateUsernameSync(USERID, USERNAME);
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);

        Mockito.verify(mUpdateUsernameHttpEndpointSyncMock, Mockito.times(1))
                .updateUsername(argumentCaptor.capture(), argumentCaptor.capture());
        List<String> args = argumentCaptor.getAllValues();
        Assert.assertThat(args.get(0), CoreMatchers.is(USERID));
        Assert.assertThat(args.get(1), CoreMatchers.is(USERNAME));
    }

    @Test
    public void updateUsername_success_userCached() throws Exception {
        SUT.updateUsernameSync(USERID, USERNAME);
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        Mockito.verify(mUsersCacheMock).cacheUser(argumentCaptor.capture());
        User cachedUser = argumentCaptor.getValue();
        Assert.assertThat(cachedUser.getUserId(), CoreMatchers.is(USERID));
        Assert.assertThat(cachedUser.getUsername(), CoreMatchers.is(USERNAME));
    }

    @Test
    public void updateUsername_generalError_userNotCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USERID, USERNAME);
        Mockito.verifyNoMoreInteractions(mUsersCacheMock);
    }

    @Test
    public void updateUsername_authError_userNotCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USERID, USERNAME);
        verifyNoMoreInteractions(mUsersCacheMock);
    }

    @Test
    public void updateUsername_serverError_userNotCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USERID, USERNAME);
        verifyNoMoreInteractions(mUsersCacheMock);
    }

    @Test
    public void updateUsername_success_loggedInEventPosted() throws Exception {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USERID, USERNAME);
        verify(mEventBusPosterMock).postEvent(ac.capture());
        assertThat(ac.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void updateUsername_generalError_noInteractionWithEventBusPoster() throws Exception {
        generalError();
        SUT.updateUsernameSync(USERID, USERNAME);
        Mockito.verifyNoMoreInteractions(mEventBusPosterMock);

    }

    @Test
    public void updateUsername_authError_noInteractionWithEventBusPoster() throws Exception {
        authError();
        SUT.updateUsernameSync(USERID, USERNAME);
        Mockito.verifyNoMoreInteractions(mEventBusPosterMock);
    }

    @Test
    public void updateUsername_serverError_noInteractionWithEventBusPoster() throws Exception {
        serverError();
        SUT.updateUsernameSync(USERID, USERNAME);
        Mockito.verifyNoMoreInteractions(mEventBusPosterMock);
    }

    @Test
    public void updateUsername_success_successReturned() throws Exception {
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void updateUsername_serverError_failureReturned() throws Exception {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_authError_failureReturned() throws Exception {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_generalError_failureReturned() throws Exception {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void updateUsername_networkError_networkErrorReturned() throws Exception {
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID, USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    private void success() throws Exception {
        Mockito.when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS, USERID, USERNAME));
    }

    private void generalError() throws Exception {
        Mockito.when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR, "", ""));
    }

    private void authError() throws Exception {
        Mockito.when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR, "", ""));
    }

    private void serverError() throws Exception {
        Mockito.when(mUpdateUsernameHttpEndpointSyncMock.updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString()))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR, "", ""));
    }

    private void networkError() throws Exception {
        doThrow(new NetworkErrorException()).
                when(mUpdateUsernameHttpEndpointSyncMock).updateUsername(ArgumentMatchers.anyString(), ArgumentMatchers.anyString());
    }
}