package highfives.utils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;


// Class containing functions that make Natural Language API calls
public final class TextProcessingUtils {

    // Get words list from the given text
    public static List<String> textToWordsList(String text) {

        String s = text.toLowerCase();
        String[] words = s.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].replaceAll("[^\\w]", "");
        }
        return Arrays.asList(words);
    }

    // Get word frequency map from the given word list
    public static Map<String, Integer> wordListToFrequencyMap(List<String> words) {

        Map<String, Integer> map = new HashMap<>();
        for (String w : words) {
            Integer n = map.get(w);
            n = (n == null) ? 1 : ++n;
            map.put(w, n);
        }
        return map;
    }

    // Get word frequency map from the given text
    public static Map<String, Integer> textToFrequencyMap(String text) {

        List<String> words = textToWordsList(text);
        return wordListToFrequencyMap(words);
    }
}
