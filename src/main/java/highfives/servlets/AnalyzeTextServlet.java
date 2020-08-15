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
import highfives.Constants;


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
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, errorMessage);
            return;
        }

        JSONObject jsonResponse = new JSONObject();

        // Analyze text using highfives.utils.NaturalLanguageAPIUtils
        // and populate jsonResponse
        try {

            Sentiment sentiment = NaturalLanguageAPIUtils.analyzeSentimentText(text);
            List<Entity> entities = NaturalLanguageAPIUtils.entitySentimentText(text);

            // Sentiment score of the entire text
            jsonResponse.put("score", sentiment.getScore());

            // Get entities and their sentiment scores
            JSONObject entitiesJson = new JSONObject();
            for (Entity entity: entities) {
                entitiesJson.put(entity.getName(), entity.getSentiment().getScore());
            }
            jsonResponse.put("entities", entitiesJson);

            // Get word frequency
            Map<String, Integer> wordFrequencies = TextProcessingUtils.textToFrequencyMap(text);
            JSONObject wordFrequenciesJson = new JSONObject(wordFrequencies);
            jsonResponse.put("wordcount", wordFrequencies);

        } catch (Exception e) {
            //Some issue while processing, send internal server error (500)
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
            return;
        }

        System.out.println(jsonResponse.toString());

        //Return JSON response
        response.setContentType("application/json");
        response.getWriter().println(jsonResponse.toString());
    }

}
