package at.fhv.Event.application.event;

import at.fhv.Event.presentation.rest.response.booking.EventParticipantsStats;
import at.fhv.Event.presentation.rest.response.booking.EventCheckoutStats;
import at.fhv.Event.presentation.rest.response.booking.ParticipantDTO;

import java.util.List;

public interface GetParticipantsForEventService {

    List<ParticipantDTO> getParticipants(Long eventId);

    EventParticipantsStats getStatsForEvent(Long eventId);

    EventCheckoutStats getCheckoutStats(Long eventId);
}
