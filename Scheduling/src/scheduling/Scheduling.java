/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;
import java.util.ArrayList;
import java.util.Arrays;
/**
 *
 * @author leijurv
 */
public class Scheduling {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Teacher shrek = new Teacher("Shrek");
        shrek.setDoesWork(0, false);
        shrek.setDoesWork(1, false);
        shrek.setDoesWork(2, false);
        shrek.setDoesWork(3, false);
        shrek.setDoesWork(4, false);
        Teacher donkey = new Teacher("Donkey");
        donkey.setDoesWork(0, false);
        donkey.setDoesWork(2, false);
        ArrayList<Teacher> teachers = new ArrayList<>(Arrays.asList(new Teacher[] {shrek, donkey}));
        Subject math = new Subject("Math", new String[] {"Algebra", "Geometry (dash)", "Cat math"}, new int[] {2, 2, 1}, new Teacher[] {shrek, donkey});
        Subject history = new Subject("History", new String[] {"Swamp history"}, new int[] {2}, new Teacher[] {shrek});
        Student.addRequiredSubject(Grade.GRADE9, math);
        Student.addRequiredSubject(Grade.GRADE9, history);
        Student dragon = new Student("Dragon", Grade.GRADE9, new Klass[] {math.klasses.get(0)});
        ArrayList<Student> students = new ArrayList<>(Arrays.asList(new Student[] {dragon}));
        System.out.println(dragon.getRequirements());
        ArrayList<Subject> subjects = new ArrayList<>(Arrays.asList(new Subject[] {math, history}));
        RandomScheduler rd = new RandomScheduler(teachers, students, subjects);
        rd.startScheduling();
    }
}
