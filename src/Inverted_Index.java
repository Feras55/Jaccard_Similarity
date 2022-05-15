/*
 * InvertedIndex - Given a set of text files, implement a program to create an
 * inverted index. Also create a user interface to do a search using that inverted
 * index which returns a list of files that contain the query term / terms.
 * The search index can be in memory.
 *

 */
import java.io.*;
import java.util.*;

//=====================================================================
class DictEntry3 {

    public int doc_freq = 0; // number of documents that contain the term
    public int term_freq = 0; //number of times the term is mentioned in the collection
    public HashSet<Integer> postingList;

    DictEntry3() {
        postingList = new HashSet<Integer>();
    }
}

//=====================================================================
class Index3 {

    //--------------------------------------------
    Map<Integer, String> sources;  // store the doc_id and the file name
    HashMap<String, DictEntry3> index; // THe inverted index
    //--------------------------------------------

    Index3() {
        sources = new HashMap<Integer, String>();
        index = new HashMap<String, DictEntry3>();
    }

    //---------------------------------------------
    public void printPostingList(HashSet<Integer> hset) {
        Iterator<Integer> it2 = hset.iterator();
        while (it2.hasNext()) {
            System.out.print(it2.next() + ", ");
        }
        System.out.println("");
    }

    public void printDictionary() {
        Iterator it = index.entrySet().iterator();
//        while (it.hasNext()) {
//            Map.Entry pair = (Map.Entry) it.next();
//            DictEntry3 dd = (DictEntry3) pair.getValue();
//            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
//            //it.remove(); // avoids a ConcurrentModificationException
//             printPostingList(dd.postingList);
//        }
        System.out.println("------------------------------------------------------");
        System.out.println("*****    Number of terms = " + index.size());
        System.out.println("------------------------------------------------------");

    }

    //-----------------------------------------------
    public void buildIndex(String[] files) {
        int i = 0;
        for (String fileName : files) {
            try ( BufferedReader file = new BufferedReader(new FileReader(fileName))) {
                sources.put(i, fileName);
                String ln;
                while ((ln = file.readLine()) != null) {
                    String[] words = ln.split("\\W+");
                    for (String word : words) {
                        word = word.toLowerCase();
                        // check to see if the word is not in the dictionary
                        if (!index.containsKey(word)) {
                            index.put(word, new DictEntry3());
                        }
                        // add document id to the posting list
                        if (!index.get(word).postingList.contains(i)) {
                            index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                            index.get(word).postingList.add(i); // add the posting to the posting:ist
                        }
                        //set the term_fteq in the collection
                        index.get(word).term_freq += 1;
                    }
                }

            } catch (IOException e) {
                System.out.println("File " + fileName + " not found. Skip it");
            }
            i++;
        }
        printDictionary();
    }


    //----------------------------------------------------------------------------
    HashSet<Integer> intersect(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer>answer = new HashSet<>();
        for (Integer x :
                pL2) {
            if(pL1.contains(x)){
                answer.add(x);
            }
        }
        return answer;
    }
    //-----------------------------------------------------------------------


    HashSet<Integer> oringFunction(HashSet<Integer> pL1, HashSet<Integer> pL2) {
        HashSet<Integer>answer = new HashSet<>();
        for (Integer x : pL2) {
            answer.add(x);
        }

        for (Integer x : pL1) {
            answer.add(x);
        }
        return answer;
    }

    HashSet<Integer> notFunction(HashSet<Integer> pL1) {
        HashSet<Integer>answer = new HashSet<>();
        for (Integer x : sources.keySet()) {
            if(pL1.contains(x)){
                continue;
            }else{
                answer.add(x);
            }
        }
        return answer;
    }

    //-----------------------------------------------------------------------

    String[] rearrange(String[] words, int[] freq, int len) {
        boolean sorted = false;
        int temp;
        String sTmp;
        for (int i = 0; i < len - 1; i++) {
            freq[i] = index.get(words[i].toLowerCase()).doc_freq;
        }
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < len - 1; i++) {
                if (freq[i] > freq[i + 1]) {
                    temp = freq[i];
                    sTmp = words[i];
                    freq[i] = freq[i + 1];
                    words[i] = words[i + 1];
                    freq[i + 1] = temp;
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }
    HashSet<Integer> wordQuery(String query){
        HashSet<Integer> answer = new HashSet<Integer>();

        String[] tokens = query.split(" ");
        int tt=0;
        boolean f = false;
        for (int i=0;i<tokens.length;i++){
            if(tokens[i].equals("and")){
                tt=1;
            }else if(tokens[i].equals("or")) {
                tt=2;
            }else if(tokens[i].equals("not")){
                f=true;
            }else{
                if(index.get(tokens[i]) == null){
                    index.put(tokens[i], new DictEntry3());
                }
                HashSet<Integer> postingList = index.get(tokens[i]).postingList;
                if(f){
                    f=false;
                    postingList = notFunction(postingList);
                }

                if(tt==1){
                    answer = intersect(answer,postingList);
                }else{
                    answer = oringFunction(answer,postingList);
                }
            }
        }
        return answer;
    }
    //-----------------------------------------------------------------------
    public String find_04(String phrase) { // any mumber of terms optimized search
        String result = "";
        String[] words = phrase.split("\\W+");
        int len = words.length;
        //int [] freq = new int[len];
        words = rearrange(words, new int[len], len);
        HashSet<Integer> res = new HashSet<Integer>(index.get(words[0].toLowerCase()).postingList);
        int i = 1;
        while (i < len) {
            res = intersect(res, index.get(words[i].toLowerCase()).postingList);
            i++;
        }
        for (int num : res) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + sources.get(num) + "\n";
        }
        return result;
    }
    //-----------------------------------------------------------------------


}

//=====================================================================
public class Inverted_Index {

    public static void main(String args[]) throws IOException {
        Index3 index = new Index3();
        String phrase1 = "blue or best";
        String phrase2 = "blue and best";
        String phrase3 = "not blue and not best";

        index.buildIndex(new String[]{
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\500.txt", // change it to your path e.g. "c:\\tmp\\100.txt"
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\501.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\502.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\503.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\504.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\100.txt", // change it to your path e.g. "c:\\tmp\\100.txt"
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\101.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\102.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\103.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\104.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\105.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\106.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\107.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\108.txt",
                "D:\\College\\Year 4\\Second Term\\Information Retrieval\\Assignment\\assignment1\\322-2022\\docs\\109.txt"
        });
        System.out.println(index.wordQuery(phrase1));
        System.out.println(index.wordQuery(phrase2));
        System.out.println(index.wordQuery(phrase3));

    }
}
