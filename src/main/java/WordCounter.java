
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;

public class WordCounter {
    static String ss = "";
    static Map<String, Integer> myMap = Collections.synchronizedMap(new HashMap<String, Integer>());
    static Map<Long, Integer> myThreadMap = Collections.synchronizedMap
            (new HashMap<Long, Integer>());
    static Result rs = new Result(ss, myMap,myThreadMap);
    static String filePath = "C://soft3/sft.txt";
    static int threadCount = 5;
    public static void main(String[] args) throws InterruptedException {

        threadWorks(filePath,threadCount);
    }

    public static void threadWorks(String fileName, int cntThread){

        File file = new File(fileName);


        String fileContent = "";

        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        ExecutorService executorService = (ExecutorService) Executors.newFixedThreadPool(cntThread);


        while (scanner.hasNext()) {

            String m = scanner.nextLine();
            fileContent = fileContent + m;

        }


        String[] str = fileContent.split("[ \n\t\r,.;:!?(){}}]+");

        double totalWord = str.length;

        String[] sentenceHolder = fileContent.split("(?<=[.!?])\\s*");

        List<ThreadDnm2> taskList = new ArrayList<>(sentenceHolder.length);

        double totalSntnc = sentenceHolder.length;

        System.out.println();
        System.out.println("Sentence Count : " + sentenceHolder.length);
        System.out.println("Avg. Word Count: " + Math.round(totalWord / totalSntnc));
        System.out.println();

        for (int j = 0; j < sentenceHolder.length; j++) {

            String sntnTemp = sentenceHolder[j];
            Result rsTemp = new Result(sntnTemp, rs.myMap, rs.myThreadMap);
            ThreadDnm2 tD2 = new ThreadDnm2(rsTemp);
            taskList.add(tD2);
        }

        long start = System.currentTimeMillis();

        List<Future<Result>> resultList = null;
        try {
            resultList = executorService.invokeAll(taskList);
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);

        } catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("Interrupted Exception" );
        }

        System.out.println("kelime array size :" + rs.myMap.size());



        LinkedHashMap<String, Integer> reverseSortedMap = new LinkedHashMap<>();
        rs.myMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        rs.myThreadMap.entrySet().forEach( entry -> {
            System.out.println(  "Threadid=" + entry.getKey() + " , Count =  " + entry.getValue() );
        });



        reverseSortedMap.entrySet().forEach( entry -> {
            System.out.println( entry.getKey() + " " + entry.getValue() );
        });

    }


    public static class ThreadDnm2 implements Callable<Result> {
        private final Result input;

        public ThreadDnm2(Result input) {
            this.input = input;
        }


        @Override
        public Result call() throws Exception {

            String m = this.input.sntn;  //;
            String[] str = m.split("[ !\"\\#$%&'()*+,-./:;<=>?@\\[\\]^_`{|}~]+");


            for (int f = 0; f < str.length; f++) {
                String key = str[f];  //.toUpperCase();
                 if (str[f].length() > 1) {
                    if  (input.myMap.get(key) == null) {
                        synchronized(this.input){
                        this.input.myMap.put(key, 1);}
                        //System.out.println("ben " + Thread.currentThread().getId() + " nolu thred im ve " + key + " kelimesini ekledim  rs.myMap.size() = " + rs.myMap.size());
                    } else {
                        synchronized(this.input){
                     this.input.myMap.put(key, (input.myMap.get(key)) + 1);}
                       // System.out.println("ben " + Thread.currentThread().getId() + " nolu thred im ve " + key + " kelimesini arttırdım rs.myMap.size() = " + rs.myMap.size());
                    }
                }

            }


           // System.out.println("ben " + Thread.currentThread().getId() + " nolu thred im ve " + input.sntn + " cümlesini aldım");


            Long key2 = Thread.currentThread().getId();
            if  (input.myThreadMap.get(key2) == null) {
                synchronized(this.input){
                    this.input.myThreadMap.put(key2, 1);}
                //System.out.println("ben " + Thread.currentThread().getId() + " nolu thred im ve " + key + " kelimesini ekledim  rs.myMap.size() = " + rs.myMap.size());
            } else {
                synchronized(this.input){
                    this.input.myThreadMap.put(key2, (input.myThreadMap.get(key2)) + 1);}
                // System.out.println("ben " + Thread.currentThread().getId() + " nolu thred im ve " + key + " kelimesini arttırdım rs.myMap.size() = " + rs.myMap.size());
            }
            synchronized(this.input) {return this.input;}
        }


    }

    static class Result {
        private String sntn;
        static Map<String, Integer> myMap = Collections.synchronizedMap(new HashMap<String, Integer>());
        static Map<Long, Integer> myThreadMap = Collections.synchronizedMap(new HashMap<Long, Integer>());

        public Result(String sntn, Map<String, Integer> myMap, Map<Long, Integer> myThreadMap) {
            super();
            this.sntn = sntn;
            this.myMap = myMap;
        }
    }

}

