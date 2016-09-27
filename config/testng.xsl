<!DOCTYPE suite SYSTEM "http://testng.org/testng-1.0.dtd" >
<suite name="TestAll" parallel="false">
    <parameter name="exampleDesc" value="TestNG Parameter Example"></parameter>
        <test name="firstTest">
            <classes>
                <class name="Main" />
            </classes>
        </test>
</suite>