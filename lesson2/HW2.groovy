/**
 * Created by Killgur on 24.10.2015.
 */
//import groovy.json.JsonOutput //��� ��������� �����������

def vendors = ['BMW', 'Audi', 'Mercedes', 'Volkswagen', 'Bugatti', 'Lamborgini', 'Renault', 'Pegeout', 'Citroen', '���', '���'] // �������������
def colors = ['black', 'white', 'red', 'blue', 'green', 'yellow', 'grey', 'beige', 'cherry'] as Set // �����
def years = [] as Set // ���� �������
def volumes = [] as Set // ����� ���������

def myCar = [:] // ������ �������� ����
def carsList = [] // ������ ����

def yearsRange = 1905..2015 // ���������� �������� ����� �������
def volumesRange = 1000..4500 // ���������� �������� ������ ���������

//����� ��� �������� ������� ���� ArrayList � ������ JSON
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

//����� ��� �������� ������� ���� LinkedHashMap � ������ JSON
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
                print "���� ${ ++i } - "
                MapToJson ( it, isNotEmpty )
                print '\n'
        }
}

// ������� ��������� ����� �������
yearsRange.each { i -> years << i }

// ������� ��������� ������� ��������� � ����� � 100 ��3
volumesRange.each { i ->
        if (i % 100 == 0) { volumes << i }
}

// ������� ������ ����
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
                vendor: vendors.find { it == '���' },
                model: '966',
                year: years.find { it == 1967 },
                color: colors.find { it == 'grey' },
                volume: volumes.find { it == 1000 },
                cost: 100
        ]
]

myCar = [
        vendor: vendors.find { it == '���' },
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

//������ ��� �������� ����������� ������� � ������ JSON (����������������� 1-� �������)
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

//����� � ������� JSON
println "��� ���� �������:"
ListToJson ( carsList, carsList ? true : false )

println "\n\n��� ���� ���������:"
printMapJson ( carsList, carsList ? true : false )

println "\n������ ���� � ������ ������ �����:"
resultList = carsList.find { it.color == 'white' }
if (resultList) {
        print '���� 1 - '
        MapToJson ( resultList, true )
        print '\n'
}

println "\n��� ���� ������ \$10.000:"
resultList = carsList.findAll { it.cost > 10000 }
printMapJson ( resultList, resultList ? true : false )

println "\n��� ������ BMW:"
resultList = carsList.findAll { it.vendor == 'BMW' }
printMapJson ( resultList, resultList ? true : false )

println "\n��� ���� �� 1970 �.�.:"
resultList = carsList.findAll { it.year != null && it.year < 1970 }
printMapJson ( resultList, resultList ? true : false )

println "\n��� ������ BMW �� 1970 �.�.:"
resultList = carsList.findAll { it.vendor == 'BMW' && it.year < 1970 }
printMapJson ( resultList, resultList ? true : false )

println "\n��� ������ BMW � ������� ��������� ����� 1600 ��3:"
resultList = carsList.collect { it.vendor == 'BMW' && it.volume > 1600 ? it : null }
def i = 0
resultList.each { it ->
        if (it) {
                print "���� ${ ++i } - "
                MapToJson ( it, true )
                print '\n'
        }
}

println "\n��� ������ BMW � ����� ������ \"�����\":"
resultList = carsList.findAll { it ->
        it.vendor == 'BMW' }.collect { it ->
        it?.body?.type == 'Sedan' ? it : null
}
i = 0
resultList.each { it ->
        if (it) {
                print "���� ${ ++i } - "
                MapToJson ( it, true )
                print '\n'
        }
}

//������ ��� �������� ����������� ������� � ������ JSON (����������������� 1-� �������)
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