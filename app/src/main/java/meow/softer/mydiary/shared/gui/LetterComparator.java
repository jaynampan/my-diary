package meow.softer.mydiary.shared.gui;

import meow.softer.mydiary.contacts.ContactsEntity;

import java.util.Comparator;

public class LetterComparator implements Comparator<ContactsEntity> {

    @Override
    public int compare(ContactsEntity lhs, ContactsEntity rhs) {
        return lhs.getSortLetters().compareTo(rhs.getSortLetters());

    }
    //comparator 用于比较两个对象
}
