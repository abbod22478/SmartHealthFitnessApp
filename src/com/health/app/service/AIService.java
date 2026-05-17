package com.health.app.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * AIService — Uses Groq API (free, 14,400 req/day) for:
 *   1. Smart meal text parsing with full nutrition data (FR18-FR21)
 *   2. Smart food recommendations (FR29-FR30)
 *
 * Model: llama-3.1-8b-instant (fast + free)
 * Docs: https://console.groq.com/docs
 */
public class AIService {

    private static final String API_KEY =
            "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";


    private static final String API_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private static final String MODEL = "llama-3.1-8b-instant";

    // =========================================================================
    //  INNER CLASS — Full result including nutrition per 100g
    // =========================================================================

    public static class ParsedMeal {
        private final String foodKeyword;
        private final double grams;
        private final double caloriesPer100g;
        private final double proteinPer100g;
        private final double carbsPer100g;
        private final double fatsPer100g;
        private final boolean success;
        private final String errorMessage;

        public ParsedMeal(String foodKeyword, double grams,
                          double caloriesPer100g, double proteinPer100g,
                          double carbsPer100g, double fatsPer100g) {
            this.foodKeyword     = foodKeyword;
            this.grams           = grams;
            this.caloriesPer100g = caloriesPer100g;
            this.proteinPer100g  = proteinPer100g;
            this.carbsPer100g    = carbsPer100g;
            this.fatsPer100g     = fatsPer100g;
            this.success         = true;
            this.errorMessage    = null;
        }

        public ParsedMeal(String errorMessage) {
            this.foodKeyword     = null;
            this.grams           = 0;
            this.caloriesPer100g = 0;
            this.proteinPer100g  = 0;
            this.carbsPer100g    = 0;
            this.fatsPer100g     = 0;
            this.success         = false;
            this.errorMessage    = errorMessage;
        }

        public String getFoodKeyword()    { return foodKeyword;     }
        public double getGrams()          { return grams;           }
        public double getCaloriesPer100g(){ return caloriesPer100g; }
        public double getProteinPer100g() { return proteinPer100g;  }
        public double getCarbsPer100g()   { return carbsPer100g;    }
        public double getFatsPer100g()    { return fatsPer100g;     }
        public boolean isSuccess()        { return success;         }
        public String getErrorMessage()   { return errorMessage;    }
    }

    // =========================================================================
    //  1. SMART MEAL TEXT PARSING WITH NUTRITION (FR18-FR21)
    // =========================================================================

    public static ParsedMeal parseMealText(String userInput) {
        String systemPrompt =
                "You are a nutrition database assistant. " +
                        "Extract food name, quantity, and nutrition per 100g. " +
                        "Respond with EXACTLY 6 lines, no extra text:\n" +
                        "FOOD: <simple food name, e.g. Apple, Chicken breast>\n" +
                        "GRAMS: <quantity number only, use 100 if not mentioned>\n" +
                        "CALORIES: <kcal per 100g, number only>\n" +
                        "PROTEIN: <grams protein per 100g, number only>\n" +
                        "CARBS: <grams carbs per 100g, number only>\n" +
                        "FATS: <grams fat per 100g, number only>";

        String responseText = callGroq(systemPrompt, userInput);

        if (responseText == null) {
            return fallbackRegexParse(userInput);
        }

        return parseFullResponse(responseText, userInput);
    }

    private static ParsedMeal parseFullResponse(String response, String originalInput) {
        String foodKeyword     = null;
        double grams           = 100;
        double caloriesPer100g = 0;
        double proteinPer100g  = 0;
        double carbsPer100g    = 0;
        double fatsPer100g     = 0;

        for (String line : response.split("\n")) {
            line = line.trim();
            try {
                if (line.toUpperCase().startsWith("FOOD:")) {
                    foodKeyword = line.substring(5).trim();
                } else if (line.toUpperCase().startsWith("GRAMS:")) {
                    grams = Double.parseDouble(line.substring(6).trim());
                } else if (line.toUpperCase().startsWith("CALORIES:")) {
                    caloriesPer100g = Double.parseDouble(line.substring(9).trim());
                } else if (line.toUpperCase().startsWith("PROTEIN:")) {
                    proteinPer100g = Double.parseDouble(line.substring(8).trim());
                } else if (line.toUpperCase().startsWith("CARBS:")) {
                    carbsPer100g = Double.parseDouble(line.substring(6).trim());
                } else if (line.toUpperCase().startsWith("FATS:")) {
                    fatsPer100g = Double.parseDouble(line.substring(5).trim());
                }
            } catch (NumberFormatException e) {
                // keep default 0
            }
        }

        if (foodKeyword == null || foodKeyword.isEmpty()) {
            return fallbackRegexParse(originalInput);
        }

        return new ParsedMeal(foodKeyword, grams,
                caloriesPer100g, proteinPer100g, carbsPer100g, fatsPer100g);
    }

    private static ParsedMeal fallbackRegexParse(String input) {
        double grams = 100;
        String foodKeyword = input;

        java.util.regex.Pattern pattern =
                java.util.regex.Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*g");
        java.util.regex.Matcher matcher = pattern.matcher(input.toLowerCase());

        if (matcher.find()) {
            grams = Double.parseDouble(matcher.group(1));
            foodKeyword = input.replaceAll("(?i)(\\d+(?:\\.\\d+)?)\\s*g", "").trim();
        }

        if (foodKeyword.isEmpty()) {
            return new ParsedMeal("Could not understand input. Try: 'apple 100g'");
        }

        return new ParsedMeal(foodKeyword, grams, 0, 0, 0, 0);
    }

    // =========================================================================
    //  2. SMART FOOD RECOMMENDATIONS (FR29-FR30)
    // =========================================================================

    public static String[] getSmartRecommendations(
            double remainingCalories,
            String fitnessGoal,
            double remainingProtein,
            int variety) {

        String systemPrompt =
                "You are a fitness nutrition coach. " +
                        "Suggest exactly 4 foods I should eat now. Variation set " + variety + ": pick different foods each time.\n" +
                        "Format EXACTLY as 4 lines, each like:\n" +
                        "• <Food name> — ~<calories> kcal / 100g | Protein: <protein>g\n" +
                        "No extra text. Just 4 lines.";

        String userMessage =
                "My goal: " + (fitnessGoal == null ? "General fitness" : fitnessGoal) + "\n" +
                        "Remaining calories: " + (int) remainingCalories + " kcal\n" +
                        "Remaining protein: " + (int) remainingProtein + " g\n" +
                        "Suggest 4 foods.";

        String responseText = callGroq(systemPrompt, userMessage);

        if (responseText == null || responseText.trim().isEmpty()) {
            return new String[0];
        }

        String[] lines = responseText.trim().split("\n");
        String[] result = new String[Math.min(lines.length, 4)];
        for (int i = 0; i < result.length; i++) {
            result[i] = lines[i].trim().replaceAll("^[•\\-\\*]\\s*", "");
        }
        return result;
    }

    // =========================================================================
    //  CORE — HTTP call to Groq API (OpenAI-compatible format)
    // =========================================================================

    /**
     * Groq uses OpenAI-compatible API format:
     * POST https://api.groq.com/openai/v1/chat/completions
     * Authorization: Bearer <key>
     * {
     *   "model": "llama-3.1-8b-instant",
     *   "messages": [
     *     {"role": "system", "content": "..."},
     *     {"role": "user",   "content": "..."}
     *   ]
     * }
     *
     * Response: { "choices": [{ "message": { "content": "..." } }] }
     */
    private static String callGroq(String systemPrompt, String userMessage) {
        try {
            String requestBody = "{"
                    + "\"model\": \"" + MODEL + "\","
                    + "\"max_tokens\": 256,"
                    + "\"temperature\": 0.1,"
                    + "\"messages\": ["
                    +   "{\"role\": \"system\", \"content\": " + jsonString(systemPrompt) + "},"
                    +   "{\"role\": \"user\",   \"content\": " + jsonString(userMessage)  + "}"
                    + "]"
                    + "}";

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + API_KEY)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.err.println("[AIService] Groq error " + response.statusCode()
                        + ": " + response.body());
                return null;
            }

            return extractTextFromGroqResponse(response.body());

        } catch (Exception e) {
            System.err.println("[AIService] Groq request failed: " + e.getMessage());
            return null;
        }
    }

    // =========================================================================
    //  HELPERS
    // =========================================================================

    private static String jsonString(String value) {
        if (value == null) return "\"\"";
        return "\""
                + value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t")
                + "\"";
    }

    /**
     * Extracts content from Groq's OpenAI-format response:
     * { "choices": [{ "message": { "content": "..." } }] }
     */
    private static String extractTextFromGroqResponse(String json) {
        // Find "content": "..."
        String marker = "\"content\":";
        int idx = json.indexOf(marker);
        if (idx < 0) return null;

        int start = json.indexOf('"', idx + marker.length());
        if (start < 0) return null;
        start++;

        StringBuilder sb = new StringBuilder();
        int i = start;
        while (i < json.length()) {
            char c = json.charAt(i);
            if (c == '\\' && i + 1 < json.length()) {
                char next = json.charAt(i + 1);
                switch (next) {
                    case '"':  sb.append('"');  i += 2; continue;
                    case '\\': sb.append('\\'); i += 2; continue;
                    case 'n':  sb.append('\n'); i += 2; continue;
                    case 'r':  sb.append('\r'); i += 2; continue;
                    case 't':  sb.append('\t'); i += 2; continue;
                    default:   sb.append(next); i += 2; continue;
                }
            }
            if (c == '"') break;
            sb.append(c);
            i++;
        }

        return sb.toString();
    }
}