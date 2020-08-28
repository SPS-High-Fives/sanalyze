package highfives;


//Class to store constants
public final class Constants {

    public static final String DATABASE_NAME = System.getenv("DATABASE_NAME");

    public static final String INSTANCE_CONNECTION_NAME = System.getenv("INSTANCE_CONNECTION_NAME");

    public static final String MYSQL_USER_NAME = System.getenv("MYSQL_USER_NAME");

    public static final String MYSQL_USER_PASSWORD = System.getenv("MYSQL_USER_PASSWORD");

    public static final String JDBC_URL = ((new StringBuilder())
                        .append("jdbc:mysql:///")
                        .append(DATABASE_NAME)
                        .append("?cloudSqlInstance=")
                        .append(INSTANCE_CONNECTION_NAME)
                        .append("&socketFactory=com.google.cloud.sql.mysql.SocketFactory&user=")
                        .append(MYSQL_USER_NAME)
                        .append("&password=")
                        .append(MYSQL_USER_PASSWORD)).toString();

    public static final String ANALYZE_TEXT = "text";

    public static final Integer TEXT_MAX_LENGTH = 10000;

    public static final String _ANALYSIS = "Analysis";

    public static final String SENTIMENT = "score";

    public static final String ENTITY_SENTIMENTS = "entities";

    public static final String ENTITY_SALIENCES = "saliences";

    public static final String WORD_FREQUENCIES = "wordcount";

    public static final String RESULT = "result";

    public static final int MONTHLY_UNITS = 200;

    public static final int UNIT_SIZE = 1000;

    public static final int CALLS_GAP = 10;

    public static final int RESET_DAYS = 30;
}
