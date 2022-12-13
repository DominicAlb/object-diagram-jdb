package test_environment;
@SuppressWarnings("unused")
public class Test {
    
    public static void main(String[] args) {
        Mensch mensch1 = new Mensch(false, true, null);
        Mensch mensch2 = new Mensch(true, false, null);

        Auto autoA = new Auto(4, null, null, null, false);
        Auto autoB = new Auto(2, null, null, null, false);
        Auto autoC = new Auto(6, null, null, null, false);

        mensch1.inAuto = autoC;
        mensch2.inAuto = autoB;

        autoA.beschAuto = autoB;
        autoA.mensch1 = mensch2;
        autoA.siehtAutos = new Auto[] {autoB};
        autoA.beschaedigt = true;

        int i = 10;

        autoB.mensch1 = mensch1;
        autoB.siehtAutos = new Auto[] {autoA};
        autoB.beschaedigt = true;

        autoC.siehtAutos = new Auto[] {autoA, autoB};
        autoC.beschaedigt = false;

        double d = 10.2;

    }

}

