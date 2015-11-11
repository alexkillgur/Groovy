import Car.*
import Transporter.*

/**
 * Created by Killgur on 10.11.2015.
 * Данная домашняя работа является своего рода реализацией паттерна Builder, найденного на http://codelab.ru/pattern/builder/?print=1
 *
 * Пакет Сar содержит все классы, необходимые для создания легкового автомобиля, пакет Transporter содержит непосредственно реализацию конвейера
 *
 * Для чтения конфигураций автомобилей используются файлы вида car[Model].txt, для чтения кофигурации сборки - файл carOperations.txt
 *
 * Имеется возможность создать любой пакет с необходимым набором классов для запуска в производство любого продукта из созданных
 * для данного продукта файлов кофигурации путем добавления в класс Transporter соответствующего метода
 */

//Задаем базовую дирректорию для поиска конфигураций
//def baseDir = 'D:/Projects/Groovy/lesson4'
def baseDir = '.'

//Создаем конвейер
Transporter transporter = new Transporter()

//Загружаем конфигурацию сборки автомобилей в конвейер
transporter.operationsFile = new File( baseDir, 'carOperations.txt' )

//Список изготовленных автомобилей
Car[] carsList = []

//Запускаем в производство все автомобили
new File( baseDir ).eachFileMatch( ~/^car.*\.txt$/ ) { file ->
    if ( !( file.name =~ /.*Operations\.txt$/ ) ) {
        CarBuilder carBuilder = new PassengerCarBuilder()
        carBuilder.configurationFile = file
        Car car = transporter.makePassengerCar( carBuilder )
        carsList +=car
    }
}

//Печатает список сошедших с конвейера автомобилей
def printList = { list ->
    list.eachWithIndex { it, i ->
        println "Авто ${++i} - $it\n"
    }
}

println "--------------ВCE СОШЕДШИЕ С КОНВЕЙЕРА АВТОМОБИЛИ В ПОРЯДКЕ ВЫПУСКА:--------------\n"
printList( carsList )
println "----------------------------------------------------------------------------------\n"

println "--------------ВCE СОШЕДШИЕ С КОНВЕЙЕРА АВТОМОБИЛИ, ОТСОРТИРОВАННЫЕ ПО МАКСИМАЛЬНОЙ СКОРОСТИ:--------------\n"
printList( carsList.sort { a, b -> a.configuration.maxSpeed <=> b.configuration.maxSpeed } )
println "----------------------------------------------------------------------------------------------------------\n"

//Фильтрует список выпущенных автомобилей по заданному условию
def filterCars = { Closure filter, Car[] list -> list.findAll( filter ) }

//Только BMW
def filterBmw = { Car it -> it.configuration.model =~ /^BMW.*/ }
def onlyBmw = filterCars.curry( filterBmw )
//def filterCarsList = onlyBmw( carsList )
def filterCarsList = carsList.grep( onlyBmw )

println "--------------ТОЛЬКО ВЫПУЩЕННЫЕ BMW:--------------\n"
printList( filterCarsList )
println "--------------------------------------------------\n"

//Автомобили с установленным сзади двигателем
def filterEngineBack = { Car it -> it.engineInstalled[0] == 'BACK_ENGINE' }
def engineBack = filterCars.curry( filterEngineBack )
//filterCarsList = engineBack( carsList )
filterCarsList = carsList.grep( engineBack )

println "--------------ВЫПУЩЕННЫЕ АВТОМОБИЛИ С УСТАНОВЛЕННЫМ СЗАДИ ДВИГАТЕЛЕМ:--------------\n"
printList( filterCarsList )
println "-----------------------------------------------------------------------------------\n"

//Автомобили, выкрашенные полностью в белый цвет
def filterWhiteColor = { Car it -> it.colorsPaint[ ( Car.PaintType.ALL ) ] == Car.ColorType.WHITE.toString() }
def whiteColor = filterCars.curry( filterWhiteColor )
//filterCarsList = whiteColor( carsList ).sort { a, b -> a.configuration.maxSpeed <=> b.configuration.maxSpeed }
filterCarsList = carsList.grep( whiteColor ).sort { a, b -> a.configuration.maxSpeed <=> b.configuration.maxSpeed }

println "--------------ВЫПУЩЕННЫЕ АВТОМОБИЛИ ПОЛНОСТЬЮ БЕЛОГО ЦВЕТА, ОТСОРТИРОВАННЫЕ ПО МАКСИМАЛЬНОЙ СКОРОСТИ:--------------\n"
printList( filterCarsList )
println "-------------------------------------------------------------------------------------------------------------------\n"