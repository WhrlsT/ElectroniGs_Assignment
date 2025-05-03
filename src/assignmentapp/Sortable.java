package assignmentapp;

import java.util.Comparator;

public interface Sortable<T> {
    // Method to get a comparator for a specific field
    Comparator<T> getComparator(String field);
    
    // Method to get available sort fields
    String[] getSortFields();
} 