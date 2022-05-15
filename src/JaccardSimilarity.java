import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;



/*
Read file
put string into set (each file should contain the set of words)
read query
put query into set
check each word in query if found in the file set
combine both sets
calculate jaccard
 */


public class JaccardSimilarity {
    Map<String,Set<String>> doc = new HashMap<>(); //List of words in each document, defined by document name

    public void buildIndex(String[] files) {
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                String ln;
                Set<String> wordsSet = new HashSet<>();
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        wordsSet.add(word);
                    }

                }

                String docName = fileName.substring(fileName.length() - 7); //fixing document name
                docName = docName.substring(0,3);
                doc.put(docName,wordsSet);





            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }

        }

    }


    public void jaccardSimilarity(String q){
       String[]query = q.split(" ");
        Set<String> querySet = new HashSet<>(Arrays.asList(query));
        Map<Double,String> result = new TreeMap<>();

        for (var file: doc.entrySet()){
            int intersection = 0;
            //intersection
            for (var word: querySet){
                word = word.toLowerCase();
                if(file.getValue().contains(word)){
                    intersection++;
                }
            }
            //union
            int union = file.getValue().size() + querySet.size() - intersection;
//            System.out.println(file.getKey() + ": Intersection: " + intersection + "Union: " + union);

            double jaccard = 1.0 * intersection / union;
            result.put(-jaccard,file.getKey()); //for descending order
        }
        for (var r : result.entrySet()){
            System.out.println(r.getValue()+": " +r.getKey()*-1);
        }




    }

}
