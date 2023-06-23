package com.rachitbhutani.whysave

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rachitbhutani.whysave.database.ContactItemDao
import com.rachitbhutani.whysave.helper.PhoneNumberUtil
import com.rachitbhutani.whysave.helper.WhySaveDataStore
import com.rachitbhutani.whysave.model.ContactItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val contactDao: ContactItemDao,
    private val phoneNumberUtil: PhoneNumberUtil,
    private val dataStore: WhySaveDataStore
) : ViewModel() {

    val contactLiveData = MutableLiveData<List<ContactItem>>()

    val isUserOnboarded = dataStore.isUserOnboarded()

    val detailContactLiveData by lazy {
        mutableDetailContactLiveData
    }
    private val mutableDetailContactLiveData = MutableLiveData<ContactItem?>()

    fun fetchContacts() = viewModelScope.launch(Dispatchers.IO) {
        val contacts =
            contactDao.getAllContacts().sortedByDescending { it.timestamp }
        contactLiveData.postValue(contacts)
    }

    fun insertContact(phone: String) = viewModelScope.launch(Dispatchers.IO) {
        val contactItem = contactDao.findContact(phone)
        val currentTimestamp = System.currentTimeMillis()
        val newContact =
            contactItem?.copy(timestamp = currentTimestamp, logList = ArrayDeque<Long>().apply {
                addAll(contactItem.logList.orEmpty())
                addLast(currentTimestamp)
                while (size > 10) removeFirst()
            }.toList()) ?: ContactItem(
                phone = phone,
                timestamp = currentTimestamp,
                logList = listOf(currentTimestamp)
            )
        contactDao.insertContact(newContact)
        contactLiveData.postValue(
            contactDao.getAllContacts()
                .sortedByDescending { it.logList?.lastOrNull() ?: it.timestamp })
    }

    fun refineRawText(rawText: CharSequence): String {
        return phoneNumberUtil.refinePhoneNumber(rawText.toString())
    }

    fun markUserOnboarded() = viewModelScope.launch {
        dataStore.updateOnboardedKey()
    }

    fun fetchContactByNumber(number: String) = viewModelScope.launch(Dispatchers.IO) {
        val contact = contactDao.findContact(number)
        mutableDetailContactLiveData.postValue(contact)
    }

    fun updateNote(text: String) = viewModelScope.launch(Dispatchers.IO) {
        val newContact = detailContactLiveData.value?.copy(note = text) ?: return@launch
        contactDao.insertContact(newContact)
    }

}