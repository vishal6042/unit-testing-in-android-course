package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.example11.FetchCartItemsUseCase;
import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {
    public interface Listener {
        void onContactFetched(List<Contact> contactList);

        void onFetchContactFailed();
    }

    GetContactsHttpEndpoint mGetContactsHttpEndpoint;
    List<Listener> mListeners = new ArrayList<>();

    public FetchContactsUseCase(GetContactsHttpEndpoint mGetContactsHttpEndpointMock) {
        this.mGetContactsHttpEndpoint = mGetContactsHttpEndpointMock;
    }


    public void getContacts(String filterTerm) {
        mGetContactsHttpEndpoint.getContacts(filterTerm, new GetContactsHttpEndpoint.Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> cartItems) {
                for (Listener listener : mListeners) {
                    listener.onContactFetched(contactItemsFromSchema(cartItems));
                }
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                if (failReason == GetContactsHttpEndpoint.FailReason.GENERAL_ERROR ||
                        failReason == GetContactsHttpEndpoint.FailReason.NETWORK_ERROR) {
                    for (Listener listener : mListeners) {
                        listener.onFetchContactFailed();
                    }
                }
            }
        });

    }

    private List<Contact> contactItemsFromSchema(List<ContactSchema> contactItems) {
        List<Contact> contactList = new ArrayList<>();
        for (ContactSchema schema : contactItems) {
            contactList.add(new Contact(
                    schema.getId(),
                    schema.getFullName(),
                    schema.getImageUrl()
            ));
        }
        return contactList;
    }

    public void registerListener(Listener listener) {
        mListeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        mListeners.remove(listener);
    }
}
