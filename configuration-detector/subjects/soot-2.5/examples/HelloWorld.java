import java.io.*;
// UI.java
interface UI {
  public void display(String msg);
}

// HelloWorld.java
public class HelloWorld {
  public static void main(String[] arg) {
    UI ui = new TextUI();
    ui.display("Hello World");
  }
}

// TextUI.java
class TextUI implements UI {
  public void display(String msg) {
      System.out.println(msg);
  }
}
