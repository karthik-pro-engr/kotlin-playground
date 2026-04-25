import com.interop.*;
import com.interop.User;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

public class JavaCaller {
    public void caller() {
        InterOp interOp = new InterOp();
        interOp.defaultParams("Karthik", 33, "Male");
        interOp.overloadDefaultParams("Kumar", 34);
        interOp.overloadDefaultParams("Karthik", 34, "Male");
        Utils.greet();
        PersonWithDefaultParams karthik = new PersonWithDefaultParams("Karthik", 34);
        PersonWithJvmOverloadsConstructor kumar = new PersonWithJvmOverloadsConstructor("Kumar");
        PersonWithJvmOverloadsConstructor dup = new PersonWithJvmOverloadsConstructor("Kumar", 33);
        System.out.println(PersonWithJvmOverloadsConstructor.staticHello());
        System.out.println(PersonWithJvmOverloadsConstructor.Companion.nonStaticHello());
        System.out.println(PersonWithJvmOverloadsConstructor.Companion.staticHello());
        System.out.println(PersonWithJvmOverloadsConstructor.Companion.getVAL());
        System.out.println(PersonWithJvmOverloadsConstructor.JVM_FIELD_VAL);
        System.out.println(PersonWithJvmOverloadsConstructor.Companion.getPROP());
        System.out.println(PersonWithJvmOverloadsConstructor.getStaticVAL());

        System.out.println(PersonWithJvmOverloadsConstructor.Companion.getPROP_WITHOUT_GETTER());
        System.out.println(PersonWithJvmOverloadsConstructor.Companion.getPROP());


        System.out.println(Utils.kotlinNullable());
        System.out.println(Utils.kotlinNonNullable());

        //Drill_1
        System.out.println(Drill_1.stat());
        System.out.println(Drill_1.Companion.stat());
        System.out.println(Drill_1.Companion.normal());
        // System.out.println(Drill_1.normal()); can't

        // Drill_2
        System.out.println(Drill_2.F);
        System.out.println(Drill_2.Companion.getG());
        System.out.println(Drill_2.Companion.getS());
        System.out.println(Drill_2.getS());

        // Drill_3
        System.out.println(Drill_3.getV());
        System.out.println(Drill_3.Companion.getV());
        // System.out.println(Drill_3.V); can't

        // System.out.println(Drill_2.getG()); can't
        // System.out.println(Drill_2.getF); can't
        // System.out.println(Drill_1.normal()); can't


        try {
            Utils.mayThrow(true);
        } catch (IOException e) {
            System.out.println(e.getMessage());
//            e.printStackTrace();
        }
        Utils.mayNotThrow(false);
//        Utils.mayNotThrow(true);

        LibKt.greetInterview();

        System.out.println("User Interview Style");
        System.out.println("-----------------------------------");
        User user = new User("30");
        User.Companion.role();
        User.Companion.getDefaultRole();
        System.out.println(User.VERSION);
        System.out.println(User.getAPI());
        System.out.println(User.staticRole());

        LibKt.transformSums(10, 20);
        LibKt.transformSums(30, 40);
        LibKt.transformTimes(30);

        LibKt.transform(20,30);
        LibKt.transform(40,25);
        String risky = LibKt.risky();
        System.out.println(Objects.requireNonNullElse(risky, "Println null"));
    }

    public @Nullable String getMayBeValue() {
        return "Empty";
    }

    public String maybeNull(boolean returnNull) {
        return returnNull ? null : "Okay";
    }

}
