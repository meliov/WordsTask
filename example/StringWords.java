package example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class StringWords {
    /**
     * googled stuff:
     //https://www.geeksforgeeks.org/java/arrays-binarysearch-java-examples-set-1/
     //https://stackoverflow.com/questions/692569/how-can-i-count-the-time-it-takes-a-function-to-complete-in-java
     */
    private static List<String> loadAllWords() {
        try {
            URL wordsUrl = new URL("https://raw.githubusercontent.com/nikiiv/JavaCodingTestOne/master/scrabble-words.txt");
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(wordsUrl.openConnection().getInputStream()))) {
                return bufferedReader.lines().skip(2).toList();
            }
        }catch (IOException ioEx){
            System.out.printf("ERROR loading words: %s", ioEx.getMessage());
            System.out.println(Arrays.toString(ioEx.getStackTrace()));
            throw new RuntimeException(ioEx); // todo add throwing exception after testing is done, this stays for faster testing
        }
    }


    public static void main(String[] args) {
        String[] allWords = loadAllWords().toArray(new String[0]);
        long start = System.nanoTime();
        Arrays.sort(allWords);
         List<String> nineLetterWords =  Arrays.stream(allWords).filter(it -> it.length() == 9).toList().stream().sorted().toList(); //List.of("STARLINK");
        allWordsBetween2and8Letters = Arrays.stream(allWords)
                .filter(it -> it.length() >= 1 && it.length() <= 9)
                .sorted()
                .toArray(String[]::new);

        List<String> myWords = nineLetterWords.stream().map(word -> {
                    List<DepthWord> childWord = new LinkedList<>();
                    for (int i = 0; i < word.length(); i++) {
                        childWord.add(mapChildren(word, i));
                    }
                    return new DepthWord(childWord.stream().filter(Objects::nonNull).max(Comparator.comparingInt(DepthWord::getDepth)).orElse(null), word);
                })
                .filter(Objects::nonNull)
                .filter(it -> it.getDepth() == 9)
                .map(it -> it.word)
                .toList();

        System.out.println(myWords.size());

        System.out.printf("Time taken: %.2f seconds\n", (System.nanoTime() - start) / 1000000000.0);
    }



    static class DepthWord{
        DepthWord childWord;
        String word;
        public DepthWord(DepthWord childWord, String word) {
            this.childWord = childWord;
            this.word = word;
        }

        public int getDepth(){
            int depth = 0;
            DepthWord current = this;
            while (current != null) {
                depth++;
                current = current.childWord;
            }
            return depth;
        }

        @Override
        public String toString() {
            return "DepthWord{" +
                    "childWord=" + childWord +
                    ", word='" + word + '\'' +
                    '}';
        }
    }
    private static String[] allWordsBetween2and8Letters;

    private static Map<String, DepthWord> cache = new HashMap<>();
    private static DepthWord mapChildren(String wordValue, int index){

        String key = wordValue + "_" + index;
        if(cache.containsKey(key)){
            return cache.get(key);
        }

        StringBuilder reducedWord = new StringBuilder();
        for (int i = 0; i < wordValue.length(); i++) {
            if(i != index){
                reducedWord.append(wordValue.charAt(i));
            }
        }
        DepthWord value;
        if(reducedWord.toString().length() == 1){
            if(reducedWord.toString().equals("A") || reducedWord.toString().equals("I")){
                value = new DepthWord(null, reducedWord.toString());
            }else{
                value = null;
            }
        }else if(Arrays.binarySearch(allWordsBetween2and8Letters, reducedWord.toString()) >= 0){
            List<DepthWord> childWord = new LinkedList<>();
            for (int i = 0; i < reducedWord.toString().length(); i++) {
                childWord.add(mapChildren(reducedWord.toString(), i));
            }
            value = new DepthWord(childWord.stream().filter(Objects::nonNull).max(Comparator.comparingInt(DepthWord::getDepth)).orElse(null), reducedWord.toString());
        }else{
            if(index == wordValue.length()-1){
                value = null;
            }else{
            value = mapChildren(wordValue, ++index);
            }
        }

        cache.put(key, value);

        return value;
    }


}
