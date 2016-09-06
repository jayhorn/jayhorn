import java.util.ArrayList;

class UnsatArrayList01 {
  public static void main(String[] args)  { 
    ArrayList<Object> al = new ArrayList<Object>();
    al.add(new Object());
    al.add(new Object());
    assert al.size()==0;
  }
}
