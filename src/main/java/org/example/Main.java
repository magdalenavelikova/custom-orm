package org.example;
/**
 * 1. Клас за настройките за вързка с базата - ще връща обект - EntityManager , който ще може да управлява базата на абстрактно ниво
 * - вид база
 * - потребителско име и парола
 * - име на базата
 * 2. Преди да се върне такъв обект, ще обиколим всички класове в проекта, за а намерим тези, които са анотирани с @Entity /наша анотация/
 * 3. Ще проверяваме дали има такава таблица и ако няма ще я създадем по предварително описани @Id и @Column(име, тип)
 * 4. Манипулация на данните чрез EntityManager - в него трябва да има
 * - функция за извличане на обект по ID– find(id, Class)
 * - функция за записване или редакция на обект по ID– persist(Object)
 * - функция за изтриване на обект по ID– delete(id, Class)
 **/

public class Main {
    public static void main(String[] args) {
        System.out.println("Hello world!");
    }
}