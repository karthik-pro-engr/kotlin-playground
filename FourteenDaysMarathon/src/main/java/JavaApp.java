import com.interop.LibKt;
import com.interop.PersonInterOp;

public class JavaApp {
    public static void main(String[] args) {
        // 1 — default args attempt
        System.out.println(LibKt.welcome("Karthik", true));             // A
        System.out.println(LibKt.welcome("Amy",true));        // B

        // 2 — calling top-level calc functions
        System.out.println(LibKt.calc(3,4));               // C (which binding?)
        System.out.println(LibKt.calc(2,5));
        // 3 — constructor misuse
        PersonInterOp p = new PersonInterOp("Sam", 34);                    // D

        // 4 — companion access
        System.out.println(PersonInterOp.BUILD);                // E
        System.out.println(PersonInterOp.TAG);                  // F
        //System.out.println(PersonInterOp.API_VERSION);          // G
        System.out.println(PersonInterOp.getAPI_VERSION());     // H
        System.out.println(PersonInterOp.staticRole());         // I
        //System.out.println(PersonInterOp.role);                 // J

        // 5 — checked exception visibility
        LibKt.loadConfig("");                             // K

        // 6 — trying to call Kotlin-only API
       // System.out.println(LibKt.secretForKotlinOnly()); // L
    }
}
