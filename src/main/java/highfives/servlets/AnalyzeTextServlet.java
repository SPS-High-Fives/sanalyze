package highfives.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.cloud.language.v1.Entity;
import com.google.cloud.language.v1.Sentiment;

import highfives.utils.NaturalLanguageAPIUtils;
import highfives.utils.TextProcessingUtils;
import highfives.utils.DatastoreUtils;
import highfives.utils.UsageUtils;
import highfives.data.AnalysisResult;
import highfives.data.UserUsage;
import highfives.Constants;


@WebServlet(name = "AnalyzeTextServlet", value = "/analyze/text")
public class AnalyzeTextServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        // Get POST request body as JSON
        JSONObject requestBody = new JSONObject(request.getReader().lines().collect(Collectors.joining()));
        String text = (String) requestBody.get(Constants.ANALYZE_TEXT);

        // If invalid input text, return
        if(!validateInputText(text, response)) {
            return;
        }

        String ipAddress = getIPAddress(request);

        // If ipAddress couldn't be retrieved, send bad request (400)
        if(ipAddress == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        int requiredUnits = (int) Math.ceil(text.length()*1.0f / Constants.UNIT_SIZE);

        // If user has doesn't has enough units, return
        if(!checkUsage(ipAddress, requiredUnits)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Please try again after some time.");
            return;
        }

        // See if datastore has analysis for the text
        // If yes, return it
        AnalysisResult analysis = DatastoreUtils.getAnalysis(text);
        
        if (analysis != null) {
            response.setContentType("application/json");
            response.getWriter().println(analysis.getResult().toString());
            return;
        }

        // Analyze text using highfives.utils.NaturalLanguageAPIUtils
        // and populate jsonResponse
        try {

            Sentiment sentiment = NaturalLanguageAPIUtils.analyzeSentimentText(text);
            List<Entity> entities = NaturalLanguageAPIUtils.entitySentimentText(text);

            // Get entities and their sentiment scores
            JSONObject entitiesJson = new JSONObject();
            for (Entity entity: entities) {
                entitiesJson.put(entity.getName(), entity.getSentiment().getScore());
            }

            // Get word frequency
            Map<String, Integer> wordFrequencies = TextProcessingUtils.textToFrequencyMap(text);
            JSONObject wordFrequenciesJson = new JSONObject(wordFrequencies);

            analysis = DatastoreUtils.saveAnalysis(text, sentiment.getScore(), entitiesJson, wordFrequenciesJson);

            UsageUtils.updateUsageUnits(ipAddress, requiredUnits);

        } catch (Exception e) {
            //Some issue while processing, send internal server error (500)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().println("Processing error. Please try again.");
            e.printStackTrace();
            return;
        }

        //Return JSON response
        response.setContentType("application/json");
        response.getWriter().println(analysis.getResult());
    }

    private boolean validateInputText(String text, HttpServletResponse response) {

        try {
            // If length of the text exceeds the limit, send bad request (400)
            if(text.length() > Constants.TEXT_MAX_LENGTH) {
                String errorMessage = ((new StringBuilder())
                            .append("Length of text to be processed should not exceed ")
                            .append(Constants.TEXT_MAX_LENGTH)
                            .append(" characters.")).toString();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println(errorMessage);
                return false;
            }

            // If length of the text is 0, send bad request (400)
            if(text.length() == 0) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().println("Text cannot be empty.");
                return false;
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        return true;
    }

    // Get client's IP address
    private String getIPAddress(HttpServletRequest request) {
    
        String ipAddress = request.getHeader("X-FORWARDED-FOR");  
        if (ipAddress == null) {  
            ipAddress = request.getRemoteAddr();  
        }
        return ipAddress;
    }

    // Checks whether user has the required number of units,
    // creates users whenever required.
    private boolean checkUsage(String ipAddress, int requiredUnits) {

        try {
            // If user exists, check whether user has enough units, and isn't making too many calls
            if(UsageUtils.checkClientExists(ipAddress)) {

                UserUsage usage = UsageUtils.getUserUsage(ipAddress);
                long duration  = (new Date()).getTime() - usage.getLastCalled().getTime();
                long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(duration);

                // Temporarily solution for resetting usage (after 30 days of previous reset)
                long durationSinceReset  = (new Date()).getTime() - usage.getLastReset().getTime();
                if(TimeUnit.MILLISECONDS.toDays(durationSinceReset) >= Constants.RESET_DAYS)
                {
                    UsageUtils.resetUsageUnits(ipAddress);
                    return true;
                }

                return (usage.getUnits() >= requiredUnits) && (diffInSeconds >= Constants.CALLS_GAP);
            }

            // else, create an entry for the user
            UsageUtils.createUsageEntry(ipAddress);
            return true;
    
        } catch (Exception e) {
            e.printStackTrace
            return false;
        }
    }
}
