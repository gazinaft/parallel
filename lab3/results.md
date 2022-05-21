<h1>Звіт
з лабораторної роботи  № 3 з дисципліни
«Технології паралельних обчислень»
</h1>

# «Розробка паралельних програм з використанням механізмів синхронізації: синхронізовані методи, локери, спеціальні типи»

[//]: # (## Виконав: ІП-91 Газін Костянтин)

[//]: # ()
[//]: # ()
[//]: # (## Перевірила: Стеценко Інна Вячеславівна)


# Зміст
1) [Завдання](#Завдання)
2) [Виконання](#Виконання)
3) [Висновок](#Висновок)
4) [Додаток 1](#Додаток-1)

# Завдання
1. Реалізуйте програмний код, даний у лістингу, та протестуйте його при різних значеннях параметрів. Модифікуйте програму, використовуючи методи управління потоками, так, щоб її робота була завжди коректною. Запропонуйте три різних варіанти управління. 30 балів.
2. Реалізуйте приклад Producer-Consumer application (див. https://docs.oracle.com/javase/tutorial/essential/concurrency/guardmeth.html ). Модифікуйте масив даних цієї програми, які читаються, у масив чисел заданого розміру (100, 1000 або 5000) та протестуйте програму. Зробіть висновок про правильність роботи програми. 20 балів.
3. Реалізуйте роботу електронного журналу групи, в якому зберігаються оцінки з однієї дисципліни трьох груп студентів. Кожного тижня лектор і його 3 асистенти виставляють оцінки з дисципліни за 100-бальною шкалою. 40 балів.
4. Зробіть висновки про використання методів управління потоками в java. 10 балів.

# Виконання
## Синхронізація банку
### I метод синхронізований метод
```java
    public synchronized void transfer(int from, int to, int amount) {
        accounts[from] -= amount;
        accounts[to] += amount;
        ntransacts++;
        if (ntransacts % NTEST == 0)
            test();
    }
```

### II метод Atomic типи даних
```java
    private final AtomicInteger[] accounts;

    private long ntransacts = 0;
    
    public Bank.Bank(int n, int initialBalance){
        accounts = new AtomicInteger[n];
        for (int i = 0; i < accounts.length; ++i) {
            accounts[i] = new AtomicInteger(0);
        }
        int i;
        for (i = 0; i < accounts.length; i++)
            accounts[i].getAndSet(initialBalance);
        ntransacts = 0;
    }
    
    public void transfer(int from, int to, int amount) {
        accounts[from].addAndGet(-amount);
        accounts[to].addAndGet(amount);
        ntransacts++;
        if (ntransacts % NTEST == 0)
            test();
    }

    public void test(){
        int sum = 0;
        for (int i = 0; i < accounts.length; i++)
            sum += accounts[i].get();
        System.out.println("Transactions:" + ntransacts
                + " Sum: " + sum);
    }
```

### III метод Локери
```java
    private Lock lock = new ReentrantLock();

    public void transfer(int from, int to, int amount) {
        lock.lock();
        try {
           accounts[from] -= amount;
           accounts[to] += amount;
           ntransacts++;
           if (ntransacts % NTEST == 0)
           test();
        } finally {
            lock.unlock();
        }
    }
```

## Producer-Consumer application
### Модифікований код
```java
package ConsumerProducer;

import java.util.Random;

public class Consumer implements Runnable {
    private Drop drop;

    public Consumer(Drop drop) {
        this.drop = drop;
    }

    public void run() {
        Random random = new Random();
        int size = drop.take();
        for (int i = 0; i < size; ++i) {
            var message = drop.take();
            System.out.println("MESSAGE " + (i + 1) + " OF " + size + " RECEIVED: " + message);
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {}
        }
    }
}
```
```java
package ConsumerProducer;

public class Drop {
    // Message sent from producer
    // to consumer.
    private int message;
    // True if consumer should wait
    // for producer to send message,
    // false if producer should wait for
    // consumer to retrieve message.
    private boolean empty = true;

    public synchronized int take() {
        // Wait until message is
        // available.
        while (empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = true;
        // Notify producer that
        // status has changed.
        notifyAll();
        return message;
    }

    public synchronized void put(int message) {
        // Wait until message has
        // been retrieved.
        while (!empty) {
            try {
                wait();
            } catch (InterruptedException e) {}
        }
        // Toggle status.
        empty = false;
        // Store message.
        this.message = message;
        // Notify consumer that status
        // has changed.
        notifyAll();
    }
}
```
```java
package ConsumerProducer;

import java.util.Random;

public class Producer implements Runnable {
    private Drop drop;
    private int ARRSIZE = 1000;

    public Producer(Drop drop) {
        this.drop = drop;
    }

    public void run() {
        Random random = new Random();
        int[] importantInfo = new int[ARRSIZE];

        drop.put(ARRSIZE);
        for (int i = 0; i < importantInfo.length; i++) {
            importantInfo[i] = i;
            drop.put(importantInfo[i]);
            try {
                Thread.sleep(random.nextInt(100));
            } catch (InterruptedException e) {}
        }
    }
}
```

Також розроблений варіант Producer-Consumer
application з використанням ArrayBlockingQueue
у ролі посередника для передачі даних

## Електронний журнал
Особливістю журналу є його структура, з мінімізованою синхронізацією, що дозволяє досягти кращого перформансу
```java
package EJournal;

import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;

public class Journal {

    public static final int GRADES_PER_TERM = 10;
    public static final int STUDENTS_PER_GROUP = 10;

    HashMap<String, ArrayBlockingQueue<Integer>[]> students;

    public Journal(String[] groupNames) {
        students = new HashMap<>();
        for (var gn: groupNames) {
            var gradesPerGroup = new ArrayBlockingQueue[STUDENTS_PER_GROUP];
            for (int i = 0; i < STUDENTS_PER_GROUP; ++i) {
                gradesPerGroup[i] = new ArrayBlockingQueue<Integer>(GRADES_PER_TERM);
            }
            students.put(gn, gradesPerGroup);
        }
    }

    public void grade(String groupName, int person, int grade) {
        try {
            students.get(groupName)[person].put(grade);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Object[] getGradesFor(String groupName, int person) {
        return students.get(groupName)[person].toArray();
    }

    public void printAllGrades() {
        for (var group : students.keySet()) {
            System.out.println(group);
            for (int i = 0; i < STUDENTS_PER_GROUP; i++) {
                System.out.println(students.get(group)[i]);
            }
        }
    }

}
```
# Висновок
У ході роботи ми ознайомились з різними методами синхронізації потоків: методи, типи, guarded-блоки. Також попрактикувались у написанні Producer-consumer додатків.

# Додаток 1
Код можна знайти на [гітхабі](https://github.com/gazinaft/parallel)

