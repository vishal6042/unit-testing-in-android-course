package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.hamcrest.CoreMatchers;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class FetchUserProfileUseCaseSyncTest {

    private static final String USER_ID = "user_id";
    private static final String FULL_NAME = "full_name";
    private static final String IMAGE_URL = "image_url";
    UserProfileHttpEndpointSyncTD mUserProfileHttpEndpointSyncTD;
    UserCacheTD mUserCacheTD;
    FetchUserProfileUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {

        mUserProfileHttpEndpointSyncTD = new UserProfileHttpEndpointSyncTD();
        mUserCacheTD = new UserCacheTD();
        SUT = new FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSyncTD, mUserCacheTD);
    }

    @Test
    public void test_success_fetchUserProfile_userIdCached() {
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUserProfileHttpEndpointSyncTD.mUserId, CoreMatchers.is(USER_ID));
    }

    @Test
    public void test_fetchUserProfile_EndPointUserDetails() {
        SUT.fetchUserProfileSync(USER_ID);
        User cachedUser = mUserCacheTD.getUser(USER_ID);
        Assert.assertThat(cachedUser.getUserId(), CoreMatchers.is(USER_ID));
        Assert.assertThat(cachedUser.getFullName(), CoreMatchers.is(FULL_NAME));
        Assert.assertThat(cachedUser.getImageUrl(), CoreMatchers.is(IMAGE_URL));
    }

    @Test
    public void test_generalError_notCached() {
        mUserProfileHttpEndpointSyncTD.mIsGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUserCacheTD.getUser(USER_ID), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void test_serverError_notCached() {
        mUserProfileHttpEndpointSyncTD.mIsServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUserCacheTD.getUser(USER_ID), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void test_authError_notCached() {
        mUserProfileHttpEndpointSyncTD.mIsAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUserCacheTD.getUser(USER_ID), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void test_networkError_notCached() {
        mUserProfileHttpEndpointSyncTD.mIsNetworkError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUserCacheTD.getUser(USER_ID), CoreMatchers.is(CoreMatchers.nullValue()));
    }

    @Test
    public void test_successReturned_cached() {
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, CoreMatchers.is(FetchUserProfileUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void test_generalErrorReturned_cached() {
        mUserProfileHttpEndpointSyncTD.mIsGeneralError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, CoreMatchers.is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void test_serverErrorReturned_cached() {
        mUserProfileHttpEndpointSyncTD.mIsServerError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, CoreMatchers.is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void test_AuthErrorReturned_cached() {
        mUserProfileHttpEndpointSyncTD.mIsAuthError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, CoreMatchers.is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void test_networkErrorReturned_cached() {
        mUserProfileHttpEndpointSyncTD.mIsNetworkError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, CoreMatchers.is(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }


    public class UserProfileHttpEndpointSyncTD implements UserProfileHttpEndpointSync {

        public String mUserId;
        public boolean mIsGeneralError;
        public boolean mIsServerError;
        public boolean mIsNetworkError;
        public boolean mIsAuthError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            this.mUserId = userId;
            if (mIsGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (mIsAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else if (mIsServerError) {
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "", "", "");
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, USER_ID, FULL_NAME, IMAGE_URL);
            }
        }
    }

    public class UserCacheTD implements UsersCache {

        private List<User> mUserList = new ArrayList<>(1);

        @Override
        public void cacheUser(User user) {
            User existingUser = getUser(user.getUserId());
            if (existingUser != null) {
                mUserList.remove(existingUser);
            }
            mUserList.add(user);
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            for (User user : mUserList) {
                if (user.getUserId().equals(userId)) {
                    return user;
                }
            }
            return null;
        }
    }
}