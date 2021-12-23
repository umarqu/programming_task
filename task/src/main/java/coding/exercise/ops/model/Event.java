package coding.exercise.ops.model;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Event {

    private String id;
    private Integer duration;
    private String type;
    private String host;
    private boolean alert;

    public boolean getAlert(){
        return alert;
    }

}