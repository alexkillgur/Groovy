/**
 * Created by Killgur on 22.11.2015.
 */
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

// Difference between @TypeChecked and @CompileStatic
//-----------------------------------------------------------
@TypeChecked
class Computer {
    int compute( String str ) {
        str.length()
    }
    String compute( int x ) {
        String.valueOf( x )
    }
}

@TypeChecked
void testTC() {
    def computer = new Computer()
    computer.with {
        assert compute( compute( 'foobar' ) ) =='6'
    }
}

@CompileStatic
void testCS() {
    def computer = new Computer()
    computer.with {
        assert compute( compute( 'foobar' ) ) =='6'
    }
}

testTC() // Pass
Computer.metaClass.compute = { String str -> new Date() } // Monkey patching
//testTC() // Fail
testCS() // Pass
//-----------------------------------------------------------

// Optional typing
//-----------------------------------------------------------
String aString = 'foo'
assert aString.toUpperCase()

def bString = 'foo'
assert bString.toUpperCase()

private concat( a, b ) {
    a+b
}

assert concat( 'foo', 'bar' ) == 'foobar'
assert concat( 1, 2 ) == 3
//-----------------------------------------------------------

// TypeChecked annotation
//-----------------------------------------------------------
class SentenceBuilder {
    def storage = []

    def propertyMissing( String name ) { storage << name }

    String toString() {
        storage.join(' ')
    }
}

@TypeChecked
class GreetingService {
    String greeting() {
        doGreet()
    }

    @TypeChecked( TypeCheckingMode.SKIP )
    private String doGreet() {
        def b = new SentenceBuilder()
        b.with {
            Hello
            my
            name
            is
            John
        }
        b
    }
}

def s = new GreetingService()
assert s.greeting() == 'Hello my name is John'
//-----------------------------------------------------------

// Dynamic Groovy
//-----------------------------------------------------------
@TupleConstructor
class Person {
    String firstName
    String lastName
}

Person.metaClass.getFormattedName = { "$delegate.firstName $delegate.lastName" }

def p = new Person( firstName: 'Raymond', lastName: 'Devos' )
assert p.formattedName == 'Raymond Devos'
//-----------------------------------------------------------

// Type checking assignments with Groovy Truth
// Проверка примеров из документации
//-----------------------------------------------------------
@TypeChecked
class TypeCheck {
    String stringDate = new Date() // Date.toString()
    Boolean boxed = 'some string' // Не пустая строка, Groovy Truth
    boolean prim = 'some string' // Не пустая строка, Groovy Truth
    Class clazz = 'java.lang.String' // Принудительное приведение к классу
    String someString = null // Не примитивный тип
//    int iNull = null // Примитивный тип, fail
    int[] intArray = new int[4] // Массив типа int
//    int[] intArrayWithString = new String[4] // Нельзя присвоить массиву типа int значения типа String, fail
    int[] intList = [1,2,3] // Список типа int
//    int[] intListWithDate = [1,2, new Date()] // Нельзя присвоить элементу списка типа int значение типа String, fail
    AbstractList abstractList = new ArrayList() // AbstractList -> суперкласс для ArrayList

    @TypeChecked( TypeCheckingMode.SKIP )
    void excludeLinkedList() {
        LinkedList linkedList = new ArrayList() // LinkedList не является суперклассом для ArrayList, fail without @TypeChecked( TypeCheckingMode.SKIP )
        linkedList << [ 1, [ name: 'killgur' ], new Date(), [ 'Croovy', 2 ] ]
        println """LinkedList linkedList = new ArrayList()
        \rВ отличие от остальных примеров не выпадает при компиляции без аанотации @TypeChecked и работает в рантайме - $linkedList"""
//        int iNull = null // В данном случае отвалится в рантайме
//        int[] intArrayWithString = new String[4] // В данном случае отвалится в рантайме
//        int[] intListWithDate = [1,2, new Date()] // В данном случае отвалится в рантайме
    }

    List list = new ArrayList() // ArrayList реализует интерфейс List
    //RandomAccess randomAccess = new LinkedList() // LinkedList не реализует интерфейс RandomAccess, fail
}

TypeCheck typeCheck = new TypeCheck()
typeCheck.excludeLinkedList()

// Тут все понятно
int iZero = 0
Integer bi = 1
int x = new Integer(123)
double d = new Float(5f)

// SAM-type
Runnable r = { println "\nПривет из SAM-type" }
r()

interface SAMType {
    int doSomething()
}

SAMType sam = { 123 }
assert sam.doSomething() == 123

abstract class AbstractSAM {
    int calc() { 2* value() }
    abstract int value()
}

AbstractSAM c = { 123 }
assert c.calc() == 246

// Use of @TupleConstructor, list and map constructor
Person tupleConstructor = new Person('Ada','Lovelace')
assert tupleConstructor.formattedName == 'Ada Lovelace'

Person listConstructor = ['Ada','Lovelace']
assert listConstructor.formattedName == 'Ada Lovelace'

Person mapConstructor = [firstName:'Ada', lastName:'Lovelace']
assert mapConstructor.formattedName == 'Ada Lovelace'

// Method resolution
class Duck {
    void quack() {
        println 'Quack!'
    }
}

class QuackingBird {
    void quack() {
        println '\nQuack-Quack!'
    }
}

@TypeChecked
void accept( Duck quacker ) {
    quacker.quack()
}

accept( new Duck() )
//accept( new QuackingBird() )

// Type inference
def message = 'Welcome to Groovy!'
assert message.toUpperCase() == 'WELCOME TO GROOVY!'
//println message.upper() // compile time error

// Variables vs fields in type inference
@TypeChecked
class SomeClass {
    def someUntypedField
    String someTypedField

    @TypeChecked( TypeCheckingMode.SKIP )
    void someMethod() {
        someUntypedField = '123'
        someUntypedField = someUntypedField.toUpperCase()
    }

    void someSafeMethod() {
        someTypedField = '123'
        someTypedField = someTypedField.toUpperCase()
    }

    void someMethodUsingLocalVariable() {
        def localVariable = '123'
        someUntypedField = localVariable.toUpperCase()
    }
}
//-----------------------------------------------------------