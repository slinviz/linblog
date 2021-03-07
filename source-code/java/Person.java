import java.lang.Comparable;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

class PersonComparator implements Comparator<Person> {
    @Override
    public int compare(Person p1, Person p2){
        return Integer.compare(p1.age, p2.age);
    }
}

public class Person implements Comparable<Person> {
    String name;
    int age;
    public Person(String name, int age){
        super();
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString(){
        return "Person < " + this.name + " , " + this.age +" >";
    }
    
    @Override
    public int compareTo(Person other){
        return name.compareTo(other.name);
    }

    public static void main(String[] args){
        List<Person> list = new ArrayList<>();
        Person p1 = new Person("Tom", 12);
        Person p2 = new Person("Tim", 34);
        Person p3 = new Person("Sally", 15);
        list.add(p1);
        list.add(p2);
        list.add(p3);

        System.out.println("java.lang.Comparable --------------");
        Collections.sort(list);
        for(Person elem: list){
            System.out.println(elem);
        }

        // using Comparator
        System.out.println("java.util.Comparator ==============");
        Collections.sort(list, new PersonComparator());
        for(Person elem: list){
            System.out.println(elem);
        }

        // using Comparator
        System.out.println("java.util.Comparator2 ==============");
        Collections.sort(list, new Comparator<Person>(){
            @Override
            public int compare(Person p1, Person p2){
                return p2.age - p1.age;
            }
        });
        for(Person elem: list){
            System.out.println(elem);
        }
    }
}