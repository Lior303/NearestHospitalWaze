#!/usr/bin/env python
# -*- coding: utf-8 -*-
import WazeRouteCalculator
import logging

from flask import Flask
from flask import request
app = Flask(__name__)

list_of_hospital = ['בית חולים קפלן, ישראל',
                    'בית חולים סורוקה, ישראל',
                    'בית חולים אסותא אשדוד, ישראל',
                    'בית חולים הדסה, ישראל']

from_address = 'Ashdod, Israel'
to_address = None


def find_minimum_destination():
    minimum = 800
    index = 0
    coords = 0
    for i in range(len(list_of_hospital)):
        to_address = list_of_hospital[i]
        route = WazeRouteCalculator.WazeRouteCalculator(from_address, to_address)
        if minimum > route.calc_route_info()[0]:
            minimum = route.calc_route_info()[0]
            index = i
            coords = route.address_to_coords(list_of_hospital[i])
    return index, coords


@app.route('/', methods=['GET', 'POST'])
def user_function():
    if request.method == 'GET':
        hospital = find_minimum_destination()
        print("$$$$$$$$$$$$$$$$$$$$$")
        print(hospital[0])
        print(hospital[1])
        return "{}".format(hospital[1])
    if request.method == 'POST':
        """modify/update the information for <user_id>"""
        # you can use <user_id>, which is a str but could
        # changed to be int or whatever you want, along
        # with your lxml knowledge to make the required
        # changes
        from_address = request.form  # a multidict containing POST data
    else:
        # POST Error 405 Method Not Allowed
        return None


logger = logging.getLogger('WazeRouteCalculator.WazeRouteCalculator')
logger.setLevel(logging.DEBUG)
handler = logging.StreamHandler()
logger.addHandler(handler)


try:
    app.run(host='10.200.202.149')
except WazeRouteCalculator.WRCError as err:
    print(err)
