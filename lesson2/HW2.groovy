/**
 * Created by Killgur on 24.10.2015.
 */
def vendors = ['BMW', 'Audi', 'Mercedes', 'Volkswagen', 'Bugatti', 'Lamborgini', 'Renault', 'Pegeout', 'Citroen', 'ВАЗ', 'ЗАЗ'] // Производитель
def colors = ['black', 'white', 'red', 'blue', 'green', 'yellow', 'grey', 'beige', 'cherry'] as Set // Цвета
def years = [] as Set // Года выпуска
def volumes = [] as Set // Объем двигателя

def myCar = [:] // Шаблон описания авто
def carsList = [] // Список авто

def yearsRange = 1905..2015 // Допустимые значения годов выпуска
def volumesRange = 1000..4500 // Допустимые значения объема двигателя

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
def newLCarsList = carsList.findAll { it.vendor == 'BMW'}.collect { it.volume > 1600 ? it : null }

newLCarsList.eachWithIndex { it, i ->
    if (!it) newLCarsList.remove(i)
}

println newLCarsList