/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduling;
/**
 *
 * @author leijurv
 */
public class Scheduling {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Subject math = new Subject("Math", new String[] {"Algebra", "Geometry (dash)"}, new int[] {2, 4});
        for (Klass k : math.klasses) {
            for (Section s : k.sections) {
                System.out.println(s);
            }
        }
    }
}
