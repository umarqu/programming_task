package coding.test.ops.model;

import lombok.*;

@Data
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class IncomingJson {

    private String host;
    private String id;
    private String state;
    private String type;
    private int timeStamp;

}
