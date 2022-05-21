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

