class bar {       // OK
  function a(){}
  private function b(){}
}

class foo {       // Noncompliant {{Class "foo" has 19 methods, which is greater than 1 authorized. Split it into smaller classes.}}
  public function a(){}
  public function b(){}
  public function c(){}
  public function d(){}
  public function e(){}
  public function f(){}
  public function g(){}
  public function h(){}
  public function i(){}
  public function j(){}
  public function k(){}
  public function l(){}
  public function m(){}
  public function n(){}
  public function o(){}
  public function p(){}
  public function q(){}
  public function r(){}
  public function s(){}
  private function t(){}
  internal function u(){}
}
