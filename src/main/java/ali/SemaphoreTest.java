package ali;

import java.util.concurrent.Semaphore;

// success
public class SemaphoreTest {

    //表示同一时间内最多只允许1个线程执行 acquire()和release()之间的代码
    private static Semaphore mutex = new Semaphore(1);
    private static volatile int num = 1;

    public static void main(String[] args) {
        new Thread(() -> {
            while (true) {
                try {
                    if (num > 100) {
                        return;
                    }
                    if (num % 2 != 0) {
                        mutex.acquire();
                        System.out.println("奇数：" + num++);
                        mutex.release();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "打印奇数的线程").start();

        new Thread(() -> {
            while (true) {
                try {
                    if (num > 100) {
                        return;
                    }
                    if (num % 2 == 0) {
                        mutex.acquire();
                        System.out.println("偶数: " + num++);
                        mutex.release();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "打印偶数的线程").start();
    }
}