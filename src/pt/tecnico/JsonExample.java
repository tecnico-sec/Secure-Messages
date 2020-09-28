package pt.tecnico;

import com.google.gson.*;

/**
 * Example of how to use GSON's library to parse and navigate a JSON document.
 * The example also shows how to use the parser to build new JSON documents.
 * 
 * credits: http://tutorials.jenkov.com/java-json/gson-jsonparser.html
 * reference: https://github.com/google/gson
 */
public class JsonExample {

    public static void main(String[] args) throws Exception {

        // JSON can be created directly as a String
        final String jsonString = "{ \"f1\":\"Hello\", \"f2\":{\"f3\":\"World\"}}";
        System.out.println("JSON string: " + jsonString);

        // The parser checks the syntax and loads the document in memory
        JsonElement jsonTree = JsonParser.parseString​(jsonString).getAsJsonObject();
        System.out.println("Parsed JSON: " + jsonTree);

        // The document can be navigated
        if (jsonTree.isJsonObject()) {
            // you need a JsonObject to be able to get sub-elements
            JsonObject jsonObject = jsonTree.getAsJsonObject();

            JsonElement f1 = jsonObject.get("f1");
            System.out.println("f1 value is " + f1.getAsString());

            JsonElement f2 = jsonObject.get("f2");
            System.out.println("f2 value is " + f2);
            if (f2.isJsonObject()) {
                JsonObject f2Obj = f2.getAsJsonObject();

                JsonElement f3 = f2Obj.get("f3");
                System.out.println("f3 value is " + f3.getAsString());
            }
        }

        // The parser can also be used to create JSON.
        // The code is more readable and the library also adds the special characters
        // and escapes
        System.out.println("Building a new JSON tree...");
        // create empty tree
        JsonObject jsonRoot = JsonParser.parseString​("{}").getAsJsonObject();
        // add child property
        jsonRoot.addProperty("f1", "Hello again");
        // create another node and add to root
        JsonObject jsonF2 = JsonParser.parseString​("{}").getAsJsonObject();
        String f3Value = "Text with comma, curly brackets { }, square brackets [ ], tab \t, \"quotes\" and a " + System.lineSeparator() + "newline included. No problem!";
        jsonF2.addProperty("f3", f3Value);
        // add node to root
        jsonRoot.add("f2", jsonF2);
        // print the constructed JSON
        System.out.println(jsonRoot);

        System.out.println("f3 value is " + jsonF2.get("f3").getAsString());
    }
}
