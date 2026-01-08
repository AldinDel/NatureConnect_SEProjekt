package at.fhv.Event.application.event;

import at.fhv.Event.presentation.rest.response.event.EventOverviewDTO;

import java.util.List;

public interface GetEventsForTodayService {
    List<EventOverviewDTO> getEventsForToday();
}
