import Car.*
import Transporter.*

/**
 * Created by Killgur on 10.11.2015.
 * ������ �������� ������ �������� ������ ���� ����������� �������� Builder, ���������� �� http://codelab.ru/pattern/builder/?print=1
 *
 * ����� �ar �������� ��� ������, ����������� ��� �������� ��������� ����������, ����� Transporter �������� ��������������� ���������� ���������
 *
 * ��� ������ ������������ ����������� ������������ ����� ���� car[Model].txt, ��� ������ ����������� ������ - ���� carOperations.txt
 *
 * ������� ����������� ������� ����� ����� � ����������� ������� ������� ��� ������� � ������������ ������ �������� �� ���������
 * ��� ������� �������� ������ ����������� ����� ���������� � ����� Transporter ���������������� ������
 */

//������ ������� ����������� ��� ������ ������������
//def baseDir = 'D:/Projects/Groovy/lesson4'
def baseDir = '.'

//������� ��������
Transporter transporter = new Transporter()

//��������� ������������ ������ ����������� � ��������
transporter.operationsFile = new File( baseDir, 'carOperations.txt' )

//������ ������������� �����������
Car[] carsList = []

//��������� � ������������ ��� ����������
new File( baseDir ).eachFileMatch( ~/^car.*\.txt$/ ) { file ->
    if ( !( file.name =~ /.*Operations\.txt$/ ) ) {
        CarBuilder carBuilder = new PassengerCarBuilder()
        carBuilder.configurationFile = file
        Car car = transporter.makePassengerCar( carBuilder )
        carsList +=car
    }
}

//�������� ������ �������� � ��������� �����������
def printList = { list ->
    list.eachWithIndex { it, i ->
        println "���� ${++i} - $it\n"
    }
}

println "--------------�CE �������� � ��������� ���������� � ������� �������:--------------\n"
printList( carsList )
println "----------------------------------------------------------------------------------\n"

println "--------------�CE �������� � ��������� ����������, ��������������� �� ������������ ��������:--------------\n"
printList( carsList.sort { a, b -> a.configuration.maxSpeed <=> b.configuration.maxSpeed } )
println "----------------------------------------------------------------------------------------------------------\n"

//��������� ������ ���������� ����������� �� ��������� �������
def filterCars = { Closure filter, Car[] list -> list.findAll( filter ) }

//������ BMW
def filterBmw = { Car it -> it.configuration.model =~ /^BMW.*/ }
def onlyBmw = filterCars.curry( filterBmw )
//def filterCarsList = onlyBmw( carsList )
def filterCarsList = carsList.grep( onlyBmw )

println "--------------������ ���������� BMW:--------------\n"
printList( filterCarsList )
println "--------------------------------------------------\n"

//���������� � ������������� ����� ����������
def filterEngineBack = { Car it -> it.engineInstalled[0] == 'BACK_ENGINE' }
def engineBack = filterCars.curry( filterEngineBack )
//filterCarsList = engineBack( carsList )
filterCarsList = carsList.grep( engineBack )

println "--------------���������� ���������� � ������������� ����� ����������:--------------\n"
printList( filterCarsList )
println "-----------------------------------------------------------------------------------\n"

//����������, ����������� ��������� � ����� ����
def filterWhiteColor = { Car it -> it.colorsPaint[ ( Car.PaintType.ALL ) ] == Car.ColorType.WHITE.toString() }
def whiteColor = filterCars.curry( filterWhiteColor )
//filterCarsList = whiteColor( carsList ).sort { a, b -> a.configuration.maxSpeed <=> b.configuration.maxSpeed }
filterCarsList = carsList.grep( whiteColor ).sort { a, b -> a.configuration.maxSpeed <=> b.configuration.maxSpeed }

println "--------------���������� ���������� ��������� ������ �����, ��������������� �� ������������ ��������:--------------\n"
printList( filterCarsList )
println "-------------------------------------------------------------------------------------------------------------------\n"