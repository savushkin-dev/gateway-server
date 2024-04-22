
import java
Person = java.type("com.example.SpringTestGraalVM.model.Person")

class ConnectorImpl:
    @staticmethod
    def testMethod_1(a,b) :
        return a + b

    @staticmethod
    def testMethod_2() :
        return "Python method_2 worked!"

    @staticmethod
    def testMethod_3(person: Person) :
        str = f"Имя: {person.getName()}; Возраст: {person.getAge()}"
        return str

    @staticmethod
    def testMethod_4(person: Person) :
        person.setAge(99999)
        return person