package test_environment;

public class Human {

    public boolean alive;
    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;             
    }

    public boolean injured;
    public boolean isInjured() {
        return this.injured;
    }

    public void setInjured(boolean injured) {
        this.injured = injured;
    }

    public Car inCar;
    
    public Car getInCar() {
        return inCar;
    }

    public void setInCar(Car inCar) {
        this.inCar = inCar;
    }

    Human(boolean alive, boolean injured, Car inCar)  {
        this.alive = alive;
        this.injured = injured;
        this.inCar = inCar;
    }

}
