#!/bin/python3.8

import json
import urllib.request

body = urllib\
    .request\
    .urlopen("http://localhost:8080/api/v1/headhunter/demands?position=java&city=samara").read()
items = json.loads(body)["items"]

for item in items:
    print("id: " + str(item["id"]))
    print("position: " + item["position"])
    print("averageRubGrossSalary: " + str(item["averageRubGrossSalary"]))
    print("amount: " + str(item["amount"]))
    print("at_moment: " + item["atMoment"])
    print("city: " + item["city"])
    print("minYearExperience: " + str(item["minYearExperience"]))
    print("")
