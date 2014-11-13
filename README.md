httpclient
========================================================================================================================
This is yet another sample code for android http client to access any known REST API.

The IDE environment used was IntellJ IDEA 14.0. In this sample the target was to implement a module that can be used as
library for other projects. VCS used is Git and dependencies are fetch using maven project structure. Libraries used are
com.google.code.gson:gson:2.2.3
org.apache.httpcomponents.httpclient:4.0

Other libraries like android support and httpmime are included as exported libraries inside the product but not used.

A JUnit4 case is testing the client against a known REST API that will return the geodetic coordinates of any location.

http://maps.googleapis.com/maps/api/geocode/json?address=Tampere

If the location is found the result in json response will contain strings like: "status" : "OK"

The test case also simulates the authentication ( which is not needed for an open API like the example above).
Only needed in json header is the user email and password. Against the response should include the strings with "status"
"OK" so the test will not fail. Otherwise the test case should be changed.

========================================================================================================================
send your issues to run at remware.net



