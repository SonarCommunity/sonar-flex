switch(i) {
  case 1:
    doSomething(1);
    break;
  case 2:
    doSomething(2);
    break;
  case 3:                 // Noncompliant {{Either merge this case with the identical one on line "5" or change one of the implementations.}}
    doSomething(2);
    break;
  case 4:
  case 5:
    break;
  default:
}

switch(i) {
    case 1:
        doSomething(1);
        break;
    case 2:
    case 3:
        break;
    default:              // Noncompliant
        doSomething(1);
        break;
}

switch(i) {
    case 1:
        doSomething(1);
        break;
    case 2:
    case 3:
        break;
    case 1:               // Noncompliant
        doSomething(1);
        break;
}

switch(i) {
    case 1:               // OK
    case 2:
    case 3:
      doSomething(1);
      break;
}
