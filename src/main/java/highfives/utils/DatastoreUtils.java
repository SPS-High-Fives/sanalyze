package highfives.utils;

import java.util.List;
import org.json.JSONObject;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;

import highfives.data.AnalysisResult;
import highfives.Constants;


// Class to perform operations on datastore
public final class DatastoreUtils {

    // Get analysis of a text from datastore
    // return null if not found
    public static AnalysisResult getAnalysis(String text)
    {    
        try {
    
            // Find analysis with the given text
            Query query = new Query(Constants._ANALYSIS);
            query.addFilter(Constants.ANALYZE_TEXT, Query.FilterOperator.EQUAL, text);

            //Get analysis from datastore
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            PreparedQuery results = datastore.prepare(query);

            AnalysisResult analysis = null;
            for (Entity entity : results.asIterable()) {
                long id = entity.getKey().getId();
                analysis = new AnalysisResult(id, text, (String) entity.getProperty(Constants.RESULT));
            }
    
            return analysis;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }        
    }

    // Save analysis for the given text
    public static AnalysisResult saveAnalysis(String text, float sentiment, JSONObject entitySentiments, JSONObject entitySaliences, JSONObject wordFrequencies)
    {
        // Construct Analysis object from data
        AnalysisResult analysis = new AnalysisResult(0, text, sentiment, entitySentiments, entitySaliences, wordFrequencies);

        // Upload data to datastore
        Entity analysisEntity = new Entity(Constants._ANALYSIS);
        analysisEntity.setProperty(Constants.ANALYZE_TEXT, text);
        analysisEntity.setProperty(Constants.RESULT, analysis.getResult());

        try {
            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(analysisEntity);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return analysis;
    }
}
