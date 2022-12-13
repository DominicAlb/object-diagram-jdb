package test_environment;

public class Car {

    public int amountDoors;
    public int getAmountDoors() {
        return this.amountDoors;
    }


    public void setamountDoors(int amountDoors) {
        this.amountDoors = amountDoors;
    }


    public Human human1;
    public Human getHuman1() {
        return this.human1;
    }


    public void setHuman1(Human human1) {
        this.human1 = human1;
    }


    public Car damagedCar;
    public Car getDamagedCar() {
        return this.damagedCar;
    }


    public void setDamagedCar(Car damagedCar) {
        this.damagedCar = damagedCar;
    }


    public Car[] seesCars;
    public Car[] getSeesCars() {
        return this.seesCars;
    }


    public void setSeesCars(Car[] seesCars) {
        this.seesCars = seesCars;
    }


    public boolean damaged;

    public boolean isDamaged() {
        return this.damaged;
    }


    public void setdDamaged(boolean damaged) {
        this.damaged = damaged;
    }


    Car(int amountDoors, Human human1, Car damagedCar, Car[] seesCars, boolean damaged) {
        this.amountDoors = amountDoors;
        this.human1 = human1;
        this.damagedCar = damagedCar;
        this.seesCars = seesCars;
        this.damaged = damaged;
    }


    public Car[] getCarsInAccident() {
        Car[] arr = {this, this.damagedCar};
        return arr;
    }

    

    
}
