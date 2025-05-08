package com.example.zone

import com.example.zone.ModelClasses.Chat
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.Test

class chatTesting {

    @Test
    fun testGetSender() {
        val chat = Chat(sender = "Rosko", message = "Coming in now", receiver = "Dad", isseen = true, url = "", messageId = "msg001")
        assertEquals("Rosko", chat.sender)
    }

    @Test
    fun testSetSender() {
        val chat = Chat(sender = "Mave", message = "Where are you?", receiver = "Alise", isseen = false, url = "", messageId = "msg002")
        chat.sender = "Attacker"
        assertEquals("Attacker", chat.sender)
    }

    @Test
    fun testGetMessage() {
        val chat = Chat(sender = "Dan", message = "Dinner tonight?", receiver = "Jess", isseen = true, url = "", messageId = "msg003")
        assertEquals("Dinner tonight?", chat.message)
    }

    @Test
    fun testSetMessage() {
        val chat = Chat(sender = "Pinkie", message = "pick up...", receiver = "Von", isseen = false, url = "", messageId = "msg004")
        chat.message = "I have PINKIE"
        assertEquals("I have PINKIE", chat.message)
    }

    @Test
    fun testGetReceiver() {
        val chat = Chat(sender = "Mateo", message = "Come around back", receiver = "Leo", isseen = true, url = "", messageId = "msg005")
        assertEquals("Leo", chat.receiver)
    }

    @Test
    fun testSetReceiver() {
        val chat = Chat(sender = "Selena", message = "Ok", receiver = "Alyssa", isseen = true, url = "", messageId = "msg006")
        chat.receiver = "Grog"
        assertEquals("Grog", chat.receiver)
    }

    @Test
    fun testGetIsSeen() {
        val chat = Chat(sender = "Rebeca", message = "5pm sharp", receiver = "Alex", isseen = false, url = "", messageId = "msg007")
        assertTrue(chat.isseen == false)
    }

    @Test
    fun testSetIsSeen() {
        val chat = Chat(sender = "Chris", message = "Cant do", receiver = "Tyler", isseen = false, url = "", messageId = "msg008")
        chat.isseen = true
        assertTrue(chat.isseen == true)
    }

    @Test
    fun testGetUrl() {
        val chat = Chat(sender = "Will", message = "Check this out", receiver = "Peter", isseen = true, url = "https://www.fake.com/120", messageId = "msg009")
        assertTrue(chat.url == "https://www.fake.com/120")
    }

    @Test
    fun testSetUrl() {
        val chat = Chat(sender = "Nia", message = "Beach day!", receiver = "Mariene", isseen = true, url = "https://www.fake.com/120", messageId = "msg010")
        chat.url = "https://www.hacked.com/150"
        assertTrue(chat.url == "https://www.hacked.com/150")
    }

}