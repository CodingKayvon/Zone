package com.example.zone.ModelClasses

data class Chat(
    private var sender: String? = "",
    private var message: String? = "",
    private var receiver: String? = "",
    private var isseen: Boolean? = null,
    private var url: String? = "",
    private var messageId: String? = ""
) {

    fun getSender(): String {
        return sender!!
    }

    fun setSender(sender: String) {
        this.sender = sender
    }

    fun getMessage(): String {
        return message!!
    }

    fun setMessage(message: String) {
        this.message = message
    }

    fun getReceiver(): String {
        return receiver!!
    }

    fun setReceiver(receiver: String){
        this.receiver = receiver
    }

    fun getIsSeen(): Boolean {
        return isseen!!
    }

    fun setIsSeen(isseen: Boolean) {
        this.isseen = isseen
    }

    fun getUrl(): String {
        return url!!
    }

    fun setUrl(url: String) {
        this.url = url
    }

    fun getMessageId(): String {
        return messageId!!
    }

    fun setMessageId(messageId: String){
        this.messageId = messageId
    }

}