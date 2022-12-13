package Test;

public class Mensch {

    public boolean lebendig;
    public boolean isLebendig() {
        return lebendig;
    }

    public void setLebendig(boolean lebendig) {
        this.lebendig = lebendig;
    }

    public boolean verletzt;
    public boolean isVerletzt() {
        return verletzt;
    }

    public void setVerletzt(boolean verletzt) {
        this.verletzt = verletzt;
    }

    public Auto inAuto;
    
    public Auto getInAuto() {
        return inAuto;
    }

    public void setInAuto(Auto inAuto) {
        this.inAuto = inAuto;
    }

    Mensch(boolean lebendig, boolean verletzt, Auto inAuto)  {
        this.lebendig = lebendig;
        this.verletzt = verletzt;
        this.inAuto = inAuto;
    }

    public boolean hatVerletzungen() {
        return this.verletzt;
    } 
}
