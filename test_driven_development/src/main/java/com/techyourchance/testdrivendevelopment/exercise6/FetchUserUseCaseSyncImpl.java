package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync {
    FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;
    UsersCache mUsersCache;

    public FetchUserUseCaseSyncImpl(FetchUserHttpEndpointSync mFetchUserHttpEndpointSync, UsersCache mUsersCache) {
        this.mFetchUserHttpEndpointSync = mFetchUserHttpEndpointSync;
        this.mUsersCache = mUsersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        FetchUserHttpEndpointSync.EndpointResult result = null;

        try {
            result = mFetchUserHttpEndpointSync.fetchUserSync(userId);
        } catch (NetworkErrorException e) {
            return new UseCaseResult(Status.NETWORK_ERROR, null);
        }
        if (result.getStatus() == FetchUserHttpEndpointSync.EndpointStatus.SUCCESS) {
            mUsersCache.getUser(userId);
            mUsersCache.cacheUser(new User(result.getUserId(), result.getUsername()));
            return new UseCaseResult(Status.SUCCESS, new User(result.getUserId(), result.getUsername()));
        } else {
            return new UseCaseResult(getSTatus(result.getStatus()), null);
        }

    }

    private Status getSTatus(FetchUserHttpEndpointSync.EndpointStatus status) {
        switch (status) {
            case SUCCESS:
                return Status.SUCCESS;
            case AUTH_ERROR:
            case GENERAL_ERROR:
                return Status.FAILURE;
            default:
                throw new RuntimeException();
        }
    }
}
