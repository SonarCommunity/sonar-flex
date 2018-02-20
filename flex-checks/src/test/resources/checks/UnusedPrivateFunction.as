public class A
{
  private function unused1() {}      // Noncompliant
  private function unused2() {}      // Noncompliant
}

public class B
{
  public function B(a:boolean)
  {
    if (a) {
      trace(used1().b);
    }
  }

  private function used1():B {}      // OK
  private function unused2() {}      // Noncompliant

  class Inner {
    private function unsedInner1(){}  // Noncompliant
  }
}

public class C
{
  var a = used2();
  public function C(a:boolean) {
    this.used1()
  }

  private function get a() {};       // OK (unused private accessor is not supported)
  private function set a() {};       // OK (unused private accessor is not supported)
  private function used1():int {}    // OK
  private function used2() {}        // OK
}

public class Utils {
  private function Utils() {}        // OK
}

public class D {
  private function D() { // Noncompliant
    trace("bla");
  }
}
