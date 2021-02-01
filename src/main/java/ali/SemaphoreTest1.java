package ali;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;

// failure
public class SemaphoreTest1 {

    private static Semaphore mutex = new Semaphore(1);
    private static volatile int num = 1;
    private static volatile int sum = 0;
    private static ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap();

    public static void main(String[] args) {
        //打印奇数的线程
        new Thread(() -> {
            while (true) {
                try {
                    if (num > 100) {
                        return;
                    }
                    if (num % 3 == 1) {
                        if ((num & 1) == 1) {
                            mutex.acquire();
                            System.out.println("奇数：" + num);
                            map.put("A", num);
                            num++;
                            mutex.release();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread A").start();

        //打印偶数的线程
        new Thread(() -> {
            while (true) {
                try {
                    if (num > 100) {
                        return;
                    }
                    if (num % 3 == 2) {
                        if ((num & 1) != 1) {
                            mutex.acquire();
                            System.out.println("偶数：" + num);
                            map.put("B", num);
                            num++;
                            mutex.release();
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread B").start();

        //相加A，B的线程
        new Thread(() -> {
            while (true) {
                try {
                    if (num > 100) {
                        return;
                    }
                    if (num % 3 == 0) {
                        mutex.acquire();
                        sum += map.get("A") + map.get("B");
                        System.out.println("总数: ====>" + sum);
                        mutex.release();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "Thread C").start();

        try {
            Thread.sleep(25);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            String mapKey = entry.getKey();
            Integer mapValue = entry.getValue();
            System.out.println(mapKey + "==============" + mapValue);
        }
        System.exit(0);

    }
}