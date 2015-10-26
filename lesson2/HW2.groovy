/**
 * Created by Killgur on 24.10.2015.
 */
import groovy.json.JsonOutput //Для сравнения результатов

def vendors = ['BMW', 'Audi', 'Mercedes', 'Volkswagen', 'Bugatti', 'Lamborgini', 'Renault', 'Pegeout', 'Citroen', 'ВАЗ', 'ЗАЗ'] // Производитель
def colors = ['black', 'white', 'red', 'blue', 'green', 'yellow', 'grey', 'beige', 'cherry'] as Set // Цвета
def years = [] as Set // Года выпуска
def volumes = [] as Set // Объем двигателя

def myCar = [:] // Шаблон описания авто
def carsList = [] // Список авто

def yearsRange = 1905..2015 // Допустимые значения годов выпуска
def volumesRange = 1000..4500 // Допустимые значения объема двигателя

//Метод для перевода объекта типа ArrayList в формат JSON
def ListToJson ( list, isNext ) {
        def lastIndex = list.size() - 1
        print "["
        list.eachWithIndex { it, i ->
                if ( i == lastIndex ) isNext = false else isNext = true
                switch ( it ) {
                        case LinkedHashMap:
                                MapToJson ( it, isNext )
                                break

                        case ArrayList:
                                ListToJson ( it, isNext )
                                break

                        case String:
                                print "\"$it\""
                                break

                        default:
                                print "$it"
                                break
                }
                if ( isNext ) print ","
        }
        print "]"
}

//Метод для перевода объекта типа LinkedHashMap в формат JSON
def MapToJson ( map, isNext ) {
        def lastIndex = map.size() - 1
        print "{"
        map.eachWithIndex { key, value, i ->
                if ( i == lastIndex ) isNext = false else isNext = true
                switch ( value ) {
                        case LinkedHashMap:
                                print "\"$key\":"
                                MapToJson ( value, isNext )
                                break

                        case ArrayList:
                                print "\"$key\":"
                                ListToJson ( value, isNext )
                                break

                        case String:
                                print "\"$key\":\"$value\""
                                break

                        default:
                                print "\"$key\":$value"
                                break
                }
                if ( isNext ) print ","
        }
        print "}"
}

yearsRange.each { i -> years << i } // Создаем множество годов выпуска

// Создаем множество объемов двигателя с шагом в 100 см3
volumesRange.each { i ->
        if (i % 100 == 0) { volumes << i }
}

// Создаем список авто
carsList = [
        [
                vendor: vendors.find { it == 'BMW' },
                model: 'X6',
                year: years.find { it == 2010 },
                color: colors.find { it == 'white' },
                volume: volumes.find { it == 3500 },
                cost: 65000
        ],
        [
                vendor: vendors.find { it == 'Audi' },
                model: 'T4',
                year: years.find { it == 2001 },
                color: colors.find { it == 'red' },
                volume: volumes.find { it == 2500 },
                cost: 40000
        ],
        [
                vendor: vendors.find { it == 'ЗАЗ' },
                model: '966',
                year: years.find { it == 1967 },
                color: colors.find { it == 'grey' },
                volume: volumes.find { it == 1000 },
                cost: 100
        ]
]

myCar = [
        vendor: vendors.find { it == 'ВАЗ' },
        model: '2105',
        year: years.find { it == 1984 },
        color: colors.find { it == 'white' },
        volume: volumes.find { it == 1200 },
        cost: 1500
]
carsList << myCar

myCar = [
        vendor: vendors.find { it == 'BMW' },
        model: '315',
        year: years.find { it == 1983 },
        color: colors.find { it == 'beige' },
        volume: volumes.find { it == 1600 },
        cost: 2500
]
carsList.add myCar

myCar = [
        vendor: vendors.find { it == 'BMW' },
        model: '507',
        year: years.find { it == 1957 },
        color: colors.find { it == 'white' },
        volume: volumes.find { it == 2000 },
        cost: 250000
]
carsList += myCar

println "Все авто:\n${ carsList }\n"
println "Первое авто в списке белого цвета:\n${ carsList.find { it.color == 'white' } }\n"
println "Все авто дороже \$100.000:\n${ carsList.findAll { it.cost > 100000 } }\n"
println "Все модели BMW:\n${ carsList.findAll { it.vendor == 'BMW' } }\n"
println "Все авто до 1970 г.в.:\n${ carsList.findAll { it.year != null && it.year < 1970 } }\n"
println "Все модели BMW до 1970 г.в.:\n${ carsList.findAll { it.vendor == 'BMW' && it.year < 1970 } }\n"

//Костыль на метод collect - не понял пока до конца
println 'Все модели BMW с объемом двигателя более 1600 см3:'
//def newCarsList = carsList.findAll { it.vendor == 'BMW'}.collect { it.volume > 1600 ? it : null }
def newCarsList = carsList.collect { it.vendor == 'BMW' && it.volume > 1600 ? it : null }

newCarsList.each { it ->
        if (it) { print "${it} " }
}

//Для сравнения результатов
/*
def json = JsonOutput.toJson ( carsList[0] )
println "\n\n${json}"

def sizeOfTheList = carsList.size()
json = MapToJson ( carsList[0], sizeOfTheList ? true : false )

def person = [
               firstName: 'Guillame',
               lastName: 'Laforge',
               address: [
                           city: 'Paris',
                           country: 'France',
                           zip: 12345,
                        ],
               married: true,
               conferences: [ 'JavaOne', 'Gr8conf', 5 ]
             ]

def list = ['groovy', 'java', [1, 'name', 2, [3, 'for']], [one : 1, two : 'two', three : [0, 3], for : [k : 'k', z : 33]], -25e10]

def stringJson = JsonOutput.toJson ( list )
println "\n\n$stringJson"

sizeOfTheList = list.size()
ListToJson ( list, sizeOfTheList ? true : false )
println "\n"

stringJson = JsonOutput.toJson ( person )
println "$stringJson"

def sizeOfTheMap = person.size()
MapToJson ( person, sizeOfTheMap ? true : false )
*/