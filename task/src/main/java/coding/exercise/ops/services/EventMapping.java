package coding.exercise.ops.services;

import coding.exercise.ops.LoggingApplication;
import coding.exercise.ops.constants.AppConstants;
import coding.exercise.ops.model.Event;
import coding.exercise.ops.model.IncomingJson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class EventMapping {
    private static final Logger logger = LogManager.getLogger(EventMapping.class);

    private Map<String, Event> map;

    public EventMapping(){
        map = new HashMap<>();
    }

    /* writeToMap takes in json row and adds it to map, id is key, value is event */
    public void writeToMap(IncomingJson incomingJson) {

        //if the id doesn't exist in the map, then add it
        if (map.containsKey(incomingJson.getId()) == false) {
            // adding the event as value if the event doesn't exist
            Event event = incomingJsonToEventMapping(incomingJson);
            map.put(incomingJson.getId(), event);
        } else {
            // if the event is present, then it will calculate duration and update the map for that event
            Event event;
            if (incomingJson.getState() == AppConstants.STATE_STARTED) {
                event = map.get(incomingJson.getId());
                addEventToMap(event,incomingJson.getId(),incomingJson.getTimeStamp(),incomingJson.getTimeStamp());
            } else {
                event = map.get(incomingJson.getId());
                addEventToMap(event,incomingJson.getId(),event.getDuration(),incomingJson.getTimeStamp());
            }
        }
    }

    // create Event object and returns it
    public Event incomingJsonToEventMapping(IncomingJson incomingJson){
        return Event.builder()
                .id(incomingJson.getId())
                .duration(incomingJson.getTimeStamp())
                .type(incomingJson.getType())
                .host(incomingJson.getHost())
                .alert(checkAlert(incomingJson.getTimeStamp())).build();
    }

    // adds Event object to map and calculates new duration
    private void addEventToMap(Event event,String eventId,int startTimeStamp, int endTimeStamp){
        int duration = endTimeStamp-startTimeStamp;
        duration = (duration < 0 ? -duration : duration); // if difference happens to be negative

        event.setDuration(duration);
        event.setAlert(checkAlert(duration));

        // flag event that is particularly long
        checkEventAndflag(event,duration);
        map.put(eventId, event);
    }
    // returns boolean for flag field
    private boolean checkAlert(int duration){
        return duration >= 4;
    }

    // returns the Events map
    public Map<String, Event> getMap(){
        return map;
    }

    // returns the Events map
    public void checkEventAndflag(Event event, int duration){
        if (checkAlert(duration)){
            logger.info("Event Flagged. Duration={} for event-{}",duration,event);
        }
    }

}
