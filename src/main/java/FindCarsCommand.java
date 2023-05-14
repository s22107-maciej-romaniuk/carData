import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FindCarsCommand extends CommandAdapter implements Serializable {

    public FindCarsCommand() {}

    public void execute() {
        clearResult();
        String input = (String) getParameter("input");
        if (input == null) {
            setStatusCode(1);
            return;
        }
        JSONArray jsonArray;
        try {
            Path path = Paths.get(getClass().getClassLoader()
                    .getResource("package.txt").toURI());

            Stream<String> lines = Files.lines(path);
            String data = lines.collect(Collectors.joining());
            System.out.println("Odczytane dane" + data);
            lines.close();
            System.out.println("Zamykanie strumienia");
            try {
                jsonArray = new JSONArray(data);
            }
            catch (Exception exc){
                System.out.println("Message: " + exc.getMessage());
                for(StackTraceElement s : exc.getStackTrace()){
                    System.out.println(s);
                }
                setStatusCode(2);
                return;
            }
            System.out.println("Utworzono obiekt jsonArray");
        } catch (IOException | URISyntaxException exc) {
            System.out.println("Message: " + exc.getMessage());
            for(StackTraceElement s : exc.getStackTrace()){
                System.out.println(s);
            }
            setStatusCode(2);
            return;
        }
        System.out.println("Poszukowanie dopasowań...");
        List<JSONObject> matchedEntries = new LinkedList<>();
        for(Object entry : jsonArray){
            System.out.println("iteracja jsonArray");
            JSONObject jsonEntry = (JSONObject) entry;
            if(jsonEntry.getString("rodzaj").equals(input)){
                System.out.println("znaleziono dopasowanie");
                matchedEntries.add(jsonEntry);
            }
        }
        if (matchedEntries.size() == 0) setStatusCode(3);
        else {
            setStatusCode(0);
            String[] keys = BundleInfo.getResultKeys();
            for(JSONObject jsonObject : matchedEntries){

                System.out.println("iteracja matchedEntries");
                System.out.println(jsonObject.toString(2));

                int columnCount = jsonObject.keySet().size();
                Object[] obj = new Object[columnCount];
                int iterator = 0;

                System.out.println(jsonObject.keySet());

                for(String key : keys){

                    System.out.println("iteracja keySet");

                    obj[iterator] = jsonObject.get(key);
                    System.out.println(obj[iterator]);
                    iterator++;
                }
                addResult(obj);

            }
        }
    }

}
