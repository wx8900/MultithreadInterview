package ali;

import java.util.concurrent.ConcurrentHashMap;

// success
public class PrintNumThread {

    private static volatile int i = 1;
    private static volatile int sum = 0;
    private static ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap();
    private volatile int flag = 0;

    Thread threadA = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (i > 100) {
                    return;
                }
                if (flag == 0) {
                    System.out.println("threadA->" + ":" + i);
                    map.put("A", i);
                    i++;
                    flag = 1;
                }
            }
        }

    });

    Thread threadB = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (i > 100) {
                    return;
                }
                if (flag == 1) {
                    System.out.println("threadB->" + ":" + i);
                    map.put("B", i);
                    i++;
                    flag = 2;
                }
            }
        }

    });

    Thread threadC = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                if (flag == 2) {
                    sum += map.get("A") + map.get("B");
                    System.out.println("总数: ====>" + sum);
                    flag = 0;
                }
            }
        }
    });

    public static void main(String[] args) {
        new PrintNumThread().startThreeThread();
        try {
            Thread.sleep(6);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.exit(1);
    }

    public void startThreeThread() {
        threadA.start();
        threadB.start();
        threadC.start();
    }
}
