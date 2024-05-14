package se.lexicon.data.impl;

import se.lexicon.data.CalendarDao;
import se.lexicon.model.Calendar;

import java.util.Collection;
import java.util.Optional;

public class CalendarDaoImpl implements CalendarDao {

    //todo Implement methods
    @Override
    public Calendar createCalendar(String title, String username) {
        return null;
    }

    @Override
    public Optional<Calendar> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<Calendar> findCalendarsByUsername(String username) {
        return null;
    }

    @Override
    public Optional<Calendar> findByTitle(String title) {
        return Optional.empty();
    }

    @Override
    public boolean deleteCalendar(int id) {
        return false;
    }
}
