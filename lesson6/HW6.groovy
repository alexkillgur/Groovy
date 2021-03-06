/**
 * Created by Killgur on 22.11.2015.
 * ������ ���� :)
 */
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

// �������� �������� �� ������
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

    def propertyMissing( String name ) {
        storage << name
//        println this
//        println this.class.name
        return this
    }

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
        b.Hello.my.name.is.John
        b
        // �� � ��� ��������
//        b.with {
//            Hello
//            my
//            name
//            is
//            John
//        }
//        b
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
// �������� �������� �� ������������
//-----------------------------------------------------------
@TypeChecked
class TypeCheck {
    String stringDate = new Date() // Date.toString()
    Boolean boxed = 'some string' // �� ������ ������, Groovy Truth
    boolean prim = 'some string' // �� ������ ������, Groovy Truth
    Class clazz = 'java.lang.String' // �������������� ���������� � ������
    String someString = null // �� ����������� ���
//    int iNull = null // ����������� ���, fail
    int[] intArray = new int[4] // ������ ���� int
//    int[] intArrayWithString = new String[4] // ������ ��������� ������� ���� int �������� ���� String, fail
    int[] intList = [1,2,3] // ������ ���� int
//    int[] intListWithDate = [1,2, new Date()] // ������ ��������� �������� ������ ���� int �������� ���� String, fail
    AbstractList abstractList = new ArrayList() // AbstractList -> ���������� ��� ArrayList

    @TypeChecked( TypeCheckingMode.SKIP )
    void excludeLinkedList() {
        LinkedList linkedList = new ArrayList() // LinkedList �� �������� ������������ ��� ArrayList, fail without @TypeChecked( TypeCheckingMode.SKIP )
        linkedList << [ 1, [ name: 'killgur' ], new Date(), [ 'Croovy', 2 ] ]
        println """LinkedList linkedList = new ArrayList()
        \r�� �������� ��� ���������� ��� ��������� @TypeChecked � �������� � �������� - $linkedList"""
//        int iNull = null // � ������ ������ ��������� � ��������
//        int[] intArrayWithString = new String[4] // � ������ ������ ��������� � ��������
//        int[] intListWithDate = [1,2, new Date()] // � ������ ������ ��������� � ��������
//        RandomAccess randomAccess = new LinkedList() // � ������ ������ ��������� � ��������
    }

    List list = new ArrayList() // ArrayList ��������� ��������� List
//    RandomAccess randomAccess = new LinkedList() // LinkedList �� ��������� ��������� RandomAccess, fail
}

TypeCheck typeCheck = new TypeCheck()
typeCheck.excludeLinkedList()

// ��� ��� �������
int iZero = 0
Integer bi = 1
int x = new Integer(123)
double d = new Float(5f)

// SAM-type
Runnable r = { println "\n������ �� SAM-type" }
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

    void sayHello( def string ) {
        println "Hello from $string"
    }

    @TypeChecked( TypeCheckingMode.SKIP )
    void someMethod() {
        someUntypedField = 'Some Method'
        someUntypedField = someUntypedField.toUpperCase()
        sayHello( someUntypedField )
    }

    void someSafeMethod() {
        someTypedField = 'Some Safe Method'
        someTypedField = someTypedField.toUpperCase()
        sayHello( someTypedField )
    }

    void someMethodUsingLocalVariable() {
        def localVariable = 'Some Method Using Local Variable'
        someUntypedField = localVariable.toUpperCase()
        sayHello( someUntypedField )
    }
}

SomeClass someClass = new SomeClass()
someClass.someMethod()
someClass.someSafeMethod()
someClass.someMethodUsingLocalVariable()
//-----------------------------------------------------------

//-----------------------------------------------------------
// Ducks :)
// ������ ����� ��� �������� ���������� ������������ ����
//-----------------------------------------------------------
class CreateDynamicDucks {
    // ������ ��������
    def listOfQuacks = [ 'Mu-u-u!', "I'm a crazy dack O_o!" ]
    // ����� �������� �� ��������� ������ � ������
    def mapOfQuacks = [
            quack: "Fear of the duck, fear of the duck - i have a constant fear that some duck's always near!",
            someFields: "This is not a quack! You should't see this!"
    ]
    // ����� �������� �� ��������� ������ � ������
    def mapOfQuacksWitnList = [
            quack: [ 'C �o��� �pe��� �a�a���o� �py����� �e �a���� �o�a���o ��c����� �������y� c�oco�e� ���op�po�a�� �e��e���� �apa�o�ca����x ��o���.',
                     '���������������� ������������ ��������� ��������������� � ����������� ������� ������������-������������ ��������� �������������.' ],
            someOtherFields: "This is not a quack! You should't see this!"
    ]

    // ������� �� ���������
    def closureQuack = { miltiply, ... quacks ->
        def result = quacks.join(' ').toUpperCase()*miltiply
        result.trim()
    }

    // ���������� ������� ������������ ���� Lizzy
    def dynamicDuckLizzy = new DynamicDuckBuilder().build {
        name 'Lizzy'
        isFlying true
        quacking {
            quack 'Quack!'
            quack 'Bla-Bla-Bla!'
            shout this.listOfQuacks
        }
    }

    // ���������� ������� ������������ ���� Dizzy
    def dynamicDuckDizzy = new DynamicDuckBuilder().build {
        name 'Dizzy'
        isFlying true
        quacking {
            sing this.mapOfQuacks
            sing this.closureQuack( 2, 'o!', 'yeah! ' )
        }
    }

    // ���������� ������� ������������ ���� Shizzy
    def dynamicDuckShizzy = new DynamicDuckBuilder().build {
        name 'Shizzy'
        isFlying false
        quacking {
            say this.mapOfQuacksWitnList
            say "������� � ��� ���������� �_�?"
        }
    }

    // ���������� ������� ������������ ���� Grizly
    def dynamicDuckGrizly = new DynamicDuckBuilder().build {
        name 'Grizly'
        isFlying false
        quacking {
            hi '��-��!'
        }
    }

    // ���������� ������� ������������ ���� DeadDuck
    def dynamicDuckDeadDuck = new DynamicDuckBuilder().build {
        name 'DeadDuck'
        isFlying false
        quacking {
            hi 'La-la-la!'
        }
    }
}

println """\n-------------------------------------------------------------------
\r------------------------ ������ ���� :) ---------------------------
\r-------------------------------------------------------------------\n"""

//-----------------------------------------------------------
// ������� ������������
//-----------------------------------------------------------
def createDynamicDucks = new CreateDynamicDucks()
createDynamicDucks.dynamicDuckLizzy.quacks()
println()
createDynamicDucks.dynamicDuckDizzy.sings()
println()
createDynamicDucks.dynamicDuckShizzy.speaks()
println()
createDynamicDucks.dynamicDuckGrizly.quacks()
println()
createDynamicDucks.dynamicDuckDeadDuck.laughs()
println()

//-----------------------------------------------------------
// ������ �����������
//-----------------------------------------------------------
List< String > listOfQuacks = [ "����� �� ������� ��� ������ '�������'!", "������ ����� �� ������!" ]
LinkedHashMap< String, String > mapOfQuacks = [
        quack: "New duck joins this earth, and quickly it's subdued!",
        someFields: "This is not a quack! You should't see this!"
]
LinkedHashMap< String, Object > mapOfQuacksWitnList = [
        quack: [ """\n� ����� �p���� ��������� �p������, ������ �p��������� ���p����� �p���������� ����p��p����� ������
                    \rp����������� ����������� �������� ����� ����p���� ����p����p����� � ����������� ������������ ��p������
                    \r��������������-��������������� ���� �p� ������� �������p��-�p���������� ����p���������� ��p��� �
                    \r�����p����� �����p���������� �p���p������, ������ �p� ��p��������� ������p�������� ������� �����p��p��������
                    \r��������, �����p��� p��������� � �������������� ����p���������� ��p���������, ����p�p���p�����
                    \r���p�������p������� ��������� H��-���p����, ��������� ����������� ������������� ���������� ���p�� ������������,
                    \r� p��������� ���� ���� �p����� �� �������� ���������: ��������� �� ������ �����p�������, �� � ����������������
                    \r����p��������p������� ���p������ ������������ ���������p������� ����������p������� �����������, ��������
                    \r�p������������ ��������p�����, �� ��� ��� ������������� �����p ��p��������, �� �, ��������������,
                    \r����������������� ����p������� ���p���p��� � �������������� ���p�������, ���������, �������� � �p�����p������
                    \r���������, �p��������� ������ �������, �������������� ��������� ���p��������� �������p������, ����� ����p�����p�����
                    \r����� �p����� �����p���� � �����p�������� � ����� ���p��������, ������ �������, ��� � p��������� ����p��������,
                    \r��p��������� ���������� ���������� ����p�������� ��p���, ��� ������ �����p���������� ��������� �p����� �p���������
                    \r�p���������� �p�����p����� ���������������.\n""",
                 '\n��� ��� ����� ������� �_�?' ],
        someOtherFields: "This is not a quack! You should't see this!"
]

def closureQuack = { int miltiply, String... quacks ->
    String result = quacks.join(' ').toUpperCase()*miltiply
    result.trim()
}

StaticDuckBuilder staticDuckBuilder = new StaticDuckBuilder()
StaticDuck Lizzy = new StaticDuck()
StaticDuck Dizzy = new StaticDuck()
StaticDuck Shizzy = new StaticDuck()
StaticDuck Grizly = new StaticDuck()
StaticDuck DeadDuck = new StaticDuck()

Map configLizzy = [
        name: 'Lizzy',
        isFlying: true,
        quacking: {
            quack 'Quack!'
            quack 'Bla-Bla-Bla!'
            shout listOfQuacks
        }
]

Map configDizzy = [
        name: 'Dizzy',
        isFlying: true,
        quacking: {
            sing mapOfQuacks
            sing closureQuack( 2, 'yeah!', 'ugu! ' )
        }
]

Map configShizzy = [
        name: 'Shizzy',
        isFlying: false,
        quacking: {
            say mapOfQuacksWitnList
        }
]

Map configGrizly = [
        name: 'Grizly',
        isFlying: false,
        quacking: {
            hi '��-��!'
        }
]

Map configDeadDuck = [
        name: 'DeadDuck',
        isFlying: false,
        quacking: {
            hi '��-��!'
        }
]

staticDuckBuilder.build( Lizzy, configLizzy )
staticDuckBuilder.build( Dizzy, configDizzy )
staticDuckBuilder.build( Shizzy, configShizzy )
staticDuckBuilder.build( Grizly, configGrizly )
staticDuckBuilder.build( DeadDuck, configDeadDuck )

Lizzy.quacks()
println()
Dizzy.sings()
println()
Shizzy.speaks()
println()
Grizly.quacks()
println()
DeadDuck.laughs()
