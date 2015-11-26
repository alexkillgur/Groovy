/**
 * Created by Killgur on 22.11.2015.
 * Летять утки :)
 */
import groovy.transform.TypeChecked
import groovy.transform.TypeCheckingMode
import groovy.transform.CompileStatic
import groovy.transform.TupleConstructor

// проверка примеров из лекции
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
        // Но и так работает
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
        \rНе выпадает при компиляции без аннотации @TypeChecked и работает в рантайме - $linkedList"""
//        int iNull = null // В данном случае отвалится в рантайме
//        int[] intArrayWithString = new String[4] // В данном случае отвалится в рантайме
//        int[] intListWithDate = [1,2, new Date()] // В данном случае отвалится в рантайме
//        RandomAccess randomAccess = new LinkedList() // В данном случае отвалится в рантайме
    }

    List list = new ArrayList() // ArrayList реализует интерфейс List
//    RandomAccess randomAccess = new LinkedList() // LinkedList не реализует интерфейс RandomAccess, fail
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
// Просто класс для создания енскольких динамических уток
//-----------------------------------------------------------
class CreateDynamicDucks {
    // Список кряканья
    def listOfQuacks = [ 'Mu-u-u!', "I'm a crazy dack O_o!" ]
    // Карта кряканья со значением кряков в строке
    def mapOfQuacks = [
            quack: "Fear of the duck, fear of the duck - i have a constant fear that some duck's always near!",
            someFields: "This is not a quack! You should't see this!"
    ]
    // Карта кряканья со значением кряков в списке
    def mapOfQuacksWitnList = [
            quack: [ 'C тoчки зpeния бaнaльнoй эpyдиции нe кaждый лoкaльнo мыcлящий индивидyм cпocoбeн игнopиpoвaть тeндeнции пapaдoкcaльныx эмoций.',
                     'Синусоидальность дидукционнго индуктора некоэмутируется с хромофорной эфузией аксирогентно-адиквантного фотонного триангулятора.' ],
            someOtherFields: "This is not a quack! You should't see this!"
    ]

    // Крякаем по замыканию
    def closureQuack = { miltiply, ... quacks ->
        def result = quacks.join(' ').toUpperCase()*miltiply
        result.trim()
    }

    // Собственно создаем динамическую утку Lizzy
    def dynamicDuckLizzy = new DynamicDuckBuilder().build {
        name 'Lizzy'
        isFlying true
        quacking {
            quack 'Quack!'
            quack 'Bla-Bla-Bla!'
            shout this.listOfQuacks
        }
    }

    // Собственно создаем динамическую утку Dizzy
    def dynamicDuckDizzy = new DynamicDuckBuilder().build {
        name 'Dizzy'
        isFlying true
        quacking {
            sing this.mapOfQuacks
            sing this.closureQuack( 2, 'o!', 'yeah! ' )
        }
    }

    // Собственно создаем динамическую утку Shizzy
    def dynamicDuckShizzy = new DynamicDuckBuilder().build {
        name 'Shizzy'
        isFlying false
        quacking {
            say this.mapOfQuacksWitnList
            say "Неужели я это выговорила О_О?"
        }
    }

    // Собственно создаем динамическую утку Grizly
    def dynamicDuckGrizly = new DynamicDuckBuilder().build {
        name 'Grizly'
        isFlying false
        quacking {
            hi 'Ку-ку!'
        }
    }

    // Собственно создаем динамическую утку DeadDuck
    def dynamicDuckDeadDuck = new DynamicDuckBuilder().build {
        name 'DeadDuck'
        isFlying false
        quacking {
            hi 'La-la-la!'
        }
    }
}

println """\n-------------------------------------------------------------------
\r------------------------ ЛЕТЯТЬ УТКИ :) ---------------------------
\r-------------------------------------------------------------------\n"""

//-----------------------------------------------------------
// Сначала динамические
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
// Теперь статические
//-----------------------------------------------------------
List< String > listOfQuacks = [ "Врагу не сдается наш гордый 'МакКряк'!", "Пощады никто не желает!" ]
LinkedHashMap< String, String > mapOfQuacks = [
        quack: "New duck joins this earth, and quickly it's subdued!",
        someFields: "This is not a quack! You should't see this!"
]
LinkedHashMap< String, Object > mapOfQuacksWitnList = [
        quack: [ """\nС точки зpения банальной эpудиции, каждый пpоизвольно выбpанный пpедикативно абсоpбиpующий обьект
                    \rpациональной мистической индукции можно дискpетно детеpминиpовать с аппликацией ситуационной паpадигмы
                    \rкоммуникативно-функционального типа пpи наличии детектоpно-аpхаического дистpибутивного обpаза в
                    \rГилбеpтовом конвеpгенционном пpостpанстве, однако пpи паpаллельном колабоpационном анализе спектpогpафичеких
                    \rмножеств, изомоpфно pелятивных к мультиполосным гипеpболическим паpаболоидам, интеpпpетиpующим
                    \rантpопоцентpический многочлен Hео-Лагpанжа, возникает позиционный сигнификатизм гентильной теоpии психоанализа,
                    \rв pезультате чего надо пpинять во внимание следующее: поскольку не только эзотеpический, но и экзистенциальный
                    \rаппеpцепциониpованный энтpополог антецедентно пассивизиpованный высокоматеpиальной субстанцией, обладает
                    \rпpизматической идиосинхpацией, но так как валентностный фактоp отpицателен, то и, соответственно,
                    \rантагонистический дискpедитизм дегpадиpует в эксгибиционном напpавлении, поскольку, находясь в пpепубеpтатном
                    \rсостоянии, пpактически каждый субьект, меланхолически осознавая эмбpиональную клаустоpофобию, может экстpаполиpовать
                    \rлюбой пpоцесс интегpации и диффеpенциации в обоих напpавлениях, отсюда следует, что в pезультате синхpонизации,
                    \rогpаниченной минимально допустимой интеpполяцией обpаза, все методы конвеpгенционной концепции тpебуют пpактически
                    \rтpадиционных тpансфоpмаций неоколониализма.\n""",
                 '\nКто это сейчс говорил О_о?' ],
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
            hi 'Ку-ку!'
        }
]

Map configDeadDuck = [
        name: 'DeadDuck',
        isFlying: false,
        quacking: {
            hi 'Ку-ку!'
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
