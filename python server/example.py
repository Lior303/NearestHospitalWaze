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

from_address = None
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
        lat = request.args.get('lat')
        lon = request.args.get('lon')
        from_address = "{}, {}".format(lat, lon)
        hospital = find_minimum_destination()
        return "{}".format(hospital[1])
    else:
        # POST Error 405 Method Not Allowed
        return None


logger = logging.getLogger('WazeRouteCalculator.WazeRouteCalculator')
logger.setLevel(logging.DEBUG)
handler = logging.StreamHandler()
logger.addHandler(handler)


try:
    app.run(host='10.200.201.111')
except WazeRouteCalculator.WRCError as err:
    print(err)
