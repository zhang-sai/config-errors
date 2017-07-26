package test;

public class Test {
  public static void main(String[] args) {
    Element l = new Element();
    Client a = new Client(l);
    a.start();
    Client b = new Client(l);
    b.start();
  }
}

class Client extends Thread {
  Element e;
  Client(Element l){
    this.e = l;
  }
  public void run() {
    int count = 0;
    while(count < 100) {
      count++;
      //e.set(1);
    }
    e.set(1);
  }
}

class Element {
  int f;
  Element() {
    f = 0;
  }
  public void set(int i) {
    this.f = i;
  }
}
