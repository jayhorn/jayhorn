import java.util.ArrayList;

class SatArrayList02 {
  public static void main(String[] args)  { 
    ArrayList<Object> al = new ArrayList<Object>();
    al.add(new Object());
    al.add(new Object());
    assert al.size()>=0 && al.size() <=2;
  }
}
