package com.rachitbhutani.whysave

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rachitbhutani.whysave.database.ContactItemDao
import com.rachitbhutani.whysave.model.ContactItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val contactDao: ContactItemDao) : ViewModel() {

    val contactLiveData = MutableLiveData<List<ContactItem>>()

    fun fetchContacts() = viewModelScope.launch(Dispatchers.IO) {
        val contacts = contactDao.getAllContacts()
        contactLiveData.postValue(contacts)
    }

    fun insertContact(phone: String) = viewModelScope.launch(Dispatchers.IO) {
        val contactItem = contactDao.findContact(phone)
        val newContact = contactItem?.copy(timestamp = System.currentTimeMillis()) ?: ContactItem(
            phone = phone,
            timestamp = System.currentTimeMillis()
        )
        contactDao.insertContact(newContact)
    }

}