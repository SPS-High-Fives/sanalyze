package highfives.data;

import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.Getter;

import highfives.Constants;


/** Analysis result for a certain text */
@AllArgsConstructor
public final class AnalysisResult {
    private final long id;
    @Getter private final String text;
    @Getter private final String result;

    public AnalysisResult(long id, String text, float sentiment, JSONObject entitySentiments, JSONObject entitySaliences, JSONObject wordFrequencies) {
        this.id = id;
        this.text = text;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(Constants.SENTIMENT, sentiment);
        jsonObject.put(Constants.ENTITY_SENTIMENTS, entitySentiments);
        jsonObject.put(Constants.ENTITY_SALIENCES, entitySaliences);
        jsonObject.put(Constants.WORD_FREQUENCIES, wordFrequencies);
        this.result = jsonObject.toString();
    }
}
