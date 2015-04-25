/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
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
    public static void main1(String[] args) {
        Teacher shrek = new Teacher("Shrek");
        shrek.setDoesWork(0, false);
        shrek.setDoesWork(1, false);
        shrek.setDoesWork(2, false);
        shrek.setDoesWork(3, false);
        shrek.setDoesWork(4, false);
        Teacher donkey = new Teacher("Donkey");
        donkey.setDoesWork(0, false);
        donkey.setDoesWork(2, false);
        Subject math = new Subject("Math", new String[] {"Algebra", "Geometry (dash)", "Cat math"}, new int[] {2, 2, 1}, new Teacher[] {shrek, donkey}, 20);
        Subject history = new Subject("History", new String[] {"Swamp history"}, new int[] {2}, new Teacher[] {shrek}, 20);
        Student.addRequiredSubject(Grade.GRADE9, math);
        Student.addRequiredSubject(Grade.GRADE9, history);
        Student dragon = new Student("Dragon", Grade.GRADE9, new Klass[] {math.klasses.get(0)});
        ArrayList<Student> students = new ArrayList<>(Arrays.asList(new Student[] {dragon}));
        System.out.println(dragon.getRequirements());
        ArrayList<Subject> subjects = new ArrayList<>(Arrays.asList(new Subject[] {math, history}));
        RandomScheduler rd = new RandomScheduler(students, subjects);
        rd.startScheduling();
    }
    static Subject language;
    static int ti = 0;
    public static void createSubjects(Grade grade, ArrayList<Subject> subjects, ArrayList<Student> students) {
        String[] subjectn = new String[] {grade + "Math", grade + "History", grade + "English", grade + "Science"};
        for (int i = 0; i < subjectn.length; i++) {
            Teacher a = new Teacher("Teech" + (ti++));
            Teacher b = new Teacher("Teech" + (ti++));
            Subject dank = new Subject(subjectn[i], new String[] {"ClassOne", "ClassTwo"}, new int[] {3, 3}, new Teacher[] {a, b}, 19);
            Student.addRequiredSubject(grade, dank);
            subjects.add(dank);
        }
        for (int i = 0; i < 100; i++) {
            Student dragon = new Student(grade + "student" + i, grade);
            students.add(dragon);
        }
    }
    public static void main(String[] args) {
        ArrayList<Student> students = new ArrayList<>();
        ArrayList<Subject> subjects = new ArrayList<>();
        Teacher[] langTeach = new Teacher[] {new Teacher("Teech" + (ti++)), new Teacher("Teech" + (ti++)), new Teacher("Teech" + (ti++))};
        language = new Subject("Language", new String[] {"Spanish 1", "Spanish 2", "Japanese 1", "Japanese 2", "Mandarin 1", "Mandarin 2"}, new int[] {2, 2, 2, 2, 2, 2}, langTeach, 18);
        subjects.add(language);
        Student.addRequiredSubject(Grade.GRADE9, language);
        Student.addRequiredSubject(Grade.GRADE10, language);
        createSubjects(Grade.GRADE9, subjects, students);
        createSubjects(Grade.GRADE10, subjects, students);
        RandomScheduler rd = new RandomScheduler(students, subjects);
        double numAt = 0;
        double numU = 0;
        int numStud = students.size();
        BigInteger ntF = factorial(students.size());
        ArrayList<Integer> sofar = new ArrayList<>();
        ArrayList<BigDecimal> bd = new ArrayList<>();
        while (true) {
            try {
                rd.startScheduling();
                return;
            } catch (IllegalStateException e) {
                String x = e.getMessage();
                if (x.startsWith("S")) {
                    long time = System.currentTimeMillis();
                    try {
                        int numUn = Integer.parseInt(x.substring(1, x.length()));
                        //System.out.println("did");
                        numU += numUn;
                        numAt++;
                        int ii = students.size() - numUn;
                        sofar.add(ii);
                        BigDecimal qq = quotient(ii, numStud, ntF);
                        bd.add(qq);
                        for (int chance = 1; chance < 999; chance++) {
                            double c = ((double) chance) / 1000D;
                            chanceArray[chance] += binomialDist(ii, numStud, c, qq);
                        }
                    } catch (NumberFormatException ee) {
                        System.out.println(ee);
                        System.out.println(e);
                        continue;
                    }
                    System.out.println("Calc took " + (System.currentTimeMillis() - time));
                }
                System.out.println(x + "," + numU + "," + numAt);
                System.out.println(Math.floor(numU / (numAt) * 100) / 100 + " of " + students.size() + ", or " + toPercent(numU / (numAt * students.size())) + " of students, are unplacable on average over " + numAt + " attempts");
                double p = calculate(sofar, bd, students.size());
                System.out.println("This suggests that each student has a probability of " + toPercent(p));
                double xx = binomialDist(numStud, numStud, p, quotient(numStud, numStud, ntF));
                System.out.println("Therefore, the probability of getting all of them is " + toPercent(xx));
                double r = Math.log(0.5) / Math.log(1 - xx);
                System.out.println("Should take " + r + " attempts on average");
            }
        }
    }
    static double[] chanceArray = new double[1000];
    public static String toPercent(double v) {
        return Math.floor(v * 10000) / 100 + "%";
    }
    /*
     public static void main(String[] args) {
     ArrayList<Integer> dank = new ArrayList<>();
     dank.add(110);
     dank.add(12);
     dank.add(21);
     dank.add(9);
     calculate(dank, 200);
     }*/
    public static double calculate(ArrayList<Integer> trials, ArrayList<BigDecimal> quotient, int numTrials) {
        double maxV = 0;
        double maxC = 0;
        for (int c = 1; c < 99; c++) {
            double chance = ((double) c) / 1000D;
            double tot = chanceArray[c];
            if (tot > maxV) {
                maxV = tot;
                maxC = chance;
            }
        }
        //System.out.println(maxV);
        //System.out.println(maxC);
        return maxC;
    }
    public static BigDecimal quotient(int numCorrect, int numTrials, BigInteger ntF) {
        BigInteger denom = factorial(numCorrect).multiply(factorial(numTrials - numCorrect));
        BigDecimal ntFBD = new BigDecimal(ntF);
        BigDecimal denomBD = new BigDecimal(denom);
        BigDecimal quotient = ntFBD.divide(denomBD, 40, RoundingMode.HALF_UP);
        return quotient;
    }
    public static double binomialDist(int numCorrect, int numTrials, double probValue, BigDecimal quotient) {
        BigDecimal restBD = BigDecimal.valueOf(Math.pow(probValue, numCorrect) * Math.pow((1d - probValue), numTrials - numCorrect));
        return (quotient.multiply(restBD).doubleValue());
    }
    public static BigInteger factorial(int n) {
        BigInteger res = BigInteger.ONE;
        for (int i = n; i > 1; i--) {
            res = res.multiply(BigInteger.valueOf(i));
        }
        return (res);
    }
}
