package Car
import groovy.transform.*

/**
 * Created by Killgur on 10.11.2015.
 * ����� ��� �������� �����������
 */

//@Canonical
//@ToString ( includeNames = true, includeFields = true)
@ToString ( includeNames = true)
class Car {
    //��� ������� (�����, ������)
    enum SideType {
        LEFT, RIGHT, NONE
    }

    //��� ���������� (�����, �����)
    enum PlacementType {
        FRONT, BACK
    }

    //�����
    enum ColorType {
        RED, BLUE, WHITE, METALLIC, BEIGE, BLACK, DEAD_COLOR
    }

    //���� ��������
    enum PaintType {
        PRIMER, ALL, ROOF, BODY
    }

    //��� ���������
    enum EngineType {
        INJECTOR, MONOINJECTOR, CARBURETTOR, DIESEL
    }

//    public Map configuration
    Map configuration
    List doorsInstalled //������ ������������� ������
    List wheelsInstalled //������ ������������� �����
    List engineInstalled //������ �������������� ���������
    Map colorsPaint //����� ��������
}
