package se.lexicon.data.impl;

import se.lexicon.data.MeetingDao;
import se.lexicon.model.Meeting;

import java.util.Collection;
import java.util.Optional;

//todo Implement methods

public class MeetingDaoImpl implements MeetingDao {
    @Override
    public Meeting createMeeting(Meeting meeting) {
        return null;
    }

    @Override
    public Optional<Meeting> findById(int id) {
        return Optional.empty();
    }

    @Override
    public Collection<Meeting> findAllMeetingsByCalendarId(int calendarId) {
        return null;
    }

    @Override
    public boolean deleteMeeting(int meetingId) {
        return false;
    }
}
