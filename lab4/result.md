<h1>Звіт
з лабораторної роботи  № 4 з дисципліни
«Технології паралельних обчислень»
</h1>

# «Розробка паралельних програм з використанням пулів потоків, екзекьюторів та ForkJoinFramework»

# Зміст
1) [Завдання](#Завдання)
2) [Виконання](#Виконання)
3) [Висновок](#Висновок)
4) [Додаток 1](#Додаток-1)

# Завдання
1. Побудуйте алгоритм статистичного аналізу тексту та визначте
   характеристики випадкової величини «довжина слова в символах» з
   використанням ForkJoinFramework. 20 балів. Дослідіть побудований
   алгоритм аналізу текстових документів на ефективність
   експериментально. 10 балів.
2. Реалізуйте один з алгоритмів комп’ютерного практикуму 2 або 3 з
   використанням ForkJoinFramework та визначте прискорення, яке
   отримане за рахунок використання ForkJoinFramework. 20 балів.
3. Розробіть та реалізуйте алгоритм пошуку спільних слів в текстових
   документах з використанням ForkJoinFramework. 20 балів.
4. Розробіть та реалізуйте алгоритм пошуку текстових документів, які
   відповідають заданим ключовим словам (належать до області
   «Інформаційні технології»), з використанням ForkJoinFramework. 30
   балів

# Виконання
## Алгоритм статистичного аналізу тексту
```scala
  def parallelAnalyze(array: Array[String], start: Int, end: Int, s: ParHashSet[Int]): Int = {
    if (end - start < threshold) sequentialAnalyze(array, start, end, s)
    else {
      val mid = start + ((end - start) / 2)
      val (l, r) = parallel(parallelAnalyze(array, start, mid, s), parallelAnalyze(array, mid, end, s))
      l + r
    }
  }

  def sequentialAnalyze(array: Array[String], start: Int, end: Int, parHashSet: ParHashSet[Int]): Int = {
    var res = 0
    for (i <- (start until end)) {
      val l = array(i).length
      if (!parHashSet.contains(l)) {
        parHashSet.addOne(l)
      }
      res += array(i).length
    }
    res
  }

  def getResult(s: ParHashSet[Int], sum: Int): Double = {
    try {
      sum / (s.sum.toDouble)
    } catch {
      case _: Throwable => 0
    }
  }
```
Прискорення при паралелізації: від 1 до 6 разів, в залежнсті від нижньої межі паралелізації

Найоптимальніша межа це 86 слів

Методи тестування можна побачити у додатку 1
## Реалізація множення матриць через ForkJoinFramework
```java
public MyMatrix mult(MyMatrix a, MyMatrix b) {
        var res =  new MyMatrix(a.size);


        StripeWorker[] runs = divideThreads(a, b, stripeQuantity, res);
        ForkJoinTask<Void>[] fs = new ForkJoinTask[runs.length];
        for (int i = 0; i < runs.length; i++) {
            StripeWorker rable = runs[i];
            fs[i] = (ForkJoinTask<Void>) pool.submit(rable);
        }
        for (var future : fs) {
            try {
                future.get();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return res;
    }
```
Прискорення виявилось невеликим, в середньому 1.04-1.06 рази в порівнянні зі звичайними потоками

## Алгоритм пошуку спільних слів
```scala
  def readOneFile(path: String): Set[String] = {
    tr.read(path).toSet
  }

  def seqRead(paths: Array[String], start: Int, end: Int): ParSet[String] = {
    paths.slice(start, end).foldLeft(new mutable.HashSet[String]())((acc, path) => acc.addAll(tr.read(path))).par
  }

  def parRead(paths: Array[String], start: Int, end: Int, threshold: Int): ParSet[String] = {
    if (end - start <= threshold) seqRead(paths, start, end)
    else {
      val mid = (start + end) / 2
      val (l, r) = parallel(parRead(paths, start, mid, threshold), parRead(paths, mid, end, threshold))
      l ++ r
    }
  }
```
Прискорення при паралелізації в середньому 2.4

## Пошук файлів за ключовими словами

```scala
  def seqSearch(paths: List[String], keywords: Set[String]): List[String] = {
    paths.filter(p => fileFitsKeywords(p, keywords))
  }


  def parSearch(paths: List[String], keywords: Set[String]): List[String] = {
    paths match {
      case Nil => Nil
      case x :: Nil => {
        if (fileFitsKeywords(x, keywords)) paths else Nil
      }
      case x :: xs => {
        val t = task(fileFitsKeywords(x, keywords))
        val l = parSearch(xs, keywords)
        if (t.join) x :: l
        else l
      }
    }
  }
```
Прискорення в середньому в 2 рази

# Висновок
У ході роботи ми дослідили **ForkJoinFramework**, екзекьютори та пули потоків на прикладі **Scala** та **Java**.
Для виміру прискорення я використовував бібліотеку `scalameter`
та дослідив різницю у швидкості між ForkJoin фреймворком та звичайними потоками.
ForkJoin виявився не набагато, але швидшим. У **Scala** ця концепція показує себе
набагато краще за стандартні потоки, через більш функціональну структуру мови, в
якій набагато частіше зустрічається рекурсія та такий дизайн.

# Додаток 1
Бенчмарк проходить за допомогою бібліотеки `scalameter`, яка дозволяє на момент
тестування вимкнути Garbage Collection та підігріти програму, щоб був більш
стабільний результат кожного разу. І треба менше експериментів, щоб отримати
правдиве значення
```scala
  val time = withWarmer(new Warmer.Default) withMeasurer {
    new Measurer.IgnoringGC
  } measure {
    // code we want to benchmark
  }
```