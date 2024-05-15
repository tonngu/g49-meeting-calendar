package se.lexicon.dao.impl;

import se.lexicon.dao.MeetingDao;
import se.lexicon.exception.MySQLException;
import se.lexicon.model.Calendar;
import se.lexicon.model.Meeting;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

//todo Implement methods

public class MeetingDaoImpl implements MeetingDao {

    private Connection connection;

    public MeetingDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Meeting createMeeting(Meeting meeting) {

        String insertQuery = "INSERT INTO meetings (title, start_time, end_time, _description, calendar_id) VALUES (?, ?, ?, ?, ?)";
        try (
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)

        ) {
            preparedStatement.setString(1, meeting.getTitle());
            preparedStatement.setTimestamp(2, Timestamp.valueOf(meeting.getStartTime()));
            preparedStatement.setTimestamp(3, Timestamp.valueOf(meeting.getEndTime()));
            preparedStatement.setString(4, meeting.getDescription());
            preparedStatement.setInt(5, meeting.getCalendar().getId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                String errorMessage = "Creating meeting failed, no rows affected.";
                throw new MySQLException(errorMessage);
            }
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int meetingId = generatedKeys.getInt(1);
                    new Meeting(meetingId, meeting.getTitle(), meeting.getStartTime(), meeting.getEndTime(), meeting.getDescription(), meeting.getCalendar());
                    return meeting;
                } else {
                    String errorMessage = "Creating meeting failed, no ID obtained.";
                    throw new MySQLException(errorMessage);
                }
            }
        } catch (SQLException e) {
            String errorMessage = "Error occurred while creating a meeting";
            throw new MySQLException(errorMessage, e);
        }
    }

    @Override
    public Optional<Meeting> findById(int id) {
        String selectQuery = "SELECT m.*, mc.username as username, mc.title as calendarTitle FROM meetings m inner join meeting_calendars mc on m.calendar_id = mc.id WHERE m.id = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {

                int meetingId = resultSet.getInt("id");
                String title = resultSet.getString("title");
                Timestamp startTime = resultSet.getTimestamp("start_time");
                Timestamp endTime = resultSet.getTimestamp("end_time");
                String description = resultSet.getString("_description");
                int calendarId = resultSet.getInt("calendar_id");
                String calendarUsername = resultSet.getString("username");
                String calendarTitle = resultSet.getString("calendarTitle");

                LocalDateTime startDateTime = startTime.toLocalDateTime();
                LocalDateTime endDateTime = endTime.toLocalDateTime();

                return Optional.of(new Meeting(meetingId, title, startDateTime, endDateTime, description, new Calendar(calendarId, calendarUsername, calendarTitle)));

            }
        } catch (SQLException e) {
            String errorMessage = "Error occurred while finding a meeting by ID " + id;
            throw new MySQLException(errorMessage, e);
        }
        return Optional.empty();
    }

    @Override
    public Collection<Meeting> findAllMeetingsByCalendarId(int calendarId) {
        List<Meeting> meetings = new ArrayList<>();
        String selectQuery = "SELECT * FROM meetings WHERE calendar_id = ?";

        try (

                PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);

        ) {
            preparedStatement.setInt(1, calendarId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {

                    int meetingId = resultSet.getInt("id");
                    String title = resultSet.getString("title");
                    Timestamp startTime = resultSet.getTimestamp("start_time");
                    Timestamp endTime = resultSet.getTimestamp("end_time");
                    String description = resultSet.getString("_description");

                    LocalDateTime startDateTime = startTime.toLocalDateTime();
                    LocalDateTime endDateTime = endTime.toLocalDateTime();
                    //Add proper constructor to Meeting class
                    Meeting meeting = new Meeting(meetingId, title, startDateTime, endDateTime, description);

                    meetings.add(meeting);
                }
            }


        } catch (SQLException e) {
            String errorMessage = "Error occurred while retrieving all meetings";
            throw new MySQLException(errorMessage, e);
        }
        return meetings;
    }

    @Override
    public boolean deleteMeeting(int meetingId) {
        String deleteQuery = "DELETE FROM meetings WHERE id = ?";

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)
        ) {
            preparedStatement.setInt(1, meetingId);

            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            String errorMessage = "Error occurred while deleting meeting with ID: " + meetingId;
            throw new MySQLException(errorMessage, e);
        }
    }
}
