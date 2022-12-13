package Test;

public class Auto {

    public int anzTueren;
    public int getAnzTueren() {
        return anzTueren;
    }


    public void setAnzTueren(int anzTüren) {
        this.anzTueren = anzTüren;
    }


    public Mensch mensch1;
    public Mensch getMensch1() {
        return mensch1;
    }


    public void setMensch1(Mensch mensch1) {
        this.mensch1 = mensch1;
    }


    public Auto beschAuto;
    public Auto getBeschAuto() {
        return beschAuto;
    }


    public void setBeschAuto(Auto beschAuto) {
        this.beschAuto = beschAuto;
    }


    public Auto[] siehtAutos;
    public Auto[] getSiehtAutos() {
        return siehtAutos;
    }


    public void setSiehtAutos(Auto[] siehtAutos) {
        this.siehtAutos = siehtAutos;
    }


    public boolean beschaedigt;

    public boolean isBeschädigt() {
        return beschaedigt;
    }


    public void setBeschädigt(boolean beschädigt) {
        this.beschaedigt = beschädigt;
    }


    Auto(int anzTueren, Mensch mensch1, Auto beschAuto, Auto[] siehtAutos, boolean beschaedigt) {
        this.anzTueren = anzTueren;
        this.mensch1 = mensch1;
        this.beschAuto = beschAuto;
        this.siehtAutos = siehtAutos;
        this.beschaedigt = beschaedigt;
    }


    public Auto[] getAutosImUnfall() {
        Auto[] arr = {this, this.beschAuto};
        return arr;
    }

    

    
}
