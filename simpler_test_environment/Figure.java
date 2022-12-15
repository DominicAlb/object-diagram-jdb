@SuppressWarnings("unused")
class Figure
{
  int age;
  Figure loves;
  Figure knows;

  int foo(int pi, float pf)
  {
    
    int foo_i;
    boolean b;
    double d;
    float foo_f;
    foo_f = pf * (float)pi;
    return((int)foo_f);
  }  

  public static void main(String[] args)
  {
    Figure obj1 = new Figure();
    int i = 1;
    double a = 2.0;
    float f = 1.0f;
    String s= "Hi";
    i = obj1.foo(i,1.1f);
    Figure obj2 = new Figure();
    // here is a comment
    s = "Germany";
    i = 2;
    obj1.age = 42;
    obj1.age = i;
    obj1.loves = obj2;
    obj1.knows = obj2;
  }

}

class Gigure
{

  int gAge;
  Gigure gLoves;

  void bar()  {
    int bar_i = 3;
    int bar_ii = 5;
    int sum = bar_i + bar_ii;
    System.out.println("Summe:"+sum);
  }

}

