/**
 * Created by Killgur on 24.10.2015.
 */
def vendors = ['BMW', 'Audi', 'Mercedes', 'Volkswagen', 'Bugatti', 'Lamborgini', 'Renault', 'Pegeout', 'Citroen', '���', '���'] // �������������
def colors = ['black', 'white', 'red', 'blue', 'green', 'yellow', 'grey', 'beige', 'cherry'] as Set // �����
def years = [] as Set // ���� �������
def volumes = [] as Set // ����� ���������

def myCar = [:] // ������ �������� ����
def carsList = [] // ������ ����

def yearsRange = 1905..2015 // ���������� �������� ����� �������
def volumesRange = 1000..4500 // ���������� �������� ������ ���������

yearsRange.each { i -> years << i } // ������� ��������� ����� �������

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
                volume: volumes.find { it == 2500 },
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

println "��� ����:\n${ carsList }\n"
println "������ ���� � ������ ������ �����:\n${ carsList.find { it.color == 'white' } }\n"
println "��� ���� ������ \$100.000:\n${ carsList.findAll { it.cost > 100000 } }\n"
println "��� ������ BMW:\n${ carsList.findAll { it.vendor == 'BMW' } }\n"
println "��� ���� �� 1970 �.�.:\n${ carsList.findAll { it.year != null && it.year < 1970 } }\n"
println "��� ������ BMW �� 1970 �.�.:\n${ carsList.findAll { it.vendor == 'BMW' && it.year < 1970 } }\n"

//������� �� ����� collect - �� ����� ���� �� �����
println '��� ������ BMW � ������� ��������� ����� 1600 ��3:'
def newLCarsList = carsList.findAll { it.vendor == 'BMW'}.collect { it.volume > 1600 ? it : null }

newLCarsList.eachWithIndex { it, i ->
    if (!it) newLCarsList.remove(i)
}

println newLCarsList