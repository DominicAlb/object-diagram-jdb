package test_environment;
@SuppressWarnings("unused")
public class Test {
    
    public static void main(String[] args) {
        Human human1 = new Human(false, true, null);
        Human human2 = new Human(true, false, null);

        Car carA = new Car(4, null, null, null, false);
        Car carB = new Car(2, null, null, null, false);
        Car carC = new Car(6, null, null, null, false);

        human1.inCar = carC;
        human2.inCar = carB;

        carA.damagedCar = carB;
        carA.human1 = human2;
        carA.seesCars = new Car[] {carB};
        carA.damaged = true;

        int i = 10;

        carB.human1 = human1;
        carB.seesCars = new Car[] {carA};
        carB.damaged = true;

        carC.seesCars = new Car[] {carA, carB};
        carC.damaged = false;

        double d = 10.2;

    }

}

