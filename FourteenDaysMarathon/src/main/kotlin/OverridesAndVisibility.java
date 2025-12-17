public class OverridesAndVisibility {

    public static void main() {
        System.out.println(X.C);
        System.out.println(X.SF);
        X.Companion.foo();
        X.bar();
        Api api = new Api();
        api.visibleToAll();
     /*   api.getA();
        api.setA();
        api.setA(11);*/

        F f = new F();
        f.foo(10);
    }
}
