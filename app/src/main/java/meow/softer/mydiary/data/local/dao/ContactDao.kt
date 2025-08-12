package meow.softer.mydiary.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import meow.softer.mydiary.data.entity.ContactEntry

@Dao
interface ContactDao {
    @Query("SELECT * FROM contact_entry")
    fun getAll(): List<ContactEntry>

    @Query("SELECT * FROM contact_entry WHERE ref_topic_id = :topicId")
    fun getAllByTopicId(topicId: Int): List<ContactEntry>

    @Query("SELECT * FROM contact_entry WHERE id = :id")
    fun getById(id: Int): ContactEntry

    @Query("SELECT * FROM contact_entry WHERE phone_number LIKE :phoneNumber")
    fun getByPhoneNumber(phoneNumber: String): List<ContactEntry>

    @Query("SELECT * FROM contact_entry WHERE name LIKE :name")
    fun getByName(name: String): List<ContactEntry>

    @Query("DELETE FROM contact_entry WHERE id = :id")
    fun deleteById(id: Int)

    @Query("DELETE FROM contact_entry WHERE ref_topic_id = :topicId")
    fun deleteByTopicId(topicId: Int)

    @Query("DELETE FROM contact_entry")
    fun deleteAll()

    @Query("SELECT COUNT(*) FROM contact_entry")
    fun getCount(): Int

    @Query("SELECT COUNT(*) FROM contact_entry WHERE ref_topic_id = :topicId")
    fun getCountByTopicId(topicId: Int): Int

    @Insert
    fun insert(contactEntry: ContactEntry)

    @Insert
    fun insertAll(vararg contactEntry: ContactEntry)

    @Delete
    fun delete(contactEntry: ContactEntry)

    @Delete
    fun deleteAll(vararg contactEntry: ContactEntry)

    @Update
    fun update(contactEntry: ContactEntry)
}