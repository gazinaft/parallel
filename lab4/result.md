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
  def parallelSum(array: Array[String], start: Int, end: Int, s: ParMap[Int, Int]): (Int, Int, Int) = {
   if (end - start < threshold) sequentialSum(array, start, end, s)
   else {
      val mid = start + ((end - start) / 2)
      val (l, r) = parallel(parallelSum(array, start, mid, s), parallelSum(array, mid, end, s))
      (l._1 + r._1, l._2 + r._2, array.length)
   }
}

def parallelMetrics(array: Array[String], tr: TextReader, start: Int, end: Int, threshold: Int,
                    f: (Array[String], Int, Int, ParMap[Int, Int]) => (Int, Int, Int)): ((Int, Int, Int), ParMap[Int, Int]) = {
   if (end - start <= threshold) sequentialMetrics(array: Array[String], tr, start, end, f)
   else {
      val mid = start + ((end - start) / 2)
      val (l, r) = parallel(parallelMetrics(array, tr, start, mid, threshold, f),
         parallelMetrics(array, tr, mid, end, threshold, f))
      val (sum1, sqsum1, len1) = l._1
      val (sum2, sqsum2, len2) = r._1
      val resmap: ParMap[Int, Int] = new ParHashMap[Int, Int]() ++ (l._2.toParArray ++ r._2.toParArray).groupBy(_._1).map { case (k, v) => k -> v.map(_._2).sum }
      ((sum1 + sum2, sqsum1 + sqsum2, len1 + len2), resmap)
   }
}

def sequentialMetrics(strings: Array[String], reader: TextReader, start: Int, end: Int,
                      f: (Array[String], Int, Int, ParMap[Int, Int]) => (Int, Int, Int)): ((Int, Int, Int), ParMap[Int, Int]) = {
   val hm = new ParHashMap[Int, Int]()
   var res = (0, 0, 0)
   for (i <- start until end) {
      val text = reader.read(strings(i))
      val (sum, sqsum, len) = f(text, 0, text.length, hm)
      res = (res._1 + sum, res._2 + sqsum, res._3 + len)
   }
   (res, hm)
}

def sequentialSum(array: Array[String], start: Int, end: Int, parHashMap: ParMap[Int, Int]): (Int, Int, Int) = {
   var res = 0
   var ressq = 0
   for (i <- (start until end)) {
      val l = array(i).length;
      parHashMap.synchronized {
         if (!parHashMap.contains(l)) parHashMap.put(l, 0)
         parHashMap.put(l, parHashMap.apply(l) + 1);
      }
      res += l
      ressq += (l * l)
   }
   (res, ressq, array.length)
}

def resFromMetrics(metrics: ((Int, Int, Int), ParMap[Int, Int])) = {
   val (temp, hm) = metrics
   val (sum, sqsum, size) = temp
   val mean = sum.toDouble / size
   val dispersion = sqsum.toDouble / size
   val mode = hm.reduce((acc, x) => if (acc._2 > x._2) acc else x)._1
   (mean, dispersion, mode)
}
```
Тут я розглянув 2 рівні паралелізації: паралелізація на рівні читання файлів,
та паралельна обробка всередині самих файлів

### Паралельна обробка файлів при послідовній обробці
| Time               | Type                             |
|--------------------|----------------------------------|
| 16.377270225000004 | Full sequential                  |
| 9.612178624999999  | File parallelization threshold 1 |
| 11.215021574999998 | File parallelization threshold 2 |


### Паралельна обробка всередині файлів при зовнішній паралелізації з межею 1
| Time               | Inner parallelization threshold |
|--------------------|---------------------------------|
| 17.785259475000004 | 2                               |
| 14.810244037500008 | 3                               |
| 13.748511312500003 | 4                               |
| 13.617458500000001 | 5                               |
| 12.0746725375      | 6                               |
| 12.067185074999998 | 7                               |
| 11.680434725000003 | 8                               |
| 12.069076549999997 | 9                               |
| 11.600807825       | 10                              |
| 11.440259862500003 | 11                              |
| 11.428775937499996 | 12                              |
| 11.272804162499998 | 13                              |
| 11.506774462500001 | 14                              |
| 11.78076165        | 15                              |
| 11.041735362499999 | 16                              |
| 11.444801637500001 | 17                              |
| 11.183827437500002 | 18                              |
| 11.361060137499999 | 19                              |
| 10.974701100000003 | 20                              |
| 10.844088975000004 | 21                              |
| 10.847700962500006 | 22                              |
| 10.629176737499995 | 23                              |
| 10.788235625       | 24                              |
| 10.7280808875      | 25                              |
| 10.940930125000003 | 26                              |
| 10.767355149999998 | 27                              |
| 10.960828200000002 | 28                              |
| 11.0184899         | 29                              |
| 11.560550199999998 | 30                              |
| 11.446403299999991 | 31                              |
| 10.894525099999996 | 32                              |
| 10.996249850000002 | 33                              |
| 10.652961849999997 | 34                              |
| 10.859267012499998 | 35                              |
| 10.690172224999998 | 36                              |
| 10.732948900000004 | 37                              |
| 11.1544136875      | 38                              |
| 10.957705187500002 | 39                              |
| 10.861863425000003 | 40                              |
| 10.6074239375      | 41                              |
| 10.771508825000002 | 42                              |
| 10.589431824999998 | 43                              |
| 10.569895787500004 | 44                              |
| 10.389353125000001 | 45                              |
| 10.617139999999996 | 46                              |
| 10.39383905        | 47                              |
| 10.515025312500004 | 48                              |
| 10.4365505         | 49                              |
| 10.476556849999994 | 50                              |
| 10.526301399999994 | 51                              |
| 10.583200625       | 52                              |
| 10.510145099999995 | 53                              |
| 10.874294250000002 | 54                              |
| 10.345737637499997 | 55                              |
| 10.549401875000003 | 56                              |
| 10.803476375000008 | 57                              |
| 10.298822187500003 | 58                              |
| 10.503104662500002 | 59                              |
| 10.399166675       | 60                              |
| 10.394642137499998 | 61                              |
| 10.524965625       | 62                              |
| 10.468274074999998 | 63                              |
| 10.6425523         | 64                              |
| 10.702117562499998 | 65                              |
| 10.5285801         | 66                              |
| 10.626908562499995 | 67                              |
| 10.667103300000004 | 68                              |
| 10.537429850000006 | 69                              |
| 10.794193737499999 | 70                              |
| 10.417014149999996 | 71                              |
| 10.572113449999998 | 72                              |
| 10.438794975000006 | 73                              |
| 10.8185265625      | 74                              |
| 10.7020425625      | 75                              |
| 10.332905812499998 | 76                              |
| 10.473048012500001 | 77                              |
| 10.417664237499997 | 78                              |
| 10.455175899999999 | 79                              |
| 10.707047799999996 | 80                              |
| 10.484214937500003 | 81                              |
| 10.381860075000002 | 82                              |
| 10.487782287499996 | 83                              |
| 10.35839975        | 84                              |
| 10.352975262500003 | 85                              |
| 10.5138437375      | 86                              |
| 10.18792525        | 87                              |
| 10.835528562499999 | 88                              |
| 10.422155          | 89                              |
| 10.527104862500002 | 90                              |
| 10.611547925       | 91                              |
| 10.331682624999997 | 92                              |
| 10.391108737500002 | 93                              |
| 10.485931025000001 | 94                              |
| 10.281445362500003 | 95                              |
| 10.5285182         | 96                              |
| 10.543238750000002 | 97                              |
| 10.472343850000001 | 98                              |
| 10.4812899         | 99                              |
| 10.339855275000001 | 100                             |

Найбільш вигідний варіант паралелізації: зовнішня паралелізація з
межею 1 і послідовною внутрішною обробкою

### Результати обробки:
Матсподівання: 5.2302076701531
Дисперсія: 34.69997877823253
Мода: 3

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
### Варіюваня кількосі потоків та підзадач
| Matrix Size | Stripe Threads | FJ Threads | FJ tasks | Speedup            |
|-------------|----------------|------------|----------|--------------------|
| 1000        | 2              | 2          | 2        | 0.9721041248812821 |
| 1000        | 2              | 2          | 4        | 1.001379399078551  |
| 1000        | 2              | 2          | 8        | 0.8622476394150593 |
| 1000        | 2              | 2          | 16       | 0.8758347766034216 |
| 1000        | 4              | 4          | 2        | 0.5818391194565733 |
| 1000        | 4              | 4          | 4        | 1.0059315557849282 |
| 1000        | 4              | 4          | 8        | 0.4960512358863374 |
| 1000        | 4              | 4          | 16       | 0.5210259104769244 |
| 1000        | 8              | 8          | 4        | 0.7940361021168661 |
| 1000        | 8              | 8          | 8        | 1.0244889801613537 |
 | 1000        | 8              | 8          | 12       | 0.5889486750214702 |
| 1000        | 8              | 8          | 16       | 0.5182440605291971 |
Найоптимальніші результати у нас виходять коли у нас кількість потоків дорівнює кількості підзадач

### Варіювання об'єму матриць
| Matrix Size | Stripe Threads | FJ Threads | FJ tasks | Speedup            |
|-------------|----------------|------------|----------|--------------------|
| 500         | 2              | 2          | 2        | 1.0680380127747626 |
| 500         | 2              | 2          | 4        | 0.9605025615169447 |
| 500         | 4              | 4          | 4        | 1.0781446773116015 |
| 500         | 4              | 4          | 8        | 0.7510143369120321 |
| 1500        | 2              | 2          | 2        | 1.0266697711123807 |
| 1500        | 4              | 4          | 4        | 0.9717568313830587 |
| 1500        | 8              | 8          | 8        | 0.9590039475259751 |
| 1500        | 8              | 8          | 10       | 0.7364381813325    |
Найоптимальніше працює на маленьких матрицях

Прискорення виявилось невеликим, іноді навіть повільніше виходить, в середньому 0.7-1.06 рази в порівнянні зі звичайними потоками, найкраще показує себе на невеликих розмірах, тому що там більше latency
де відсоток чекання більший, ніж на більших розмірах

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
Код можна знайти у [репозиторії на github](https://github.com/gazinaft/parallel)

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


FJ Framework у package файлах. У scala робота з ним відбувається трохи по-іншому, через обгортки `parallel` i `task`
```scala
import java.util.concurrent.{ForkJoinPool, ForkJoinTask, ForkJoinWorkerThread, RecursiveTask}
import scala.util.DynamicVariable

package object FileSearch {
  val forkJoinPool = new ForkJoinPool

  abstract class TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T]
    def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
      val right = task {
        taskB
      }
      val left = taskA
      (left, right.join())
    }
  }

  class DefaultTaskScheduler extends TaskScheduler {
    def schedule[T](body: => T): ForkJoinTask[T] = {
      val t = new RecursiveTask[T] {
        def compute = body
      }
      Thread.currentThread match {
        case wt: ForkJoinWorkerThread =>
          t.fork()
        case _ =>
          forkJoinPool.execute(t)
      }
      t
    }
  }

  val scheduler =
    new DynamicVariable[TaskScheduler](new DefaultTaskScheduler)

  def task[T](body: => T): ForkJoinTask[T] = {
    scheduler.value.schedule(body)
  }

  def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
    scheduler.value.parallel(taskA, taskB)
  }

  def parallel[A, B, C, D](taskA: => A, taskB: => B, taskC: => C, taskD: => D): (A, B, C, D) = {
    val ta = task { taskA }
    val tb = task { taskB }
    val tc = task { taskC }
    val td = taskD
    (ta.join(), tb.join(), tc.join(), td)
  }

}
```