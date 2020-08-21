package highfives.data;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;


/** User's usage information */
@AllArgsConstructor
public final class UserUsage {
    @Getter private final String id;
    @Getter private final int units;
    @Getter private final Date lastCalled;
    @Getter private final Date lastReset;
}
