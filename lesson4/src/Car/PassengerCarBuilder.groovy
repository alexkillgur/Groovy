package Car

/**
 * Created by Killgur on 10.11.2015.
 * ����� ��� �������� ���������� ������ ��������� ���������� �� ������ ����������������� �����
 */

class PassengerCarBuilder extends CarBuilder {
    Car finalCar = null

    //������ ������������ ����������
    def makeCar() {
        finalCar = new Car()
        finalCar.with {
            configuration = [
                    model: getStringProperty( configurationFile, 'MODEL' ),
                    maxSpeed: getIntFromStringProperty( configurationFile, 'MAX_SPEED' ),
                    doors: [
                            doorsNum: getIntProperty( getListFromStringProperty( configurationFile, 'DOORS' )[0] ),
                            numBolts: getIntProperty( getListFromStringProperty( configurationFile, 'DOORS' )[1] )
                    ],
                    wheels: [
                            num: getIntProperty( getListFromStringProperty( configurationFile, 'WHEELS' )[0] ),
                            diameter: getListFromStringProperty( configurationFile, 'WHEELS' )[1],
                            numBolts: getIntProperty( getListFromStringProperty( configurationFile, 'WHEELS' )[2] )
                    ],
                    engine: [
                            type: getEnumProperty( getListFromStringProperty( configurationFile, 'ENGINE' )[0], Car.EngineType.class ),
                            numCylinders: getIntProperty( getListFromStringProperty( configurationFile, 'ENGINE' )[1] ),
                            volume: getIntProperty( getListFromStringProperty( configurationFile, 'ENGINE' )[2] ),
                            placement: getEnumProperty( getListFromStringProperty( configurationFile, 'ENGINE' )[3], Car.PlacementType.class ),
                            numBolts: getIntProperty( getListFromStringProperty( configurationFile, 'ENGINE' )[4] )
                    ],
                    colors: getListFromStringProperty( configurationFile, 'COLOR' ).each { color ->
                        getEnumProperty( color, Car.ColorType.class )
                    }
            ]
            wheelsInstalled = []
            doorsInstalled = []
            engineInstalled = []
            colorsPaint = [:]
        }
    }

    //��������� ����� �����
    def makeWheel( Car.SideType sideType, Car.PlacementType placementType ) {
        finalCar.wheelsInstalled = installWheel( finalCar.wheelsInstalled, sideType, placementType )
    }

    //��������� ����� ���������
    def makeWheels = {
        Car.PlacementType.each { placementType ->
            Car.SideType.each { sideType ->
                if ( sideType != Car.SideType.NONE ) {
                    finalCar.wheelsInstalled = installWheel( finalCar.wheelsInstalled, sideType, placementType )
                }
            }
        }
        clampingBolts( finalCar.wheelsInstalled, finalCar.configuration.wheels.numBolts )
    }

    //��������� ������
    def makeDoor( Car.SideType sideType, Car.PlacementType placementType ) {
        finalCar.doorsInstalled = installDoor( finalCar.doorsInstalled, sideType, placementType )
    }

    //������ �������
    def makeHatchback( Car.PlacementType placementType ) {
        finalCar.doorsInstalled = installBackDoor( finalCar.doorsInstalled, placementType )
    }

    //��������� ���������
    def makeEngine( Car.PlacementType placementType ) {
        finalCar.engineInstalled = installEngine( finalCar.engineInstalled, placementType )
        clampingBolts( finalCar.engineInstalled, finalCar.configuration.engine.numBolts )
    }

    //��������
    def makePaint( List colors ) {
        finalCar.colorsPaint = paintAll( colors, finalCar.colorsPaint )
    }
}
