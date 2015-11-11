package Transporter
import Car.*

/**
 * Created by Killgur on 10.11.2015.
 * ����� ���������� ��������� ��� ���� ������
 */

class Transporter {
    //���� �������� ��� ������ ���� ������
    File operationsFile
    //������������, ����������� �� ����� � ��������������� ����������� ���������������� ������
    Map mapConfiguration

    //�������� ��������� ����������
    Car makePassengerCar( PassengerCarBuilder builder ) {
        builder.makeCar()

        mapConfiguration = builder.car.configuration

        operationsFile.eachLine { line ->
            switch ( line ) {
            //�����
                case ( 'DOORS' ):
                    builder.&makeDoor( Car.SideType.LEFT, Car.PlacementType.FRONT )
                    builder.&makeDoor( Car.SideType.RIGHT, Car.PlacementType.FRONT )

                    if ( mapConfiguration.doors.doorsNum > 2 && mapConfiguration.doors.doorsNum != 3 ) {
                        builder.&makeDoor( Car.SideType.LEFT, Car.PlacementType.BACK )
                        builder.&makeDoor( Car.SideType.RIGHT, Car.PlacementType.BACK )
                    }

                    //�������
                    if ( mapConfiguration.doors.doorsNum == 3 || mapConfiguration.doors.doorsNum == 5 ) {
                        builder.&makeHatchback( Car.PlacementType.BACK )
                    }

                    builder.&clampingBolts( builder.car.doorsInstalled, mapConfiguration.doors.numBolts )
                    break
            //������
                case ( 'WHEELS' ):
//                    builder.&makeWheel( Car.SideType.LEFT, Car.PlacementType.FRONT )
//                    builder.&makeWheel( Car.SideType.RIGHT, Car.PlacementType.FRONT )
//                    builder.&makeWheel( Car.SideType.LEFT, Car.PlacementType.BACK )
//                    builder.&makeWheel( Car.SideType.RIGHT, Car.PlacementType.BACK )
                    builder.makeWheels()
                    break
            //���������
                case ( 'ENGINE' ):
                    builder.&makeEngine( mapConfiguration.engine.placement )
                    break
            //��������
                case ( 'COLOR' ):
                    builder.&makePaint( mapConfiguration.colors )
                    break
            }
        }
        //���������� ����������
        return builder.car
    }
    //����� ����� ���� ������ ��� �������� ���� ������
}
