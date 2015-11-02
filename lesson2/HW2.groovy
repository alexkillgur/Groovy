/**
 * Created by Killgur on 24.10.2015.
 */
//import groovy.json.JsonOutput //Для сравнения результатов

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

def resultList

def printMapJson (list, isNotEmpty) {
        list.eachWithIndex { it, i ->
                print "Авто ${ ++i } - "
                MapToJson ( it, isNotEmpty )
                print '\n'
        }
}

// Создаем множество годов выпуска
yearsRange.each { i -> years << i }

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
                body: [
                        type: 'Speedster',
                        doors: 2,
                        isPrototype: true
                ],
                volume: volumes.find { it == 2500 },
                owners: [ 'Alex', 'Jim' ],
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
        body: [
                type: 'Sedan',
                doors: 2,
                isPrototype: false
        ],
        volume: volumes.find { it == 1600 },
        owners: [ 'Jane', 'Bob' ],
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

//Секция для проверки результатов парсера в формат JSON (раскомментировать 1-ю строчку)
/*
def stringJson = JsonOutput.toJson ( carsList[1] )
println "$stringJson"
MapToJson (carsList[1], true)
println '\n'

stringJson = JsonOutput.toJson ( carsList[4] )
println "$stringJson"
MapToJson (carsList[4], true)
println '\n'
*/

//Вывод в формате JSON
println "Все авто спсиком:"
ListToJson ( carsList, carsList ? true : false )

println "\n\nВсе авто построчно:"
printMapJson ( carsList, carsList ? true : false )

println "\nПервое авто в списке белого цвета:"
resultList = carsList.find { it.color == 'white' }
if (resultList) {
        print 'Авто 1 - '
        MapToJson ( resultList, true )
        print '\n'
}

println "\nВсе авто дороже \$10.000:"
resultList = carsList.findAll { it.cost > 10000 }
printMapJson ( resultList, resultList ? true : false )

println "\nВсе модели BMW:"
resultList = carsList.findAll { it.vendor == 'BMW' }
printMapJson ( resultList, resultList ? true : false )

println "\nВсе авто до 1970 г.в.:"
resultList = carsList.findAll { it.year != null && it.year < 1970 }
printMapJson ( resultList, resultList ? true : false )

println "\nВсе модели BMW до 1970 г.в.:"
resultList = carsList.findAll { it.vendor == 'BMW' && it.year < 1970 }
printMapJson ( resultList, resultList ? true : false )

println "\nВсе модели BMW с объемом двигателя более 1600 см3:"
resultList = carsList.collect { it.vendor == 'BMW' && it.volume > 1600 ? it : null }
def i = 0
resultList.each { it ->
        if (it) {
                print "Авто ${ ++i } - "
                MapToJson ( it, true )
                print '\n'
        }
}

println "\nВсе модели BMW с типом кузова \"Седан\":"
resultList = carsList.findAll { it ->
        it.vendor == 'BMW' }.collect { it ->
        it?.body?.type == 'Sedan' ? it : null
}
i = 0
resultList.each { it ->
        if (it) {
                print "Авто ${ ++i } - "
                MapToJson ( it, true )
                print '\n'
        }
}

//Секция для проверки результатов парсера в формат JSON (раскомментировать 1-ю строчку)
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