package Car

/**
 * Created by Killgur on 10.11.2015.
 * ����� ����� ��� ������������ �����������
 */

abstract class CarBuilder {
    //���������������� ����
    File configurationFile

    //������ ������������ �� �����
    abstract makeCar()
    //��������� �����
    abstract makeWheel( Car.SideType sideType, Car.PlacementType placementType )
    //��������� ������
    abstract makeDoor( Car.SideType sideType, Car.PlacementType placementType )
    //�������
    abstract makeHatchback( Car.PlacementType placementType )
    //��������� ���������
    abstract makeEngine( Car.PlacementType placementType )
    //��������
    abstract makePaint( List colors )

    //���������� ������ ����� �������� ����������
    def getStringProperty = { File file, String property ->
        String value
        file.eachLine { line ->
            if ( line =~ /^${property}/ )
                value = line - /${property}:/
        }
        return value.trim()
    }

    //���������� ������������� ��������
    def getIntProperty = { String property ->
        return property as Integer
    }

    //���������� ������������� ��������
    def getEnumProperty = { String property, enumType ->
        return Enum.valueOf( enumType, property )
    }

    //���������� ������ ����������
    def getListProperty = { String property ->
        return property.tokenize("\n;, ")
    }

    //���������� ���������� ����� << ������
    def getIntFromStringProperty = getIntProperty << getStringProperty
    //���������� ���������� ������ >> ������
    def getListFromStringProperty = getStringProperty >> getListProperty

    //��������� ����������
    def installPart = { String part, List partsList, Car.SideType sideType, Car.PlacementType placementType ->
        sideType == Car.SideType.NONE ? ( partsList += "${placementType}_${part}" ) : ( partsList += "${sideType}_${placementType}_${part}" )
    }

    //��������� �����
    def installDoor = installPart.curry( 'DOOR' )
    //������ �������
    def installBackDoor = installDoor.ncurry( 1, Car.SideType.NONE ) //Zero-based index
    //��������� ���������
    def installEngine = installPart.curry( 'ENGINE' ).ncurry( 1, Car.SideType.NONE ) //Zero-based index
    //��������� ������
    def installWheel = installPart.curry( 'WHEEL' )

//    //�������� ��������
//    def paintMap = { List paintList ->
//        Map paintMap = [:]
//        paintList.each { it -> paintMap << [ ( it ):'' ] }
//        return paintMap
//    }

    //������ �������� ������ �������� ����� ������, �� ��������� ���� ����������
    def paintPart = { String colorType, Car.PaintType paintType = Car.PaintType.ALL ->
        return [( paintType ):colorType]
    }
    //������ �����
    def paintBody = paintPart.rcurry( Car.PaintType.BODY )
    //������ �����
    def paintRoof = paintPart.rcurry( Car.PaintType.ROOF )
    //��������
    def paintPrimer = paintPart.curry( Car.ColorType.DEAD_COLOR.toString() ).rcurry( Car.PaintType.PRIMER )

    //�������� ����� �� ��� ������ - ��� ������� ��������� ������ �������� � ������� trampoline(), ������ ������ � mrhaki
    def clampingBolts = { List listOfParts, int numBolts ->
        def bolts
        bolts = { list, counter = 0 ->
            if ( list.size() == 0 ) {
                return counter*numBolts
            } else {
                bolts.trampoline( list.tail(), counter + 1 )
            }
        }.trampoline()
        numBolts = bolts( listOfParts )
        return listOfParts << "BOLTS CLAMPING: $numBolts"
    }

//    //������ ��� ������
//    def paint = { Closure paintSomething, Map paintMap ->
//        return paintMap.add( paintSomething )
//    }

    //������ ���� ���������� �������� ���-�� ������ �� ������� ���������� ���������
    def paintAll = { List colorList, Map colorMap ->
        colorMap << paintPrimer()
        switch ( colorList.size() ) {
            case 1:
                colorMap << paintPart( colorList[0] )
//                paint( paintPart( colorList[0] ), colorMap )
                break
            case 2:
                colorMap << paintBody( colorList[0] )
                colorMap << paintRoof( colorList[1] )
//                paint( paintBody( colorList[0] ), colorMap )
//                paint( paintRoof( colorList[1] ), colorMap )
                break
        }
        return colorMap
    }

    //���������� ��������� ����������
    Car getCar() {
        return finalCar
    }
}