/**
 * Created by Killgur on 30.11.2015.
 * Some usage of XML Builders, Parsers and Templates
 */
import groovy.xml.MarkupBuilder
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.text.SimpleTemplateEngine

// класс для описания особы
class Person {
    String firstName
    String lastName
    Date dateOfBirth
    String dateFormat = 'dd.MM.yyyy'
    Map address
    List phones
    Map curses
}

// Класс для построения XML из экземпляров класса Person
def xmlPersonBuilder( MarkupBuilder builder, Person person ) {
    builder.person( firstName: person.firstName, lastName: person.lastName, dateOfBirth: person.dateOfBirth.format( person.dateFormat ) ) {
        address {
            postalCode( person.address.postalCode )
            country( person.address.country )
            city( person.address.city )
            street( person.address.street )
            house( person.address.house )
            flat( person.address.flat )
            phones {
                person.phones.each { 'phone'( it ) }
            }
        }
        curses {
            person.curses.each { curse( name: it.key, it.value ) }
        }
    }
}

Person person01 = new Person(
        firstName: 'Aleksey',
        lastName: 'Shashlyuk',
        dateOfBirth: new Date().parse( 'dd/MM/yyyy', '09/04/1981' ),
        address: [
                postalCode: 18030,
                country: 'Ukraine',
                city: 'Cherkassy',
                street: 'Ryabokonya',
                house: 31,
                flat: 41,
        ],
        phones: [ '+38(0472) 73-32-00', '+38(093) 406-98-60' ],
        curses: [ 'Groovy': true,
                  'Jawa for Web': true,
                  'Jawa Script': false
        ]
)

Person person02 = new Person(
        firstName: "Vas'ka",
        lastName: 'Kurolesov',
        dateOfBirth: new Date().parse( 'dd/MM/yyyy', '01/01/1991' ),
        address: [
                postalCode: 18000,
                country: 'Ukraine',
                city: 'Lvov',
                street: "Banderivs'ka",
                house: 1,
                flat: 1,
        ],
        phones: [ '+38(0322) 11-22-33', '+38(097) 444-44-44' ],
        curses: [ 'Groovy': true,
                  'Jawa for Web': false,
                  'Jawa Script': true,
                  'Ruby on Rails': true,
        ]
)

Person person03 = new Person(
        firstName: "Arnold",
        lastName: 'Schwarzenegger',
        dateOfBirth: new Date().parse( 'dd/MM/yyyy', '30/07/1947' ),
        address: [
                postalCode: 18000,
                country: 'USA',
                city: 'LA',
                street: "Terminator-str.",
                house: 1,
                flat: 1,
        ],
        phones: [ '+1(209) 111-11-11', '+1(818) 111-11-11' ],
        curses: [ 'Ruby on Rails': false,
                  'Advansed PHP': false,
                  'Jawa Script': false,
                  'Barbell Bench Press': true,
                  'Destroing All the Enimies': true
        ]
)

def baseDir = '.'
def xmlPersonsFile = new File( baseDir, 'persons.xml' )

def writerForPersons = new FileWriter( xmlPersonsFile )
def xmlBuilderForPersons = new MarkupBuilder( writerForPersons )

// Строитм XML файл
xmlBuilderForPersons.persons() {
    xmlPersonBuilder( xmlBuilderForPersons, person01 )
    xmlPersonBuilder( xmlBuilderForPersons, person02 )
    xmlPersonBuilder( xmlBuilderForPersons, person03 )
}

// Чавкаем наш файл :)
def listOfPersons = new XmlSlurper().parse( xmlPersonsFile )
assert listOfPersons instanceof groovy.util.slurpersupport.GPathResult

def person = listOfPersons.person[0]
assert person.@firstName == 'Aleksey'
assert person.address.phones.phone[1] == '+38(093) 406-98-60'
assert person.address.postalCode == '18030'
assert person.curses.curse[0].@name == 'Groovy'
assert person.curses.curse[0] == true

// Разные поиски
def result = listOfPersons.'*'.findAll { node ->
    node.name() == 'person' && node.@firstName == 'Aleksey'
}

assert result.size() == 1
assert result.address.postalCode == '18030'
assert result.curses.curse[0].@name == 'Groovy'
assert result.curses.curse[0] == true

result = listOfPersons.'*'.findAll { node ->
    node.@lastName == 'Shashlyuk'
}

assert result.size() == 1

result = listOfPersons.person.'*'.findAll { adr ->
    adr.postalCode == '18030'
}

assert result.size() == 1
assert result.postalCode == '18030'

result = listOfPersons.'**'.findAll { adr ->
    adr.country == 'Ukraine'
}

assert result.size() == 2
assert result[1].postalCode == '18000'

result = listOfPersons.'**'.findAll { node ->
    node.name() == 'curse' && node.@name == 'Jawa for Web'
}

assert result.size() == 2
assert result[0] == true

result = listOfPersons.'**'.findAll { curse ->
    curse.@name == 'Jawa for Web'
}

assert result.size() == 2
assert result[1] == false

result = listOfPersons.person.curses.'*'.findAll { curse ->
    curse.@name == 'Groovy'
}

assert result.size() == 2
assert result[0] == true
assert result[1] == true

// Строим JSON
def json = JsonOutput.toJson( person01 )
//println JsonOutput.prettyPrint( json )

def jsonSlurper = new JsonSlurper()
def objectPerson = jsonSlurper.parseText( json )

assert objectPerson.firstName == 'Aleksey'
assert objectPerson.phones[1] == '+38(093) 406-98-60'
assert objectPerson.curses['Groovy'] == true
assert objectPerson.curses['Jawa Script'] == false

//json = JsonOutput.toJson( person02 )
//println JsonOutput.prettyPrint( json )
//json = JsonOutput.toJson( person03 )
//println JsonOutput.prettyPrint( json )

//def person = listOfPersons.person[0]
assert person.@firstName == 'Aleksey'
assert person.address.phones.phone[1] == '+38(093) 406-98-60'
assert person.address.postalCode == '18030'
assert person.curses.curse[0].@name == 'Groovy'
assert person.curses.curse[0] == true

def engine = new SimpleTemplateEngine()
def tplInviteFile = new File( baseDir, 'invite.tpl' )

listOfPersons.person.each { it ->
    def cursesMap = [:]

    it.curses.curse.each { curse ->
        cursesMap.put( curse.@name, curse )
    }

    def phones = [:]

    phones.put( 'tel', it.address.phones.phone[0] )
    phones.put( 'mobile', it.address.phones.phone[1] )

    def binding = [
            postalCode: it.address.postalCode,
            country: it.address.country,
            city: it.address.city,
            street: it.address.street,
            house: it.address.house,
            flat: it.address.flat,
            phones: phones,
            firstName: it.@firstName,
            lastName: it.@lastName,
            curses: cursesMap,
            date: new Date().parse( 'dd/MM/yyyy', '21/12/2015' ).format( 'dd.MM.yyyy' )
    ]

    def template = engine.createTemplate( tplInviteFile ).make( binding )
    println template.toString()
    println '--------------------------------------------------------------------'
}
