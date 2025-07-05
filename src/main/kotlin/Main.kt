import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.io.File

// This class holds the contact info and can be saved/loaded as JSON
@Serializable
data class Contact(val name: String, val phone: String, val email: String)

// Where the contacts will be saved on your computer
val filePath = "contacts.json"

// Load contacts from the file, or start with an empty list if no file
fun loadContacts(): MutableList<Contact> {
    val file = File(filePath)
    if (!file.exists()) return mutableListOf()
    val jsonData = file.readText()
    return Json.decodeFromString(jsonData)
}

// Save the contacts list to the file as JSON
fun saveContacts() {
    val jsonData = Json { prettyPrint = true }.encodeToString(contacts)
    File(filePath).writeText(jsonData)
}

// This holds all your contacts in the program
val contacts = loadContacts()

fun main() {
    println("Welcome to Contact Manager!")
    while (true) {
        // Show menu options
        println(
            """
            ===== Contact Manager =====
            1. Add Contact
            2. View Contacts
            3. Search Contact
            4. Edit Contact
            5. Delete Contact
            6. Exit
            Choose an option:
            """.trimIndent()
        )

        when (readLine()?.trim()) {
            "1" -> addContact()
            "2" -> viewContacts()
            "3" -> searchContact()
            "4" -> editContact()
            "5" -> deleteContact()
            "6" -> {
                println("Goodbye!")
                return
            }
            else -> println("Invalid option. Try again.")
        }
    }
}

// Add a new contact to the list
fun addContact() {
    println("Enter contact name:")
    val name = readLine()?.trim().orEmpty()

    // Keep asking for phone number until it's digits only
    var phone: String
    while (true) {
        println("Enter phone number (digits only):")
        phone = readLine()?.trim().orEmpty()
        if (phone.all { it.isDigit() } && phone.isNotEmpty()) break
        println("Invalid phone. Use digits only.")
    }

    // Keep asking for email until it has '@' and ends with '.com'
    var email: String
    while (true) {
        println("Enter email:")
        email = readLine()?.trim().orEmpty()
        if ("@" in email && email.endsWith(".com")) break
        println("Invalid email. Needs '@' and end with '.com'.")
    }

    contacts.add(Contact(name, phone, email))
    saveContacts()
    println("Contact added.")
}

// Show all contacts or say if none found
fun viewContacts() {
    if (contacts.isEmpty()) {
        println("No contacts found.")
        return
    }

    println("===== Contact List =====")
    contacts.forEachIndexed { index, contact ->
        println("${index + 1}. ${contact.name} - ${contact.phone} - ${contact.email}")
    }
}

// Search for contacts by name (case-insensitive)
fun searchContact() {
    println("Enter name to search:")
    val name = readLine()?.trim().orEmpty()

    val results = contacts.filter { it.name.contains(name, ignoreCase = true) }

    if (results.isEmpty()) {
        println("No contacts found.")
    } else {
        println("Matching Contacts:")
        results.forEach {
            println("${it.name} - ${it.phone} - ${it.email}")
        }
    }
}

// Edit a contact by choosing its number
fun editContact() {
    viewContacts()
    if (contacts.isEmpty()) return

    println("Enter contact number to edit:")
    val index = readLine()?.toIntOrNull()?.minus(1)

    if (index == null || index !in contacts.indices) {
        println("Invalid number.")
        return
    }

    val oldContact = contacts[index]

    println("Editing ${oldContact.name}")
    println("Enter new name (or press enter to keep '${oldContact.name}'):")
    val newName = readLine()?.trim().orEmpty().ifBlank { oldContact.name }

    // Keep asking for new phone or keep old if blank
    var newPhone: String
    while (true) {
        println("Enter new phone (or press enter to keep '${oldContact.phone}'):")
        val input = readLine()?.trim().orEmpty()
        if (input.isBlank()) {
            newPhone = oldContact.phone
            break
        } else if (input.all { it.isDigit() }) {
            newPhone = input
            break
        } else {
            println("Invalid phone number.")
        }
    }

    println("Enter new email (or press enter to keep '${oldContact.email}'):")
    val newEmail = readLine()?.trim().orEmpty().ifBlank { oldContact.email }

    contacts[index] = Contact(newName, newPhone, newEmail)
    saveContacts()
    println("Contact updated.")
}

// Delete a contact by choosing its number
fun deleteContact() {
    viewContacts()
    if (contacts.isEmpty()) return

    println("Enter contact number to delete:")
    val index = readLine()?.toIntOrNull()?.minus(1)

    if (index == null || index !in contacts.indices) {
        println("Invalid number.")
        return
    }

    val removed = contacts.removeAt(index)
    saveContacts()
    println("Deleted ${removed.name}")
}
