package lahuo;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceCalculator implements Calculator {
    private int parallism;
    private ExecutorService pool;

    public static void main(String[] args) {
        Instant start = Instant.now();
        ExecutorServiceCalculator loopCalculator = new ExecutorServiceCalculator();
        int charNumber = 26;
        int result = loopCalculator.count(ForLoopCalculator.str, charNumber);
        Instant end = Instant.now();
        System.out.println("耗时：" + Duration.between(start, end).toMillis() + "ms");
        System.out.println("The minimum of character appear is " + result);
    }

    public ExecutorServiceCalculator() {
        // CPU的核心数 默认就用cpu核心数了
        parallism = Runtime.getRuntime().availableProcessors();
        pool = Executors.newFixedThreadPool(parallism);
    }

    @Override
    public int count(String str, int charNumbers) {
        List<Future<Integer>> results = new ArrayList<>();
        // 把任务分解为 n 份，交给 n 个线程处理   4核心 就等分成4份呗
        // 然后把每一份都扔个一个SumTask线程 进行处理
        int part = str.length() / parallism;
        for (int i = 0; i < parallism; i++) {
            int from = i * part; //开始位置
            int to = (i == parallism - 1) ? str.length() - 1 : (i + 1) * part - 1; //结束位置

            //扔给线程池计算
            results.add(pool.submit(new SumTask(str, from, to, charNumbers)));
        }

        // 把每个线程的结果相加，得到最终结果 get()方法 是阻塞的
        // 优化方案：可以采用CompletableFuture来优化  JDK1.8的新特性
        int total = 0;
        for (Future<Integer> f : results) {
            try {
                total += f.get();
            } catch (Exception ignore) {
            }
        }
        int maxNumber = Integer.MAX_VALUE;
        int[] chars = new int[charNumbers];
        try {
            for (int i = 0, len = str.length(); i < len; i++) {
                char ch = str.charAt(i);
                chars[ch - 97]++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < charNumbers; i++) {
            //System.out.println((char) (i + 97) + " appears " + chars[i] + " times! ");
            if (chars[i] < maxNumber) {
                maxNumber = chars[i];
            }
        }
        return maxNumber;
    }

    //处理计算任务的线程
    private static class SumTask implements Callable<Integer> {
        private String str;
        private int charNumbers;
        private int from;
        private int to;

        public SumTask(String str, int from, int to, int charNumbers) {
            this.str = str;
            this.from = from;
            this.to = to;
            this.charNumbers = charNumbers;
        }

        @Override
        public Integer call() {
            Integer total = 0;
            int[] chars = new int[charNumbers];
            for (int i = from; i <= to; i++) {
                char ch = str.charAt(i);
                chars[ch - 97]++;
            }
            return total;
        }
    }

}
