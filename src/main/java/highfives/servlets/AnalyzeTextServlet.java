package highfives.servlets;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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
import highfives.data.AnalysisResult;
import highfives.Constants;
import java.util.stream.Collectors;

@WebServlet(name = "AnalyzeTextServlet", value = "/analyze/text")
public class AnalyzeTextServlet extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException {

        // Get POST request body as JSON
        JSONObject requestBody = new JSONObject(request.getReader().lines().collect(Collectors.joining()));
        String text = (String) requestBody.get(Constants.ANALYZE_TEXT);

        // If length of the text exceeds the limit, send bad request (400)
        if(text.length() > Constants.TEXT_MAX_LENGTH) {
            String errorMessage = ((new StringBuilder())
                        .append("Length of text to be processed should not exceed ")
                        .append(Constants.TEXT_MAX_LENGTH)
                        .append(" characters.")).toString();
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(errorMessage);
            return;
        }

        // If length of the text is 0, send bad request (400)
        if(text.length() == 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println("Text cannot be empty.");
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

}
